# Lab 1
### Modified Files:
- Total of **9** files:
   - defs.h, Makefile, proc.c, proc.h, syscall.c, syscall.h, sysproc.c, user.h, usys.S

### Steps:

1. Download XV6 source code
   - `git clone git://github.com/mit-pdos/xv6-public.git`

2. Make basic changes to add a system call in XV6
   - Define system call and number in 'syscall.h': `#define SYS_getcount 22`
   - Initialize in 'syscall.c'
   - Add system call to 'user.h': `int getcount(int)`
   - Define in 'usys.S': `SYSCALL(getcount)`
   - Add 'getcount' function to return `p->pcount`
   - Add getcount in UPROGS of Makefile

3. Add data structure to track call count
   - In proc struct (proc.h), add array of integers to track call count: `int pcount[22]`
   - Initialize pcount to 0 in for loop of allocproc 'proc.c'
   - Increment pcount in 'syscall' (syscall.c)
   - Add def for getcount in defs.h
   - Add getcount to 'proc.c' to return `pcount[syscallid]`

### Execution:

1. Run XV6 machine
   - From linux or thoth.cs.pitt.edu, run `make qemu-nox` within the 'xv6-public' directory
   - Run lab test program with `./getcount` within XV6 machine
   - Exit XV6 with **CTRL+A** then **X**
