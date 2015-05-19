package rpgGridBrawl;
import java.util.Scanner;

class Interaction extends GridBrawl {
	Scanner bob;
	String[][] messages;
	String report;
	/* Output codes for first array:
	 * 0: Gridbrawl		1: FileOptionsMenu		2: Interact internal		3: Place	4. move
	 * 5: Humble & Pickpocket		6: Ascension/sacrifice	7: Dragonfire, Ring Attack, Transfer, Spend, & Flee
	 * 9: winning
	 */
	
	public Interaction() {
		this.bob = new Scanner(System.in);
		this.messages = new String[10][10];
		this.messages[0][0]  = "Welcome to RPG Grid Brawl!";
		this.messages[0][1]  = "Please select from the following options:\n(S)tart a new game\n(L)oad a saved game\nRandom (M)ayhem\n" +
				"Random (R)egular Brawl\n(G)ame Editor\n(E)xit\n\nYour selection: ";
		this.messages[0][2]  = "PREPARE TO BRAWL...";
		this.messages[1][0]  = "What is the name of this game? ";
		this.messages[1][1]  = "Red Player, what is your name? ";
		this.messages[1][2]  = "Blue Player, what is your name? ";
		this.messages[1][3]  = "The game has been reset to round 11, and all brawlers are on the board...";
		this.messages[1][4]  = "RANDOMIZED FOR MAYHEM...";
		this.messages[1][5]  = "File Not Found! Creating game now...";
		this.messages[1][6]  = "Save game already exists, would you like to overwrite it?";
		this.messages[1][7]  = "Game saved successfully.";
		this.messages[1][8]  = "The game has been reset to round 11, with a random regular Square-off.";
		this.messages[2][0]  = "Are you sure you want to do that? (y/n): ";
		this.messages[2][1]  = "Action cancelled.";
		this.messages[2][2]  = "The Princess must be placed on board level 4.";
		this.messages[2][3] = "The Dragon and the Princess must both be placed on level 4, and that level is full.";
		this.messages[2][4] = "This game will have to be restarted.  Nice work, asshole!";
		this.messages[2][5] = "Here are the pieces you may place on the board this turn:";
		this.messages[2][6] = "You cannot place both the Princess and the Dragon on the same space.";
		this.messages[2][7] = "Placement Confirmed.";
		this.messages[3][0]  = "Enter number of piece to place or enter '-1' to return to main menu:";
		this.messages[3][1]  = "That is not a valid selection this turn";
		this.messages[3][2]  = "Characters must be placed on board level 1.";
		this.messages[3][3]  = "Nemeses must be placed on board level 4.";
		this.messages[3][4]  = "Enter 3-digit number of space on which to place this piece or enter anything else to return to main menu:";
		this.messages[3][5]  = "That is not a valid board space.";
		this.messages[3][6]  = "You cannot place a Brawler on the Glorification space.";
		this.messages[3][7]  = "You may only place Characters on board level 1.";
		this.messages[3][8]  = "You may only place Nemeses on board level 4.";
		this.messages[4][0]  = "Enter the number of your selection or anything else to return to main menu: ";
		this.messages[4][1]  = "That brawler is not available to be moved by you this turn.";
		this.messages[4][2]  = "Enter the number of the destination space: ";
		this.messages[4][3]  = "Nemeses cannot move up, they can only move down, or around on the level they are currently on.";
		this.messages[4][4]  = "Monsters cannot be moved to a board level higher than their level.";
		this.messages[4][5]  = "You cannot move a brawler directly towards a Ghost.";
		this.messages[4][6]  = "Characters cannot attack other Characters while the Princess is on the same board level.";
		this.messages[4][7]  = "The Vampire has drunk the sweet nectar of another brawler, and it regains some of its strength!";
		this.messages[5][0]  = "You may humble your Cleric before one of your other characters.";
		this.messages[5][1]  = "Please select from among the following options:";
		this.messages[5][2]  = "You have chosen to humble your Cleric before your Warrior.";
		this.messages[5][3]  = "You have chosen to humble your Cleric before your Mage.";
		this.messages[5][4]  = "You have chosen to humble your Cleric before your Rogue.";
		this.messages[5][5]  = "Humbling was successful.";
		this.messages[5][6]  = "Pickpocket was successful.  Yoink!";
		this.messages[6][0]  = "This Character is now permanently CL4.";
		this.messages[6][1]  = "You may select one brawler to be bumped off of the board.  Your choices:";
		this.messages[6][2]  = "You may select one Character or Monster, its level will increase.  Your choices:";
		this.messages[6][3]  = "The Ring will bestow itself on another Character or Monster.  Your choices:";
		this.messages[6][4]  = "You may move any one brawler on the board to any unoccupied space.  Your choices:";
		this.messages[6][5]  = "Enter the 3-digit number of an unoccupied space to move that to: ";
		this.messages[6][6]  = "The ritual of sacrifice was successfully performed.";
		this.messages[7][0]  = "There are no available targets for the Dragon's fiery breath.";
		this.messages[7][1]  = "There are no available targets for the Red Mage's fireball spell.";
		this.messages[7][2]  = "There are no available targets for the Blue Mage's fireball spell.";
		this.messages[7][3]  = "Transfer of Treasure was successful.";
		this.messages[7][4]  = "Choose a Brawler to flee the brawl:";
		this.messages[8][0]  = "These brawlers are open to being bribed this turn:";
		this.messages[8][1]  = "Enter the 3-digit number of any unoccupied space: ";
		this.messages[8][2]  = "The Diamond has been spent, your bribe was successful.";
		this.messages[8][3]  = "1. Sword\n2. Shield\n3. Ring\nEnter the number of the Treasure you wish to purchase: ";
		this.messages[8][4]  = "Your Rogue may 'invest' the Diamond, and in doing so will take possession of any one Treasure on its board level.";
		this.messages[8][5]  = "The Diamond has been spent, your investment was fruitful.";
		this.messages[9][0]  = "The game is over, and the Red player has won up!";
		this.messages[9][1]  = "The game is over, and the Blue player has won up!";
		this.messages[9][2]  = "The game is over, and the Red player has won down!";
		this.messages[9][3]  = "The game is over, and the Blue player has won down!";
	}

	public boolean confirm() {
		this.tellPlayer(2, 0, true); // "Are you sure you want to do that? (y/n): "
		String confirm = bob.next();
		if (confirm.toLowerCase().equals("y")) return true;
		else {
			this.tellPlayer(2, 1, false); // "Action cancelled."
			return false;
		}	
	}
		
	public String gameReport(GameData crt) {
		report = crt.gameRecord + "\n";
		if (crt.isRedsTurn()) report += ("It is RED's turn number " + crt.getGameRound() + "\t");
		else report += ("It is BLUE's turn number " + crt.getGameRound()  + "\t");
		// these next two lines are tracers.
		if (crt.getRedLastUsed() != -1) System.out.print("red last used = " + BRWLRS[crt.getRedLastUsed()] + "\t");
		if (crt.getBlueLastUsed() != -1) System.out.println("blue last used = " + BRWLRS[crt.getBlueLastUsed()]);
		System.out.println();
		for (int chars = 0; chars < 8; chars++) {
			Brawler Jimbo = crt.getBrawler(chars);
			int spacer = 0;
			
			if (crt.getRedLastUsed() == Jimbo.getPieceID() || crt.getBlueLastUsed() == Jimbo.getPieceID()) {
				report += "++";
				spacer -= 2;
			}
			
			if (Jimbo.isGlorified()) {
				report += "^";
				spacer--;
			}
			report += BRWLRS[chars] + ":";
			if (Jimbo.isPoweredUp()) {
				report += "*";
				spacer--;
			}
			if (!Jimbo.isOnBoard()) {
				for (spacer += (22 - BRWLRS[chars].length()); spacer >= 0; spacer--) report += " ";
				report += "off board";
			}
			else {
				for (spacer += (17 - BRWLRS[chars].length()); spacer >= 0; spacer--) report += " ";
				report += ("CL" + Jimbo.getLevel() + "  on space# " + Jimbo.getFloor() + Jimbo.getColumn() + Jimbo.getRow());
			}
			if (Jimbo.isHandsFull()) report += ("\t(" + BRWLRS[Jimbo.getPossession()] + ")");
			report += "\n";
		}
		for (int therest = 8; therest < 24; therest++) {
			Brawler xyz = crt.getBrawler(therest);
			int spacer = 0;
			// this next if block was commented out, I am putting it back in to see if it works better now
			if ((crt.getRedLastUsed() == xyz.getPieceID()) || (crt.getBlueLastUsed() == xyz.getPieceID())){
				report += "++";
				spacer -= 2;
			}
			
			report += BRWLRS[therest] + ":";
			if (!xyz.isOnBoard()) {
				for (spacer += (22 - BRWLRS[therest].length()); spacer >= 0; spacer--) report += " ";
				report += "off board";
			}
			else if (therest < 16 && xyz.isOnBoard()){
				for (spacer += (17 - BRWLRS[therest].length()); spacer >= 0; spacer--) report += " ";
				report += ("CL" + xyz.getLevel() + "  on space# " + xyz.getFloor() + xyz.getColumn() + xyz.getRow());
			}
			else {
				for (spacer += (22 - BRWLRS[therest].length()); spacer >= 0; spacer--) report += " ";
				report += ("on space# " + xyz.getFloor() + xyz.getColumn() + xyz.getRow());
			}
			report += "\n";
		}
		return report;
	}
	
	public int getBrawlerNumber(int setlength) {
		int bn = -1;
		String bns = this.bob.next();
		if (!isNumeric(bns)) return -1;
		else {
			bn = Integer.parseInt(bns);
			if (bn < 0 || bn > setlength) return -1;
		}
		return bn;
	}
	
	public int getDragonFireSelection(int[] targetList) {
		int df = getIntOrNothing();
		if (df < 1 || df > targetList.length) return -1;
		else return targetList[df - 1];
	}
	
	public int getFileOptionsMenuSelection() {
		int bn = 0; // 0 is the "return to previous menu" option
		String bns = this.bob.next();
		if (!isNumeric(bns)) return 0;
		else {
			bn = Integer.parseInt(bns);
			if (bn <= 0 || bn > 6) return 0;
		}
		return bn;
	}
	
	public int getFleeSelection(boolean[] cowards) {
		int fs = getIntOrNothing();
		if (fs == -1 || !cowards[fs]) return -1;
		return fs;
	}
	
	public int getHumbleSelection() {
		int hs = getIntOrNothing();
		if (hs == 1 || hs == 2 || hs == 3) return hs;
		return -1;
	}
	
	public int getIntOrNothing() {
		int select = -1; // if this is returned, return to main menu
		String s = bob.next();
		if (isNumeric(s)) select = Integer.parseInt(s);
		else return -1;
		return select;
	}
	
	public String[] getNames() {
		String[] gameNames = new String[3];
		this.tellPlayer(1, 0, true); // "What is the name of this game? "
		gameNames[0] = bob.next();
		this.tellPlayer(1, 1, true); // "Red Player, what is your name? "
		gameNames[1] = bob.next();
		this.tellPlayer(1, 2, true); // "Blue Player, what is your name? "
		gameNames[2] = bob.next();
		return gameNames;
	}
	
	public int getPickpocketSelection(int[] targets) {
		int select = getIntOrNothing();
		if (select < 0 || select >= targets.length || targets[select] == -1) return -1;
		else return select;
	}
	
	public int getSacrificeSelection(boolean[] choices) {
		int select = getIntOrNothing();
		if (select < 0 || select >= choices.length || !choices[select]) return -1;
		else return select;
	}
	
	public int getSpaceNumber() {
		int sn = 0;
		String sns = this.bob.next();
		if (!isNumeric(sns) && sns.toLowerCase().equals("o")) return 0;  // this is used by the game editor and might be dangerous.
		// I might want to include a boolean argument in here--game editor or not.
		if (!isNumeric(sns)) return -1;
		else {
			sn = Integer.parseInt(sns);
			if (sn < 100 || sn > 500) return -1;
		}
		return sn;
	}
	
	public int getSpendSelection1(boolean[] choices) {
		int select = getIntOrNothing();
		if (choices[0] && select == 1) return 1;
		if (choices[1] && select == 2) return 2;
		if (choices[2] && select == 3) return 3;
		return -1;
	}
	
	public int getSpendSelection2(int[] bribeList) {
		int select = getIntOrNothing();
		if (select > 0 && select < bribeList.length) return --select; // selection should correspond to place in bribeList[]
		return -1;
	}
	
	public int getSpendSelection3() {
		int select = getIntOrNothing();
		if (select != 1 && select != 2 && select != 3) select = -1;
		return select;
	}
	
	public int getSpendSelection4(boolean[] opts3) {
		int select = getIntOrNothing();
		if (select != 1 && select != 2 && select != 3) select = -1;
		if (select == 1 && !opts3[0]) select = -1;
		if (select == 2 && !opts3[1]) select = -1;
		if (select == 3 && !opts3[2]) select = -1;
		return select;
	}
	
	public String getString() {
		return bob.next();
	}
	
	public int getTransferSelection(boolean[] choices) {
		int select = getIntOrNothing();
		try {
			if (choices[--select]) return select;
			else return -1;
		}
		catch(ArrayIndexOutOfBoundsException ex) {
			return -1;
		}
	}
	
	public int[] getUnoccupiedSpaceNumber(GameData crt) {
		int select = this.getSpaceNumber();
		int[] space = {-1, -1, -1};
		if (select == -1) return space;
		boolean occupado;
		int occupier = -1;
		int[] spacex = new int[3];
		try {
			spacex = splitBoardSpace(select);
			occupado = crt.getLocation(spacex[0], spacex[1], spacex[2]).isOccupied();
		}
		catch(ArrayIndexOutOfBoundsException ex) {
			return space;
		}
		if(occupado) {
			this.tellPlayer(this.msgBuild(crt, 8, occupier), false); // "That space is already occupied by the " + BRWLRS[occupier] + "."
			spacex = getUnoccupiedSpaceNumber(crt);	// recursion...fancy!
		}
		return spacex;
	}
	 
	public boolean getYesOrNo() { // this is like confirm(), but it doesn't say anything to the player
		boolean confirmation = false;
		String confirm = bob.next();
		if (confirm.toLowerCase().equals("y")) confirmation = true;
		return confirmation;
	}
	
	public int mainMenuSelect() {
		String select = bob.next();
		if (select.toUpperCase().equals("S")) return 1; 		// start a new game
		else if (select.toUpperCase().equals("L")) return 2;	// load a saved game
		else if (select.toUpperCase().equals("M")) return 3;	// start a random mayhem
		else if (select.toUpperCase().equals("R")) return 4;	// start a random regular brawl
		else if (select.toUpperCase().equals("G")) return 5;	// enter the game editor
		return 0;
	}
		
	public String moveILDErrorMsg(GameData crt, int slct, int errorCode) {
		String err = "";
		switch(errorCode) {
		case 2: err = this.msgBuild(crt, 11, slct); break; // "The " + BRWLRS[slct] + " cannot move up or down this turn because the Sentinel is on the same level."
		case 3: err = this.messages[4][3]; break; // "Nemeses cannot move up, they can only move down, or around on the level they are currently on."
		case 4: err = this.messages[4][4]; break; // "Monsters cannot be moved to a board level higher than their level."
		case 5: err = this.msgBuild(crt, 12, slct); break; // "That space cannot be reached by the " + BRWLRS[slct] + " this turn."
		case 6: err = this.messages[4][5]; break; // "You cannot move a brawler directly towards a Ghost."
		case 7: err = this.msgBuild(crt, 13, slct); break; // "The hands of the " + BRWLRS[slct] + " are full, it cannot pick up a treasure this turn."
		case 8: err = this.messages[4][6]; break; // "Characters cannot attack other Characters while the Princess is on the same board level."
		default: err = "You tried to pass something weird into moveILDErrorMsg()";
		}
		return err;
	}
	
	public String msgBuild(GameData crt, int mb, int passIn) {
		String t = "";
		switch (mb) {
		case 0: t = "Game " + crt.gameName + " has been created.";										break; // GridBrawl.main
		case 1: t = crt.redPlayerName + " will take the first turn as the red player.";					break; // GridBrawl.main
		case 2: t = "Game " + crt.gameName + " has been loaded.";										break; // GridBrawl.main
		case 3: t = "Game will begin on round #" + crt.getGameRound() + ".";								break; // GridBrawl.main
		case 4: t = "Player " + crt.redPlayerName + ", it is Red's Turn.";								break; // GridBrawl.main
		case 5: t = "Player " + crt.bluePlayerName + ", it is Blue's Turn.";								break; // GridBrawl.main
		case 6: t = "It is round #" + crt.getGameRound();														break; // GridBrawl.main
		case 7: t = "You have selected " + BRWLRS[passIn] + ".";											break; // Interact.placeGetPSelect()
		case 8: t = "That space is already occupied by the " + BRWLRS[passIn] + ".";						break; // Interact.placeGetSpace() & Place.dragonPrincess()
		case 9: t = "When you place that piece you must also place the " + BRWLRS[passIn] + "."; 		break; // Place.dragonPrincess()
		case 10:t = "Player " + crt.crtPlayerName + ", these pieces are available to move this turn:";	break; // Move.execute
		case 11:t = "The " + BRWLRS[passIn] + " cannot move up or down this turn because the Sentinel is on the same level.";
			break; 																								   // Interact.moveILDErrorMsg
		case 12:t = "That space cannot be reached by the " + BRWLRS[passIn] + " this turn."; 			break; // Interact.moveILDErrorMsg
		case 13:t = "The hands of the " + BRWLRS[passIn] + " are full, it cannot pick up a treasure this turn.";
			break; 																								   // Interact.moveILDErrorMsg
		case 14:t = "Please select a new destination for the " + BRWLRS[passIn] + ".";					break; // Move.execute()
		case 15:t = "The destination space is occupied by the " + BRWLRS[passIn] + ".";					break; // Move.execute()
		case 16:t = "The Abyss has swallowed up the " + BRWLRS[passIn] + "...";							break; // Move.finishMoveTurn()
		case 17:t = "You are about to perform the ritual of Glorification on the " + BRWLRS[passIn] + "!";	break; // Glorification.executeAscension()
		case 18:t = "The " + BRWLRS[passIn] + " has left the board!";									break; // Glorification.executeAscension()
		case 19:t = "The " + BRWLRS[passIn] + " has returned to the pool.";								break; // Glorification.executeAscension()
		case 20:t = "You may sacrifice the " + BRWLRS[passIn] + ".";										break; // Glorification.executeSacrifice()
		case 21:t = "The " + BRWLRS[passIn] + " has been bumped off of the board!";						break; // Move.performBumpDwns() & Ascension.executeSacrifice
		case 22:t = "You have selected to perform the ritual of sword sacrifice against the " + BRWLRS[passIn]; // Glorification.executeSacrifice()
			break; 																								   
		case 23:t = "You have selected to perform the ritual of shield sacrifice on the " + BRWLRS[passIn];	   // Glorification.executeSacrifice()
			break; 																								   
		case 24:t = "You have selected to bestow the magic ring on the " + BRWLRS[passIn];				break; // Glorification.executeSacrifice()
		case 25:t= "You have selected to perform the ritual of diamond sacrifice on the " + BRWLRS[passIn];    // Glorification.executeSacrifice()
			break; 																								   
		case 26:t = "You have selected to have the Dragon breathe fire on the " + BRWLRS[passIn] + ".";	break; // Fireball.burn()
		case 27:t = "The charred husk of the " + BRWLRS[passIn] + " has been tossed off of the board!";	break; // Fireball.burn()
		case 28:t = "The Dragon burns the " + BRWLRS[passIn] + " with its firey breath!";				break; // Fireball.burn()
		case 29:t = "You have selected to have your Mage hurl a fireball at the " + BRWLRS[passIn] + ".";break; // Fireball.burn()
		case 30:t = "The Red Mage casts a fire spell, burning the " + BRWLRS[passIn] + "!";				break; 	// Fireball.burn()
		case 31:t = "The Blue Mage casts a fire spell, burning the " + BRWLRS[passIn] + "!";				break; // Fireball.burn()
		case 32:t = "You have selected to bribe the " + BRWLRS[passIn] + " into moving to a new space.";	break; // Spend.execute()
		case 33:t = "The " + BRWLRS[passIn] + " may purchase a Treasure from the Merchant.";				break; // Spend.execute()
		case 34:t = "There are no available Treasures for the " + BRWLRS[passIn] + " to take on that level";    // Spend.execute()
			break;	
		case 35:t = "It is the top of round " + crt.getGameRound();	break;										   // GridBrawl.endGame()
		case 36:t = "It is the bottom of round " + crt.getGameRound();	break;									   // GridBrawl.endGame()
		case 37:t = "Game " + crt.gameName + " is over.  Red wins up!  Congratulations, " + crt.redPlayerName + "!";  	// GridBrawl.endGame()
			break;
		case 38:t = "Game " + crt.gameName + " is over.  Blue wins up!  Congratulations, " + crt.bluePlayerName + "!";	// GridBrawl.endGame()
			break;
		case 39:t = "Game " + crt.gameName + " is over.  Red wins down!  Congratulations, " + crt.redPlayerName + "!";   // GridBrawl.endGame()
			break;
		case 40:t = "Game " + crt.gameName + " is over.  Blue wins down!  Congratulations, " + crt.bluePlayerName + "!"; // GridBrawl.endGame()
		case 41:t = "The " + BRWLRS[passIn] + " gains no level because the Leper is on the same floor."; break;			// Move.finishMoveTurn()
		case 42:t = "The Vampire has sucked an additional character level out of the " + BRWLRS[passIn] + "!"; break;	// Move.finishMoveTurn()
		case 43:t = "The Vampire bites, and the bloodless husk of the " + BRWLRS[passIn] + " is tossed off of the board!"; // Move.finishMoveTurn()
		case 44:t = "The " + BRWLRS[passIn] + " has \"cured\" the leper, and will gain a Character Level!"; break;		// Move.finishMoveTurn()
		case 45:t = "The " + BRWLRS[passIn] + " is now level " + crt.getBrawler(passIn).getLevel();	break;						// Move.finishMoveTurn()
		case 46:t = "The " + BRWLRS[passIn] + " has \"cured\" the Leper, and is rewarded with the Shield!"; 	break;	// Move.finishMoveTurn()
		case 47:t = "The " + BRWLRS[passIn] + " has \"cured\" the Leper, and receives humble thanks.";	break;			// Move.finishMoveTurn()
		case 48:t = "The " + BRWLRS[passIn] + " has been weakened by attacking the Leper.";				break;			// Move.finishMoveTurn()
		case 49:t = "The " + BRWLRS[passIn] + " has \"rescued\" the Princess, and is rewarded with the Sword.";	break;	// Move.finishMoveTurn()
		case 50:t = "The " + BRWLRS[passIn] + " has robbed the Merchant, absconding with the Diamond.";	break;			// Move.finishMoveTurn()
		case 51:t = "The Ghost dissipates, and the " + BRWLRS[passIn] + " discovers a magic ring!";		break;			// Move.finishMoveTurn()
		case 52:t = "The " + BRWLRS[passIn] + " has also vanished into the Abyss......";				break;		    // Move.finishMoveTurn()
		case 53:t = "You have selected to remove the " + BRWLRS[passIn] + " from the board.";			break;			// Flee.execute()
		case 54:t = "The " + BRWLRS[passIn] + " has fled the brawl!";									break;			// Flee.execute()
		case 55:t = "Would you like to move the Vampire up to space #" + passIn + "? ";					break;			// Move.finishMoveTurn()
		case 56:t = "The Vampire cackles as it heads back up to level " + passIn + "...";				break;			// Move.finishMoveTurn()
			// break;
		}
		return t;
	}
	
	public int OptionSelection(boolean[] oA) {
		int selection = 0;
		boolean goodChoice = false;
		System.out.println("Please select from the following options:"); 
		System.out.println();
		if (oA[0]) System.out.println("0. File Options and Help");
		if (oA[1] && !oA[2]) System.out.println("1. Your only option is to place a piece on the board this turn.");
		if (oA[1] &&  oA[2]) System.out.println("1. Place a piece on the board.");
		if (oA[2]) System.out.println("2. Move a piece on the board.");
		if (oA[3]) System.out.println("3. Sacrifice your Treasure to the glorification space.");
		if (oA[4]) System.out.println("4. Perform the ritual of Glorification.");
		if (oA[5]) System.out.println("5. Transfer a Treasure from one of your Characters to another.");
		if (oA[6]) System.out.println("6. Spend the Diamond.");
		if (oA[7]) System.out.println("7. Humble your Cleric before one of your other Characters.");
		if (oA[8]) System.out.println("8. Shoot a fireball from your Mage's Ring.");
		if (oA[9]) System.out.println("9. Pickpocket a Treasure from an opponent's Character.");
		if (oA[10]) System.out.println("10. The Dragon Breathes Fire!");
		if (oA[11]) System.out.println("11. A Brawler on BL1 Flees from the board.");
		System.out.println("12. Show the board.");
		System.out.println("13. Game report.");
		System.out.println();
		int input = -1;
		while(!goodChoice) {
			System.out.print("Your selection: ");
			
			// This is kicking up a NoSuchElementException when I load a game.
			
			String Sinput = this.bob.next();
			try {
				input = Integer.parseInt(Sinput);
				if (input < 0 || input > 13) {
					tellPlayer("That is not a valid option number.", false);
					goodChoice = false;	
				}
				else if (!oA[input]) {
					tellPlayer("That is not a valid option this particular turn.", false);
					goodChoice = false;	
				}
				if (input >=0 && input <= 14 && oA[input]) {
					System.out.println();
					selection = input;
					goodChoice = true;
				}
			}
			catch(NumberFormatException ex) {
				tellPlayer("invalid input", false);
				goodChoice = false;
			}
		}
		return selection;
	}
	
	public int placeGetPSelect(boolean[] validSelection, GameData crt) {
		int pSelect = -1;
		boolean validChoice = false;
		while(!validChoice) { // this loop gathers an id of a piece to place, stores it as pSelect
			this.tellPlayer(3, 0, true); // "Enter number of piece to place or enter '-1' to return to main menu:"
			pSelect = this.getBrawlerNumber(24);
			if (pSelect == -1) return pSelect;
			else if (validSelection[pSelect] == false) {
				this.tellPlayer(3, 1, false); // "That is not a valid selection this turn"
				this.space();
				validChoice = false;
			}
			else if (validSelection[pSelect] == true) {
				this.tellPlayer(this.msgBuild(crt, 7, pSelect), false); // "You have selected " + BRWLRS[pSelect] + "."
				if (pSelect <= 7) this.tellPlayer(3, 2, false); // "Characters must be placed on board level 1."
				if (pSelect >= 8 && pSelect <= 11) this.tellPlayer(3, 3, false); // "Nemeses must be placed on board level 4.")
				this.space();
				validChoice = true;
			}
			else { // for any bullshit input
				pSelect = -1;
				validChoice = true;
			}
		}
		return pSelect;
	}
	
	// The logic from this method, or some of it, should perhaps move into the Place class.
	// Also, try to incorporate the new GridBrawl.isLegitSpace() logic into this
	public int placeGetSpace(GameData crtGame, int pSelect) {
		int[] sSelectsplit = new int[3];
		int sSelect = 0;
		int fSelect = 0;
		int cSelect = 0;
		int rSelect = 0;
		boolean validChoice = false;
		while (!validChoice) { // this loop gathers until it gets a legitimate sSelect
			try {
				this.tellPlayer(3,  4, true); // "Enter 3-digit number of space on which to place this piece or enter anything else to return to main menu:"
				sSelect = this.getSpaceNumber();
				if (sSelect == -1) {
					crtGame.setSuccessfulTurn(false);
					return -1;
				}
				else {
					try {
						sSelectsplit = splitBoardSpace(sSelect);
						fSelect = sSelectsplit[0];
						cSelect  = sSelectsplit[1];
						rSelect  = sSelectsplit[2];
						BoardSpace tryMe = crtGame.getLocation(fSelect, cSelect, rSelect);
					}
					catch (ArrayIndexOutOfBoundsException ex) {
						return -1;
					}
				}	
				
				boolean vbs = true;
				if (fSelect == 1 && (cSelect > 4 || rSelect > 4)) vbs = false;
				else if (fSelect == 2 && (cSelect > 3 || rSelect > 3)) vbs = false;
				else if ((fSelect == 3 || fSelect == 4) && (cSelect > 2 || rSelect > 2)) vbs = false;
				
				if (!vbs) {
					this.tellPlayer(3, 5, false); // "That is not a valid board space.");
					validChoice = false;
				} 
				else if (fSelect == 5) {
					this.tellPlayer(3, 6, false); // "You cannot place a piece on the Glorification space."
					validChoice = false;
				}
				else if (pSelect <= 7 && fSelect > 1) {
					this.tellPlayer(3, 7, false); // "You may only place Characters on board level 1."
					validChoice = false;
				}
				else if (pSelect >= 8 && pSelect <= 11 && fSelect != 4) {
					this.tellPlayer(3, 8, false); // "You may only place Nemeses on board level 4."
					validChoice = false;
				}
				else if (pSelect == 16 && fSelect != 4) {
					this.tellPlayer(2, 2, false); // "The Princess must be placed on board level 4."
					validChoice = false;
				}
				else if ((pSelect == 8 || pSelect == 16) && !Place.checkLevelFour(crtGame)) {
					this.tellPlayer(2, 3, false); // "The Dragon and the Princess must both be placed on level 4, and that level is full."
					validChoice = false;
					if (crtGame.getGameRound() <= 10) {
						this.tellPlayer(2, 4, false); // "This game will have to be restarted.  Nice work, asshole!"
						System.exit(0);  // I need to make a rule that you can't place on level 4 unless there are 2 open spaces.
					}
				}
				else if (crtGame.getLocation(fSelect, cSelect, rSelect).isOccupied()) {
					int occupiedBy = crtGame.getLocation(fSelect, cSelect, rSelect).getOccupiedBy();
					this.tellPlayer(this.msgBuild(crtGame, 8, occupiedBy), false);  // "That space is already occupied by the " + BRWLRS[occupiedBy] + "."
					validChoice = false;
				}
				else {  // presumably this is a valid selection
					String tellThem = ("You have selected to place the " + BRWLRS[pSelect] + " on space #" + fSelect + cSelect + rSelect);
					this.tellPlayer(tellThem, false);
					boolean confirmed = this.confirm();
					if (confirmed) validChoice = true;
					else validChoice = false;
				}	
			} // end of try block
			catch(StringIndexOutOfBoundsException ex) {
				crtGame.setSuccessfulTurn(false);
				return -1;
			}
		} // end of while loop, space has been selected and confirmed.
		return sSelect;
	}
	
	public String placeReportOnValidselection(boolean[] vs) {
		report = "";
		for (int i = 0; i < vs.length; i++) if (vs[i]) report += (i + ". " + BRWLRS[i] + "\n");
		return report;
	}
	
	public String reportOnFireballOptions(GameData crt, int[] targets, int shooter) {
		report = "";
		int numSay = 1;
		String prefix = "";
		Brawler ourShooter = crt.getBrawler(shooter);
		if (ourShooter instanceof Dragon) prefix = "The Dragon breathes fire on the ";
		else if (ourShooter.isRed()) prefix = "The Red Mage hurls a fireball at the";
		else prefix = "The Blue Mage hurls an ice ball at the";
		for (int df = 0; df < targets.length; df++) {
			if (targets[df] != -1) {
				Brawler targ = crt.getBrawler(targets[df]);
				boolean bumpable = ((targ instanceof Character || targ instanceof Monster) && targ.getLevel() > 1 && targ.getFloor() > 1);
				String result = "";
				if (bumpable) result = "which will bump it down.";
				else result = "which will remove it from the board.";
				report += numSay + ". " + prefix + BRWLRS[targ.getPieceID()]+ ", " + result + "\n"; 
				numSay++;
			}
		}
		report += "Enter the number of your selection: ";
		return report;
	}
	
	public String reportOnFleeOptions(boolean[] cowards) {
		report = "";
		for (int f = 0; f < cowards.length; f++) if (cowards[f]) report += f + ". " + BRWLRS[f] + "\n";
		report += "Select the number of the Brawler to flee: ";
		return report;
	}
	
	public String reportOnPickpocketOptions(GameData PPO, int[] targets) {
		Brawler victim = PPO.getBrawler(targets[0]);
		String report = "1. You may have your Rogue steal the " + BRWLRS[victim.getPossession()] + " from the " + BRWLRS[victim.getPieceID()] + ".\n"; 
		if (targets[1] != -1) {
			victim = PPO.getBrawler(targets[1]);
			report += "2. You may have your Rogue steal the " + BRWLRS[victim.getPossession()] + " from the " + BRWLRS[victim.getPieceID()] + ".\n";
		}
		if (targets[2] != -1) {
			victim = PPO.getBrawler(targets[1]);
			report += "3. You may have your Rogue steal the " + BRWLRS[victim.getPossession()] + " from the " + BRWLRS[victim.getPieceID()] + ".\n";
		}
		if (targets[3] != -1) {
			victim = PPO.getBrawler(targets[1]);
			report += "4. You may have your Rogue steal the " + BRWLRS[victim.getPossession()] + " from the " + BRWLRS[victim.getPieceID()] + ".\n";
		}
		report += "Enter the number of your selection: ";
		return report;
	}
	
	
	public String reportOnPlacementOptions(GameData CG) {
		String report = "";
		boolean rcp = CG.canRedPlace();
		boolean bcp = CG.canBluePlace();
		if(rcp && bcp) report = "You may place this round, and your opponent will be able to place also.";
		if(!rcp && !bcp) report = "Neither you nor your opponent may place this round.";
		if(CG.isRedsTurn()) {
			if(rcp && !bcp) report = "You may place this round, your opponent may not place as their next turn.";
			else if(!rcp && bcp) report = "You may not place this round, but your opponent may place as their next turn.";
			
		}
		else { // it is blue's turn
			if(!rcp && bcp) report = "You may place this round, your opponent will not be able to place as their next turn.";
			else if(rcp && !bcp) report = "You may not place this round, but your opponent may place as their next turn.";	
		}
		return report;
	}
	

	public String reportOnLastUsed(GameData CGG) {
		String report = "You last acted upon the ";
		int pieceID;
		if (CGG.isRedsTurn()) pieceID = CGG.getRedLastUsed();
		else pieceID = CGG.getBlueLastUsed();
		report += BRWLRS[pieceID];
		report += " on round " + (CGG.getGameRound() - 1) + ".";
		return report;
	}
	
	public String reportOnSacrificeOptions(boolean[] choices) {
		String report = "";
		for (int n = 0; n < choices.length; n++) {
			if (choices[n]) report += n + ". " + BRWLRS[n] + "\n";
		}
		report += "Enter the number of your selection or anything else to return to the main menu: ";
		return report;
	}
	
	// FIX THIS, invest has gone away
	public String reportOnSpendOptions1(boolean[] opts) {
		String report = "";
		if (opts[0]) report += "1. Bribe any one brawler within line of sight to move to any unoccupied space.\n";
		if (opts[1]) report += "2. Purchase any one Treasure from the Merchant.\n";
		if (opts[2]) report += "3. Your rogue may invest the Diamond to take any one Treasure on the same board level.\n";
		report += "Enter your selection: ";
		return report;
	}
	
	public String reportOnSpendOptions2(int[] targetList, GameData crt) {
		String report = "";
		int counter = 1;
		for (int s2 = 0; s2 < targetList.length; s2++) {
			report += counter + ". " + BRWLRS[targetList[s2]] + "\n";
			counter++;
		}
		report += "Enter the number of your selection: ";
		return report;
	}
	
	public String reportOnSpendOptions3(boolean[] opts3) {
		String report = "Your investment will yield one of the following:\n";
		if (opts3[0]) report += "1. Sword\n";
		if (opts3[1]) report += "2. Shield\n";
		if (opts3[2]) report += "3. Ring\n";
		report += "Enter your selection: ";
		return report;
	}
	
	public String reportOnTransferOptions(boolean[] cases, Brawler walt, Brawler clara, Brawler meg, Brawler roy) {
		// 12 scenarios: WC, WM, WR, CM, CR, MR & the opposites CW, MW, RW, MC, RC, RM
		String report = "";
		int wt = walt.getPossession();
		int ct = clara.getPossession();
		int mt = meg.getPossession();
		int rt = roy.getPossession();
		int wp = walt.getPieceID();
		int cp = clara.getPieceID();
		int mp = meg.getPieceID();
		int rp = roy.getPieceID();
		int[] blr = new int[3];
		for (int c = 0; c < cases.length; c++) {
			if (cases[c]) {
				switch(c) {
				case 0:  blr[0] = wt;	blr[1] = wp;	blr[2] = cp;	break;
				case 1:  blr[0] = wt;	blr[1] = wp;	blr[2] = mp;	break;
				case 2:  blr[0] = wt;	blr[1] = wp;	blr[2] = rp;	break;
				case 3:  blr[0] = ct;	blr[1] = cp;	blr[2] = mp;	break;
				case 4:  blr[0] = ct;	blr[1] = cp;	blr[2] = rp;	break;
				case 5:  blr[0] = mt;	blr[1] = mp;	blr[2] = rp;	break;
				case 6:  blr[0] = ct;	blr[1] = cp;	blr[2] = wp;	break;
				case 7:  blr[0] = mt;	blr[1] = mp;	blr[2] = wp;	break;
				case 8:  blr[0] = rt;	blr[1] = rp;	blr[2] = wp;	break;
				case 9:  blr[0] = mt;	blr[1] = mp;	blr[2] = cp;	break;
				case 10: blr[0] = rt;	blr[1] = rp;	blr[2] = cp;	break;
				case 11: blr[0] = rt;	blr[1] = rp;	blr[2] = mp;	break;
				}
				String cc = (c + 1) + ". ";
				report += cc + "You may transfer the " + BRWLRS[blr[0]] + " from the " + BRWLRS[blr[1]] + " to the " + BRWLRS[blr[2]] + ".\n";
			}
		}
		report += "Please enter your selection or anything else to return to the main menu: ";
		return report;		
	}
	
	public String reportScores(int[] scores, double advantage) {
		String result = "";
		String color = "red";
		if (advantage < 0.0) color = "blue";
		else if (advantage == 0) color = "";
		result += "The score is:\tRED: " + scores[0] + "\tBLUE: " + scores[1] + "\t\tADVANTAGE: " + advantage + "%  " + color;
		return result;
	}
	
	public void space() {
		System.out.println();
	}
	
	public void tellPlayer(int section, int index, boolean isPrompt) {
		try {
			if (isPrompt) System.out.print(this.messages[section][index]);
			else System.out.println(this.messages[section][index]);
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("You're trying to have me tell the player something, but you're passing in some bullshit.");
			System.exit(0);
		}
	}
	
	public void tellPlayer(String say, boolean isPrompt) {
		if (isPrompt) System.out.print(say);
		else System.out.println(say);
	}
	
	
} // end of Interaction class
	
