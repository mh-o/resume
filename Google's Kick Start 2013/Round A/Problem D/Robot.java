import java.util.*;
import java.io.*;

public class Robot {
  static int[] location;
  static char facing = 'N';

  Robot(int[] val) {
    location = val;
  }

  public static int[] getLocation() {
    return location;
  }

  public static void setLocation(int x, int y) {
    int old_x = location[0];
    int old_y = location[1];

    location[0] = (old_x + x);
    location[1] = (old_y + y);
  }

  public static char isFacing() {
    return facing;
  }

  public static void turnRight() {
    switch (facing) {
      case 'N': facing = 'E'; break;
      case 'E': facing = 'S'; break;
      case 'S': facing = 'W'; break;
      case 'W': facing = 'N'; break;
    }
  }

  public static void turnLeft() {
    switch (facing) {
      case 'N': facing = 'W'; break;
      case 'E': facing = 'N'; break;
      case 'S': facing = 'E'; break;
      case 'W': facing = 'S'; break;
    }
  }

  public static void turnAround() {
    switch (facing) {
      case 'N': facing = 'S'; break;
      case 'E': facing = 'W'; break;
      case 'S': facing = 'N'; break;
      case 'W': facing = 'E'; break;
    }
  }

  public static String moveForward() {
    switch (facing) {
      case 'N': setLocation(-1, 0); return "N";
      case 'E': setLocation(0, 1); return "E";
      case 'S': setLocation(1, 0); return "S";
      case 'W': setLocation(0, -1); return "W";
    }
    return "";
  }
}
