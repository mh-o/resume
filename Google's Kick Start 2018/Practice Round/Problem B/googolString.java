/*
 * Compile: `javac googolString.java`
 * Execute: `java googolString < infile > outfile`
 * Execute (powershell):
 * > Get-Content test.in | java googolString | Out-File -Filepath output.txt -Encoding ASCII
 */

 import java.io.*;
 import java.util.*;

public class googolString {
  static Scanner in;
  static String running;

  public static void main(String[] args) {
    in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    int num_test_cases = in.nextInt();
    in.nextLine();

    running = "";

    for (int i = 0; i < num_test_cases; i++) {
      String res = "Case #" + (i + 1) + ": ";

      //in.nextLine();
      res += testCase(running);

      System.out.println(res);
    }
  }

  private static String testCase(String str) {
    String res = str;
    in.nextLine();
    int index = in.nextInt();


    while (running.length() < index) {
      res += "0";
      res += switchString(reverseString(str));
      running = res;
      str = running;
    }

    str = Character.toString(res.charAt(index));

    return res;
  }

  private static String reverseString(String str) {
    StringBuilder sb = new StringBuilder(str);
    String res = sb.reverse().toString();

    return res;
  }

  private static String switchString(String str) {
    String res = "";

    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == '0') {
        res += "1";
      } else {
        res += "0";
      }
    }

    return res;
  }
}
