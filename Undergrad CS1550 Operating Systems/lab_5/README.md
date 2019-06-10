# Lab 3
### Modified Files:
- Total of **3** files:
   - big.c, Makefile, param.h

### Steps:

1. Download XV6 source code
   - `git clone git://github.com/mit-pdos/xv6-public.git`

2. Setup
   - Modify CPUs def in 'Makefile': `CPUS := 1`
   - Add `QEMUEXTRA = -snapshot` in 'Makefile'
   - Add 'big.c' to UPROGS in 'Makefile'
   - Add 'big.c' to XV6 directory and run




### Execution:

1. Run XV6 machine
   - From linux or thoth.cs.pitt.edu, run `make qemu-nox` within the 'xv6-public' directory
   - Exit XV6 with **CTRL+A** then **X**
