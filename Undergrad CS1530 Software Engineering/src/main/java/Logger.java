import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import java.util.Date;
import java.util.ArrayList;

public class Logger {
  private static PrintWriter pw;
  private FileWriter fw;
  private BufferedWriter bw;

  private static ArrayList<Player> players = new ArrayList<Player>();

  public Logger() {
    players = TexasHoldem.getPlayers();
    File temp = new File("log.txt");
    boolean exists = temp.exists();

    if (exists) {
      try {
        fw = new FileWriter("log.txt", true);
      } catch (IOException e) {

      }

      BufferedWriter bw = new BufferedWriter(fw);

      pw = new PrintWriter(bw);
      start();
    } else {
      try {
        pw = new PrintWriter("log.txt", "UTF-8");
      } catch (FileNotFoundException | UnsupportedEncodingException e) {

      }

      start();
    }
  }

  public void start() {
    // Date
    Date date = new Date();
    pw.println("================================================================================");
    pw.println("================================================================================");
    pw.println("GAME STARTED - " + date.toString() + "\n");

    // Player 1
    pw.println("PLAYER NAME:\n\t" + players.get(0).getName());

    // AI Players
    String ai_names = "";
    for (int i = 1; i < players.size(); i++) {
      ai_names += players.get(i).getName();

      if (i < (players.size() - 1)) {
        ai_names += ", ";
      }
    }
    pw.println("\nAI PLAYERS:\n\t" + ai_names + "\n");

    // Cards dealt
    pw.println("CARDS DEALT: ");
    for (int i = 0; i < players.size(); i++) {
      String temp = "\t";
      temp += players.get(i).getName();
      temp += ": --> ";
      temp += players.get(i).getCard_1().toString();
      temp += " ";
      temp += players.get(i).getCard_2().toString();
      pw.println(temp);
    }
  }

  public static void logRank(int index, String rank) {
    pw.println("\n" + players.get(index).getName() + "'s best hand:");
    pw.println("\t" + rank);
  }

  public static void logAction(int index, String action) {
    pw.println(players.get(index).getName() + " " + action);
  }

  public static void preFlop() {
    pw.println("\n------PRE-FLOP----PRE-FLOP----PRE-FLOP----PRE-FLOP----PRE-FLOP----PRE-FLOP------");
  }

  public static void flop() {
    FlopTurnRiver FTR = TexasHoldem.getFTR();
    pw.println("\n------FLOP----FLOP----FLOP----FLOP----FLOP----FLOP----FLOP----FLOP----FLOP------");
    pw.println("\nFLOP CARDS:");
    pw.println("\t" + FTR.getCard_1().toString() + ", " +
                      FTR.getCard_2().toString() + ", " +
                      FTR.getCard_3().toString());
  }

  public static void turn() {
    FlopTurnRiver FTR = TexasHoldem.getFTR();
    pw.println("\n------TURN----TURN----TURN----TURN----TURN----TURN----TURN----TURN----TURN------");
    pw.println("\nTURN CARDS:");
    pw.println("\t" + FTR.getCard_1().toString() + ", " +
                      FTR.getCard_2().toString() + ", " +
                      FTR.getCard_3().toString() + ", " +
                      FTR.getCard_4().toString());
  }

  public static void river() {
    FlopTurnRiver FTR = TexasHoldem.getFTR();
    pw.println("\n------RIVER----RIVER----RIVER----RIVER----RIVER----RIVER----RIVER----RIVER------");
    pw.println("\nRIVER CARDS:");
    pw.println("\t" + FTR.getCard_1().toString() + ", " +
                      FTR.getCard_2().toString() + ", " +
                      FTR.getCard_3().toString() + ", " +
                      FTR.getCard_4().toString() + ", " +
                      FTR.getCard_5().toString());
  }

  public void finish() {
    pw.close();
  }
}
