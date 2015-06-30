package rpgGridBrawl;

/**
 * 
 * This class has all the methods to have the AI make a move in the game.
 * They will generally all take in Strings, use them to make adjustments to an AInode, and then return a String back.
 *
 */
public class AIhands {
	private static AInode actuator;
	
	public AIhands() {
		actuator = new AInode();
	}

	// This is a modified version of GridBrawl.checkForChainBumps()
	// We return at least one, because the Brawler on loc is assumed to be a legit bump victim
	public static byte AICountBumps(AInode node, int loc) {
		byte counter = 1;
		int locLook = loc;
		boolean keepLooking = true;
		while (keepLooking) {
			locLook -= 100;
			if (!isLegitSpace(node, locLook)) return counter;
			try { keepLooking = node.getLocation(locLook).isOccupied(); }
			catch (NullPointerException ex) { return counter; }
			if (keepLooking) {
				Brawler nextVictim = node.getBrawler(node.getLocation(locLook).getOccupiedBy());
				counter++;
				if (!isLegitBumpTarget(nextVictim)) keepLooking = false;
			}
		}
		return counter;	
	}
	
	// This method figures out if bumps are required, and then calls the appropriate method.
	public String executeAttack(String nodeString, int id, int loc) {
		actuator.setToString(nodeString);
		Brawler attacker = actuator.getBrawler(id);
		Brawler victim0 = actuator.getBrawler(actuator.getLocation(loc).getOccupiedBy());
		boolean bumps = isLegitBumpTarget(victim0);
		if (attacker instanceof Abyss) bumps = false;
		if (attacker instanceof Vampire && victim0 instanceof Cleric) {
			int shield = actuator.getBrawler("Shield").getPieceID();
			if (victim0.getLevel() < 3 && victim0.getPossession() != shield) bumps = false;
		}
		String retString;
		if (bumps) retString = this.excuteAttackWithBumps(nodeString, id, loc);
		else retString = this.excuteAttackNoBumps(nodeString, id, loc);
		return retString;
	}
	
	// victim0 is a character or monster that gets bumped
	public String excuteAttackWithBumps(String node, int id, int loc) {
		actuator.setToString(node);
		Brawler attacker = actuator.getBrawler(id);
		int equipment = -1;
		if (attacker.isHandsFull()) equipment = attacker.getPossession();
		int bottomsLoot, victim0Loot;
		Brawler victim0 = actuator.getBrawler(actuator.getLocation(loc).getOccupiedBy());
		victim0Loot = victim0.getPossession();
		byte bumps = AICountBumps(actuator, loc);
		int startBumps = (loc - (bumps * 100) + 100);
		int landing = startBumps - 100;
		for (int i = 1; i <= bumps; i++) {
			int bottom = actuator.getLocation(startBumps).getOccupiedBy();
			bottomsLoot = actuator.getBrawler(bottom).getPossession();
			actuator.getBrawler(bottom).getsBumped();
			actuator.getLocation(startBumps).setOccupied(false);
			actuator.getLocation(startBumps).setOccupiedBy(-1);
			if (actuator.getBrawler(bottom).isOnBoard()) {
				try {
					if (bottomsLoot != -1) {
						actuator.moveBrawler(bottomsLoot, landing);
					}
					actuator.getLocation(landing).setOccupied(true);
					actuator.getLocation(landing).setOccupiedBy(bottom);
				} catch(NullPointerException x) {
					System.out.println("landing = " + landing + " id = " + id + " and bottom = " + bottom);
				}
			}
			else {
				actuator.removeEffects(bottom);
				if (bottomsLoot != 0) actuator.dispossess(bottomsLoot);
			}
			startBumps += 100;
			landing += 100;
		}
		actuator.moveBrawler(id, loc);  // move the attacker into place
		if (equipment != -1) actuator.takePossession(id, equipment);
		if (attacker instanceof Nemesis) {
			int shield = actuator.getBrawler("Shield").getPieceID();
			if (attacker instanceof Vampire && victim0 instanceof Cleric && (victim0.getPossession() != shield)) {
				int levelMinusOne = actuator.getBrawler(victim0.getPieceID()).getLevel() - 1;
				actuator.getBrawler(victim0.getPieceID()).setLevel(levelMinusOne);
			}
		}
		else { // attacker is a character or a monster
			int leperFloor = actuator.getBrawler("Leper").getFloor();
			if ((leperFloor != actuator.getBrawler(id).getFloor()) && (actuator.getBrawler(id).getLevel() < 4)) {
				int levelPlusOne = actuator.getBrawler(id).getLevel() + 1;
				actuator.getBrawler(id).setLevel(levelPlusOne);
				if (attacker instanceof Character && actuator.getBrawler(id).getLevel() > 2) actuator.getBrawler(id).setPoweredUp(true);
			}
			if (victim0Loot != -1 && !attacker.isHandsFull()) actuator.takePossession(attacker.getPieceID(), victim0Loot);
		}
		actuator.updateTreasureEffects();
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	public String excuteAttackNoBumps(String node, int id, int loc) {
		actuator.setToString(node);
		Brawler attacker = actuator.getBrawler(id);
		Brawler victim0 = actuator.getBrawler(actuator.getLocation(loc).getOccupiedBy());
		int vicID = victim0.getPieceID();
		int snatched = -1;
		if (attacker instanceof Nemesis) {
			if (actuator.getBrawler(vicID).isHandsFull()) {
				int loot = actuator.getBrawler(vicID).getPossession();
				actuator.dispossess(loot);
			}
			actuator.getBrawler(vicID).remove();
		}
		if (attacker instanceof Monster || attacker instanceof Character) {
			if (victim0 instanceof Nemesis || victim0 instanceof Character || victim0 instanceof Monster) {
				int leperFloor = actuator.getBrawler("Leper").getFloor();
				if ((leperFloor != victim0.getFloor()) && (attacker.getLevel() < 4)) {
					int levelPlusOne = attacker.getLevel() + 1;
					actuator.getBrawler(id).setLevel(levelPlusOne);
				}
				if (victim0.isHandsFull()) {
					int vicLoot = victim0.getPossession();
					if (attacker.isHandsFull()) actuator.dispossess(vicLoot);
					else snatched = vicLoot;
				}
			}
			else if (victim0 instanceof Treasure) {
				actuator.getBrawler(vicID).remove();
				if (!attacker.isHandsFull()) snatched = vicID;
				if (attacker instanceof Mage && actuator.getLocation(loc).isGhostEffect()) snatched = actuator.getBrawler("Ring").getPieceID();
			}
		}
		if (attacker instanceof Character) { // considerations that don't apply to monsters
			if (victim0 instanceof Princess) {
				if (attacker instanceof Warrior) { // attacker gets sword
					int swordID = actuator.getBrawler("Sword").getPieceID();
					actuator.takePossession(id, swordID);
				}
				else { // attacker is Cleric, Mage, or Rogue
					int getMeATreasure = this.selectBestOffboardTreasure(actuator, id);
					if (getMeATreasure != 0) actuator.takePossession(id, getMeATreasure);
				}
			}
			// attacking the Leper causes non-cleric char or mons to lose a level
			else if (victim0 instanceof Leper) {
				if (attacker instanceof Cleric) {
					if (attacker.getLevel() < 4) {
						int newlevel = actuator.getBrawler(id).getLevel() + 1;
						actuator.getBrawler(id).setLevel(newlevel);
					}
					else if (attacker.getLevel() == 4 && !attacker.isHandsFull()) { // cleric will gain the shield
						int shieldID = actuator.getBrawler("Shield").getPieceID();
						actuator.takePossession(id, shieldID);
					}
				}
				else {
					int k = actuator.getBrawler(id).getLevel();
					actuator.getBrawler(id).setLevel(k - 1);
				}
			}			
			// Rogue attacking Merchant yields Diamond to Rogue
			else if (attacker instanceof Rogue && victim0 instanceof Merchant){
				int diamondID = actuator.getBrawler("Diamond").getPieceID();
				actuator.takePossession(id, diamondID);
			}
		}
		
		if (!(victim0 instanceof Treasure)) actuator.getBrawler(vicID).remove();
		actuator.removeEffects(vicID);
		actuator.moveBrawler(id, loc); // move the attacker into place
		if (snatched != -1) actuator.takePossession(id, snatched);
		actuator.updateTreasureEffects();
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	
	
	// prereq: rogue with diamond is assumed to be LOS to victim, and loc is assumed to be a legit, unoccupied space.
	public String executeBribe(String node, int rogue, int victim, int loc) {
		actuator.setToString(node);
		actuator.dispossess(actuator.getBrawler(rogue).getPossession());
		actuator.moveBrawler(victim, loc);
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	public String executeHumble(String node, int cleric, int recipient) {
		actuator.setToString(node);
		int newLevel = 0;
		if (actuator.getBrawler(cleric).getLevel() == 1) actuator.getBrawler(cleric).remove();
		else {
			newLevel = actuator.getBrawler(cleric).getLevel() - 1;
			actuator.getBrawler(cleric).setLevel(newLevel);
		}
		newLevel = actuator.getBrawler(recipient).getLevel() + 1;
		if (newLevel <= 4) actuator.getBrawler(recipient).setLevel(newLevel);
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	public String executeFireball(String node, int loc) {
		actuator.setToString(node);
		int bumps = AICountBumps(actuator, loc);
		int startBumps = (loc - (bumps * 100) + 100);
		int landing = startBumps - 100;
		for (int i = 1; i <= bumps; i++) {
			int bottom = actuator.getLocation(startBumps).getOccupiedBy();
			int loot = actuator.getBrawler(bottom).getPossession();
			actuator.getBrawler(bottom).getsBumped();
			actuator.getLocation(startBumps).setOccupied(false);
			actuator.getLocation(startBumps).setOccupiedBy(-1);
			if (actuator.getBrawler(bottom).isOnBoard()) {
				try {
					if (actuator.getBrawler(bottom).isHandsFull()) actuator.moveBrawler(loot, landing);
					actuator.getLocation(landing).setOccupied(true);
					actuator.getLocation(landing).setOccupiedBy(bottom);
				} catch(NullPointerException x) {
					System.out.println("landing = " + landing + " and bottom = " + bottom);
				}
			}
			else {
				actuator.removeEffects(bottom);
				if (loot != -1) actuator.dispossess(loot);
			}
			startBumps += 100;
			landing += 100;
		}
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	public String executeFlee(String node, int id) {
		actuator.setToString(node);
		if (actuator.getBrawler(id).isHandsFull()) {
			int loot = actuator.getBrawler(id).getPossession();
			actuator.dispossess(loot);
		}
		actuator.getBrawler(id).remove();
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	public String executeGlorify(String node, int id) {
		actuator.setToString(node);
		actuator.getBrawler(id).remove();
		actuator.getBrawler(id).setGlorified(true);
		actuator.getBrawler(id).setLevel(4);
		if (actuator.getBrawler(id).handsFull) {
			actuator.getBrawler(id).setHandsFull(false);
			int loot = actuator.getBrawler(id).getPossession();
			actuator.dispossess(loot);
			actuator.getBrawler(id).setPossession(-1);
		}
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	public String executeMove(String node, int id, int loc) {
		actuator.setToString(node);
		actuator.moveBrawler(id, loc);
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	public String executePickpocket(String node, int rogue, int victim) {
		actuator.setToString(this.executeTransferTreasure(node, victim, rogue, true));
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	// placement is assumed to be on a valid space
	public String executePlacement(String node1, int brawlerID, int loc) {
		actuator.setToString(node1);
		int[] fcr = splitBoardSpace(loc);
		Brawler placeMe = actuator.getBrawler(brawlerID);
		actuator.getBrawler(brawlerID).setOnBoard(true);
		actuator.getBrawler(brawlerID).setFloor(fcr[0]);
		actuator.getBrawler(brawlerID).setColumn(fcr[1]);
		actuator.getBrawler(brawlerID).setRow(fcr[2]);
		if (placeMe instanceof Monster) actuator.getBrawler(brawlerID).setLevel(fcr[0]);
		actuator.updateStatusArray();
		return actuator.toString();
	}		
	
	public String executePurchase(String node, int buyer, int merch) {
		actuator.setToString(node);
		actuator.dispossess(actuator.getBrawler(buyer).getPossession());
		actuator.takePossession(buyer, merch);
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	public String executeSacrifice(String node, int lamb, int victim, int targetLoc) {
		actuator.setToString(node);
		actuator.dispossess(lamb);
		if (actuator.getBrawler(lamb) instanceof Sword) {
			if (actuator.getBrawler(victim).isHandsFull()) {
				int lostLoot = actuator.getBrawler(victim).getPossession();
				actuator.dispossess(lostLoot);
			}
			actuator.getBrawler(victim).remove();
		}
		else if (actuator.getBrawler(lamb) instanceof Shield) {
			int newLevel = actuator.getBrawler(victim).getLevel() + 1;
			if (newLevel <= 4) actuator.getBrawler(victim).setLevel(newLevel);
		}
		else if (actuator.getBrawler(lamb) instanceof Ring) {
			if (!actuator.getBrawler(victim).isHandsFull()) actuator.takePossession(victim, lamb);
		}
		else if (actuator.getBrawler(lamb) instanceof Diamond) {
			actuator.moveBrawler(victim, targetLoc);
		}
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	public String executeTransferTreasure(String node, int pitcher, int catcher, boolean moveTreasureAlso) {
		actuator.setToString(node);
		int ball = actuator.getBrawler(pitcher).getPossession();
		actuator.getBrawler(pitcher).setPossession(-1);				// pitcher winds up
		actuator.getBrawler(pitcher).setHandsFull(false);				// and the pitch...
		if (moveTreasureAlso) {
			actuator.getBrawler(ball).setFloor(actuator.getBrawler(catcher).getFloor());
			actuator.getBrawler(ball).setColumn(actuator.getBrawler(catcher).getColumn());		// the ball travels (maybe)
			actuator.getBrawler(ball).setRow(actuator.getBrawler(catcher).getRow());			// the ball travels (maybe)
		}
		actuator.getBrawler(catcher).setHandsFull(true);			// the ball thumps into the mitt
		actuator.getBrawler(catcher).setPossession(ball);			// steeerike!
		actuator.updateStatusArray();
		return actuator.toString();
	}
	
	private static boolean isLegitBumpTarget(Brawler vic) {
		boolean ret = false;
		if ((vic instanceof Character) || (vic instanceof Monster)) {
			if ((vic.getFloor() > 1) && (vic.getLevel() > 1)) ret = true;
		}
		return ret;
	}
	
	private static boolean isLegitSpace(AInode node, int fcr) {
		int[] split = splitBoardSpace(fcr);
		int ccrr = 3;
		if (split[0] == 2) ccrr = 4;
		else if (split[0] == 1) ccrr = 5;
		
		if (split[0] == 5 && (split[1] != 0 || split[2] != 0)) return false;
		if (split[0] < 0 || split[0] > 5) return false;
		else if (split[1] < 0 || split[1] >= ccrr) return false;
		else if (split[2] < 0 || split[2] >= ccrr) return false;
		return true;
	}
	
	/**
	 * Action Codes:
	 * 0: do nothing	1: place	2: move		3: attack
	 * 4: fireball		5: flee		6: glorify	7: pickpocket
	 * 8: purchase		9: sack		10: transfer
	 * 11: humble		12: bribe
	 * 
	 * What the AIresult tells us is: given the current AInode, the best move for a particular Brawler at this moment
	 */
	// outputs a string based on passed in AInode String, in which action detailed in AIresult object is performed
	public String performDesiredAction(String node, AIresult result) {
		String actuated = node;
		int id = result.getID();
		int loc = result.getIdealPlace();
		int target = result.getSecondaryTarget();
		switch (result.getAction()) {
		case 0: break; // "do nothing"
		case 1: actuated = this.executePlacement(node, id, loc);					break;
		case 2: actuated = this.executeMove(node, id, loc);							break;
		case 3: actuated = this.executeAttack(node, id, loc);						break;
		case 4: actuated = this.executeFireball(node, loc);							break;
		case 5: actuated = this.executeFlee(node, id);								break;
		case 6: actuated = this.executeGlorify(node, id);							break;
		case 7: actuated = this.executePickpocket(node, id, target);				break;
		case 8: actuated = this.executePurchase(node, id, target);					break;
		case 9: actuated = this.executeSacrifice(node, id, target, loc);			break;
		case 10: actuated = this.executeTransferTreasure(node, id, target, true);	break;
		case 11: actuated = this.executeHumble(node, id, target);	break;
		case 12: actuated = this.executeBribe(node, id, target, loc);				break;
		}
		return actuated;
	}
	
	// this is a helper method for executeAttack()
	// It returns the ID of an off-board Treasure that the Princess will grant to an attacking
	private int selectBestOffboardTreasure(AInode node, int a) {
		Brawler attacker = node.getBrawler(a);
		boolean[] offBoardTreasures = new boolean[4];
		boolean isThereEvenOne = false;
		for (int i = 0, j = 20; i <= 3; i++, j++) {
			if (!node.getBrawler(j).isOnBoard()) {
				offBoardTreasures[i] = true;
				isThereEvenOne = true;
			}
		}
		if (!isThereEvenOne) return 0;
		else if (attacker instanceof Cleric && offBoardTreasures[1]) return 21; // Shield
		else if (attacker instanceof Mage   && offBoardTreasures[2]) return 22; // Ring
		else if (attacker instanceof Rogue  && offBoardTreasures[3]) return 23; // Diamond
		else {
			while (true) {
				int rando = ((int) Math.random() * 4);
				if (offBoardTreasures[rando]) return (rando + 20); // what could go wrong with this?
			}
		}
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
	
	
	
} // end of class AIhands
