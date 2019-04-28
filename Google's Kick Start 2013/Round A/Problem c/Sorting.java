/*
 * Compile: `javac Sorting.java`
 * Execute: `java Sorting < infile > outfile`
 * Execute (powershell):
 * > Get-Content infile | java Sorting | Out-File -Filepath outfile -Encoding ASCII
 */

 import java.util.*;
 import java.io.*;

/*
 * Class for the Sorting problem from round A of Google's 2013 Kick Start coding
 * challenge.
 */
 public class Sorting {
   static Scanner in;
   static int[] labels;
   static ArrayList<Integer> alex, bob;

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
       res += testCase(in.nextLine(), in.nextLine());

       System.out.println(res);
     }
   }

   /*
    * Method for a single test case. Organizes books according to rules.
    *
    * @param String how many elements to be sorted
    * @param String the elements for sorting
    * @return String of correct input
    */
   private static String testCase(String str_size, String str){
     String res = "";
     String[] split = str.split(" ");
     int size = Integer.parseInt(str_size);

     initArrays(size);
     getLabels(size, split);

     Collections.sort(alex);
     Collections.sort(bob);
     Comparator c = Collections.reverseOrder();
     Collections.sort(bob, c);

     res += sortBooks(size);

     return res;
   }

   /*
    * Method to initialize the global array variables.
    */
   private static void initArrays(int size) {
     labels = new int[size];
     alex = new ArrayList<Integer>();
     bob = new ArrayList<Integer>();
   }

   /*
    * Method to generate an array of bits to determine if a book belongs to
    * Alex or Bob.
    *
    * @param int number of books
    * @param String[] worth of each book
    */
   private static void getLabels(int size, String[] split) {
     for (int i = 0; i < size; i++) {
       if (Integer.parseInt(split[i]) % 2 == 0) {
         // Even values belong to Bob.
         labels[i] = 0;

         bob.add(Integer.parseInt(split[i]));
       } else {
         // Odd values belong to Alex.
         labels[i] = 1;

         alex.add(Integer.parseInt(split[i]));
       }
     }
   }

   /*
    * Method to sort all books into a result string according to the rules.
    * Label order is maintained, while Alex's books are sorted in descending
    * wroth while Bob's books are sorted in ascending worth.
    *
    * @param int number of books
    * @return String of correct output
    */
   private static String sortBooks(int index) {
     String res = "";
     int a = 0, b = 0;

     for (int i = 0; i < index; i++) {
       if (labels[i] == 0) {
         res += Integer.toString(bob.get(b));
         b++;
       } else if (labels[i] == 1) {
         res += Integer.toString(alex.get(a));
         a++;
       }

       if (i < (index - 1)) {
         res += " ";
       }
     }

     return res;
   }
 }
