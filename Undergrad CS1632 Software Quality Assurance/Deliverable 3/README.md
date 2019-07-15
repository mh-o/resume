# Program MUST be run with *JRuby* -- NOT Ruby -- at the command line!

While running with Ruby will still work (mostly), becuase of differences between the two, running with Ruby will NOT validate each line against the REGEX pattern, so invalid characters will not be caught when running with Ruby. JRuby will validate each line against REGEX, and it will also offer a large speedup on Intel 4-core machines.


### CURRENTLY IMPLEMENTED
1. file_parser.rb (split file lines into arrays of like elements, REGEX each line for validation of syntax)
2. time_stamp_checker.rb (verify timestamps are in ascending order, print line error if found)
3. error_checker.rb (total up total errors in blockchain, if > 0, inform of invalid block chain)
4. block_num_checker (makes sure blocks start at 0 and incriment by 1)
5. transaction_checker.rb (add new people to hash, check for negative balence, print all when done)
6. hash_checker.rb (checks previous hash, then calculates hash for each line and checks that)
7. verifier_sim.rb (generates a flame graph)

### PERFORMANCE ENHANCEMENTS

Our program offers the following performance enhancements on the following processors: (java v1.8.0_161)
  - Intel Core i5-8520 (4 cores, 8 threads): ~21 sec -> 10 sec (~210% faster)
  - AMD A8-7410 (4 cores): ~62 sec -> ~29 sec (~214% faster)
  - Intel Core i7-5820K (6 cores, 12 threads): ~19 sec -> 8 sec (~238% faster)
  
So obviously I couldn't just not use my 5829K's full potential. On 6 cores, 12 threads @4.4 GHz my best runtime was just over 4.03 seconds (couldn't get sub 4 seconds... maybe if I pushed my overclock to 4.5 GHz but I decided 3.9 seconds wasn't worth frying $400. Anyways, that brings us to:
  - Intel Core i7-5820k (6 cores, 12 threads): ~19 sec -> 4 sec (~475% faster)
I'll admit it's odd that 8 -> 12 threads (50% more threads) cut the run time in half. This must have something to do with the hex-core CPU just being engineered to like 12 threads better than 8. Running at 4.4 GHz doesn't hurt either.

### STRETCH GOAL?

The method of threading in this program is simple. For each thread, divide the array of blocks into one array per thread, and split the hash checking of these arrays between the threads. Since I re=use all my original methods, it would be fun/straightforward to code this program in a way that allows for variable threading. Might tackle it later on if I'm bored.
