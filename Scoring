package rpgGridBrawl;

// several methods in this class rely on the standard set of Brawlers, for the sake of convenience.
public class Scoring {
	private AInode CRT;
	private ScoringProfile sProf;
	private int[] brLocs; // an array of all the locations of all the brawlers
	
	public Scoring(AInode scoreMe, ScoringProfile sp) {
		CRT = scoreMe;
		sProf = sp;
		int blength = scoreMe.getBrawlersLength();
		this.brLocs = new int[blength];
		for (int a = 0; a < blength; a++) {
			Brawler locateMe = scoreMe.getBrawler(a);
			brLocs[a] = ((locateMe.getFloor() * 100) + (locateMe.getColumn() * 10) + locateMe.getRow());
		}
	}
	
	public void reset(String nodeString) {
		this.CRT.setToString(nodeString);
		int blength = CRT.getBrawlersLength();
		for (int a = 0; a < blength; a++) {
			Brawler locateMe = CRT.getBrawler(a);
			this.brLocs[a] = ((locateMe.getFloor() * 100) + (locateMe.getColumn() * 10) + locateMe.getRow());
		}
	}
	
	public int[] squareOffScore() {
		int[] sQ = {0, 0};
		int[] charSeekNPCandT = this.charSeekingNPCsAndTreasure();
		sQ[0] += (sProf.getSQO(6) * charSeekNPCandT[0]); // Red  Characters -> SS NPCs
		sQ[1] += (sProf.getSQO(6) * charSeekNPCandT[1]); // Blue Characters -> SS NPCs
		sQ[0] += (sProf.getSQO(5) * charSeekNPCandT[2]); // Red  Characters -> SS Treasures
		sQ[1] += (sProf.getSQO(5) * charSeekNPCandT[3]); // Blue Characters -> SS Treasures
		sQ[0] += (sProf.getSQO(1) * charSeekNPCandT[4]); // Red  Characters -> OS Treasures
		sQ[1] += (sProf.getSQO(1) * charSeekNPCandT[5]); // Blue Characters -> OS Treasures
		int[] cVSm = this.charVMonstersSquareOff();
		sQ[0] += (sProf.getSQO(2) * cVSm[0]); // Red  Characters -> SS Monsters
		sQ[1] += (sProf.getSQO(2) * cVSm[1]); // Blue Characters -> SS Monsters
		sQ[1] += (sProf.getSQO(0) * cVSm[2]); // OS Monsters -> Red  Characters
		sQ[0] += (sProf.getSQO(0) * cVSm[3]); // OS Monsters -> Blue Characters
		int[] tbg = this.trappedByGhost();
		sQ[1] += (sProf.getSQO(3) * tbg[0]); // Red  Characters trapped by Ghost
		sQ[0] += (sProf.getSQO(3) * tbg[1]); // Blue Characters trapped by Ghost
		for (int bC = 5; bC <= 7; bC++) sQ[0] += (sProf.getSQO(4) * this.charVSchar(0, bC)); // Red Warrior -> OS Blue Characters
		for (int rC = 1; rC <= 3; rC++) sQ[1] += (sProf.getSQO(4) * this.charVSchar(4, rC)); // Blue Warrior -> OS Red Characters
		return sQ;
	}

	public int[] brawlScore() {
		int[] brawl = {0, 0};
		int[] charSeekNPCandT = this.charSeekingNPCsAndTreasure();
		brawl[0] += (sProf.getB(12) * charSeekNPCandT[0]); // Red  Characters -> SS NPCs
		brawl[1] += (sProf.getB(12) * charSeekNPCandT[1]); // Blue Characters -> SS NPCs
		brawl[0] += (sProf.getB(7) * charSeekNPCandT[2]); // Red  Characters -> SS Treasures
		brawl[1] += (sProf.getB(7) * charSeekNPCandT[3]); // Blue Characters -> SS Treasures
		brawl[0] += (sProf.getB(5) * charSeekNPCandT[4]); // Red  Characters -> OS Treasures
		brawl[1] += (sProf.getB(5) * charSeekNPCandT[5]); // Blue Characters -> OS Treasures
		int[] cVSm = this.charVMonstersBrawl();
		brawl[0] += (sProf.getB(4) * cVSm[0]); // Red  Characters -> Monsters
		brawl[1] += (sProf.getB(4) * cVSm[1]); // Blue Characters -> Monsters
		brawl[1] += (sProf.getB(3) * cVSm[2]); // Monsters -> Red  Characters
		brawl[0] += (sProf.getB(3) * cVSm[3]); // Monsters -> Blue Characters
		int[] tbg = this.trappedByGhost();
		brawl[1] += (sProf.getB(16) * tbg[0]); // Red  Characters trapped by Ghost
		brawl[0] += (sProf.getB(16) * tbg[1]); // Blue Characters trapped by Ghost
		// Here is where it diverges from the SquareOff
		int color = 0;
		for (int c = 0; c <= 7; c++) {
			if (c > 3) color = 1;
			int[] status = this.characterStatus(c);
			boolean glorified = (status[2] == 1);
			boolean onBoard = (status[7] == 1);
			if (onBoard) {
				brawl[color] += (sProf.getB(0) * status[0]); // floor
				brawl[color] += (sProf.getB(1) * status[1]); // level
				brawl[color] += (sProf.getB(19) * status[2]); // glorified?
				brawl[color] += (sProf.getB(2) * status[3]); // powered up?
				brawl[color] += (sProf.getB(14) * status[4]); // any treasure
				brawl[color] += (sProf.getB(15) * status[5]); // SS treasure
				brawl[color] += (sProf.getB(8) * status[6]); // on the glory space?
			}
			else if (glorified) brawl[color] += sProf.getB(18); // glorified and off board
		}
		int[] sabreRattling = this.charVScharBattleTally();
		brawl[0] += (sProf.getB(9) * sabreRattling[0]); // Red characters -> Blue characters
		brawl[1] += (sProf.getB(9) * sabreRattling[1]); // Blue characters -> Red characters
		brawl[0] += (sProf.getB(10) * sabreRattling[2]); // Red can snatch this many OS treasures
		brawl[1] += (sProf.getB(10) * sabreRattling[3]); // Blue can snatch this many OS treasures
		brawl[0] += (sProf.getB(11) * sabreRattling[4]); // Red can snatch this many SS treasures
		brawl[1] += (sProf.getB(11) * sabreRattling[5]); // Blue can snatch this many SS treasures
		
		int[] ppp = this.pickpocketPossibilities();
		brawl[0] += (sProf.getB(13) * ppp[0]); // Red Rogue can steal this many OS treasures
		brawl[0] += (sProf.getB(20) * ppp[1]); // Red Rogue can steal the diamond
		brawl[1] += (sProf.getB(13) * ppp[2]); // Blue Rogue can steal this many OS treasures
		brawl[1] += (sProf.getB(20) * ppp[3]); // Blue Rogue can steal the diamond
		
		int[] nemeses = this.nemesisStatus();
		brawl[1] += (sProf.getB(3) * nemeses[0]); // Nemeses -> Red Characters
		brawl[0] += (sProf.getB(3) * nemeses[1]); // Nemeses -> Blue Characters
		brawl[1] += (sProf.getB(16) * nemeses[2]); // SS Nemeses on same floor as Red Characters
		brawl[0] += (sProf.getB(16) * nemeses[3]); // SS Nemeses on same floor as Blue Characters
		
		int[] merchAvail = this.merchantAvailable();
		brawl[0] += (sProf.getB(6) * merchAvail[0]); // red player could purchase from the Merchant
		brawl[1] += (sProf.getB(6) * merchAvail[1]); // blue player could purchase from the Merchant
		return brawl;
		
		// need a fireball thing--mage with ring or dragon within LOS of enemy characters
		
	} // end of brawlScore()
	
	/**
	 * takes in a 2-value int[], the total red/blue scores
	 * outputs (higher score) / (lower score)
	 * if blue has a higher score, returned value is negative.
	 * if both scores are 0, then return value is zero
	 * if one score is zero but not both, return value is the non-zero score
	 *  
	 */
	public double calculateAdvantage(int[] scores) {
		double advantage = 0.0;
		double denom = 0;
		if (scores[0] == scores[1]) return 0.0;
		else if (scores[0] > scores[1]) denom = scores[0];
		else denom = scores[1];
		advantage = (((scores[0] - scores[1]) / denom) + (scores[0] - scores[1]));
		return advantage;	
	}
	
	private boolean canMageGetToGhost(int mage_id) {
		boolean empty, empty2, haunted, haunted2;
		if (!CRT.getBrawler(mage_id).isOnBoard()) return false;
		int fM = CRT.getBrawler(mage_id).getFloor();
		int cM = CRT.getBrawler(mage_id).getColumn();
		int rM = CRT.getBrawler(mage_id).getRow();
		int ccrr = getCCRR(fM);
		for (int x = 0; x < ccrr; x++) {
			empty = !CRT.getLocation(fM, x, rM).isOccupied();
			haunted = CRT.getLocation(fM, x, rM).isGhostEffect();
			empty2 = !CRT.getLocation(fM, cM, x).isOccupied();
			haunted2 = CRT.getLocation(fM, cM, x).isGhostEffect();
			if ((empty && haunted) || (empty2 && haunted2)) return true;
		}
		return false;
	}
	
	/**
	 * status[0] = floor
	 * status[1] = level
	 * status[2] = glorified?
	 * status[3] = powered up?
	 * status[4] = possess Treasure?
	 * status[5] = same suit Treasure?
	 * status[6] = on the glory space?
	 * status[7] = on board?
	 */
	public int[] characterStatus(int exmd) {
		Brawler zed = CRT.getBrawler(exmd);
		int[] status = new int[8];
		if (zed.isOnBoard()) {
			status[7] = 1;
			status[0] = zed.getFloor();
			status[1] = zed.getLevel();
			if (zed.isGlorified()) status[2] = 1; 
			if (zed.isPoweredUp()) status[3] = 1;
			if (zed.isHandsFull()) {
				status[4] = 1;
				if (zed.isRed()  && zed.getPossession() - zed.getPieceID() == 20) status[5] = 1;
				if (zed.isBlue() && zed.getPossession() - zed.getPieceID() == 16) status[5] = 1;
			}
			if (!zed.isGlorified() && zed.getFloor() == 5) status[6] = 1;
		}
		else if (zed.isGlorified()) status[2] = 1;
		return status;
	}
	
	/**
	 * returns int[] with 6 values:
	 * 0 & 1 are the total number of red/blue characters that can reach same suit NPCs
	 * 2 & 3 are the total number of red/blue characters that can reach SS Treasure
	 * 4 & 5 are the total number of red/blue characters that can reach OS Treasure
	 * uses standard set of brawlers
	 */
	private int[] charSeekingNPCsAndTreasure() {
		int[] result = {0, 0, 0, 0, 0, 0};
		for (int c = 0, n = 16; c <= 7; c++, n++) {
			if (c == 4) n = 16; // reset n for blue chars
			Brawler character = CRT.getBrawler(c);
			Brawler npc = CRT.getBrawler(n);
			int[] charPlayground = CRT.buildPlayground(c);
			if ((c != 2) && (c != 6)) { // you mages get out of here
				if ((character.getSuit() == npc.getSuit()) && inPlayground(charPlayground, brLocs[n])) {
					if (character.isRed()) result[0]++;
					else if (character.isBlue())result[1]++; 
				}
			}
			for (int t = 20; t <= 23; t++) {
				Brawler treasure = CRT.getBrawler(t);
				if ((character.getSuit() == treasure.getSuit()) && inPlayground(charPlayground, brLocs[t])) {
					if (character.isRed()) result[2]++;
					else if (character.isBlue())result[3]++; 
				}
				else if ((character.getSuit() != treasure.getSuit()) && inPlayground(charPlayground, brLocs[t])) {
					if (character.isRed()) result[4]++;
					else if (character.isBlue())result[5]++; 
				}
			}	
		}
		if (canMageGetToGhost(2)) result[0]++;
		if (canMageGetToGhost(6)) result[1]++;
		return result;
	}
	
	// returns 0 if attacker cannot attack victim
	// returns 1 if attacker could attack hands-free victim
	// returns 2 if attacker could attack victim and snatch OS treasure
	// returns 3 if attacker could attack victim and snatch SS treasure
	// uses standard set of Brawlers, but only for convenience in locating the Princess.
	private int charVSchar(int att, int vic) {
		Brawler attacker = CRT.getBrawler(att);
		Brawler victim = CRT.getBrawler(vic);
		boolean colorMatch = ((attacker.isRed() && victim.isRed()) || (attacker.isBlue() && victim.isBlue()));
		if (colorMatch || !attacker.isOnBoard() || !victim.isOnBoard()) return 0;
		Brawler princess = CRT.getBrawler("Princess");
		if (princess.getFloor() == victim.getFloor()) return 0; 
		int[] attackerPlayground = CRT.buildPlayground(att);
		if (inPlayground(attackerPlayground, brLocs[vic])) {
			int battleSkill = 0;
			if (attacker instanceof Warrior) battleSkill = 1;
			int attackScore = attacker.getLevel() + attacker.getSwordBonus() + battleSkill;
			int defenseScore = victim.getLevel() + victim.getShieldBonus() + victim.getClericDefenseBonus();
			if (attackScore > defenseScore) {
				if (!victim.isHandsFull()) return 1;
				else {
					if (attacker.isRed() && (victim.getPossession() - att == 20)) return 3;
					if (attacker.isBlue() && (victim.getPossession() - att == 16))return 3;
					return 2;
				}
			}
		}
		return 0;
	}
	
	/**
	 * returns 6 values:
	 * result[0] = Red characters -> Blue characters
	 * result[1] = Blue characters -> Red characters
	 * result[2] = Red can snatch this many OS treasures
	 * result[3] = Blue can snatch this many OS treasures
	 * result[4] = Red can snatch this many SS treasures
	 * result[5] = Blue can snatch this many SS treasures
	 * 
	 */
	private int[] charVScharBattleTally() {
		int[] result = new int[6];
		for(int rAtt = 0, bAtt = 4; rAtt <= 3; rAtt++, bAtt++) {
			for (int rVic = 0, bVic = 4; rVic <= 3; rVic++, bVic++) {
				switch (this.charVSchar(rAtt, bVic)) {
				case 0: break;
				case 1: result[0]++;	break;
				case 2: result[0]++;	result[2]++;	break;
				case 3: result[0]++;	result[4]++;	break;
				}
				switch (this.charVSchar(bAtt, rVic)) {
				case 0: break;
				case 1: result[1]++;	break;
				case 2: result[1]++;	result[3]++;	break;
				case 3: result[1]++;	result[5]++;	break;
				}
			}	
		}
		return result;
	}
	
	/**
	 * returns int[] with 4 values
	 * [0] and [1] = Red/Blue Characters -> Monsters
	 * [2] and [3] = Monsters -> Red/Blue Characters
	 * uses standard set of brawlers
	 */
	private int[] charVMonstersBrawl() {
		int[] result = {0, 0, 0, 0};
		for (int rC = 0, bC = 4; rC <= 3; rC++, bC++) {
			Brawler redChar = CRT.getBrawler(rC);
			Brawler bluChar = CRT.getBrawler(bC);
			int[] rcPlayground = CRT.buildPlayground(rC);
			int[] bcPlayground = CRT.buildPlayground(bC);
			for (int m = 12; m <= 15; m++) {
				Brawler monster = CRT.getBrawler(m);
				int[] monsPlayground = CRT.buildPlayground(m);
				if (inPlayground(rcPlayground, brLocs[m]) && redChar.canAttack(monster))  result[0]++;
				if (inPlayground(bcPlayground, brLocs[m]) && bluChar.canAttack(monster)) result[1]++;
				if (inPlayground(monsPlayground, brLocs[rC]) && monster.canAttack(redChar))  result[2]++;
				if (inPlayground(monsPlayground, brLocs[bC]) && monster.canAttack(bluChar)) result[3]++;
				
			}
		}
		return result;
	}
	
	/**
	 * returns int[] with 4 values
	 * [0] and [1] = Red/blue characters -> SS Monsters
	 * [2] and [3] = OS Monsters -> Red/Blue Characters
	 * uses standard set of brawlers
	 */
	private int[] charVMonstersSquareOff() {
		int[] result = {0, 0, 0, 0};
		for (int rC = 0, bC = 4; rC <= 3; rC++, bC++) {
			int[] rcPlayground = CRT.buildPlayground(rC);
			int[] bcPlayground = CRT.buildPlayground(bC);
			for (int m = 12; m <= 15; m++) {
				int[] monsPlayground = CRT.buildPlayground(m);
				if ((m - rC == 12) && inPlayground(rcPlayground, brLocs[m])) result[0]++;
				if ((m - bC == 8) && inPlayground(bcPlayground, brLocs[m])) result[1]++;
				if ((m - rC != 12) && inPlayground(monsPlayground, brLocs[rC])) result[2]++;
				if ((m - bC != 8) && inPlayground(monsPlayground, brLocs[bC])) result[3]++;
			}
		}
		return result;
	}
	
	// test method
	/*
	private static void reportPG(int[] playground, int id) {
		String output = "ID #" + id + " PG: ";
		for (int g = 0; g < playground.length; g++) output += playground[g] + " ";
		System.out.println(output);
	}
	*/
	
	private static int getCCRR(int floor) {
		int result;
		switch(floor) {
		case 1: result = 5; break;
		case 2: result = 4; break;
		default: result = 3;
		}
		return result;
	}
	
	private static boolean inPlayground(int[] playground, int kidLocation) {
		if (0 == kidLocation) return false;
		for (int j = 0; j < playground.length; j++) if (kidLocation == playground[j]) return true;
		return false;
	}
	
	/**
	 * returns int[] with 2 values--either 0 or 1
	 * result[0]: The red player can or cannot purchase from the Merchant this turn
	 * result[1]: The red player can or cannot purchase from the Merchant this turn
	 * uses standard set of brawlers
	 */
	private int[] merchantAvailable() {
		int[] result = {0, 0};
		Brawler merchant = CRT.getBrawler(19);
		if (!merchant.isOnBoard()) return result;
		for (int c = 0; c <= 7; c++) {
			Brawler character = CRT.getBrawler(c);
			if (!character.isOnBoard() || character.getPossession() != 23) continue;
			if(inPlayground(CRT.getPlayground(c), brLocs[19])) {
				if (character.isRed()) result[0] = 1;
				else if (character.isBlue()) result[1] = 1; 
			}
		}
		return result;
	}
	
	
	/**
	 * returns 4 values:
	 * result[0] = Nemeses -> Red Characters
	 * result[1] = Nemeses -> Blue Characters
	 * result[2] = SS Nemeses on same floor as Red Characters
	 * result[3] = SS Nemeses on same floor as Blue Characters
	 * assumes standard set of brawlers
	 */
	private int[] nemesisStatus() {
		int[] result = {0, 0, 0, 0};
		for (int n = 8; n <= 11; n++) {
			Brawler nemesis = CRT.getBrawler(n);
			if (!nemesis.isOnBoard()) continue;
			int[] nPlayground = CRT.getPlayground(n);
			for (int c = 0; c <= 7; c++) {
				Brawler character = CRT.getBrawler(c);
				if (!character.isOnBoard()) continue; 
				if (inPlayground(nPlayground, brLocs[c])) {
					if (character.isRed()) result[0]++;
					else if (character.isBlue())result[1]++; 
				}
				if((nemesis.getFloor() == character.getFloor()) && (nemesis.getSuit() == character.getSuit())) {
					if (character.isRed()) result[2]++;
					else if (character.isBlue())result[3]++; 
				}
			}
		}
		return result;
	}
	
	/**
	 * returns 4 values:
	 * result[0] = # of OS Treasures the red rogue could steal
	 * result[1] = A 1 if the Red Rogue could steal the Diamond
	 * result[2] = # of OS Treasures the blue rogue could steal
	 * result[3] = A 1 if the blue Rogue could steal the Diamond
	 * assumes standard set of brawlers
	 */
	private int[] pickpocketPossibilities() {
		int[] result = {0, 0, 0, 0};
		LineOfSight los = new LineOfSight();
		// is rogue on the board and powered up and hands-free
		for (int r = 3, z = 0; r <= 7; r += 4, z += 2) {
			Brawler rogue = CRT.getBrawler(r);
			if (!rogue.isOnBoard() || !rogue.isPoweredUp() || rogue.isHandsFull()) {
				result[z] = 0;
				result[z + 1] = 0;
			}
			else { // we have a rogue that is ready to pickpocket
				for (int j = 0; j <= 15; j++) {
					if (j == r || (j >= 8 && j <= 11)) continue;
					Brawler victim = CRT.getBrawler(j);
					boolean colorMatch = (rogue.isRed() && victim.isRed()) || (rogue.isBlue() && victim.isBlue());
					if (colorMatch || !victim.isOnBoard() || !victim.isHandsFull()) continue;
					else if (los.AIcalculate(CRT, rogue, victim)) {
						Brawler loot = CRT.getBrawler(victim.getPossession());
						if (r == 3) { // Red Rogue
							if (loot instanceof Diamond) result[1] = 1;
							else result[0]++;
						}
						else { // Blue Rogue
							if (loot instanceof Diamond) result[3] = 1;
							else result[2]++;
						}
					}
				}
			}
		}
	return result;
	}
	
	public void setNode(String node) { CRT.setToString(node); }
	
	
	// returns int[] with 2 values, the number of red/blue characters that are trapped by the Ghost
	// assumes that characters are ID# 0-7, as in the standard set
	private int[] trappedByGhost() {
		int[] result = {0, 0};
		for (int gFX = 0; gFX <= 7; gFX++) {
			if ((gFX < 4) && (0 != brLocs[gFX]) && CRT.getLocation(brLocs[gFX]).isGhostEffect()) result[0]++;
			if ((gFX > 3) && (0 != brLocs[gFX]) && CRT.getLocation(brLocs[gFX]).isGhostEffect()) result[1]++;
		}
		return result;
	}
	
} // end of Scoring class
