import java.util.*;

/*
 * Class for a member object, which stores the members own name as well as a
 * list of enemies (or nodes) that the member should not be in a gruop (graph)
 * with.
 */
public class Member {
    private String name;
    private ArrayList<String> enemies;

    /*
     * Constructor, sets the name of the new member and initializes an empty
     * list of enemies.
     */
    Member(String str) {
      name = str;
      enemies = new ArrayList<String>();
    }

    /*
     * Method to add an enemy to this member's list of enemies - if that enemy
     * is not already listed.
     */
    public void addEnemy(String str) {
      if (!searchEnemies(str)) {
        enemies.add(str);
      }
    }

    /*
     * Method to search through the list of enemies and see if one exists.
     */
    public Boolean searchEnemies(String str) {
      for (int i = 0; i < enemies.size(); i++) {
        if (enemies.get(i).equals(str)) {
          return true;
        }
      }

      return false;
    }

    /*
     * Method to return this member's name as a string.
     */
    public String getName() {
      return name;
    }

    /*
     * Method to return an array of strings with the name of each enemy for this
     * member.
     */
    public String[] getEnemies() {
      String[] names = new String[enemies.size()];

      for (int i = 0; i < enemies.size(); i++) {
        names[i] = enemies.get(i);
      }

      return names;
    }
}
