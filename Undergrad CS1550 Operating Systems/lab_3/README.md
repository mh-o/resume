# Lab 3
### Modified Files:
- Total of **7** files:
   - proc.c, proc.h, syscall.c, syscall.h, sysproc.c, user.h, usys.S

### Steps:

1. Download XV6 source code
   - `git clone git://github.com/mit-pdos/xv6-public.git`

2. Add priority based scheduler to XV6
   - Add integer to track priority to 'proc.h'
   - Modify code in 'proc.c' to execute highest priority using round-robin

3. Add system call to set priority of a process
  - Edit 'syscall.h', 'user.h', 'usys.S', and 'syscall.c' to add a system call
    to XV6

### Execution:

1. Run XV6 machine
   - From linux or thoth.cs.pitt.edu, run `make qemu-nox` within the 'xv6-public' directory
   - Exit XV6 with **CTRL+A** then **X**
