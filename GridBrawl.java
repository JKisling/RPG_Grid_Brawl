package rpgGridBrawl;
import javax.swing.JFrame;

/*
 * 	RPG GRID BRAWL
 * 
 * Designed and implemented by Jackson Aaron Kisling
 * 
 */

public class GridBrawl {
	
	protected static FileOptionsMenu FOmenu;
	protected static Fireball fireball;
	protected static Flee flee;
	private static GameEditor gameEditor;
	protected static Glorification glorification;
	protected static Humble humble;
	protected static Interaction interact;
	protected static LineOfSight los;
	protected static Move move;
	protected static Pickpocket pickpocket;
	protected static Place place;
	private static OptionsAssessor optionsAssessor;
	protected static Spend spend;
	protected static Transfer transfer;
	
	public static final String[] BRWLRS = {"Red Warrior","Red Cleric","Red Mage","Red Rogue","Blue Warrior","Blue Cleric","Blue Mage",
		"Blue Rogue","Dragon","Vampire","Abyss","Sentinel","Orc","Zombie","Goblin","Serpent","Princess","Leper","Ghost",
		"Merchant","Sword","Shield","Ring","Diamond"};
	
	public static void main(String[] args) {
		AIbox boxRed, boxBlue;
		interact = new Interaction();
		GameData crtGame = new GameData();
		flee = new Flee();
		FOmenu = new FileOptionsMenu();
		gameEditor = new GameEditor();
		place = new Place();
		move = new Move();
		humble = new Humble();
		glorification = new Glorification();
		fireball = new Fireball();
		optionsAssessor = new OptionsAssessor();
		transfer = new Transfer();
		spend = new Spend();
		pickpocket = new Pickpocket();
		
		int winCondition = 0;
		boolean selectGood = false;
		interact.tellPlayer(0, 0, false); // "Welcome to RPG Grid Brawl"
		interact.space();
		while(selectGood == false) {
			
			interact.tellPlayer(0, 1, true); 
			// "Please select from the following options:\n(S)tart a new game\n(L)oad a saved game\n(R)ando-MIZE?\n" +
			//"(G)ame Editor\n(E)xit\n\nYour selection: "
			int mainMenu = interact.mainMenuSelect();
			switch (mainMenu) {
			// this next line makes the whole main menu loop unnecessary, but maybe I want it?
			case 0: System.exit(0); // player entered some bullshit at main menu
			case 1: // new game
				crtGame = FOmenu.newGame();
				interact.tellPlayer(interact.msgBuild(crtGame, 0, 0), false); // "Game " + gameName + " has been created."
				interact.tellPlayer(interact.msgBuild(crtGame, 1, 0), false); // redPlayerName + " will take the first turn as the red player."
				interact.space();
				selectGood = true;
				break;
			case 2: // load game
				crtGame = FOmenu.loadGame();
				interact.tellPlayer(interact.msgBuild(crtGame, 2, 0), false); // "Game " + gameName + " has been loaded."
				interact.tellPlayer(interact.msgBuild(crtGame, 3, 0), false); // "Game will begin on turn number " + gameRound
				selectGood = true;
				break;
			case 3: // make new random mayhem
				crtGame = FOmenu.newRandomGame(crtGame, true);
				selectGood = true;
				break;
			case 4: // make new random regular brawl
				crtGame = FOmenu.newRandomGame(crtGame, false);
				selectGood = true;
				break;
			case 5: // game editor
				crtGame = gameEditor.execute();
				selectGood = true;
				break;
			}
		}	
		interact.tellPlayer(0, 2, true); // "PREPARE TO BRAWL..."
		interact.space();
		
		// Game runs within this loop until crtGame.evaluateWinCondition != 0
		
		while(winCondition == 0) {
			crtGame.setSuccessfulTurn(false);
			if(crtGame.isRedsTurn()) {
				crtGame.getConfig().setCrtPlayerName(crtGame.getConfig().getRedPlayerName());
				interact.tellPlayer(interact.msgBuild(crtGame, 4, 0), false); // "Player " + redPlayerName + ", it is Red's Turn."
			}
			else {
				crtGame.getConfig().setCrtPlayerName(crtGame.getConfig().getBluePlayerName());
				interact.tellPlayer(interact.msgBuild(crtGame, 5, 0), false); // "Player " + bluePlayerName + ", it is Blue's Turn."
			}
			interact.tellPlayer(interact.msgBuild(crtGame, 6, 0), false); // "It is round #" + gameRound
			interact.space();
			
			// every round is scored here
			AInode scoreNode = new AInode(crtGame);
			ScoringProfile sProf = new ScoringProfile("standard");
			Scoring scoreKeeper = new Scoring(scoreNode.toString(), sProf);
			if (crtGame.getGameRound() <= 10) crtGame.currentScore = scoreKeeper.squareOffScore();
			else crtGame.currentScore = scoreKeeper.brawlScore();
			double advantage = scoreKeeper.calculateAdvantage(crtGame.currentScore);
			interact.tellPlayer(interact.reportScores(crtGame.currentScore, advantage), false);
			interact.space();
			try {
				if (crtGame.isRedsTurn() && crtGame.getConfig().isAIRed()) {
					boxRed = new AIbox(crtGame);
					interact.tellPlayer(boxRed.getDecisionAsSuggestion(), false);
					interact.space();
				}
				if (!crtGame.isRedsTurn() && crtGame.getConfig().isAIBlue()) {
					boxBlue = new AIbox(crtGame);
					interact.tellPlayer(boxBlue.getDecisionAsSuggestion(), false);
					interact.space();
				}
			} catch (Exception ex) { ex.printStackTrace(); }
			
			interact.tellPlayer(interact.reportOnPlacementOptions(crtGame), false);
			if(crtGame.getGameRound() > 1) interact.tellPlayer(interact.reportOnLastUsed(crtGame), false);
			interact.space();
			boolean[] playerOptions = optionsAssessor.assessPlayerOptions(crtGame);
			int optionSelected = interact.OptionSelection(playerOptions);
			interact.space();
			/*0: file options: (new game, save game, load game, random game, help, AI options, exit game)
			 *1: place			2: move				3: sack treasure	4: ascend
			 *5: transfer		6: spend diamond	7: humble			8: ring attack
			 *9: pickpocket		10: dragon fire		11. game report
			 *12: Show Board (if i add more options, keep this one last)		
			 * 
			 */
			switch (optionSelected) {
				case 0: crtGame = FOmenu.execute(crtGame); 							break;
				case 1: crtGame =  place.execute(crtGame);							break;
				case 2: crtGame =  move.execute(crtGame);							break;
				case 3: crtGame =  glorification.executeSacrifice(crtGame);			break;
				case 4: crtGame =  glorification.executeGlorification(crtGame);		break;
				case 5: crtGame =  transfer.execute(crtGame);						break;
				case 6: crtGame =  spend.execute(crtGame);							break;
				case 7: crtGame =  humble.execute(crtGame);							break;
				case 8: crtGame =  fireball.burn(crtGame, 2);						break;
				case 9: crtGame =  pickpocket.execute(crtGame);						break;
				case 10: crtGame = fireball.burn(crtGame, 1);						break;
				case 11: crtGame = flee.execute(crtGame);							break;
				case 12: // show board method
					int dtBL = 1;
					int rlu = crtGame.getRedLastUsed();
					int blu = crtGame.getBlueLastUsed();
					if (rlu == -1 || blu == -1) dtBL = 1; // it's the first round of the game
					else if (crtGame.isRedsTurn()) dtBL = crtGame.getBrawler(blu).getFloor();
					else dtBL = crtGame.getBrawler(rlu).getFloor();
					if (dtBL == 5) dtBL = 4;  // The ascension space is drawn on the page with BL 4
					if (dtBL == 0) dtBL = 1; // Sometimes the piece last used is removed, e.g. humbling a CL1 Cleric
					JFrame frame = new JFrame(crtGame.getConfig().getGameName());
					frame.getContentPane().add(new DrawBoard(crtGame, dtBL));
					frame.pack();
					frame.setVisible(true);
					break;	
				case 13: interact.tellPlayer(interact.gameReport(crtGame), false); 	break;
			}
			winCondition = evaluateWinCondition(crtGame);
			crtGame = endTurn(crtGame);
			
		} // end of huge winCondition while loop
		
		endGame(crtGame, winCondition);
	}  // end of main()
	
	private static AIresult aiDecide(GameData crt) {
		AIbox decider = new AIbox(crt);
		AIresult decision = new AIresult();
		try {
			decision = decider.getLocalDecision();
			// occasionally the ai fails to come up with a viable move.  This should account for that.
			if (decision.getAction() == 0) decision = decider.getRandomAction();
		} catch(Exception ex) { ex.printStackTrace(); }
		return decision;
	}
	
	private static GameData aiExecute(AIresult decision, GameData crt) {
		String names = crt.preserveNames();
		AIhands executor = new AIhands();
		AInode node = new AInode(crt);
		String nodeString = node.toString();
		String moveMade = executor.performDesiredAction(nodeString, decision);
		node.setToString(moveMade);
		return new GameData(node.toGameDataString(names));
	}
	
	// used by Fireball and Spend classes
	// returns an int[] with all of the pieceIDs of all brawlers within line of sight of "shooter"
	protected int[] buildTargetList(GameData crt, Brawler shooter) {
		LineOfSight los = new LineOfSight();
		int rl = 8;
		int[] targetList = new int[rl];
		for (int s = 0; s <= 7; s++) targetList[s] = -1;
		int TLplace = 0;
		for (int t = 0; t < crt.getAllBrawlers().length; t++) {
			// You can't shoot at or breathe fire on Treasures.
			if (los.calculate(crt, shooter, crt.getBrawler(t)) && !(crt.getBrawler(t) instanceof Treasure) && TLplace <= 7) {
				targetList[TLplace] = crt.getBrawler(t).getPieceID();
				TLplace++;
			}
		}
		while (targetList[targetList.length - 1] == -1 && targetList.length > 1) {
			rl--;
			int[] temp = new int[rl];
			for (int t = rl - 1; t >= 0; t--) temp[t] = targetList[t];
			targetList = new int[rl];
			for (int u = 0; u < rl; u++) targetList[u] = temp[u];
		}
		return targetList;
	}	
	
	protected static int countChainBumpVictims(GameData crtCB, int fcr) {
		Brawler victim1 = crtCB.getBrawler(crtCB.getLocation(fcr).getOccupiedBy());
		if (victim1 instanceof Treasure) return 0;
		if (!(victim1 instanceof Character || victim1 instanceof Monster) || victim1.getLevel() == 1 || victim1.getFloor() == 1) return 1;
		int counter = 1;
		boolean keepLooking = true;
		while (keepLooking) {
			fcr -= 100;
			if (!isLegitSpace(crtCB, fcr)) return counter;
			try { keepLooking = crtCB.getLocation(fcr).isOccupied(); }
			catch (NullPointerException ex) { return counter; }
			if (keepLooking) {
				Brawler nextVictim = crtCB.getBrawler(crtCB.getLocation(fcr).getOccupiedBy());
				if (!(nextVictim instanceof Character || nextVictim instanceof Monster) || 
						nextVictim.getLevel() == 1 || nextVictim.getFloor() == 1) keepLooking = false;
				counter++;
			}
		}
		return counter;	
	}
	
	protected static GameData clearSpace(GameData game, int pieceID) {
		game.removeBrawler(pieceID);
		game.getBrawler(pieceID).remove();
		return game;
	}
	
	private static void endGame(GameData GM, int condition) {
		Interaction interact = new Interaction();
		interact.tellPlayer(9, condition - 1, false); // "The game is over, and the (Red/Blue) player has won (up/down)!";
		String tellRnd = "It is the top of round " + GM.getGameRound();
		if (!GM.isRedsTurn()) tellRnd = "bottom";
		interact.tellPlayer(tellRnd, false);
		System.exit(0);
	}
	
	private static GameData endTurn(GameData GM) {
		if (GM.isSuccessfulTurn()) {
			if (GM.isBluesTurn()) GM.setGameRound(GM.getGameRound() + 1);
			GM.updateStatusArray();
			GM.switchTurns();
			GM.updateSentinelAndLeperEffects();
			GM.updateGhostEffects();
			GM.updateTreasureEffects();
			GM.setSuccessfulTurn(false);
		}
		return GM;
	}
	
	// condition 0: no win condition
	// condition 1: Red wins up		condition 2: Blue wins up
	// condition 3: Red wins down	condition 4: Blue wins down
	private static int evaluateWinCondition(GameData GM) {
		int condition = 0;
		if (!GM.isSuccessfulTurn()) return 0; // don't check for a win if it wasn't a legit turn (backed out, looked at board, etc.)
		int redChars = 0;
		int blueChars = 0;
		int redGlory = 0;
		int blueGlory = 0;
		boolean brawlIsOn = (GM.getGameRound() > 10);
		for (int b = 0; b < GM.getAllBrawlers().length; b++) {
			Brawler guy = GM.getBrawler(b);
			if (guy instanceof Character && guy.isOnBoard() && guy.isRed()) redChars++;
			if (guy instanceof Character && guy.isOnBoard() && guy.isBlue()) blueChars++;
			if (guy.isRed()  && guy.isGlorified()) redGlory++;
			if (guy.isBlue() && guy.isGlorified()) blueGlory++;
		}
		if (brawlIsOn && (redGlory - blueGlory >= 2))  condition = 1;
		if (brawlIsOn && (blueGlory - redGlory >= 2)) condition = 2;
		if (brawlIsOn && blueChars == 0)  condition = 3;
		if (brawlIsOn && redChars == 0)   condition = 4;
		return condition;
	}
	
	// assumes standard board config (ccrr variable)
	protected static boolean isLegitSpace(GameData crtILS, int fcr) {
		int[] split = splitBoardSpace(fcr);
		int ccrr = 3;
		if (split[0] == 2) ccrr = 4;
		if (split[0] == 1) ccrr = 5;
		
		if (split[0] == 5 && (split[1] != 0 || split[2] != 0)) return false;
		if (split[0] < 0 || split[0] > 5) return false;
		else if (split[1] < 0 || split[1] >= ccrr) return false;
		else if (split[2] < 0 || split[2] >= ccrr) return false;
		return true;
	}
	
	public static boolean isNumeric(String str) {
		  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	// this is helper method 02 for finishMoveTurn, also used in DragonFire and RingAttack
	// it takes in GameData object, int of space on which to start bumps, and counter from checkForChainBumps()
	// it returns GameData object, with all "bump downs" executed
	protected static GameData performBumpDowns(GameData crtPBD, int bumps, int startSpace) {
		int[] bcrP = new int[3];
		bcrP = splitBoardSpace(startSpace);
		String jklm = "\n";
		String report = "";
		while (bumps > 0) {
			// if bumps == 1 then we have just 1 victim left, the one on the startspace.
			int bumpMe = crtPBD.getLocation((bcrP[0] - (bumps - 1)), bcrP[1], bcrP[2]).getOccupiedBy();
			crtPBD.getBrawler(bumpMe).getsBumped();
			if (!crtPBD.getBrawler(bumpMe).isOnBoard()) {
				jklm = interact.msgBuild(crtPBD, 21, bumpMe); // "The " + BRWLRS[bumpMe] + " has been bumped off of the board!"
				crtPBD.getLocation((bcrP[0] - (bumps - 1)), bcrP[1], bcrP[2]).setOccupied(false);
				crtPBD.getLocation((bcrP[0] - (bumps - 1)), bcrP[1], bcrP[2]).setOccupiedBy(-1);
			}
			else { // bumpMe is still on the board
				System.out.println("bumps = " + bumps);
				crtPBD.getLocation((bcrP[0] - (bumps - 1) - 1), bcrP[1], bcrP[2]).setOccupied(true);
				crtPBD.getLocation((bcrP[0] - (bumps - 1) - 1), bcrP[1], bcrP[2]).setOccupiedBy(bumpMe);
				String newSpace = (100 * (bcrP[0] - (bumps - 1) - 1)) + (10 * bcrP[1]) + bcrP[2] + "";
				jklm = ("The " + BRWLRS[bumpMe] + " has been bumped down to space #" + newSpace + ".");
				Brawler victim = crtPBD.getBrawler(bumpMe);
				if (victim instanceof Monster || (victim instanceof Character && !victim.isGlorified())) {
					jklm += ("\n   Its level has been lowered to " + crtPBD.getBrawler(bumpMe).getLevel() + ".\n");
				}
			}
			report = jklm + "\n" + report;
			bumps--;
		}
		interact.tellPlayer(report, false);
		return crtPBD;
	}
	
	public static int[] splitBoardSpace(int bcr) {
		int[] answer = new int[3];
		int x = bcr;
		answer[2] = x % 10;
		x = x / 10;
		answer[1] = x % 10;
		x = x / 10;
		answer[0] = x % 10;
		return answer;
	}
	
	// A helper method to take the # of the piece just acted upon and make it the new "last used"
	public static GameData updateLastUsed(GameData uLU, int piece_ID) {
		if (uLU.isRedsTurn()) {
			int uluLU = uLU.getRedLastUsed();
			if(uluLU != -1) uLU.getBrawler(uluLU).setUsedLast(false); //whichever piece was used last is now available again
			uLU.setRedLastUsed(piece_ID);
			uLU.getBrawler(piece_ID).setUsedLast(true); //the piece just acted upon is now "last used"
		}
		else {
			int uluLUb = uLU.getBlueLastUsed();
			if(uluLUb != -1) uLU.getBrawler(uluLUb).setUsedLast(false); //whichever piece was used last is now available again
			uLU.setBlueLastUsed(piece_ID);
			uLU.getBrawler(piece_ID).setUsedLast(true); //the piece just acted upon is now "last used"
		}
		return uLU;
	}
	
} //end of gridBrawl class
