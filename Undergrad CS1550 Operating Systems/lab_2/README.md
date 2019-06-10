# Lab 2
### Modified Files:
- Total of **10** files:
   - condvar.h, defs.h, Makefile, race.c, syscall.c, syscall.h, sysproc.c,
     ulib.c, user.h, usys.S

### Steps:

1. Write 'race.c' to have a parent process forking two child processes
   - This program is provided in Lab2.pdf
   - Add the program to the XV6 source code folder (from Lab 1)
   - Add `_race\` to `UPROGS` within Makefile
   - `make qemu-nox` and `race` to run the program **multiple times**
   - *Do you always get the same order of execution?*
      - No.
   - *Does Child 1 always execute before Child 2?*
      - No. They execute in different orders seemingly at random.
   - Add `sleep(5);` in 'race.c' before Child 1 prints "Child Executing"
   - *What do you notice?*
      - After multiple runs, Child 2 seems to run before Child 1 every time.
   - *Can we guarantee that Child 1 always executes before Child 2?*
      - Adding `sleep(5);` before Child 2 prints "Child Executing" appears to
        achieve this.

2. Define a spinlock to be used in 'race.c'
   - Add `#include "spinlock.h"` and `#include "types.h"` to 'ulib.c'
   - Add function prototypes and structs to 'user.h' (per Lab2.pdf instructions)
   - New spinlock can now be used in 'race.c'

3. Add condition variables
   - Define condition variable struct in 'condvar.h'
   - Add two syscalls to 'syscall.h'
   - Add two syscalls to 'usys.S'
   - Add two syscalls in two locations in 'syscall.c'
   - Add condvar struct to 'user.h' **already done, error in Lab2.pdf?**
   - Add sleep1 function def to 'proc.c'
   - Add function prototype to 'defs.h'
   - Add include and new functions to 'sysproc.c'
   - *Why add these system calls for the operations on condition variables? Why
      not have them in ulib.c as we did for the spinlock?*

4. Use condition variables
   - Modify 'race.c' according to Lab2.pdf
   - Compile and run
   - *Does Child 1 always execute before Child 2?*
      - Child 1 executes and then deadlock occurs.

5. Lost wakeups
   - Modify 'race.c' to fix deadlock issue


### Execution:

1. Step one
