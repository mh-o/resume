/*
 * Compile: `javac RationalNumberTree.java`
 * Execute: `java RationalNumberTree < infile > outfile`
 * Execute (powershell):
 * > Get-Content infile | java RationalNumberTree | Out-File -Filepath outfile -Encoding ASCII
 */

import java.util.*;
import java.io.*;

public class RationalNumberTree {
  static Scanner in;
  static int[] frac = { 0, 1 };

  public static void main(String[] args) {
    in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    int num_test_cases = in.nextInt();
    in.nextLine();

    // Iterate through all test cases.
    for (int i = 0; i < num_test_cases; i++) {
      String res = "Case #" + (i + 1) + ": ";
      res += testCase(in.nextLine());

      System.out.println(res);
    }
  }

  private static String testCase(String str) {
    String[] split = str.split(" ");
    String res = "";
    frac[0] = 0;
    frac[1] = 1;

    if (split[0].equals("1")) {
      int[] val = findNthElement(Integer.parseInt(split[1]));

      res += Integer.toString(val[0]);
      res += " ";
      res += Integer.toString(val[1]);
    } else if (split[0].equals("2")) {
      int val = findPosition(Integer.parseInt(split[1]),
                             Integer.parseInt(split[2]));

      res += Integer.toString(val);
    }

    return res;
  }

/*
 * Method to
 */
  private static int[] findNthElement(int n) {
    if (n > 0) {
      findNthElement(n / 2);
    }

    frac[~n & 1] += frac[n & 1];

    return frac;
  }

  private static int findPosition(int p, int q) {
    int depth = getDepth(p, q);
    int breadth = getBreadth(p, q, depth);
    int pos = 1 ;

    for (int i = 0; i < depth; i++) {
      pos += Math.pow(2, i);
    }

    pos += breadth;

    return pos;
  }

 /*
  * Method to get the depth of pair p/q in a complete binary tree. Traverse up
  * the tree until we get to the root, where p = q = 1.
  *
  * @param int p
  * @param int q
  * @return int depth
  */
  private static int getDepth(int p, int q) {
    int depth = 0;
    double x = p;
    double y = q;

    while (x / y != 1) {
      if (x / y < 1) {
        y = (y - x);
      } else if (x / y > 1) {
        x = (x - y);
      }

      depth++;
    }

    return depth;
  }

  private static int getBreadth(int p, int q, int depth) {
    int breadth = 0;
    double x = p;
    double y = q;

    for (int i = 0; i < depth; i++) {
      if (x / y < 1) {
        y = (y - x);
      } else if (x / y > 1) {
        x = (x - y);
        breadth += (Math.pow(2, i));
      }
    }

    return breadth;
  }
}
