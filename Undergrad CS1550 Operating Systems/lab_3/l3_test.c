#include "types.h"
#include "user.h"
#include "syscall.h"

int main(int argc, char *argv[]) {
  printf(1, "Testing priority...\n\n");

  if (fork() == 0) {
    printf(1, "1\n");
    setpriority(100);
    printf(1, "2\n");
    sleep(1);

    printf(1, "My priority is 100, I should print first.\n");
  }

  setpriority(150);
  sleep(1);
  printf(1, "My priority is 150, I should print second.\n");
}
