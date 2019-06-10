#include "types.h"
#include "x86.h"
#include "defs.h"
#include "date.h"
#include "param.h"
#include "memlayout.h"
#include "mmu.h"
#include "proc.h"

int
sys_fork(void)
{
  return fork();
}

int
sys_exit(void)
{
  exit();
  return 0;  // not reached
}

int
sys_wait(void)
{
  return wait();
}

int
sys_kill(void)
{
  int pid;

  if(argint(0, &pid) < 0)
    return -1;
  return kill(pid);
}

int
sys_getpid(void)
{
  return myproc()->pid;
}

int
sys_sbrk(void)
{
  int addr;
  int n;

  if(argint(0, &n) < 0)
    return -1;

  addr = myproc()->sz;

  // If an argument passed will cause an out of bounds error, don't allocate.
  if ((myproc()->sz + n) >= 0xfffffff) { // Lab 4
    cprintf("Could not allocate pages!\n");
    return -1;
  }

  myproc()->oldsz = myproc()->sz; // Lab 4
  myproc()->sz = myproc()->sz+n; // Lab 4

  // Lab 4 - handle negative arguments.
  if (n < 0) {
    uint sz;
    // deallocuvm() resides in vm.c and deallocates user pages to bring a proc
    // from oldsz to newsz, and returns newsz.
    if ((sz = deallocuvm(myproc()->pgdir, myproc()->oldsz, myproc()->sz)) == 0) {
      cprintf("Could not deallocate pages!\n");
      return -1;
    }
  }

  // Lab 4 (remove these two lines)
  //if(growproc(n) < 0)
    //return -1;

  return addr;
}

int
sys_sleep(void)
{
  int n;
  uint ticks0;

  if(argint(0, &n) < 0)
    return -1;
  acquire(&tickslock);
  ticks0 = ticks;
  while(ticks - ticks0 < n){
    if(myproc()->killed){
      release(&tickslock);
      return -1;
    }
    sleep(&ticks, &tickslock);
  }
  release(&tickslock);
  return 0;
}

// return how many clock tick interrupts have occurred
// since start.
int
sys_uptime(void)
{
  uint xticks;

  acquire(&tickslock);
  xticks = ticks;
  release(&tickslock);
  return xticks;
}
