public class Turbolift {
  int a, b, t;

  Turbolift(String str) {
    String[] split = str.split(" ");
    
    a = Integer.parseInt(split[0]);
    b = Integer.parseInt(split[1]);
    t = Integer.parseInt(split[2]);
  }

  public int getA() {
    return a;
  }

  public int getB() {
    return b;
  }

  public int getT() {
    return t;
  }
}
