/**
 * To Run This Program:
 *     1) Copy directory to class3:
 *          rsync -r ./ <your-username>@class3.cs.pitt.edu:/path/
 *     2) Change USERNAME and PASSWORD values at beginning of Messenger
 *     3) Compile:
 *          javac Messenger.java
 *     4) Run:
 *          java -cp postgresql-42.1.3.jre6.jar:. Messenger
 */

import java.util.*;
import java.lang.*;
import java.sql.*;
import java.io.*;

public class Messenger {
    private final String USERNAME = "mts74";
    private final String PASSWORD = "1234567";

    private Connection conn;
    private QueryExecutor queryExecutor;
    private Scanner sc;

    /**
     * Initializes the database connection
     * Creates this program's scanner
     * Initializes tables (creates tables, PL/pgSQL functions, triggers, and inserts mock data)
     * Creates a QueryExecutor that constructs and executes queries
     * Starts the runProgram loop
     */
    public Messenger() {
        initializeDB(USERNAME, PASSWORD); // Login credentials
        sc = new Scanner(System.in);
        queryExecutor = new QueryExecutor(conn, sc);
        initializeTables();
        runProgram();
    }

    /**
     * Infinately loops
     * Each loop, prints out option menu,
     * Asks for user input,
     * And executes appropriate QueryExecuter method
     */
    private void runProgram() {
        Menu.title();

        String choice = "";

        while (true) {
            Menu.options();
            System.out.print("\n > ");
            choice = sc.next();

            switch (choice) {
                case "1":
                    queryExecutor.createUser();
                    break;
                case "2":
                    queryExecutor.addToContacts();
                    break;
                case "3":
                    queryExecutor.displayContacts();
                    break;
                case "4":
                    queryExecutor.createGroupChat();
                    break;
                case "5":
                    queryExecutor.addToGroupChat();
                    break;
                case "6":
                    queryExecutor.leaveGroupChat();
                    break;
                case "7":
                    queryExecutor.sendMessageToUser();
                    break;
                case "8":
                    queryExecutor.sendMessageToGroupChat();
                    break;
                case "9":
                    queryExecutor.displayMessages();
                    break;
                case "10":
                    queryExecutor.displayNewMessages();
                    break;
                case "11":
                    queryExecutor.threeDegrees();
                    break;
                case "12":
                    queryExecutor.mostPopular();
                    break;
                case "13":
                    queryExecutor.mostVocal();
                    break;
                case "14":
                    queryExecutor.dropUser();
                    break;
                case "15":
                    testProgram();
                    break;
                case "16":
                    endProgram();
                    break;
                default:
                    System.out.println("\nInvalid choice, use (1-15).\n");
            }
        }
    }

    private void endProgram() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

    /**
     * Reassigns System.in to a ByteArrayInputStream
     * Now, scanner will read from the buffer instead of the console
     */
    private void testProgram() {
        ByteArrayInputStream in = new ByteArrayInputStream(
                ("1 22222 Matt T Stoss mts74@pitt.edu " +
                 "2 23518172  55928230 " +
                 "3 23518172 " +
                 "4 GroupName " +
                 "5 45372232 31390718 " +
                 "6 45372232 31390718 " +
                 "7 1111 SampleText 2018-01-01 47865396 86078637 " +
                 "8 2222 SampleText 2018-01-01 45372232 16323735 " +
                 "9 47865396 " +
                 "10 92090725 " +
                 "11 91712518 51172338 " +
                 "12 " +
                 "13 " +
                 "14 91712518 " +
                 "16"
                ).getBytes()
        );
        System.setIn(in);
        Messenger messenger = new Messenger();
    }

    /**
     * Method to setup the program with the appropriate tables from
     * 'create_messenger.sql' and then insert values from 'insert_messenger.sql'.
     */
    private void initializeTables() {
        System.out.print(" > Creating tables...");
        getSQLStatements("create_messenger.sql", ";");
        System.out.print(" ...done!\n");

        System.out.print(" > Creating functions...");
        getSQLStatements("functions.sql", "plpgsql;");
        System.out.print(" ...done!\n");

        System.out.print(" > Creating triggers...");
        getSQLStatements("triggers.sql", ";");
        System.out.print(" ...done!\n");

        System.out.print(" > Inserting data...");
        getSQLStatements("insert_messenger.sql", ";");
        System.out.print(" ...done!\n\n");
    }

    /**
     * Parses SQL files for queries, then extracts and execute those queries
     *
     * @param The SQL file to parse and execute
     * @param A string that signifies the end of a single query
     */
    private void getSQLStatements(String file, String statementEnd) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String statement = "";
            String line;

            while ((line = br.readLine()) != null) {
                statement += line;

                if (line.endsWith(statementEnd)) {
                    try {
                        Statement st = conn.createStatement();
                         st.executeUpdate(statement);
                    } catch (SQLException e) {
                        System.out.println("\nSQL Error");

                        while (e != null) {
                            System.out.println("Message: " + e.getMessage());
                            System.out.println("SQLState: " + e.getSQLState());
                            System.out.println("ErrorCode: " + e.getErrorCode() + "\n");
                            e = e.getNextException();
                        }
                    }
                    statement = "";
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + file);
        }
    }

    /**
     * Method to initialize the database connection. Username will be concatenated
     * to the url as well as set in the DB properties. The password is set on the
     * class3 server by the \password command.
     *
     * @param user  the username to connect to
     * @param pass  the password set in psql
     */
    private void initializeDB(String user, String pass) {
        System.out.print("\n > Connecting to database ...");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find class at org.postgresql.Driver!");
        }

        String url = "jdbc:postgresql://localhost/" + user;

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", pass);

        try {
            conn = DriverManager.getConnection(url, props);
            System.out.print(" ...done!\n");
        } catch (SQLException e) {
            System.out.println("Unable to connect to ");
        }
    }

    public static void main(String[] args) {
        Messenger messenger = new Messenger();
    }
}
