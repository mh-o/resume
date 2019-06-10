#ifndef PROJ_THREE
#define PROJ_THREE

// Struct for a page for the aging algorithm. Each page number also stores its
// age as the number of cpu cylces since it was last referenced.
struct aging_page {
  uint32_t page_number;
  uint8_t age;
};

// Struct for a page for using the FIFO algorithm. Eeach page also stores its
// age as the current instruction index.
struct fifo_page {
  uint32_t page_number;
  int age;
};

// Struct for an instruction. Stores the address, page number, mode, and number
// of CPU cycles for each instruction.
struct instruction {
  uint32_t address;
  uint32_t page_number;
  char mode;
  uint32_t cycles;
};

// Struct for a page for the optimal algorithm. Each page number also stores
// the next time it is referenced so we can properly simulate the optimal
// page replacement algorithm.
struct opt_page {
  uint32_t page_number;
  uint32_t next_ref;
};

// Function declarations.
int alg_aging();
int alg_fifo();
int alg_opt();
int clear_info(uint32_t);
int clear_r_bits(struct aging_page *, int);
int is_page_dirty(uint32_t);
int is_page_referenced(uint32_t);
int is_page_valid(uint32_t);
int load_tracefile(char[]);
int parse_cmd_args(int, char **);
int print_summary();
int set_dirty(uint32_t);
int set_referenced(uint32_t);
int set_valid(uint32_t);
int update_page(uint32_t, uint32_t, char);
int aging_update();
int zero_r_bit(uint32_t);

uint32_t find_next_ref(uint32_t, uint32_t);

#endif
