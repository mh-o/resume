import java.util.ArrayList;

public class HandRank {
  private ArrayList<Player> players;
  private ArrayList<Card> hand_1, hand_2, hand_3, hand_4, hand_5, hand_6, hand_7, hand_8;
  private int cur_player;
  private int score;
  private Card high_card;

  /**
  * Constructor method, just initializes the players so we can access the
  * ArrayList of the player objects
  * @param ArrayList<Player> players
  */
  public HandRank(ArrayList<Player> list) {
    players = list;
  }

  /**
  * This method loops through all players and ranks each of their hands, by
  * calling the rankhand() method
  */
  public void rankHands() {
    for (int i = 0; i < players.size(); i++) {
      score = 0;
      Card first_card = players.get(i).getCard_1(); // COMMENT THIS TO MANUALLY TEST HANDS
      Card second_card = players.get(i).getCard_2(); //COMMENT THIS TO MANUALLY TEST HANDS
      //Card first_card = new Card(4, 4); // UN-COMMENT THIS TO MANUALLY TEST HANDS
      //Card second_card = new Card(4, 3); // UN-COMMENT THIS TO MANUALLY TEST HANDS

      ArrayList<Card> hand = new ArrayList<Card>();
      hand.add(first_card);
      hand.add(second_card);

      if (PlayPoker.isFlop() == true) { // Flop case (5 cards total in hand)
        hand.add(TexasHoldem.getFTR().getCard_1()); // COMMENT THIS TO MANUALLY TEST HANDS
        hand.add(TexasHoldem.getFTR().getCard_2()); // COMMENT THIS TO MANUALLY TEST HANDS
        hand.add(TexasHoldem.getFTR().getCard_3()); // COMMENT THIS TO MANUALLY TEST HANDS
        //hand.add(new Card(6, 4)); // UN-COMMENT THIS TO MANUALLY TEST HANDS
        //hand.add(new Card(6, 1)); // UN-COMMENT THIS TO MANUALLY TEST HANDS
        //hand.add(new Card(2, 4)); // UN-COMMENT THIS TO MANUALLY TEST HANDS
      }

      if (PlayPoker.isTurn() == true) { // Turn case (6 cards total in hand)
        hand.add(TexasHoldem.getFTR().getCard_4()); // COMMENT THIS TO MANUALLY TEST HANDS
        //hand.add(new Card(6, 4)); // UN-COMMENT THIS TO MANUALLY TEST HANDS
      }

      if (PlayPoker.isRiver() == true) { // River case (7 cards total in hand)
        hand.add(TexasHoldem.getFTR().getCard_5()); // COMMENT THIS TO MANUALLY TEST HANDS
        //hand.add(new Card(6, 4)); // UN-COMMENT THIS TO MANUALLY TEST HANDS
      }

      cur_player = i;
      int player_rank = rankHand(hand);
      players.get(i).setHandScore(score);
    }
  }

  /**
  * This method ranks the hand of the player by checking for each poker hand
  * starting from the strongest and progressing to the weakest. The hands are
  * ranked by integer as follows:
  *   Straight Flush -> 8
  *   Four of a Kind -> 7
  *   Full House     -> 6
  *   Flush          -> 5
  *   Straight       -> 4
  *   Three of a Kind-> 3
  *   Two Pair       -> 2
  *   One Pair       -> 1
  * @param ArrayList<Card> hand
  * @return int representing the hand rank
  */
  public int rankHand(ArrayList<Card> hand) {
    int temp_rank = 0;
    ArrayList<Card> no_dups = new ArrayList<Card>();
    ArrayList<Card> copy = new ArrayList<Card>(hand);

    while (temp_rank == 0) {
      temp_rank = isStraightFlush(hand); // Return 8 if we have a straight flush
      if (temp_rank != 0) {break;}

      temp_rank = isFourOfAKind(hand_1); // Return 7 if we have four of a kind
      if (temp_rank != 0) {break;}

      temp_rank = isFullHouse(hand_2); // Return 6 if we have a full house
      if (temp_rank != 0) {break;}

      temp_rank = isFlush(hand_3); // Return 5 if we have a flush
      if (temp_rank != 0) {break;}

      temp_rank = isStraight(hand_4); // Return 4 if we have a straight
      if (temp_rank != 0) {break;}

      temp_rank = isThreeOfAKind(hand_5); // Return 3 if we have three of a kind
      if (temp_rank != 0) {break;}

      temp_rank = isTwoPair(hand_6); // Return 2 if we have two pair
      if (temp_rank != 0) {break;}

      temp_rank = isOnePair(hand_7); // Return 1 if we have one pair
      if (temp_rank != 0) {break;}

      high_card = isHighCard(hand_8);
      break;
    }

    switch (temp_rank) {
      case 8: System.out.println("Straight Flush");
              Logger.logRank(cur_player, "Straight Flush");
              break;
      case 7: System.out.println("Four of a Kind");
              Logger.logRank(cur_player, "Four of a Kind");
              break;
      case 6: System.out.println("Full House");
              Logger.logRank(cur_player, "Full House");
              break;
      case 5: System.out.println("Flush");
              Logger.logRank(cur_player, "Flush");
              break;
      case 4: System.out.println("Striaght");
              Logger.logRank(cur_player, "Straight");
              break;
      case 3: System.out.println("Three of a Kind");
              Logger.logRank(cur_player, "Three of a Kind");
              break;
      case 2: System.out.println("Two Pair");
              Logger.logRank(cur_player, "Two Pair");
              break;
      case 1: System.out.println("One Pair");
              Logger.logRank(cur_player, "One Pair");
              break;
      case 0: String temp = "High Card: " + high_card.toString();
              Logger.logRank(cur_player, temp);
              break;
    }

    return temp_rank;
  }

  /**
  * This method accepts an ArrayList of Card objects of any length and removes
  * any duplicate valued cards (useful for testing straights)
  * @param ArrayList<Card> the original hand
  * @return ArrayList<Card> with duplicate cards removed
  */
  public ArrayList<Card> popDups(ArrayList<Card> hand) {
    ArrayList<Card> temp = new ArrayList<Card>(); // This will hold our 'new' hand
    int prev_val = 0; // Initialize to 0 so we can never accidentally remove the first card

    for (int i = 0; i < hand.size(); i++) { // For each card in the hand
      if (hand.get(i).getValue() != prev_val) { // Check to see if it has the same value as previous
        temp.add(hand.get(i)); // If it is a unique value, add to our new hand
        prev_val = hand.get(i).getValue(); // Set the previous value for next iteration
      }
    }

    return temp;
  }

  /**
  * This method tests a hand for a straight flush, by first testing if the hand
  * is a straight and then testing any hand of 5 that returns true to see if it
  * is also a flush
  * @param ArrayList<Card> the hand to test
  * @return int 8 to signal we have a straight flush, 0 otherwise
  */
  public int isStraightFlush(ArrayList<Card> hand) {
    hand_1 = new ArrayList<>(hand);

    if (hand.size() < 5) { // Pre-flop case
      return 0; // If we dont have 5 cards, we can't possibly have four of a kind
    }

    hand = sortByRank(hand);

    if (hand.size() == 5) { // Flop case
      if (isFlush(hand) != 0 & isStraight(hand) != 0) {
        score += 8000000;
        return 8;
      } else {
        return 0;
      }
    }

    ArrayList<Card> copy1 = new ArrayList<Card>(hand);
    ArrayList<Card> copy2 = new ArrayList<Card>(hand);

    ArrayList<Card> temp1 = new ArrayList<Card>();
    for (int i = 0; i < 5; i++) {
      temp1.add(copy1.get(i));
    }

    ArrayList<Card> temp2 = new ArrayList<Card>();
    for (int i = 1; i < 6; i++) {
      temp2.add(copy2.get(i));
    }

    if (hand.size() == 6) { // Turn case
      if ((isFlush(temp1) != 0 & isStraight(temp1) != 0) |
          (isFlush(temp2) != 0 & isStraight(temp2) != 0)) {
        score += 8000000;
        return 8;
      } else {
        return 0;
      }
    }

    if (hand.size() == 7) { // River case
      ArrayList<Card> temp3 = new ArrayList<Card>();
      for (int i = 2; i < 7; i++) {
        temp3.add(hand.get(i));
      }

      if ((isFlush(temp1) != 0 & isStraight(temp1) != 0) |
          (isFlush(temp2) != 0 & isStraight(temp2) != 0) |
          (isFlush(temp3) != 0 & isStraight(temp3) != 0)) {
        score += 8000000;
        return 8;
      } else {
        return 0;
      }
    }

    return 0;
  }

  public int isFlush(ArrayList<Card> hand) {
    hand_4 = new ArrayList<>(hand);

    if (hand.size() < 5) { // Pre-flop case
      return 0; // If we don't have 5 cards, we can't possibly have a flush
    } else {
      int num_clubs = 0;
      int num_diamonds = 0;
      int num_hearts = 0;
      int num_spades = 0;

      for (int i = 0; i < hand.size(); i++) { // For each card in the hand
        int cur_suit = hand.get(i).getSuit(); // 1=C, 2=D, 3=H, 4=S

        switch (cur_suit) {
          case 1: num_clubs++;
                  break;
          case 2: num_diamonds++;
                  break;
          case 3: num_hearts++;
                  break;
          case 4: num_spades++;
                  break;
        }

        if (num_clubs > 4 | num_diamonds > 4 | num_hearts > 4 | num_spades > 4) {
          score += 5000000;
          return 5;
        }
      }
    }

    return 0;
  }

  public int isStraight(ArrayList<Card> hand) {
    hand_5 = new ArrayList<>(hand);

    hand = popDups(hand);

    if (hand.size() < 5) { // Pre-flop case
      return 0; // If we dont have 5 cards, we can't possibly have a straight
    }

    hand = sortByRank(hand);
    ArrayList<Card> copy = new ArrayList<Card>(hand);
    //System.out.println(hand);

    if (hand.size() == 5) { // Flop case
      if (testStraight(copy)) {
        return 4;
      } else {
        return 0;
      }
    }

    ArrayList<Card> temp1 = new ArrayList<Card>();
    for (int i = 0; i < 5; i++) {
      temp1.add(hand.get(i));
    }

    ArrayList<Card> temp2 = new ArrayList<Card>();
    for (int i = 1; i < 6; i++) {
      temp2.add(hand.get(i));
    }

    if (hand.size() == 6) { // Turn case
      if (testStraight(temp1) | testStraight(temp2)) {
        return 4;
      } else {
        return 0;
      }
    }

    if (hand.size() == 7) { // River case
      ArrayList<Card> temp3 = new ArrayList<Card>();
      for (int i = 2; i < 7; i++) {
        temp3.add(hand.get(i));
      }

      //System.out.println(temp3);
      if (testStraight(temp1) | testStraight(temp2) | testStraight(temp3)) {
        return 4;
      } else {
        return 0;
      }
    }

    return 0; // We shouldn't reach this but java likes return vals
  }

  public boolean testStraight(ArrayList<Card> hand) {
    if (hand.get(0).getValue() == 14) { // Check if hand has ace
      boolean a = hand.get(4).getValue() == 2 && // Case for A, 2, 3, 4, 5
                  hand.get(3).getValue() == 3 &&
                  hand.get(2).getValue() == 4 &&
                  hand.get(1).getValue() == 5;
      boolean b = hand.get(4).getValue() == 10 && // Case for 10, J, Q, K, A
                  hand.get(3).getValue() == 11 &&
                  hand.get(2).getValue() == 12 &&
                  hand.get(1).getValue() == 13;
      if (a || b) {
        score += 4000000;
      }

      return (a || b);
    } else {
      int testRank = hand.get(4).getValue() + 1;

      for (int i = 3; i >= 0; i--) {
        if (hand.get(i).getValue() != testRank) {
          return false; // Not a straight
        }

        testRank++;
      }

      score += 4000000;
      return true; // Straight found
    }
  }

  /**
  * This methods takes the hand and sorts it by rank, with list[0] being the
  * highest rank and list[n] being the lowest rank where n = # of list items
  * @param ArrayList of card objects
  * @return ArrayList of card objects (sorted)
  */
  public ArrayList<Card> sortByRank(ArrayList<Card> hand) {
    ArrayList<Card> temp = new ArrayList<Card>();

    while (hand.size() > 0) {
      int max = 0;
      Card max_card = null;

      for (int i = 0; i < hand.size(); i++) {
        if (hand.get(i).getValue() > max) {
          max = hand.get(i).getValue();
          max_card = hand.get(i);
        }
      }

      temp.add(max_card);
      hand.remove(max_card);
    }

    return temp;
  }

  /**
  * This method tests a hand for four of a kind
  * @param ArrayList<Card> the hand to test
  * @return int 2 to signal we have four of a kind, 0 otherwise
  */
  public int isFourOfAKind(ArrayList<Card> hand) {
    hand_2 = new ArrayList<>(hand);

    if (hand.size() < 5) { // Pre-flop case
      return 0; // If we dont have 5 cards, we can't possibly have four of a kind
    }

    hand = sortByRank(hand);
    ArrayList<Card> copy1 = new ArrayList<Card>(hand);
    ArrayList<Card> copy2 = new ArrayList<Card>(hand);

    if (hand.size() == 5) { // Flop case
      if (testFourOfAKind(hand)) {
        return 7;
      } else {
        return 0;
      }
    }

    ArrayList<Card> temp1 = new ArrayList<Card>();
    for (int i = 0; i < 5; i++) {
      temp1.add(copy1.get(i));
    }

    ArrayList<Card> temp2 = new ArrayList<Card>();
    for (int i = 1; i < 6; i++) {
      temp2.add(copy2.get(i));
    }

    if (hand.size() == 6) { // Turn case
      if (testFourOfAKind(temp1) | testFourOfAKind(temp2)) {
        return 7;
      } else {
        return 0;
      }
    }

    if (hand.size() == 7) { // River case
      ArrayList<Card> temp3 = new ArrayList<Card>();
      for (int i = 2; i < 7; i++) {
        temp3.add(hand.get(i));
      }

      if (testFourOfAKind(temp1) | testFourOfAKind(temp2) | testFourOfAKind(temp3)) {
        return 7;
      } else {
        return 0;
      }
    }

    return 0; // We shouldn't reach this but java likes return vals
  }

  public boolean testFourOfAKind(ArrayList<Card> hand) {
    boolean temp1 = hand.get(0).getValue() == hand.get(1).getValue() &&
                    hand.get(1).getValue() == hand.get(2).getValue() &&
                    hand.get(2).getValue() == hand.get(3).getValue();

    boolean temp2 = hand.get(1).getValue() == hand.get(2).getValue() &&
                    hand.get(2).getValue() == hand.get(3).getValue() &&
                    hand.get(3).getValue() == hand.get(4).getValue();

    // Now we handle the score for 4 of a kind, since there are only 4 of each
    // card we will never have to worry about the highest card value
    if (temp1 | temp2) {
      score += 7000000; // 7th highest hand gets 7000000 points
      score += (hand.get(2).getValue() * 4) * 1000; // Second teir tie break handling
    }

    return (temp1 | temp2);
  }

  public int isFullHouse(ArrayList<Card> hand) {
    hand_3 = new ArrayList<>(hand);

    if (hand.size() < 5) { // Pre-flop case
      return 0; // If we dont have 5 cards, we can't possibly have a full house
    }

    hand = sortByRank(hand);

    if (hand.size() == 5) { // Flop case
      if (testFullHouse(hand)) {
        return 6;
      } else {
        return 0;
      }
    }

    ArrayList<Card> temp1 = new ArrayList<Card>();
    for (int i = 0; i < 5; i++) {
      temp1.add(hand.get(i));
    }

    ArrayList<Card> temp2 = new ArrayList<Card>();
    for (int i = 1; i < 6; i++) {
      temp2.add(hand.get(i));
    }

    if (hand.size() == 6) { // Turn case
      if (testFullHouse(temp1) | testFullHouse(temp2)) {
        return 6;
      } else {
        return 0;
      }
    }

    if (hand.size() == 7) { // River case
      ArrayList<Card> temp3 = new ArrayList<Card>();
      for (int i = 2; i < 7; i++) {
        temp3.add(hand.get(i));
      }

      if (testFullHouse(temp1) | testFullHouse(temp2) | testFullHouse(temp3)) {
        return 6;
      } else {
        return 0;
      }
    }

    return 0; // We shouldn't reach this but java likes return vals
  }

  public boolean testFullHouse(ArrayList<Card> hand) {
    // Test for (X, X, X, Y, Y)
    boolean temp1 = hand.get(0).getValue() == hand.get(1).getValue() &&
                    hand.get(1).getValue() == hand.get(2).getValue() &&
                    hand.get(3).getValue() == hand.get(4).getValue();

    // Test for (X, X, Y, Y, Y)
    boolean temp2 = hand.get(0).getValue() == hand.get(1).getValue() &&
                    hand.get(2).getValue() == hand.get(3).getValue() &&
                    hand.get(3).getValue() == hand.get(4).getValue();

    if (temp1 | temp2) {
      score += 6000000;
      score += (hand.get(2).getValue() * 3) * 1000; // Really no reason for * 3 here, but helps visualize
    }

    return (temp1 | temp2);
  }

  public int isThreeOfAKind(ArrayList<Card> hand) {
    hand_6 = new ArrayList<>(hand);

    if (hand.size() < 5) { // Pre-flop case
      return 0; // If we dont have 5 cards, we can't possibly have a full house
    }

    hand = sortByRank(hand);
    ArrayList<Card> copy = new ArrayList<Card>(hand);

    if (hand.size() == 5) { // Flop case
      if (testThreeOfAKind(copy)) {
        return 3;
      } else {
        return 0;
      }
    }

    ArrayList<Card> temp1 = new ArrayList<Card>();
    for (int i = 0; i < 5; i++) {
      temp1.add(hand.get(i));
    }

    ArrayList<Card> temp2 = new ArrayList<Card>();
    for (int i = 1; i < 6; i++) {
      temp2.add(hand.get(i));
    }

    if (hand.size() == 6) { // Turn case
      if (testThreeOfAKind(temp1) | testThreeOfAKind(temp2)) {
        return 3;
      } else {
        return 0;
      }
    }

    if (hand.size() == 7) { // River case
      ArrayList<Card> temp3 = new ArrayList<Card>();
      for (int i = 2; i < 7; i++) {
        temp3.add(hand.get(i));
      }

      if (testThreeOfAKind(temp1) | testThreeOfAKind(temp2) | testThreeOfAKind(temp3)) {
        return 3;
      } else {
        return 0;
      }
    }

    return 0; // We shouldn't reach this but java likes return vals
  }

  public boolean testThreeOfAKind(ArrayList<Card> hand) {
    // Test for (X, X, X, Y, Z)
    boolean temp1 = hand.get(0).getValue() == hand.get(1).getValue() &&
                    hand.get(1).getValue() == hand.get(2).getValue();

    // Test for (Y, X, X, X, Z)
    boolean temp2 = hand.get(1).getValue() == hand.get(2).getValue() &&
                    hand.get(2).getValue() == hand.get(3).getValue();

    // Test for (Y, Z, X, X, X)
    boolean temp3 = hand.get(2).getValue() == hand.get(3).getValue() &&
                    hand.get(3).getValue() == hand.get(4).getValue();

    if (temp1 | temp2 | temp3) {
      score += 3000000;
      score += (hand.get(2).getValue() * 3) * 1000;
    }

    return (temp1 | temp2 | temp3);
  }

  public int isTwoPair(ArrayList<Card> hand) {
    hand_7 = new ArrayList<>(hand);

    if (hand.size() < 5) { // Pre-flop case
      return 0; // If we dont have 5 cards, we can't possibly have a full house
    }

    hand = sortByRank(hand);
    ArrayList<Card> copy = new ArrayList<Card>(hand);

    if (hand.size() == 5) { // Flop case
      if (testTwoPair(copy)) {
        return 2;
      } else {
        return 0;
      }
    }

    ArrayList<Card> temp1 = new ArrayList<Card>();
    for (int i = 0; i < 5; i++) {
      temp1.add(hand.get(i));
    }

    ArrayList<Card> temp2 = new ArrayList<Card>();
    for (int i = 1; i < 6; i++) {
      temp2.add(hand.get(i));
    }

    if (hand.size() == 6) { // Turn case
      if (testTwoPair(temp1) | testTwoPair(temp2)) {
        return 2;
      } else {
        return 0;
      }
    }

    if (hand.size() == 7) { // River case
      ArrayList<Card> temp3 = new ArrayList<Card>();
      for (int i = 2; i < 7; i++) {
        temp3.add(hand.get(i));
      }

      if (testTwoPair(temp1) | testTwoPair(temp2) | testTwoPair(temp3)) {
        return 2;
      } else {
        return 0;
      }
    }

    return 0; // We shouldn't reach this but java likes return vals
  }

  public boolean testTwoPair(ArrayList<Card> hand) {
    // Test for (X, X, Y, Y, Z)
    boolean temp1 = hand.get(0).getValue() == hand.get(1).getValue() &&
                    hand.get(2).getValue() == hand.get(3).getValue();

    // Test for (X, X, Z, Y, Y)
    boolean temp2 = hand.get(0).getValue() == hand.get(1).getValue() &&
                    hand.get(3).getValue() == hand.get(4).getValue();

    // Test for (Z, X, X, Y, Y)
    boolean temp3 = hand.get(1).getValue() == hand.get(2).getValue() &&
                    hand.get(3).getValue() == hand.get(4).getValue();

    if (temp1) {
      score += 2000000;
      score += ((hand.get(1).getValue() * 2) + (hand.get(3).getValue() * 2)) * 1000;
      score += hand.get(4).getValue(); // If both pairs happen to tie, tiebreak
    } else if (temp2) {
      score += 2000000;
      score += ((hand.get(1).getValue() * 2) + (hand.get(3).getValue() * 2)) * 1000;
      score += hand.get(2).getValue(); // If both pairs happen to tie, tiebreak
    } else if (temp3) {
      score += 2000000;
      score += ((hand.get(1).getValue() * 2) + (hand.get(3).getValue() * 2)) * 1000;
      score += hand.get(0).getValue(); // If both pairs happen to tie, tiebreak
    }

    return (temp1 | temp2 | temp3);
  }

  public int isOnePair(ArrayList<Card> hand) {
    hand_8 = new ArrayList<>(hand);

    if (hand.size() < 5) { // Pre-flop case
      if (hand.get(0).getValue() == hand.get(1).getValue()) {
        return 1;
      } else {
        return 0;
      }
    }

    hand = sortByRank(hand);
    ArrayList<Card> copy = new ArrayList<Card>(hand);

    if (hand.size() == 5) { // Flop case
      if (testOnePair(copy)) {
        return 1;
      } else {
        return 0;
      }
    }

    ArrayList<Card> temp1 = new ArrayList<Card>();
    for (int i = 0; i < 5; i++) {
      temp1.add(hand.get(i));
    }

    ArrayList<Card> temp2 = new ArrayList<Card>();
    for (int i = 1; i < 6; i++) {
      temp2.add(hand.get(i));
    }

    if (hand.size() == 6) { // Turn case
      if (testOnePair(temp1) | testOnePair(temp2)) {
        return 1;
      } else {
        return 0;
      }
    }

    if (hand.size() == 7) { // River case
      ArrayList<Card> temp3 = new ArrayList<Card>();
      for (int i = 2; i < 7; i++) {
        temp3.add(hand.get(i));
      }

      if (testOnePair(temp1) | testOnePair(temp2) | testOnePair(temp3)) {
        return 1;
      } else {
        return 0;
      }
    }

    return 0; // We shouldn't reach this but java likes return vals
  }

  public boolean testOnePair(ArrayList<Card> hand) {
    // Testing for (X, X, A, B, C)
    boolean temp1 = hand.get(0).getValue() == hand.get(1).getValue();

    // Testing for (A, X, X, B, C)
    boolean temp2 = hand.get(1).getValue() == hand.get(2).getValue();

    // Testing for (A, B, X, X, C)
    boolean temp3 = hand.get(2).getValue() == hand.get(3).getValue();

    // Testing for (A, B, C, X, X)
    boolean temp4 = hand.get(3).getValue() == hand.get(4).getValue();

    if (temp1) {
      score += 1000000;
      score += (hand.get(0).getValue() * 2) * 1000;

      int max = hand.get(2).getValue();
      if (hand.get(3).getValue() > max) {max = hand.get(3).getValue();}
      if (hand.get(4).getValue() > max) {max = hand.get(4).getValue();}
      score += max; // Tiebreak
    } else if (temp2) {
      score += 1000000;
      score += (hand.get(1).getValue() * 2) * 1000;

      int max = hand.get(1).getValue();
      if (hand.get(3).getValue() > max) {max = hand.get(3).getValue();}
      if (hand.get(4).getValue() > max) {max = hand.get(4).getValue();}
      score += max; // Tiebreak
    } else if (temp3) {
      score += 1000000;
      score += (hand.get(2).getValue() * 2) * 1000;

      int max = hand.get(0).getValue();
      if (hand.get(1).getValue() > max) {max = hand.get(1).getValue();}
      if (hand.get(4).getValue() > max) {max = hand.get(4).getValue();}
      score += max; // Tiebreak
    } else if (temp4) {
      score += 1000000;
      score += (hand.get(3).getValue() * 2) * 1000;

      int max = hand.get(0).getValue();
      if (hand.get(1).getValue() > max) {max = hand.get(1).getValue();}
      if (hand.get(2).getValue() > max) {max = hand.get(2).getValue();}
      score += max; // Tiebreak
    }

    return (temp1 | temp2 | temp3 | temp4);
  }

  public Card isHighCard(ArrayList<Card> hand) {
    int max = 0;
    int max_index = 0;

    for (int i = 0; i < 2; i++) { // We only care about the player's cards
      if (hand.get(i).getValue() > max) {
        max = hand.get(i).getValue();
        max_index = i;
      }
    }

    return hand.get(max_index);
  }
}
