import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class CreateVotingApp extends JFrame
{
    public static Map<String, String> voterTable = new HashMap<String, String>();
    public static int votes_for1 = 0;
    public static int votes_for2 = 0;
    public static int votes_for3 = 0;
    public static boolean pollOpen = false;
    // socket for connection to SISServer
    static Socket universal;
    private static int port = 53217;
    // message writer
    static MsgEncoder encoder;
    // message reader
    static MsgDecoder decoder;
    // scope of this component
    private static final String SCOPE = "SIS.Scope1";
    // name of this component
    private static final String NAME = "VotingApp";
    //Return Message
    private static KeyValueList record = new KeyValueList();

    //m3
    private JLabel item1;




    public static void main(String[] args)
    {
      gui myGUI = new gui();
      myGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      myGUI.setSize(1800, 500); // 90% fullscreen resolution
      myGUI.setVisible(true);



        //Main Program Loop
        while (true)
        {
            //Connect to SIS Server
            try
            {

                // try to establish a connection to SISServer
                universal = connect();

                // bind the message reader to inputstream of the socket
                decoder = new MsgDecoder(universal.getInputStream());
                // bind the message writer to outputstream of the socket
                encoder = new MsgEncoder(universal.getOutputStream());

                /*
                 * construct a Connect message to establish the connection
                 */
                KeyValueList conn = new KeyValueList();
                conn.putPair("Scope", SCOPE);
                conn.putPair("MessageType", "Connect");
                conn.putPair("Role", "Basic");
                conn.putPair("Name", NAME);
                encoder.sendMsg(conn);

                initRecord();

                // KeyValueList for inward messages, see KeyValueList for
                // details
                KeyValueList kvList;

                while (true)
                {
                    // attempt to read and decode a message, see MsgDecoder for
                    // details
                    kvList = decoder.getMsg();

                    // process that message
                    ProcessMsg(kvList);
                }

            }
            catch (Exception e)
            {
                // if anything goes wrong, try to re-establish the connection
                e.printStackTrace();
                try
                {
                    // wait for 1 second to retry
                    Thread.sleep(1000);
                }
                catch (InterruptedException e2)
                {
                }
                System.out.println("Try to reconnect");
                try
                {
                    universal = connect();
                }
                catch (IOException e1)
                {
                }
            }
        }
    }

    static Socket connect() throws IOException
    {
        //Used for connect(reconnect) to SIS Server
        Socket socket = new Socket("127.0.0.1", port);
        return socket;
    }

    private static void initRecord()
    {
        //Constructor for return message.
        record.putPair("Scope", "SIS");
        record.putPair("Sender", NAME);
        record.putPair("Date", System.currentTimeMillis() + "");
    }

    private static void ProcessMsg(KeyValueList kvList) throws Exception
    {
        System.out.println("Message Received.");

        String message = kvList.getValue("MessageType");
        String purpose = kvList.getValue("Purpose");

        //Switch for messages coming to component
        //I couldn't customize MessageTypes. Have to use Setting.
        //I added an extra XML item of "Purpose" for us to use.
        switch (message)
        {
            case "Confirm":
                //Confirm connection.
                System.out.println("Connect to SISServer successful!");
                break;
            //Our messages
            case "Setting":
                switch(purpose)
                {
                    case "Vote":
                        if (pollOpen == true) {
                        //Process a vote
                          String email = kvList.getValue("Email");
                          String vote = kvList.getValue("Vote");

                          System.out.println("Received a vote from " + email + " with value " + vote);
                          TallyTable(email, vote);
                        } else {
                          System.out.println("Polling is closed.");
                        }
                          break;
                    case "Kill":
                        //Kill the component
                        System.exit(0);
                        break;
                    case "StartPoll":
                        //Start a Poll
                        System.out.println("Starting Poll.");
                        Integer numberOfIDs = Integer.parseInt(kvList.getValue("NumberOfCandidates"));
                        int[] candidates = new int[numberOfIDs];
                        for (Integer i = 0; i < numberOfIDs; i++)
                        {
                            candidates[i] = Integer.parseInt(kvList.getValue("Candidate" + i.toString()));
                        }
                        pollOpen = true;
                        break;
                    case "ClosePoll":
                        //Close the Poll
                        System.out.println("Closing Poll.");
                        CountVotes();

                        pollOpen = false;
                        break;
                }
            break;
        }
    }


	public static void TallyTable(String email, String vote) {
    if (voterTable.get(email) == null) {
      voterTable.put(email, vote);
    } else {
      System.out.println(email + " has already voted!");
    }
	}

  public static void CountVotes() {
    Iterator it = voterTable.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();


      if (pair.getValue().equals("1000")) {
        votes_for1 ++;
      } else if (pair.getValue().equals("1001")) {
        votes_for2 ++;
      } else if (pair.getValue().equals("1002")) {
        votes_for3 ++;
      }

      it.remove();
    }

    System.out.println("Votes for candidate 1: " + votes_for1);
    System.out.println("Votes for candidate 2: " + votes_for2);
    System.out.println("Votes for candidate 3: " + votes_for3);

    if (votes_for1 > votes_for2) {
      if (votes_for1 > votes_for3) {
        System.out.println("Candidate 1 wins!");
      } else if (votes_for1 == votes_for3) {
        System.out.println("It's a tie between candidates 1 and 3!");
      } else {
        System.out.println("Candidate 3 wins!");
      }
    } else if (votes_for1 == votes_for2) {
      if (votes_for1 > votes_for3) {
        System.out.println("It's a tie between candidates 1 and 2!");
      } else if (votes_for1 < votes_for3) {
        System.out.println("Candidate 3 wins!");
      } else {
        System.out.println("It's a three-way tie!");
      }
    } else {
      if (votes_for2 > votes_for3) {
        System.out.println("Candidate 2 wins!");
      } else if (votes_for2 == votes_for3) {
        System.out.println("It's a tie between candidates 2 and 3!");
      } else {
        System.out.println("Candidate 3 wins!");
      }
    }
  }
}
