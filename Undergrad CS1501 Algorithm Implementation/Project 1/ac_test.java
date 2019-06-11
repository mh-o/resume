import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.DecimalFormat;

public class ac_test {
	private static String line;																						// String to hold each word from dictionary.txt
	private static DLB<Character, String> trie;
	private static DLB<Character, String> userTrie;
	//private static ArrayList<String> timingOps = new ArrayList<String>();
	
	public static void main(String[] args) {
		System.out.print("\nBuilding DLB...");																		// Notify we begin building DLB
		long startTime = System.nanoTime();																			// DEBUG Begin timing building DLB
		boolean hasUserHistory = false;
		boolean hasPrefix = true;
		boolean hasUserPrefix = true;
		
		userTrie = new DLB<Character, String>();
		
		try {
			File userHistory = new File("user_history.txt");
			FileReader fileReader = new FileReader(userHistory);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			hasUserHistory = true;
			
			while((line = bufferedReader.readLine()) != null) {
				BuildDLB.addWord(userTrie, line);
			}
		} catch (IOException e) {
			System.out.println("\n\nNo 'user_history.txt' was found. Populating results strictly from 'dictionary.txt' DLB.\n");
			hasUserHistory = false;
		}
		
		try {
			File myDictionary = new File("dictionary.txt");
			FileReader fileReader = new FileReader(myDictionary);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			trie = new DLB<Character, String>();																	// Initialize DLB trie
			
			while((line = bufferedReader.readLine()) != null) {														// For each word in dictionary.txt
				BuildDLB.addWord(trie, line);																		// Add word to the trie (detailed in DLB)
			}
		} catch (IOException e) {
			System.out.println("Error. 'dictionary.txt' was not found.");
		}
		
		long endTime = System.nanoTime();																			// DEBUG Finish timing building DLB
		long duration = (endTime - startTime);																		// DEBUG Calculate duration
		long seconds = (duration / 1000000);																		// DEBUG convert to MS
		double mSeconds = 0.0000;
		double avgTime = 0.0000;
		int avgDiv = 0;
		
		System.out.println("...finished in " + seconds + " milliseconds!\n");										// Notify that DLB has been constructed, in X milliseconds
		
		boolean flag1 = true;
		boolean flag2 = true;
		
		while(flag1 == true) {																						// This is a rather unitelligent way of looping,
			flag2 = true;																							// but making this part a function kept giving me errors :/
			
			while (flag2 != false) {
				hasPrefix = true;
				hasUserPrefix = true;
				Scanner sc = new Scanner(System.in);
				System.out.print("Enter your first character: ");													// Prompt for first character
				String myChar = sc.nextLine();																		// Store in myChar
	
				if(myChar.charAt(0) == '!') {
					flag1 = false;
					flag2 = false;
					break;
				}
	
				startTime = System.nanoTime();																		// Begin timing our search
		
				DLBNode<Character, String> node = trie.getRootNode();												// Initialize root node

				try {
					node = findCharacter(node, myChar.charAt(0), hasPrefix);										// Current node is now our character
				} catch(StringIndexOutOfBoundsException e) {
					System.out.println("\nOops! It looks like you didn't enter a valid character.\n");
					break;
				}
		
				DLBNode<Character, String> node2 = userTrie.getRootNode();
				DLBNode<Character, String> tempNode = node2;			
				
				try {
					tempNode = findCharacter(node2, myChar.charAt(0), hasPrefix);
				} catch (StringIndexOutOfBoundsException e) {
					System.out.println("caught");
					hasUserHistory = false;
				}
				
				if(tempNode == null) {
					hasUserPrefix = false;
				} else {
					node2 = tempNode;
				}
				
				endTime = System.nanoTime();																		// Stop timing our search
				duration = ((endTime - startTime));
				int intTime = Math.toIntExact(duration);
				String timeToAdd = Integer.toString(intTime);
				mSeconds = (duration / 1000000.0000);
				avgTime = avgTime + mSeconds;
				avgDiv = avgDiv + 1;
				
				//timingOps.add(timeToAdd);
				
				System.out.println("\nFound suggestions in " + mSeconds + " milliseconds:\n");
				System.out.println("---------------------------------------");

				if(hasUserHistory == true & hasPrefix == true & hasUserPrefix == true) {							// If a user_history.txt exists...
					BuildDLB.printValues(node2, 5, hasUserHistory);													// Then we need to print node2 val first
				}

				if(hasPrefix == true) {
					BuildDLB.printValues(node, 1, hasUserHistory);
				}
		
				boolean foundWord = false;
		
				while(foundWord == false) {
					System.out.println("---------------------------------------");
					System.out.print("\nEnter your next character: ");
					myChar = sc.nextLine();
					
					if(myChar.charAt(0) == '!') {
						flag1 = false;
						flag2 = false;
						break;
					}
			
					boolean isAnInt = false;
					int userChoice = 0;
			
					try {
						userChoice = Integer.parseInt(myChar);
						isAnInt = true;
					} catch(NumberFormatException e) {
						isAnInt = false;
					}
			
					if(isAnInt == true) {
						String chosenWord = BuildDLB.getArrayWord(userChoice);
						System.out.println("WORD COMPLETED: " + chosenWord);
						BuildDLB.addWord(userTrie, chosenWord);
				
						if((new File("user_history.txt").isFile()) == false) {
							try {
								PrintWriter writer = new PrintWriter("user_history.txt", "UTF-8");
								writer.println(chosenWord);
								writer.close();
								break;
							} catch (IOException e) {
						
							}
						} else {
							try(FileWriter fw = new FileWriter("user_history.txt", true);
							BufferedWriter bw = new BufferedWriter(fw);
							PrintWriter writer = new PrintWriter(bw)) {
								writer.println(chosenWord);
								writer.close();
								break;
							} catch (IOException e) {
								System.out.println("Could not write to file.");
							}
						}
					} else {
			
						startTime = System.nanoTime();
				
						try {
							node = findCharacter(node, myChar.charAt(0), hasPrefix);
						} catch(StringIndexOutOfBoundsException e) {
							hasPrefix = false;
							System.out.println("\nOops! It appears you didn't enter a valid character...\n");
							flag2 = false;
							break;
						}
				
						try {
							tempNode = findCharacter(node2, myChar.charAt(0), hasPrefix);
						} catch (StringIndexOutOfBoundsException e) {
							System.out.println("caught");
							hasUserHistory = false;
						}
				
						if(tempNode == null) {
							hasUserPrefix = false;
						} else {
							node2 = tempNode;
						}
				
						endTime = System.nanoTime();																// Stop timing our search
						duration = ((endTime - startTime));
						intTime = Math.toIntExact(duration);
						timeToAdd = Integer.toString(intTime);
						mSeconds = (duration / 1000000.0000);
						avgTime = avgTime + mSeconds;
						avgDiv = avgDiv + 1;
						
						//timingOps.add(timeToAdd);
			
						System.out.println("\nFound suggestions in " + mSeconds + " milliseconds:\n");
						System.out.println("---------------------------------------");
						BuildDLB.clearArray();																		// Clear array that holds words user might want to pick
						
						if(hasUserHistory == true & hasPrefix == true & hasUserPrefix == true) {															// If a user_history.txt exists...
							BuildDLB.printValues(node2, 1, hasUserHistory);															// Then we need to print node2 val first
						}
						
						if(hasPrefix == true) {
							BuildDLB.printValues(node, 5, hasUserHistory);
						}
					}
				}
			}
			flag2 = false;
		}
		
		double avgSearchTime = (avgTime / avgDiv);
		
		System.out.println("\nGoodbye! Your average search time was " + avgSearchTime + " milliseconds!");
	}
	
	private static DLBNode<Character, String> findCharacter(DLBNode<Character, String> currentNode, Character ch, boolean hasPrefix) {
		if (ch.equals(currentNode.getKey()) && !ch.equals('#')) {													// If the character matches the key and is not the terminal symbol
			currentNode = currentNode.getNextList();																// Sets the node to be returned as the next linked list of the trie
		}
		else if (ch.equals(currentNode.getKey()) && ch.equals('#')) {												// If the character matches the key and is the terminal symbol
			return currentNode;																						// Returns the current node (to pull the password from it)
		}
		else if (currentNode.hasNextNode()) {																		// If the key and character do not match and the current node has an adjacent node
			currentNode = currentNode.getNextNode();
			currentNode = findCharacter(currentNode, ch, hasPrefix);															// Searches for the character in the next node of the linked list
		}
		else {																										// Otherwise, the password is invalid as there are no more characters to search for
			//System.out.println("Word not found.");
			currentNode = null;
		}
		
		return currentNode;																							// Returns a node from the trie
	}
}




