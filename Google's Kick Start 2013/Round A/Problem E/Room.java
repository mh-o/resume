public class Room {
  int id;
  String color;

  Room(int val, String str) {
    id = (val + 1);
    color = str;
  }

  public int getID() {
    return id;
  }

  public String getColor() {
    return color;
  }
}
