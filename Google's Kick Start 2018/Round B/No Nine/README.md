## Problem
No Nine is a counting game that you can try when you are bored. In this game,
you are only allowed to say numbers that are legal. A number is legal if and
only if all of the following are true:

- it is a natural number (i.e. in the set {1, 2, 3...})
- it does not contain the digit 9 anywhere in its base 10 representation
- it is not divisible by 9

For example, the numbers 16 and 17 are legal. The numbers 18, 19, 17.2, and -17
are not legal.

On the first turn of the game, you choose and say a legal number F. On each
subsequent turn, you say the next legal number. For example, if you played a
game with F = 16, you would say 16, 17, 20, 21, and so on.

Alice is very good at this game and never makes mistakes. She remembers that she
played a game in which the first number was F and the last number was L (when
  she got tired of the game and stopped), and she wonders how many turns were in
  the game in total (that is, how many numbers she said).

## Input
The first line of input contains a single integer T, the number of test cases.
Each test case starts with a line containing an integer N, the number of books
on the bookshelf. The next line contains N integers separated by spaces,
representing s0, s1, ..., sN-1, which are the worths of the books.

## Output
For each test case, output one line containing "Case #X: ", followed by t0, t1,
..., tN-1 in order, and separated by spaces. X is the test case number
(starting from 1) and t0, t1, ..., tN-1 forms the resulting sequence of worths
of the books from the left to the right.

## Limits
- 1 ≤ T ≤ 100.
- Time limit: 60 seconds per test set.
- Memory limit: 1 GB.
- F does not contain a 9 digit.
- F is not divisible by 9.
- L does not contain a 9 digit.
- L is not divisible by 9.

## Small dataset (Test set 1 - Visible)
1 ≤ F < L ≤ 10^6.

## Large dataset (Test set 2 - Hidden)
1 ≤ F < L ≤ 10^18.
