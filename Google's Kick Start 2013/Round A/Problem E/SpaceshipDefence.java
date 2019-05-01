/*
 * Compile: `javac SpaceshipDefence.java`
 * Execute: `java SpaceshipDefence < infile > outfile`
 * Execute (powershell):
 * > Get-Content test.in | java SpaceshipDefence | Out-File -Filepath output.txt -Encoding ASCII
 */

 import java.util.*;
 import java.io.*;

 public class SpaceshipDefence {
   static Scanner in;
   static ArrayList<Room> rooms;
   static ArrayList<Turbolift> turbolifts;
   static ArrayList<Soldier> soldiers;

   static int INFINITY = 2147483647;

   static Map<Room, Integer> total_time;
   static Map<Room, Room> prev_rooms;
   static PriorityQueue<Room> min_pq;
   static Set<Room> visited;

   public static void main(String[] ars) {
     in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

     int num_test_cases = in.nextInt();
     in.nextLine();

     for (int i = 0; i < 1; i++) {
       testCase();
     }
   }

   private static void testCase() {
     int num_rooms = in.nextInt();
     in.nextLine();
     addRooms(num_rooms);
     roomsToString();

     int num_turbolifts = in.nextInt();
     in.nextLine();
     addTurbolifts(num_turbolifts);
     turboliftsToString();

     int num_soldiers = in.nextInt();
     in.nextLine();
     addSoldiers(num_soldiers);
     soldiersToString();

     Dijkstra(rooms.get(soldiers.get(0).getP() - 1));
   }

   private static void addRooms(int num_rooms) {
     rooms = new ArrayList<Room>();

     for (int i = 0; i < num_rooms; i++) {
       rooms.add(new Room(i, in.nextLine()));
     }
   }

   private static void roomsToString() {
     for (int i = 0; i < rooms.size(); i++) {
       System.out.print("Room " + rooms.get(i).getID() + ": ");
       System.out.println(rooms.get(i).getColor());
     }
   }

   private static void addTurbolifts(int num_turbolifts) {
     turbolifts = new ArrayList<Turbolift>();

     for (int i = 0; i < num_turbolifts; i++) {
       turbolifts.add(new Turbolift(in.nextLine()));
     }
   }

   private static void turboliftsToString() {
     for (int i = 0; i < turbolifts.size(); i++) {
       System.out.print("Turbolift " + i + ": ");
       System.out.print(turbolifts.get(i).getA() + " to ");
       System.out.print(turbolifts.get(i).getB() + " in ");
       System.out.println(turbolifts.get(i).getT() + " seconds");
     }
   }

   private static void addSoldiers(int num_soldiers) {
     soldiers = new ArrayList<Soldier>();

     for (int i = 0; i < num_soldiers; i++) {
       soldiers.add(new Soldier(in.nextLine()));
     }
   }

   private static void soldiersToString() {
     for (int i = 0; i < soldiers.size(); i++) {
       System.out.println("hi");
       System.out.print("Soldier " + (i + 1) + ": ");
       System.out.print("p: " + soldiers.get(i).getP());
       System.out.println(", q: " + soldiers.get(i).getQ());
     }
   }

   private static void Dijkstra(Room start) {

   }
 }
