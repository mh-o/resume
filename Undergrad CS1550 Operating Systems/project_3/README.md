# Project 3
### Modified Files:
   - Total of **1** files:
      - aptsim.c (main program)

### Steps:
1. Figure out how to properly parse CMD arguments.

2. Write function to load tracefile into memory.

3. Implement the optimal page replacement algorithm.
   - I figured this would be the most difficult so I started with this one.
     I used linear search to look for the next time each page was referenced. If
     I wanted to improve performance, I would have sorted the instructions into
     a separate array to utilize binary search for lookup. Since the run-time
     of the longest files was under a second, I decided it wasn't necessary.

4. Implement the aging page replacement algorithm.
   - If optimal was the most difficult, aging was somewhere in the middle. Every
     clock tick the 8-bit aging counter gets shifted to increase a page's age.
     My program was seg-faulting for awhile when I would clear the R bits, but
     after a few hours I realized I was just using an out-of-bounds index when
     accessing my page table.

5. Implement the FIFO page replacement algorithm.
  - My implementation of FIFO is very naive, but it works. I essentially just
    set an age for each page which is equal to the line of the instruction,
    then I just evict the min instruction index.

### Building Changes/Execution:
1. Compile:
   - `gcc -o vmsim vmsim.c`

2. Execution:
   - `./vmsim -n N -a A file.trace`
   - Where N = (# of frames)
   - Where A = (opt|fifo|)
   - Where file.trace = (gcc.trace|gzip.trace|swim.trace)

   - `./vmsim -n N -a A -r R file.trace`
   - Where N = (# of frames)
   - Where A = (aging)
   - Where file.trace = (gcc.trace|gzip.trace|swim.trace)

### Explanation:
