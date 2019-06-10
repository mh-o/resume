import java.util.Random;

/**
* This class represents the mind of the AI player, and contains methods to
* decide how to act in different game states
*/
public class AI {
  /**
  * This method accepts a player's score and decides how to act based on the
  * strength of each hand. Probabilities were based off of:
  * https://www.pokerstrategy.com/strategy/various-poker/texas-holdem-probabilities/
  * however the actual probabilities differ to account for "bold" or "timid" AI
  * players
  * @param int the score for a player's best hand
  * @return String representation of the action to be taken
  */
  public static String getAction(int score) {
    if (PlayPoker.isRiver()) {
      return riverAction(score); // 7 total cards
    } else if (PlayPoker.isTurn()) {
      return turnAction(score); // 6 total cards
    } else if (PlayPoker.isFlop()) {
      return flopAction(score); // 5 total cards
    } else {
      return preFlopAction(score);
    }
  }

  /**
  * Method for probabilities if there are 7 total cards
  * @param int the score for a player's best hand
  * @return String representation of the action to be taken
  */
  public static String riverAction(int score) {
    Random r = new Random();
    int rand = r.nextInt((100 - 0) + 1) + 0; // Generate int from 0 to 100

    if (score > 8000000) { // Straight flush
      return raiseCallOrFold(96, 101, rand); // 95% raise, 5% call, 0% fold
    } else if (score > 7000000) { // Four of a kind
      return raiseCallOrFold(91, 101, rand); // 90% raise, 10% call, 0% fold
    } else if (score > 6000000) { // Full house
      return raiseCallOrFold(81, 100, rand); // 80% raise, 19% call, 1% fold
    } else if (score > 5000000) { // Flush
      return raiseCallOrFold(76, 99, rand); // 75% raise, 33% call, 2% fold
    } else if (score > 4000000) { // Straight
      return raiseCallOrFold(51, 96, rand); // 50% raise, 45% call, 5% fold
    } else if (score > 3000000) { // Three of a kind
      return raiseCallOrFold(36, 91, rand); // 35% raise, 55% call, 10% fold
    } else if (score > 2000000) { // Two pair
      return raiseCallOrFold(31, 81, rand); // 30% raise, 50% call, 20% fold
    } else if (score > 1000000) { // One pair
      return raiseCallOrFold(21, 66, rand); // 20% raise, 45% call, 35% fold
    } else { // High card
      return raiseCallOrFold(11, 41, rand); // 10% raise, 30% call, 60% fold
    }
  }

  /**
  * Method for probabilities if there are 6 total cards
  * @param int the score for a player's best hand
  * @return String representation of the action to be taken
  */
  public static String turnAction(int score) {
    Random r = new Random();
    int rand = r.nextInt((100 - 0) + 1) + 0; // Generate int from 0 to 100

    if (score > 8000000) { // Straight flush
      return raiseCallOrFold(96, 101, rand); // 95% raise, 5% call, 0% fold
    } else if (score > 7000000) { // Four of a kind
      return raiseCallOrFold(91, 101, rand); // 90% raise, 10% call, 0% fold
    } else if (score > 6000000) { // Full house
      return raiseCallOrFold(81, 100, rand); // 80% raise, 19% call, 1% fold
    } else if (score > 5000000) { // Flush
      return raiseCallOrFold(76, 99, rand); // 75% raise, 33% call, 2% fold
    } else if (score > 4000000) { // Straight
      return raiseCallOrFold(51, 96, rand); // 50% raise, 45% call, 5% fold
    } else if (score > 3000000) { // Three of a kind
      return raiseCallOrFold(36, 91, rand); // 35% raise, 55% call, 10% fold
    } else if (score > 2000000) { // Two pair
      return raiseCallOrFold(31, 86, rand); // 30% raise, 55% call, 15% fold
    } else if (score > 1000000) { // One pair
      return raiseCallOrFold(21, 81, rand); // 20% raise, 55% call, 20% fold
    } else { // High card
      return raiseCallOrFold(11, 76, rand); // 10% raise, 50% call, 25% fold
    }
  }

  /**
  * Method for probabilities if there are 5 total cards
  * @param int the score for a player's best hand
  * @return String representation of the action to be taken
  */
  public static String flopAction(int score) {
    Random r = new Random();
    int rand = r.nextInt((100 - 0) + 1) + 0; // Generate int from 0 to 100

    if (score > 8000000) { // Straight flush
      return raiseCallOrFold(96, 101, rand); // 95% raise, 5% call, 0% fold
    } else if (score > 7000000) { // Four of a kind
      return raiseCallOrFold(91, 101, rand); // 90% raise, 10% call, 0% fold
    } else if (score > 6000000) { // Full house
      return raiseCallOrFold(81, 101, rand); // 80% raise, 20% call, 0% fold
    } else if (score > 5000000) { // Flush
      return raiseCallOrFold(76, 100, rand); // 75% raise, 34% call, 1% fold
    } else if (score > 4000000) { // Straight
      return raiseCallOrFold(51, 96, rand); // 50% raise, 45% call, 5% fold
    } else if (score > 3000000) { // Three of a kind
      return raiseCallOrFold(36, 96, rand); // 35% raise, 60% call, 5% fold
    } else if (score > 2000000) { // Two pair
      return raiseCallOrFold(31, 91, rand); // 30% raise, 60% call, 10% fold
    } else if (score > 1000000) { // One pair
      return raiseCallOrFold(21, 86, rand); // 20% raise, 65% call, 15% fold
    } else { // High card
      return raiseCallOrFold(11, 81, rand); // 10% raise, 70% call, 20% fold
    }
  }

  /**
  * Method for probabilities if there are 2 total cards
  * @param int the score for a player's best hand
  * @return String representation of the action to be taken
  */
  public static String preFlopAction(int score) {
    Random r = new Random();
    int rand = r.nextInt((100 - 0) + 1) + 0; // Generate int from 0 to 100

    if (score > 1000000) { // One pair
      return raiseCallOrFold(51, 101, rand); // 50% raise, 50% call, 0% fold
    } else {
      return raiseCallOrFold(21, 91, rand); // 20% raise, 70% call, 10% fold
    }
  }

  /**
  * This method accepts three integers, the first two being the flags for raise
  * and call, the last being a randomly generated number from 0 to 100
  * @param int raise, (this - 1) = % chance the AI will raise
  * @param int call, (this - raise) = % chance the AI will call
  * @param int rand a ranomd integer to generate the action
  * @return String representation of the action to be taken
  */
  public static String raiseCallOrFold(int raise, int call, int rand) {
    if (rand < raise) {
      return "RAISE";
    } else if (rand < call) {
      return "CALL";
    } else {
      return "FOLD";
    }
  }
}
