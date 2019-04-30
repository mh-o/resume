import java.util.*;
import java.io.*;

public class Maze {
  static int maze_size;
  static int[][] maze;
  static int[] start, finish;

  /*
   * Method to build the maze as a 2D array for each test case.
   *
   * @param int size of the maze
   */
  Maze(int size, Scanner in) {
    maze = new int[size][size];
    maze_size = size;

    // Build maze array.
    for (int i = 0; i < size; i++) {
      addRow(in.nextLine(), i);
    }
  }

  public static void setStartFinish(String line) {
    start = new int[2];
    finish = new int[2];

    String[] start_and_finish = line.split(" ");

    start[0] = Integer.parseInt(start_and_finish[0]) - 1;
    start[1] = Integer.parseInt(start_and_finish[1]) - 1;
    finish[0] = Integer.parseInt(start_and_finish[2]) - 1;
    finish[1] = Integer.parseInt(start_and_finish[3]) - 1;
  }

  /*
   * Method to add a row to the 2D maze array. Stores walls as 1s and spaces as
   * 0s for comparisons.
   *
   * @param String the actual row, providing the vals for each column
   * @param int the current row that we will be storing
   */
  private static void addRow(String columns, int row) {
    for (int i = 0; i < columns.length(); i++) {
      if (Character.toString(columns.charAt(i)).equals(".")) {
        maze[row][i] = 0;
      } else {
        maze[row][i] = 1;
      }
    }
  }

  /*
   * Method to print our 2D array to make sure it's created correctly.
   *
   * @param int size of the maze
   */
  public static void printMaze() {
    for (int i = 0; i < maze_size; i++) {
      String line = "";

      for (int j = 0; j < maze_size; j++) {
        if (maze[i][j] == 0) {
          line += ".";
        } else {
          line += "#";
        }
      }

      System.out.println(line);
    }
  }

  public static int getSize() {
    return maze_size;
  }

  public static int[] getStart() {
    return start;
  }

  public static int[] getFinish() {
    return finish;
  }

  public static Boolean isSpaceEmpty(int x, int y) {
    if (x < 0 || y < 0) {
      return false;
    } else if ( x >= maze_size || y >= maze_size) {
      return false;
    }

    //System.out.println("space at " + x + ", " + y + " is " + maze[x][y]);

    if (maze[x][y] == 0) {
      return true;
    }

    return false;
  }
}
