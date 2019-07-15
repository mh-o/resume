import java.util.*;

public class Exercise {
  String title;
  ArrayList<String> problem, solution;

  public Exercise(String t, ArrayList<String> p, ArrayList<String> s) {
    title = t;
    problem = p;
    solution = s;
  }

  public String getTitle() {
    String temp = "\n";

    for (int i = 0; i < title.length(); i++) {
      temp += "/";
    }

    temp += "\n";
    temp += title;
    temp += "\n";

    for (int i = 0; i < title.length(); i++) {
      temp += "/";
    }

    temp += "\n";

    return temp;
  }

  public ArrayList<String> getProblem() {
    return problem;
  }

  public ArrayList<String> getSolution() {
    return solution;
  }
}
