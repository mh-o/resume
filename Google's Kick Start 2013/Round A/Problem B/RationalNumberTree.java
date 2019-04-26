/*
 * Compile: `javac RationalNumberTree.java`
 * Execute: `java RationalNumberTree < infile > outfile`
 * Execute (powershell): `Get-Content infile | java RationalNumberTree |
 *                        Out-File -Filepath outfile -Encoding ASCII`
 */

import java.util.*;
import java.io.*;

public class RationalNumberTree {
  static Scanner in;

  public static void main(String[] args) {
    in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    int num_test_cases = in.nextInt();
    in.nextLine();

    // Iterate through all test cases.
    for (int i = 0; i < 4; i++) {
      String res = "Case #" + i + ": ";
      res += testCase(in.nextLine());

      System.out.println(res);
    }
  }

  private static String testCase(String str) {
    String[] split = str.split(" ");
    String res = "";

    if (split[0].equals("1")) {
      int[] val = findNthElement(Integer.parseInt(split[1]));

      res += Integer.toString(val[0]);
      res += " ";
      res += Integer.toString(val[1]);
    } else if (split[1].equals("2")) {
      findPosition(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    return res;
  }

  static int[] frac = { 0, 1 };

  private static int[] findNthElement(int n) {
    if (n > 0) {
      findNthElement(n / 2);
    }

    frac[~n & 1] += frac[n & 1];

    return frac;
  }

  private static void findPosition(int p, int q) {

  }
}
