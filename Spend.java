package rpgGridBrawl;

// I plan to pretty much rewrite this entire class and the Interaction methods associated with it.

public class Spend extends GridBrawl {
	Brawler faust, mephisto;
	
	public Spend() {}
	
	// relies on standard set of Brawlers
	public GameData execute(GameData crt) {
		Brawler sword = crt.getBrawler(20);
		Brawler shield = crt.getBrawler(21);
		Brawler ring = crt.getBrawler(22);
		mephisto = crt.getBrawler(23);
		for (int f = 0; f < 8; f++) if (crt.getBrawler(f).getPossession() == 23) faust = crt.getBrawler(f);
		int[] bribeList = buildTargetList(crt, faust);
		boolean options1[] = new boolean[2]; // options1[0] is for bribe, options1[1] is for purchase
		boolean confirm = false;
		
		if ((faust instanceof Rogue) && bribeList.length >= 1) options1[0] = true; // There is at least one brawler available to bribe
		if (faust.getFloor() == crt.getBrawler(19).getFloor()) options1[1] = true; // faust shares floor with merchant	
		
		interact.tellPlayer(5, 1, false); // "Please select from among the following options:"
		interact.tellPlayer(interact.reportOnSpendOptions1(options1), true);
		int opt1 = interact.getSpendSelection1(options1);
		if (opt1 == -1) {
			crt.setSuccessfulTurn(false);
			return crt;
		}
		interact.space();
		switch(opt1) {
		case 1: // bribe
			interact.tellPlayer(8, 0, false); // "These brawlers are open to being bribed this turn:"
			interact.tellPlayer(interact.reportOnSpendOptions2(bribeList, crt), true);
			int opt2 = interact.getSpendSelection2(bribeList);
			if (opt2 == -1) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			int bribed = bribeList[opt2];
			interact.tellPlayer(interact.msgBuild(crt, 32, bribed), false); // "You have selected to bribe the " + BRWLRS[bribed] + " into moving to a new space."
			interact.tellPlayer(8,  1, true); // "Enter the 3-digit number of any unoccupied space: "
			int[] nwSpc = interact.getUnoccupiedSpaceNumber(crt);
			if (nwSpc[0] == -1) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			int newSpace = nwSpc[0] * 100 + nwSpc[1] * 10 + nwSpc[2];
			String result = "You have selected to use your" + BRWLRS[faust.getPieceID()] + " to bribe the " + BRWLRS[bribed]+ " to space #" + newSpace + ".";
			interact.tellPlayer(result, false);
			confirm = interact.confirm();
			if (!confirm) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			Brawler brbd = crt.getBrawler(bribed);
			crt.getLocation(brbd.getFloor(), brbd.getColumn(), brbd.getRow()).setOccupied(false);
			crt.getLocation(brbd.getFloor(), brbd.getColumn(), brbd.getRow()).setOccupiedBy(-1);
			dispossess(crt, mephisto.getPieceID());
			crt.getBrawler(bribed).setFloor(nwSpc[0]);
			crt.getBrawler(bribed).setColumn(nwSpc[1]);
			crt.getBrawler(bribed).setRow(nwSpc[2]);
			crt.getLocation(newSpace).setOccupied(true);
			crt.getLocation(newSpace).setOccupiedBy(bribed);
			crt.updateGameRecord("Bribe", BRWLRS[faust.getPieceID()], newSpace, BRWLRS[bribed]);
			
			interact.space();
			interact.tellPlayer(8,  2, false); // "The Diamond has been spent, your bribe was successful."
			interact.space();
			break;
		case 2: // purchase
			Brawler merchandise = new Sword();
			interact.tellPlayer(interact.msgBuild(crt, 33, faust.getPieceID()), false); // "The " + BRWLRS[faust] + " may purchase a Treasure from the Merchant."
			interact.tellPlayer(8, 3, true); // "1. Sword\n2. Shield\n3. Ring\nEnter the number of the Treasure you wish to purchase: "
			int purchase = interact.getSpendSelection3();
			if (purchase == 1) merchandise = sword;
			else if (purchase == 2) merchandise = shield;
			else merchandise = ring;
			String transaction = "You have selected to purchase the " + BRWLRS[merchandise.getPieceID()] + " for the " + BRWLRS[faust.getPieceID()] + ".";
			interact.tellPlayer(transaction, false);
			confirm = interact.confirm();
			if (!confirm) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			crt = dispossess(crt, merchandise.getPieceID());
			crt.takePossession(faust.getPieceID(), merchandise.getPieceID());
			crt.updateGameRecord("Purchase", BRWLRS[faust.getPieceID()], 0, BRWLRS[merchandise.getPieceID()]);
			break;
		}
		crt.setSuccessfulTurn(true);
		if (crt.isRedsTurn()) crt.setRedCanPlace(true);
		else crt.setBlueCanPlace(true);
		crt = updateLastUsed(crt, faust.getPieceID());
		crt.getBrawler(mephisto.getPieceID()).remove();
		return crt;
	} // end of execute()
	
	// whoever has the treasure in question gives it up.
	private static GameData dispossess(GameData crt, int loot) {
		int formerOwner = -1;
		for (int d = 0; d < crt.getAllBrawlers().length; d++) if (loot == crt.getBrawler(d).getPossession()) formerOwner = d;	
		if (formerOwner != -1) { // loot is currently owned
			crt.getBrawler(formerOwner).setHandsFull(false);
				crt.getBrawler(formerOwner).setPossession(-1);
		}
		else { // loot is loose on the board
			crt.getLocation(crt.getBrawler(loot).getLoc()).setOccupied(false);
			crt.getLocation(crt.getBrawler(loot).getLoc()).setOccupiedBy(-1);
		}
		crt.getBrawler(loot).remove();
		return crt;
	}	
	
} // end of Spend class
