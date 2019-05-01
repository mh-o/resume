public class Soldier {
  int p;
  int q;

  Soldier(String str) {
    String[] split = str.split(" ");

    p = Integer.parseInt(split[0]);
    q = Integer.parseInt(split[1]);
  }

  public int getP() {
    return p;
  }

  public int getQ() {
    return q;
  }
}
