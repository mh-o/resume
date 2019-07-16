import java.util.*;
import java.sql.*;

public class QueryExecutor {
    private Connection conn;
    private Scanner sc;

    public QueryExecutor(Connection conn, Scanner sc) {
        this.conn = conn;
        this.sc = sc;
    }

    /**
     * Method to execute a PreparedStatement object.
     * Should be used for all queries that return a ResultSet
     *
     * @param PreparedStatement to execute
     * @return ResultSet object that contains the results of the PreparedStatement
     */
    private ResultSet executePreparedSQLQuery(PreparedStatement st) {
        ResultSet res = null;
        try {
            res = st.executeQuery();
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return res;
    }

    /**
     * Method to execute a Prepared SQL update statment, such as CREATE TABLE and INSERT
     * INTO. This method should only be called on updates and will generate SQL
     * errors if applied to SQL queries.
     *
     * @param PreparedStatement to execute
     * @return (1) 0 for SQL Statements that return nothing or (2) The SQL Row Count
     */
    private int executePreparedSQLUpdate(PreparedStatement st) {
        int success = 0;
        try {
            success = st.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return success;
    }

    /**
     * Prints an SQLException's message, SQLState, and Errorcode.
     *
     * @param SQLException
     */
    private void handleSQLException(SQLException e) {
        System.out.println("\nSQL Error");

        while (e != null) {
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("ErrorCode: " + e.getErrorCode() + "\n");
            e = e.getNextException();
        }
    }

    /**
     * Method that accepts an array of messages and returns an array of answers.
     *
     * @param prompts a String[] of the fields to be entered
     * @return res    a String[] of the answers to those fields
     */
    private String[] inputLoop(String[] prompts) {
        String[] res = new String[prompts.length];

        for (int i = 0; i < prompts.length; i++) {
            System.out.print(prompts[i]);
            res[i] = sc.next();
        }

        System.out.println("");
        return res;
    }

    /**
     * Method to generate a random 8 character numeric ID for a new user.
     *
     * @return res String of 8 random integers
     */
    private String getNewID() {
        char[] chars = "0123456789".toCharArray();

        Random rand = new Random();
        String res = "";

        for (int i = 0; i < 8; i++) {
            res += chars[rand.nextInt(chars.length)];
        }

        return res;
    }

    /**
     * Method to prompt for fields required to add a new user to the system, and
     * then attempt to perform said query.
     */
    public void createUser() {
        Menu.createUser();
        String userID = getNewID();

        String[] userPrompt = {
                "USERNAME (10 character max): ",
                "FIRST NAME (15 character max): ",
                "MIDDLE INITIAL: ",
                "LAST NAME (15 character max): ",
                "EMAIL (abc12@pitt.edu): "
        };

        String[] userInput = new String[userPrompt.length];
        userInput = inputLoop(userPrompt);

        try {
            PreparedStatement st = conn.prepareStatement("INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?)");
            st.setInt(1, Integer.parseInt(userID));
            st.setString(2, userInput[0]);
            st.setString(3, userInput[1]);
            st.setString(4, userInput[2]);
            st.setString(5, userInput[3]);
            st.setString(6, userInput[4]);

            if(executePreparedSQLUpdate(st) != 0) {
                System.out.printf("User created with id %s!\n\n", userID);
            } else {
                System.out.printf("Unable to create user\n\n");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Method to prompt for fields required to add an existing user to another
     * existing user's contact list within the system, and then attempt to perform
     * said query.
     */
    public void addToContacts() {
        Menu.addToContacts();

        String[] res = addToContactsPrompt();

        try {
          PreparedStatement st = conn.prepareStatement("INSERT INTO Contact_List VALUES (?, ?)");
          st.setInt(1, Integer.parseInt(res[0]));
          st.setInt(2, Integer.parseInt(res[1]));

          if (executePreparedSQLUpdate(st) != 0) {
            System.out.printf("User added to contacts!\n\n");
          } else {
            System.out.printf("Unable to add user to contacts\n\n");
          }
        } catch (SQLException e) {
          handleSQLException(e);
        }
    }

    /**
     * Method to prompt for input for the required fields to add an existing user
     * to another existing user's contact list. Pre-emptive attempts will be made
     * to avoid possible syntax errors for the database.
     *
     * @return res  a String[] of required fields for addToContacts
     */
    private String[] addToContactsPrompt() {
        String[] res = new String[2];
        String[] prompts = new String[2];

        prompts[0] = " > USER ID (user's contact list): ";
        prompts[1] = " > USER ID (contact to add): ";

        res = inputLoop(prompts);

        return res;
    }

    /**
     * Method to prompt for fields required to display contacts for an existing
     * user, and then attempt to perform said query.
     */
    public void displayContacts() {
        Menu.displayContacts();

        String[] res = displayContactsPrompt();

        try {
          PreparedStatement st = conn.prepareStatement("SELECT * FROM Users WHERE UserID IN " +
                                                       "(SELECT ContactID AS UserID FROM Contact_List " +
                                                       "WHERE UserId=?)");
          st.setInt(1, Integer.parseInt(res[0]));

          ResultSet res_set = executePreparedSQLQuery(st);


          while (res_set.next()) {
              String usr_ID = res_set.getString("UserID");
              String usr_name = res_set.getString("Username");
              System.out.println(" > ID: " + usr_ID + " Username: " + usr_name);
          }

          System.out.println("");
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Method to prompt for input for the required fields to add an existing user
     * to another existing user's contact list. Pre-emptive attempts will be made
     * to avoid possible syntax errors for the database.
     *
     * @return res  a String[] of required fields for displayContacts
     */
    private String[] displayContactsPrompt() {
        String[] res = new String[1];
        String[] prompts = new String[1];

        prompts[0] = " > USER ID: ";

        res = inputLoop(prompts);

        return res;
    }

    /**
     * Method to prompt for fields required to add a new group chat to the system,
     * and then attempt to perform said query.
     */
    public void createGroupChat() {
        Menu.createGroupChat();

        String[] res = createGroupChatPrompt();
        String temp = getNewID();

        try {
          PreparedStatement st = conn.prepareStatement("INSERT INTO Group_Chat VALUES (?, ?)");
          st.setInt(1, Integer.parseInt(temp));
          st.setString(2, res[0]);

          if (executePreparedSQLUpdate(st) != 0) {
            System.out.println(" > Created new group chat with ID: " + temp + "\n\n");
          } else {
            System.out.println(" > Unable to create new group chat.\n\n");
          }
        } catch (SQLException e) {
          handleSQLException(e);
        }
    }

    /**
     * Method to prompt for input for the required fields to add a new group chat
     * to the system. Pre-emptive attempts will be made to avoid possible syntax
     * errors for the database.
     *
     * @return res  a String[] of required fields for createGroupChat
     */
    private String[] createGroupChatPrompt() {
        String[] res = new String[1];
        String[] prompts = new String[1];

        prompts[0] = " > GROUP CHAT NAME (20 character max): ";

        res = inputLoop(prompts);

        return res;
    }

    /**
     * Method to prompt for fields required to add an existing user to an existing
     * group chat within the system, and then attempt to perform said query.
     */
    public void addToGroupChat() {
        Menu.addToGroupChat();

        String[] res = addToGroupChatPrompt();

        try {
          PreparedStatement st = conn.prepareStatement("INSERT INTO Group_Chat_Membership Values (?, ?)");
          st.setInt(1, Integer.parseInt(res[0]));
          st.setInt(2, Integer.parseInt(res[1]));

          if (executePreparedSQLUpdate(st) != 0) {
            System.out.printf("Inserted into group chat!\n\n");
          } else {
            System.out.printf("Unable to insert!\n\n");
          }
        } catch (SQLException e) {
          handleSQLException(e);
        }
    }

    /**
     * Method to prompt for input for the required fields to add an existing user
     * to an existing group chat. Pre-emptive attempts will be made to avoid
     * possible syntax errors for the databse.
     *
     * @return res  a String[] of required fields for addToGroupChat
     */
    private String[] addToGroupChatPrompt() {
        String[] res = new String[2];
        String[] prompts = new String[2];

        prompts[0] = " > GROUP CHAT ID: ";
        prompts[1] = " > USER ID: ";

        res = inputLoop(prompts);

        return res;
    }

    /**
     * Method to prompt for fields required to remove an existing user from an
     * existing group chat within the system, and then attempt to perform said
     * query.
     */
    public void leaveGroupChat() {
        Menu.leaveGroupChat();

        String[] userPrompt = { "GROUPID (int): ",  "USERID (int): "};
        String[] userInput = new String[userPrompt.length];
        userInput = inputLoop(userPrompt);

        try {
            PreparedStatement st = conn.prepareStatement("DELETE FROM Group_Chat_Membership WHERE GroupID=? AND UserId=?");
            st.setInt(1, Integer.parseInt(userInput[0]));
            st.setInt(2, Integer.parseInt(userInput[1]));

            if(executePreparedSQLUpdate(st) != 0) {
                System.out.printf("Successfully removed user %s from group %s!\n\n", userInput[1], userInput[0]);
            } else {
                System.out.printf("Unable to remove from group\n\n");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Allows a user to send a message to another user
     */
    public void sendMessageToUser() {
        Menu.sendMessageToUser();

        String[] userPrompt = {
                "Message ID <INT>: ",
                "Message Text <MAX 100 CHAR>: ",
                "Date <YYYY-MM-DD>: ",
                "RecipientID <INT>: ",
                "SenderID <INT>: "
        };
        String[] userInput = new String[userPrompt.length];

        userInput = inputLoop(userPrompt);

        try {
            PreparedStatement st = conn.prepareStatement("INSERT INTO Message (ID, Text, Date, RecipientID, SenderID) " +
                    "VALUES (?, ?, ?, ?, ?)");
            st.setInt(1, Integer.parseInt(userInput[0]));
            st.setString(2, userInput[1]);
            st.setDate(3, java.sql.Date.valueOf(userInput[2]));
            st.setInt(4, Integer.parseInt(userInput[3]));
            st.setInt(5, Integer.parseInt(userInput[4]));

            if(executePreparedSQLUpdate(st) != 0) {
                System.out.printf("Message sent!\n\n");
            } else {
                System.out.printf("Message unable to send\n\n");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * A user can send a message to a group chat
     * (aka all users who are members of that group chat)
     */
    public void sendMessageToGroupChat() {
        Menu.sendMessageToGroupChat();

        String[] userPrompt = {
                "Message ID <INT>: ",
                "Message Text <MAX 100 CHAR>: ",
                "Date <YYYY-MM-DD>: ",
                "GroupID <INT>: ",
                "SenderID <INT>: "
        };

        String[] userInput = new String[userPrompt.length];
        userInput = inputLoop(userPrompt);

        try {
            PreparedStatement st = conn.prepareStatement("INSERT INTO Message (ID, Text, Date, GroupID, SenderID) " +
                    "VALUES (?, ?, ?, ?, ?)");
            st.setInt(1, Integer.parseInt(userInput[0]));
            st.setString(2, userInput[1]);
            st.setDate(3, java.sql.Date.valueOf(userInput[2]));
            st.setInt(4, Integer.parseInt(userInput[3]));
            st.setInt(5, Integer.parseInt(userInput[4]));

            if(executePreparedSQLUpdate(st) != 0) {
                System.out.printf("Message sent!\n\n");
            } else {
                System.out.printf("Message unable to send\n\n");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Given a userID, display all messages either sent to or recieved by that user
     */
    public void displayMessages() {
        Menu.displayMessages();

        String[] userPrompt = {"User ID <INT>: "};
        String[] userInput = new String[userPrompt.length];

        userInput = inputLoop(userPrompt);

        try {
            PreparedStatement st1 = conn.prepareStatement("SELECT * FROM Message M WHERE M.RecipientID=? " +
                    "OR ? IN (SELECT UserID FROM Group_Chat_Membership G WHERE G.GroupID = M.GroupID)");
            PreparedStatement st2 = conn.prepareStatement("DELETE FROM Unread_Messages WHERE UserID=?");

            st1.setInt(1, Integer.parseInt(userInput[0]));
            st1.setInt(2, Integer.parseInt(userInput[0]));
            st2.setInt(1, Integer.parseInt(userInput[0]));

            printMessageResultSet(executePreparedSQLQuery(st1)); // Print and read all user messages
            executePreparedSQLUpdate(st2); // Remove messages that were just read from the unread_messages tables
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Given a userID, display all messages either sent to or recieved by that user
     * that have not been previously read
     */
    public void displayNewMessages() {
        Menu.displayNewMessages();

        String[] userPrompt = {"User ID <INT>: "};
        String[] userInput = new String[userPrompt.length];

        userInput = inputLoop(userPrompt);

        try {
            PreparedStatement st1 = conn.prepareStatement("SELECT * FROM MESSAGE WHERE ID IN" +
                    "(SELECT MessageID AS ID FROM Unread_Messages WHERE UserID=?)");
            PreparedStatement st2 = conn.prepareStatement("DELETE FROM Unread_Messages WHERE UserID=?");

            st1.setInt(1, Integer.parseInt(userInput[0]));
            st2.setInt(1, Integer.parseInt(userInput[0]));

            printMessageResultSet(executePreparedSQLQuery(st1));
            executePreparedSQLUpdate(st2);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Prints messages from a ResultSet
     *
     * @param: ResultSet containing messages from the Message table
     */
    private void printMessageResultSet(ResultSet results) {
        try {
            if(!results.isBeforeFirst()) {
                System.out.println("No new messages");
            } else {
                while (results.next()) {
                    int messageID = results.getInt("ID");
                    String text = results.getString("Text");
                    java.sql.Date date = results.getDate("Date");
                    int senderID = results.getInt("SenderID");
                    int groupID = results.getInt("GroupID");
                    int recipientID = results.getInt("RecipientID");

                    System.out.printf("MessageID: %d\nText: %s\nDate: %s\nSenderID: %d\nGroupID: %d\nRecipientID: %d\n\n",
                            messageID, text, date, senderID, groupID, recipientID);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Given two user ID's, print the path of their relationship,
     * if a relationship of three degrees or less exists
     */
    public void threeDegrees() {

        Menu.displayNewMessages();

        String[] userPrompt = {"UserID #1 <INT>: ", "UserID #2 <INT>: "};
        String[] userInput = new String[userPrompt.length];

        userInput = inputLoop(userPrompt);

        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM three_degrees(?, ?)");

            st.setInt(1, Integer.parseInt(userInput[0]));
            st.setInt(2, Integer.parseInt(userInput[1]));

            ResultSet results = executePreparedSQLQuery(st);

            if (!results.isBeforeFirst()) { // If ResultSet is empty, no path exists
                System.out.println(userInput[0] + " and " + userInput[1] + " are not connected by three degrees or less.");
            }
            else {
                int[] path = new int[4];
                int count = 0;

                // For each tuple in the query result, insert into the path array in order
                while (results.next()) {
                    int userID = results.getInt("userid");
                    int contactID = results.getInt("contactid");

                    if (count == 0) { // if path is empty, insert at beginning
                        path[0] = userID;
                        path[1] = contactID;
                        count = 2;
                    } else if (path[count - 1] == userID) { // if previous contactID == current userID, append to end of path array
                        path[count++] = contactID;
                    } else { // if current contactID == previous userID, insert at beginning of path array
                        int insertAtIndex;
                        for (insertAtIndex = 0; insertAtIndex < path.length && path[insertAtIndex] != contactID; insertAtIndex++);

                        for (int i = insertAtIndex; i < path.length - 1; i++) {
                            path[i + 1] = path[i];
                        }
                        path[insertAtIndex] = userID;
                        count++;
                    }
                }

                // Print path
                System.out.print("START --->>> ");
                for (int i = 0; i < count; i++) {
                    System.out.print(path[i] + " --->>> ");
                }
                System.out.println("END");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Print the 10 most popular users (users with the most tuples in the contact table)
     */
    public void mostPopular() {
        Menu.mostPopular();

        try {
            PreparedStatement st = conn.prepareStatement("SELECT ContactID, COUNT(ContactID) FROM Contact_List " +
                    "GROUP BY ContactID ORDER BY COUNT(ContactID) DESC FETCH FIRST 10 ROWS ONLY");
            ResultSet results = executePreparedSQLQuery(st);

            System.out.println("UserID   |  # of friends");
            System.out.println("------------------------");


            while (results.next()) {
                int contactID = results.getInt("ContactID");
                int count = results.getInt("Count");

                System.out.println(contactID + " | " + count);
            }

            System.out.println();

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Print the most vocal users (users with the most tuples in the message table)
     */
    public void mostVocal() {
        Menu.mostVocal();

        try {
            PreparedStatement st = conn.prepareStatement("SELECT SenderID, COUNT(SenderID) FROM Message " +
                    "WHERE Date >= now() - interval '11' MONTH GROUP BY SenderID ORDER BY COUNT(SenderID) DESC FETCH FIRST 10 ROWS ONLY");
            ResultSet results = executePreparedSQLQuery(st);

            System.out.println("UserID   |  # of messages");
            System.out.println("------------------------");

            while (results.next()) {
                int senderID = results.getInt("SenderID");
                int count = results.getInt("Count");

                System.out.println(senderID + " | " + count);
            }

            System.out.println();

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Drop a user's data from the system
     * Due to triggers and integrity constaints, we only have to remove them from the user table
     * Deletions from all other tables will occur automatically
     */
    public void dropUser() {
        Menu.dropUser();

        String[] userPrompt = {"UserID #1 <INT>: "};
        String[] userInput = new String[userPrompt.length];

        userInput = inputLoop(userPrompt);

        try {
            PreparedStatement st = conn.prepareStatement("DELETE FROM Users WHERE UserID= ?");
            st.setInt(1, Integer.parseInt(userInput[0]));

            if (executePreparedSQLUpdate(st) != 0) {
                System.out.println("User data for " + userInput[0] + " is dropped");
            } else {
                System.out.println("Unable to remove user data for " + userInput[0]);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
} // END QUERYEXECUTOR CLASS
