import java.util.*;
import java.io.*;

public class NumberSwapper {
  static Scanner in;

  /*
   * Main program.
   */
  public static void main(String args[]) {
    in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    if (args.length != 2) {
      System.out.println("\nProgram is meant to be run with 2 arguments, " +
                         "integers A and B. Exiting...\n");
      System.exit(0);
    }

    int a = Integer.parseInt(args[0]);
    int b = Integer.parseInt(args[1]);

    printVals(a, b);

    System.out.println("\nCalling 'swap' method on A and B...");

    int[] arr = swap(a, b);
    new_a = arr[0];
    new_b = arr[1];

    printVals(new_a, new_b);
  }

  /*
   * Method to print the A and B values.
   */
  public static void printVals(int a, int b) {
    System.out.println("\nValue of A: " + a + "\n" + "Value of B: " + b);
  }

  /*
   * This method accept some integers A and B, and should return these integers,
   * swapped, without using any temporary variables.
   */
  public static int[] swap(int a, int b) {
    int[] arr = new int[2];

    // YOUR SOLUTION GOES HERE

    arr[0] = a;
    arr[1] = b;

    return arr;
  }
}
