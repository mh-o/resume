# Project 1
### Modified Files:
   - Total of **5** files:
      - arch/i386/kernel/syscall_table.S
      - include/asm/unistd.h
      - kernel/sys.c
      - sem.h
      - trafficsim.c

### Steps:
1. Setup project environment (see Project1Setup.pdf)
2. Setup new syscalls in linux
   - Add `.long sys_cs1550_down` and `.long sys_cs1550_up` to 'arch/i386/kernel/syscall_table.S'
   - Add up and down syscalls to 'include/asm/unistd.h' and increment number of syscalls from 325 to 327
3. Create implementation of syscalls
   - Actual implementation located in 'kernel/sys.c'
   - Need to import 'sched.h' for TASK_INTERRUPTIBLE `#include <linux/sched.h>`in 'kernel/sys.c'
4. Add struct cs1550_sem to trafficsim.c

### Building Changes:
1. Rebuild kernel
   - from thoth.cs.pitt.edu, `make ARCH=i386 bzImage`
2. Copy files to QEMU
   - boot into QEMU 'original':
      - `scp mho8@thoth.cs.pitt.edu:/u/OSLab/mho8/linux-2.6.23.1/arch/i386/boot/bzImage .`
      - `scp mho8@thoth.cs.pitt.edu:/u/OSLab/mho8/linux-2.6.23.1/System.map .`
3. Install kernel to QEMU
   - From /root:
      - `cp bzImage /boot/bzImage-devel`
      - `cp System.map /boot/System.map-devel`

### Execution:
1. Compile trafficsim.c
   - From thoth:
      - `gcc -m32 -o trafficsim -I /u/OSLab/mho8/linux-2.6.23.1/include/ trafficsim.c`
   - From root in QEMU:
      - `scp mho8@thoth.cs.pitt.edu:/u/OSLab/mho8/trafficsim .`
2. Running trafficsim
   - From within QEMU:
      - `./trafficsim`
