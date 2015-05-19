package rpgGridBrawl;

public class Pickpocket extends GridBrawl {
	Brawler roger, victim;
	int loot;
	
	public GameData execute(GameData crt) {
		boolean redVic = false;
		boolean redRoger = false;
		int[] targets = new int[4];
		for (int q = 0; q < 4; q++) targets[q] = -1;
		int tc = 0;
		int bLength = crt.getAllBrawlers().length;
		if (crt.isRedsTurn()) {
			for (int r = 0; r < bLength; r++) if (crt.getBrawler(r) instanceof Rogue && crt.getBrawler(r).isRed()) roger = crt.getBrawler(r);
		}
		else {
			for (int r = 0; r < bLength; r++) if (crt.getBrawler(r) instanceof Rogue && crt.getBrawler(r).isBlue()) roger = crt.getBrawler(r);
		}
		victim = new Diamond(); // Diamond is arbitrary, this is just for initialization
		redRoger = roger.isRed();
		for (int j = 0; j < bLength; j++) {
			victim = crt.getBrawler(j);
			redVic = victim.isRed();
			if (!victim.isOnBoard() || !victim.isHandsFull() || (redRoger && redVic) || (!redRoger && !redVic)) continue;
			if (los.calculate(crt, roger, victim)) targets[tc++] = j;
		}
		interact.tellPlayer(5, 1, false); // "Please select from among the following options:"
		interact.tellPlayer(interact.reportOnPickpocketOptions(crt, targets), true);
		int selection = interact.getPickpocketSelection(targets);
		if (selection == -1) {
			crt.setSuccessfulTurn(false);
			return crt;
		}
		victim = crt.getBrawler(targets[selection - 1]);
		loot = victim.possession;
		String pp = "You have selected to have the " + BRWLRS[roger.getPieceID()] + " steal the " + BRWLRS[loot] + " from the " + BRWLRS[victim.getPieceID()] + ".";
		interact.tellPlayer(pp, false);
		boolean confirm = interact.confirm();
		if (!confirm) {
			crt.setSuccessfulTurn(false);
			return crt;
		}
		crt.getBrawler(victim.getPieceID()).setPossession(-1);
		crt.getBrawler(victim.getPieceID()).setHandsFull(false);
		crt.getBrawler(roger.getPieceID()).setPossession(loot);
		crt.getBrawler(roger.getPieceID()).setHandsFull(true);
		crt.getBrawler(loot).setFloor(roger.getFloor());
		crt.getBrawler(loot).setColumn(roger.getColumn());
		crt.getBrawler(loot).setRow(roger.getRow());
		if (crt.isRedsTurn()) crt.setRedCanPlace(true);
		else crt.setBlueCanPlace(true);
		interact.space();
		interact.tellPlayer(5, 6, false); // "Pickpocket was successful.  Yoink!"
		interact.space();
		crt.setSuccessfulTurn(true);
		return crt;
	} // end of execute()
	
} // end of Pickpocket class
