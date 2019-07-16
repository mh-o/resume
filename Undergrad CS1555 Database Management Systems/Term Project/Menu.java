public class Menu {
    public static void title() {
        System.out.println("\n");
        System.out.println("===================================================================");
        System.out.println("-------------------------------------------------------------------");
        System.out.println("   _____                                                           ");
        System.out.println("  /     \\   ____   ______ ______ ____   ____    ____   ___________ ");
        System.out.println(" /  \\ /  \\_/ __ \\ /  ___//  ___// __ \\ /    \\  / ___\\_/ __ \\_  __ \\");
        System.out.println("/    Y    \\  ___/ \\___ \\ \\___ \\\\  ___/|   |  \\/ /_/  >  ___/|  | \\/");
        System.out.println("\\____|__  /\\___  >____  >____  >\\___  >___|  /\\___  / \\___  >__|   ");
        System.out.println("        \\/     \\/     \\/     \\/     \\/     \\//_____/      \\/       ");
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println("===================================================================\n");
    }

    public static void options() {
        System.out.println("======>>>>>>-----    PLEASE SELECT AN ACTION:     -----<<<<<<======\n");
        System.out.println(" ---> 1) <CREATE NEW USER>");
        System.out.println(" ---> 2) <ADD TO CONTACTS>");
        System.out.println(" ---> 3) <VIEW CONTACTS>");
        System.out.println(" ---> 4) <CREATE NEW GROUP CHAT>");
        System.out.println(" ---> 5) <ADD USER TO GROUP CHAT>");
        System.out.println(" ---> 6) <REMOVE USER FROM GROUP CHAT>");
        System.out.println(" ---> 7) <SEND MESSAGE TO USER>");
        System.out.println(" ---> 8) <SEND MESSAGE TO GROUP CHAT>");
        System.out.println(" ---> 9) <DISPLAY MESSAGES FOR A USER>");
        System.out.println(" ---> 10) <DISPLAY NEW MESSAGES FOR A USER>");
        System.out.println(" ---> 11) <FIND THREE DEGREES FRIENDSHIP>");
        System.out.println(" ---> 12) <DISPLAY MOST POPULAR USERS>");
        System.out.println(" ---> 13) <DISPLAY MOST VOCAL USERS>");
        System.out.println(" ---> 14) <DROP A USER'S DATA>");

        System.out.println();
        System.out.println(" ---> 15) <TEST PROGRAM>");

        System.out.println();
        System.out.println(" ---> 16) <EXIT>");
    }

    public static void createUser() {
        System.out.println("\n======>>>>>>-----         CREATE NEW USER         -----<<<<<<======\n");
    }

    public static void addToContacts() {
        System.out.println("\n======>>>>>>-----         ADD TO CONTACTS         -----<<<<<<======\n");
    }

    public static void displayContacts() {
        System.out.println("\n======>>>>>>-----           VIEW CONTACTS         -----<<<<<<======\n");
    }

    public static void createGroupChat() {
        System.out.println("\n======>>>>>>-----       CREATE NEW GROUP CHAT     -----<<<<<<======\n");
    }

    public static void addToGroupChat() {
        System.out.println("\n======>>>>>>-----        ADD TO GROUP CHAT        -----<<<<<<======\n");
    }

    public static void leaveGroupChat() {
        System.out.println("\n======>>>>>>-----   REMOVE USER FROM GROUP CHAT   -----<<<<<<======\n");
    }

    public static void sendMessageToUser() {
        System.out.println("\n======>>>>>>-----   SEND A MESSAGE TO A USER   -----<<<<<<======\n");
    }

    public static void sendMessageToGroupChat() {
        System.out.println("\n======>>>>>>-----   SEND A MESSAGE TO A GROUP CHAT   -----<<<<<<======\n");
    }

    public static void displayMessages() {
        System.out.println("\n======>>>>>>-----   DISPLAY MESSAGES FOR A USER   -----<<<<<<======\n");
    }

    public static void displayNewMessages() {
        System.out.println("\n======>>>>>>-----   DISPLAY NEW MESSAGES FOR A USER   -----<<<<<<======\n");
    }

    public static void threeDegrees() {
        System.out.println("\n======>>>>>>-----   DISPLAY THREE DEGREES FRIENDSHIP   -----<<<<<<======\n");
    }

    public static void mostPopular() {
        System.out.println("\n======>>>>>>-----   DISPLAY MOST POPULAR USERS  -----<<<<<<======\n");
    }

    public static void mostVocal() {
        System.out.println("\n======>>>>>>-----   DISPLAY MOST VOCAL USERS  -----<<<<<<======\n");
    }

    public static void dropUser() {
        System.out.println("\n======>>>>>>-----   DROP A USER'S DATA  -----<<<<<<======\n");
    }
}
