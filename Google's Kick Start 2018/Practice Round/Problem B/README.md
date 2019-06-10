## Problem
A "0/1 string" is a string in which every character is either 0 or 1. There are
two operations that can be performed on a 0/1 string:

switch: Every 0 becomes 1 and every 1 becomes 0. For example, "100" becomes
"011".

reverse: The string is reversed. For example, "100" becomes "001".

Consider this infinite sequence of 0/1 strings:

S0 = ""

S1 = "0"

S2 = "001"

S3 = "0010011"

S4 = "001001100011011"

...

SN = SN-1 + "0" + switch(reverse(SN-1)).

You need to figure out the Kth character of Sgoogol, where googol = 10100.

## Input
The first line of the input gives the number of test cases, T. Each of the next
T lines contains a number K.

## Output
For each test case, output one line containing "Case #x: y", where x is the test
case number (starting from 1) and y is the Kth character of Sgoogol.

## Limits
1 ≤ T ≤ 100.

## Small dataset
1 ≤ K ≤ 105.

## Large dataset
1 ≤ K ≤ 1018.
