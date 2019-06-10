/**
* This class represents a single card object, each card has an associated
* image path, suit, and value
*/
public class Card {
  String IMG_PATH; // Path of the card's image
  int suit; // Integer representing suit (1-4)
  int value; // Integer represeting value (2-14)
  boolean flipped, folded; // Is the card face up or face down

  /**
  * This method cosntructs a card object
  * @param int card value (1-13)
  * @param int card suit (1-4)
  */
  public Card(int num1, int num2) {
    this.setValue(num1); // Initialize value
    this.setSuit(num2); // Initialize suit

    String path = pathToString(num1, num2);
    this.setIMGPath(path);

    flipped = true; // By default, a card is face down
  }

  /**
  * This method returns a string for the filepath of a card PNG
  * @param int the value of the card
  * @param int the suit of the card
  */
  public String pathToString(int num1, int num2) {
    String valString = getValString(num1);
    String suitString = getSuitString(num2);

    String path = valString + suitString + ".png";

    return path;
  }

  /**
  * This method translates an integer key to its corresponding face card
  * character if it is not a numbered card
  * @param int (2-14)
  * @return String (2-10 | J | Q | K | A)
  */
  public String getValString(int val) {
    String temp = "";

    if (val < 11) {
      temp = Integer.toString(val);
    } else {
      switch (val) {
        case 11: temp = "J";
                 break;
        case 12: temp = "Q";
                 break;
        case 13: temp = "K";
                 break;
        case 14: temp = "A";
                 break;
      }
    }

    return temp;
  }

  /**
  * This method translates an integer key to its corresponding character
  * representing the suit of a card
  * @param int (1 | 2 | 3 | 4)
  * @return String (C | D | H | S)
  */
  public String getSuitString(int val) {
    String temp = "";

    switch (val) {
      case 1: temp = "C";
              break;
      case 2: temp = "D";
              break;
      case 3: temp = "H";
              break;
      case 4: temp = "S";
              break;
    }

    return temp;
  }

  /**
  * Set method for a card's suit
  * @param int (1 | 2 | 3 | 4) for (club | diamond | heart | spade)
  */
  public void setSuit(int val) {
    suit = val;
  }

  /**
  * Get method for a card's suit
  * @return int (1 | 2 | 3 | 4) for (club | diamond | heart | spade)
  */
  public int getSuit() {
    return suit;
  }

  /**
  * Set method for a card's value
  * @param int (2-14) for (2-ACE)
  */
  public void setValue(int val) {
    value = val;
  }

  /**
  * Get method for a card's value
  * @return int (2-14) for (2-ACE)
  */
  public int getValue() {
    return value;
  }

  /**
  * Set method for a card's image path, the ace of spades will have image path
  * 'AS.png'
  * @param String path
  */
  public void setIMGPath(String path) {
    IMG_PATH = path;
  }

  /**
  * Get method for a card's image path, the ace of spades will have image path
  * 'AS.png'
  * @return String path
  */
  public String getIMGPath() {
    if (folded) {
      return "fold.png";
    } else if (flipped) {
      return "FD.png";
    } else {
      return IMG_PATH;
    }
  }

  public String toString() {
    String temp = IMG_PATH;
    String one = Character.toString(temp.charAt(0));
    String two = Character.toString(temp.charAt(1));

    if (two.equals("0")) {
      String three = Character.toString(temp.charAt(2));
      temp = one + two + three;
    } else {
      temp = one + two;
    }

    return temp;
  }

  public void flip() {
    if (flipped) {
      flipped = false;
    } else {
      flipped = true;
    }
  }

  public void fold() {
    folded = true;
  }
}
