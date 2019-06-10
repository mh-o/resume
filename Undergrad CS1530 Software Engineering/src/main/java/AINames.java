import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Random;

/**
* This class simply generates an ArrayList of random unique names for the AI
* players, using first_names.txt and last_names.txt
*/
public class AINames {
  private ArrayList<String> names = new ArrayList<String>();
  private Random r;

  /**
  * This method constructs an AINames object, a list of 7 random names
  */
  public AINames() {
    int seed = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    r = new Random(seed);

    for (int i = 0; i < 7; i++) {
      buildName();
    }
  }

  /**
  * This method generates a random name by selecting a first and last name from
  * first_names.txt and last_names.txt respectively
  */
  public void buildName() {
    int first = getRandomIndexes(40); // Generate random int from 0-39
    int last = getRandomIndexes(20); // Generate random int from 0-19

    String first_name = findName(first, "first_names.txt"); // Select random first name from 40 choices in first_names.txt
    String last_name = findName(last, "last_names.txt"); // Select random last name from 20 choices in last_names.txt

    String full_name = first_name + " " + last_name; // Concatinate first and last name
    names.add(full_name); // Add to names ArrayList
  }

  /**
  * This method simply generates a random integer between zero and
  * @param int some value
  * @return int between 0 and some value
  */
  public int getRandomIndexes(int val) {
    int temp = r.nextInt(val);
    return temp;
  }

  /**
  * This method gets the nth line of a file
  * @param int the line number of the file to read
  * @param String the path of the file
  * @return String that line of the file
  */
  public String findName(int val, String filename) {
    int count = 0;

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;

      while ((line = br.readLine()) != null) {
        if (count == val) {
          return line;
        } else {
          count ++;
        }
      }
    } catch (IOException e) {
      System.out.println("Could not read " + filename);
    }

    return "ERROR";
  }

  /**
  * This method pops a name from the names ArrayList
  * @return String full name
  */
  public String getName() {
    return names.remove(0);
  }
}
