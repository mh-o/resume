///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////---->>>>           BuildDLB.java - - BuildDLB.java - - BuildDLB.java - - BuildDLB.java - - BuildDLB.java - - BuildDLB.java - - BuildDLB.java - - BuildDLB.java - - BuildDLB.java           <<<<----////
////																																																   ////
////-->>	A class to add words to a DLB, and add termination characters.																															   ////																																									   ////
////																																																   ////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;

public class BuildDLB {
	public static void addWord(DLB<Character, String> trie, String word) {											// Add a word to our DLB (called in ac_test.java to build DLB)
		DLBNode<Character, String> currentNode, rootNode;															// Initialize current and root nodes
		
		rootNode = trie.getRootNode();																				// Gets the first key of the trie
		currentNode = rootNode;																						// Sets the current node to the root
		
		for (int i = 0; i < word.length(); i++) {																	// For each character in the word...
			currentNode = addCharacter(word.charAt(i), currentNode);												// Set character to current node
		}
		
		addSentinelNode(word, currentNode);																			// Adds the sentinel node and sets its value (end-of-path)
	}

	private static DLBNode<Character, String> addCharacter(Character ch, DLBNode<Character, String> node) {			// Add character to key of node in DLB
		if (node.hasNoKey()) {																						// If the node does not have a key...
			node.setKey(ch);																						// Set key to the character
			node.setNextList(new DLBNode<Character, String>());
			node = node.getNextList();																				// Will return the node of the next linked list
		}
		else if (ch.equals(node.getKey())) {																		// If the character == the key of our node...
			node = node.getNextList();																				// Return node of next linked list (move right)
		}
		else if (node.hasNextNode()) {																				// If the character != the key and we have a node to the right...
			node = node.getNextNode();
			node = addCharacter(ch, node);																			// Recursively attempt to add the key to the node to the right
		}
		else {																										// If the character != the key and we have a node to the right...
			node.setNextNode(new DLBNode<Character, String>());														// Create new node (linked list) to the right
			node = node.getNextNode();																				// Set current node to that node
			node = addCharacter(ch, node);																			// Call method to add character to new node
		}
		
		return node;																								// Return the next node to search from
	}
	
	public static DLBNode<Character, String> resetRoot(DLBNode<Character, String> node) {
		while (node.hasLastNode()) {
			//while (node.hasLastList()) {
				//node = node.getLastList();
			//}
			node = node.getLastNode();
		}
		
		return node;
	}
	
	private static void addSentinelNode(String word, DLBNode<Character, String> node) {								// Add terminator
		Character sentinelChar = '#';
		node.setKey(sentinelChar);																					// Sets key to '#'
		node.setValue(word);																						// Store the word in value so we can get it
	}

	public static boolean isTerminalNode(DLBNode<Character, String> node) {											// Determine if we have a word
		return (node.getKey().equals('#'));																			// Return true if key is '#'
	}
	
	public static void printValues(DLBNode<Character, String> node, int valuesToPrint, boolean hasUserHistory) {	// Print strings from trie given (node, #ofStrings)
		while (node.hasLastNode()) {																				// Traverses to bottom of our nodes
			node = node.getLastNode();
		}
		
		valuesToPrint = printSingleValue(node, valuesToPrint, hasUserHistory);										// Prints out the values of the trie
		
		if (valuesToPrint > 0) {																					// If all strings aren't printed...
			while (node.hasLastNode()) {																			// Returns to the head of the linked list
				node = node.getLastNode();
			}
			
			node = node.getLastList();																				// Traverses to the previous linked list
			
			if (node.hasNextNode()) {																				// Gets the next node if it has one
				node = node.getNextNode();
			}
			else {																									// Otherwise, gets the last node
				node = node.getLastNode();
			}
			
			printSingleValue(node, valuesToPrint, hasUserHistory);													// Prints the remaining strings requested
		}
	}
	
	public static void printUserValues(DLBNode<Character, String> node, int valuesToPrint, boolean hasUserHistory) {	// Print strings from trie given (node, #ofStrings)
		while (node.hasLastNode()) {																				// Traverses to bottom of our nodes
			node = node.getLastNode();
		}
		
		valuesToPrint = printSingleValue(node, valuesToPrint, hasUserHistory);										// Prints out the values of the trie
		
		if (valuesToPrint > 0) {																					// If all strings aren't printed...
			while (node.hasLastNode()) {																			// Returns to the head of the linked list
				node = node.getLastNode();
			}
			
			node = node.getLastList();																				// Traverses to the previous linked list
			
			if (node.hasNextNode()) {																				// Gets the next node if it has one
				node = node.getNextNode();
			}
			else {																									// Otherwise, gets the last node
				node = node.getLastNode();
			}
			
			printSingleValue(node, valuesToPrint, hasUserHistory);													// Prints the remaining strings requested
		}
	}

	public static ArrayList<String> myList = new ArrayList<String>();
	
	public static void arrayBuilder(String myString) {																// Build an array if the user selects a word we display
		myList.add(myString);
		return;
	}
	
	public static void clearArray() {																				// If we generate 5 more words, we need to clear the array
		myList.clear();
		return;
	}
	
	public static String getArrayWord(int index) {
		String myString = myList.get(index - 1);
		return myString;
	}
	
	private static int printSingleValue(DLBNode<Character, String> node, int valuesToPrint, boolean hasUserHistory) {// Print actual word from trie	
		if (!node.hasNoKey() && valuesToPrint > 0) {																// If the node has a key and there are still strings to print
			if (node.hasNextList()) {																				// Traverses to the next linked list node if it exists
				valuesToPrint = printSingleValue(node.getNextList(), valuesToPrint, hasUserHistory);								// Calls the print function of this node
				if (0 == valuesToPrint) {																			// Returns if there are no strings left to print
					return valuesToPrint;
				}
			}
			if (node.hasNextNode()) {																				// Traverses to the next node if it exists
				valuesToPrint = printSingleValue(node.getNextNode(), valuesToPrint, hasUserHistory);								// Calls the print function of this node
				if (0 == valuesToPrint) {																			// Returns if there are no strings left to print
					return valuesToPrint;
				}
			}
			if (isTerminalNode(node)) {																				// Conditional if the node is a terminal node
				int labelInt = 0;
	
				if(hasUserHistory == true) {
					labelInt = Math.abs(valuesToPrint - 7);															// Convert our 
				} else {
					labelInt = Math.abs(valuesToPrint - 6);
				}

				System.out.print("\t(" + labelInt + ") ");
				System.out.printf("%s\n", node.getValue());															// Prints out the string of the node
				String myString = node.getValue();
				arrayBuilder(myString);
				valuesToPrint--;																					// Decrements the number of strings to print
			}
			
			return valuesToPrint;
		}
		
		return valuesToPrint;
	}
}