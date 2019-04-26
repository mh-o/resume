/*
 * Compile: `javac Sorting.java`
 * Execute: `java Sorting < infile > outfile`
 * Execute (powershell):
 * > Get-Content infile | java Sorting | Out-File -Filepath outfile -Encoding ASCII
 */

 import java.util.*;
 import java.io.*;

 public class Sorting {
   static scanner in;

   public static void main(String[] args) {
     in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

     int num_test_cases = in.nextInt();
     in.nextLine();

     for (int i = 0; i < num_test_cases; i++) {
       String res = "Case #" + (i + 1) + ": ";
       res += testCase(in.nextLine());
     }
   }

   private static String testCase() {
     String res = "";

     return res;
   }
 }
