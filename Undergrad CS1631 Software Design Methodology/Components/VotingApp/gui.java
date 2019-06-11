import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;
import java.io.*;
import java.util.*;

public class gui extends JFrame {
  private JTextField item1, item2, item3, item4, item5, item6;
  private JLabel label1, label2, label3, label4, label5, label6;
  private JLabel createNewVoteDisplay, createNewScriptDisplay, runDisplay;
  private JTextArea textArea, newVoteConsole, newScriptConsole;
  private ArrayList<String> voteLines, scriptLines;
  private JScrollPane scroll;
  private Boolean pollingOpen = false;
  private String voter;
  private int votes_for1, votes_for2, votes_for3;
  public static Map<String, String> voterTable = new HashMap<String, String>();

  public gui() {
    super("Voting App GUI");
    setLayout(new GridLayout(3,1));

    ///////////////////////////////////////////////////////////////////////////
    // CREATE NEW VOTE XML ---- CREATE NEW VOTE XML ---- CREATE NEW VOTE XML //
    ///////////////////////////////////////////////////////////////////////////
    JPanel newPanel1 = new JPanel(new FlowLayout());

    label1 = new JLabel("Email:");
    label1.setHorizontalTextPosition(JLabel.LEFT);
    newPanel1.add(label1);

    item1 = new JTextField("you@example.com", 20);
    newPanel1.add(item1);

    label2 = new JLabel("Vote:");
    label2.setHorizontalTextPosition(JLabel.LEFT);
    newPanel1.add(label2);

    item2 = new JTextField("1001", 20);
    newPanel1.add(item2);

    label3 = new JLabel("File:");
    label3.setHorizontalTextPosition(JLabel.LEFT);
    newPanel1.add(label3);

    item3 = new JTextField("file1.xml", 20);
    newPanel1.add(item3);

    createNewVoteDisplay = new JLabel("REPLACE 'vote' WITH CODES: 1001, 1002, 1003");
    newPanel1.add(createNewVoteDisplay);

    newPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Create New Vote XML"));

    add(newPanel1);
    pack();

    //////////////////////////////////////////////////////////////////
    // NEW VOTE CONSOLE ---- NEW VOTE CONSOLE ---- NEW VOTE CONSOLE //
    //////////////////////////////////////////////////////////////////
    JPanel newPanel5 = new JPanel(new FlowLayout());

    newVoteConsole = new JTextArea(7, 50);
    newVoteConsole.setEditable(false);

    JScrollPane newVoteScroll = new JScrollPane(newVoteConsole);
    newVoteScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    newPanel5.add(newVoteScroll);
    newPanel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "New Vote Console"));

    add(newPanel5);
    pack();

    ////////////////////////////////////////////////////////////////////////////////////
    // CREATE NEW TEST SCRIPT ---- CREATE NEW TEST SCRIPT ---- CREATE NEW TEST SCRIPT //
    ////////////////////////////////////////////////////////////////////////////////////
    JPanel newPanel2 = new JPanel(new FlowLayout());

    label4 = new JLabel("Files (separated by ','):");
    label4.setHorizontalTextPosition(JLabel.LEFT);
    newPanel2.add(label4);

    item4 = new JTextField("openPoll.xml,file1.xml,closePoll.xml", 30);
    newPanel2.add(item4);

    label5 = new JLabel("File (.script extension):");
    label5.setHorizontalTextPosition(JLabel.LEFT);
    newPanel2.add(label5);

    item5 = new JTextField("test.script", 20);
    newPanel2.add(item5);

    createNewScriptDisplay = new JLabel("PROPER SCRIPTS SHOULD BEGIN 'openPoll.xml' AND END 'closePoll.xml'");
    newPanel2.add(createNewScriptDisplay);

    newPanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Create New Test Script"));

    add(newPanel2);
    pack();

    ////////////////////////////////////////////////////////////////////////
    // NEW SCRIPT CONSOLE ---- NEW SCRIPT CONSOLE ---- NEW SCRIPT CONSOLE //
    ////////////////////////////////////////////////////////////////////////
    JPanel newPanel6 = new JPanel(new FlowLayout());

    newScriptConsole = new JTextArea(7, 50);
    newScriptConsole.setEditable(false);

    JScrollPane newScriptScroll = new JScrollPane(newScriptConsole);
    newScriptScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    newPanel6.add(newScriptScroll);
    newPanel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "New Script Console"));

    add(newPanel6);
    pack();

    //////////////////////////////////////////////////////////////
    // RUN TEST SCRIPT ---- RUN TEST SCRIPT ----RUN TEST SCRIPT //
    //////////////////////////////////////////////////////////////
    JPanel newPanel3 = new JPanel(new FlowLayout());

    label6 = new JLabel("Script to run:");
    label5.setHorizontalTextPosition(JLabel.LEFT);
    newPanel3.add(label6);

    item6 = new JTextField("test.script", 64);
    newPanel3.add(item6);

    runDisplay = new JLabel("RUN TEST SCRIPT INFO WILL BE DISPLAYED HERE");
    newPanel3.add(runDisplay);

    newPanel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Run Test Script"));

    add(newPanel3);
    pack();

    ////////////////////////////////////////////
    // CONSOLE ---- CONSOLE ---- CONSOLE ---- //
    ////////////////////////////////////////////
    JPanel newPanel4 = new JPanel(new FlowLayout());

    textArea = new JTextArea(7, 70);
    JScrollPane scrollPane = new JScrollPane(textArea);
    textArea.setEditable(false);

    scroll = new JScrollPane(textArea);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    newPanel4.add(scroll);

    newPanel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Console"));

    add(newPanel4);
    pack();



    /////////////////////////////////////////////
    // LISTENERS ---- LISTENERS ---- LISTENERS //
    /////////////////////////////////////////////
    thehandler handler = new thehandler();
    item1.addActionListener(handler);
    item2.addActionListener(handler);
    item3.addActionListener(handler);
    item4.addActionListener(handler);
    item5.addActionListener(handler);
    item6.addActionListener(handler);
  }

  public void createScript(String files, String file) throws IOException {
    PrintWriter pw = new PrintWriter(file, "UTF-8");

    String[] lines = files.split(",");

    for (int i=0; i < lines.length; i++) {
      pw.println(lines[i]);
    }

    pw.close();

  }

  public void createVote(String email, String vote, String file) throws IOException {
    PrintWriter pw = new PrintWriter(file, "UTF-8");

    pw.println("<?xml version=\"1.0\" standalone=\"yes\"?>");
    pw.println("<!--Generated by guiComp.java -->");
    pw.println("<Msg>");
    pw.println("  <Item>");
    pw.println("    <Key>Scope</Key>");
    pw.println("    <Value>SIS.Scope1</Value>");
    pw.println("  </Item>");
    pw.println("  <Item>");
    pw.println("    <Key>MessageType</Key>");
    pw.println("    <Value>Setting</Value>");
    pw.println("  </Item>");
    pw.println("  <Item>");
    pw.println("    <Key>Purpose</Key>");
    pw.println("    <Value>Vote</Value>");
    pw.println("  </Item>");
    pw.println("  <Item>");
    pw.println("    <Key>Receiver</Key>");
    pw.println("    <Value>VotingApp</Value>");
    pw.println("  </Item>");
    pw.println("  <Item>");
    pw.println("    <Key>Sender</Key>");
    pw.println("    <Value>Debugger</Value>");
    pw.println("  </Item>");
    pw.println("  <Item>");
    pw.println("    <Key>Vote</Key>");
    pw.println("    <Value>" + vote + "</Value>");
    pw.println("  </Item>");
    pw.println("  <Item>");
    pw.println("    <Key>Email</Key>");
    pw.println("    <Value>" + email +"</Value>");
    pw.println("  </Item>");
    pw.println("</Msg>");

    pw.close();
  }

  public ArrayList<String> parseFile(String filename) {
    ArrayList<String> lines = new ArrayList<String>();

    try {
      Scanner sc = new Scanner(new File(filename));

      while (sc.hasNextLine()) {
        lines.add(sc.nextLine());
      }

      sc.close();
    } catch (FileNotFoundException e) {

    }

    return(lines);
  }

  public void votePreview(ArrayList<String> lines) {
    newVoteConsole.setText("");

    for (int i=0; i < lines.size(); i++) {
      newVoteConsole.append(lines.get(i) + "\n");
    }
  }

  public void scriptPreview(ArrayList<String> lines) {
    newScriptConsole.setText("");

    for (int i=0; i < lines.size(); i++) {
      newScriptConsole.append(i + ": " + lines.get(i) + "\n");
    }
  }

  public void runScript(String file) {
    textArea.setText("");
    voterTable.clear();
    votes_for1 = 0;
    votes_for2 = 0;
    votes_for3 = 0;


    ArrayList<String> files = new ArrayList<String>(parseFile(file));

    for (int i=0; i < files.size(); i++) {
      textArea.append(i + ": ");

      ArrayList<String> fileLines = new ArrayList<String>(parseFile(files.get(i)));

      for (int j=0; j < fileLines.size(); j++) {
        int val = handleMessage(fileLines.get(j));

        if (val >= 2) {
          j = fileLines.size() - 1;
        } else if (val == 1) {
          getVoter(files.get(i));
        }
      }

      textArea.append("\n");
    }
  }

  public void getVoter(String file) {
    ArrayList<String> lines = new ArrayList<String>(parseFile(file));
    String voterString = null;
    String voteString = null;

    for (int i=0; i < lines.size(); i++) {
      if (lines.get(i).contains("Email</K")) {
        voterString = lines.get(i+1);
        voterString = voterString.replace("    <Value>", "");
        voterString = voterString.replace("</Value>", "");
      }

      if (lines.get(i).contains("Vote</K")) {
        voteString = lines.get(i+1);
        voteString = voteString.replace("    <Value>", "");
        voteString = voteString.replace("</Value>", "");
      }
    }

    if (voterTable.get(voterString) == null) {
      voterTable.put(voterString, voteString);
      System.out.println(voterTable);


      if (pollingOpen) {
        textArea.append("Vote recieved from " + voterString);
      } else {
        textArea.append(voterString + "sent a vote that was not accepted due to polling being closed.");
      }
    } else {
      textArea.append("This person has already voted: ");
    }
  }

  public int handleMessage(String line) {
    if (line.contains("StartPoll")) {
      if (!pollingOpen) {
        textArea.append("Message recieved... starting poll!");
        pollingOpen = true;
      } else {
        textArea.append("Polling is alread open.");
      }
      return 2;
    } else if (line.contains("ClosePoll")) {
      if (pollingOpen) {
        textArea.append("Message recieved... closing poll!");
        pollingOpen = false;

        Iterator it = voterTable.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry pair = (Map.Entry)it.next();

          if (pair.getValue().equals("1001")) {
            votes_for1 ++;
          } else if (pair.getValue().equals("1002")) {
            votes_for2 ++;
          } else if (pair.getValue().equals("1003")) {
            votes_for3 ++;
          }

          it.remove();
        }

        textArea.append("\n\nVotes for candidate 1: " + votes_for1);
        textArea.append("\nVotes for candidate 2: " + votes_for2);
        textArea.append("\nVotes for candidate 3: " + votes_for3 + "\n");

        if (votes_for1 > votes_for2) {
          if (votes_for1 > votes_for3) {
            textArea.append("Candidate 1 wins!");
          } else if (votes_for1 == votes_for3) {
            textArea.append("It's a tie between candidates 1 and 3!");
          } else {
            textArea.append("Candidate 3 wins!");
          }
        } else if (votes_for1 == votes_for2) {
          if (votes_for1 > votes_for3) {
            textArea.append("It's a tie between candidates 1 and 2!");
          } else if (votes_for1 < votes_for3) {
            textArea.append("Candidate 3 wins!");
          } else {
            textArea.append("It's a three-way tie!");
          }
        } else {
          if (votes_for2 > votes_for3) {
            textArea.append("Candidate 2 wins!");
          } else if (votes_for2 == votes_for3) {
            textArea.append("It's a tie between candidates 2 and 3!");
          } else {
            textArea.append("Candidate 3 wins!");
          }
        }
      } else {
        textArea.append("Can't close polling, polling is already closed.");
      }
      return 3;
    } else if (line.contains(">1001<") || line.contains(">1002<") || line.contains(">1003<")) {
      return 1;
    } else {
      return 0;
    }
  }

  private class thehandler implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      String string1 = "";
      String string2 = "";
      String string3 = "";

      if (event.getSource() == item1 || event.getSource() == item2 || event.getSource() == item3) {
        try {
          createVote(item1.getText(), item2.getText(), item3.getText());
          voteLines = new ArrayList<String>(parseFile(item3.getText()));
          votePreview(voteLines);
          createNewVoteDisplay.setText("FILE '" + item3.getText() + "' CREATED SUCCESSFULLY, SEE PREVIEW ==>");
        } catch (IOException e) {
          System.out.println("File not created.");
        }
      } else if (event.getSource() == item4 || event.getSource() == item5) {
        try {
          createScript(item4.getText(), item5.getText());
          scriptLines = new ArrayList<String>(parseFile(item5.getText()));
          scriptPreview(scriptLines);
          createNewScriptDisplay.setText("SCRIPT '" + item5.getText() + "' CREATED SUCCESSFULLY, SEE PREVIEW ==>");
        } catch (IOException e) {
          //
        }
      } else if (event.getSource() == item6) {
        runScript(item6.getText());
        runDisplay.setText("NOW RUNNING '" + item6.getText() + "', SEE RESULT ==>");
      }
    }
  }


}
