package rpgGridBrawl;
/**
 * 
 * The AIsensors class is responsible for taking in a game, and determining what move should be made by the current player.
 * It has a set of rules for most of the different Brawlers.  It functions by taking every single possible action by
 * every available Brawler, and then it scores the game each time.  By keeping track of which move provides the best "advantage"
 * (which is basically the difference between the two scores) the AIsensors can reliably make at least a competent move.
 *
 */
public class AIsensors {
	private final AInode gameNode;
	final String gNode;
	final ScoringProfile sProf;
	static final AIhands hands = new AIhands();
	double inFavor, against, chkAdv;
	static Scoring scorer;
	
	public AIsensors(String nodeString) {
		this.gameNode = new AInode(nodeString);
		// speculator = new AInode(gameNode.toString());
		this.gNode = this.gameNode.toString();
		this.sProf = new ScoringProfile("standard"); // eventually build this out to incorporate other Scoring Profiles
		scorer = new Scoring(this.gNode, this.sProf);
		this.inFavor = 1;
		this.against = -1;
		this.chkAdv = 0;
		if (!gameNode.isRedsTurn()) {
			this.inFavor = -1;	this.against = 1;
		}
	}
	
	// The switchboard method that sends the search into individual bestAction methods
	public AIresult bestActionByBrawler(int id, boolean redsTurn) {
		Brawler whatAmI = gameNode.getBrawler(id);
		gameNode.scoreMyself(sProf);
		AIresult result = new AIresult();
		if (whatAmI instanceof Warrior) result = this.bestAction_Warrior(id, redsTurn);
		if (whatAmI instanceof Cleric) result = this.bestAction_Cleric(id, redsTurn);
		if (whatAmI instanceof Mage) result = this.bestAction_Mage(id, redsTurn);
		if (whatAmI instanceof Rogue) result = this.bestAction_Rogue(id, redsTurn);
		if (whatAmI instanceof Nemesis) result = this.bestAction_Nemesis(id, redsTurn);
		if (whatAmI instanceof Monster) result = this.bestAction_Monster(id, redsTurn);
		if (whatAmI instanceof Princess) result = this.bestAction_Princess(id, redsTurn);
		if (whatAmI instanceof Leper) result = this.bestAction_Leper(id, redsTurn);
		if (whatAmI instanceof Ghost) result = this.bestAction_Ghost(id, redsTurn);
		if (whatAmI instanceof Merchant) result = this.bestAction_Merchant(id, redsTurn);
		if (whatAmI instanceof Treasure) result = this.bestAction_Treasure(id, redsTurn);
		// speculator.setToString(gNode);
		return result;
	}
	
	// assumes a standard 5X5 first floor
	public AIresult bestAction_Warrior(int id, boolean redsTurn) {
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		Brawler warrior = gameNode.getBrawler(id);
		boolean haunted = (warrior.isOnBoard() && gameNode.getLocation(warrior.getFloor(), warrior.getColumn(), warrior.getRow()).isGhostEffect());
		if (warrior.isUsedLast() || haunted) return results;
		chkAdv = 0.0;
		int chkSpc = 0;
		int[] fcr = new int[3];
		int[] onBoardCount = countOnBoardCharacters(redsTurn);
		double preAdjust = 0, postAdjust = 0;
		Brawler myCleric = gameNode.getBrawler("My Cleric");
		Brawler myMage = gameNode.getBrawler("My Mage");
		Brawler myRogue = gameNode.getBrawler("My Rogue");
		Brawler ghost = gameNode.getBrawler("Ghost");
		
		if (!warrior.isOnBoard()){ // we can only place
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			for (int u = 3; u > 0; u--) if (onBoardCount[0] < u) preAdjust += (0.1 * inFavor); // place it if you have <3 characters on board
			preAdjust += (0.15 * onBoardCount[1] * inFavor); // better to place if opponent has more characters on board
			// other preAdjustments go here
			
			for (int w = 0; w < legitSpaces.length; w++) {
				chkSpc = legitSpaces[w];
				fcr = splitBoardSpace(chkSpc);
				// This is a Character, so it's better not to place it out on the "frontier" on floor 1
				if (fcr[1] == 4 || fcr[2] == 4) postAdjust += (.75 * against);
				// put the Warrior closer to the center of the board
				if (fcr[1] != 0 && fcr[1] != 4 && fcr[2] != 0 && fcr[2] != 4) {
					postAdjust += (0.1 * inFavor);
					if (fcr[1] == 2) postAdjust += (0.1 * inFavor);
					if (fcr[2] == 2) postAdjust += (0.1 * inFavor);
				}
				// if Ghost is not on-board, don't place this guy adjacent to another friendly Character
				if (!ghost.isOnBoard()) {
					if (myCleric.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
					if (myMage.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
					if (myRogue.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
				}
				if (gameNode.getRound() > 10 && willIGetMyselfKilledHere(gameNode, chkSpc)) postAdjust += (2 * against);
				// other postAdjustments go here
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else { // we do anything but place
			int[] warPG = gameNode.buildPlayground(id);
			
			// move
			int[] moveTargets = this.sortPlaygroundForMove(gameNode, warPG, id);
			for (int m = 0; m < moveTargets.length; m++) {
				chkSpc = moveTargets[m];
				// postAdjust: moving up is encouraged, especially if level > floor
				int lazy = (warrior.getLevel() - warrior.getFloor());
				if ((chkSpc == warrior.getLoc() + 100) && (lazy > 0)) {
					if (warrior.getFloor() == 1) lazy *= 2;
					if (warrior.isGlorified()) lazy = 0;
					postAdjust = (lazy * inFavor);	
				}
				
				// postAdjust: Moving into the Glory corridor is encouraged when lvl == 4
				if ((chkSpc % 100 == 0) && (gameNode.getBrawler(id).getLevel() == 4)) {
					postAdjust = (5 * inFavor);
				}
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 2, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 2, chkSpc, -1, chkAdv, redsTurn);
				postAdjust = 0;
			}
			
			// attack
			// preAdjust: attacking generally a good plan for Characters
			preAdjust = (3 * inFavor);
			int[] targets = this.sortPlaygroundForAttack(gameNode, warPG, id);
			if (targets.length > 0) {
				for (int att = 0; att < targets.length; att++) {
					chkSpc = targets[att];
					int victim = gameNode.getLocation(chkSpc).getOccupiedBy();
					// postadjust: The "Must Kill" rule: If opponent only has 1 onboard Character and it can be attacked, it must be.
					int[] cob = this.countOnBoardCharacters(redsTurn);
					if (cob[1] == 1 && gameNode.getBrawler(victim) instanceof Character) {
						return new AIresult(id, 3, chkSpc, victim, (1000 * inFavor), redsTurn); // If you don't do this, you're just being a dick.
					}
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 3, chkSpc, victim) + postAdjust;
					results.updateMyself(id, 3, chkSpc, victim, chkAdv, redsTurn);
				}
			}
			preAdjust = 0;
			
			// glorify
			if (warrior.getLevel() == 4 && warrior.getFloor() == 5 && !warrior.isGlorified()) {
				// preAdjust: if you can do this, it's the best thing you can do.
				// preAdjust: However!  If you have no other Characters on the Board you CANNOT do this.
				int[] cob = this.countOnBoardCharacters(redsTurn);
				if (cob[0] > 1) {
					return new AIresult(id, 6, 500, -1, (1000 * inFavor), redsTurn);
				}
			}				
			
			// purchase
			if (warrior.getPossession() == gameNode.getBrawler("Diamond").getPieceID() && 
					warrior.getFloor() == gameNode.getBrawler("Merchant").getFloor()) {
				for (int tsr = 20; tsr <= 22; tsr++) {
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 8, 0, tsr) + postAdjust;
					results.updateMyself(id, 8, 0, tsr, chkAdv, redsTurn);
				}
			}
			
			// transfer
			if (warrior.isHandsFull()) {
				int potentialRecipient = this.getBestTransferTarget(id);
				if (potentialRecipient != -1) {
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 10, 0, potentialRecipient);
					results.updateMyself(id, 10, 0, potentialRecipient, chkAdv, redsTurn);
				}
			}
			
			// post: attacking is better if you can make a chain bump, but only if your characters are not in the chain.
			// post: amongst choices of attack targets, choose the biggest threat first
			// post: moving into line of sight with powered up friendly Cleric is a small bonus.
			// post: if the warrior is level 2 or higher, moving off the first floor is very good.
		}
		return results;
	}
	
	
	public AIresult bestAction_Cleric(int id, boolean redsTurn) {
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		// String spec = gameNode.toString();
		Brawler cleric = gameNode.getBrawler(id);
		boolean haunted = (cleric.isOnBoard() && gameNode.getLocation(cleric.getFloor(), cleric.getColumn(), cleric.getRow()).isGhostEffect());
		if (cleric.isUsedLast() || haunted) return results;
		chkAdv = 0.0;
		int chkSpc = 0;
		int[] onBoardCount = countOnBoardCharacters(redsTurn);
		double preAdjust = 0, postAdjust = 0;
		Brawler myWarrior = gameNode.getBrawler("My Warrior");
		Brawler myMage = gameNode.getBrawler("My Mage");
		Brawler myRogue = gameNode.getBrawler("My Rogue");
		Brawler ghost = gameNode.getBrawler("Ghost");
		Brawler leper = gameNode.getBrawler("Leper");
		
		if (!cleric.isOnBoard()){ // we can only place
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			for (int u = 3; u > 0; u--) if (onBoardCount[0] < u) preAdjust += (0.1 * inFavor); // place it if you have <3 characters on board
			// other preAdjustments go here
			
			for (int w = 0; w < legitSpaces.length; w++) {
				chkSpc = legitSpaces[w];
				int[] fcr = GridBrawl.splitBoardSpace(chkSpc);
				
				// This is a Character, so it's better not to place it out on the "frontier" on floor 1
				if (fcr[1] == 4 || fcr[2] == 4) postAdjust += (.75 * against);
				// if space in question is directly underneath the Leper, give an extra small boost
				if (fcr[0] - 1 == leper.getFloor() && fcr[1] == leper.getColumn() && fcr[2] == leper.getRow()) postAdjust += (1 * inFavor);
				
				// if Ghost is not on-board, don't place this guy adjacent to another friendly Character
				if (!ghost.isOnBoard()) {
					if (myWarrior.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
					if (myMage.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
					if (myRogue.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
				}
				if (gameNode.getRound() > 10 && willIGetMyselfKilledHere(gameNode, chkSpc)) postAdjust += (2 * against);
				// other postAdjustments go here
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else {
			gameNode.buildPlayground(id);
			
			// move
			int[] moveTargets = this.sortPlaygroundForMove(gameNode, gameNode.buildPlayground(id), id);
			for (int m = 0; m < moveTargets.length; m++) {
				chkSpc = moveTargets[m];
				// postAdjust: moving up is encouraged, especially if level > floor
				int lazy = (cleric.getLevel() - cleric.getFloor());
				if ((chkSpc == cleric.getLoc() + 100) && (lazy > 0)) {
					if (cleric.getFloor() == 1) lazy *= 2;
					if (cleric.isGlorified()) lazy = 0;
					postAdjust = (lazy * inFavor);	
				}
				// postAdjust: Moving into the Glory corridor is encouraged when lvl == 4
				if ((chkSpc % 100 == 0) && (gameNode.getBrawler(id).getLevel() == 4)) {
					postAdjust = (5 * inFavor);
				}
				
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 2, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 2, chkSpc, -1, chkAdv, redsTurn);
				postAdjust = 0;
			}
			
			// attack
			// preAdjust: attacking generally a good plan for Characters
			preAdjust = (3 * inFavor);
			int[] targets = this.sortPlaygroundForAttack(gameNode, gameNode.buildPlayground(id), id);
			if (targets.length > 0) {
				for (int att = 0; att < targets.length; att++) {
					chkSpc = targets[att];
					int victim = gameNode.getLocation(targets[att]).getOccupiedBy();
					// postadjust: The "Must Kill" rule: If opponent only has 1 onboard Character and it can be attacked, it must be.
					int[] cob = this.countOnBoardCharacters(redsTurn);
					if (cob[1] == 1 && gameNode.getBrawler(victim) instanceof Character) {
						return new AIresult(id, 3, chkSpc, victim, (1000 * inFavor), redsTurn); // If you don't do this, you're just being a dick.
					}
					// postAdjust: do let's attack the Leper
					if (gameNode.getBrawler(victim) instanceof Leper) postAdjust += 3 * inFavor;
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 3, chkSpc, victim) + postAdjust;
					results.updateMyself(id, 3, chkSpc, victim, chkAdv, redsTurn);
				}
			}
			preAdjust = 0;
			postAdjust = 0;
			
			// glorify
			if (cleric.getLevel() == 4 && cleric.getFloor() == 5 && !cleric.isGlorified()) {
				// preAdjust: if you can do this, it's the best thing you can do.
				// preAdjust: However!  If you have no other Characters on the Board you CANNOT do this.
				int[] cob = this.countOnBoardCharacters(redsTurn);
				if (cob[0] > 1) {
					return new AIresult(id, 6, 500, -1, (1000 * inFavor), redsTurn);
				}
			}				
			
			// purchase
			if (cleric.getPossession() == gameNode.getBrawler("Diamond").getPieceID() && 
					cleric.getFloor() == gameNode.getBrawler("Merchant").getFloor()) {
				for (int tsr = 20; tsr <= 22; tsr++) {
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 8, 0, tsr) + postAdjust;
					results.updateMyself(id, 8, 0, tsr, chkAdv, redsTurn);
				}
			}
		
			// transfer
			if (cleric.isHandsFull()) {
				int potentialRecipient = this.getBestTransferTarget(id);
				if (potentialRecipient != -1) {
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 10, 0, potentialRecipient);
					results.updateMyself(id, 10, 0, potentialRecipient, chkAdv, redsTurn);
				}
			}
			
			// humble
			int humbleTarget = this.getBestHumbleTarget(id);
			if (humbleTarget != -1) {
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 11, 0, humbleTarget);
				results.updateMyself(id, 11, 0, humbleTarget, chkAdv, redsTurn);
			}
		}
		return results;
	}
	
	public AIresult bestAction_Mage(int id, boolean redsTurn) {
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		Brawler mage = gameNode.getBrawler(id);
		boolean haunted = (mage.isOnBoard() && gameNode.getLocation(mage.getFloor(), mage.getColumn(), mage.getRow()).isGhostEffect());
		if (mage.isUsedLast() || haunted) return results;
		chkAdv = 0.0;
		int chkSpc = 0;
		int[] onBoardCount = countOnBoardCharacters(redsTurn);
		double preAdjust = 0, postAdjust = 0;
		Brawler myWarrior = gameNode.getBrawler("My Warrior");
		Brawler myCleric = gameNode.getBrawler("My Cleric");
		Brawler myRogue = gameNode.getBrawler("My Rogue");
		Brawler ghost = gameNode.getBrawler("Ghost");
		
		if (!mage.isOnBoard()){ // we can only place
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			for (int u = 3; u > 0; u--) if (onBoardCount[0] < u) preAdjust += (0.1 * inFavor); // place it if you have <3 characters on board
			// other preAdjustments go here
			
			for (int w = 0; w < legitSpaces.length; w++) {
				chkSpc = legitSpaces[w];
				int[] fcr = GridBrawl.splitBoardSpace(chkSpc);
				// This is a Character, so it's better not to place it out on the "frontier" on floor 1
				if (fcr[1] == 4 || fcr[2] == 4) postAdjust += (.75 * against);
				
				// if Ghost is not on-board, don't place this guy adjacent to another friendly Character
				if (!ghost.isOnBoard()) {
					if (myWarrior.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
					if (myCleric.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
					if (myRogue.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
				}
				else { // never place a Mage adjacent to the Ghost
					BoardSpace examined = gameNode.getLocation(chkSpc);
					if (examined.isGhostEffect()) postAdjust += (5 * against); // really don't do that.
				}
				if (gameNode.getRound() > 10 && willIGetMyselfKilledHere(gameNode, chkSpc)) postAdjust += (2 * against);
				// other postAdjustments go here
				
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else {
			gameNode.buildPlayground(id);
			
			// move
			int[] moveTargets = this.sortPlaygroundForMove(gameNode, gameNode.buildPlayground(id), id);
			for (int m = 0; m < moveTargets.length; m++) {
				chkSpc = moveTargets[m];
				// postAdjust: moving up is encouraged, especially if level > floor
				int lazy = (mage.getLevel() - mage.getFloor());
				if ((chkSpc == mage.getLoc() + 100) && (lazy > 0)) {
					if (mage.getFloor() == 1) lazy *= 2;
					if (mage.isGlorified()) lazy = 0;
					postAdjust = (lazy * inFavor);	
				}
				
				// postAdjust: Moving into the Glory corridor is encouraged when lvl == 4
				if ((chkSpc % 100 == 0) && (gameNode.getBrawler(id).getLevel() == 4)) {
					postAdjust = (5 * inFavor);
				}
				
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 2, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 2, chkSpc, -1, chkAdv, redsTurn);
				postAdjust = 0;
			}
			
			// attack and/or fireball
			// preAdjust: attacking generally a good plan for Characters
			preAdjust = (3 * inFavor);
			int[] targets = this.sortPlaygroundForAttack(gameNode, gameNode.buildPlayground(id), id);
			if (targets.length > 0) {
				for (int att = 0; att < targets.length; att++) {
					chkSpc = targets[att];
					int victim = gameNode.getLocation(targets[att]).getOccupiedBy();
					// postadjust: The "Must Kill" rule: If opponent only has 1 onboard Character and it can be attacked, it must be.
					int[] cob = this.countOnBoardCharacters(redsTurn);
					if (cob[1] == 1 && gameNode.getBrawler(victim) instanceof Character) {
						return new AIresult(id, 3, chkSpc, victim, (1000 * inFavor), redsTurn); // If you don't do this, you're just being a dick.
					}
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 3, chkSpc, victim) + postAdjust;
					results.updateMyself(id, 3, chkSpc, victim, chkAdv, redsTurn);
					if (mage.getPossession() == gameNode.getBrawler("Ring").getPieceID()) {
						LineOfSight los = new LineOfSight();
						boolean iSeeYou = los.AIcalculate(gameNode, mage, gameNode.getBrawler(victim));
						if (iSeeYou) {
							chkAdv = preAdjust + this.getAdvantageFromAction(id, 4, chkSpc, victim) + postAdjust;
							results.updateMyself(id, 4, chkSpc, victim, chkAdv, redsTurn);
						}
					}
				}
			}
			preAdjust = 0;
			
			// glorify
			if (mage.getLevel() == 4 && mage.getFloor() == 5 && !mage.isGlorified()) {
				// preAdjust: if you can do this, it's the best thing you can do.
				// preAdjust: However!  If you have no other Characters on the Board you CANNOT do this.
				int[] cob = this.countOnBoardCharacters(redsTurn);
				if (cob[0] > 1) {
					return new AIresult(id, 6, 500, -1, (1000 * inFavor), redsTurn);
				}
			}			
			
			// purchase
			if (mage.getPossession() == gameNode.getBrawler("Diamond").getPieceID() && 
					mage.getFloor() == gameNode.getBrawler("Merchant").getFloor()) {
				for (int tsr = 20; tsr <= 22; tsr++) {
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 8, 0, tsr) + postAdjust;
					results.updateMyself(id, 8, 0, tsr, chkAdv, redsTurn);
				}
			}
			
			// transfer
			if (mage.isHandsFull()) {
				int potentialRecipient = this.getBestTransferTarget(id);
				if (potentialRecipient != -1) {
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 10, 0, potentialRecipient);
					results.updateMyself(id, 10, 0, potentialRecipient, chkAdv, redsTurn);
				}
			}			
		}
		return results;
	}
	
	public AIresult bestAction_Rogue(int id, boolean redsTurn) {
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		Brawler rogue = gameNode.getBrawler(id);
		Brawler sentinel = gameNode.getBrawler("Sentinel");
		boolean haunted = (rogue.isOnBoard() && gameNode.getLocation(rogue.getFloor(), rogue.getColumn(), rogue.getRow()).isGhostEffect());
		boolean lockedDown = (rogue.isOnBoard() && sentinel.isOnBoard() && (sentinel.getFloor() == rogue.getFloor()));
		if (rogue.isUsedLast() || haunted || lockedDown) return results;
		chkAdv = 0.0;
		int chkSpc = 0;
		double preAdjust = 0, postAdjust = 0;
		int[] onBoardCount = countOnBoardCharacters(redsTurn);
		Brawler myWarrior = gameNode.getBrawler("My Warrior");
		Brawler myCleric = gameNode.getBrawler("My Cleric");
		Brawler myMage = gameNode.getBrawler("My Mage");
		Brawler ghost = gameNode.getBrawler("Ghost");
		
		if (!rogue.isOnBoard()){ // we can only place
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			for (int u = 3; u > 0; u--) if (onBoardCount[0] < u) preAdjust += (0.1 * inFavor); // place it if you have <3 characters on board
			// other preAdjustments go here
			
			for (int w = 0; w < legitSpaces.length; w++) {
				chkSpc = legitSpaces[w];
				int[] fcr = GridBrawl.splitBoardSpace(chkSpc);
				// This is a Character, so it's better not to place it out on the "frontier" on floor 1
				if (fcr[1] == 4 || fcr[2] == 4) postAdjust += (.75 * against);
				
				// if Ghost is not on-board, don't place this guy adjacent to another friendly Character
				if (!ghost.isOnBoard()) {
					if (myWarrior.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
					if (myCleric.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
					if (myMage.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
				}
				// Don't place the Rogue where its movement will be blocked on all sides.
				int blockers = 0;
				if (fcr[1] > 0 && gameNode.getLocation(1, fcr[1] - 1, fcr[2]).isOccupied()) blockers++;
				if (fcr[1] < 4 && gameNode.getLocation(1, fcr[1] + 1, fcr[2]).isOccupied()) blockers++;
				if (fcr[2] > 0 && gameNode.getLocation(1, fcr[1], fcr[2] - 1).isOccupied()) blockers++;
				if (fcr[2] < 4 && gameNode.getLocation(1, fcr[1], fcr[2] + 1).isOccupied()) blockers++;
				if (blockers == 2) postAdjust += (.25 * against);
				else if (blockers == 3) postAdjust += (.75 * against);
				else if (blockers == 4) postAdjust += (1.5 * against);
				if (gameNode.getRound() > 10 && willIGetMyselfKilledHere(gameNode, chkSpc)) postAdjust += (2 * against);
				// other postAdjustments go here
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else {
			int[] pgR = gameNode.buildPlayground(id);
			
			// move
			int[] moveTargets = this.sortPlaygroundForMove(gameNode, pgR, id);
			for (int m = 0; m < moveTargets.length; m++) {
				chkSpc = moveTargets[m];
				// postAdjust: moving up is encouraged, especially if level > floor
				int lazy = (rogue.getLevel() - rogue.getFloor());
				if ((chkSpc == rogue.getLoc() + 100) && (lazy > 0)) {
					if (rogue.getFloor() == 1) lazy *= 2;
					if (rogue.isGlorified()) lazy = 0;
					postAdjust = (lazy * inFavor);	
				}
				
				// postAdjust: Moving into the Glory corridor is encouraged when lvl == 4
				if ((chkSpc % 100 == 0) && (gameNode.getBrawler(id).getLevel() == 4)) {
					postAdjust = (5 * inFavor);
				}
				
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 2, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 2, chkSpc, -1, chkAdv, redsTurn);
				postAdjust = 0;
			}
			
			// attack
			// preAdjust: attacking generally a good plan for Characters
			preAdjust = (3 * inFavor);
			int[] targets = this.sortPlaygroundForAttack(gameNode, pgR, id);
			if (targets.length > 0) {
				for (int att = 0; att < targets.length; att++) {
					chkSpc = targets[att];
					int victim = gameNode.getLocation(targets[att]).getOccupiedBy();
					// postadjust: The "Must Kill" rule: If opponent only has 1 onboard Character and it can be attacked, it must be.
					int[] cob = this.countOnBoardCharacters(redsTurn);
					if (cob[1] == 1 && gameNode.getBrawler(victim) instanceof Character) {
						return new AIresult(id, 3, chkSpc, victim, (1000 * inFavor), redsTurn); // If you don't do this, you're just being a dick.
					}
					// postAdjust: do let's attack the Merchant
					if (gameNode.getBrawler(victim) instanceof Merchant && !rogue.isHandsFull()) postAdjust += 3 * inFavor;
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 3, chkSpc, victim) + postAdjust;
					results.updateMyself(id, 3, chkSpc, victim, chkAdv, redsTurn);
				}
			}
			preAdjust = 0;
			postAdjust = 0;
			
			// glorify
			if (rogue.getLevel() == 4 && rogue.getFloor() == 5 && !rogue.isGlorified()) {
				// preAdjust: if you can do this, it's the best thing you can do.
				// preAdjust: However!  If you have no other Characters on the Board you CANNOT do this.
				int[] cob = this.countOnBoardCharacters(redsTurn);
				if (cob[0] > 1) {
					return new AIresult(id, 6, 500, -1, (1000 * inFavor), redsTurn);
				}
			}
			
			// purchase
			if (rogue.getPossession() == gameNode.getBrawler("Diamond").getPieceID() && 
					rogue.getFloor() == gameNode.getBrawler("Merchant").getFloor()) {
				for (int tsr = 20; tsr <= 22; tsr++) {
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 8, 0, tsr) + postAdjust;
					results.updateMyself(id, 8, 0, tsr, chkAdv, redsTurn);
				}
			}
			
			// transfer
			if (rogue.isHandsFull()) {
				int potentialRecipient = this.getBestTransferTarget(id);
				if (potentialRecipient != -1) {
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 10, 0, potentialRecipient);
					results.updateMyself(id, 10, 0, potentialRecipient, chkAdv, redsTurn);
				}
			}
			
			// pickpocket
			if (!rogue.isHandsFull() && rogue.isPoweredUp()) {
				int mark = this.getBestPickpocketTarget(id);
				if (mark != -1) {
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 7, 0, mark);
					results.updateMyself(id, 7, 0, mark, chkAdv, redsTurn);
				}
			}
			
			// bribe
			if (rogue.isHandsFull() && rogue.getPossession() == 23) {
				boolean[] available = getAvailableToBribe(id);
				byte thereIsAtLeastOne = 0;
				for (int r = 0; r < available.length; r++) if (available[r]) thereIsAtLeastOne++;
				if (thereIsAtLeastOne > 0) {
					int[] bribeMe = getBestBribe(available);
					if (bribeMe[0] != -1) {
						if (bribeMe[1] == 500) postAdjust += (5 * inFavor); // definitely do this if you can.
						chkAdv = preAdjust + this.getAdvantageFromAction(id, 12, bribeMe[1], bribeMe[0]) + postAdjust;
						results.updateMyself(id, 12, bribeMe[1], bribeMe[0], chkAdv, redsTurn);
					}
				}
			}
			postAdjust = 0;	
		}
		return results;
	}
	
	public AIresult bestAction_Nemesis(int id, boolean redsTurn) {
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		Brawler nem = gameNode.getBrawler(id);
		boolean haunted = (nem.isOnBoard() && gameNode.getLocation(nem.getFloor(), nem.getColumn(), nem.getRow()).isGhostEffect());
		if (nem.isUsedLast() || haunted) return results;
		chkAdv = 0.0;
		int chkSpc = 0;
		double preAdjust = 0, postAdjust = 0;
		if (!nem.isOnBoard()){ // we can only place
			// This is an extra check on "canNemesisBePlaced()"
			boolean placeNemesisOkay = false;
			int cl4CharsOnBoard = 0;
			int nemesesOnBoard = 0;
			for (int c = 0; c <= 7; c++) if (gameNode.getBrawler(c).isOnBoard() && gameNode.getBrawler(c).getLevel() == 4) cl4CharsOnBoard++;
			for (int n = 8; n <= 11; n++) if (gameNode.getBrawler(n).isOnBoard()) nemesesOnBoard++;
			if (nemesesOnBoard <= cl4CharsOnBoard && gameNode.getRound() > 10) placeNemesisOkay = true;
			if (!placeNemesisOkay) return new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			// don't place this Nemesis if opponent's same suit Character is not the one on level 4.
			int[] oppChars = {4, 5, 6, 7};
			if (!redsTurn) for(int i = 0; i <= 3; i++) oppChars[i] = i;
			Brawler[] opC = new Brawler[4];
			for (int j = 0; j <= 3; j++) opC[j] = gameNode.getBrawler(j);
			for (int k = 0, n = 8; k <= 3; k++, n++) if (nem.getPieceID() == n && opC[k].getLevel() != 4) preAdjust += (3 * against);
			// other preAdjustments go here
			
			for (int w = 0; w < legitSpaces.length; w++) {
				chkSpc = legitSpaces[w];
				int[] fcr = GridBrawl.splitBoardSpace(chkSpc);
				// Don't place a Nemesis directly above an opponent's level 4 Character
				for (int p = 0; p <= 3; p++) {
					if (opC[p].getFloor() == 3 && opC[p].getLevel() == 4 && opC[p].getColumn() == fcr[1] && opC[p].getRow() == fcr[2]) postAdjust += (2 * against);
				}
				// other postAdjustments go here
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else { // we are in the brawl
			gameNode.buildPlayground(id);
			
			// move
			int[] moveTargets = this.sortPlaygroundForMove(gameNode, gameNode.buildPlayground(id), id);
			for (int m = 0; m < moveTargets.length; m++) {
				chkSpc = moveTargets[m];
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 2, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 2, chkSpc, -1, chkAdv, redsTurn);
			}
			
			// attack and/or fireball
			int[] targets = this.sortPlaygroundForAttack(gameNode, gameNode.buildPlayground(id), id);
			if (targets.length > 0) {
				for (int att = 0; att < targets.length; att++) {
					chkSpc = targets[att];
					int victim = gameNode.getLocation(targets[att]).getOccupiedBy();
					boolean colorMatch = ((gameNode.isRedsTurn() && gameNode.getBrawler(victim).isRed()) || 
							(!gameNode.isRedsTurn() && gameNode.getBrawler(victim).isBlue()));
					if (colorMatch) continue; // don't even try to have a nemesis attack your own characters
					// postAdjust: Having a Nemesis attack an NPC or a Treasure is probably a waste of a turn
					if (gameNode.getBrawler(victim) instanceof NPC || gameNode.getBrawler(victim) instanceof Treasure) {
						postAdjust = (4 * against);
					}
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 3, chkSpc, victim) + postAdjust;
					results.updateMyself(id, 3, chkSpc, victim, chkAdv, redsTurn);
					if (nem instanceof Dragon) {
						chkAdv = preAdjust + this.getAdvantageFromAction(id, 4, chkSpc, victim) + postAdjust;
						results.updateMyself(id, 4, chkSpc, victim, chkAdv, redsTurn);
					}
				}	
			}
			postAdjust = 0;
			
			// flee
			// preadjust: if you have any Characters off-board, fleeing a nemesis is a good idea
			if (nem.getFloor() == 1) {
				int[] cob = this.countOnBoardCharacters(redsTurn);
				if (cob[0] < 4) preAdjust = (4 * inFavor);
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 5, 0, -1) + postAdjust;
				results.updateMyself(id, 5, 0, -1, chkAdv, redsTurn);
			}
		}
		return results;
	}
	
	public AIresult bestAction_Monster(int id, boolean redsTurn) {
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		Brawler mons = gameNode.getBrawler(id);
		boolean haunted = (mons.isOnBoard() && gameNode.getLocation(mons.getFloor(), mons.getColumn(), mons.getRow()).isGhostEffect());
		if (mons.isUsedLast() || haunted) return results;
		chkAdv = 0.0;
		int chkSpc = 0;
		double preAdjust = 0, postAdjust = 0;
		if (!mons.isOnBoard()){ // we can only place
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			results.setAction(1);
			// other preAdjustments go here
			for (int w = 0; w < legitSpaces.length; w++) {
				chkSpc = legitSpaces[w];
				// int[] fcr = splitBoardSpace(chkSpc);
				
				// other postAdjustments go here
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else { // we are in the brawl	
			gameNode.buildPlayground(id);
			
			// move
			int[] moveTargets = this.sortPlaygroundForMove(gameNode, gameNode.buildPlayground(id), id);
			for (int m = 0; m < moveTargets.length; m++) {
				chkSpc = moveTargets[m];
				if (chkSpc == 500) continue;
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 2, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 2, chkSpc, -1, chkAdv, redsTurn);
			}
			
			// attack
			int[] targets = this.sortPlaygroundForAttack(gameNode, gameNode.buildPlayground(id), id);
			if (targets.length > 0) {
				for (int att = 0; att < targets.length; att++) {
					chkSpc = targets[att];
					int victim = gameNode.getLocation(targets[att]).getOccupiedBy();
					// postadjust: The "Must Kill" rule: If opponent only has 1 onboard Character and it can be attacked, it must be.
					int[] cob = this.countOnBoardCharacters(redsTurn);
					if (cob[1] == 1 && gameNode.getBrawler(victim) instanceof Character) {
						return new AIresult(id, 3, chkSpc, victim, (1000 * inFavor), redsTurn); // If you don't do this, you're just being a dick.
					}
					boolean colorMatch = ((gameNode.isRedsTurn() && gameNode.getBrawler(victim).isRed()) || 
							(!gameNode.isRedsTurn() && gameNode.getBrawler(victim).isBlue()));
					if (colorMatch) continue; // don't even try to have a monster attack your own characters
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 3, chkSpc, victim) + postAdjust;
					results.updateMyself(id, 3, chkSpc, victim, chkAdv, redsTurn);
				}
			}
			
			
			// flee
				
			if (mons.getFloor() == 1) {
				// preadjust: having level 1 or 2 monsters flee from the brawl is generally a waste of a turn
				if (mons.getLevel() < 3) preAdjust = (5 * against);
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 5, 0, -1) + postAdjust;
				results.updateMyself(id, 5, 0, -1, chkAdv, redsTurn);
			}			
		}
		return results;
	}
	
	public AIresult bestAction_Princess(int id, boolean redsTurn) {
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		Brawler prin = gameNode.getBrawler(id);
		boolean haunted = (prin.isOnBoard() && gameNode.getLocation(prin.getFloor(), prin.getColumn(), prin.getRow()).isGhostEffect());
		if (prin.isUsedLast() || haunted) return results;
		chkAdv = 0.0;
		int chkSpc = 0;
		Brawler myWar = gameNode.getBrawler("My Warrior");
		Brawler opWar = gameNode.getBrawler("Enemy Warrior");
		double preAdjust = 0, postAdjust = 0;
		if (!prin.isOnBoard()){ // we can only place
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			// if it's round 1, it's a good idea to place the princess but I don't want AI to do it every time.
			if (gameNode.getRound() == 1) {
				int r1Factor = (int) (Math.random() * 4);
				preAdjust += (r1Factor * inFavor);
			}
			// placing Princess generally discouraged if my Warrior possesses Treasure
			if (myWar.isHandsFull()) preAdjust += (2 * against);
			// other preAdjustments go here
			
			for (int w = 0; w < legitSpaces.length; w++) {
				chkSpc = legitSpaces[w];
				int[] fcr = GridBrawl.splitBoardSpace(chkSpc);
				if (myWar.getFloor() == 3) {
					// placing Princess generally good, but not directly above my Warrior (vulnerability to Dragon)
					if (fcr[1] == myWar.getColumn() && fcr[2] == myWar.getRow()) postAdjust = (1 * against);
					else postAdjust += (2* inFavor);
				}
				if (opWar.getFloor() == 3) {
					// placing Princess generally bad, unless directly above opponent's Warrior (vulnerability to Dragon)
					if (fcr[1] == opWar.getColumn() && fcr[2] == opWar.getRow()) postAdjust = (1 * inFavor);
					else postAdjust += (2* against);
				}
				
				// other postAdjustments go here
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else { // we are in the brawl
			gameNode.buildPlayground(id);
			
			// move
			// preAdjust: Why are you even moving the Princess?
			preAdjust = (3 * against);
			int[] moveTargets = this.sortPlaygroundForMove(gameNode, gameNode.buildPlayground(id), id);
			for (int m = 0; m < moveTargets.length; m++) {
				chkSpc = moveTargets[m];
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 2, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 2, chkSpc, -1, chkAdv, redsTurn);
			}
			preAdjust = 0;
			
			// flee
			if (prin.getFloor() == 1) {
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 5, 0, -1) + postAdjust;
				results.updateMyself(id, 5, 0, -1, chkAdv, redsTurn);
			}				
		}
		return results;
	}
	
	public AIresult bestAction_Leper(int id, boolean redsTurn) {
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		Brawler leper = gameNode.getBrawler(id);
		boolean haunted = (leper.isOnBoard() && gameNode.getLocation(leper.getFloor(), leper.getColumn(), leper.getRow()).isGhostEffect());
		if (leper.isUsedLast() || haunted) return results;
		chkAdv = 0.0;
		int chkSpc = 0;
		Brawler myCler = gameNode.getBrawler("My Cleric");
		Brawler opCler = gameNode.getBrawler("Enemy Cleric");
		double preAdjust = 0, postAdjust = 0;
		if (!leper.isOnBoard()){ // we can only place
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			// if !myCler.isOnBoard() and opCler.isOnBoard() definitely place the Leper right away.
			if (!myCler.isOnBoard() && opCler.isOnBoard()) preAdjust += (3 * inFavor);
			// other preAdjustments go here
			
			for (int w = 0; w < legitSpaces.length; w++) {
				chkSpc = legitSpaces[w];
				int[] fcr = GridBrawl.splitBoardSpace(chkSpc);
				
				// if oppCler is not on the board, do not place the Leper on floor 2 above an empty space unless myCler is on floor 2
				if (fcr[0] == 2 && !opCler.isOnBoard() && !gameNode.getLocation(1, fcr[1], fcr[2]).isOccupied()) {
					if (myCler.getFloor() != 2) postAdjust += (4 * against);
				}
				// if space in question is directly above my Cleric, give a small boost in favor.
				if (fcr[0] - 1 == myCler.getFloor() && fcr[1] == myCler.getColumn() && fcr[2] == myCler.getRow()) postAdjust += (1 * inFavor);
				
				// other postAdjustments go here
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else { // we are in the brawl
			gameNode.buildPlayground(id);
			
			// move
			
			// preAdjust: There is little reason to touch the Leper unless it's in opponent Cleric's playground
				int[] opCl = gameNode.buildPlayground(opCler.getPieceID());
				boolean leperSafe = true;
				for (int m = 0; m < opCl.length; m++) if (opCl[m] == leper.getLoc()) leperSafe = false;
				if (leperSafe) preAdjust = (20 * against);
			
			int[] moveTargets = this.sortPlaygroundForMove(gameNode, gameNode.buildPlayground(id), id);
			for (int m = 0; m < moveTargets.length; m++) {
				chkSpc = moveTargets[m];
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 2, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 2, chkSpc, -1, chkAdv, redsTurn);
				preAdjust = 0;
			}
			
			// flee
			if (leper.getFloor() == 1) {
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 5, 0, -1) + postAdjust;
				results.updateMyself(id, 5, 0, -1, chkAdv, redsTurn);
			}	
		}
		return results;
	}
	
	public AIresult bestAction_Ghost(int id, boolean redsTurn) {
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		Brawler ghost = gameNode.getBrawler(id);
		if (ghost.isUsedLast()) return results;
		chkAdv = 0.0;
		int chkSpc = 0;
		int[] onBoardCount = countOnBoardCharacters(redsTurn);
		double preAdjust = 0, postAdjust = 0;
		Brawler myWarrior = gameNode.getBrawler("My Warrior");
		Brawler myCleric = gameNode.getBrawler("My Cleric");
		Brawler myMage = gameNode.getBrawler("My Mage");
		Brawler myRogue = gameNode.getBrawler("My Rogue");
		Brawler oppMage = gameNode.getBrawler("Enemy Mage");
		
		if (!ghost.isOnBoard()){ // we can only place
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			// don't place the Ghost if opponent has < 3 Characters on-board
			int gg = 3 - onBoardCount[1];
			preAdjust += (gg * against);  // if opp has all 4 chars on board gg will be -1, this will preadjust in favor.
			// other preAdjustments go here
			
			for (int w = 0; w < legitSpaces.length; w++) {
				chkSpc = legitSpaces[w];
				int[] fcr = GridBrawl.splitBoardSpace(chkSpc);
				
				// don't place the ghost adjacent to a friendly Character
				if (myWarrior.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
				if (myCleric.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
				if (myMage.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
				if (myRogue.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (1 * against);
				
				// but do place him next to opponent's mage.
				if (oppMage.isAdjacentToMe(fcr[0], fcr[1], fcr[2])) postAdjust += (2 * inFavor);
				
				// other postAdjustments go here
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else { // we are in the brawl
			gameNode.buildPlayground(id);
			
			// move
			int[] moveTargets = this.sortPlaygroundForMove(gameNode, gameNode.buildPlayground(id), id);
			// preadjust: if my mage is trapped by the ghost, move the ghost
			if (gameNode.getLocation(myMage.getLoc()).isGhostEffect()) preAdjust = (4 * inFavor);
			int[] myMagePlayground = gameNode.buildPlayground(myMage.getPieceID());
			for (int m = 0; m < moveTargets.length; m++) {
				chkSpc = moveTargets[m];
				// postAdjust: don't move the ghost to trap my own mage
				for (int k = 0; k < 9; k++) if (myMagePlayground[k] == chkSpc) postAdjust = (4 * against);
				
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 2, chkSpc, -1) + postAdjust;
				
				results.updateMyself(id, 2, chkSpc, -1, chkAdv, redsTurn);
				postAdjust = 0;
			}
			preAdjust = 0;
			/*
			// flee
			if (ghost.getFloor() == 1) {
				// preadjust: having the Ghost flee is generally a bad strategy.
					preAdjust = (7 * against);
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 5, 0, -1) + postAdjust;
				results.updateMyself(id, 5, 0, -1, chkAdv, redsTurn);
				preAdjust = 0;
			}	
			*/
			// preadjust: if ghost is trapping your Brawlers (especially the mage) moving ghost or fleeing is a good idea	
		}
		return results;
	}
	
	public AIresult bestAction_Merchant(int id, boolean redsTurn) {
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		Brawler merc = gameNode.getBrawler(id);
		boolean haunted = (merc.isOnBoard() && gameNode.getLocation(merc.getFloor(), merc.getColumn(), merc.getRow()).isGhostEffect());
		if (merc.isUsedLast() || haunted) return results;
		chkAdv = 0.0;
		int chkSpc = 0;
		Brawler myRogue = gameNode.getBrawler("My Rogue"); 
		Brawler opRogue = gameNode.getBrawler("Enemy Rogue");
		double preAdjust = 0, postAdjust = 0;
		if (!merc.isOnBoard()){ // we can only place
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			// if opponent's Rogue is on board and mine is not, definitely place the Merchant right away.
			if (!myRogue.isOnBoard() && opRogue.isOnBoard()) preAdjust += (3 * inFavor);
			// other preAdjustments go here
			
			for (int w = 0; w < legitSpaces.length; w++) {
				chkSpc = legitSpaces[w];
				int[] fcr = GridBrawl.splitBoardSpace(chkSpc);
				
				// if opponent's Rogue is on the board, do not place the Merchant anywhere directly above or below it.
				if (opRogue.isOnBoard() && fcr[1] == opRogue.getColumn() && fcr[2] == opRogue.getRow()) postAdjust += (4 * against);
				// if space in question is anywhere directly above or below my Rogue, small boost in favor
				if (myRogue.isOnBoard() && fcr[1] == myRogue.getColumn() && fcr[2] == myRogue.getRow()) postAdjust += (1 * inFavor);
				// other postAdjustments go here
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else { // we are in the brawl
			gameNode.buildPlayground(id);
			// move
			
			// preAdjust: There is little reason to touch the Merchant unless it's in opponent Rogue's playground
			int[] opRg = gameNode.buildPlayground(opRogue.getPieceID());
			boolean merchantSafe = true;
			for (int m = 0; m < opRg.length; m++) if (opRg[m] == merc.getLoc()) merchantSafe = false;
			if (merchantSafe) preAdjust = (5 * against);
			// preAdjust: if your Rogue already has the Diamond, seriously don't fuck with the Merchant
			boolean leaveBritneyAlone = (myRogue.getPossession() == gameNode.getBrawler("Diamond").getPieceID());
			if (leaveBritneyAlone) preAdjust = (5 * against);
			
			int[] moveTargets = this.sortPlaygroundForMove(gameNode, gameNode.buildPlayground(id), id);
			for (int m = 0; m < moveTargets.length; m++) {
				chkSpc = moveTargets[m];
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 2, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 2, chkSpc, -1, chkAdv, redsTurn);
			}
			preAdjust = 0;
		}
		return results;
	}
	
	// This method should be finished.  There is very little that can be done with treasures.
	public AIresult bestAction_Treasure(int id, boolean redsTurn) {
		chkAdv = 0.0;
		AIresult results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		Brawler treasure = gameNode.getBrawler(id);
		double preAdjust = 0, postAdjust = 0;
		if (!treasure.isOnBoard()){ // we can only place
			int[] legitSpaces = this.getAllLegitPlacementTargets(id);
			// other preAdjustments go here
			for (int w = 0; w < legitSpaces.length; w++) {
				int chkSpc = legitSpaces[w];
				// other postAdjustments go here
				chkAdv = preAdjust + this.getAdvantageFromAction(id, 1, chkSpc, -1) + postAdjust;
				results.updateMyself(id, 1, legitSpaces[w], -1, chkAdv, redsTurn);
			}
		}
		else { // If the Treasure is already on the board, there is nothing we can do with it unless it's on the glory space
			// sacrifice
			if (treasure.getFloor() == 5) {
				int possessor = -1;
				for (int b = 0; b < 16; b++) if (gameNode.getBrawler(b).getPossession() == id) possessor = b;
				if (possessor == -1) return results; // this shouldn't happen.
				Brawler owner = gameNode.getBrawler(possessor);
				boolean colorMatch = ((redsTurn && owner.isRed()) || (!redsTurn && owner.isBlue()));
				if (!colorMatch) return results;
				if (gameNode.getBrawler(id) instanceof Diamond) {
					boolean[] available = this.getAvailableToBribe(id);
					int[] target = this.getBestBribe(available);
					if (target[0] != -1) {
						chkAdv = preAdjust + this.getAdvantageFromAction(id, 9, target[1], target[0]);
						results.updateMyself(id, 9, target[1], target[0], chkAdv, redsTurn);
					}
				}
				else {
					int sackTarget = this.getBestSacrificeTarget_notDiamond(id, redsTurn);
					chkAdv = preAdjust + this.getAdvantageFromAction(id, 9, 0, sackTarget);
					results.updateMyself(id, 9, 0, sackTarget, chkAdv, redsTurn);
				}	
			}
			else results = new AIresult(id, 0, 0, 0, gameNode.getAdvantage(), redsTurn);
		}
		return results;
	}
	
	/**
	 * This method returns two ints, [0] is number of my onBoard Characters, and [1] is number of opponent's onBoard Characters
	 */
	public int[] countOnBoardCharacters(boolean iAmRed) {
		int rob = 0, bob = 0;
		int[] tell = new int[2];
		for (int r = 0, b = 4; r < 4; r++, b++) {
			Brawler checkRed = gameNode.getBrawler(r);
			Brawler checkBlue = gameNode.getBrawler(b);
			if (checkRed.isOnBoard()) rob++;
			if (checkBlue.isOnBoard()) bob++; 
		}
		if (iAmRed) {
			tell[0] = rob;	tell[1] = bob;
		}
		else {
			tell[0] = bob;	tell[1] = rob;
		}
		return tell;
	}
	
	// prereq: passed in array must be only occupied target squares
	// this method is only called by sortPlaygroundForAttack()
	private int[] getAllLegitAttackTargets(AInode node, int[] sortedPlayground, int att) {
		if (sortedPlayground.length == 0) return new int[0];
		int spl = sortedPlayground.length;
		Brawler attacker = node.getBrawler(att);
		if (attacker instanceof NPC || attacker instanceof Treasure) return new int[0];
		int targets = 0;
		boolean[] marks = new boolean[spl];
		for (int i = 0; i < spl; i++) {
			BoardSpace examined = node.getLocation(sortedPlayground[i]);
			if (!examined.isOccupied()) continue; // just to be safe
			Brawler victim = node.getBrawler(examined.getOccupiedBy());
			Brawler princess = node.getBrawler("Princess");
			if (princess.getFloor() == victim.getFloor() && attacker instanceof Character && victim instanceof Character) continue;
			else if (victim instanceof Leper && !(attacker instanceof Cleric) && attacker.getLevel() == 1) continue;
			else if (victim instanceof Ghost) continue;
			else if (victim instanceof Treasure && attacker.isHandsFull()) continue;
			else if (attacker.canAttack(victim)) {
				marks[i] = true;
				targets++;
			}
		}
		if (targets == 0) return new int[0];
		int[] targetList = new int[targets];
		for (int j = 0; j < spl; j++) {
			if (marks[j]) targetList[--targets] = sortedPlayground[j];
		}
		return targetList;
	}
	
	public int[] getAllLegitPlacementTargets(int id) {
		Brawler placeMe = gameNode.getBrawler(id);
		int[] spaces = new int[60];
		int xy = 0;
		int locID = 0;
		boolean[] lookOnFloor = {false, true, true, true, true}; // there is no floor zero
		if (placeMe instanceof Character) for (int a = 2; a <= 4; a++) lookOnFloor[a] = false;
		if (placeMe instanceof Nemesis || placeMe instanceof Princess) for (int b = 1; b <= 3; b++) lookOnFloor[b] = false;
		int ccrr = 5;
		BoardSpace examined;
		for (int fl = 1; fl <= 4; fl++) {
			if (lookOnFloor[fl]) {
				switch (fl) {
				case 1: ccrr = 5;	break;
				case 2: ccrr = 4;	break;
				default: ccrr = 3;	break;
				}
				for (int cc = 0; cc < ccrr; cc++) {
					for (int rr = 0; rr < ccrr; rr++) {
						locID = ((fl * 100) + (cc * 10) + rr);
						examined = gameNode.getLocation(locID);
						if (!examined.isOccupied()) {
							spaces[xy] = locID;
							xy++;
						}
					}
				}
			}
		} // end of fl loop
		int[] realSpaces = new int[xy];
		for (int d = 0; d < xy; d++) realSpaces[d] = spaces[d];
		return realSpaces;
	}
	
	/**
	 * 0: do nothing	1: place	2: move		3: attack
	 * 4: fireball		5: flee		6: glorify	7: pickpocket
	 * 8: purchase		9: sack		10: transfer
	 * 11: humble		12: bribe
	 */
	public double getAdvantageFromAction(int id, int action, int loc, int victim) {
		String actionString = "";
		switch (action) {
		case 1: actionString = hands.executePlacement(gNode, id, loc);					break;
		case 2: actionString = hands.executeMove(gNode, id, loc);						break;
		case 3: actionString = hands.executeAttack(gNode, id, loc);						break;	
		case 4: actionString = hands.executeFireball(gNode, loc);						break;
		case 5: actionString = hands.executeFlee(gNode, id);							break;
		case 6: actionString = hands.executeGlorify(gNode, id);							break;
		case 7: actionString = hands.executePickpocket(gNode, id, victim);				break;
		case 8: actionString = hands.executePurchase(gNode, id, victim);				break;
		case 9: actionString = hands.executeSacrifice(gNode, id, victim, loc);			break;
		case 10:actionString = hands.executeTransferTreasure(gNode, id, victim, true);	break;
		case 11:actionString = hands.executeHumble(gNode, id, victim);					break;
		case 12:actionString = hands.executeBribe(gNode, id, victim, loc);				break;
		}
		int[] scores = new int[2];
		scorer.reset(actionString);
		if (gameNode.isInSquareOff()) scores = scorer.squareOffScore();
		else scores = scorer.brawlScore(); 
		return scorer.calculateAdvantage(scores);
	}
	
	public boolean[] getAvailableToBribe(int briber) {
		boolean[] available = new boolean[24];
		LineOfSight los = new LineOfSight();
		Brawler x = gameNode.getBrawler(briber);
		if (x.getFloor() == 5) { // This is a Diamond sacrifice situation
			for (int c = 0; c < gameNode.getBrawlersLength(); c++) {
				if (gameNode.getBrawler(c).isOnBoard()) available[c] = true;
			}
		}
		else {
			for (int f = 0; f < gameNode.getBrawlersLength(); f++) {
				available[f] = los.AIcalculate(gameNode, x, gameNode.getBrawler(f));
				if (gameNode.getBrawler(f) instanceof Treasure) available[f] = false; // you can't bribe treasure
			}
		}
		available[briber] = false; // one can never bribe one's self.
		return available;
	}
	
	// will test each available Brawler on every single unoccupied space
	// returns [0] : best bribe target ID and [1]: best Location
	// used for Bribe and for Diamond sacrifice
	public int[] getBestBribe(boolean[] available) {
		double testAdvantage = 0.0;
		int loc = 0;
		scorer.reset(gNode);
		double currentBestAdvantage = scorer.calculateAdvantage(gameNode.getScores());
		int ccrr = 5;
		int[] result = {-1, -1};
		String examined = "";
		for (int x = 0; x < gameNode.getBrawlersLength(); x++) {
			if (available[x]) {
				if (this.getBestBribe_forGlory(x)) {
					int[] bribeTheGloryHound = {x, 500};
					return bribeTheGloryHound;
				}
				for (int f = 1; f <= 5; f++) {
					if (f == 2) ccrr = 4;
					if (f == 3 || f == 4) ccrr = 3;
					if (f == 5) ccrr = 1;
					for (int c = 0; c < ccrr; c++) {
						for (int r = 0; r < ccrr; r++) {
							if (gameNode.getLocation(f,c,r).isOccupied()) continue;
							loc = ((f * 100) + (c * 10) + r);
							examined = hands.executeMove(gNode, x, loc);
							scorer.reset(examined);
							testAdvantage = scorer.calculateAdvantage(scorer.brawlScore());
							if (gameNode.isRedsTurn() && testAdvantage > currentBestAdvantage) {
								currentBestAdvantage = testAdvantage;
								result[0] = x;
								result[1] = loc;
							}
							else if (!gameNode.isRedsTurn() && testAdvantage < currentBestAdvantage) {
								currentBestAdvantage = testAdvantage;
								result[0] = x;
								result[1] = loc;
							}
						} // end of r loop
					} // end of c loop
				} // end of f loop
			}
		} // end of x loop
		return result;
	}
	
	// this is a helper method for getBestBribe()--details a very specific circumstance the AI will want to exploit.
	private boolean getBestBribe_forGlory(int id) {
		Brawler carl = gameNode.getBrawler(id);
		if (carl instanceof Character && !carl.isGlorified() && carl.getLevel() == 4) {
			if ((gameNode.isRedsTurn() && carl.isRed()) || (!gameNode.isRedsTurn() && carl.isBlue())) {
				if (!gameNode.getLocation(500).isOccupied()) return true;
			}
		}
		return false;
	}
	
	// returns pieceID of friendly Character who would be best to receive a humble.
	public int getBestHumbleTarget(int id) {
		int target = -1;
		int[] q = new int[3];
		Brawler myWar = gameNode.getBrawler("My Warrior");
		if (!myWar.isOnBoard() || myWar.getLevel() == 4) q[0] = 0;
		else {
			q[0] = myWar.getLevel() + myWar.getFloor();
			if (myWar.isHandsFull()) q[0]++;
		}
		Brawler myMag = gameNode.getBrawler("My Mage");
		if (!myMag.isOnBoard() || myMag.getLevel() == 4) q[1] = 0;
		else {
			q[1] = myMag.getLevel() + myMag.getFloor();
			if (myMag.isHandsFull()) q[1]++;
		}
		Brawler myRog = gameNode.getBrawler("My Rogue");
		if (!myRog.isOnBoard() || myRog.getLevel() == 4) q[2] = 0;
		else {
			q[2] = myRog.getLevel() + myRog.getFloor();
			if (myRog.isHandsFull()) q[2]++;
		}
		if (q[2] != 0 && (q[2] >= q[1]) && (q[2] >= q[0]))  target = myRog.getPieceID();
		if (q[1] != 0 && (q[1] >= q[2]) && (q[1] >= q[0]))  target = myMag.getPieceID();
		if (q[0] != 0 && (q[0] >= q[1]) && (q[0] >= q[2]))  target = myWar.getPieceID();
		return target;
	}
	
	// returns -1 if no good pickpocket target is available.
	public int getBestPickpocketTarget(int id) {
		Brawler rogue = gameNode.getBrawler(id);
		int target = -1;
		LineOfSight los = new LineOfSight();
		for (int j = 0; j < 16; j++) {
			Brawler x = gameNode.getBrawler(j);
			boolean colorMatch = ((rogue.isRed() && x.isRed()) || (rogue.isBlue() && x.isBlue()));
			if (colorMatch) continue; // don't steal from your friends
			if (los.AIcalculate(gameNode, rogue, x) && x.isHandsFull()) {
				if (x.getPossession() == gameNode.getBrawler("Diamond").getPieceID()) return x.getPieceID();
				else target = x.getPieceID(); // keep looking if it's not the Diamond, but use this one if Diamond is not found.
			}
		}
		return target;
	}
	
	public int getBestSacrificeTarget_notDiamond(int lamb, boolean redsTurn) {
		Brawler loot = gameNode.getBrawler(lamb);
		int target = -1;	int most = 0;	int choice = 0;
		if (loot instanceof Sword) {
			Brawler[] enemies = new Brawler[4];
			enemies[0] = gameNode.getBrawler("Enemy Warrior");
			enemies[1] = gameNode.getBrawler("Enemy Cleric");
			enemies[2] = gameNode.getBrawler("Enemy Mage");
			enemies[3] = gameNode.getBrawler("Enemy Rogue");
			int[] threatScores = new int[4];
			for (int th = 0; th < 4; th++) {
				if (!enemies[th].isOnBoard()) continue;
				threatScores[th] += enemies[th].getLevel();
				if (enemies[th].getFloor() > 2)  threatScores[th]++;
				if (enemies[th].isHandsFull()) {
					threatScores[th]++;
					Brawler hisLoot = gameNode.getBrawler(enemies[th].getPossession());
					if (hisLoot.getSuit() == enemies[th].getSuit()) threatScores[th]++;
				}
			}
			for (int c = 0; c < 4; c++) {
				if (threatScores[c] >= most) {
					most = threatScores[c];
					choice = c;
				}
			}
			target = enemies[choice].getPieceID();
		}
		else if (loot instanceof Shield) {
			int[] pickMe = new int[4];
			Brawler[] friends = new Brawler[4];
			friends[0] = gameNode.getBrawler("My Warrior");
			friends[1] = gameNode.getBrawler("My Cleric");
			friends[2] = gameNode.getBrawler("My Mage");
			friends[3] = gameNode.getBrawler("My Rogue");
			for (int f = 0; f < 4; f++) {
				if (friends[f].getLevel() == 4 || !friends[f].isOnBoard()) continue;
				pickMe[f] = 4 - friends[f].getLevel(); // select friend who stands to benefit the most
				if (friends[f].isHandsFull()) pickMe[f]++;
			}
			for (int c = 0; c < 4; c++) {
				if (pickMe[c] > most) {
					most = pickMe[c];
					choice = c;
				}
			}
			target = friends[choice].getPieceID();
		}
		else if (loot instanceof Ring) {
			int[] pickMe = new int[4];
			Brawler[] friends = new Brawler[4];
			friends[0] = gameNode.getBrawler("My Warrior");
			friends[1] = gameNode.getBrawler("My Cleric");
			friends[2] = gameNode.getBrawler("My Mage");
			friends[3] = gameNode.getBrawler("My Rogue");
			
			for (int g = 0; g < 4; g++) {
				if (friends[g].isHandsFull() || !friends[g].isOnBoard()) continue;
				if (g == 2) pickMe[g] = 3;
				pickMe[g] += (3 - friends[g].getLevel());
			}
			for (int c = 0; c < 4; c++) {
				if (pickMe[c] >= most) {
					most = pickMe[c];
					choice = c;
				}
			}
			target = friends[choice].getPieceID();
		}
		return target;
	}
	
	// ID is assumed to be a Character possessing Treasure.
	// returns ID of Character who would benefit most from receiving this Treasure
	// if -1 is returned, then no Transfer can or should be made.
	public int getBestTransferTarget(int id) {
		Brawler owner = gameNode.getBrawler(id);
		if (owner.getPossession() == -1) return -1;
		Brawler loot = gameNode.getBrawler(owner.getPossession());
		if (owner.getSuit() == loot.getSuit()) return -1; // don't transfer away a same-suit Treasure
		int recipient = -1;
		LineOfSight los = new LineOfSight();
		switch (loot.getSuit()) {
		case 'W':
			Brawler myWar = gameNode.getBrawler("My Warrior");
			if (los.AIcalculate(gameNode, owner, myWar) && !myWar.isHandsFull()) recipient = myWar.getPieceID();
			break;
		case 'C':
			Brawler myCle = gameNode.getBrawler("My Cleric");
			if (los.AIcalculate(gameNode, owner, myCle) && !myCle.isHandsFull()) recipient = myCle.getPieceID();
			break;
		case 'M':
			Brawler myMag = gameNode.getBrawler("My Mage");
			if (los.AIcalculate(gameNode, owner, myMag) && !myMag.isHandsFull()) recipient = myMag.getPieceID();
			break;
		case 'R':
			Brawler myRog = gameNode.getBrawler("My Rogue");
			if (los.AIcalculate(gameNode, owner, myRog) && !myRog.isHandsFull()) recipient = myRog.getPieceID();
			break;
		}
		return recipient;
	}
	
	public int[] sortPlaygroundForMove(AInode node, int[] playground, int id) {
		int targs = 0;
		for (int i = 0; i < playground.length; i++) {
			if (playground[i] == 500 && !(node.getBrawler(id) instanceof Character)) continue;
			try {
				if (!node.getLocation(playground[i]).isOccupied()) targs++;
			} catch(NullPointerException ex) { continue; }
			
		}
		if (targs == 0) return new int[0];
		int[] emptyPlayground = new int[targs];
		int index = 0;
		for (int j = 0; j < playground.length; j++) {
			if (playground[j] == 500 && !(node.getBrawler(id) instanceof Character)) continue;
			if (!node.getLocation(playground[j]).isOccupied()) {
				emptyPlayground[index] = playground[j];
				index++;
			}
		}
		return emptyPlayground;
	}
	
	public int[] sortPlaygroundForAttack(AInode node, int[] playground, int id) {
		int targs = 0;
		for (int k = 0; k < playground.length; k++) {
			if (node.getLocation(playground[k]).isOccupied()) targs++;
		}
		if (targs == 0) return new int[0];
		int[] battleground = new int[targs];
		int index = 0;
		for (int m = 0; m < playground.length; m++) {
			if (node.getLocation(playground[m]).isOccupied()) {
				battleground[index] = playground[m];
				index++;
			}
		}
		int[] realBattleground = this.getAllLegitAttackTargets(node, battleground, id);
		return realBattleground;
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
	
	public static boolean willIGetMyselfKilledHere(AInode node, int loc) {
		for (int j = 0; j < 16; j++) {
			Brawler killer = node.getBrawler(j);
			boolean enemy = ((node.isRedsTurn() && !killer.isRed()) || (!node.isRedsTurn() && !killer.isBlue()));
			if (enemy && !killer.isUsedLast() && (killer instanceof Warrior || killer.getLevel() > 1)) {
				int[] playground = node.buildPlayground(j);
				for (int k = 0; k < playground.length; k++) if (playground[k] == loc) return true;
			}
		}
		return false;
	}
} // end of AIsensors class
