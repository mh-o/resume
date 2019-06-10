/*
 * Compile: `javac ReadPhoneNumber.java`
 * Execute: `java ReadPhoneNumber < infile > outfile`
 * Execute (powershell): `Get-Content infile | java ReadPhoneNumber >
 *                        outfile -Encoding ASCII`
 */

 import java.util.*;
 import java.io.*;

/*
 * Class for the Read Phone Number problem from round A of Google's 2013 Kick
 * Start coding challenge.
 */
 public class ReadPhoneNumber {
   static Scanner in;
   static String[] digits = {"zero", "one", "two", "three", "four",
                             "five", "six", "seven", "eight", "nine"};

   static String[] rules = {"double", "triple", "quadruple", "quintuple",
                            "sextuple", "septuple", "octuple", "nonuple",
                            "decuple"};

   /*
    * Main program.
    */
   public static void main(String[] args) {
     in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

     int num_test_cases = in.nextInt();
     in.nextLine();

     // Iterate through all test cases.
     for (int i = 0; i < num_test_cases; i++) {
       String res = "Case #" + (i+1) + ": ";
       res += testCase(in.nextLine());

       System.out.println(res);
     }
   }

   /*
    * Method for a single test case. Splits a line into a phone number and the
    * divide format.
    *
    * @param String of line of infile
    * @return String of correct output
    */
   private static String testCase(String str) {
     String[] split = str.split(" ");
     String[] divFormat = split[1].split("-");

     String res = phoneToString(split[0], divFormat);

     return res;
   }

   /*
    * Method to convert a phone number into the proper english format.
    *
    * @param String of the phone number
    * @param String array for divide format
    * @return String of correct output
    */
   private static String phoneToString(String number, String[] div) {
     String res = "";
     int k = 0;

     // For each divided section.
     for (int i = 0; i < div.length; i++) {
       // For every digit of some divided section.
       for (int j = 0; j < Integer.parseInt(div[i]); j++) {
         int cur_dig = number.charAt(k);
         int repeat = 0;
         int x = 1;

         // While we still have digits left in our section according to the
         // divide format, check to see if the digit is repeated multiple times.
         while (j+x < Integer.parseInt(div[i])) {
           if (number.charAt(k+x) == cur_dig) {
             repeat++;
             x++;
           } else {
             break;
           }
         }

         res += readDigit(number.charAt(k), repeat);

         k++;
         k+=repeat;
         j+=repeat;

         // Format if we're not finished yet.
         if (k < number.length()) {
           res += " ";
         }
       }
     }

     return res;
   }

   /*
    * Method to convert N (repeat) number of digits into english according to
    * the rules and divide format.
    *
    * @param character for the current digit
    * @param int number of times the digit occurs consecutively
    * @return String of correct format
    */
   private static String readDigit(char c, int repeat) {
     int num = Character.getNumericValue(c);
     String res = "";

     // If the digit repeats 1-9 times, append the correct prefix.
     if (repeat > 0 && repeat < 10) {
       res += rules[repeat - 1];
       res += " ";
     } else if (repeat >= 10) {
       // If it's more than 10, we just print it N times.
       for (int i = 0; i < repeat; i++) {
         res += digits[num];

         // Format if we're not finished yet.
         if (i < repeat) {
           res += " ";
         }
       }
     }

     res += digits[num];

     return res;
   }
 }
