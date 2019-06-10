# Lab 3
### Modified Files:
- Total of **4** files:
   - proc.h, sysproc.c, trap.c, vm.c

### Steps:

1. Download XV6 source code
   - `git clone git://github.com/mit-pdos/xv6-public.git`

2. Eliminate allocation from `sys_sbrk()`
   - `sys_sbrk()` located in 'sysproc.c'
   - This system call grows a process's memory size by n bytes via `sys_sbrk(n)`
     and returns the start of the newly allocated region
   - Modify to increment the process's size by n and return the old size
      - 1) Removed lines 54-55 (call to `growproc(n)`)
   - Running `echo hi` on XV6 now produces the following message:
      - *pid 3 sh: trap 14 err 6 on cpu 0 eip 0x12f1 addr 0x4004--kill proc*
   - This message is from the kernel trap handler in 'trap.c' meaning it has
     caught a page fault which the XV6 kernel does not know how to handle. The
     virtual address that caused this page fault is 0x4004.
3. Lazy allocation
   - Modify 'trap.c' to respond to a page fault from user space by mapping a
     newly-allocated page of phsyical memory at the faulting address, and then
     returning back to user space to let the process continue executing.
   - Code is added just before the `cprintf()` that produces the previous
     message. After modifying you should be able to run shell commands `echo`
     and `ls`.
   - All changes:
      - 1) Add `uint oldsz;` to proc struct in 'proc.h' for tracking original
           process size
      - 2) Remove static property of `mappages()` from vm.c
      - 3) Declare `mappages()` in 'trap.c' for use
      - 4) Allocate the page memory in 'trap.c'
      - 5) Handle negative and out-of-bounds arguments in `sys_sbrk()` in
           'sysproc.c'

### Execution:

1. Run XV6 machine
   - From linux or thoth.cs.pitt.edu, run `make qemu-nox` within the 'xv6-public' directory
   - Exit XV6 with **CTRL+A** then **X**
