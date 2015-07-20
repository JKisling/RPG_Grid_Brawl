package rpgGridBrawl;
import java.util.Scanner;


public class AInode {
	private boolean redsTurn, inSquareOff, redCanPlace, blueCanPlace;
	private int redLastUsed, blueLastUsed, round;
	private int[] scores;
	private double advantage;
	private Brawler[] brawlers;
	private BoardSpace[][][] gameBoard;
	private String[] blrStatuses;
	public AInode[] next;
	private int[][] playgrounds;
	
	public AInode() {
		redLastUsed = -1;
		blueLastUsed = -1;
		redCanPlace = true;
		blueCanPlace = true;
		redsTurn = true;
		brawlers = GameData.makeSetOfPieces();
		gameBoard = GameData.makeBoard();
		scores = new int[2];
		playgrounds = new int[24][64];
		blrStatuses = new String[24];
		updateStatusArray();
		setPlaygrounds();
	}
	
	public AInode(GameData GM) {
		round = GM.getGameRound();
		redCanPlace = GM.canRedPlace();
		blueCanPlace = GM.canBluePlace();
		redsTurn = GM.isRedsTurn();
		redLastUsed = GM.getRedLastUsed();
		blueLastUsed = GM.getBlueLastUsed();
		brawlers = GM.getAllBrawlers();
		gameBoard = GM.getGameBoard();
		scores = new int[2];
		if (GM.getGameRound() <= 10) inSquareOff = true;
		else inSquareOff = false;
		playgrounds = new int[(this.brawlers.length)][64];
		blrStatuses = new String[brawlers.length];
		updateStatusArray();
		setPlaygrounds();
	}
	
	public AInode(String loader) {
		Scanner walter = new Scanner(loader);
		walter.useDelimiter(",");
		this.brawlers = GameData.makeSetOfPieces();
		this.gameBoard = GameData.makeBoard();
		String ldrRead = "";
		this.setRound(walter.nextInt());
		ldrRead = walter.next();
		if ("T".equals(ldrRead)) this.setRedsTurn(true);
		else this.setRedsTurn(false);
		ldrRead = walter.next();
		if ("T".equals(ldrRead)) this.setRedCanPlace(true);
		else this.setRedCanPlace(false);
		ldrRead = walter.next();
		if ("T".equals(ldrRead)) this.setBlueCanPlace(true);
		else this.setBlueCanPlace(false);
		this.setRedLU(walter.nextInt());
		this.setBlueLU(walter.nextInt());
		int[] skrs = new int[2];
		skrs[0] = walter.nextInt();
		skrs[1] = walter.nextInt();
		this.setScores(skrs);
		this.setAdvantage(walter.nextDouble());
		for (int b = 0; b < this.getBrawlersLength(); b++) {
			this.getBrawler(b).setPieceID(walter.nextInt());
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setOnBoard(true);
			else this.getBrawler(b).setOnBoard(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setAsRed(true);
			else this.getBrawler(b).setAsRed(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setAsBlue(true);
			else this.getBrawler(b).setAsBlue(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setHandsFull(true);
			else this.getBrawler(b).setHandsFull(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setGlorified(true);
			else this.getBrawler(b).setGlorified(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setUsedLast(true);
			else this.getBrawler(b).setUsedLast(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setPoweredUp(true);
			else this.getBrawler(b).setPoweredUp(false);
			this.getBrawler(b).setLevel(walter.nextInt());
			int[] fcr = new int[3];
			for (int k = 0; k < 3; k++) fcr[k] = walter.nextInt();
			this.getBrawler(b).setFloor(fcr[0]);
			this.getBrawler(b).setColumn(fcr[1]);
			this.getBrawler(b).setRow(fcr[2]);
			this.getBrawler(b).setPossession(walter.nextInt());
			this.getBrawler(b).setSwordBonus(walter.nextInt());
			this.getBrawler(b).setShieldBonus(walter.nextInt());
			if (this.getBrawler(b).isOnBoard()) {
				this.getLocation(fcr[0], fcr[1], fcr[2]).setOccupied(true);
				this.getLocation(fcr[0], fcr[1], fcr[2]).setOccupiedBy(b);
				if (this.getBrawler(b) instanceof Treasure) {
					for (int c = 0; c < this.getBrawlersLength(); c++) {
						if (this.getBrawler(c).getPossession() == b) this.getLocation(fcr[0], fcr[1], fcr[2]).setOccupiedBy(c);
					}
				}
			}
			if (this.getBrawler(b) instanceof Ghost) this.addGhostEffect(fcr[0], fcr[1], fcr[2]);
			if (this.getBrawler(b) instanceof Leper) this.addLeperOrSentinelEffect(fcr[0], 'L');
			if (this.getBrawler(b) instanceof Sentinel) this.addLeperOrSentinelEffect(fcr[0], 'S');
			
		}
		this.blrStatuses = new String[brawlers.length];
		updateStatusArray();
		this.playgrounds = new int[(this.brawlers.length)][64];
		// setPlaygrounds();
		walter.close();
	}
	
	// relies on standard brawler set for the ghost
	public void addGhostEffect(int fl, int cc, int rr) {
		if (fl == 0 || fl == 5) return;
		int q = 2;
		if (fl == 2) q = 3;
		else if (fl == 1) q = 4;
		this.getLocation(fl,cc,rr).setGhostEffect(true);
		if ((cc > 0) && (rr > 0)) this.getLocation(fl,(cc - 1),(rr - 1)).setGhostEffect(true);
		if ((cc < q) && (rr > 0)) this.getLocation(fl,(cc + 1),(rr - 1)).setGhostEffect(true);
		if ((cc < q) && (rr < q)) this.getLocation(fl,(cc + 1),(rr + 1)).setGhostEffect(true);
		if ((cc > 0) && (rr < q)) this.getLocation(fl,(cc - 1),(rr + 1)).setGhostEffect(true);
		if (rr > 0) this.getLocation(fl,cc,(rr - 1)).setGhostEffect(true);
		if (rr < q) this.getLocation(fl,cc,(rr + 1)).setGhostEffect(true);
		if (cc > 0) this.getLocation(fl,(cc - 1),rr).setGhostEffect(true);
		if (cc < q) this.getLocation(fl,(cc + 1),rr).setGhostEffect(true);
	}
	
	public void addLeperOrSentinelEffect(int floor, char brawler) {
		if (floor == 0 || floor == 5) return;
		int cr = 3;
		switch (floor) {
		case 1: cr = 5;		break;
		case 2: cr = 4;		break;
		default: cr = 3;	break;
		}
		for (int c = 0; c < cr; c++) {
			for (int r = 0; r < cr; r++) {
				if (brawler == 'L') this.getLocation(floor, c, r).setLeperEffect(true);
				if (brawler == 'S') this.getLocation(floor, c, r).setSentinelEffect(true);
			}
		}
	}
	
	public int[] buildPlayground(int pieceID) {
		int[] playground = new int[0];
		char suit = this.getBrawler(pieceID).getSuit();
		Brawler xyz = this.getBrawler(pieceID);
		int iAmHere = xyz.getLoc();
		if (!xyz.isOnBoard() || xyz instanceof Treasure) return new int[0];
		else if (this.getLocation(xyz.getFloor(), xyz.getColumn(), xyz.getRow()).isGhostEffect()) return new int[0];
		else if (xyz instanceof Rogue && this.getBrawler("Sentinel").getFloor() == xyz.getFloor()) return new int[0];
		else {
			switch (suit) {
			case 'W': playground = this.buildPlayground_W(xyz.getPieceID());	break;
			case 'C': playground = this.buildPlayground_C(xyz.getPieceID());	break;
			case 'M': playground = this.buildPlayground_M(xyz.getPieceID());	break;
			case 'R': playground = this.buildPlayground_R(xyz.getPieceID());	break;
			}
		}
		return this.buildCleanPlayground(playground, iAmHere);		
	}
	
	// sorts playground and eliminates duplicates and zeroes
	private int[] buildCleanPlayground(int[] dirty, int iAmHere) {
		quickSort(dirty, 0, dirty.length - 1);
		int countLegit = 0;
		for (int i = 0; i < dirty.length; i++) {
			if (dirty[i] == 0 || (i > 0 && (dirty[i] == dirty[i - 1])) || (dirty[i] == iAmHere)) continue;
			else countLegit++;
		}
		int[] clean = new int[countLegit];
		int xy = 0;
		for (int j = 0; j < dirty.length; j++) {
			if (j == 0 && (dirty[j] != 0)) {
				clean[xy++] = dirty[0];
			}
			else if (j > 0) {
				if (dirty[j] == 0 || (dirty[j] == iAmHere)) continue;
				else if (dirty[j] == dirty[j - 1]) continue;
				else clean[xy++] = dirty[j];
			}
		}
		return clean;
	}
	
	// returns an array with the space above, space below, and two spaces above and below if appropriate
	// any not legit spaces will be zero in this array
	private int[] buildPlayground_upAndDown(int id) {
		int csCode = 0;
		Brawler jimbo = this.getBrawler(id);
		int[] miniPlayground = new int[4];
		int current = jimbo.getLoc();
		boolean extraSpaces = ('R' == jimbo.getSuit() || (20 == jimbo.getPossession() && jimbo instanceof Warrior));
		csCode = checkSpace(this, (current + 100));
		if (0 != csCode && !(jimbo instanceof Nemesis)) {
			miniPlayground[0] = (current + 100);
			if (2 == csCode && extraSpaces) {
				csCode = checkSpace(this, (current + 200));
				if (0 != csCode) miniPlayground[2] = (current + 200);
			}
		}
		csCode = checkSpace(this, (current - 100));
		if (csCode != 0) {
			miniPlayground[1] = (current - 100);
			if (2 == csCode && extraSpaces) {
				csCode = checkSpace(this, (current - 200));
				if (0 != csCode) miniPlayground[3] = (current - 200);
			}
		}
		return miniPlayground;
	}
	
	private int[] buildPlayground_W(int id) {
		int xy = 0, checker = 0, csCode = 0, offset = 0, travel = 3;
		boolean keepLooking = true;
		Brawler jimbo = this.getBrawler(id);
		int[] upAndDown = this.buildPlayground_upAndDown(id);
		int[] playground = new int[36];
		for (int a = 0; a < 4; a++) if (upAndDown[a] != 0) playground[xy++] = upAndDown[a];
		if (20 == jimbo.getPossession() && jimbo instanceof Warrior) travel = 4;
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
					checker = ((offset * w) + jimbo.getLoc());
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
		return playground;
	}
	
	private int[] buildPlayground_C(int id) {
		int xy = 0, csCode = 0;
		Brawler jimbo = this.getBrawler(id);
		int[] upAndDown = this.buildPlayground_upAndDown(id);
		int[] playground = new int[50];
		for (int a = 0; a <= 1; a++) if (upAndDown[a] != 0) playground[xy++] = upAndDown[a]; // we only look 1 space up and down
		int[] oGroup = new int[6];
		int loc = jimbo.getLoc();
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
			csCode = checkSpace(this, (oGroup[0] + loc));
			if (0 != csCode) {
				playground[xy++] = (oGroup[0] + loc);
				if (2 == csCode) {
					for (int g = 1; g <= 5; g++) {
						if (0 != checkSpace(this, (oGroup[g] + loc))) playground[xy++] = (oGroup[g] + loc);
					}
				}
			}
		}
		return playground;
	}
	
	private int[] buildPlayground_M(int id) {
		int xy = 0, checker = 0, csCode = 0, offset = 0, travel = 2;
		Brawler jimbo = this.getBrawler(id);
		int loc = jimbo.getLoc();
		boolean keepLooking;
		int[] upAndDown = this.buildPlayground_upAndDown(id);
		int[] playground = new int[28];
		for (int a = 0; a <= 1; a++) if (upAndDown[a] != 0) playground[xy++] = upAndDown[a]; // we only look 1 space up and down
		boolean abyssPower = false;
		if (jimbo instanceof Abyss) { // Abyss can teleport if it shares the floor with a Mage
			int myMagFloor = this.getBrawler("My Mage").getFloor();
			int opMagFloor = this.getBrawler("Enemy Mage").getFloor();
			abyssPower = ((jimbo.getFloor() == myMagFloor) || (jimbo.getFloor() == opMagFloor));
		}
		if (jimbo instanceof Mage || abyssPower) {
			int fl = this.getBrawler(id).getFloor();
			if (fl == 0) System.out.println("mage id: " + id + " is on board: " + this.getBrawler(id).isOnBoard() + " at loc" + loc);
			int ccrr = getCCRR(fl);
			
			if (jimbo.isPoweredUp() || abyssPower) {
				for (int cc = 0; cc < ccrr; cc++) {
					for (int rr = 0; rr < ccrr; rr++) {
						playground[xy++] = ((fl * 100) + (cc * 10) + rr);
					}
				}
				return playground;
			}
			else { // Mage can blink
				int jC = jimbo.getColumn(), jR = jimbo.getRow();
				for (int c = 0; c < ccrr; c++) {
					if (fl == 5) break;
					try {
						if (!this.getLocation(fl, c, jR).isOccupied()) playground[xy++] = ((fl * 100) + (c * 10) + jR);
					} catch(NullPointerException ex) {
						System.out.println(fl + " " + c + " " + jR + " something is null");
					}
					
					
					
				}
				for (int r = 0; r < ccrr; r++) {
					if (fl == 5) break;
					if (!this.getLocation(fl, jC, r).isOccupied()) playground[xy++] = ((fl * 100) + (jC * 10) + r);
				}
			}
		}
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
				checker = ((offset * w) + loc);
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
		return playground;
	}
	
	private int[] buildPlayground_R(int id) {
		int xy = 0, checker = 0, csCode = 0;
		Brawler jimbo = this.getBrawler(id);
		int loc = jimbo.getLoc();
		boolean keepLooking;
		int[] upAndDown = this.buildPlayground_upAndDown(id);
		int[] playground = new int[64];
		for (int a = 0; a <= 3; a++) if (upAndDown[a] != 0) playground[xy++] = upAndDown[a];
		
		int d1 = 0, d2 = 0, d3 = 0, d4 = 0;
		for (int p = 0; p < 4; p++) {
			keepLooking = true;
			switch(p) {
			case 0: d1 = -1;	break;
			case 1: d1 = 1;		break;
			case 2: d1 = -10;	break;
			case 3: d1 = 10;	break;
			}
			checker = loc + d1;
			csCode = checkSpace(this, checker);
			switch(csCode) {
			case 0: keepLooking = false; continue;								// not a legit space
			case 1: playground[xy++] = checker; keepLooking = false; continue;	// occupado
			case 2: playground[xy++] = checker; keepLooking = true;	 break;		// real and no occupado	
			}
			if (keepLooking) {
				for (int q = 0; q <= 1; q++) {
					d2 = changeDirection(d1, q);
					checker = loc + d1 + d2;
					csCode = checkSpace(this, checker);
					switch(csCode) {
					case 0: keepLooking = false; continue;								// not a legit space
					case 1: playground[xy++] = checker; keepLooking = false; continue;	// occupado
					case 2: playground[xy++] = checker; keepLooking = true;	 break;		// real and no occupado	
					}
					if (keepLooking) {
						for (int r = 0; r <= 1; r++) {
							d3 = changeDirection(d2, r);
							checker = loc + d1 + d2 + d3;
							csCode = checkSpace(this, checker);
							switch(csCode) {
							case 0: keepLooking = false; continue;								// not a legit space
							case 1: playground[xy++] = checker; keepLooking = false; continue;	// occupado
							case 2: playground[xy++] = checker; keepLooking = true;	 break;		// real and no occupado	
							}
							if (keepLooking) {
								for (int s = 0; s <= 1; s++) {
									d4 = changeDirection(d3, s);
									checker = loc + d1 + d2 + d3 + d4;
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
		return playground;
	}
	
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
	
	public String checkNodeIntegrity() {
		// check 1: All brawlers on board are registered as occupying the spaces they are on.
		for (int b = 0; b < 24; b++) {
			int loc = this.getBrawler(b).getLoc();
			if (this.getBrawler(b).isOnBoard()) {
				BoardSpace examined = this.getLocation(loc);
				if (!examined.isOccupied()) return ("BoardSpace " + loc + " shows as unoccupied but Brawler " + b + " is on it.");
				else {
					int occByWhat = examined.getOccupiedBy();
					if (b != occByWhat) {
						if (this.getBrawler(b) instanceof Treasure && this.getBrawler(occByWhat).getPossession() == b) {}
						else return ("Brawler " + b + " is on BoardSpace " + loc + " which is occupied by " + GridBrawl.BRWLRS[occByWhat]);
					}
				}
			}
		}
		// check 2: All empty spaces are occupied by -1
		int ccrr = 5;
		for (int f = 1; f <= 5; f++) {
			switch (f) {
			case 1: ccrr = 5;	break;
			case 2: ccrr = 4;	break;
			case 3: ccrr = 3;	break;
			case 4: ccrr = 3;	break;
			case 5: ccrr = 1;	break;
			}
			for (int c = 0; c < ccrr; c++) {
				for (int r = 0; r < ccrr; r++) {
					BoardSpace examined = this.getLocation(f,c,r);
					int occupado = examined.getOccupiedBy();
					if (!examined.isOccupied() && occupado != -1) {
						return ("BoardSpace " + f + c + r + " is showing as unoccupied but occupied by " + GridBrawl.BRWLRS[occupado]);
					}
					
				}
			}
		}
		// check 3: All brawlers that are hands full are registered as possessing a specific treasure, that is on the correct space.
		for (int p = 0; p < 16; p++) {
			if (this.getBrawler(p).isHandsFull()) {
				int loot = this.getBrawler(p).getPossession();
				int lootLoc = this.getBrawler(loot).getLoc();
				int posLoc = this.getBrawler(p).getLoc();
				if (lootLoc != posLoc) {
					return (GridBrawl.BRWLRS[p] + " on space " + posLoc + " possesses treasure " + loot + " which is on space " + lootLoc);
				}
			}
		}
		return "OK";
	}
	
	
	/**
	 * return values:
	 * 0: no win condition
	 * 1: red wins up
	 * 2: blue wins up
	 * 3: red wins down
	 * 4: blue wins down
	 */
	public int checkWinConditions() {
		int condition = 0;
		int redChars = 0;
		int blueChars = 0;
		int redGlory = 0;
		int blueGlory = 0;
		boolean brawlIsOn = (this.getRound() > 10);
		for (int b = 0; b < this.getBrawlersLength(); b++) {
			Brawler guy = this.getBrawler(b);
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
	
	// this returns a 0 if the space is not real, a 1 if it's real but occupied, and a 2 if it's real and unoccupied
	private static int checkSpace(AInode crt, int bcr) {
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
		for (int b = 0; b < this.brawlers.length; b++) {
			if (treasureID == this.getBrawler(b).getPossession()) {
				this.getBrawler(b).setPossession(-1);
				this.getBrawler(b).setHandsFull(false);
			}
		}
		// using the if statement here is a patch: if treasureID == -1 it was passed in erroneously
		if (treasureID != -1) {
			this.getBrawler(treasureID).setOnBoard(false);
			this.getBrawler(treasureID).setFloor(0);
			this.getBrawler(treasureID).setColumn(0);
			this.getBrawler(treasureID).setRow(0);
		}	
	}
	
	public boolean canRedPlace() 			{ return this.redCanPlace; }
	public boolean canBluePlace() 			{ return this.blueCanPlace; }
	public boolean isInSquareOff() 			{ return this.inSquareOff; }
	public boolean isRedsTurn() 			{ return this.redsTurn; }
	public int getRedLU() 					{ return this.redLastUsed; }
	public int getBlueLU() 					{ return this.blueLastUsed; }
	public double getAdvantage() 			{ return this.advantage; }
	public BoardSpace[][][] getBoard() 		{ return this.gameBoard; }
	// public int[] getPlayground(int index) 	{ return this.playgrounds[index]; }
	// public int[][] getPlaygrounds() 		{ return this.playgrounds; }
	public BoardSpace getLocation(int locID) {
		int[] a = GridBrawl.splitBoardSpace(locID);
		return this.gameBoard[a[0]][a[1]][a[2]];
	}
	public BoardSpace getLocation(int floor, int column, int row) { return this.gameBoard[floor][column][row]; }
	public int getRound() 					{ return this.round; }
	public int[] getScores() 				{ return this.scores; }
	public Brawler getBrawler(int ID) 		{ return this.brawlers[ID]; }
	public Brawler[] getBrawlers() 			{ return this.brawlers; }
	public int getBrawlersLength() 			{ return this.brawlers.length; }
	public String getBlrStatus(int index) 	{ return this.blrStatuses[index]; }
	public String[] getAllBlrStatuses() 	{ return this.blrStatuses; }
	
	private static int getCCRR(int floor) {
		int result;
		switch(floor) {
		case 1: result = 5; break;
		case 2: result = 4; break;
		default: result = 3;
		}
		return result;
	}
	
	
	
	// prereq: Passed in string must be a legit Brawler name
	// But, you can also pass in "My <Character Name>" or "Enemy <Character Name>"
	public Brawler getBrawler(String name) {
		for (int i = 0; i < this.brawlers.length; i++) {
			Brawler whoAmI = this.getBrawler(i);
			boolean colorMatch = ((whoAmI.isRed() && this.redsTurn) || (whoAmI.isBlue() && !this.redsTurn));
			if (name.equals("Red Warrior") && whoAmI instanceof Warrior && whoAmI.isRed()) return this.getBrawler(i);
			else if (name.equals("Red Cleric") && whoAmI instanceof Cleric && whoAmI.isRed()) return this.getBrawler(i);
			else if (name.equals("Red Mage") && whoAmI instanceof Mage && whoAmI.isRed()) return this.getBrawler(i);
			else if (name.equals("Red Rogue") && whoAmI instanceof Rogue && whoAmI.isRed()) return this.getBrawler(i);
			else if (name.equals("Blue Warrior") && whoAmI instanceof Warrior && whoAmI.isBlue()) return this.getBrawler(i);
			else if (name.equals("Blue Cleric") && whoAmI instanceof Cleric && whoAmI.isBlue()) return this.getBrawler(i);
			else if (name.equals("Blue Mage") && whoAmI instanceof Mage && whoAmI.isBlue()) return this.getBrawler(i);
			else if (name.equals("Blue Rogue") && whoAmI instanceof Rogue && whoAmI.isBlue()) return this.getBrawler(i);
			else if (name.equals("My Warrior") && whoAmI instanceof Warrior && colorMatch) return this.getBrawler(i);
			else if (name.equals("My Cleric") && whoAmI instanceof Cleric && colorMatch) return this.getBrawler(i);
			else if (name.equals("My Mage") && whoAmI instanceof Mage && colorMatch) return this.getBrawler(i);
			else if (name.equals("My Rogue") && whoAmI instanceof Rogue && colorMatch) return this.getBrawler(i);
			else if (name.equals("Enemy Warrior") && whoAmI instanceof Warrior && !colorMatch) return this.getBrawler(i);
			else if (name.equals("Enemy Cleric") && whoAmI instanceof Cleric && !colorMatch) return this.getBrawler(i);
			else if (name.equals("Enemy Mage") && whoAmI instanceof Mage && !colorMatch) return this.getBrawler(i);
			else if (name.equals("Enemy Rogue") && whoAmI instanceof Rogue && !colorMatch) return this.getBrawler(i);
			else if (name.equals("Dragon") && whoAmI instanceof Dragon) return this.getBrawler(i);
			else if (name.equals("Vampire") && whoAmI instanceof Vampire) return this.getBrawler(i);
			else if (name.equals("Abyss") && whoAmI instanceof Abyss) return this.getBrawler(i);
			else if (name.equals("Sentinel") && whoAmI instanceof Sentinel) return this.getBrawler(i);
			else if (name.equals("Orc") && whoAmI instanceof Orc) return this.getBrawler(i);
			else if (name.equals("Zombie") && whoAmI instanceof Zombie) return this.getBrawler(i);
			else if (name.equals("Goblin") && whoAmI instanceof Goblin) return this.getBrawler(i);
			else if (name.equals("Serpent") && whoAmI instanceof Serpent) return this.getBrawler(i);
			else if (name.equals("Princess") && whoAmI instanceof Princess) return this.getBrawler(i);
			else if (name.equals("Leper") && whoAmI instanceof Leper) return this.getBrawler(i);
			else if (name.equals("Ghost") && whoAmI instanceof Ghost) return this.getBrawler(i);
			else if (name.equals("Merchant") && whoAmI instanceof Merchant) return this.getBrawler(i);
			else if (name.equals("Sword") && whoAmI instanceof Sword) return this.getBrawler(i);
			else if (name.equals("Shield") && whoAmI instanceof Shield) return this.getBrawler(i);
			else if (name.equals("Ring") && whoAmI instanceof Ring) return this.getBrawler(i);
			else if (name.equals("Diamond") && whoAmI instanceof Diamond) return this.getBrawler(i);
		}
		System.out.println("AInode.getBrawler method had a problem.");
		return new Diamond();
	}
	
	// prereq: no attack here, loc is assumed to be unoccupied
	public void moveBrawler(int id, int loc) {
		this.getBrawler(id).setOnBoard(true);
		int fl = this.getBrawler(id).getFloor();
		int cc = this.getBrawler(id).getColumn();
		int rr = this.getBrawler(id).getRow();
		this.getLocation(fl, cc, rr).setOccupied(false);
		this.getLocation(fl, cc, rr).setOccupiedBy(-1);
		this.removeEffects(id);
		int[] fcr = GridBrawl.splitBoardSpace(loc);
		this.getBrawler(id).setFloor(fcr[0]);
		this.getBrawler(id).setColumn(fcr[1]);
		this.getBrawler(id).setRow(fcr[2]);
		this.getLocation(loc).setOccupied(true);
		this.getLocation(loc).setOccupiedBy(id);
		if (this.getBrawler(id).isHandsFull()) { // move treasure also
			int loot = this.getBrawler(id).getPossession();
			try {
				this.getBrawler(loot).setFloor(fcr[0]);
				this.getBrawler(loot).setColumn(fcr[1]);
				this.getBrawler(loot).setRow(fcr[2]);
			} catch(ArrayIndexOutOfBoundsException ex) {
				System.out.println("AInode.moveBrawler() tried to move a -1 possession for id" + id);
			}
		}
		if (this.getBrawler(id) instanceof Ghost) this.addGhostEffect(fcr[0], fcr[1], fcr[2]);
		else if (this.getBrawler(id) instanceof Leper) this.addLeperOrSentinelEffect(fcr[0], 'L');
		else if (this.getBrawler(id) instanceof Sentinel) this.addLeperOrSentinelEffect(fcr[0], 'S');
		if (this.getBrawler(id) instanceof Mage && this.getLocation(loc).isGhostEffect()) {
			this.getBrawler("Ghost").remove();
			if (!this.getBrawler(id).isHandsFull()) {
				int ringID = this.getBrawler("Ring").getPieceID();
				this.dispossess(ringID);
				this.takePossession(id, ringID);
			}
		}
		this.updateStatusArray();
	}
	
	// this method is mainly for test purposes
	public void quickPlacement(int id, int loc) {
		int[] fcr = GridBrawl.splitBoardSpace(loc);
		this.getBrawler(id).setOnBoard(true);
		this.getBrawler(id).setFloor(fcr[0]);
		this.getBrawler(id).setColumn(fcr[1]);
		this.getBrawler(id).setRow(fcr[2]);
		this.getLocation(loc).setOccupied(true);
		this.getLocation(loc).setOccupiedBy(id);
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
		//recursively sort two sub parts
		if (low < j)  quickSort(arr, low, j);
		if (high > i) quickSort(arr, i, high);
	}
	
	public void randomlySelectAndPlaceBrawler(boolean rTurn) {
		boolean[] avail = new boolean[24];
		byte charsOnBoard = 0;
		boolean colorMatch, procrastinator;
		int spin1 = -1;
		if (this.getBrawler("My Warrior").isOnBoard()) charsOnBoard++;
		if (this.getBrawler("My Cleric").isOnBoard()) charsOnBoard++;
		if (this.getBrawler("My Mage").isOnBoard()) charsOnBoard++;
		if (this.getBrawler("My Rogue").isOnBoard()) charsOnBoard++;
		procrastinator = ((this.getRound() + (4 - charsOnBoard)) > 10);
		for (int a = 0; a < 24; a++) {
			Brawler amAvail = this.getBrawler(a);
			if (amAvail instanceof Character) {
				colorMatch = ((rTurn && amAvail.isRed()) || (!rTurn && amAvail.isBlue()));
				if (colorMatch && !amAvail.isOnBoard()) avail[a] = true;
			}
			else if (!(amAvail instanceof Nemesis) && !amAvail.isOnBoard() && !procrastinator) avail[a] = true;
			else avail[a] = false;	
		}
		do {
			spin1 = ((int) (Math.random() * 24));
		} while (!avail[spin1]);
		
		int rFloor = 1;
		int ccrr = 5;
		if (spin1 == 16) { // Princess
			ccrr = 3;
			rFloor = 4;
		}
		else if (spin1 > 7) rFloor = (int) (Math.random() * 2.0) + 1; // floor 1 or 2 for anything other than characters
		int cc = 0, rr = 0;
		if (rFloor == 2) ccrr = 4;
		boolean goodSpot = false;
		do {
			cc = (int) (Math.random() * ccrr);
			rr = (int) (Math.random() * ccrr);
			if (!this.getLocation(rFloor, cc, rr).isOccupied()) goodSpot = true;
		} while (!goodSpot);
		
		this.getBrawler(spin1).setOnBoard(true);
		this.getBrawler(spin1).setFloor(rFloor);
		this.getBrawler(spin1).setColumn(cc);
		this.getBrawler(spin1).setRow(rr);
		this.getLocation(rFloor, cc, rr).setOccupied(true);
		this.getLocation(rFloor, cc, rr).setOccupiedBy(spin1);
		this.updateStatusArray();
		
		// next 2 lines are test code
		//String x = "   " + GridBrawl.BRWLRS[spin1] + " on space " + rFloor + cc + rr;
		//if (!rTurn) System.out.println(x);
		
	}
			
	public void removeEffects(int id) {
		Brawler fx = this.getBrawler(id);
		int ccrr = 0;
		switch (fx.getFloor()){
		case 0: return;
		case 1: ccrr = 5;	break;
		case 2: ccrr = 4;	break;
		default: ccrr = 3;	break;
		}
		if (fx instanceof Ghost) {
			for (int cc = 0; cc < ccrr; cc++) {
				for (int rr = 0; rr < ccrr; rr++) {
					this.getLocation(fx.getFloor(), cc, rr).setGhostEffect(false);
				}
			}
		}
		else if (fx instanceof Leper) {
			for (int cc = 0; cc < ccrr; cc++) {
				for (int rr = 0; rr < ccrr; rr++) {
					this.getLocation(fx.getFloor(), cc, rr).setLeperEffect(false);
				}
			}
		}
		else if (fx instanceof Sentinel) {
			for (int cc = 0; cc < ccrr; cc++) {
				for (int rr = 0; rr < ccrr; rr++) {
					this.getLocation(fx.getFloor(), cc, rr).setSentinelEffect(false);
				}
			}
		}
	}
	
	public void scoreMyself(ScoringProfile prof) {
		Scoring scorer = new Scoring(this.toString(), prof);
		if (this.round < 11) this.setScores(scorer.squareOffScore());
		else this.setScores(scorer.brawlScore());
		this.setAdvantage(scorer.calculateAdvantage(this.getScores()));
	}
	
	public void resetToBlank() {
		String blank = "0,F,F,F,-1,-1,0,0,0.0,0,F,T,F,F,F,F,F,1,0,0,0,-1,0,0,1,F,T,F,F,F,F,F,1,0,0,0,-1,0,0," +
				"2,F,T,F,F,F,F,F,1,0,0,0,-1,0,0,3,F,T,F,F,F,F,F,1,0,0,0,-1,0,0,4,F,F,T,F,F,F,F,1,0,0,0,-1,0,0," +
				"5,F,F,T,F,F,F,F,1,0,0,0,-1,0,0,6,F,F,T,F,F,F,F,1,0,0,0,-1,0,0,7,F,F,T,F,F,F,F,1,0,0,0,-1,0,0," +
				"8,F,F,F,F,F,F,F,4,0,0,0,0,0,0,9,F,F,F,F,F,F,F,4,0,0,0,0,0,0,10,F,F,F,F,F,F,F,4,0,0,0,0,0,0,11,F," +
				"F,F,F,F,F,F,4,0,0,0,0,0,0,12,F,F,F,F,F,F,F,1,0,0,0,-1,0,0,13,F,F,F,F,F,F,F,1,0,0,0,-1,0,0,14,F,F," +
				"F,F,F,F,F,1,0,0,0,-1,0,0,15,F,F,F,F,F,F,F,1,0,0,0,-1,0,0,16,F,F,F,F,F,F,F,0,0,0,0,0,0,0,17,F,F,F," +
				"F,F,F,F,0,0,0,0,0,0,0,18,F,F,F,F,F,F,F,0,0,0,0,0,0,0,19,F,F,F,F,F,F,F,0,0,0,0,0,0,0,20,F,F,F,F,F,F,F" +
				",0,0,0,0,0,0,0,21,F,F,F,F,F,F,F,0,0,0,0,0,0,0,22,F,F,F,F,F,F,F,0,0,0,0,0,0,0,23,F,F,F,F,F,F,F,0,0,0,0,0,0,0,";
		this.setToString(blank);
	}
	
	public void resetBrawlers() { this.brawlers = GameData.makeSetOfPieces(); }
	public void resetGameBoard() { this.gameBoard = GameData.makeBoard(); }
	
	public void setAdvantage(double adv) { this.advantage = adv; }
	public void setBoardSpace(BoardSpace setMe, int floor, int column, int row) { this.gameBoard[floor][column][row] = setMe; }
	public void setBrawler(Brawler setMe, int id) { this.brawlers[id] = setMe; }
	public void setRedsTurn(boolean set) { this.redsTurn = set; }
	public void setRedCanPlace(boolean set) { this.redCanPlace = set; }
	public void setRedLU(int set) { this.redLastUsed = set; }
	public void setBlueCanPlace(boolean set) { this.blueCanPlace = set; }
	public void setBlueLU(int set) { this.blueLastUsed = set; }
	public void setRound(int set) { this.round = set; }
	public void setScores(int[] skers) { this.scores = skers; }
	public void addToRedScore(int add) { this.scores[0] += add; }
	public void addToBlueScore(int add) { this.scores[1] += add; }
	public void setBlrStatus(int index, String blr) { this.blrStatuses[index] = blr; }
	public void setAllBlrStatuses(String[] blrs) { this.blrStatuses = blrs; }
	// public void setPlayground(int id, int[] pg) { this.playgrounds[id] = pg; }
	
	private void setPlaygrounds() {
		for (int i = 0; i < this.getBrawlersLength(); i++) this.playgrounds[i] = this.buildPlayground(i);
	}
	
	// basically is the same as the constructor, but without creating a new AInode object
	public void setToString(String xyz) {
		Scanner walter = new Scanner(xyz);
		walter.useDelimiter(",");
		this.resetBrawlers();
		this.resetGameBoard();
		String ldrRead = "";
		this.setRound(walter.nextInt());
		ldrRead = walter.next();
		if ("T".equals(ldrRead)) this.setRedsTurn(true);
		else this.setRedsTurn(false);
		ldrRead = walter.next();
		if ("T".equals(ldrRead)) this.setRedCanPlace(true);
		else this.setRedCanPlace(false);
		ldrRead = walter.next();
		if ("T".equals(ldrRead)) this.setBlueCanPlace(true);
		else this.setBlueCanPlace(false);
		
		this.setRedLU(walter.nextInt());
		this.setBlueLU(walter.nextInt());
		int[] skrs = new int[2];
		skrs[0] = walter.nextInt();
		skrs[1] = walter.nextInt();
		this.setScores(skrs);
		this.setAdvantage(walter.nextDouble());
		for (int b = 0; b < this.getBrawlersLength(); b++) {
			this.getBrawler(b).setPieceID(walter.nextInt());
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setOnBoard(true);
			else this.getBrawler(b).setOnBoard(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setAsRed(true);
			else this.getBrawler(b).setAsRed(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setAsBlue(true);
			else this.getBrawler(b).setAsBlue(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setHandsFull(true);
			else this.getBrawler(b).setHandsFull(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setGlorified(true);
			else this.getBrawler(b).setGlorified(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setUsedLast(true);
			else this.getBrawler(b).setUsedLast(false);
			ldrRead = walter.next();
			if ("T".equals(ldrRead)) this.getBrawler(b).setPoweredUp(true);
			else this.getBrawler(b).setPoweredUp(false);
			this.getBrawler(b).setLevel(walter.nextInt());
			int[] fcr = new int[3];
			for (int k = 0; k < 3; k++) fcr[k] = walter.nextInt();
			this.getBrawler(b).setFloor(fcr[0]);
			if (this.getBrawler(b).isOnBoard() && this.getBrawler(b).getFloor() == 0) System.out.println("Set to string is setting brawler " + b + "'s floor to zero, or: " + fcr[0]);
			this.getBrawler(b).setColumn(fcr[1]);
			this.getBrawler(b).setRow(fcr[2]);
			this.getBrawler(b).setPossession(walter.nextInt());
			this.getBrawler(b).setSwordBonus(walter.nextInt());
			this.getBrawler(b).setShieldBonus(walter.nextInt());
			if (this.getBrawler(b).isOnBoard()) {
				this.getLocation(fcr[0], fcr[1], fcr[2]).setOccupied(true);
				this.getLocation(fcr[0], fcr[1], fcr[2]).setOccupiedBy(b);
				if (this.getBrawler(b) instanceof Treasure) {
					for (int c = 0; c < this.getBrawlersLength(); c++) {
						if (this.getBrawler(c).getPossession() == b) this.getLocation(fcr[0], fcr[1], fcr[2]).setOccupiedBy(c);
					}
				}
			}
			if (this.getBrawler(b) instanceof Ghost) this.addGhostEffect(fcr[0], fcr[1], fcr[2]);
			if (this.getBrawler(b) instanceof Leper) this.addLeperOrSentinelEffect(fcr[0], 'L');
			if (this.getBrawler(b) instanceof Sentinel) this.addLeperOrSentinelEffect(fcr[0], 'S');
		}
			this.blrStatuses = new String[24];
			this.updateStatusArray();
			this.playgrounds = new int[24][64];
			this.setPlaygrounds();
			this.updateTreasureEffects();
			walter.close();	
	}
	
	public void switchTurns() {
		if (this.isRedsTurn()) this.setRedsTurn(false);
		else this.setRedsTurn(true);
	}
	
	public void takePossession(int taker, int loot) {
		this.dispossess(loot);
		this.getBrawler(taker).setHandsFull(true);
		this.getBrawler(taker).setPossession(loot);
		this.getBrawler(loot).setOnBoard(true);
		this.getBrawler(loot).setFloor(this.getBrawler(taker).getFloor());
		this.getBrawler(loot).setColumn(this.getBrawler(taker).getColumn());
		this.getBrawler(loot).setRow(this.getBrawler(taker).getRow());
		int loc = this.getBrawler(taker).getLoc();
		this.getLocation(loc).setOccupiedBy(taker);
	}
	
	// format of saveData String: game names, gameRound, who's turn, red/blue CanPlace, red/blue lastused , status of all 24 pieces
	public String toString() {
		this.updateStatusArray();
		String saveData = this.getRound() + ",";
		if (this.isRedsTurn()) saveData += "T,";
		else saveData += "F,";
		if (this.canRedPlace()) saveData += "T,";
		else saveData += "F,";
		if (this.canBluePlace()) saveData += "T,";
		else saveData += "F,";
		saveData += this.getRedLU() + ",";
		saveData += this.getBlueLU() + ",";
		int[] scores = this.getScores();
		saveData += scores[0] + ",";
		saveData += scores[1] + ",";
		saveData += this.getAdvantage() + ",";
		for (int i = 0; i < this.brawlers.length; i++) saveData += this.getBlrStatus(i) + ",";
		return saveData;	
	}	
	
	// This method is for producing a String that will work in the "load data" constructor for GameData
	public String toGameDataString(String names) {
		String saveData = names;
		this.updateStatusArray();
		saveData += this.getRound() + ",";
		if (this.isRedsTurn()) saveData += "T,";
		else saveData += "F,";
		if (this.canRedPlace()) saveData += "T,";
		else saveData += "F,";
		if (this.canBluePlace()) saveData += "T,";
		else saveData += "F,";
		saveData += this.getRedLU() + ",";
		saveData += this.getBlueLU() + ",";
		for (int i = 0; i < this.brawlers.length; i++) saveData += this.getBlrStatus(i) + ",";
		return saveData;	
	}
	
	// adapted from same method in GameData
	// generate a 10-digit string that describes the current state of a brawler in a game and save it in GameData's status array
	public void updateStatusArray() {
		String build = "";
		for (int b = 0; b < this.getBrawlersLength(); b++) {
			build = this.getBrawler(b).toString();
			this.setBlrStatus(b, build);
		}
	}	
	
	public void updateTreasureEffects() {
		int swordID = this.getBrawler("Sword").getPieceID();
		int shieldID = this.getBrawler("Shield").getPieceID();
		int ringID = this.getBrawler("Ring").getPieceID();
		for (int i = 0; i < 8; i++) {
			if (this.getBrawler(i).getPossession() == swordID) this.getBrawler(i).setSwordBonus(1);
			else this.getBrawler(i).setSwordBonus(0);
			if (this.getBrawler(i).getPossession() == shieldID) this.getBrawler(i).setShieldBonus(1);
			else this.getBrawler(i).setShieldBonus(0);
			if (this.getBrawler(i).getPossession() == ringID || this.getBrawler(i).getLevel() >= 3) this.getBrawler(i).setPoweredUp(true);
			else this.getBrawler(i).setPoweredUp(false);
		}
	}
	
} // end of AInode class
