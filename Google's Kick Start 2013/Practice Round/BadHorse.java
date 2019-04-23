/*
 * Compile: `javac BadHorse.java`
 * Execute: `java BadHorse < infile`
 * Execute (powershell): `Get-Content infile | java BadHorse`
 */

import java.util.*;
import java.io.*;

public class BadHorse {
  public static void main(String[] args) {
    // To read input.
    Scanner in = new Scanner(new BufferedReader(
      new InputStreamReader(System.in)));

    int num_test_cases = in.nextInt();

    // Iterate through all test cases.
    for (int i = 0; i < num_test_cases; i++) {
      int num_pairs = in.nextInt();
      testCase(num_pairs);
    }
  }

  private static void testCase(int num_pairs) {

  }
}
