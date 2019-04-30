/*
 * Compile: `javac Sorting.java`
 * Execute: `java Sorting < infile > outfile`
 * Execute (powershell):
 * > Get-Content infile | java CrossTheMaze | Out-File -Filepath outfile -Encoding ASCII
 */

 import java.util.*;
 import java.io.*;

 /*
  * Class for the Cross the Maze problem from round A of Google's 2013 Kick
  * Start coding challenge.
  */
public class CrossTheMaze {
  static Scanner in;
  static Maze the_maze;
  static Robot edison;
  static int num_steps = 0;

  /*
   * Main program.
   */
  public static void main(String[] args) {
    in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    int num_test_cases = in.nextInt();
    in.nextLine();

    for (int i = 0; i < num_test_cases; i++) {
      String res = "Case #" + (i + 1) + ": ";

      res += testCase(in.nextInt());
      System.out.println(res);
    }
  }

  /*
   * Method for a single test case. Determins if the maze can be solved in less
   * than 10,000 steps and returns the path to take if solvable.
   *
   * @param int size of the maze
   * @return String of correct output
   */
  private static String testCase(int size) {
    String res = "";
    in.nextLine();

    the_maze = new Maze(size, in);
    the_maze.setStartFinish(in.nextLine());

    edison = new Robot(the_maze.getStart());

    res += solveMaze();

    return res;
  }

  private static String solveMaze() {
    //System.out.println("Edison is facing: " + edison.isFacing());
    //firstStep();
    String res = "";
    String path = "";

    for (int i = 0; i < 10000; i++) {
      if (reachedExit()) {
        return path;
      }

      //System.out.println("Edison is facing: " + edison.isFacing());
      path += nextStep();
    }
    res += "out of energy";

    return res;
  }

  private static void firstStep() {
    int[] location = edison.getLocation();

    edison.turnAround();

  }

  private static Boolean reachedExit() {
    int[] loc = edison.getLocation();
    int[] fin = the_maze.getFinish();
    //System.out.println("finish: " + fin[0] + " " + fin[1]);

    if (loc[0] == fin[0] && loc[1] == fin[1]) {
      return true;
    }

    return false;
  }

  private static String nextStep() {
    int[] loc = edison.getLocation();
    String res = "";
    //System.out.println("Edison is at: " + loc[0] + ", " + loc[1]);

    if (hasDeadEnd(loc)) {
      //System.out.println(1);
      edison.turnAround();
    } else if (hasWallLeft(loc) && hasSpaceAhead(loc)) {
      //System.out.println(2);
      res += edison.moveForward();
    } else if (hasWallBackLeft(loc) && hasSpaceLeft(loc)) {
      //System.out.println(3);
      edison.turnLeft();
      res += edison.moveForward();
    } else {
      //System.out.println(4);
      edison.turnRight();
    }

    num_steps++;
    return res;
    //System.out.println("Edison is now at: " + new_loc[0] + ", " + new_loc[1]);
  }

  private static Boolean hasSpaceAhead(int[] loc) {
    Boolean empty = false;
    int x = loc[0];
    int y = loc[1];

    switch (edison.isFacing()) {
      case 'N': empty = the_maze.isSpaceEmpty((x - 1), y); break;
      case 'E': empty = the_maze.isSpaceEmpty(x, (y + 1)); break;
      case 'S': empty = the_maze.isSpaceEmpty((x + 1), y); break;
      case 'W': empty = the_maze.isSpaceEmpty(x, (y - 1)); break;
    }

    return empty;
  }

  /*     -1 y +1
   *  -1
   *   x  O E
   *  +1
   *
   */
  private static Boolean hasSpaceLeft(int[] loc) {
    Boolean empty = false;
    int x = loc[0];
    int y = loc[1];

    switch (edison.isFacing()) {
      case 'N': empty = the_maze.isSpaceEmpty(x, (y - 1)); break;
      case 'E': empty = the_maze.isSpaceEmpty((x - 1), y); break;
      case 'S': empty = the_maze.isSpaceEmpty(x, (y + 1)); break;
      case 'W': empty = the_maze.isSpaceEmpty((x + 1), y); break;
    }

    return empty;
  }

  private static Boolean hasWallLeft(int[] loc) {
    Boolean wall = false;
    int x = loc[0];
    int y = loc[1];

    switch (edison.isFacing()) {
      case 'N': wall = !the_maze.isSpaceEmpty(x, (y - 1)); break;
      case 'E': wall = !the_maze.isSpaceEmpty((x - 1), y); break;
      case 'S': wall = !the_maze.isSpaceEmpty(x, (y + 1)); break;
      case 'W': wall = !the_maze.isSpaceEmpty((x + 1), y); break;
    }

    //System.out.println("wall left: " + wall);
    return wall;
  }

  private static Boolean hasWallRight(int[] loc) {
    Boolean wall = false;
    int x = loc[0];
    int y = loc[1];

    switch (edison.isFacing()) {
      case 'N': wall = !the_maze.isSpaceEmpty(x, (y + 1)); break;
      case 'E': wall = !the_maze.isSpaceEmpty((x + 1), y); break;
      case 'S': wall = !the_maze.isSpaceEmpty(x, (y - 1)); break;
      case 'W': wall = !the_maze.isSpaceEmpty((x - 1), y); break;
    }

    return wall;
  }

  private static Boolean hasWallAhead(int[] loc) {
    Boolean wall = false;
    int x = loc[0];
    int y = loc[1];

    switch (edison.isFacing()) {
      case 'N': wall = !the_maze.isSpaceEmpty((x - 1), y); break;
      case 'E': wall = !the_maze.isSpaceEmpty(x, (y + 1)); break;
      case 'S': wall = !the_maze.isSpaceEmpty((x + 1), y); break;
      case 'W': wall = !the_maze.isSpaceEmpty(x, (y - 1)); break;
    }

    return wall;
  }

  private static Boolean hasWallBackLeft(int[] loc) {
    Boolean wall = false;
    int x = loc[0];
    int y = loc[1];

    switch (edison.isFacing()) {
      case 'N': wall = !the_maze.isSpaceEmpty((x + 1), (y - 1)); break;
      case 'E': wall = !the_maze.isSpaceEmpty((x - 1), (y - 1)); break;
      case 'S': wall = !the_maze.isSpaceEmpty((x - 1), (y + 1)); break;
      case 'W': wall = !the_maze.isSpaceEmpty((x + 1), (y + 1)); break;
    }

    return wall;
  }

  private static Boolean hasDeadEnd(int[] loc) {
    if (hasWallLeft(loc) && hasWallRight(loc) && hasWallAhead(loc)) {
      return true;
    }

    return false;
  }
}
