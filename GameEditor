package rpgGridBrawl;

public class GameEditor extends GridBrawl {
	boolean glorified;
	GameData game, badInput;
	int place, CL;
	int[] splitPlace;
	String getSpace;
	
	public GameEditor() {}
	
	public GameData execute() {
		game = new GameData();
		badInput = new GameData();
		String[] gameNames = interact.getNames();
		gameNames[1] = "edit-" + gameNames[1];
		gameNames[2] = "edit-" + gameNames[2];
		game.gameName = gameNames[0];
		game.redPlayerName = gameNames[1];
		game.bluePlayerName = gameNames[2];
		game.setRedCanPlace(true);
		game.setBlueCanPlace(true);
		
		interact.tellPlayer("Enter the round this game will start on: ", true);
		game.setGameRound(interact.getIntOrNothing());
		interact.tellPlayer("Enter the piece ID for red's last used: ", true);
		game.setRedLastUsed(interact.getIntOrNothing());
		interact.tellPlayer("Enter the piece ID for blue's last used: ", true);
		game.setBlueLastUsed(interact.getIntOrNothing());
		if (game.getGameRound() == -1 || game.getRedLastUsed() == -1 || game.getBlueLastUsed() == -1) return badInput;
		
		for (int c = 0; c < game.getAllBrawlers().length; c++) { // place Brawlers on board
			boolean success;
			if (game.getBrawler(c) instanceof Character) {
				success = ezPlace(c);
				if (!success) return badInput;
				interact.tellPlayer("Is this brawler glorified? (y/n): ", true);
				glorified = interact.getYesOrNo();
				if (glorified) {
					game.getBrawler(c).setGlorified(true);
					game.getBrawler(c).setPoweredUp(true);
					game.getBrawler(c).setLevel(4);
				}
				else if (game.getBrawler(c).isOnBoard()) success = ezCL(c);
				if (!success) return badInput;
				if (CL >= 3) game.getBrawler(c).setPoweredUp(true);
				
			}
			else if (game.getBrawler(c) instanceof Nemesis || game.getBrawler(c) instanceof NPC) {
				success = ezPlace(c);
				if (!success) return badInput;
			}
			else if (game.getBrawler(c) instanceof Monster) {
				success = ezPlace(c);
				if (!success) return badInput;
				if (game.getBrawler(c).isOnBoard()) success = ezCL(c);
				if (!success) return badInput;
			}
			else if (game.getBrawler(c) instanceof Treasure) {
				boolean propah = false;
				String getSpace = "";
				while (!propah) {
					getSpace = "Enter the space on which to place the " + BRWLRS[c] + " or enter the letter 'o' for off-board: ";
					interact.tellPlayer(getSpace, true);
					place = interact.getSpaceNumber();
					if (place == -1) return badInput;
					else if (place == 0) {
						game.getBrawler(c).setFloor(0);
						game.getBrawler(c).setColumn(0);
						game.getBrawler(c).setRow(0);
						propah = true;
					}
					else { // place is assumed to be a real board space here
						int[] dstn = splitBoardSpace(place);
						if (game.getLocation(dstn[0], dstn[1], dstn[2]).isOccupied()) {
							int occBy = game.getLocation(dstn[0], dstn[1], dstn[2]).getOccupiedBy();
							Brawler meOccBy = game.getBrawler(occBy);
							if ((meOccBy instanceof Character || meOccBy instanceof Monster) && !meOccBy.isHandsFull()) {
								game.getBrawler(c).setOnBoard(true);
								game.getBrawler(c).setFloor(dstn[0]);
								game.getBrawler(c).setColumn(dstn[1]);
								game.getBrawler(c).setRow(dstn[2]);
								game.getBrawler(occBy).setHandsFull(true);
								game.getBrawler(occBy).setPossession(c);
								game.getLocation(dstn[0], dstn[1], dstn[2]).setOccupied(true);
								game.getLocation(dstn[0], dstn[1], dstn[2]).setOccupiedBy(c);
								
								String report = "The " + BRWLRS[occBy] + " will take possession of the " + BRWLRS[c] + ".";
								interact.tellPlayer(report, false);
							}
							else {
								interact.tellPlayer("That space is occupied by something that can't take a Treasure.", false);
								continue;
							}
						}
						else { // place is not occupied, stick the treasure there and be done
							game.getBrawler(c).setOnBoard(true);
							game.getBrawler(c).setFloor(dstn[0]);
							game.getBrawler(c).setColumn(dstn[1]);
							game.getBrawler(c).setRow(dstn[2]);
							game.getLocation(dstn[0], dstn[1], dstn[2]).setOccupied(true);
							game.getLocation(dstn[0], dstn[1], dstn[2]).setOccupiedBy(c);
						}
					}
					propah = true;
				} // end of propah while loop
			} // end of treasure loop
		} // end of loop to place the pieces
		interact.tellPlayer("Is it red's turn? (y/n): ", true);
		game.setRedsTurn(interact.getYesOrNo());
		game.updateStatusArray();
		game.updateGhostEffects();
		game.updateSentinelAndLeperEffects();
		game.updateTreasureEffects();
		return game;
	} // end of execute()
	
	private boolean ezCL(int bwlr) {
		interact.tellPlayer("Enter the CL of this brawler: ", true);
		CL = interact.getIntOrNothing();
		if (CL == 1 || CL == 2 || CL == 3 || CL == 4) {
			game.getBrawler(bwlr).setLevel(CL);
			return true;
		}
		else return false;
	}
	
	private boolean ezPlace(int bwlr) {
		getSpace = "Enter the space on which to place the " + BRWLRS[bwlr] + " or enter the letter 'o' for off-board: ";
		boolean proper = false;
		int[] x = new int[3];
		while (!proper) {
			interact.tellPlayer(getSpace, true);
			place = interact.getSpaceNumber();
			x = splitBoardSpace(place);
			if (place == -1) continue;
			else if (place == 0) return true;
			else if (game.getLocation(x[0], x[1], x[2]).isOccupied()) {
				interact.space();
				interact.tellPlayer("That space is already occupied.", false);
				continue;
			}
			proper = true;
		}
		splitPlace = splitBoardSpace(place);
		game.getBrawler(bwlr).setOnBoard(true);
		game.getBrawler(bwlr).setFloor(splitPlace[0]);
		game.getBrawler(bwlr).setColumn(splitPlace[1]);
		game.getBrawler(bwlr).setRow(splitPlace[2]);
		game.getLocation(x[0], x[1], x[2]).setOccupied(true);
		game.getLocation(x[0], x[1], x[2]).setOccupiedBy(bwlr);
		return true;
	}
	
} // end of GameEditor class
