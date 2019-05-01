public class GBus {
  int lowest_city, highest_city;

  GBus(int a, int b) {
    lowest_city = a;
    highest_city = b;
  }

  public Boolean servesCity(int city) {
    if (city >= lowest_city && city <= highest_city) {
      return true;
    }

    return false;
  }

  public String toString(int i) {
    String str = "GBus #" + (i + 1) + ": " + lowest_city +
                 " -> " + highest_city;

    return str;
  }
}
