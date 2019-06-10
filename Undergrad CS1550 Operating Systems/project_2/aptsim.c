#include <sys/mman.h>
#include <linux/unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

// Integers set by the command line arguments
int k_num_agents = 0, pa_percent_agents = 0, da_delay_agents = 0;
int m_num_tenants = 0, pt_percent_tenants = 0, dt_delay_tenants = 0;
int sa_seed = 0, st_seed = 0;

// For loops
int i;

// For timing processes
time_t seconds;
int start_time, secs;

// Struct for semaphore
struct cs1550_sem {
	int value;

	struct Node *head;
	struct Node *tail;
};

// Wrapper function for wait
void down(struct cs1550_sem *sem) {
	syscall(__NR_cs1550_down, sem);
}
// Wrapper function for signal
void up(struct cs1550_sem *sem) {
	syscall(__NR_cs1550_up, sem);
}

// Shared memory region and semaphores that reside within that region
struct cs1550_sem* sem_region;
struct cs1550_sem *slots, *agent_can_leave, *mutex, *num_tenants_in_apt;
struct cs1550_sem *apartment, *mutex2;

// Shared memory region for ins so we don't acces sem values directly
int* int_region;
int *total_num_tenants, *int_tenants_in_apt;

/*
 * The main function accepts multiple flags:
 *  -k    -> the number of agent (producer) processes
 *  -pa   -> the percent chance of an agent arriving directly after another
 *  -da   -> the delay (in seconds) before any new agent arrives
 *  -m    -> the number of tenant (consumer) processes
 *  -pt   -> the percent chance of a tenant arriving directly after another
 *  -dt   -> the delay (in seconds) before any new tenant arrives
 *  -sa   -> the seed for srand() for agents
 *	-st   -> the seed for srand() for tennants
 */
int main(int argc, char *argv[]) {
	// Make sure we have 12 arguments (6 flags + 6 values)
  if (argc != 17) {
    printf("16 additional command line arguments are required. "
           "Check README.md for proper compiling syntax.\n\n");

    return 1;
  } else {
		// To make sure all flags are set properly
    int flags_set = 0;
		int k_set = 0, pa_set = 0, da_set = 0, m_set = 0;
		int pt_set = 0, dt_set = 0, sa_set = 0, st_set = 0;

		// Loop through each argument, check for a flag, and then set that flag to
		// its corresponding global integer.
    for (i = 1; i<17; i+=2) {
      if (strcmp(argv[i], "-k") == 0) {
        k_num_agents = atoi(argv[i+1]);
        k_set = 1;
      } else if (strcmp(argv[i], "-pa") == 0) {
        pa_percent_agents = atoi(argv[i+1]);
        pa_set = 1;
      } else if (strcmp(argv[i], "-da") == 0) {
        da_delay_agents = atoi(argv[i+1]);
        da_set = 1;
      } else if (strcmp(argv[i], "-m") == 0) {
        m_num_tenants = atoi(argv[i+1]);
        m_set = 1;
      } else if (strcmp(argv[i], "-pt") == 0) {
        pt_percent_tenants = atoi(argv[i+1]);
        pt_set = 1;
      } else if (strcmp(argv[i], "-dt") == 0) {
        dt_delay_tenants = atoi(argv[i+1]);
        dt_set = 1;
      } else if (strcmp(argv[i], "-sa") == 0) {
				sa_seed = atoi(argv[i+1]);
				sa_set = 1;
			} else if (strcmp(argv[i], "-st") == 0) {
				st_seed = atoi(argv[i+1]);
				st_set = 1;
			}
    }

    // Check to make sure all flags were set properly. Accomplished with a naive
    // approach, maybe there is a better way but it works. Setting flags as
		// characters instead of integers will still cause issues.
		flags_set = k_set+pa_set+da_set+m_set+pt_set+dt_set+sa_set+st_set;

    if (flags_set < 8) {
      printf("All 8 flags were not properly set. "
             "Check README.md for proper compiling syntax.\n\n");

			return 1;
    }
  }

  // Set up the shared memory region which will contain all of the semaphores
  // shared between the tenant and agent processes.
  sem_region = (struct cs1550_sem*) mmap(NULL, sizeof(struct cs1550_sem)*7,
    PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, 0, 0);

	// Region for ints, used for accessing counting sem values.
	int_region = (int*) mmap(NULL, sizeof(int)*2, PROT_READ | PROT_WRITE,
		MAP_SHARED | MAP_ANONYMOUS, 0, 0);

	// Assign each semaphore to its own space within the shared memory region.
	slots = (struct cs1550_sem*)sem_region;
  mutex = (struct cs1550_sem*)sem_region+1;
	mutex2 = (struct cs1550_sem*)sem_region+2;
	apartment = (struct cs1550_sem*)sem_region+3;
	agent_can_leave = (struct cs1550_sem*)sem_region+4;
  num_tenants_in_apt = (struct cs1550_sem*)sem_region+5;

	// Shared ints so we don't access sem values from user program.
	total_num_tenants = (int*)int_region;
	int_tenants_in_apt = (int*)int_region+1;

	// Initialize each semaphore to the corresponding integer value.
	//   -> MUTEXs:
	//      - mutex
	//      - mutex2
	//      - apartment
	//      - agent_can_leave
	//   -> COUNTING SEMAPHOREs:
	//      - num_tenants_in_apt
	initSem(slots, 0);
  initSem(mutex, 1);
  initSem(mutex2, 1);
  initSem(apartment, 0);
  initSem(agent_can_leave, 0);
	initSem(num_tenants_in_apt, 0);

	// Initialize integers.
	*total_num_tenants = m_num_tenants;
	*int_tenants_in_apt = 0;

	// Begin timing operations.
  start_time = time(&seconds);
  printf("The apartment is now empty.\n");

	// Generate random int stream based on agent seed
  srand(sa_seed);
  int agent_chance;

  // Create agent processes.
  for (i = 0; i < k_num_agents; i++) {
		agent_chance = (rand() % 100);

		// Make sure we only delay after the first agent

		// Fork and agent
    if (fork() == 0) {
			if (i != 0) {
				if (agent_chance > pa_percent_agents) {
					sleep(da_delay_agents*i);
				}
			}

      agentArrives(i);
      exit(0);
    }
  }

	// Generate random int stream based on tenant seed
	srand(st_seed);
	int tenant_chance;

  // Create tenant processes.
  for (i = 0; i < m_num_tenants; i++) {
		tenant_chance = (rand() % 100);

		// Make sure we only delay after the first tenant
		if (i != 0) {
			if (tenant_chance > pt_percent_tenants) {
				sleep(dt_delay_tenants);
			}
		}

		// Fork a tenant
    if (fork() == 0) {
      tenantArrives(i);

			// Protect the number of tenants in a mutex.
      down(mutex2);
			*total_num_tenants = (*total_num_tenants) - 1;
      up(mutex2);

      exit(0);
    }
  }

  // Wait for all children to finish. Fixed output printing after the program
  // exits for k > 1 agents with `+ (k_num_agents - 1)`.
  for (i = 0; i < (m_num_tenants + (k_num_agents - 1)); i++) {
    wait(NULL);
  }

  // End the program successfully.
  usleep(100); // Just in case
  return 0;
}

/*
 * This function is called when an agent is born. The agent prints the time
 * it arrives regardless of whether or not it can enter the apartment. The
 * agent then waits for the lock on the apartment, makes sure all tenants have
 * not viewed, creates slots for the tenants to consume and waits for a tenant
 * to open the apartment.
 */
int agentArrives(int agent_ID) {
	secs = (time(&seconds)) - start_time;
	printf("Agent %d arrives at time %d.\n", agent_ID, secs);

  // Lock the region so only 1 agent can open arrive at a time
  down(mutex);

	// We need this lock to prevent a race condition for the total number of
	// tenants.
  down(mutex2);

	// If all the tenants have seen the apartment, then there is no need to enter
	// so we simply release the locks and kill the child agent process.
	if (*total_num_tenants == 0) {
    up(mutex);
    up(mutex2);
    exit(0);
  }

	// We are here because there are tenants who have not yet viewed
  up(mutex2);

  // There are now 10 new slots for tenants to use
  for (i = 0; i < 10; i++) {
    up(slots);
  }

  // Wait for a tenant before opening the apt, then revert the value because
  // the agent does not affect the number of tenants in the apartment.
  down(num_tenants_in_apt);
  up(num_tenants_in_apt);

	// Open the apartment
  openApt(agent_ID);
}

/*
 * This function is called when a tenant is born. The tenant prints the time it
 * arrives regardless of whether or not it can enter the apartment. The tenant
 * then waits for a slot, and for the apartment to be open and calls viewApt().
 */
int tenantArrives(int tenant_ID, int chance) {
	secs = (time(&seconds)) - start_time;
  printf("Tenant %d arrives at time %d.\n", tenant_ID, secs);

  // Take an open slot in the apartment
  down(slots);

  up(num_tenants_in_apt);
	*int_tenants_in_apt = (*int_tenants_in_apt) + 1;

  // Wait for the apartment to be open, then revert the value because the
  // apartment is still open.
  down(apartment);
  up(apartment);

	// View the apartment
  viewApt(tenant_ID);
}

/*
 * This function is called by an agent process. The tenant process. The agent
 * opens the apartment and then waits for all tenants to leave before leaving.
 * The agent then closes the apartment and releases the mutex lock allowing
 * another agent to enter.
 */
int openApt(int agent_ID) {
  secs = (time(&seconds)) - start_time;
  printf("Agent %d opens at time %d.\n", agent_ID, secs);

  // Open the apartment
  up(apartment);

	// Wait for all tenants to leave
  down(agent_can_leave);

  secs = (time(&seconds)) - start_time;
  printf("Agent %d leaves the apartment at time %d.\n", agent_ID, secs);
	printf("The apartment is now empty.\n");

	// Close the apartment and prevent tenants from viewing until another agent
	// opens the apartment.
  down(apartment);

	// Release the mutex lock, allowing another agent to enter the apartment.
  up(mutex);
}

/*
 * This function is called by a tenant process. The tenant views the apartment
 * for 2 seconds, leaves, then checks to see if it is the last tenant in the
 * apartment. If it is, it releases the lock preventing the agent from leaving.
 */
int viewApt(int tenant_ID) {
  secs = (time(&seconds)) - start_time;
  printf("Tenant %d inspects the apartment at time %d.\n", tenant_ID, secs);

	// Tenant is now inspecting the apartment.
  sleep(2);

  secs = (time(&seconds)) - start_time;
  printf("Tenant %d leaves at time %d.\n", tenant_ID, secs);

	// Decrement values tracking tenants in the apartment.
  down(num_tenants_in_apt);
	*int_tenants_in_apt = (*int_tenants_in_apt) - 1;

	// If this tenant was the last in the apartment and has now left, we can
	// signal the agent to leave the apartment.
	if (*int_tenants_in_apt == 0) {
    up(agent_can_leave);
  }
}

/*
 * Helper function to initialize semaphores to the provided value.
 */
int initSem(struct cs1550_sem *sem, int val) {
	sem->value = val;
	sem->head = NULL;
	sem->tail = NULL;
}
