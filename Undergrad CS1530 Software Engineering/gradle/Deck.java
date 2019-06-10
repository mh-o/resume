import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
* This class represents the deck as a stack (via ArrayList) of card objects,
* which can be shuffled and drawn from (top card)
*/
public class Deck {
  private ArrayList<Card> theDeck = new ArrayList<Card>(); // Stack for the cards

  /**
  * This method constructs a deck object of 52 card objects
  */
  public Deck() {
    for(int i = 2; i < 15; i ++) { // For cards 2, 3, 4... ACE
      for(int j = 1; j < 5; j ++) { // For suits club, diamond, heart, spade
        Card newCard = new Card(i, j); // Create new card object (value, suit)
        theDeck.add(newCard); // Add card object to the deck
      }
    }
  }

  /**
  * This method shuffles the deck ArrayList, using some constant seed for
  * testing an reproducibility, or current system time for more true random
  * card dealing
  * @param int seed
  */
  public void shuffle(int seed) {
    Collections.shuffle(theDeck, new Random(seed));
  }

  /**
  * This method pops a card object from the stack (ArrayList)
  * @return Card object
  */
  public Card draw() {
    return theDeck.remove(0);
  }

  /**
  * Helper method than will print every card in the deck to double check that
  * the deck was constructed/shuffled correctly
  */
  public void printDeck() {
    for (int i = 0; i < theDeck.size(); i++) {
      System.out.println(theDeck.get(i).getIMGPath());
    }
  }
}