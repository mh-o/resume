/*
 * Compile: `javac BadHorse.java`
 * Execute: `java BadHorse < infile`
 * Execute (powershell): `Get-Content infile | java BadHorse`
 */

import java.util.*;
import java.io.*;

/*
 * Class for the Bad Horse problem from the practice round of Google's 2013
 * Kick Start coding challenge.
 */
public class BadHorse {
  static Scanner in;
  static ArrayList<Member> members;

  /*
   * Main program.
   */
  public static void main(String[] args) {
    in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
    members = new ArrayList<Member>();

    int num_test_cases = in.nextInt();

    // Iterate through all test cases.
    for (int i = 0; i < 1; i++) {
      int num_pairs = in.nextInt();
      in.nextLine();

      testCase(num_pairs);
    }

    printMembers();
  }

  /*
   * Method for a single test case, takes all troublesome pairs and decides if
   * they case be split into two groups of completely disconnected graphs.
   */
  private static void testCase(int num_pairs) {
    // Iterate through all pairs for a single test case.
    for (int i = 0; i < num_pairs; i++) {
      String line = in.nextLine();
      String[] split = line.split(" ");

      addMember(split[0], split[1]);
      addMember(split[1], split[0]);
    }
  }

  /*
   * Method to add a member to the members list if they are not already present
   * in the list. Will also update the member object to add the enemy for that
   * member.
   */
  private static void addMember(String member, String enemy) {
    int index = searchMembers(member);

    if (index < 0) {
      // Create our new member since we couldn't find them in our list.
      Member new_member = new Member(member);

      // Add the enemy if they don't exist yet and then add our new member.
      new_member.addEnemy(enemy);
      members.add(new_member);
    } else {
      members.get(index).addEnemy(enemy);
    }
  }

  /*
   * Helper method to search through all members in a test case to see if they
   * exist within the list.
   *
   * @param string of member to search
   * @return index of member if they exist, -1 otherwise
   */
  private static int searchMembers(String str) {
    for (int i = 0; i < members.size(); i++) {
      if (members.get(i).getName().equals(str)) {
        return i;
      }
    }

    return -1;
  }

  /*
   * Helper method to print all unique members for a test case.
   */
  private static void printMembers() {
    System.out.println();

    // Iterate through all unique members in the order we found them.
    for (int i = 0; i < members.size(); i++) {
      System.out.print(members.get(i).getName() + ": ");

      String[] names = members.get(i).getEnemies();

      // Iterate through each enemy of the current member.
      for (int j = 0; j < names.length; j++) {
        System.out.print(names[j]);

        if (j != (names.length - 1)) {
          System.out.print(", ");
        } else {
          System.out.println();
        }
      }
    }

    System.out.println();
  }
}
