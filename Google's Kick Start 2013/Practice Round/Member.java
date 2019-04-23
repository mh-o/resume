import java.util.*;

public class Member {
    private String name;
    private ArrayList<String> enemies;

    Member(String str) {
      name = str;
      enemies = new ArrayList<String>();
    }

    public void addEnemy(String str) {
      if (!searchEnemies(str)) {
        enemies.add(str);
      }
    }

    public Boolean searchEnemies(String str) {
      for (int i = 0; i < enemies.size(); i++) {
        if (enemies.get(i).equals(str)) {
          return false;
        }
      }

      return true;
    }

    public String getName() {
      return name;
    }
}
