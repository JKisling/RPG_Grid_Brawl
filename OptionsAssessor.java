package rpgGridBrawl;

public class OptionsAssessor extends GridBrawl {
	GameData game;
	
	public OptionsAssessor(){}
	
	public boolean[] assessPlayerOptions(GameData GM) {
		// this array is larger than necessary to accommodate test methods
		game = GM;
		boolean[] optionArray = new boolean[20];
		optionArray[0] = true;
		optionArray[12] = true; // draw board
		optionArray[13] = true; // game report
		for(int j = 1; j <= 11; j++) optionArray[j] = false;
		// players start by placing pieces on board--this is only option
		if (game.getGameRound() <= 10) {
			optionArray[1] = true;
			return optionArray;
		}
		optionArray[1] = this.isPlacePossible(game);
		optionArray[2] = this.isMovePossible(game);
		optionArray[3] = this.isSacrificePossible(game);
		optionArray[4] = this.isGlorificationPossible(game);
		optionArray[5] = this.isTransferPossible(game);
		optionArray[6] = this.isSpendDiamondPossible(game);
		optionArray[7] = this.isHumblePossible(game);
		optionArray[8] = this.isRingAttackPossible(game);
		optionArray[9] = this.isPickpocketPossible(game);
		optionArray[10] = this.isDragonFirePossible(game);
		optionArray[11] = this.isFleePossible(game);
		return optionArray;
	}

	private boolean isDragonFirePossible(GameData GM) {
		boolean isDF = false;
		Brawler bahamut = new Dragon();
		for (int i = 0; i < GM.getAllBrawlers().length; i++) if (GM.getBrawler(i) instanceof Dragon) bahamut = GM.getBrawler(i);
		if (!bahamut.isOnBoard() || bahamut.isUsedLast()) return false;
		for (int j = 0; j < GM.getAllBrawlers().length; j++) if (los.calculate(GM, bahamut, GM.getBrawler(j))) isDF = true;
		return isDF;
	}
	
	// int size assumes standard 5x5 bottom floor
	private boolean isFleePossible(GameData GM) {
		int size = 5;
		boolean colorMatch = false;
		boolean isF = false;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (GM.getLocation(1, i, j).isOccupied()) {
					Brawler jimbo = GM.getBrawler(GM.getLocation(1, i, j).getOccupiedBy());
					colorMatch = ((GM.isRedsTurn() && jimbo.isRed()) || (!GM.isRedsTurn() && jimbo.isBlue()));
					if (!colorMatch && jimbo instanceof Character ) isF = false;
					else return true;
				}
			}
		}
		return isF;
	}

	private boolean isGlorificationPossible(GameData GM) {
		if (GM.isRedsTurn()) {
			for (int r = 0; r < GM.getAllBrawlers().length; r++) {
				Brawler reds = GM.getBrawler(r);
				if (reds.isRed() && reds.getFloor() == 5 && !reds.isUsedLast() && reds.getLevel() == 4 && !reds.isGlorified()) return true;
			}
		}
		else {
			for (int b = 0; b < GM.getAllBrawlers().length; b++) {
				Brawler blues = GM.getBrawler(b);
				if (blues.isBlue() && blues.getFloor() == 5 && !blues.isUsedLast() && blues.getLevel() == 4 && !blues.isGlorified()) return true;
			}
		}
		return false;
	} 
	
	// assumes standard set of Brawlers
	private boolean isHumblePossible(GameData GM) { 
		Brawler jimbo = new Diamond(); // diamond is arbitrary
		boolean colorMatch = false;
		for (int c = 0; c < GM.getAllBrawlers().length; c++) {
			jimbo = GM.getBrawler(c);
			colorMatch = ((GM.isRedsTurn() && jimbo.isRed()) || (!GM.isRedsTurn() && jimbo.isBlue()));
			if (jimbo instanceof Cleric && colorMatch) break;	
		}
		if (GM.isRedsTurn() && jimbo.isOnBoard() && !jimbo.isUsedLast() && !jimbo.isGlorified()) { // cleric on board, not used last, not glorified
			if (GM.getBrawler(0).isOnBoard() && GM.getBrawler(0).getLevel() < 4) return true; // red warrior
			if (GM.getBrawler(2).isOnBoard() && GM.getBrawler(2).getLevel() < 4) return true; // red mage
			if (GM.getBrawler(3).isOnBoard() && GM.getBrawler(3).getLevel() < 4) return true; // red rogue
		}
		if (!GM.isRedsTurn() && jimbo.isOnBoard() && !jimbo.isUsedLast() && !jimbo.isGlorified()) { // cleric on board, not used last, not glorified
			if (GM.getBrawler(4).isOnBoard() && GM.getBrawler(4).getLevel() < 4) return true; // blue warrior
			if (GM.getBrawler(6).isOnBoard() && GM.getBrawler(6).getLevel() < 4) return true; // blue mage
			if (GM.getBrawler(7).isOnBoard() && GM.getBrawler(7).getLevel() < 4) return true; // blue rogue
		}
		return false;
	}
	
	// it is highly unlikely that this method would return false post game turn 10.
	private boolean isMovePossible(GameData GM) {
		if (GM.getGameRound() < 10) return false;
		int countRed = 0;	int countBlue = 0;
		for (int m = 0; m < GM.getAllBrawlers().length; m++) {
			Brawler mmm = GM.getBrawler(m);
			if (!(mmm instanceof Treasure) && mmm.isOnBoard() && !mmm.isUsedLast()) {
				if (!mmm.isRed()) countBlue++;
				if (!mmm.isBlue()) countRed++;
				if (countRed >= 1 && countBlue >= 1) return true;
			}
		}
		return false;
	}

	private boolean isPickpocketPossible(GameData GM) {
		LineOfSight los = new LineOfSight();
		Rogue oneIwilly = new Rogue(3);
		boolean redWilly = false;
		boolean redVic = false;
		// is current player's rogue on the board and powered up and hands-free
		if (GM.isRedsTurn()) {
			for (int i = 0; i < GM.getAllBrawlers().length; i++) if (GM.getBrawler(i) instanceof Rogue && GM.getBrawler(i).isRed()) oneIwilly = (Rogue) GM.getBrawler(i);
			redWilly = true;
		}
		else for (int i = 0; i < GM.getAllBrawlers().length; i++) if (GM.getBrawler(i) instanceof Rogue && GM.getBrawler(i).isBlue()) oneIwilly = (Rogue) GM.getBrawler(i);
		if (!oneIwilly.isOnBoard() || !oneIwilly.isPoweredUp() || oneIwilly.isHandsFull()) return false;
		
		// is that rogue line-of-sight to an enemy character or monster who possesses treasure
		for (int j = 0; j < GM.getAllBrawlers().length; j++) {
			Brawler victim = GM.getBrawler(j);
			redVic = victim.isRed();
			if (!victim.isOnBoard() || !victim.isHandsFull() || (redWilly && redVic) || (!redWilly && !redVic)) continue;
			if (los.calculate(GM, oneIwilly, victim)) return true;	
		}
		return false;
	}

	private boolean isPlacePossible(GameData GM) {
		boolean isP = false;
		if ((GM.isRedsTurn() && GM.canRedPlace()) || (!GM.isRedsTurn() && GM.canBluePlace())) isP = true;
		int k = 0;
		for (int j = 0; j < GM.getAllBrawlers().length; j++) if (GM.getBrawler(j).isOnBoard()) k++;
		if (k == GM.getAllBrawlers().length) isP = false; // if all the brawlers are on the board, place will not be an option
		if (k < 7) isP = true;  // fewer than 7 brawlers on board means you can always place as an action.
		return isP;
	}

	private boolean isRingAttackPossible(GameData GM) {
		int mColor = 6;
		int pos = 0;
		if (GM.isRedsTurn()) mColor = 2; 
		Brawler raistlin = new Mage(mColor);
		if (GM.isRedsTurn()){
			for (int i = 0; i < GM.getAllBrawlers().length; i++) if (GM.getBrawler(i) instanceof Mage && GM.getBrawler(i).isRed()) raistlin = GM.getBrawler(i);
		}
		else {
			for (int i = 0; i < GM.getAllBrawlers().length; i++) if (GM.getBrawler(i) instanceof Mage && GM.getBrawler(i).isBlue()) raistlin = GM.getBrawler(i);
			
		}
		if (!raistlin.isOnBoard() || !raistlin.isHandsFull()) return false;
		else pos = raistlin.getPossession();
		
		if (raistlin.isUsedLast() || !(GM.getBrawler(pos) instanceof Ring)) return false;
		BoardSpace examined = GM.getLocation(raistlin.getFloor(), raistlin.getColumn(), raistlin.getRow());
		if (examined.isGhostEffect()) return false; // if the mage is stuck next to a Ghost it can't do anything
		for (int j = 0; j < GM.getAllBrawlers().length; j++) if (los.calculate(GM, raistlin, GM.getBrawler(j))) return true;
		return false;
	}
	
	// I think that this is rigorous enough, but might need more testing.
	private boolean isSacrificePossible(GameData GM) {
		if (!GM.getLocation(5, 0, 0).isOccupied()) return false; // ascension space is not even occupied
		int sackerID = GM.getLocation(5, 0, 0).getOccupiedBy();
		Brawler sacrificer = GM.getBrawler(sackerID);
		if (!sacrificer.isHandsFull() || (GM.isRedsTurn() && sacrificer.isBlue()) || (!GM.isRedsTurn() && sacrificer.isRed())) return false;
		else return true;
	}
	
	// assumes standard set of Brawlers
	private boolean isSpendDiamondPossible(GameData GM) {
		LineOfSight los = new LineOfSight();
		Brawler[] myChars = new Brawler[4];
		if (GM.isRedsTurn()) {
			for (int r = 0; r < 4; r++) myChars[r] = GM.getBrawler(r);
		}
		else {
			for (int b = 0; b < 4; b++) myChars[b] = GM.getBrawler(b + 4);
		}
		Brawler merchant = GM.getBrawler(19);
		Brawler sentinel = GM.getBrawler(11);
		for (int rb = 0; rb < 4; rb++) {
			if (myChars[rb].getPossession() == 23) {
				if (merchant.getFloor() == myChars[rb].getFloor()) return true;
				if (myChars[rb] instanceof Rogue && sentinel.getFloor() != myChars[rb].getFloor()) {
					for (int j = 0; j < GM.getAllBrawlers().length; j++) if (los.calculate(GM, myChars[rb], GM.getBrawler(j))) return true;
				}
			}
		}
		return false;
	}
	
	// check if current player's cleric is on the board and not used last, then check if at least one other CL<4 character is on the board
	private boolean isTransferPossible(GameData GM) {
		boolean[] cases = new boolean[6]; // 6 scenarios: WC, WM, WR, CM, CR, MR
		los = new LineOfSight();
		Brawler walt = new Warrior(0);
		Brawler clara = new Cleric(1);
		Brawler meg = new Mage(2);
		Brawler roy = new Rogue(3);
		if (GM.isRedsTurn()){
			for (int t = 0; t < GM.getAllBrawlers().length; t++) {
				Brawler ttt = GM.getBrawler(t);
				if (ttt.isRed() && ttt instanceof Warrior) walt = (Warrior) GM.getBrawler(t);
				if (ttt.isRed() && ttt instanceof Cleric) clara = (Cleric) GM.getBrawler(t);
				if (ttt.isRed() && ttt instanceof Mage) meg = (Mage) GM.getBrawler(t);
				if (ttt.isRed() && ttt instanceof Rogue) roy = (Rogue) GM.getBrawler(t);
			}
		}
		else {
			for (int u = 0; u < GM.getAllBrawlers().length; u++) {
				Brawler uuu = GM.getBrawler(u);
				if (uuu.isBlue() && uuu instanceof Warrior) walt = (Warrior) GM.getBrawler(u);
				if (uuu.isBlue() && uuu instanceof Cleric) clara = (Cleric) GM.getBrawler(u);
				if (uuu.isBlue() && uuu instanceof Mage) meg = (Mage) GM.getBrawler(u);
				if (uuu.isBlue() && uuu instanceof Rogue) roy = (Rogue) GM.getBrawler(u);
			}
		}
		boolean wf = walt.isHandsFull();
		boolean cf = clara.isHandsFull();
		boolean mf = meg.isHandsFull();
		boolean rf = roy.isHandsFull();
		boolean wlu = walt.isUsedLast();
		boolean clu = clara.isUsedLast();
		boolean mlu = meg.isUsedLast();
		boolean rlu = roy.isUsedLast();
		
		// this is different than in Transfer class, because here I just need to find out if two characters are line of sight,
		// and then if either one (but not both) possess a treasure.  If this is true for any one of these, then it will return true.
		try {
			cases[0] = (los.calculate(GM, walt, clara) && ((wf && !cf && !wlu) || (!wf && cf && !clu)));
			cases[1] = (los.calculate(GM, walt, meg)   && ((wf && !mf && !wlu) || (!wf && mf && !mlu)));
			cases[2] = (los.calculate(GM, walt, roy)   && ((wf && !rf && !wlu) || (!wf && rf && !rlu)));
			cases[3] = (los.calculate(GM, clara, meg)  && ((cf && !mf && !clu) || (!cf && mf && !mlu)));
			cases[4] = (los.calculate(GM, clara, roy)  && ((cf && !rf && !clu) || (!cf && rf && !rlu)));
			cases[5] = (los.calculate(GM, meg, roy)    && ((mf && !rf && !mlu) || (!mf && rf && !rlu)));
		}
		catch (NullPointerException ex) {
			System.out.println("isTransferPossible() threw a NPE");
			return false;
		}
		for (int p = 0; p <= 5; p++) if (cases[p]) return true;
		return false;
	}
	
} // end of OptionsAssessor class
