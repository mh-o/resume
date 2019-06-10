# Project 2
### Modified Files:
   - Total of **4** files:
      - arch/i386/kernel/syscall_table.S
      - include/asm/unistd.h
      - kernel/sys.c
      - aptsim.c (only NEW file for this project)

### Steps:
1. Figure out how to work with shared memory for semaphores

2. Trace through basic apartment simulation using semaphores

3. Add timing/error checking functionality

4. Fix autograder issues

### Building Changes/Execution:
1. SCP 'aptsim.c' to thoth.cs.pitt.edu:/u/mho8/OSLab/ (or use remote sync)
2. Compile on THOTH:
   - `gcc -m32 -o aptsim -I /u/OSLab/mho8/linux-2.6.23.1/include/ aptsim.c`
   - again, just run the .sh
3. SCP and run on QEMU
   - From /root:
      - `scp mho8@thoth.cs.pitt.edu:/u/OSLab/mho8/aptsim .`
      - `./aptsim -k 4 -pa 30 -da 1 -m 7 -pt 40 -dt 1 -sa 1`


### Explanation:
At first, I believed this project was a variation of the Reader-Writer problem.

Then, I was sure this project was a variation of the Bounded-Buffer problem.

Several long nights and countless versions later, I realized this is in fact a
variation of the Sleeping Barber problem.

The solution to the basic Sleeping Barber problem is to use semaphores. A
**binary semaphore** *MUTEX*, a **counting semaphore** *BARBER*
(initialized to 0), and another **counting semaphore** *CUSTOMERS*
(also initialized to 0).

The pseudocode of the standard Sleeping Barber algorithm (referenced from
https://www.geeksforgeeks.org/operating-system-sleeping-barber-problem/) can be
seen in the code below:

```c
  Semaphore Customers = 0;
  Semaphore Barber = 0;
  Mutex Seats = 1;
  int FreeSeats = N;

  Barber {
    while(true) {
      // waits for a customer (sleeps)
      down(Customers)

      // mutex to protect the number of available seats
      down(Seats);

      // a chair gets free
      FreeSeats++;

      // bring customer for haircut
      up(Barber);

      // release the mutex on the chair
      up(Seats);

      // barber is cutting hair
    }
  }

  Customer {
    while(true) {
      // protects seats so only 1 customer tries to sit in a chair
      down(Seats);

      if(FreeSeats > 0) {
        // sitting down
        FreeSeats--;

        // notify the barber
        up(Customers);

        // release the lock
        up(Seats);

        // wait in the waiting room if barber is busy
        down(Barber);

        // customer is having hair cut
      } else {
        // release the lock
        up(Seats);

        // customer leaves
      }
    }
  }
```
## THE ORIGINAL IMPLEMENTATION
Above, we may notice some parallels between this classic problem and the problem
we face for this project. The barber, is the agent. The tenants, are the
customers. The haircut, is the showing of the apartment.

However, we have several issues we must tackle to adapt this problem to our
project.

Namely:
   - We need multiple barbers (agents)
   - We need only **one** barber (agent) to be in the barbershop (apartment) at
     a time
   - Each barber needs to be able to give **ten** haircuts (show the apartment
     to a tenant) at once

## MY IMPLEMENTATION
For my solution, it was actually easier just to write pseudocode about what
needed to happen in order for the program to work correctly. My solution,
simplified, uses the following semaphores:

   - **mutex** to lock the agents so that only one may open the apt at a time
   - **mutex2** to prevent a race condition on **num_tenants**
   - **num_tenants** to store the number of tenants remaining
   - **tenants_in_apt** to make sure an agent doesn't show to >10 tenants
   - **apartment** as a mutex to open/close the apartment
   - **agent_can_leave** to prevent agents from leaving while tenants remain
   - **slots** so a tenant waits for an agent to open and create 10 slots

This solution is *fair* because tenants consume slots on a FCFS (with some
variation due to the nature of children in c) basis. A summary of the solution:

## SOLUTION

Agents and tenants arrive according to the seed and delay set in the command
line. When an agent arrives, it locks a mutex protecting another agent from
opening the apartment. It then locks a second mutex protecting the number of
tenants, a shared integer that tracks the total number of tenants. If it is 0,
the agent leaves and releases each mutex. The agent then calls up 10 times to
produce 10 slots for tenants to consume, or view the apartment. The agent then
waits on a semaphore for the number of tenants in the apartment, and when a
tenant shows up the apartment is open. The calls up on the apartment and waits
for all tenants to leave, then reverts the locks it set.

A tenant process arrives and waits for a slot in the apartment to be produced
by an agent (1 of 10 available). If that check passes, it waits for the
apartment to be opened. The agent then views the apartment by sleeping for 2
seconds. Before exiting, the tenant process checks to see if it is the last
tenant in the apartment. If it is, the tenant signals the agent that it is OK
to leave.
