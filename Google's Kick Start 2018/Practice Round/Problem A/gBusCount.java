/*
 * Compile: `javac gBusCount.java`
 * Execute: `java gBusCount < infile > outfile`
 * Execute (powershell):
 * > Get-Content test.in | java gBusCount | Out-File -Filepath output.txt -Encoding ASCII
 */

import java.io.*;
import java.util.*;

/*
 * Class for the GBus Count problem from Google's 2018 Kickstart coding
 * competition.
 */
public class gBusCount {
  static Scanner in;
  static ArrayList<GBus> gbuses;

  /*
   * Main program.
   */
  public static void main(String[] args) {
    in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    int num_test_cases = in.nextInt();

    for (int i = 0; i < num_test_cases; i++) {
      String res = "Case #" + (i + 1) + ": ";

      in.nextLine();
      res += testCase();

      System.out.println(res);
    }
  }

  private static String testCase() {
    String res = "";

    addGBuses();

    int num_cities = in.nextInt();
    in.nextLine();

    for (int i = 0; i < num_cities; i++) {
      int city = in.nextInt();
      in.nextLine();

      res += getNumBuses(city);

      if (i < (num_cities - 1)) {
        res += " ";
      }
    }

    return res;
  }

  private static void addGBuses() {
    gbuses = new ArrayList<GBus>();
    int num_gbuses = in.nextInt();
    in.nextLine();

    String ranges = in.nextLine();
    String[] split = ranges.split(" ");

    for (int i = 0; i < (num_gbuses * 2); i++) {
      gbuses.add(new GBus(Integer.parseInt(split[i]),
                          Integer.parseInt(split[i + 1])));
      i++;
    }
  }

  private static String getNumBuses(int city) {
    int num_buses = 0;

    for (int i = 0; i < gbuses.size(); i++) {
      if (gbuses.get(i).servesCity(city)) {
        num_buses++;
      }
    }

    String res = Integer.toString(num_buses);
    return res;
  }

  private static void printGBuses() {
    for (int i = 0; i < gbuses.size(); i++) {
      System.out.println(gbuses.get(i).toString(i));
    }
  }
}
