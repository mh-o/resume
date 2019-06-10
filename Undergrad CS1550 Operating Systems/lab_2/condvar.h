// This file was created solely for Lab 2

#include "spinlock.h"

struct condvar {
  struct spinlock lk;
};
