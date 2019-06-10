## This was a group project, however due to circumstance I wrote 100% of the code that made it to the master branch myself. This is a copy of master.

# 1530 Semester Project
#### ALL CHANGES SHOULD BE PUSHED TO YOUR OWN BRANCH
#### PLEASE SIGN UP FOR THE SLACK CHANNEL USING THE FOLLOWING LINK
https://join.slack.com/t/1530texasholdem/signup
##### Just FYI a lot of this is copy/paste from past instruction sets if by chance you're not familiar with GIT. I'm not trying to pretend to be all-knowing :)

Make sure to pull and push often. Before commiting any changes, make sure you've pulled the latest version of the repo. Communication will be essential to ensure no two members are editing the same code at the same time which will muck up our repo. When making changes:
1. Ensure you have the latest copy of the repo (via git command line or web UI)
2. Push your entire local repository to your own branch (git will highlight the changes)
3. Try to add comments/descriptions to your commits so everyone knows what's been changed
4. After reviewing individual branches, the group will decide when code is ready to be pushed to the master branch

___

### Sprint #1 -> Scrum Master: Mike Okonski

Our goal for the first sprint is to create a "walking skeleton" of the UI for our program. In other words, our UI should be fully-functional without any actual implementation of the texas holdem game.

### User Stories
1. As a game administrator,
I want the cards to be shuffled pseudorandomly and dealt,
So that players cannot unfairly know which cards others have.

1. As a player,
I want to see my own cards, but not others',
So that I can know what to play.

1. As a player,
I want to see my and others' stacks (amount of money available for betting),
So that I can bet legal amounts and adjust my strategy.

1. As a player,
I want to see the amount of money in the pot,
So that I can know how much money I can win in this round.

1. As a game administrator,
I want players to be able to input their name,
So that they can identify with the player on-screen.

1. As a game administrator,
I want all AI players to have a unique name,
So that human players can refer to AI players by a unique name.

1. As a player,
I want the game to allow me to choose the number of opponents from 1 to 7,
So that I can play with a variable number of opponents.

1. As a game administrator,
I want all players, human and AI, to start with $1,000.00,
So that the game is fair for all players.

### Division of Labor
We haven't specified roles yet, but here's what still needs to be finished (TODO):
1. BuildPokerTable.java
  - This should be a seperate class file. If you look in the main program the funcitonaliy exists for populating the board with player JPanels, however we need to make this unit-testable so it needs to be put into a class and broken into methods. Ideally, methdos should be set up so that when the user enters 1-7 when prompted for a number of players, the board populates accordingly. I've mentioned that GridBagLayout will remove rows and cols that are empty, so lower numbers of players won't display correctly unless you create some method to pad empty rows with some invisible placeholder panels.
2. FlopTurnRiver.java
  - Yes the cards in the ceneter display at the moment, but we need to turn this into an object, that contains 3-5 card objects so we can access these card objects later on. This class should start out containing 3 cards (the flop), then have the ability to add a card (the turn) and then add one final card (the river). The container for this is already implimented we just have to turn it into a class.
3. Deliverable 1 write up (should be done last)

___

### Sprint #2 -> NOT YET ASSIGNED
