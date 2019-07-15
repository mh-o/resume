import java.util.*;
import java.io.*;

public class Cracking {
  static ArrayList<Exercise> exercises;
  static ArrayList<String> lines;
  static final String filepath = "exercises.txt";
  static Scanner in;

  /*
   * Main program. Generate the exercises provided in the filepath and traverse
   * through them in some random order.
   */
  public static void main(String[] args) {
    fileToList();
    generateExercises();
    run();
  }

  public static void run() {
    in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    String input = "";

    while (!input.equals("q")) {
      randomExercise();
      System.out.println("Type 'q' and press ENTER to quit.");
      System.out.println("Press ENTER for a new problem...");
      input = in.nextLine();
    }
  }

  public static void randomExercise() {
    Random rand = new Random();
    Exercise ex = exercises.get(rand.nextInt(exercises.size()));

    String title = ex.getTitle();
    ArrayList<String> problem = ex.getProblem();
    ArrayList<String> solution = ex.getSolution();

    System.out.println(title);
    System.out.println(listToString(problem));

    System.out.println("Press ENTER to view the solution...");
    in.nextLine();
    System.out.println(listToString(solution));
  }

  /*
   * Method
   */
  public static void generateExercises() {
    String title;
    int i = 0;

    while (i < lines.size()) {
      title = "";
      ArrayList<String> problem = new ArrayList<String>();
      ArrayList<String> solution = new ArrayList<String>();
      ArrayList<String> shellFile = new ArrayList<String>();
      // Setting the title for constructor.
      title = lines.get(i);
      i+=2;

      // Setting the problem for constructor.
      while (!lines.get(i).isEmpty()) {
        problem.add(lines.get(i));
        i++;
      }
      i++;

      while (!lines.get(i).equals("###")) {
        shellFile.add(lines.get(i));
        i++;
      }
      i+=2;

      // Setting the solution for constructor.
      while (!lines.get(i).isEmpty()) {
        solution.add(lines.get(i));
        i++;

        if (i == lines.size()) {
          break;
        }
      }

      exercises.add(new Exercise(title, problem, solution, shellFile));
      i++;
    }
  }

  /*
   * Method to store all lines from a text file of exercises into an ArrayList
   * for manipulation into Exercise objects.
   */
  public static void fileToList() {
    exercises = new ArrayList<Exercise>();
    lines = new ArrayList<String>();

    try {
      FileInputStream fis = new FileInputStream(filepath);
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));

      String line;

      while ((line = br.readLine()) != null) {
        lines.add(line);
      }
    } catch (IOException e) {
      System.out.println("Unable to open '" + filepath + "'!");
    }
  }

  public static String listToString(ArrayList<String> list) {
    StringBuilder sb = new StringBuilder();

    for (String s : list) {
      sb.append(s);
      sb.append("\n");
    }

    return sb.toString();
  }
}
