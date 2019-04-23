/*
 * Compile: `javac BadHorse.java`
 * Execute: `java BadHorse < infile`
 * Execute (powershell): `Get-Content infile | java BadHorse`
 */

import java.util.*;
import java.io.*;

public class BadHorse {

  static Scanner in;
  static ArrayList<Member> members;

  public static void main(String[] args) {
    // To read input.
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

  private static void testCase(int num_pairs) {
    // Iterate through all pairs for a single test case.
    for (int i = 0; i < num_pairs; i++) {
      String line = in.nextLine();
      String[] split = line.split(" ");

      addMember(split[0], split[1]);
      addMember(split[1], split[0]);
    }
  }

  private static void addMember(String member, String enemy) {
    if (!searchMembers(member)) {
      // Create our new member since we couldn't find them in our list.
      Member new_member = new Member(member);

      // Add the enemy if they don't exist yet and then add our new member.
      new_member.addEnemy(enemy);
      members.add(new_member);
    }
  }

  private static Boolean searchMembers(String str) {
    for (int i = 0; i < members.size(); i++) {
      if (members.get(i).getName().equals(str)) {
        return true;
      }
    }

    return false;
  }

  private static void printMembers() {
    for (int i = 0; i < members.size(); i++) {
      System.out.println(members.get(i).getName());
    }
  }
}
