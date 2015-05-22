package rpgGridBrawl;
import java.util.Scanner;
import java.util.Random;

public class GameData {
	private boolean redsTurn, redCanPlace, blueCanPlace, successfulTurn;
	String gameName, redPlayerName, bluePlayerName, crtPlayerName, gameRecord;
	private int gameRound, redLastUsed, blueLastUsed, gameID;
	String[] status = new String[24];
	int[] currentScore;
	Random randy;
	private Brawler[] brawlers;
	private BoardSpace[][][] gameBoard;
	
	public GameData() {
		this.gameRound = 1;
		this.redsTurn = true;
		this.redCanPlace = true;
		this.blueCanPlace = true;
		this.successfulTurn = false;
		this.redLastUsed = -1;
		this.blueLastUsed = -1;
		// set all brawlers to be offboard, level 0 and no statuses
		this.brawlers = makeSetOfPieces();
		this.gameBoard = makeBoard();
		this.status = new String[this.brawlers.length];
		this.currentScore = new int[2];
		this.updateStatusArray();
	}
	
	// alternate constructor that takes a "saveData" string as an argument and builds a GameData object from a saved game
	public GameData(String loadData) {
		Scanner ldr = new Scanner(loadData);
		ldr.useDelimiter(",");
		this.brawlers = makeSetOfPieces();
		this.gameBoard = makeBoard();
		String ldrRead = "";
		// start with game name and then player names
		this.gameName = ldr.next();
		this.redPlayerName = ldr.next();
		this.bluePlayerName = ldr.next();
		this.gameRound = ldr.nextInt();
		ldrRead = ldr.next();
		if ("T".equals(ldrRead)) this.redsTurn = true;
		else this.redsTurn = false;
		ldrRead = ldr.next();
		if ("T".equals(ldrRead)) this.redCanPlace = true;
		else this.redCanPlace = false;
		ldrRead = ldr.next();
		if ("T".equals(ldrRead)) this.blueCanPlace = true;
		else this.blueCanPlace = false;
		this.redLastUsed = ldr.nextInt();
		this.blueLastUsed = ldr.nextInt();
		
		for (int p = 0; p < this.brawlers.length; p++) { // set the current status of all of the brawlers
			this.brawlers[p].setPieceID(ldr.nextInt());
			ldrRead = ldr.next();
			if ("T".equals(ldrRead)) this.brawlers[p].setOnBoard(true);
			else this.brawlers[p].setOnBoard(false);
			ldrRead = ldr.next();
			if ("T".equals(ldrRead)) this.brawlers[p].setAsRed(true);
			else this.brawlers[p].setAsRed(false);
			ldrRead = ldr.next();
			if ("T".equals(ldrRead)) this.brawlers[p].setAsBlue(true);
			else this.brawlers[p].setAsBlue(true);
			ldrRead = ldr.next();
			if ("T".equals(ldrRead)) this.brawlers[p].setHandsFull(true);
			else this.brawlers[p].setHandsFull(false);
			ldrRead = ldr.next();
			if ("T".equals(ldrRead)) this.brawlers[p].setGlorified(true);
			else this.brawlers[p].setGlorified(false);
			ldrRead = ldr.next();
			if ("T".equals(ldrRead)) this.brawlers[p].setUsedLast(true);
			else this.brawlers[p].setUsedLast(false);
			ldrRead = ldr.next();
			if ("T".equals(ldrRead)) this.brawlers[p].setPoweredUp(true);
			else this.brawlers[p].setPoweredUp(false);
			this.brawlers[p].setLevel(ldr.nextInt());
			this.brawlers[p].setFloor(ldr.nextInt());
			this.brawlers[p].setColumn(ldr.nextInt());
			this.brawlers[p].setRow(ldr.nextInt());
			this.brawlers[p].setPossession(ldr.nextInt());
			this.brawlers[p].setSwordBonus(ldr.nextInt());
			this.brawlers[p].setShieldBonus(ldr.nextInt());
			
			if (brawlers[p].isOnBoard()) {
				this.gameBoard[this.brawlers[p].getFloor()][this.brawlers[p].getColumn()][this.brawlers[p].getRow()].setOccupied(true);
				this.gameBoard[this.brawlers[p].getFloor()][this.brawlers[p].getColumn()][this.brawlers[p].getRow()].setOccupiedBy(p);
			}
		}
		this.updateSentinelAndLeperEffects();
		this.updateGhostEffects();
		this.updateTreasureEffects();
		this.currentScore = new int[2];
		
		ldr.close();
	} // end of loadData constructor
	
	// an alternate constructor that generates a game on game round 11 with pieces randomly placed
	public GameData(double rando) {
		this.gameRound = 11;
		this.redsTurn = true;
		this.redCanPlace = false;
		this.blueCanPlace = false;
		this.successfulTurn = false;
		this.redLastUsed = 0;
		this.blueLastUsed = 4;
		this.brawlers = makeSetOfPieces();
		this.gameBoard = makeBoard();
		this.brawlers[0].setUsedLast(true); // Warriors are arbitrarily set as last used
		this.brawlers[4].setUsedLast(true);
		randy = new Random();
		int redLevels = 10;
		int blueLevels = 10;
		for(int i = 0; i <= 23; i++) {
			boolean success = false;
			while(!success) {
				int blR = randy.nextInt(4) + 1;
				if ((this.brawlers[i].isRed() && redLevels < 7) || (this.brawlers[i].isBlue() && blueLevels < 7)) blR--;
				if ((this.brawlers[i].isRed() && redLevels < 4) || (this.brawlers[i].isBlue() && blueLevels < 4)) blR--;
				if ((this.brawlers[i].isRed() && redLevels < 3) || (this.brawlers[i].isBlue() && blueLevels < 3) || blR < 1) blR = 1;
				int ccrr = 3;
				switch (blR) {
					case 1: ccrr = 5; break;
					case 2: ccrr = 4; break;
					default: ccrr = 3; 
				}
				int cc = randy.nextInt(ccrr);
				int rr = randy.nextInt(ccrr);
				if (!this.gameBoard[blR][cc][rr].isOccupied()) {
					this.gameBoard[blR][cc][rr].setOccupied(true);
					this.gameBoard[blR][cc][rr].setOccupiedBy(i);
					this.brawlers[i].setFloor(blR);
					this.brawlers[i].setColumn(cc);
					this.brawlers[i].setRow(rr);
					this.brawlers[i].setOnBoard(true);
					if (this.brawlers[i] instanceof Monster || this.brawlers[i] instanceof Character) this.brawlers[i].setLevel(blR);
					if (this.brawlers[i].isRed()) redLevels -= blR;	
					else if (this.brawlers[i].isBlue()) blueLevels -= blR;
					success = true;
				}
			} // end of success while
		} // end of for
		this.updateStatusArray();
		this.updateSentinelAndLeperEffects();
		this.updateGhostEffects();
		this.updateTreasureEffects();
		this.currentScore = new int[2];
	} // end of random game constructor
	
	public GameData(int test_num, boolean redPlacesPrincess) { // generate a legal random game at moment of placement score
		int blR, ccrr, cc, rr;
		gameID = test_num;
		gameRound = 11;
		redsTurn = true;
		redCanPlace = false;
		blueCanPlace = false;
		successfulTurn = false;
		redLastUsed = 0;
		blueLastUsed = 4;
		brawlers = makeSetOfPieces();
		gameBoard = makeBoard();
		brawlers[0].setUsedLast(true);
		brawlers[4].setUsedLast(true);
		randy = new Random();
		boolean success = false;
		brawlers[16].setOnBoard(true); // place the princess on the throne
		brawlers[16].setFloor(4);
		brawlers[16].setColumn(0);
		brawlers[16].setRow(0);
		gameBoard[4][0][0].setOccupied(true);
		gameBoard[4][0][0].setOccupiedBy(16);
		brawlers[8].setOnBoard(true); // place the dragon
		brawlers[8].setFloor(4);
		brawlers[8].setColumn(1);
		brawlers[8].setRow(1);
		gameBoard[4][1][1].setOccupied(true);
		gameBoard[4][1][1].setOccupiedBy(8);
		for(int i = 0; i <= 23; i++) {
			if ((i >= 8 && i <= 11) || i == 16) continue; // nemeses and princess dealt with separately
			success = false;
			while(!success) {
				blR = randy.nextInt(3) + 1;
				if (brawlers[i] instanceof Character) blR = 1;
				if (brawlers[i] instanceof Monster) blR = randy.nextInt(2) + 1;
				ccrr = 3; // this is the board size, default to 3x3
				switch (blR) {
					case 1: ccrr = 5; break;
					case 2: ccrr = 4; break;
					default: ccrr = 3; 
				}
				cc = randy.nextInt(ccrr);
				rr = randy.nextInt(ccrr);
				if (!this.gameBoard[blR][cc][rr].isOccupied()) {
					gameBoard[blR][cc][rr].setOccupied(true);
					gameBoard[blR][cc][rr].setOccupiedBy(i);
					brawlers[i].setFloor(blR);
					brawlers[i].setColumn(cc);
					brawlers[i].setRow(rr);
					brawlers[i].setOnBoard(true);
					if (brawlers[i] instanceof Monster) brawlers[i].setLevel(blR);
					success = true;
				}	
			} // end of success while
		}	// end of for loop through brawlers
		this.updateStatusArray();
		this.updateSentinelAndLeperEffects();
		this.updateGhostEffects();
		this.updateTreasureEffects();
		this.currentScore = new int[2];
	} // end of legal random round 11 game constructor
	
	// The "Playground" is a set of int references to board spaces to which a given Brawler could travel in a single turn.
	// It includes occupied board spaces that the Brawler could, in theory, attack, but it doesn't determine if the attack is valid.
	// This should eventually be converted into an ArrayList (which would work better) but that is a project for another day.
	public int[] buildPlayground(int pieceID) {
		int roughSize = 0;
		int realSize = 0;
		Brawler amie = this.getBrawler(pieceID);
		if (!amie.isOnBoard()) { return new int[0]; }
		switch (amie.getSuit()) {
		case 'W': roughSize = 36;	realSize = 36;		break;
		case 'C': roughSize = 50;	realSize = 28;		break;
		case 'M': roughSize = 18;	realSize = 18;		break;
		case 'R': roughSize = 64;	realSize = 30;		break;
		}
		int[] playground = new int[roughSize];
		int fl = amie.getFloor() * 100;
		int cc = amie.getColumn() * 10;
		int rr = amie.getRow();
		int xy = 0; // the steadily increasing index for playground[]
		int checker = 0; // the board space that gets passed into checkSpace()
		int csCode = 0; // the int returned from checkSpace()
		int offset = 0; // the int added to current board space to get one that is being checked
		int travel = 0; // the number of spaces that can be traveled in one turn
		boolean keepLooking = true;
		
		// The spaces above and below are common to all suits (with exceptions for Warrior and Rogue)
		csCode = checkSpace(this, (fl + 100 + cc + rr));
		if (0 != csCode && !(amie instanceof Nemesis)) {
			playground[xy++] = (fl + 100 + cc + rr);
			if (2 == csCode && ('R' == amie.getSuit() || (20 == amie.getPossession() && amie instanceof Warrior))) {
				csCode = checkSpace(this, (fl + 200 + cc + rr));
				if (0 != csCode) playground[xy++] = (fl + 200 + cc + rr);
			}
		}
		csCode = checkSpace(this, (fl - 100 + cc + rr));
		if (csCode != 0) {
			playground[xy++] = (fl - 100 + cc + rr);
			if (2 == csCode && ('R' == amie.getSuit() || (20 == amie.getPossession() && amie instanceof Warrior))) {
				csCode = checkSpace(this, (fl - 200 + cc + rr));
				if (0 != csCode) playground[xy++] = (fl - 200 + cc + rr);
			}
		}
		switch(amie.getSuit()) {
		case 'W': // Warrior suit
			travel = 3;
			if (20 == amie.getPossession()) travel = 4;
			for (int i = 0; i <= 7; i++) {
				keepLooking = true;
				switch (i) {
				case 0: offset = -11; 	break;
				case 1: offset =  -1;	break;
				case 2: offset =   9; 	break;
				case 3: offset =  10;	break;
				case 4: offset =  11; 	break;
				case 5: offset =   1;	break;
				case 6: offset =  -9; 	break;
				case 7: offset = -10;	break;
				}
				for (int w = 1; w <= travel; w++) {
					if (keepLooking) {
						checker = ((offset * w) + fl + cc + rr);
						csCode = checkSpace(this, checker);
						switch(csCode) {
						case 0: // not a legit space
							keepLooking = false;
							continue;
						case 1: // occupado
							playground[xy++] = checker;
							keepLooking = false;
							continue;
						case 2: // real and no occupado
							playground[xy++] = checker;
							keepLooking = true;
							continue;
						}
					}
				} // end of w loop
			} // end of i loop
			break;
		case 'C': // Cleric suit
			int[] oGroup = new int[6];
			for (int c = 0; c <= 7; c++) {
				switch(c) {
				case 0: oGroup[0] = -11;	oGroup[1] = -21;	oGroup[2] = -22;	oGroup[3] = -12;	oGroup[4] = -2;	oGroup[5] = -20; 	break;
				case 1: oGroup[0] =  -1;	oGroup[1] = -12;	oGroup[2] =  -2;	oGroup[3] =   8; 	oGroup[4] = 0;	oGroup[5] = 0; 		break;
				case 2: oGroup[0] =   9;	oGroup[1] =   8;	oGroup[2] =  18;	oGroup[3] =  19;	oGroup[4] = -2;	oGroup[5] = 20; 	break;
				case 3: oGroup[0] =  10;	oGroup[1] =  19;	oGroup[2] =  20;	oGroup[3] =  21; 	oGroup[4] = 0;	oGroup[5] = 0; 		break;
				case 4: oGroup[0] =  11;	oGroup[1] =  21;	oGroup[2] =  22;	oGroup[3] =  12; 	oGroup[4] = 20;	oGroup[5] = 2; 		break;
				case 5: oGroup[0] =   1;	oGroup[1] =  12;	oGroup[2] =   2;	oGroup[3] =  -8; 	oGroup[4] = 0;	oGroup[5] = 0; 		break;
				case 6: oGroup[0] =  -9;	oGroup[1] =  -8;	oGroup[2] = -18;	oGroup[3] = -19;	oGroup[4] = 2;	oGroup[5] = -20; 	break;
				case 7: oGroup[0] = -10;	oGroup[1] = -19;	oGroup[2] = -20;	oGroup[3] = -21; 	oGroup[4] = 0;	oGroup[5] = 0; 		break;
				}
				csCode = checkSpace(this, (oGroup[0] + fl + cc + rr));
				if (0 != csCode) {
					playground[xy++] = (oGroup[0] + fl + cc + rr);
					if (2 == csCode) {
						for (int g = 1; g <= 5; g++) {
							if (0 != checkSpace(this, ((oGroup[g] + fl + cc + rr)))) playground[xy++] = (oGroup[g] + fl + cc + rr);
						}
					}
				}
			}
			break;
		case 'M': // Mage suit
			travel = 2;
			for (int i = 0; i <= 7; i++) {
				keepLooking = true;
				switch (i) {
				case 0: offset = -11; 	break;
				case 1: offset =  -1;	break;
				case 2: offset =   9; 	break;
				case 3: offset =  10;	break;
				case 4: offset =  11; 	break;
				case 5: offset =   1;	break;
				case 6: offset =  -9; 	break;
				case 7: offset = -10;	break;
				}
				for (int w = 1; w <= travel; w++) {
					checker = (offset * w + fl + cc + rr);
					if (keepLooking) {
						csCode = checkSpace(this, checker);
						switch(csCode) {
						case 0: // not a legit space
							keepLooking = false;
							continue;
						case 1: // occupado
							playground[xy++] = checker;
							keepLooking = false;
							continue;
						case 2: // real and no occupado
							playground[xy++] = checker;
							keepLooking = true;
							continue;
						}
					}
				} // end of w loop
			} // end of i loop
			break;	
		case 'R': // Rogue suit
			int d1 = 0, d2 = 0, d3 = 0, d4 = 0;
			for (int p = 0; p < 4; p++) {
				keepLooking = true;
				switch(p) {
				case 0: d1 = -1;	break;
				case 1: d1 = 1;		break;
				case 2: d1 = -10;	break;
				case 3: d1 = 10;	break;
				}
				checker = fl + cc + rr + d1;
				csCode = checkSpace(this, checker);
				switch(csCode) {
				case 0: keepLooking = false; continue;								// not a legit space
				case 1: playground[xy++] = checker; keepLooking = false; continue;	// occupado
				case 2: playground[xy++] = checker; keepLooking = true;	 break;		// real and no occupado	
				}
				if (keepLooking) {
					for (int q = 0; q <= 1; q++) {
						d2 = changeDirection(d1, q);
						checker = fl + cc + rr + d1 + d2;
						csCode = checkSpace(this, checker);
						switch(csCode) {
						case 0: keepLooking = false; continue;								// not a legit space
						case 1: playground[xy++] = checker; keepLooking = false; continue;	// occupado
						case 2: playground[xy++] = checker; keepLooking = true;	 break;		// real and no occupado	
						}
						if (keepLooking) {
							for (int r = 0; r <= 1; r++) {
								d3 = changeDirection(d2, r);
								checker = fl + cc + rr + d1 + d2 + d3;
								csCode = checkSpace(this, checker);
								switch(csCode) {
								case 0: keepLooking = false; continue;								// not a legit space
								case 1: playground[xy++] = checker; keepLooking = false; continue;	// occupado
								case 2: playground[xy++] = checker; keepLooking = true;	 break;		// real and no occupado	
								}
								if (keepLooking) {
									for (int s = 0; s <= 1; s++) {
										d4 = changeDirection(d3, s);
										checker = fl + cc + rr + d1 + d2 + d3 + d4;
										csCode = checkSpace(this, checker);
										switch(csCode) {
										case 0: keepLooking = false; continue;								// not a legit space
										case 1: playground[xy++] = checker; keepLooking = false; continue;	// occupado
										case 2: playground[xy++] = checker; keepLooking = true;	 break;		// real and no occupado	
										}
									} // end of s loop
								} // end of if 4
								
							} // end of r loop
						} // end of if 3
						
					} // end of q loop
				} // end of if 2
			} // end of p loop
			break;
		} // end of suit switch
		// The rest of this method sorts the playground and eliminates the duplicates
		quickSort(playground, 0, xy);
		int[] realPlayground = new int[realSize];
		int v = 0;
		for (int q = 0; q < roughSize; q++) {
			if (q == 0) {
				realPlayground[q] = playground[v];
				v++;
				continue;
			}
			if (playground[q] != playground[q - 1]) {
				try {
					realPlayground[v] = playground[q];
				}
				catch(ArrayIndexOutOfBoundsException ex) {
					// this rarely happens but if it does, something needs to be fixed.
					System.out.println("v = " + v + " and q = " + q + " and playground[q] = " + playground[q]);
				}
				v++;
			}
		}
		return realPlayground;
	} // end of buildPlayground()
	
	// This is a helper method for buildPlayground() to determine which space should be examined next in Rogue suit
	private static int changeDirection(int last, int whichPass) {
		final int NORTH = -1;
		final int SOUTH = 1;
		final int WEST = -10;
		final int EAST = 10;
		int next = 0;
		if (whichPass == 0) {
			switch(last) {
			case NORTH: next = WEST;	break;
			case SOUTH: next = WEST;	break;
			case WEST: next = NORTH;	break;
			case EAST: next = NORTH;	break;
			}
		}
		else {
			switch(last) {
			case NORTH: next = EAST;	break;
			case SOUTH: next = EAST;	break;
			case WEST: next = SOUTH;	break;
			case EAST: next = SOUTH;	break;
			}
		}
		return next;
	}
	
	// Another helper method for buildPlayground()
	// this returns a 0 if the space is not real, a 1 if it's real but occupied, and a 2 if it's real and unoccupied
	private static int checkSpace(GameData crt, int bcr) {
		int[] bcrA = GridBrawl.splitBoardSpace(bcr);
		try {
			if (crt.gameBoard[bcrA[0]][bcrA[1]][bcrA[2]] == null) {
				return 0;
			}
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			return 0;
		}
		if (crt.gameBoard[bcrA[0]][bcrA[1]][bcrA[2]].isOccupied()) return 1;
		else return 2;
	}
	
	public void dispossess(int treasureID) {
		for (int b = 0; b < this.getAllBrawlers().length; b++) {
			if (treasureID == this.getBrawler(b).getPossession()) {
				this.getBrawler(b).setPossession(-1);
				this.getBrawler(b).setHandsFull(false);
			}
		}
		this.getBrawler(treasureID).remove();
	}
	
	public boolean isRedsTurn() {return this.redsTurn;}
	public boolean isBluesTurn() {return !(this.redsTurn);}
	public void setRedsTurn(boolean a) {this.redsTurn = a;}
	public boolean canRedPlace() {return this.redCanPlace;}
	public void setRedCanPlace(boolean b) {this.redCanPlace = b;}
	public boolean canBluePlace() {return this.blueCanPlace;}
	public void setBlueCanPlace(boolean c) {this.blueCanPlace = c;}
	public boolean isSuccessfulTurn() {return this.successfulTurn;}
	public void setSuccessfulTurn(boolean d) {this.successfulTurn = d;}
	public int getGameRound() {return this.gameRound;}
	public void setGameRound(int f) {this.gameRound = f;}
	public int getRedLastUsed() {return this.redLastUsed;}
	public void setRedLastUsed(int g) {this.redLastUsed = g;}
	public int getBlueLastUsed() {return this.blueLastUsed;}
	public void setBlueLastUsed(int h) {this.blueLastUsed = h;}
	public int getGameID() {return this.gameID;}
	public void setGameID(int h) {this.gameID = h;}
	public Brawler[] getAllBrawlers() {return this.brawlers;}
	public void setAllBrawlers(Brawler[] j) {this.brawlers = j;}
	public Brawler getBrawler(int id) {return this.brawlers[id];}
	public void setBrawler(Brawler k, int id) {this.brawlers[id] = k;}
	public BoardSpace[][][] getGameBoard() {return this.gameBoard;}
	public void setGameBoard(BoardSpace[][][] l) {this.gameBoard = l;}
	public BoardSpace getLocation(int fcr) {
		int[] answer = new int[3];
		int x = fcr;
		answer[2] = x % 10;
		x = x / 10;
		answer[1] = x % 10;
		x = x / 10;
		answer[0] = x % 10;
		return this.gameBoard[answer[0]][answer[1]][answer[2]];
	}
	
	public void setLocation(BoardSpace m, int fcr) {
		int[] answer = new int[3];
		int x = fcr;
		answer[2] = x % 10;
		x = x / 10;
		answer[1] = x % 10;
		x = x / 10;
		answer[0] = x % 10;
		this.gameBoard[answer[0]][answer[1]][answer[2]] = m;
	}
	public BoardSpace getLocation(int f, int c, int r) {return this.gameBoard[f][c][r];}
	public void setLocation(BoardSpace n, int f, int c, int r) {this.gameBoard[f][c][r] = n;}
	
	
	// this makes the standard board
	public static BoardSpace[][][] makeBoard() {
		BoardSpace[][][] gameBoard = new BoardSpace[6][5][5];
		for(int gbB = 1; gbB <= 5; gbB++) {
			for(int gbC = 0; gbC <= 4; gbC++) {
				for(int gbR = 0; gbR <= 4; gbR++) {
					if ((gbB == 1) || (gbB == 2 && gbC < 4 && gbR < 4) || ((gbB == 3 || gbB == 4) && gbC < 3 && gbR < 3) || (gbB == 5 && gbC == 0 && gbR == 0)) {
						gameBoard[gbB][gbC][gbR] = new BoardSpace(gbB, gbC, gbR);
					}
					else gameBoard[gbB][gbC][gbR] = null;
				}
			}
		}
		gameBoard[0][0][0] = new BoardSpace(0,0,0); // The offboard space;
		return gameBoard;
	}
		
	// this builds the standard set of Brawlers
	public static Brawler[] makeSetOfPieces() {
		Brawler[] freshSet = new Brawler[24];
		Warrior  redWarrior  = new Warrior(0);			freshSet[0]  = redWarrior;
		Cleric	 redCleric   = new Cleric(1);			freshSet[1]  = redCleric;
		Mage	 redMage	 = new Mage(2);				freshSet[2]  = redMage;
		Rogue	 redRogue    = new Rogue(3);			freshSet[3]  = redRogue;
		Warrior  blueWarrior = new Warrior(4);			freshSet[4]  = blueWarrior;
		Cleric	 blueCleric  = new Cleric(5);			freshSet[5]  = blueCleric;
		Mage	 blueMage	 = new Mage(6);				freshSet[6]  = blueMage;
		Rogue	 blueRogue   = new Rogue(7);			freshSet[7]  = blueRogue;
		Dragon   dragon		 = new Dragon();			freshSet[8]  = dragon;
		Vampire  vampire	 = new Vampire();			freshSet[9]  = vampire;
		Abyss 	 abyss    	 = new Abyss();				freshSet[10] = abyss;
		Sentinel sentinel    = new Sentinel();			freshSet[11] = sentinel;
		Orc		 orc		 = new Orc(); 				freshSet[12] = orc;
		Zombie   zombie		 = new Zombie(); 			freshSet[13] = zombie;
		Goblin   goblin		 = new Goblin(); 			freshSet[14] = goblin;
		Serpent  serpent	 = new Serpent(); 			freshSet[15] = serpent;
		Princess princess	 = new Princess();			freshSet[16] = princess;
		Leper	 leper		 = new Leper();				freshSet[17] = leper;
		Ghost	 ghost		 = new Ghost();				freshSet[18] = ghost;
		Merchant merchant	 = new Merchant();			freshSet[19] = merchant;
		Sword	 sword		 = new Sword();				freshSet[20] = sword;
		Shield	 shield		 = new Shield();			freshSet[21] = shield;
		Ring	 ring		 = new Ring();				freshSet[22] = ring;
		Diamond  diamond	 = new Diamond();			freshSet[23] = diamond;
		return freshSet;
	}
	
	// this method retrieved from http://www.programcreek.com/2012/11/quicksort-array-in-java/
	// it's only used (so far) by buildPlayground()
	public static void quickSort(int[] arr, int low, int high) {
		if (arr == null || arr.length == 0 || low >= high) return;
		//pick the pivot
		int middle = low + (high - low) / 2;
		int pivot = arr[middle];
		//make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (arr[i] < pivot) i++;
			while (arr[j] > pivot) j--;
			if (i <= j) {
				int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
				i++;
				j--;
			}
		}
		if (low < j)  quickSort(arr, low, j);
		if (high > i) quickSort(arr, i, high);
	}
	
	public void rebuildAllPlaygrounds() {
		for (int j = 0; j < this.brawlers.length; j++) {
			this.getBrawler(j).setPlayground(this.buildPlayground(j));
		}
	}
	
	public void removeBrawler(int pieceID) {
		int fl = this.getBrawler(pieceID).getFloor();
		int cc = this.getBrawler(pieceID).getColumn();
		int rr = this.getBrawler(pieceID).getRow();
		this.getLocation(fl, cc, rr).setOccupied(false);
		this.getLocation(fl, cc, rr).setOccupiedBy(-1);
	}

	public void switchTurns() {
		if (this.redsTurn) this.redsTurn = false;
		else this.redsTurn = true;
	}

	public void takePossession(int taker, int loot) {
		this.dispossess(loot);
		this.getBrawler(taker).setHandsFull(true);
		this.getBrawler(taker).setPossession(loot);
		this.getBrawler(loot).setOnBoard(true);
		this.getBrawler(loot).setFloor(this.getBrawler(taker).getFloor());
		this.getBrawler(loot).setColumn(this.getBrawler(taker).getColumn());
		this.getBrawler(loot).setRow(this.getBrawler(taker).getRow());
	}
	
	// overrides toString() in order to convert a GameData object into a readable string
	// format of saveData String: game names, gameRound, who's turn, red/blue CanPlace, red/blue lastused , status of all 24 pieces
	public String toString() {
		String saveData = this.gameName + ",";
		saveData += this.redPlayerName + ",";
		saveData += this.bluePlayerName + ",";
		saveData += this.gameRound + ",";
		if (this.redsTurn) saveData += "T,";
		else saveData += "F,";
		if (this.redCanPlace) saveData += "T,";
		else saveData += "F,";
		if (this.blueCanPlace) saveData += "T,";
		else saveData += "F,";
		saveData += this.redLastUsed + ",";
		saveData += this.blueLastUsed + ",";
		for (int i = 0; i < this.getAllBrawlers().length; i++) saveData += this.status[i] + ",";
		return saveData;	
	}

	public void updateGameRecord(String action, String subject, int dstn, String object) {
		String oh = "";
		if (gameRound < 10) oh = "0";
		String report = oh;
		if (redsTurn) report += gameRound + "R" + "\t";
		else report += gameRound + "B" + "\t";
		report += action + "\t" + subject + "\t";
		if (action.equals("place") || action.equals("move")) report += dstn + "\t";
		if (!object.equals("none")) report += object;
		gameRecord += report + "\n";
	}
		
	public void updateGhostEffects() {
		for (int bl = 1; bl < 5; bl++) {
			int cr = this.gameBoard[bl].length;
			for (int c = 0; c < cr; c++) {
				for (int r = 0; r < cr; r++) {
					try {
					this.gameBoard[bl][c][r].setGhostEffect(false);
					} catch (NullPointerException ex) {}
				}
			}
		}
		Brawler ghost = new Ghost();
		for (int g = 0; g < this.brawlers.length; g++) if (this.brawlers[g] instanceof Ghost) ghost = this.brawlers[g];
		if (ghost.isOnBoard()) {
			int q = 4;
			int fl = ghost.getFloor();
			if (2 == fl) q = 3;
			else if (fl > 2) q = 2;
			int cc = ghost.getColumn();
			int rr = ghost.getRow();
			if ((cc > 0) && (rr > 0)) this.gameBoard[fl][cc - 1][rr - 1].setGhostEffect(true);
			if ((cc < q) && (rr > 0)) this.gameBoard[fl][cc + 1][rr - 1].setGhostEffect(true);
			if ((cc < q) && (rr < q)) this.gameBoard[fl][cc + 1][rr + 1].setGhostEffect(true);
			if ((cc > 0) && (rr < q)) this.gameBoard[fl][cc - 1][rr + 1].setGhostEffect(true);
			if (rr > 0) this.gameBoard[fl][cc][rr - 1].setGhostEffect(true);
			if (rr < q) this.gameBoard[fl][cc][rr + 1].setGhostEffect(true);
			if (cc > 0) this.gameBoard[fl][cc - 1][rr].setGhostEffect(true);
			if (cc < q) this.gameBoard[fl][cc + 1][rr].setGhostEffect(true);	
		}
	}

	public void updateSentinelAndLeperEffects() {
		try {
			// find the sentinel and leper
			int sentinelLevel = 0;
			int leperLevel = 0;
			for (int i = 0; i < this.brawlers.length; i++) {
				if (this.brawlers[i] instanceof Sentinel && this.brawlers[i].isOnBoard()) sentinelLevel = i;
				if (this.brawlers[i] instanceof Leper && this.brawlers[i].isOnBoard()) leperLevel = i;
			}
			if (sentinelLevel != 0) sentinelLevel = this.brawlers[sentinelLevel].getFloor();
			if (leperLevel != 0) leperLevel = this.brawlers[leperLevel].getFloor();
			
			// turn on sentinel and leper effects on board levels where they are, and turn them off where they aren't
			for (int bl = 1; bl <= 4; bl++) {
				for (int c = 0; c < this.gameBoard[bl].length; c++) {
					for (int r = 0; r < this.gameBoard[bl].length; r++) {
						if (this.gameBoard[bl][c][r] != null) {
							if (bl == sentinelLevel) this.gameBoard[bl][c][r].setSentinelEffect(true);
							else this.gameBoard[bl][c][r].setSentinelEffect(false);
							if (bl == leperLevel) this.gameBoard[bl][c][r].setLeperEffect(true);
							else this.gameBoard[bl][c][r].setLeperEffect(false);
						}	
					}
				}
			} // end of triple for loop
		}
		catch(ArrayIndexOutOfBoundsException ex) {
			System.out.println("updateSentinelAndLeperEffects() threw an Array exception.");
		}
	}	

	// generate a 10-digit string that describes the current state of a brawler in a game and save it in GameData's status array
	// piece ID, onBoard, isred, isBlue, handsFull, glorified, usedLast, poweredUp
	// level, floor, column, row, possession, swordBonus, shieldBonus
	public void updateStatusArray() {
		String build = "";
		for (int b = 0; b < brawlers.length; b++) {
			Brawler fred = this.getBrawler(b);
			build = fred.getPieceID() + ",";
			if (fred.isOnBoard()) build += "T,";
			else build += "F,";
			
			if (fred.isRed())build += "T,";
			else build += "F,";
		
			if (fred.isBlue())build += "T,";
			else build += "F,";
			
			if (fred.isHandsFull()) build += "T,";
			else build += "F,";
			
			if (fred.isGlorified()) build += "T,";
			else build += "F,";
			
			if (fred.isUsedLast()) build += "T,";
			else build += "F,";
			
			if (fred.isPoweredUp()) build += "T,";
			else build += "F,";
			
			build += fred.getLevel() + ",";
			build += fred.getFloor() + ",";
			build += fred.getColumn() + ",";
			build += fred.getRow() + ",";
			build += fred.getPossession() + ",";
			build += fred.getSwordBonus() + ",";
			build += fred.getShieldBonus();
			this.status[b] = build;
		}
	}

	public void updateTreasureEffects() {
		Brawler swd = new Sword();
		Brawler shd = new Shield();
		Brawler rng = new Ring();
		for (int b = 0; b < this.getAllBrawlers().length; b ++) {
			if (this.getBrawler(b) instanceof Sword) swd = (Sword) this.getBrawler(b);
			if (this.getBrawler(b) instanceof Shield) shd = (Shield) this.getBrawler(b);
			if (this.getBrawler(b) instanceof Ring) rng = (Ring) this.getBrawler(b);
		}
		for (int c = 0; c < this.getAllBrawlers().length; c ++) {
			if (this.getBrawler(c) instanceof Character && swd.getPieceID() == this.getBrawler(c).getPossession()) this.getBrawler(c).setSwordBonus(1);
			else this.getBrawler(c).setSwordBonus(0);
			
			if (this.getBrawler(c) instanceof Character && shd.getPieceID() == this.getBrawler(c).getPossession()) this.getBrawler(c).setShieldBonus(1);
			else this.getBrawler(c).setShieldBonus(0);
			
			if (this.getBrawler(c) instanceof Character && rng.getPieceID() == this.getBrawler(c).getPossession()) this.getBrawler(c).setPoweredUp(true);
			else if (this.getBrawler(c) instanceof Character && this.getBrawler(c).getLevel() >= 3) this.getBrawler(c).setPoweredUp(true);
			else this.getBrawler(c).setPoweredUp(false);
		}
	}
} // end of GameData class
