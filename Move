package rpgGridBrawl;

public class Move extends GridBrawl {
	
	public Move() {}
	
	public GameData execute(GameData crtM) {
		boolean[] available = sortAvailable(crtM);
		boolean goOn = true;
		int slct = 0;
		int dstn = 0;
		int victim = -1;
		interact.tellPlayer(interact.msgBuild(crtM, 10, -1), false); // "Player " + crtPlayerName + ", these pieces are available to move this turn:"
		for(int i = 0; i < crtM.getAllBrawlers().length; i++) if(available[i]) System.out.println(i + ". " + BRWLRS[i]);
		System.out.println();
		while(goOn) {
			interact.tellPlayer(4, 0, true); // "Enter the number of your selection or anything else to return to main menu: "
			slct = interact.getBrawlerNumber(crtM.getAllBrawlers().length);
			if (slct == -1) {
				crtM.setSuccessfulTurn(false);
				return crtM;
			}
			if (available[slct]) {
				int origin = (crtM.getBrawler(slct).getFloor() * 100) + (crtM.getBrawler(slct).getColumn() * 10) + crtM.getBrawler(slct).getRow();
				String youHaveSelected = ("You have selected the " + BRWLRS[slct] + " which is currently on space # " + origin);
				interact.tellPlayer(youHaveSelected, false);
				goOn = false;
			}
			else {
				interact.tellPlayer(4, 1, false); // "That brawler is not available to be moved by you this turn."
				goOn = true;
			}
		}
		// at this point slct is the number of a legit brawler to be moved.
		goOn = true;
		while(goOn) {
			interact.tellPlayer(4, 2, true); // "Enter the number of the destination space: "
			dstn = interact.getSpaceNumber();
			if (!isLegitSpace(crtM, dstn)) {
				interact.tellPlayer(4,  3, false); // "That is not a real space on this board."
				crtM.setSuccessfulTurn(false);
				return crtM;
			}
			int legit = isLegitDestination(crtM, slct, dstn);
			if (legit == 0) { // we have a legitimate destination
				goOn = false; 
			} 
			else { // some violation
				interact.tellPlayer(interact.moveILDErrorMsg(crtM, slct, legit), false);
				goOn = true;
				continue;
			}
			// at this point dstn is considered a viable destination, now check if it's occupied
			if (crtM.getLocation(dstn).isOccupied()) {
				victim = crtM.getLocation(dstn).getOccupiedBy();
				if (!canAttackThat(crtM, slct, victim)) {
					String cannotAttack = ("The " + BRWLRS[slct] + " cannot attack the " + BRWLRS[victim] + " this turn.");
					interact.tellPlayer(cannotAttack, false);
					interact.tellPlayer(interact.msgBuild(crtM, 14, slct), false); // "Please select a new destination for the " + BRWLRS[slct] + "."
					goOn = true;
					continue;
				}
			}
		} // end of destination selection loop
		// at this point the piece to move and its destination are selected, and are legit.
		int atNow = (crtM.getBrawler(slct).getFloor() * 100) + (crtM.getBrawler(slct).getColumn() * 10) + crtM.getBrawler(slct).getRow();
		String yhs = ("You have selected to move the " + BRWLRS[slct] + " from space #" + atNow + " to space #" + dstn);
		interact.tellPlayer(yhs, false);
		if (victim != -1) interact.tellPlayer(interact.msgBuild(crtM, 15, victim), false); // "The destination space is occupied by the " + BRWLRS[victim] + "."
		boolean confirmed = interact.confirm();
		if (confirmed) {
			crtM.setSuccessfulTurn(true);
			crtM = finishMoveTurn(crtM, slct, dstn);
			return crtM;
		}
		crtM.setSuccessfulTurn(false);
		return crtM;
	} // end of execute()
	
	// returns boolean on whether brawler aaa can legitimately attack brawler bbb in passed in game
	private static boolean canAttackThat(GameData crtCAT, int attacker, int victim) {
		Brawler aaa = crtCAT.getBrawler(attacker);
		Brawler bbb = crtCAT.getBrawler(victim);
		if (!aaa.canAttack(bbb)) return false;
		// at this point it is assumed that aaa can legally attack bbb in general.
		boolean answer = true;
		// other conditions may require code here
		// is it a warrior charging?
		// is it a rogue ambushing?
		return answer;
	}
	
	private static boolean canVampireMoveUp(GameData crt, int dstn) {
		int[] fcr = splitBoardSpace(dstn);
		if (!(isLegitSpace(crt, dstn + 100))) return false;
		if (crt.getLocation(fcr[0] + 1, fcr[1], fcr[2]).isOccupied()) return false;
		return true;
	}	
		
	// performs end of turn updates on GameData object and then returns it
	public static GameData finishMoveTurn(GameData crt, int mover, int dstn) {
		GameData crtFT = crt;
		String subject = BRWLRS[mover];
		String object = "none";
		int[] fcr = splitBoardSpace(dstn);
		int startSp = (crtFT.getBrawler(mover).getFloor() * 100) + (crtFT.getBrawler(mover).getColumn() * 10) + crtFT.getBrawler(mover).getRow();
		crtFT.getLocation(startSp).setOccupied(false);
		crtFT.getLocation(startSp).setOccupiedBy(-1);
		// identify the Treasures for interaction with them and/or the NPCs
		Brawler[] treasures = new Brawler[4];
		for (int tsr = 0; tsr < crtFT.getAllBrawlers().length; tsr++) {
			if (crtFT.getBrawler(tsr) instanceof Sword) treasures[0] = crtFT.getBrawler(tsr);
			if (crtFT.getBrawler(tsr) instanceof Shield) treasures[1] = crtFT.getBrawler(tsr);
			if (crtFT.getBrawler(tsr) instanceof Ring) treasures[2] = crtFT.getBrawler(tsr);
			if (crtFT.getBrawler(tsr) instanceof Diamond) treasures[3] = crtFT.getBrawler(tsr);
		}			
		if (crtFT.getLocation(dstn).isOccupied()) { // There is a victim
			int vic = crtFT.getLocation(dstn).getOccupiedBy();
			object = BRWLRS[vic];
			Brawler victim = crtFT.getBrawler(vic);
			Brawler attacker = crtFT.getBrawler(mover);
			int bumps = countChainBumpVictims(crtFT, dstn);
			// brawlers do not get bumped by the Abyss, they are simply removed.
			if (attacker instanceof Abyss) {
				if (victim.isHandsFull()) {
					int loot = victim.getPossession();
					crtFT.dispossess(loot);
					crtFT.getBrawler(loot).remove();
					crtFT = clearSpace(crtFT, vic);
					interact.tellPlayer(interact.msgBuild(crtFT, 16, vic), false); // "The Abyss has swallowed up the " + BRWLRS[vic] + "..."
					interact.tellPlayer(interact.msgBuild(crtFT, 52, loot), false); // "The " + BRWLRS[loot] + " has also vanished into the Abyss......"
				} else {
					crtFT = clearSpace(crtFT, vic);
					interact.tellPlayer(interact.msgBuild(crtFT, 16, vic), false); // "The Abyss has swallowed up the " + BRWLRS[vic] + "..."
				}
			}
			else crtFT = performBumpDowns(crtFT, bumps, dstn);
			// everything after this should be aftereffects of bumpdown		
			// Character or Monster attacker gains a level for attacking, unless the leper is on the same floor as victim
			if ((attacker instanceof Character || attacker instanceof Monster) && attacker.getLevel() < 4 && !(victim instanceof NPC || victim instanceof Treasure)) {
				Brawler leper = new Leper();
				for (int le = 0; le < crtFT.getAllBrawlers().length; le++) if (crtFT.getBrawler(le) instanceof Leper) leper = crtFT.getBrawler(le);
				if (leper.isOnBoard() && (fcr[0] == leper.getFloor())) {
					interact.tellPlayer(interact.msgBuild(crtFT, 41, mover), false); // "The " + BRWLRS[mover] + " gains no level because the Leper is on the same floor."
				}
				else {
					crtFT.getBrawler(mover).setLevel(crtFT.getBrawler(mover).getLevel() + 1);
					interact.tellPlayer(interact.msgBuild(crtFT, 45, mover), false); // "The " + BRWLRS[mover] + " is now level " + crtFT.getBrawler(mover).level)
				}	
			}	
			// If the victim is the leper, it reduces the CL of any Character or Monster other than the Cleric
			if (victim instanceof Leper) {
				if (attacker instanceof Cleric && attacker.getLevel() < 4) { // cleric will gain a level
					crtFT.getBrawler(mover).setLevel(crtFT.getBrawler(mover).getLevel() + 1);
					interact.tellPlayer(interact.msgBuild(crtFT, 44, mover), false); // "The " + BRWLRS[mover] + " has \"cured\" the leper, and will gain a Character Level!"; 
					interact.tellPlayer(interact.msgBuild(crtFT, 45, mover), false); // "The " + BRWLRS[mover] + " is now CL " + crtFT.getBrawler(mover).level)
					interact.space();
				}
				else if (attacker instanceof Cleric && attacker.getLevel() == 4 && !attacker.isHandsFull()) { // cleric will gain the shield
					crtFT.dispossess(treasures[1].getPieceID());
					crtFT = clearSpace(crtFT, treasures[1].getPieceID());
					crtFT.takePossession(mover, treasures[1].getPieceID());
					interact.tellPlayer(interact.msgBuild(crtFT, 46, mover), false); // "The " + BRWLRS[mover] + " has \"cured\" the Leper, and is rewarded with the Shield!"; 
					interact.space();
				}
				else if (attacker instanceof Cleric && attacker.getLevel() == 4 && attacker.isHandsFull()) {
					interact.tellPlayer(interact.msgBuild(crtFT, 47, mover), false); // "The " + BRWLRS[mover] + " has \"cured\" the Leper, and receives humble thanks."
				}	
				else if (!(attacker instanceof Cleric) && attacker instanceof Character) {
					crtFT.getBrawler(mover).setLevel(crtFT.getBrawler(mover).getLevel() - 1);
					interact.tellPlayer(interact.msgBuild(crtFT, 48, mover), false); // "The " + BRWLRS[mover] + " has been weakened by attacking the Leper."
					interact.tellPlayer(interact.msgBuild(crtFT, 45, mover), false); // "The " + BRWLRS[mover] + " is now CL " + crtFT.getBrawler(mover).level)
				}
			}
			// Warrior "rescues" the Princess
			if (victim instanceof Princess && attacker instanceof Warrior) {
				boolean swordHeld = false;
				int swordID = treasures[0].getPieceID();
				for (int b = 0; b < crtFT.getAllBrawlers().length; b++) {
					if (crtFT.getBrawler(b).getPossession() == swordID) {
						swordHeld = true;
						crtFT.dispossess(swordID);
					}
				}
				if (!swordHeld && treasures[0].isOnBoard()) crtFT = clearSpace(crtFT, swordID);
				crtFT.takePossession(mover, swordID);
				interact.tellPlayer(interact.msgBuild(crtFT, 49, mover), false); // "The " + BRWLRS[mover] + " has \"rescued\" the Princess, and is rewarded with the Sword."
			}
			// Rogue robs the merchant
			if (victim instanceof Merchant && attacker instanceof Rogue) {
				boolean diamondHeld = false;
				int diamondID = treasures[3].getPieceID();
				for (int b = 0; b < crtFT.getAllBrawlers().length; b++) {
					if (crtFT.getBrawler(b).getPossession() == diamondID) {
						diamondHeld = true;
						crtFT.dispossess(diamondID);
					}
				}
				if (!diamondHeld && treasures[3].isOnBoard()) crtFT = clearSpace(crtFT, diamondID);
				crtFT.takePossession(mover, diamondID);
				interact.tellPlayer(interact.msgBuild(crtFT, 50, mover), false); // "The " + BRWLRS[mover] + " has robbed the Merchant, absconding with the Diamond."
			}				
			// Character or Monster attacker takes possession of attacked Treasure
			if (victim instanceof Treasure && (attacker instanceof Character || attacker instanceof Monster)) {
				crtFT.takePossession(mover, victim.getPieceID());
				String treasureSnatch = ("The " + BRWLRS[mover] + " has taken possession of the " + BRWLRS[vic]);
				interact.tellPlayer(treasureSnatch, false);
			}
			// Character or Monster attacker snatches Treasure from victim
			if (victim.isHandsFull() && (attacker instanceof Character || attacker instanceof Monster)) { // treasure snatch
				int precious = crtFT.getBrawler(vic).getPossession();
				crtFT.getBrawler(vic).setHandsFull(false);
				crtFT.getBrawler(mover).setHandsFull(true);
				crtFT.getBrawler(vic).setPossession(-1);
				crtFT.getBrawler(mover).setPossession(precious);
				/*
				crtFT.getBrawler(precious).setOnBoard(true);
				crtFT.getBrawler(precious).setFloor(crtFT.getBrawler(mover).getFloor());
				crtFT.getBrawler(precious).setColumn(crtFT.getBrawler(mover).getColumn());
				crtFT.getBrawler(precious).setRow(crtFT.getBrawler(mover).getRow());
				*/
				String tsnatchFrom = ("The " + BRWLRS[attacker.getPieceID()] + " has snatched the " + BRWLRS[precious] + " from the " + BRWLRS[vic] + "!");
				interact.tellPlayer(tsnatchFrom, false);
			}
			// Vampire sucks out extra CL from a cleric, unless it has a shield.  Also, Vampire may move up after attacking
			if (attacker instanceof Vampire) {
				if (victim instanceof Cleric && victim.getShieldBonus() == 0) {
					if (victim.isOnBoard() && victim.getLevel() > 1) {
						interact.tellPlayer(interact.msgBuild(crtFT, 42, victim.getPieceID()), false); // "The Vampire has sucked an additional level out of the " + BRWLRS[passIn] + "!"
						int newLevel = crtFT.getBrawler(victim.getPieceID()).getLevel() - 1;
						crtFT.getBrawler(victim.getPieceID()).setLevel(newLevel);
					}
					else if (victim.isOnBoard() && victim.getLevel() == 1) {
						interact.tellPlayer(interact.msgBuild(crtFT, 43, victim.getPieceID()), false); // "The Vampire attacks, and the bloodless husk of the " + BRWLRS[passIn] + " is tossed off of the board!"
						crtFT = clearSpace(crtFT, victim.getPieceID());
						victim.remove();
					}
				}
				if (canVampireMoveUp(crtFT, dstn)) {
					interact.tellPlayer(4, 7, false); // "The Vampire has drunk the sweet nectar of another brawler, and it regains some of its strength!"
					interact.tellPlayer(interact.msgBuild(crtFT, 55, (dstn + 100)), true); // "Would you like to move the Vampire up to space #" + (dstn + 100) + "? "
					if(interact.getYesOrNo()) {
						fcr[0]++;
						interact.tellPlayer(interact.msgBuild(crtFT, 56, fcr[0]), false); // "The Vampire cackles as it heads back up to level " + fcr[0] + "..."
					}
				}
			}
			// This should account for bumping a brawler and its possessed treasure off the board together
			if (!victim.isOnBoard() && victim.isHandsFull()) {
				int lostLoot = victim.getPossession();
				crtFT.getBrawler(victim.getPieceID()).setHandsFull(false);
				crtFT.getBrawler(victim.getPieceID()).setPossession(-1);
				crtFT.getBrawler(lostLoot).remove();
				interact.tellPlayer(interact.msgBuild(crtFT, 21, lostLoot), false); // "The " + BRWLRS[lostloot] + " has been bumped off of the board!";	
			}	
		} // end of If There Is A Victim
		boolean ghostSpace = crtFT.getLocation(dstn).isGhostEffect();
		if (crtFT.getBrawler(mover) instanceof Mage && ghostSpace) {
			Brawler ghost = new Ghost();
			boolean ringHeld = false;
			for (int gr = 0; gr < crtFT.getAllBrawlers().length; gr++) if (crtFT.getBrawler(gr) instanceof Ghost) ghost = crtFT.getBrawler(gr);
			for (int b = 0; b < crtFT.getAllBrawlers().length; b++) {
				if (crtFT.getBrawler(b).getPossession() == treasures[2].getPieceID()) {
					ringHeld = true;
					crtFT.dispossess(treasures[2].getPieceID());
				}
			}
			if (!ringHeld && treasures[2].isOnBoard()) crtFT = clearSpace(crtFT, treasures[2].getPieceID());
			crtFT.takePossession(mover, treasures[2].getPieceID());
			crtFT = clearSpace(crtFT, ghost.getPieceID());
			interact.tellPlayer(interact.msgBuild(crtFT, 51, mover), false); // "The Ghost dissipates, and the " + BRWLRS[mover] + " discovers a magic ring!"
		}	
		crtFT.getLocation(fcr[0], fcr[1], fcr[2]).setOccupied(true);
		crtFT.getLocation(fcr[0], fcr[1], fcr[2]).setOccupiedBy(mover);
		crtFT.getBrawler(mover).setFloor(fcr[0]);
		crtFT.getBrawler(mover).setColumn(fcr[1]);
		crtFT.getBrawler(mover).setRow(fcr[2]);
		crtFT = updateLastUsed(crtFT, mover);
		// move treasure too?
		if (crtFT.getBrawler(mover).isHandsFull()) {
			crtFT.getBrawler(crtFT.getBrawler(mover).getPossession()).setFloor(fcr[0]);
			crtFT.getBrawler(crtFT.getBrawler(mover).getPossession()).setColumn(fcr[1]);
			crtFT.getBrawler(crtFT.getBrawler(mover).getPossession()).setRow(fcr[2]);
		}
		if (crtFT.isRedsTurn()) crtFT.setRedCanPlace(true);
		else crtFT.setBlueCanPlace(true);	
		crtFT.updateGameRecord("move", subject, dstn, object);
		interact.space();
		return crtFT;
	}	// end of finishMoveTurn()		
				
	// prereq: brawler is assumed to be a legit piece to move, and dstn is assumed to be a legit board space
	// returns int on whether passed in brawler number can legitimately move to passed in destination in passed in game
	// the int code is 0 for true, or a number of the test that was failed	
	private static int isLegitDestination(GameData crtILD, int brawler, int dstn) {
		boolean occupado = false;
		int[] fcrA = splitBoardSpace(dstn);
		Brawler mover = crtILD.getBrawler(brawler);
		BoardSpace origin = crtILD.getLocation(mover.getFloor(), mover.getColumn(), mover.getRow());
		// is legit space?
		BoardSpace dstnA = crtILD.getLocation(dstn);
		int[] playground = crtILD.buildPlayground(brawler);
		if (dstnA.isOccupied()) occupado = true;
		// test 1: if mover is a mage, can he blink or teleport to dstn?
		if (mover instanceof Mage) {
			if (mover.isPoweredUp() && (mover.getFloor() == fcrA[0])) return 0;  // mover is a CL3 Mage who can teleport there
			if (!occupado && (mover.getColumn() == dstnA.getColumn() || mover.getRow() == dstnA.getRow())) return 0; // mover is a Mage who can blink there
		}
		// test 2: Is the sentinel effect on the origin space, and if so is mover trying to move up or down?
		else if (origin.isSentinelEffect() && mover.getFloor() != dstnA.getFloor() && !(mover instanceof Sentinel)) return 2;
		// test 3: Is mover a Nemesis trying to move up?
		else if (mover instanceof Nemesis && dstnA.getFloor() > mover.getFloor()) return 3;	
		//test 4: Is mover a Monster trying to move to a BL higher than its CL?
		else if (mover instanceof Monster && dstnA.getFloor() > mover.getLevel()) return 4;
		// test 5: is dstn a space that mover could move to (fail--return 5, pass--go on)
		boolean inThePlayground = false;
		int dstnAID = dstnA.getLocationID();
		for (int p = 0; p < playground.length; p++) {
			if (dstnAID == playground[p]) inThePlayground = true;
		}
		if (!inThePlayground) return 5;
		// test 6: Is the ghost LOS with mover?  If so, is mover trying to move toward ghost?
		if (tryingToMoveTowardsGhost(crtILD, brawler, dstn)) return 6;
		// test 7: is mover trying to attack treasure?  If so, does it already possess some?
		try {
			Brawler victim = crtILD.getBrawler(dstnA.getOccupiedBy());
			if (mover.isHandsFull() && (victim instanceof Treasure || victim.isHandsFull())) return 7;
			// test 8: is mover a Character trying to attack another Character when the Princess is on the same floor?
			Princess princess = new Princess();
			for (int p = 0; p < crtILD.getAllBrawlers().length; p++) if (crtILD.getBrawler(p) instanceof Princess) princess = (Princess) crtILD.getBrawler(p);
			if (mover instanceof Character && victim instanceof Character && princess.isOnBoard() && princess.getFloor() == victim.getFloor()) return 8;
		}
		catch (ArrayIndexOutOfBoundsException ex) {}
		return 0;
	} // end of isLegitDestination()
		
	// returns boolean array of brawlers available to be moved by current player, this turn
	private static boolean[] sortAvailable(GameData crtSA) {
		// the extra spot at the end of the array is the exit code (for what?)
		boolean[] av = new boolean[crtSA.getAllBrawlers().length];
		Brawler brawlX = new Diamond(); // Diamond is arbitrary
		
		for (int m = 0; m < av.length; m++) {
			brawlX = crtSA.getBrawler(m);
			boolean colorMatch = ((crtSA.isRedsTurn() && brawlX.isRed()) || (!crtSA.isRedsTurn() && brawlX.isBlue()));
			boolean paralyzed = false;
			if (brawlX.isOnBoard()) paralyzed = crtSA.getLocation(brawlX.getFloor(), brawlX.getColumn(), brawlX.getRow()).isGhostEffect();
			if (brawlX instanceof Character) {
				if (!colorMatch || !brawlX.isOnBoard() || brawlX.isUsedLast() || paralyzed) av[m] = false;
				else av[m] = true;
				continue;
			}
			if ((brawlX instanceof Nemesis) || (brawlX instanceof Monster) || (brawlX instanceof NPC)) {
				if (!brawlX.isOnBoard() || brawlX.isUsedLast() || paralyzed) av[m] = false;
				else av[m] = true;
				continue;
			}
			if (brawlX instanceof Treasure) av[m] = false;
			if (brawlX.isOnBoard() && crtSA.getLocation(brawlX.getFloor(), brawlX.getColumn(), brawlX.getRow()).isGhostEffect()) av[m] = false;
		}
		return av;
	}
	
	private static boolean tryingToMoveTowardsGhost(GameData crt, int mover, int dstn) {
		if (crt.getBrawler(mover) instanceof Mage) return false; // Mages can move towards the Ghost all they want.
		LineOfSight los = new LineOfSight();
		// find the ghost and the mover
		Ghost ghost = new Ghost();
		for (int g = 0; g < crt.getAllBrawlers().length; g++) if (crt.getBrawler(g) instanceof Ghost) ghost = (Ghost) crt.getBrawler(g);
		Brawler scooby = crt.getBrawler(mover);
		// is the ghost currently LOS with mover? if not then don't worry about this.
		boolean isLOS = los.calculate(crt, scooby, ghost);
		if (!isLOS) return false;
		// we are LOS, which direction is the Ghost from Scooby?
		//	1	2	3
		//	4		5
		//	6	7	8
		int directionA = 0;		int directionB = 0;
		int gc = ghost.getColumn();	int gr = ghost.getRow();	int sc = scooby.getColumn();	int sr = scooby.getRow();
		if (gc < sc && gr < sr)   directionA = 1;
		if (gc == sc && gr < sr)  directionA = 2;
		if (gc > sc && gr < sr)   directionA = 3;
		if (gc < sc && gr == sr)  directionA = 4;
		if (gc > sc && gr == sr)  directionA = 5;
		if (gc < sc && gr > sr)   directionA = 6;
		if (gc == sc && gr > sr)  directionA = 7;
		if (gc > sc && gr > sr)   directionA = 8;
		// which direction is dstn from mover?
		int[] dstnB = splitBoardSpace(dstn);
		int dc = dstnB[1];	int dr = dstnB[2];
		if (dc < sc && dr < sr)   directionB = 1;
		if (dc == sc && dr < sr)  directionB = 2;
		if (dc > sc && dr < sr)   directionB = 3;
		if (dc < sc && dr == sr)  directionB = 4;
		if (dc > sc && dr == sr)  directionB = 5;
		if (dc < sc && dr > sr)   directionB = 6;
		if (dc == sc && dr > sr)  directionB = 7;
		if (dc > sc && dr > sr)   directionB = 8;
		// is it the same direction?  IS IT?
		if (directionA == directionB) return true;
		else return false;
	}
} // end of Move class

	
