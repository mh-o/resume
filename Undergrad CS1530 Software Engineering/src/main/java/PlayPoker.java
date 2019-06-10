import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
* This class handles the player through and functionality of one round of the
* game
*/
public class PlayPoker {
  private static ArrayList<Player> players;
  private static boolean flop_bool = false;
  private static boolean turn_bool = false;
  private static boolean river_bool = false;
  private static boolean flag;

  /**
  * This method is the actual structure of the game, and will iteratre through
  * all betting stages of a round when called
  */
  public void play() {
    players = TexasHoldem.getPlayers(); // Initialize players
    Logger log = new Logger(); // Initialize logger instance
    HandRank hands = new HandRank(players); // Initialize hand rank instance

    log.preFlop(); // Mark preflop in logger
    TexasHoldem.setBlinds(players.size()-1, players.size()-2); // Assign blinds
    hands.rankHands(); // Rank player hands for pre-flop

    takeTurn(); // Take the 1stturn

    flop(); // Add three center cards
    TexasHoldem.update(); // Refresh the board
    pause();

    takeTurn(); // Take the 2nd turn
    log.flop(); // Mark flop in logger
    hands.rankHands(); // Rank player hands for flop

    turn(); // Add 4th center card
    TexasHoldem.update(); // Refresh the board
    pause();

    takeTurn(); // Take the 3rd turn
    log.turn(); // Mark turn in logger
    hands.rankHands(); // Rank player hands for turn

    river(); // Add 5th center card
    TexasHoldem.update(); // Refresh the board
    pause();

    takeTurn(); // Take the 4th turn
    log.river(); // Mark river in logger
    hands.rankHands(); // Rank player hands for river

    finalTurn();

    log.finish(); // Close logger and send changes to log.txt
  }

  /**
  * Method to check the number of community cards on the table
  * @return boolean flop flag
  */
  public static boolean isFlop() {
    return flop_bool;
  }

  /**
  * Method to check the number of community cards on the table
  * @return boolean turn flag
  */
  public static boolean isTurn() {
    return turn_bool;
  }

  /**
  * Method to check the number of community cards on the table
  * @return boolean river flag
  */
  public static boolean isRiver() {
    return river_bool;
  }

  public void finalTurn() {
    for (int i = 0; i < players.size(); i++) {
      unHighlight();

      if (players.get(i).getAction().equals("FOLD")) {
        continue;
      } else {
        highlight(i);

        if (i != 0) {
          players.get(i).getCard_1().flip();
          players.get(i).getCard_2().flip();
          TexasHoldem.refreshPlayer(i, TexasHoldem.showPlayer(players.get(i)));
        }
      }
      pause();
    }

    findWinner();
  }

  public void findWinner() {
    int winner = 0;
    int max_score = 0;

    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getAction().equals("FOLD")) {
        continue;
      } else {
        if (players.get(i).getHandScore() > max_score) {
          max_score = players.get(i).getHandScore();
          winner = i;
        }
      }
    }

    pause();
    String temp = "The winner is: ";
    temp += players.get(winner).getName();
    JOptionPane.showMessageDialog(null, temp);
    System.out.println("winnder is " + winner);
  }

  public void takeTurn() {
    flag = false;

    for (int i = 0; i < players.size(); i++) {
      unHighlight();

      if (players.get(i).getAction().equals("FOLD")) {
        continue;
      } else {
        highlight(i);

        if (i == 0) { // It's the user's turn
          TexasHoldem.toggleButtons();
          while (flag == false) {
            try {
              Thread.sleep(200);
            } catch(InterruptedException e) {
              // Do nothing
            }
          }
          TexasHoldem.toggleButtons();
        } else { // It's an AI's turn
          String action = AI.getAction(players.get(i).getHandScore());
          players.get(i).setAction(action);
          TexasHoldem.refreshPlayer(i, TexasHoldem.showPlayer(players.get(i)));
          takeAction(i);
        }
      }

      pause();
      TexasHoldem.update();
    }
  }

  /**
  * This method handles the action of the AI players, as determined in AI.java
  * @param int index of the player to take action
  */
  public static void takeAction(int i) {
    if (players.get(i).getAction().equals("FOLD")) { // Fold action
      players.get(i).getCard_1().fold();
      players.get(i).getCard_2().fold();

      Pot.addToPot(players.get(i).getBetInt());
      players.get(i).setBet(0);
      System.out.println("pot is " + Pot.getPot());
      TexasHoldem.setPot(Pot.getPot());

      TexasHoldem.refreshPlayer(i, TexasHoldem.showPlayer(players.get(i)));
      Logger.logAction(i, "folded");
    } else if (players.get(i).getAction().equals("CALL")) { // Call action
      int call = TexasHoldem.getCall();
      players.get(i).setBet(call);
      players.get(i).subFromStack(call - players.get(i).getBetInt());
      TexasHoldem.refreshPlayer(i, TexasHoldem.showPlayer(players.get(i)));
      Logger.logAction(i, "called");
    } else if (players.get(i).getAction().equals("RAISE")) { // Raise action
      int raise = TexasHoldem.getRaise();
      players.get(i).setBet(raise);
      players.get(i).subFromStack(raise - players.get(i).getBetInt());
      TexasHoldem.setCall(raise);
      TexasHoldem.refreshPlayer(i, TexasHoldem.showPlayer(players.get(i)));
      Logger.logAction(i, "raised");
    }
  }

  public static void toggleFlag() {
    flag = true;
  }

  /**
  * This method provides delay for iteration in the AI player moves
  */
  public void pause() {
    long time_0 = System.currentTimeMillis();
    long time_1;
    long run_time = 0;

    while(run_time < 1000) {
      time_1 = System.currentTimeMillis();
      run_time = time_1 - time_0;
    }
  }

  /**
  * Method to flip over the 3 center cards once the flop begins
  */
  public void flop() {
    FlopTurnRiver ftr = TexasHoldem.getFTR();
    ftr.getCard_1().flip();
    ftr.getCard_2().flip();
    ftr.getCard_3().flip();

    JPanel the_panel = ftr.getCardsJPanel();
    TexasHoldem.updateCenterCards(the_panel);

    flop_bool = true;
  }

  /**
  * Method to add a 4th card to the 3 flop cards once the turn begins
  */
  public void turn() {
    Deck deck = TexasHoldem.getDeck();
    FlopTurnRiver ftr = TexasHoldem.getFTR();
    ftr.addCard(deck.draw());
    ftr.getCard_4().flip();

    TexasHoldem.updateDeck(deck);

    JPanel the_panel = ftr.getCardsJPanel();
    TexasHoldem.updateCenterCards(the_panel);

    turn_bool = true;
  }

  /**
  * Method to add a 5th card to the 4 turn cards once the river begins
  */
  public void river() {
    Deck deck = TexasHoldem.getDeck();
    FlopTurnRiver ftr = TexasHoldem.getFTR();
    ftr.addCard(deck.draw());
    ftr.getCard_5().flip();

    TexasHoldem.updateDeck(deck);

    JPanel the_panel = ftr.getCardsJPanel();
    TexasHoldem.updateCenterCards(the_panel);

    river_bool = true;
  }

  /**
  * This method indicates which player is currently taking a turn by changing
  * the border color to white
  */
  public void highlight(int index) {
    JPanel the_panel = TexasHoldem.getPlayerPanel(index);
    the_panel.setBorder(BorderFactory.createLineBorder(Color.white));

    TexasHoldem.updatePlayer((index + 1), the_panel);
  }

  /**
  * This method reverts player borders back to black
  */
  public void unHighlight() {
    for (int i = 0; i < players.size(); i++) {
      JPanel the_panel = TexasHoldem.getPlayerPanel(i);
      the_panel.setBorder(BorderFactory.createLineBorder(Color.black));

      TexasHoldem.updatePlayer((i + 1), the_panel);
    }
  }

  /**
  * This method gets the index of the previous player, if the current player
  * is at the last index of the players ArrayList, it avoids an out of bounds
  * exception
  */
  public int getPrevIndex(int index) {
    if (index == 0) {
      index = players.size() - 1;
    } else {
      index = index - 1;
    }

    return index;
  }
}
