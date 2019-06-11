///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////---->>>>       DLBNode.java - - DLBNode.java - - DLBNode.java - - DLBNode.java - - DLBNode.java - - DLBNode.java - - DLBNode.java - - DLBNode.java - - DLBNode.java - - DLBNode.java       <<<<----////
////																																																   ////
////-->>	This class represents each DLB node. Directional terms may be understood with the following visualization in mind.																		   ////
////-->>	Say we add the words "SHE", "SELLS", and "SEA" in that order, then our DLB can be visualized as such:																					   ////
////																																																   ////
////		S				-->>	Where '#' represents the termination character																													   ////
////		|				-->>	Note how 'S' is "above" 'H' (lastNode)																															   ////
////		H---E			-->>	Note how 'E' is "below" 'H' (nextNode)																															   ////
////		|   |			-->>	Note how 'A' is "right" of 'L' (nextList)																														   ////
////		E   L---A		-->>	Note how 'L' is "left" pf 'A' (lastList)																														   ////
////		|   |	|																																												   ////
////		#   L	#																																												   ////
////			|																																													   ////
////			L																																													   ////
////			|																																													   ////
////			S																																													   ////
////			|																																													   ////
////			#																																													   ////
////																																																   ////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class DLBNode<S, T> {																						// This has all info for our DLB nodes
	private S key;																									// The key of the node
	private T value;																								// The value of completed node path
	private DLBNode<S, T> nextNode, nextList, lastNode, lastList;													// Holds references to adjacent nodes
	
	public DLBNode() {																								// Create new trie node with null values
		key = null;																									//					||
		value = null;																								//					||
		nextNode = null;																							//					||
		nextList = null;																							//				   _||_
		lastNode = null;																							//				   \  /
		lastList = null;																							//					\/
	}
	
	public DLBNode(DLBNode<S, T> node) {																			// Create new trie node via copy
		key = node.getKey();																						//				||
		value = node.getValue();																					//				||
		nextNode = node.getNextNode();																				//				||
		nextList = node.getNextList();																				//			   _||_
		lastNode = node.getLastNode();																				//			   \  /
		lastList = node.getLastList();																				//				\/
	}

	public S getKey() {																								// Returns a node's key
		return key;
	}

	public void setKey(S keyValue) {																				// Set a node's key
		key = keyValue;
	}

	public boolean hasNoKey() {																						// Return true if a node's key is null
		return (key == null);
	}
	
	public boolean hasValue() {																						// Return true if a node's value isn't null
		return (value != null);
	}
	
	public T getValue() {																							// Return node's value
		return value;
	}
	
	public void setValue(T val) {																					// Set node's value
		value = val;
	}

	public boolean hasNextNode() {																					// Check if a node has a nextNode reference (trie extends vertically down)
		return (nextNode != null);																					// Return true if it does
	}

	public boolean hasNextList() {																					// Check if a node has a nextList reference (trie extends horizontally right)
		return (nextList != null);																					// Return true if it does
	}

	public boolean hasLastNode() {																					// Check if a node has a lastNode reference (trie extends vertically up)
		return (lastNode != null);																					// Return true if it does
	}

	public boolean hasLastList() {																					// Check if a node has a lastList reference (trie extends horizontally left)
		
		return (lastList != null);
	}

	public DLBNode<S, T> getNextNode() {																			// Return reference for the node below	
		return nextNode;
	}
	
	public DLBNode<S, T> getNextList() {																			// Return the reference for the node to the right
		return nextList;
	}

	public DLBNode<S, T> getLastNode() {																			// Return the reference for the node above
		return lastNode;
	}
	
	public DLBNode<S, T> getLastList() {																			// Return the reference for the node to the left
		return lastList;
	}

	public void setNextNode(DLBNode<S, T> node) {																	// Set reference for node below
		nextNode = node;
		node.setLastNode(this);
	}

	public void setNextList(DLBNode<S, T> list) {																	// Set reference for node to the right
		nextList = list;
		list.setLastList(this);
	}
	
	public void setLastNode(DLBNode<S, T> node) {																	// Set reference for node above
		lastNode = node;
	}
	
	public void setLastList(DLBNode<S, T> list) {																	// Set reference for node to the left
		lastList = list;
	}
}