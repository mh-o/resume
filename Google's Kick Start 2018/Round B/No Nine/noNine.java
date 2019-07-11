/*
 * Compile: `javac noNine.java`
 * Execute: `java noNine < infile > outfile`
 * Execute (powershell):
 * > Get-Content infile | java noNine | Out-File -Filepath outfile -Encoding ASCII
 */

 import java.util.*;
 import java.io.*;

/*
 * Class for the No Nine problem from round B of Google's 2018 Kick Start coding
 * challenge.
 */
 public class noNine {
   static Scanner in;

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

   private static String testCase(String line) {
     String[] vals = line.split(" ");
     long first = Long.parseLong(vals[0]);
     long last = Long.parseLong(vals[1]);

     String res = countValidNumbers(first, last);

     return res;
   }

   private static String countValidNumbers(long first, long last) {
     long val = 0;

     for (long l = first; l <= last; l++) {
       if (!containsNine(l) && !divisbleByNine(l)) {
         val++;
       }
     }

     String res = Long.toString(val);
     return res;
   }

   private static Boolean containsNine(long val) {
     String digits = Long.toString(val);

     if (digits.contains("9")) {
       return true;
     }

     return false;
   }

   private static Boolean divisbleByNine(long val) {
     if (val % 9 == 0) {
       return true;
     }

     return false;
   }
 }
