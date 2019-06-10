#include "types.h"
#include "stat.h"
#include "user.h"
#include "condvar.h" // Step 4

// We want Child 1 to execute first, then Child 2, and finally Parent.
int main() {
  struct condvar cv; // Step 4
  init_lock(&cv.lk); // Step 4
  int pid = fork(); // fork the first child

  if(pid < 0) {
    printf(1, "Error forking first child.\n");
  } else if(pid == 0) {
    printf(1, "Child 1 Executing\n");
    lock(&cv.lk); // Step 4
    cv_signal(&cv); // Step 4
    unlock(&cv.lk); // Step 4
  } else {
    pid = fork(); // fork second child

    if(pid < 0) {
      printf(1, "Error forking second child.\n");
    } else if(pid == 0) {
      lock(&cv.lk); // Step 4
      cv_wait(&cv); // Step 4
      unlock(&cv.lk); // Step 4
      printf(1, "Child 2 Executing\n");
    } else {
      printf(1, "Parent Waiting\n");

      int i;
      for(i = 0; i<2; i++) {
        wait();
      }

      printf(1, "Children completed\n");
      printf(1, "Parent Executing\n");
      printf(1, "Parent exiting\n");
    }
  }

  exit();
}
