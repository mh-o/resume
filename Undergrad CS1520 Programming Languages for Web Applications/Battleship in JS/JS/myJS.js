var shipArray1 = [];
var shipArray2 = [];
var shipString1 = "";
var shipString2 = "";
var p1A = [], p1B = [], p1S = [];
var p2A = [], p2B = [], p2S = [];
var p1A_F = [], p1B_F = [], p1S_F = [];
var p2A_F = [], p2B_F = [], p2S_F = [];
var p1A_H = 0, p1B_H = 0, p1S_H = 0;
var p2A_H = 0, p2B_H = 0, p2S_H = 0;
var p1Score = 24, p2Score = 24;
var highScores = [];
var playingAgain = false;
var rows = 11;
var cols = 11;
var squareSize = 35;
var theGridContainer = document.getElementById("theGrid");
var theGridContainer2 = document.getElementById("theGrid2");
var hitCount1 = 0;
var hitCount2 = 0;
var turnMarker = 1;
var player1 = "", player2 = "";
var p1FirstTurn = true, p2FirstTurn = true;

var theGrid = [
					[4,4,4,4,4,4,4,4,4,4,4],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
				]
					
var theGrid2 = [
					[0,0,0,0,0,0,0,0,0,0,0],
					[0,0,0,0,0,0,0,0,0,0,0],
					[0,0,0,0,0,0,0,0,0,0,0],
					[0,0,0,0,0,0,0,0,0,0,0],
					[0,0,0,0,0,0,0,0,0,0,0],
					[0,0,0,0,0,0,0,0,0,0,0],
					[0,0,0,0,0,0,0,0,0,0,0],
					[0,0,0,0,0,0,0,0,0,0,0],
					[0,0,0,0,0,0,0,0,0,0,0],
					[0,0,0,0,0,0,0,0,0,0,0],
					[0,0,0,0,0,0,0,0,0,0,0],
				]
	
var p1theGrid = [
					[4,4,4,4,4,4,4,4,4,4,4],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
				]
				
var p2theGrid = [
					[4,4,4,4,4,4,4,4,4,4,4],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
				]

playTheGame();

////////////////////////////////////////////////////////////////////////
////----PLAYTHEGAME----PLAYTHEGAME----PLAYTHEGAME----PLAYTHEGAME----////
function playTheGame() {
	readScores();
	
	player1 = prompt("Player 1, please enter your name:", "Player 1");
	shipString1 = prompt(player1 + ", please enter your ship placement:", "A:A1-A5; B:B6-E6; S:H3-J3");
	shipArray1 = checkShipString(shipString1, shipArray1);
	
	player2 = prompt("Player 2, please enter your name:", "Player 2");
	shipString2 = prompt(player2 + ", please enter your ship placement:", "A:E8-I8; B:B1-E1; S:I2-I4");
	shipArray2 = checkShipString(shipString2, shipArray2);
	
	if(playingAgain == false) {
		buildTheGrid();																						// Construct empty grids
	}
	
	parseShipArray();																					// Convert ship strings into points on the grid for ship start/end points
	
	p1A_F = getShipLocation(p1A, 5);																	// Get the location for p1's aircraft carrier
	p1B_F = getShipLocation(p1B, 4);																	// Get the location for p1's battleship
	p1S_F = getShipLocation(p1S, 3);																	// Get the location for p1's submarine
	
	p2A_F = getShipLocation(p2A, 5);																	// Get the location for p2's aircraft carrier
	p2B_F = getShipLocation(p2B, 4);																	// Get the location for p2's battleship
	p2S_F = getShipLocation(p2S, 3);																	// Get the location for p2's submarine
	
	theGridContainer.addEventListener("click", playerClick, false);
	turnLoop();
}
////----PLAYTHEGAME----PLAYTHEGAME----PLAYTHEGAME----PLAYTHEGAME----////
////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////
////----PLAYAGAIN----PLAYAGAIN----PLAYAGAIN----PLAYAGAIN----////
function playAgain() {
	if(confirm("Do you want to play again?")) {
		clearTheGrid("s");
		clearTheGrid("q");
	
		theGrid = [
					[4,4,4,4,4,4,4,4,4,4,4],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
				]
		theGrid2 = [
					[4,4,4,4,4,4,4,4,4,4,4],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
					[4,0,0,0,0,0,0,0,0,0,0],
				]
				
		hitCount1 = 0;
		hitCount2 = 0;
		p1A_H = 0;
		p1B_H = 0;
		p1S_H = 0;
		p2A_H = 0;
		p2B_H = 0;
		p2S_H = 0;
		
		turnMarker = 1;
		p1FirstTurn = true;
		p2FirstTurn = true;
		playingAgain = true;
	
		playTheGame();
	} else {
		// do nothing
	}
	return;
}
////----PLAYAGAIN----PLAYAGAIN----PLAYAGAIN----PLAYAGAIN----////
////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////
////----TURNLOOP----TURNLOOP----TURNLOOP----TURNLOOP----////
function turnLoop() {
	if (hitCount1 == 12) {
		p1Score = p1Score - (2 * hitCount2);
		alert(player1 + ", you won with a score of " + p1Score + "!");
		storeScore(player1, p1Score);
		turnMarker = 3;
	} else if (hitCount2 == 12) {
		p2Score = p2Score - (2 * hitCount1);
		alert(player2 + ", you won with a score of " + p2Score + "!");
		storeScore(player2, p2Score);
		turnMarker = 3;
	}
	
	if(turnMarker == 1) {
		takeTurn(1);
	} else if(turnMarker == 2) {
		takeTurn(2);
	} else if(turnMarker == 3) {
		playAgain();
	}
}
////----TURNLOOP----TURNLOOP----TURNLOOP----TURNLOOP----////
////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////
////----READSCORES----READSCORES----READSCORES----READSCORES----////
function readScores() {
	if(typeof(Storage) !== "undefined") {
		//alert("Reading scores...");
		for(i = 1; i < 11; i++) {
			myID = i;
			myVar = localStorage.getItem(myID);
			if(myVar !== null) {
				var nameAndScore = myVar.split("#");
				highScores.push(nameAndScore);
			}
		}
		//alert("myVar: " + myVar);
		//alert("array: " + highScores);
	} else {
		alert("Your browser does not support local storage. Scores could not be read.")
	}
}
////----READSCORES----READSCORES----READSCORES----READSCORES----////
////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////
////----STORESCORE----STORESCORE----STORESCORE----STORESCORE----////
function storeScore(myPlayerName, myPlayerScore) {
	if(typeof(Storage) !== "undefined") {
		var myMin = 24;
		var myValue = 0;
		var minLocation = 0;
		if(localStorage.getItem(10) != null) {															// If we have 10 scores in local storage...
			//alert("10 scores");
			myMin = 24;
			for(i = 1; i < 11; i ++) {
				myID = i;
				myVar = localStorage.getItem(myID);
				var tempArray = myVar.split("#");
				myValue = tempArray[0];
				//alert(myValue);
				if(myValue < myMin) {
					myMin = myValue;
					minLocation = i;
				}
			}
			if(myMin !== 24) {
				var stringToStore = myPlayerScore + "#" + myPlayerName;
				localStorage.setItem(minLocation, stringToStore);
			}
		} else {																						// If we have < 10 scores...
			for(i = 1; i < 11; i++) {
				myID = i;
				myVar = localStorage.getItem(myID);
				if(myVar !== null) {
					i = i;
				} else {
					var stringToStore = myPlayerScore + "#" + myPlayerName;
					localStorage.setItem(i, stringToStore);
					i = 11;
				}
			}
		}
	} else {
		alert("Your browser does not support local storage. Scores will not be saved.");
	}
	return;
}
////----STORESCORE----STORESCORE----STORESCORE----STORESCORE----////
////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////
////----CHECKSHIPSTRING----CHECKSHIPSTRING----CHECKSHIPSTRING----CHECKSHIPSTRING----////
function checkShipString(shipString, shipArray) {
	var myPattern = new RegExp(/[abs][:(][a-j][0-9][-][a-j][0-9][)]*[;]/i);								// Our REGEX for a single ship
	var fullPattern = new RegExp(myPattern.source + myPattern.source + myPattern.source);				// Concatenate for 3 ships
	var myTest = myPattern.test(shipString);															// Test player input string
	if(myTest == false) {																				// If the string doesn't pass the test...
		alert("It looks like you didn't enter valid ship locations... try again!")						// Inform the player
		return;
	} else {
		for(i = 0; i < shipString.length; i ++) {
			if (shipString.charAt(i+1) == ":" | shipString.charAt(i+1) == "(") {
				shipArray.push(shipString.charAt(i));													// Push ship label
				shipArray.push(shipString.charAt(i+2));													// Push character axis label
				if(shipString.charAt(i+3) == "1" & shipString.charAt(i+4) == "0") {						// Handle the 10 case
					shipArray.push("10");																// Push 10
					i = i+1;																			// We need to incriment i since we encounted a 10 and we have an extra digit
				} else {																				// If not 10 case...
				shipArray.push(shipString.charAt(i+3));													// Push integer label
				}
				shipArray.push(shipString.charAt(i+5));													// Push second character axis label
				if(shipString.charAt(i+5) == "1" & shipString.charAt(i+6) == "0") {						// Handle the 10 case
					shipArray.push("10");																// Push 10
					i = i+1;																			// We need to incriment i since we encounted a 10 and we have an extra digit
				} else {																				// If not 10 case...
					shipArray.push(shipString.charAt(i+6));												// Push integer label
				}
				i = i+6;																				// Skip to the next ship
			}
		}
		return shipArray;
	}
}
////----CHECKSHIPSTRING----CHECKSHIPSTRING----CHECKSHIPSTRING----CHECKSHIPSTRING----////
////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////
////----PARSESHIPARRAY----PARSESHIPARRAY----PARSESHIPARRAY----PARSESHIPARRAY----////
function parseShipArray() {
	for (i = 0; i < shipArray1.length; i++) {															// For each character in our ship array...
		if (shipArray1[i] == "A") {																		// Check if this is Aircraft Carrier
			temp = shipArray1[i+1];																		// Create temp of first character coord
			temp2 = shipArray1[i+3];																	// Create temp of second character coord
			p1A.push(temp.charCodeAt(0) - 64);															// Translate character to grid position and push
			p1A.push(shipArray1[i+2]);																	// Push number coord
			p1A.push(temp2.charCodeAt(0) - 64);															// Translate character to grid position and push
			p1A.push(shipArray1[i+4]);																	// Push number coord
		} else if (shipArray1[i] == "B") {																// Repeat check for Battleship
			temp = shipArray1[i+1];
			temp2 = shipArray1[i+3];
			p1B.push(temp.charCodeAt(0) - 64);
			p1B.push(shipArray1[i+2]);
			p1B.push(temp2.charCodeAt(0) - 64);
			p1B.push(shipArray1[i+4]);
		} else if (shipArray1[i] == "S") {																// Repeat for ship Submarine
			temp = shipArray1[i+1];
			temp2 = shipArray1[i+3];
			p1S.push(temp.charCodeAt(0) - 64);
			p1S.push(shipArray1[i+2]);
			p1S.push(temp2.charCodeAt(0) - 64);
			p1S.push(shipArray1[i+4]);
		}
		i = i+4;																						// Skip to next ship
	}
	for (i = 0; i < shipArray2.length; i++) {															// For each character in our ship array...
		if (shipArray2[i] == "A") {																		// Check if this is Aircraft Carrier
			temp = shipArray2[i+1];																		// Create temp of first character coord
			temp2 = shipArray2[i+3];																	// Create temp of second character coord
			p2A.push(temp.charCodeAt(0) - 64);															// Translate character to grid position and push
			p2A.push(shipArray2[i+2]);																	// Push number coord
			p2A.push(temp2.charCodeAt(0) - 64);															// Translate character to grid position and push
			p2A.push(shipArray2[i+4]);																	// Push number coord
		} else if (shipArray2[i] == "B") {																// Repeat check for Battleship
			temp = shipArray2[i+1];
			temp2 = shipArray2[i+3];
			p2B.push(temp.charCodeAt(0) - 64);
			p2B.push(shipArray2[i+2]);
			p2B.push(temp2.charCodeAt(0) - 64);
			p2B.push(shipArray2[i+4]);
		} else if (shipArray2[i] == "S") {																// Repeat for ship Submarine
			temp = shipArray2[i+1];
			temp2 = shipArray2[i+3];
			p2S.push(temp.charCodeAt(0) - 64);
			p2S.push(shipArray2[i+2]);
			p2S.push(temp2.charCodeAt(0) - 64);
			p2S.push(shipArray2[i+4]);
		}
		i = i+4;																						// Skip to next ship
	}
	return;
}
////----PARSESHIPARRAY----PARSESHIPARRAY----PARSESHIPARRAY----PARSESHIPARRAY----////
////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////
////----GETSHIPLOCATION----GETSHIPLOCATION----GETSHIPLOCATION----GETSHIPLOCATION----////
function getShipLocation(p1A, shipSize) {
	var shipVals = [];
	if(p1A[0] == p1A[2]) {																				// If the ship is veritcal...
		temp = Math.abs(p1A[1] - p1A[3]);
		if(temp != (shipSize - 1)) {
			alert("One or more ships is too long/short!");
		}
		tempMin = p1A[1];
	
		if(parseInt(p1A[3]) < parseInt(p1A[1])) {
			tempMin = p1A[3];
		}
	
		for(i = 0; i < temp+1; i++) {
			shipVals.push(p1A[0]);
			shipVals.push(parseInt(tempMin) + i);
		}
	} else if(p1A[1] == p1A[3]) {																		// If the ship is horizontal...
		temp = Math.abs(p1A[0] - p1A[2]);
		if(temp != (shipSize - 1)) {
			alert("One or more ships is too long/short!");
		}
		tempMin = p1A[0];
	
		if(parseInt(p1A[2]) < parseInt(p1A[0])) {
			tempMin = p1A[2];
		}
	
		for(i = 0; i < temp+1; i++) {
			shipVals.push(parseInt(tempMin) + i);
			shipVals.push(p1A[1]);
		}
	} else {
		alert("Someone didn't enter valid ship locations...")
	}
	return shipVals;
}
////----GETSHIPLOCATION----GETSHIPLOCATION----GETSHIPLOCATION----GETSHIPLOCATION----////
////////////////////////////////////////////////////////////////////////////////////////











////////////////////////////////////////////////////////////////////////////
////----BUILDTHEGRID----BUILDTHEGRID----BUILDTHEGRID----BUILDTHEGRID----////
function buildTheGrid() {
	for(i = 0; i < cols; i++) {
		for(j = 0; j < rows; j++) {
			var square = document.createElement("div");
			theGridContainer.appendChild(square);
		
			square.id = 's' + j + i;			
		
			var topPosition = j * squareSize;
			var leftPosition = i * squareSize;			
		
			square.style.top = topPosition + 'px';
			square.style.left = leftPosition + 'px';						
		}
	}

	for(i = 0; i < cols; i++) {
		for(j = 0; j < rows; j++) {
			var square = document.createElement("div");
			theGridContainer2.appendChild(square);
		
			square.id = 'q' + j + i;			
		
			var topPosition = j * squareSize;
			var leftPosition = i * squareSize;			
		
			square.style.top = topPosition + 'px';
			square.style.left = leftPosition + 'px';						
		}
	}

	document.getElementById("s00").style.background="black";											// Case for upper left square of top grid
	document.getElementById("q00").style.background="black";											// Case for upper left sqaure of bottom grid
					
	for(i = 1; i < cols; i++) {																			// Label each column with a letter and add a white background
		myID = 's' + '0' + i;
		var divv = document.getElementById(myID);
		divv.style.background="lightgrey";
		var myChar = String.fromCharCode(64 + i);
		divv.innerHTML += myChar;
		divv.style.textAlign="center";
		divv.style.fontFamily="Courier New";
		divv.style.fontSize="175%";
		divv.style.fontWeight="bold";
	
		myID = 'q' + '0' + i;
		var divv = document.getElementById(myID);
		divv.style.background="lightgrey";
		var myChar = String.fromCharCode(64 + i);
		divv.innerHTML += myChar;
		divv.style.textAlign="center";
		divv.style.fontFamily="Courier New";
		divv.style.fontSize="175%";
		divv.style.fontWeight="bold";
	}
	for(i = 1; i < rows; i++) {																			// Label each row with a number and add a white background
		myID = 's' + i + '0';
		var divv = document.getElementById(myID);
		divv.style.background="lightgrey";
		divv.innerHTML += i;
		divv.style.textAlign="center";
		divv.style.fontFamily="Courier New";
		divv.style.fontSize="175%";
		divv.style.fontWeight="bold";
	
		myID = 'q' + i + '0';
		var divv = document.getElementById(myID);
		divv.style.background="lightgrey";
		divv.innerHTML += i;
		divv.style.textAlign="center";
		divv.style.fontFamily="Courier New";
		divv.style.fontSize="175%";
		divv.style.fontWeight="bold";
	}
	return;
}
////----BUILDTHEGRID----BUILDTHEGRID----BUILDTHEGRID----BUILDTHEGRID----////
////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////
////----DRAWSHIPS----DRAWSHIPS----DRAWSHIPS----DRAWSHIPS----////
function drawShips(shipLabel, shipLocation) {
	for(i = 0; i < shipLocation.length; i++) {
		myID = "q" + shipLocation[i+1] + shipLocation[i];
		var divv = document.getElementById(myID);
		divv.innerHTML += shipLabel;
		i = i+1;
	}
	
	return;
}
////----DRAWSHIPS----DRAWSHIPS----DRAWSHIPS----DRAWSHIPS----////
////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////
////----POPULATEGRID----POPULATEGRID----POPULATEGRID----POPULATEGRID----////
function populateGrid(myArray, shipLabel) {
	for(i = 0; i < myArray.length; i++) {
		theGrid[myArray[i+1]][myArray[i]] = shipLabel;																	// Set a hidden ship value on the top grid
		i = i+1;
	}
	
	return;
}
////----POPULATEGRID----POPULATEGRID----POPULATEGRID----POPULATEGRID----////
////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////
////----TAKETURN----TAKETURN----TAKETURN----TAKETURN----////
function takeTurn(playerLabel) {
	if(playerLabel == 1) {
		clearTheGrid("s");
		clearTheGrid("q");
		
		setTimeout(function() {
		
		document.getElementById("topHeading").innerHTML = "Your Grid (" + player1 + ")";
		document.getElementById("bottomHeading").innerHTML = "Opponent's (" + player2 + "'s) Grid";
		theGrid = p1theGrid;
		theGrid2 = p2theGrid;
		
		drawShips("A", p1A_F);																							// For p1's turn, we put p1's ships on the bottom grid
		drawShips("B", p1B_F);																							// For p1's turn, we put p1's ships on the bottom grid
		drawShips("S", p1S_F);																							// For p1's turn, we put p1's ships on the bottom grid
		
		if(p1FirstTurn == true) {
			populateGrid(p2A_F, 1);
			populateGrid(p2B_F, 5);
			populateGrid(p2S_F, 6);
			p1FirstTurn = false;
		}
		
		drawHitsAndMisses(1);																							// Draw hits and misses on p1's girds
		
		
		alert(player1 + ", it's your turn.")
		},10)
	} else if(playerLabel == 2) {
		clearTheGrid("s");
		clearTheGrid("q");
		
		setTimeout(function() {
		
		document.getElementById("topHeading").innerHTML = "Your Grid (" + player2 + ")";
		document.getElementById("bottomHeading").innerHTML = "Opponent's (" + player1 + "'s) Grid";
		theGrid = p2theGrid;
		theGrid2 = p1theGrid;
		
		drawShips("A", p2A_F);
		drawShips("B", p2B_F);
		drawShips("S", p2S_F);
		
		if(p2FirstTurn == true) {
			populateGrid(p1A_F, 1);
			populateGrid(p1B_F, 5);
			populateGrid(p1S_F, 6);
			p2FirstTurn = false;
		}
		
		drawHitsAndMisses(2);

		alert(player2 + ", it's your turn.")
		},10)
	}
	return;
}
////----TAKETURN----TAKETURN----TAKETURN----TAKETURN----////
////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////
////----CLEARTHEGRID----CLEARTHEGRID----CLEARTHEGRID----CLEARTHEGRID----////
function clearTheGrid(gridLabel) {
	for(i = 1; i < cols; i++) {
		for(j = 1; j < rows; j++) {
			myID = gridLabel + j + i;
			var divv = document.getElementById(myID);
			divv.innerHTML="";
			divv.style.background="#9AF7FF";
		}
	}
	
	return;
}
////----CLEARTHEGRID----CLEARTHEGRID----CLEARTHEGRID----CLEARTHEGRID----////
////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////
////----DRAWHITSANDMISSES----DRAWHITSANDMISSES----DRAWHITSANDMISSES----DRAWHITSANDMISSES----////
function drawHitsAndMisses(playerLabel) {
	if(playerLabel == 1) {																								// If it's p1's turn...
		for(i = 1; i < cols; i++) {
			for(j = 1; j < rows; j++) {
				myID = "s" + j + i;
				myID2 = "q" + j + i;
				
				if(p1theGrid[j][i] == 3) {																				// If it's a recorded miss
					var divv = document.getElementById(myID);
					divv.style.background="white";																		// Draw miss on top grid
				} else if(p1theGrid[j][i] == 2) {																		// If it's a recorded hit
					var divv = document.getElementById(myID);
					divv.style.background="red";																		// Draw miss on bottom grid
				}
				if(p2theGrid[j][i] == 3) {
					var divv = document.getElementById(myID2);
					divv.style.background="white";
				} else if(p2theGrid[j][i] == 2) {
					var divv = document.getElementById(myID2);
					divv.style.background="red";
				}
			}
		}	
	} else if(playerLabel == 2) {
		for(i = 1; i < cols; i++) {
			for(j = 1; j < rows; j++) {
				myID = "s" + j + i;
				myID2 = "q" + j + i;
				
				if(p2theGrid[j][i] == 3) {																				// If it's a recorded miss
					var divv = document.getElementById(myID);
					divv.style.background="white";																		// Draw miss on top grid
				} else if(p2theGrid[j][i] == 2) {																		// If it's a recorded hit
					var divv = document.getElementById(myID);
					divv.style.background="red";																		// Draw miss on bottom grid
				}
				if(p1theGrid[j][i] == 3) {
					var divv = document.getElementById(myID2);
					divv.style.background="white";
				} else if(p1theGrid[j][i] == 2) {
					var divv = document.getElementById(myID2);
					divv.style.background="red";
				}
			}
		}
	}
}
////----DRAWHITSANDMISSES----DRAWHITSANDMISSES----DRAWHITSANDMISSES----DRAWHITSANDMISSES----////
////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////
////----PLAYERCLICK----PLAYERCLICK----PLAYERCLICK----PLAYERCLICK----////
function playerClick(e) {
	if(e.target !== e.currentTarget) {
		if(e.target.id.substring(1,3) == "10" & e.target.id.substring(3,5) == "10") {									// Handle the (10,10) square
			var row = e.target.id.substring(1,3);
			var col = e.target.id.substring(3,5);
		} else if(e.target.id.substring(1,3) == "10") {																	// Handle the (10,Y) squares
			var row = e.target.id.substring(1,3);
			var col = e.target.id.substring(3,4);
		} else if(e.target.id.substring(2,4) == "10") {																	// Handle the (X,10) sqaures
			var row = e.target.id.substring(1,2);
			var col = e.target.id.substring(2,4);
		} else {																										// Handle all other squares
			var row = e.target.id.substring(1,2);
			var col = e.target.id.substring(2,3);
		}
		
		if(theGrid[row][col] == 0) {
			e.target.style.background = 'white';
			
			theGrid[row][col] = 3;
			setTimeout(function() {
				if(turnMarker == 1) {																					// If it was p1's turn...
					alert("You missed!");
					turnMarker = 2;																						// Next it will be p2's turn
					clearTheGrid("s");
					clearTheGrid("q");
					p1theGrid[row][col] = 3;																			// Mark a miss on p1's top grid
					turnLoop();
				} else if(turnMarker == 2) {
					alert("You missed!");
					turnMarker = 1;
					clearTheGrid("s");
					clearTheGrid("q");
					p2theGrid[row][col] = 3;																			// Mark a miss on p2's bottom grid
					turnLoop();
				}
			},10)
		} else if(theGrid[row][col] == 1 | theGrid[row][col] == 5 | theGrid[row][col] == 6) {							// 1 = A, 5 = B, 6 = S 
			e.target.style.background = 'red';
			
			if(theGrid[row][col] == 1 & turnMarker == 1) {
				p2A_H++;
			} else if(theGrid[row][col] == 5 & turnMarker == 1) {
				p2B_H++;
			} else if(theGrid[row][col] == 6 & turnMarker == 1) {
				p2S_H++;
			} else if(theGrid[row][col] == 1 & turnMarker == 2) {
				p1A_H++;
			} else if(theGrid[row][col] == 5 & turnMarker == 2) {
				p1B_H++;
			} else if(theGrid[row][col] == 6 & turnMarker == 2) {
				p1S_H++;
			}
			
			theGrid[row][col] = 2;
			setTimeout(function() {
				if(turnMarker == 1) {
					alert("It's a hit!");
					turnMarker = 2;
					clearTheGrid("s");
					clearTheGrid("q");
					hitCount1++;
					
					if(p2A_H == 5) {
						alert(player1 + ", you sunk " + player2 + "'s aircraft carrier!");
						p2A_H++;
					} else if(p2B_H == 4) {
						alert(player1 + ", you sunk " + player2 + "'s battleship!");
						p2B_H++;
					} else if(p2S_H == 3) {
						alert(player1 + ", you sunk " + player2 + "'s submarine!");
						p2S_H++;
					}
					
					p1theGrid[row][col] = 2;
					turnLoop();
				} else if(turnMarker == 2) {
					alert("It's a hit!");
					turnMarker = 1;
					clearTheGrid("s");
					clearTheGrid("q");
					hitCount2++;
					
					if(theGrid[row][col] == 1) {
						p1A_H++;
					} else if(theGrid[row][col] == 5) {
						p1B_H++;
					} else if(theGrid[row][col] == 6) {
						p1S_H++;
					}
					
					if(p1A_H == 5) {
						alert(player2 + ", you sunk " + player1 + "'s aircraft carrier!");
						p1A_H++;
					} else if(p1B_H == 4) {
						alert(player2 + ", you sunk " + player1 + "'s battleship!");
						p1B_H++;
					} else if(p1S_H == 3) {
						alert(player2 + ", you sunk " + player1 + "'s submarine!");
						p1S_H++;
					}
					
					p2theGrid[row][col] = 2;
					turnLoop();
				}
			},10)
		} else if(theGrid[row][col] == 4) {
			// do nothing
		} else if(theGrid[row][col] > 1) {
			alert("Stop wasting your torpedos! You already fired at this location.");
		}		
    }
    e.stopPropagation();
}
////----PLAYERCLICK----PLAYERCLICK----PLAYERCLICK----PLAYERCLICK----////
////////////////////////////////////////////////////////////////////////