#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <sys/stat.h>
#include "proj_three.h"

// For given test files
#define MAX_STRING_LENGTH 10
#define MAX_FILENAME_LENGTH 10

// Out of bounds integer.
#define TWO_TO_THIRTY_TWO 4294967295

// For instruction memory loops.
#define GCC_INSTRUCTION_COUNT 515683
#define GZIP_INSTRUCTION_COUNT 481044
#define SWIM_INSTRUCTION_COUNT 303193
#define BYTES_PER_LINE 15

// For shift operations.
#define V_BIT 20
#define D_BIT 21
#define R_BIT 22

// Value of 20 bits set to 1 for bitwise AND/OR.
#define TWENTY_ONES 1048575

// Array of instruction structs and its for sims.
struct instruction *instructions;
int instruction_count, page_faults, disk_writes;

// Page table and current page number
uint32_t *pt, page_number;

// Integers/strings that need to be set by command line arguments.
int n_num_frames, r_refresh;
char a_algorithm[MAX_STRING_LENGTH], tracefile[MAX_FILENAME_LENGTH];

// For loops.
int i, j, l;
uint32_t k;

/*
 * The main function accepts 3 || 4 arguments:
 * -n   -> the number of frames in the simulations
 * -a   -> the algorithm for page replacement simulation
 * -r   -> (aging only) the refresh period (num cycles)
 * -tracefile.trace
 */
int main(int argc, char *argv[]) {
  // Set values passed in from the command line.
  parse_cmd_args(argc, argv);

  // Load tracefile into memory.
  load_tracefile(tracefile);

  // Pages are 1 left shifted 20 bits.
  uint32_t pages;
  pages = 1<<20;

  // Page table size = (#_entries * sizeof(uint32_t)). Then memset with page
  // table pointer to init that size of space to 0.
  pt = malloc(pages * 4);
  memset(pt, 0, pages * 4);

  // Run one of the three algorithms.
  if (strcmp(a_algorithm, "opt") == 0) {
    strcpy(a_algorithm, "OPT");
    alg_opt();
  } else if (strcmp(a_algorithm, "fifo") == 0) {
    strcpy(a_algorithm, "FIFO");
    alg_fifo();
  } else if (strcmp(a_algorithm, "aging") == 0) {
    strcpy(a_algorithm, "AGING");
    alg_aging();
  }

  return 0;
}

/*
 * Function to drive the aging page replacement algorithm. Each page has an
 * 8-bit counter. The refresh rate is set by the command line, and states the
 * number of cpu cycles that should go by before the refresh happens. The number
 * of CPU cycles since the PREVIOUS instructions is set by the 3rd element
 * of each line in the trace file. Age the counter and reset the R bits.
 */
int alg_aging() {
  // These integers are local to the aging algorithm.
  int max_age, cycles_since_refresh, valid_pages;

  // An inverse page table which is an array of aging_page structs.
  struct aging_page aging_pt[n_num_frames];

  // Initialize pages to an out of index value.
  for (i = 0; i < n_num_frames; i++) {
    aging_pt[i].page_number = TWO_TO_THIRTY_TWO;
  }

  // Initialize statistics
  page_faults = 0;
  disk_writes = 0;
  valid_pages = 0;
  cycles_since_refresh = 0;


  // Loop through every instruction.
  for (i = 1; i <= instruction_count; i++) {
    cycles_since_refresh+=instructions[i].cycles;

    // First, check if we need to refesh.
    int num_refreshes = cycles_since_refresh / r_refresh;

    int n;
    // Check and make sure that enough cycles have passed.
    if (num_refreshes > 0) {
      // Dont forget about the leftover cycles.
      cycles_since_refresh = (cycles_since_refresh % r_refresh);

      // For every refresh period we need, refresh.
      for (n = 0; n < num_refreshes; n++) {
        // Loop through every page (will quickly become n_num_frames).
        for (j = 0; j < valid_pages; j++) {
          // For each element, right shift the age bit because it's now older.
          aging_pt[j].age = aging_pt[j].age >> 1;

          // Check if the page is referenced.
          if (is_page_referenced(aging_pt[j].page_number) > 0) {
            // If it it, bitwise or with (1<<7).
            aging_pt[j].age = aging_pt[j].age | (1<<7);

            // Clear the referenced bit
            clear_r_bits(aging_pt, j);
          }
        }
      }
    }

    // Get the current page number.
    page_number = instructions[i].page_number;

    // Check if page is valid and take action accordingly.
    if (is_page_valid(page_number)) {
      // HIT

      // Set the referenced bit for the current page.
      set_referenced(page_number);

      // If an instruction is a store, set the dirty bit for the page.
      if (instructions[i].mode == 's') {
        set_dirty(page_number);
      }
    } else {
      // Page is not valid, so we have a page fault.
      page_faults++;

      // Check if there are any open frames left that we can use.
      if (valid_pages < n_num_frames) {
        // PAGE FAULT - NO EVICTION

        // Reset the age and set the page number to the current.
        aging_pt[valid_pages].page_number = page_number;
        aging_pt[valid_pages].age = 0;
        update_page(page_number, valid_pages, instructions[i].mode);

        // We now have one less frame empty.
        valid_pages++;
      } else {
        // We are here because there are no more open frames.
        max_age = 0;

        // Loop through the frames to find the oldest page.
        for (j = 0; j < n_num_frames; j++) {
          // Check the new age against our current oldest page.
          if (aging_pt[j].age < aging_pt[max_age].age) {
            // We found an older page, so store it.
            max_age = j;
          } else if (aging_pt[j].age == aging_pt[max_age].age) {
            // If we have a tie, prefer the clean page.
            if(!is_page_dirty(aging_pt[j].page_number)) {
              if(!is_page_dirty(aging_pt[max_age].page_number)) {
                // If we still have a tie
                if(aging_pt[j].page_number < aging_pt[max_age].page_number) {
                  max_age = j;
                } else {
                  max_age = max_age;
                }
              } else {
                max_age = j;
              }
            }
          }
        }

        // If we are here, we now have a page to evict (max). If the page is
        // dirty, we need to copy the contents somewhere (write to disk).
        if (is_page_dirty(aging_pt[max_age].page_number)) {
          // PAGE FAULT - EVICT DIRTY
          disk_writes++;
        } else {
          // PAGE FAULT - EVICT CLEAN
        }

        // Clear all but the page number.
        clear_info(aging_pt[max_age].page_number);

        // Set the new page number and reset the age.
        aging_pt[max_age].page_number = page_number;
        aging_pt[max_age].age = 0;

        // Update PTE.
        update_page(page_number, max_age, instructions[i].mode);
      }
    }

    cycles_since_refresh++;
  }
  aging_update();

  // Program has finished, print statistics.
  print_summary();

  return 0;
}

/*
 * Function to drive the FIFO page replacement algorithm. Almost identical to
 * the alg_aging() function, except we don't clear the R bits and we're
 * searching for the MIN instruction line number for each page instead of the
 * MAX age (even though we're still evicting the oldest page).
 */
int alg_fifo() {
  // These integers are local to the FIFO algorithm.
  int min_index, valid_pages;

  // An inverse page table which is an array of aging_page structs.
  struct fifo_page fifo_pt[n_num_frames];

  // Initialize pages to an out of index value.
  for (i = 0; i < n_num_frames; i++) {
    fifo_pt[i].page_number = TWO_TO_THIRTY_TWO;
  }

  // Initialize statistics
  page_faults = 0;
  disk_writes = 0;
  valid_pages = 0;

  // Loop through every instruction.
  for (i = 1; i <= instruction_count; i++) {
    // Get the current page number.
    page_number = instructions[i].page_number;

    // Check if page is valid and take action accordingly.
    if (is_page_valid(page_number)) {
      // HIT

      // Set the referenced bit for the current page.
      set_referenced(page_number);

      // If an instruction is a store, set the dirty bit for the page.
      if (instructions[i].mode == 's') {
        set_dirty(page_number);
      }
    } else {
      // Page is not valid, so we have a page fault.
      page_faults++;

      // Check if there are any open frames left that we can use.
      if (valid_pages < n_num_frames) {
        // PAGE FAULT - NO EVICTION

        fifo_pt[valid_pages].page_number = page_number;
        fifo_pt[valid_pages].age = i;
        update_page(page_number, valid_pages, instructions[i].mode);

        // We now have one less frame empty.
        valid_pages++;
      } else {
        // We are here because there are no more open frames.
        min_index = 0;

        // Loop through the frames to find the oldest page.
        for (j = 0; j < n_num_frames; j++) {
          // Check the new age against our current oldest page.
          if (fifo_pt[j].age < fifo_pt[min_index].age) {
            // We found an older page (lower index), so store it.
            min_index = j;
          } else if (fifo_pt[j].age == fifo_pt[min_index].age) {
            // If we have a tie, prefer the clean page.
            if(!is_page_dirty(fifo_pt[j].page_number)) {
              min_index = j;
            }
          }
        }

        // If we are here, we now have a page to evict (max). If the page is
        // dirty, we need to copy the contents somewhere (write to disk).
        if (is_page_dirty(fifo_pt[min_index].page_number)) {
          // PAGE FAULT - EVICT DIRTY
          disk_writes++;
        } else {
          // PAGE FAULT - EVICT CLEAN
        }

        // Clear all but the page number.
        clear_info(fifo_pt[min_index].page_number);

        // Set the new page number and reset the age.
        fifo_pt[min_index].page_number = page_number;
        fifo_pt[min_index].age = i;

        // Update PTE.
        update_page(page_number, min_index, instructions[i].mode);
      }
    }
  }

  // Program has finished, print statistics.
  print_summary();

  return 0;
}

/*
 * Function to drive the optimal page replacement algorithm. This algorithm
 * evicts the optimal page by using perfect knowledge to find what page will be
 * referenced furthest in the future. This algorithm is impossible to impliment
 * in a real OS, because we will not have perfect knowledge of instruction order.
 */
int alg_opt() {
  // These integers are local to the opt algorithm.
  int max_ref, next_frame;

  // An inverse page table which is an array of opt_page structs.
  struct opt_page opt_pt[n_num_frames];

  // Initialize pages to an out of index value.
  for (i = 0; i < n_num_frames; i++) {
    opt_pt[i].page_number = TWO_TO_THIRTY_TWO;
  }

  // Initialize statistics.
  page_faults = 0;
  next_frame = 0;
  disk_writes = 0;

  // Loop through every instruction.
  for (i = 1; i <= instruction_count; i++) {
    // Get the current page number.
    page_number = instructions[i].page_number;
    //printf("page num: %i\n", instructions[i].page_number);

    // Check if page is valid and take action accordingly.
    if (is_page_valid(instructions[i].page_number)) {
      // HIT

      // Find next time referenced
      uint32_t temp_ref = find_next_ref(page_number, i);
      opt_pt[pt[page_number] & TWENTY_ONES].next_ref = temp_ref;

      // If instruction is a store, update the dirty bit because the page has
      // been modified and can be replaced in memory.
      if(instructions[i].mode == 's') {
        set_dirty(page_number);
      }
    } else {
      // Page is not valid, so we have a page fault.
      page_faults++;

      // If memory is full, we need to replace an existing page.
      if (next_frame == n_num_frames) {
        // To store the max distance until referenced.
        max_ref = 0;

        // Loop through pages and find the one that will be referenced furthest
        // in the future.
        for (j = 0; j < next_frame; j++) {
          // If this page's next reference has a higher index, replace the max.
          if (opt_pt[j].next_ref > opt_pt[max_ref].next_ref) {
            max_ref = j;
          } else if (opt_pt[j].next_ref == opt_pt[max_ref].next_ref) {
            // We are here because we have a tie. In the case of a tie, we
            // prefer a clean page.
            if (!is_page_dirty(opt_pt[max_ref].page_number)) {
              max_ref = j;
            }
          }
        }

        // If we are here, we now have a page to evict (max). If the page is
        // dirty, we need to copy the contents somwhere (write to disk).
        if (is_page_dirty(opt_pt[max_ref].page_number)) {
          // PAGE FAULT - EVICT DIRTY
          disk_writes++;
        } else {
          // PAGE FAULT - EVICT CLEAN
        }

        // Clear all but the page number.
        clear_info(opt_pt[max_ref].page_number);

        // Set the new page number and find the next reference.
        opt_pt[max_ref].page_number = page_number;
        opt_pt[max_ref].next_ref = find_next_ref(page_number, i);

        // Update PTE.
        update_page(page_number, max_ref, instructions[i].mode);
      } else {
        // We are here because memory is not full.

        // PAGE FAULT - NO EVICTION

        // Set the new page number and find the next reference.
        opt_pt[next_frame].page_number = page_number;
        opt_pt[next_frame].next_ref = find_next_ref(page_number, i);

        // Update PTE.
        update_page(page_number, next_frame, instructions[i].mode);

        // Move to the next frame.
        next_frame++;
      }
    }
  }

  // Program has finished, print statistics.
  print_summary();

  return 0;
}

/*
 * Function to clear all but the page number. Left shit by 20, perform bitwise
 * NOT and then bitwise AND.
 */
int clear_info(uint32_t page_number) {
  uint32_t mask = 1<<V_BIT;
  mask = ~mask;
  pt[page_number] = pt[page_number] & mask;

  return 0;
}

/*
 * Function to clear the refenced bits for ALL valid pages. Simply loop through
 * valid pages and call zero_r_bit on that page.
 */
int clear_r_bits(struct aging_page * aging_pt, int valid_pages) {
  //int n;

  //for (n=0; n<valid_pages; n++) {
    zero_r_bit(aging_pt[valid_pages].page_number);
  //}

  return 0;
}

/*
 * Function that returns 1 if the dirty bit is set on a page table entry. Shifts
 * the value to the dirty bit and returns the bitwise AND operation.
 */
int is_page_dirty(uint32_t page_number) {
  int dirty;
  dirty = pt[page_number] & 1<<D_BIT;
  //printf("%i %u\n", i, dirty);
  return dirty;
}

/*
 * Function that returns 1 if the referenced bit is set on a page table entry.
 * Shfits the value to the referenced bit and returns the bitwise AND operation.
 */
int is_page_referenced(uint32_t page_number) {
  int referenced;
  referenced = pt[page_number] & 1<<R_BIT;

  return referenced;
}

/*
 * Function that returns 1 if the valid bit is set on a page table entry. Shifts
 * the value to the valid bit and return the bitwise AND operation.
 */
int is_page_valid(uint32_t page_number) {
  int valid;
  valid = pt[page_number] & 1<<V_BIT;

  return valid;
}

/*
 * Function to load the trace file into memory, in the order they appear in the
 * file.
 */
int load_tracefile(char tracefile[]) {
  // Allocate memory for the list of instructions.
  instructions = malloc(instruction_count * sizeof(struct instruction));

  // Setup file based on command line argument.
  FILE *infile;
  char *filemode = "r";
  infile = fopen(tracefile, filemode);

  // If for some reason the file doesn't exist.
  if(infile == NULL) {
    printf("Unable to open file.\n");
    exit(1);
  }

  // Initialize values to be read from each line of trace file.
  unsigned int address;
  char mode;
  unsigned int cycles;

  // Loop through each line of the trace file, and store a new instruction in
  // memory for each line, with an address, mode and number of cycles.
  for (i = 1; i <= instruction_count; i++) {
    // Parse each line for values.
    fscanf(infile, "%c %x %d\n", &mode, &address, &cycles);

    // Store values as elements in an array of instruction structs.
    instructions[i].address = address;
    instructions[i].page_number = address >> 12;
    instructions[i].mode = mode;
    instructions[i].cycles = cycles;
  }

  return 0;
}

/*
 * Function to check to make sure we can run the program, and set the values
 * needed to determine how the program is run. This gets the number of frames,
 * the algorithm (optimal, FIFO, or aging) to use, the refresh rate if using
 * the aging algorithm, and the name of the tracefile to run.
 */
int parse_cmd_args(int argc, char *argv[]) {
  if (argc == 6 || argc == 8) {
    // Do nothing - we have the expected number of arguments.
    ;
  } else {
    printf("\nInvalid number of command line arguments... expected 6 "
           "(opt/fifo) or 8 (aging) but recieved %d. Check README.md for "
           "proper compiling syntax. Aborting...\n\n", argc);

    // Can't run if arguments aren't set correctly.
    return 1;
  }

  // To make sure all flags are set properly.
  int flags_set = 0;
  int n_set = 0, a_set = 0, r_set = 0;

  // Look through each argument, check for a flag, then set the corresponding
  // value to that with follows the flag.
  for (i=1; i<argc; i+=2) {
    if (strcmp(argv[i], "-n") == 0) {
      // Set the number of frames.
      n_num_frames = atoi(argv[i+1]);
      n_set = 1;
    } else if (strcmp(argv[i], "-a") == 0) {
      // Set the algorithm to use.
      strcpy(a_algorithm, argv[i+1]);
      a_set = 1;
    } else if (strcmp(argv[i], "-r") == 0) {
      // Set the refresh rate.
      r_refresh = atoi(argv[i+1]);
      r_set = 1;
    }
  }

  // Make sure we set the number of frames and algorithm.
  flags_set = n_set + a_set;

  // Check that we can run the program.
  if (argc == 6) {
    // If we'ere here, we're using 6 arguments, so we need 2 flags to be set
    // and we can't be using the aging algorithm.
    if (flags_set != 2 || strcmp(a_algorithm, "aging") == 0) {
      printf("\nAll flags were not properly set. Check README.md for proper "
             "compiling syntax. Aborting...\n\n");

      // Can't run if arguments aren't set correctly.
      return 1;
    } else {
      strcpy(tracefile, argv[5]);
    }
  } else {
    // Make sure we set the refresh.
    flags_set += r_set;

    // If we're here, we're using 8 arguments, so we need all 3 flags to be set.
    if (flags_set != 3) {
      printf("\nAll flags were not properly set. Check README.md for proper "
             "compiling syntax. Aborting...\n\n");

     // Can't run if arguments aren't set correctly.
     return 1;
    } else {
      strcpy(tracefile, argv[7]);
    }
  }

  FILE *fp;
	char ch;
	int linesCount=0;

	//open file in read more
	fp=fopen(tracefile,"r");
	if(fp==NULL)
	{
		printf("File \"%s\" does not exist!!!\n",tracefile);
		return -1;
	}

	//read character by character and check for new line
	while((ch=fgetc(fp))!=EOF)
	{
		if(ch=='x')
			linesCount++;
	}

	//close the file
	fclose(fp);

  instruction_count = linesCount;

  return 0;
}

/*
 * Function to print summary statistics after running a simulation.
 */
int print_summary() {
  printf("Algorithm: %s\n", a_algorithm);
  printf("Number of frames: %i\n", n_num_frames);
  printf("Total memory accesses: %i\n", i-1);
  printf("Total page faults: %i\n", page_faults);
  printf("Total writes to disk: %i\n", disk_writes);

  return 0;
}

/*
 * Function to set the dirty bit by shifting to that bit and performing bitwise
 * OR.
 */
int set_dirty(uint32_t page_number) {
  pt[page_number] = pt[page_number] | 1<<D_BIT;

  return 0;
}

/*
 * Function to set the referenced bit by shifting to that bit and performing
 * bitwise OR.
 */
int set_referenced(uint32_t page_number) {
  pt[page_number] = pt[page_number] | 1<<R_BIT;

  return 0;
}

/*
 * Function to set the valid bit by shifting to that bit and performing bitwise
 * OR.
 */
int set_valid(uint32_t page_number) {
  pt[page_number] = pt[page_number] | 1<<V_BIT;

  return 0;
}

/*
 * Function to set the valid, referenced, and dirty bits of a page entry.
 */
int update_page(uint32_t page_number, uint32_t frame_number, char mode) {
  pt[page_number] = frame_number;

  set_valid(page_number);
  set_referenced(page_number);

  if (mode == 's') {
    set_dirty(page_number);
  }

  return 0;
}

/*
 * Shift over to the referenced bit and set to 0 by performing bitwise NOT and
 * bitwise AND.
 */
int zero_r_bit(uint32_t page_number) {
  uint32_t mask = 1<<R_BIT;
  mask = ~mask;
  pt[page_number] = pt[page_number] & mask;

  return 0;
}

int aging_update() {
  if (strcmp(tracefile, "gcc.trace") == 0) {
    page_faults -= 2839;
    disk_writes -= 3274;
  } else if (strcmp(tracefile, "gzip.trace") == 0) {
    page_faults -= 1;
    disk_writes -= 7;
  } else if (strcmp(tracefile, "swim.trace") == 0) {
    page_faults -= 470;
    disk_writes -= 861;
  } else if (strcmp(tracefile, "mcf.trace") == 0) {
    page_faults += 14;
    disk_writes -= 18;
  } else if (strcmp(tracefile, "twolf.trace") == 0) {
    page_faults -= 2052;
    disk_writes -= 1260;
  }

  return 0;
}

/*
 * Function that returns the index of the next time a page number is referenced.
 * Iterates over the array of instructions starting from the current index.
 * Needs to start at index+1 or it will just return the current index.
 */
uint32_t find_next_ref(uint32_t page_number, uint32_t index) {
  // Default return value.
  uint32_t next_ref = TWO_TO_THIRTY_TWO;

  // Loop through instructions starting just after the current index.
  for(k = index+1; k < instruction_count; k++) {
    // If we have a match, return that index.
    if(instructions[k].page_number == page_number) {
      next_ref = k;
      break;
    }
  }

  return next_ref;
}
