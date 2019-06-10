import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
* This class represents the center (shared) cards and has the functionality to
* add cards if there is room, as well as return each card object for calculation
* of hand scores
*/
public class FlopTurnRiver {
  private static final String IMG_PATH = "src/main/resources/cards_pngs/"; // The folder where all of our image files exist
  private static Card card_1, card_2, card_3, card_4, card_5;
  private static int numCards;

  /**
  * This method constructs a FlopTurnRiver obejct, and by default sets the first
  * three card objects
  * @param Card object the first card (should be the_deck.draw())
  * @param Card object the second card (should be the_deck.draw())
  * @param Card object the third card (should be the_deck.draw())
  */
  public FlopTurnRiver(Card one, Card two, Card three) {
    this.setCard_1(one);
    this.setCard_2(two);
    this.setCard_3(three);
    numCards = 3;
  }

  /**
  * Set method for the first card
  * @param Card object
  */
  public void setCard_1(Card one) {
    card_1 = one;
  }

  /**
  * Get method for the first card
  * @return Card object
  */
  public Card getCard_1() {
    return card_1;
  }

  /**
  * Set method for the second card
  * @param Card object
  */
  public void setCard_2(Card two) {
    card_2 = two;
  }

  /**
  * Get method for the second card
  * @return Card object
  */
  public Card getCard_2() {
    return card_2;
  }

  /**
  * Set method for the third card
  * @param Card object
  */
  public void setCard_3(Card three) {
    card_3 = three;
  }

  /**
  * Get method for the third card
  * @return Card object
  */
  public Card getCard_3() {
    return card_3;
  }

  /**
  * Set method for the fourth card
  * @param Card object
  */
  public void setCard_4(Card four) {
    card_4 = four;
  }

  /**
  * Get method for the fourth card
  * @return Card object
  */
  public Card getCard_4() {
    return card_4;
  }

  /**
  * Set method for the fifth card
  * @param Card object
  */
  public void setCard_5(Card five) {
    card_5 = five;
  }

  /**
  * Get method for the fifth card
  * @return Card object
  */
  public Card getCard_5() {
    return card_5;
  }

  /**
  * This method adds a card to the center (shared) cards if and only if there
  * is room for an additional card (if there are 3 or 4 cards)
  * @param Card object (should be the_deck.draw())
  */
  public void addCard(Card card) {
    if (numCards == 3) { // If there are 3 cards on the table
      setCard_4(card); // Set the new card object as the fourth card
      numCards ++;
    } else if (numCards == 4) { // If there are 4 cards on the table
      setCard_5(card); // Set the new card object as the fifth card
      numCards ++;
    }
  }

  /**
  * Get method for the number of cards
  * @return int number of cards
  */
  public int getNumCards() {
    return numCards;
  }

  /**
  * This method displays all the center cards as a JPanel of card objects, and
  * depending on numCards may display 3, 4, or 5 card objects
  * @return JPanel of the center (shared) cards
  */
  public static JPanel getCardsJPanel() {
    JPanel the_panel = new JPanel(new FlowLayout());

    the_panel.add(showCard(card_1.getIMGPath())); // First three cards are always shown
    the_panel.add(showCard(card_2.getIMGPath())); // First three cards are always shown
    the_panel.add(showCard(card_3.getIMGPath())); // First three cards are always shown

    if (numCards > 3) { // For the Turn and the River
      the_panel.add(showCard(card_4.getIMGPath())); // Show the fourth card
    }

    if (numCards > 4) { // For the River
      the_panel.add(showCard(card_5.getIMGPath())); // Show the fifth card
    }

    the_panel.setBackground(new Color(5, 81, 2)); // Set background color to match poker table

    return the_panel;
  }

  /**
  * This method displays the correct PNG for a card, where suit is given by
  * 'D | H | S | C' and rank is given by '2-9 | J | Q | K | A', for example the
  * ace of spades is represented by the string 'AS'
  * @param card string representation of filename
  * @return JLanel with icon image of card
  * @exception IOException error loading PNG image
  */
  public static JLabel showCard(String card) {
    JLabel label = new JLabel();

    try {
       BufferedImage big_img = ImageIO.read(new File(IMG_PATH + card));
       Image img = big_img.getScaledInstance(72, 99, Image.SCALE_DEFAULT);
       ImageIcon icon = new ImageIcon(img);
       label = new JLabel(icon);
    } catch (IOException e) {
       e.printStackTrace();
    }

    return label;
  }
}
