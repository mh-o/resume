import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Insets;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import javax.imageio.ImageIO;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

/**
* This class contains all current functionality of the game
* TODO seperate files should be made for deck/player/game etc.
*/
public class TexasHoldem {
  private static final String IMG_PATH = "src/main/resources/cards_pngs/"; // The folder where all of our image files exist

  private static JFrame jf;
  private static JPanel center_cards, pot, table, flop_cards; // Panels for the shared (center) cards

  private static GridBagConstraints gbc = new GridBagConstraints(); // Grid constraints for poker table
  private static GridBagConstraints p_gbc = new GridBagConstraints(); // Grid constraints for players

  private static Deck the_deck; // Deck object, an ArrayList of 52 card objects
  private static AINames names; // AI names object, an ArrayList of 7 randomly generated names
  private static Pot the_pot; // No, not the Tool song
  private static JPanel[] player_panels;
  private static Player player_1, player_2, player_3, player_4, player_5, player_6, player_7, player_8; // Player objects

  private static JLabel pot_label;

  private static ArrayList<Player> players = new ArrayList<Player>();

  private static FlopTurnRiver flop_fd;
  private static PlayPoker the_game;

  private static boolean isEnabled;
  private static JButton fold_button, call_button, raise_button;
  private static TheActionListener listener;

  private static ArrayList<JLabel> action_labels = new ArrayList<JLabel>();
  private static ArrayList<JLabel> stack_labels = new ArrayList<JLabel>();
  private static ArrayList<JLabel> bet_labels = new ArrayList<JLabel>();

  private static int call = 20;

  /**
  * This method cconstructs the poker table for the game using the layout:
  *
  *    0   1   2   3   4   5
  *  0 _ | _ | _ | _ | _ | X
  *  1 _ | _ | _ | _ | _ | X
  *  2 _ | _ | _ | _ | _ | X
  *  3 _ | _ | _ | _ | _ | X
  *  4   |   |   |   | _ | X
  *  5 X | X | X | X | X |
  */
  public TexasHoldem() {
    player_panels = new JPanel[8];

    table.setLayout(new GridBagLayout());
    table.setBackground(new Color(5, 81, 2)); // Dark green
    gbc.insets = new Insets(5, 5, 0, 0); // Spacing

    the_deck = new Deck(); // Create new deck with 52 card objects
    int seed = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    the_deck.shuffle(seed); // Shuffle the deck with some seed

    String player_name = promptName(); // Prompt for user name
    int num_players = promptNumPlayers(); // Prompt for number of players

    names = new AINames();

    buildPlayers(player_name); // Set up 8 player obejcts

    for (int i = 0; i < 5; i++) {
      JPanel temp = padPlayer(player_1);
      gbc.gridx = i;
      gbc.gridy = 5;
      table.add(temp, gbc);

      JPanel temp2 = padPlayer(player_1);
      gbc.gridx = 5;
      gbc.gridy = i;
      table.add(temp2, gbc);
    }

    JPanel buttons = buttonPanel();
    gbc.gridx = 2;
    gbc.gridy = 3;
    table.add(buttons, gbc);

    // Set up player positions on the grid depending on number of players
    switch (num_players) {
      case 1: player_1.setXPos(2);
              player_1.setYPos(4);
              players.add(player_1);

              player_2.setXPos(2);
              player_2.setYPos(0);
              players.add(player_2);
              break;

      case 2: player_1.setXPos(2);
              player_1.setYPos(4);
              players.add(player_1);

              player_2.setXPos(0);
              player_2.setYPos(1);
              players.add(player_2);

              player_3.setXPos(4);
              player_3.setYPos(1);
              players.add(player_3);
              break;

      case 3: player_1.setXPos(2);
              player_1.setYPos(4);
              players.add(player_1);

              player_2.setXPos(0);
              player_2.setYPos(2);
              players.add(player_2);

              player_3.setXPos(2);
              player_3.setYPos(0);
              players.add(player_3);

              player_4.setXPos(4);
              player_4.setYPos(2);
              players.add(player_4);
              break;

      case 4: player_1.setXPos(2);
              player_1.setYPos(4);
              players.add(player_1);

              player_2.setXPos(0);
              player_2.setYPos(2);
              players.add(player_2);

              player_3.setXPos(1);
              player_3.setYPos(0);
              players.add(player_3);

              player_4.setXPos(3);
              player_4.setYPos(0);
              players.add(player_4);

              player_5.setXPos(4);
              player_5.setYPos(2);
              players.add(player_5);
              break;

      case 5: player_1.setXPos(2);
              player_1.setYPos(4);
              players.add(player_1);

              player_2.setXPos(0);
              player_2.setYPos(3);
              players.add(player_2);

              player_3.setXPos(0);
              player_3.setYPos(1);
              players.add(player_3);

              player_4.setXPos(2);
              player_4.setYPos(0);
              players.add(player_4);

              player_5.setXPos(4);
              player_5.setYPos(1);
              players.add(player_5);

              player_6.setXPos(4);
              player_6.setYPos(3);
              players.add(player_6);
              break;

      case 6: player_1.setXPos(2);
              player_1.setYPos(4);
              players.add(player_1);

              player_2.setXPos(0);
              player_2.setYPos(3);
              players.add(player_2);

              player_3.setXPos(0);
              player_3.setYPos(1);
              players.add(player_3);

              player_4.setXPos(1);
              player_4.setYPos(0);
              players.add(player_4);

              player_5.setXPos(3);
              player_5.setYPos(0);
              players.add(player_5);

              player_6.setXPos(4);
              player_6.setYPos(1);
              players.add(player_6);

              player_7.setXPos(4);
              player_7.setYPos(3);
              players.add(player_7);
              break;

      case 7: player_1.setXPos(1);
              player_1.setYPos(4);
              players.add(player_1);

              player_2.setXPos(0);
              player_2.setYPos(3);
              players.add(player_2);

              player_3.setXPos(0);
              player_3.setYPos(1);
              players.add(player_3);

              player_4.setXPos(1);
              player_4.setYPos(0);
              players.add(player_4);

              player_5.setXPos(3);
              player_5.setYPos(0);
              players.add(player_5);

              player_6.setXPos(4);
              player_6.setYPos(1);
              players.add(player_6);

              player_7.setXPos(4);
              player_7.setYPos(3);
              players.add(player_7);

              player_8.setXPos(3);
              player_8.setYPos(4);
              players.add(player_8);
              break;
    }

    // Display players depending on number of players
    for (int i = 0; i < players.size(); i++) { // For ecah player in players ArrayList
      player_panels[i] = showPlayer(players.get(i)); // Create JPanel for the player
      gbc.gridx = players.get(i).getXPos(); // Get x position (set in switch case)
      gbc.gridy = players.get(i).getYPos(); // Get y position (set in switch case)
      table.add(player_panels[i], gbc); // Display the player
    }

    Card c_1 = the_deck.draw();
    Card c_2 = the_deck.draw();
    Card c_3 = the_deck.draw();

    flop_fd = new FlopTurnRiver(c_1, c_2, c_3);

    center_cards = flop_fd.getCardsJPanel();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 3; // Span horizontally, starting at grid position (x, y)
    table.add(center_cards, gbc);
    gbc.gridwidth = 1;

    the_pot = new Pot();
    pot = the_pot.getPotJPanel();
    pot_label = new JLabel("$0");
    pot_label.setForeground(Color.white);
    gbc.gridx = 2;
    gbc.gridy = 1;
    table.add(pot_label, gbc);
  }

  /**
  * This method prompts the user to enter their name
  * @return String representation of the user's nickname
  */
  public static String promptName() {
    String temp = JOptionPane.showInputDialog("Please enter your name: ");
    return temp;
  }

  public static void setPot(int pot) {
    String temp = "$";
    temp += Integer.toString(pot);
    pot_label.setText(temp);
  }

  public static void setBlinds(int big, int small) {
    action_labels.get(big).setText("BIG BLIND");
    players.get(big).setBet(20);
    bet_labels.get(big).setText(players.get(big).getBet());
    players.get(big).subFromStack(20);
    stack_labels.get(big).setText(Integer.toString(players.get(big).getStack()));

    action_labels.get(small).setText("SMALL BLIND");
    players.get(small).setBet(10);
    bet_labels.get(small).setText(players.get(small).getBet());
    players.get(small).subFromStack(10);
    stack_labels.get(small).setText(Integer.toString(players.get(small).getStack()));
  }

  /**
  * This method prompts the user to enter the number of opponents
  * @return int value for number of players (1-7)
  */
  public static int promptNumPlayers() {
    int val = 1;
    String temp = JOptionPane.showInputDialog("Please enter the number of opponents (1-7): ");

    try {
      val = Integer.parseInt(temp);
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(null, "Value must be an integer!");
      val = promptNumPlayers();
    }

    if (val < 1) {
      JOptionPane.showMessageDialog(null, "It's dangerous to go alone! You must have at least one opponent.");
      val = promptNumPlayers();
    } else if (val > 7) {
      JOptionPane.showMessageDialog(null, "You can't have more than 7 opponents!");
      val = promptNumPlayers();
    }
    return val;
  }

  /**
  * This method initializes 8 player objects by default, note that player
  * objects are only displayed/added to the players ArrayList if the user has
  * specified that quantity of opponents
  * @param String user nickname
  */
  public static void buildPlayers(String name) {
    player_1 = new Player(name, the_deck.draw(), the_deck.draw(), 1000);
    player_1.getCard_1().flip(); // Show the player's first card
    player_1.getCard_2().flip(); // Show the player's second card

    player_2 = new Player(names.getName(), the_deck.draw(), the_deck.draw(), 1000);
    player_3 = new Player(names.getName(), the_deck.draw(), the_deck.draw(), 1000);
    player_4 = new Player(names.getName(), the_deck.draw(), the_deck.draw(), 1000);
    player_5 = new Player(names.getName(), the_deck.draw(), the_deck.draw(), 1000);
    player_6 = new Player(names.getName(), the_deck.draw(), the_deck.draw(), 1000);
    player_7 = new Player(names.getName(), the_deck.draw(), the_deck.draw(), 1000);
    player_8 = new Player(names.getName(), the_deck.draw(), the_deck.draw(), 1000);
  }

  /**
  * This class handles the user input, specifically the fold call and raise
  * buttons
  */
  private static class TheActionListener implements ActionListener {
    /**
    * Handle button click events on the various buttons
    * @param ActionEvent event button click
    */
    public void actionPerformed(ActionEvent event) {
      if (event.getSource() == fold_button) {
        players.get(0).setAction("FOLD"); // Display FOLD
        players.get(0).getCard_1().fold(); // Set image path to folded card
        players.get(0).getCard_2().fold(); // Set image path to folded card

        Pot.addToPot(players.get(0).getBetInt());
        players.get(0).setBet(0);
        setPot(Pot.getPot());

        refreshPlayer(0, showPlayer(players.get(0)));
        Logger.logAction(0, "folded"); // Log that player 0 (user) has folded
        PlayPoker.toggleFlag(); // Stop waiting for user input
      } else if (event.getSource() == call_button) {
        players.get(0).setAction("CALL"); // Dispaly CALL
        players.get(0).setBet(call);
        players.get(0).subFromStack(call);
        refreshPlayer(0, showPlayer(players.get(0)));
        Logger.logAction(0, "called"); // Log that player 0 (user) has called
        PlayPoker.toggleFlag(); // Stop waiting for user input
      } else if (event.getSource() == raise_button) {
        action_labels.get(0).setText("RAISE");
        PlayPoker.toggleFlag();
      }
    }
  }

  public static JPanel buttonPanel() {
    JPanel the_panel = new JPanel(new GridBagLayout());
    fold_button = new JButton("FOLD");
    call_button = new JButton("CALL");
    raise_button = new JButton("RAISE");

    fold_button.setEnabled(false);
    call_button.setEnabled(false);
    raise_button.setEnabled(false);
    isEnabled = false;

    listener = new TheActionListener();
    fold_button.addActionListener(listener);
    call_button.addActionListener(listener);
    raise_button.addActionListener(listener);

    the_panel.setBackground(new Color(5, 81, 2));

    gbc.gridx = 0;
    gbc.gridy = 0;
    the_panel.add(fold_button, gbc);

    gbc.gridx = 1;
    the_panel.add(call_button, gbc);

    gbc.gridx = 2;
    the_panel.add(raise_button, gbc);

    return the_panel;
  }

  public static void toggleButtons() {
    if (isEnabled == false) {
      fold_button.setEnabled(true);
      call_button.setEnabled(true);
      raise_button.setEnabled(true);
      isEnabled = true;
    } else {
      fold_button.setEnabled(false);
      call_button.setEnabled(false);
      raise_button.setEnabled(false);
      isEnabled = false;
    }
  }

  /**
  * This method displays the associated player object information, will generate
  * a bordered panel with player name, stack, and two cards
  * @param Player the player object
  * @return JPanel representing the player
  */
  public static JPanel showPlayer(Player player) {
    JPanel the_panel = new JPanel(new GridBagLayout()); // The housing for a player's title/stack/hand
    JPanel p_info = new JPanel(new GridLayout(3, 1)); // The housing for a player's info
    JPanel the_hand = new JPanel(new GridLayout(1, 2)); // The housing for a player's cards
    JPanel money = new JPanel(new GridLayout(1, 2));

    JLabel p_action = new JLabel(player.getAction());
    p_action.setForeground(Color.yellow);
    action_labels.add(p_action);

    JLabel p_bet = new JLabel(player.getBet());
    p_bet.setForeground(Color.green);
    bet_labels.add(p_bet);

    JLabel p_label = new JLabel(player.getName()); // Player's title (name)
    p_label.setForeground(Color.white); // Make text visible on dark green background

    JLabel the_stack = new JLabel(Integer.toString(player.getStack())); // String representation of player's stack
    the_stack.setForeground(Color.white); // Make text visible on dark green background
    stack_labels.add(the_stack);

    p_gbc.insets = new Insets(5, 5, 5, 5); // Add spacing
    p_gbc.gridx = 0;
    p_gbc.gridy = 0;

    money.setBackground(new Color(5, 81, 2));
    money.add(the_stack);
    money.add(p_bet);

    p_info.add(p_label);
    p_info.add(p_action);
    p_info.add(money);
    p_info.setBackground(new Color(5, 81, 2));
    the_panel.add(p_info, p_gbc);

    p_gbc.gridx = 0; // Redundant but helps understand position
    p_gbc.gridy = 1;

    the_hand.add(showCard(player.getCard_1().getIMGPath()), p_gbc); // Show the player's first card
    the_hand.add(showCard(player.getCard_2().getIMGPath()), p_gbc); // Show the player's second card
    the_hand.setBackground(new Color(5, 81, 2));
    the_panel.add(the_hand, p_gbc);

    the_panel.setBorder(BorderFactory.createLineBorder(Color.black));
    the_panel.setBackground(new Color(5, 81, 2));

    return the_panel;
  }

  public static int getRaise() {
    Random rand = new Random();
    int raise = rand.nextInt((15-1) + 1) + 1;

    int temp = getCall();
    raise = temp + raise;

    return raise;
  }

  /**
  * This method displays an invisible player object to pad for small numbers of
  * players in the game
  * @param Player the player object
  * @return JPanel that is essentially just a green box
  */
  public static JPanel padPlayer(Player player) {
    JPanel the_panel = new JPanel(new GridBagLayout());
    JPanel p_info = new JPanel(new GridLayout(2, 1));
    JPanel the_hand = new JPanel(new GridLayout(1, 2));

    JLabel p_label = new JLabel(player.getName());
    p_label.setForeground(new Color(5, 81, 2));

    JLabel the_stack = new JLabel(Integer.toString(player.getStack()));
    the_stack.setForeground(new Color(5, 81, 2));

    p_gbc.insets = new Insets(5, 5, 5, 5);
    p_gbc.gridx = 0;
    p_gbc.gridy = 0;

    p_info.add(p_label);
    p_info.add(the_stack);
    p_info.setBackground(new Color(5, 81, 2));
    the_panel.add(p_info, p_gbc);

    p_gbc.gridx = 0;
    p_gbc.gridy = 1;

    the_hand.add(showCard("blank.png"), p_gbc);
    the_hand.add(showCard("blank.png"), p_gbc);
    the_hand.setBackground(new Color(5, 81, 2));
    the_panel.add(the_hand, p_gbc);

    the_panel.setBorder(BorderFactory.createLineBorder(new Color(5, 81, 2)));
    the_panel.setBackground(new Color(5, 81, 2));

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

  /**
  * Method to access the deck of card objects
  * @return the_deck
  */
  public static Deck getDeck() {
    return the_deck;
  }

  /**
  * Method to access the center cards object
  * @return flop_fd FlopTurnRiver object
  */
  public static FlopTurnRiver getFTR() {
    return flop_fd;
  }

  /**
  * Method to access the table, the main JPanel which spans the JFrame
  * @return JPanel table
  */
  public static JPanel getTable() {
    return table;
  }

  /**
  * Method to access the panel for each player given some index
  * @param int index
  * @return JPanel representing a player
  */
  public static JPanel getPlayerPanel(int i) {
    return player_panels[i];
  }

  /**
  * Method to access the main JFrame
  * @return jf the frame containing all JPanels
  */
  public static JFrame getFrame() {
    return jf;
  }

  /**
  * Method to access the ArrayList of players
  * @return players ArrayList
  */
  public static ArrayList<Player> getPlayers() {
    return players;
  }

  /**
  * Method to update the player panel depending on changes
  * @param int index (which player?)
  * @param JPanel the new panel to set for the player
  */
  public static void updatePlayer(int i, JPanel the_panel) {
    player_panels[i-1] = the_panel;

  }

  public static int getCall() {
    return call;
  }

  public static void setCall(int val) {
    call = val;
  }

  public static void refreshPot(JPanel the_panel) {
    table.remove(pot);

    gbc.gridx = 2;
    gbc.gridy = 1;
    table.add(pot, gbc);

    pot.revalidate();
    pot.repaint();
    update();
  }

  public static void refreshPlayer(int i, JPanel the_panel) {
    table.remove(player_panels[i]);
    player_panels[i] = the_panel;

    gbc.gridx = players.get(i).getXPos();
    gbc.gridy = players.get(i).getYPos();
    table.add(player_panels[i], gbc);

    player_panels[i].revalidate();
    player_panels[i].repaint();
  }

  /**
  * Method to update the center cards depending on the stage of the game
  * @param JPanel new JPanel representing the center cards
  */
  public static void updateCenterCards(JPanel the_panel) {
    table.remove(center_cards); // Remove the old center cards
    center_cards = the_panel; // Set the new JPanel for the center cards

    gbc.gridx = 1; // Set x position
    gbc.gridy = 2; // Set y position
    gbc.gridwidth = 3; // Span the center cards across 3 "boxes"
    table.add(center_cards, gbc);

    center_cards.revalidate(); // Update the center cards
    center_cards.repaint(); // Display the center cards
    gbc.gridwidth = 1; // Make sure we set width back to default (for players)
  }

  /**
  * Method to update changes made to the deck from PlayPoker.java
  * @param deck the new deck with one popped card
  */
  public static void updateDeck(Deck deck) {
    the_deck = deck;
  }

  /**
  * Method to refresh the frame and make sure the poker table is updated
  */
  public static void update() {
    jf.add(getTable());
    jf.revalidate();
    jf.repaint();
  }

  /**
  * Main function that begins the game and calls the constructor
  * @param args String[] unused
  */
  public static void main(String[] args) {
    table = new JPanel();

    TexasHoldem t = new TexasHoldem(); // Create the poker table
    jf = new JFrame(); // JFrame to hold the poker table JPanel

    jf.setTitle("Texas Holdem"); // Window title
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Let the user exit
    jf.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
    jf.setVisible(true); // Invisible game would be tough

    update(); // Paint our constructed changes to the frame

    PlayPoker the_game = new PlayPoker();

    the_game.play();
  }
}
