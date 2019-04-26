/*
 * Compile: `javac RationalNumberTree.java`
 * Execute: `java RationalNumberTree < infile > outfile`
 * Execute (powershell):
 * > Get-Content infile | java RationalNumberTree | Out-File -Filepath outfile -Encoding ASCII
 */

import java.util.*;
import java.io.*;

/*
 * Class for the Reational Number Tree problem from round A of Google's 2013
 * Kick Start coding challenge.
 */
public class RationalNumberTree {
  static Scanner in;
  static int[] frac = { 0, 1 };

  /*
   * Main program.
   */
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

  /*
   * Method for a single test case. Splits a line into the problem type, and
   * either N, or p and q.
   *
   * @param String of line of infile
   * @return String of correct output
   */
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
 * Method to find the Nth element of the array representing the perfect binary
 * tree.
 *
 * @param int n
 * @param int[] element of n in the tree
 */
  private static int[] findNthElement(int n) {
    if (n > 0) {
      findNthElement(n / 2);
    }

    frac[~n & 1] += frac[n & 1];

    return frac;
  }

  /*
   * Method to find the position of some p/q elements within the array
   * representing the perfect binary tree. Finds the depth of the element and
   * uses this to find the depth to calculat the position in the array.
   *
   * @param int p
   * @param int q
   * @ return int position
   */
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

  /*
   * Method to get the breadth of pai p/q using depth in the complete binary
   * tree. Traverse the tree upwards until we get to the root, keeping track
   * of every time we shift left in the tree, as well as the total shifts
   * relative to the bottom leaves depending on the depth.
   *
   * @param int p
   * @param int q
   * @param int depth
   * @return int breadth
   */
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
