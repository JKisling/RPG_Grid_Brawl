package rpgGridBrawl;

// I plan to pretty much rewrite this entire class and the Interaction methods associated with it.

public class Spend extends GridBrawl {
	Brawler faust, mephisto;
	
	public Spend() {}
	
	public GameData execute(GameData crt) {
		Sword sword = new Sword();
		Shield shield = new Shield();
		Ring ring = new Ring();
		mephisto = new Diamond();
		for (int a = 0; a < crt.getAllBrawlers().length; a++) {
			Brawler loot = crt.getBrawler(a);
			if (loot instanceof Diamond) mephisto = crt.getBrawler(a);
			if (mephisto.getPieceID() == loot.getPossession()) faust = crt.getBrawler(a);
			if (loot instanceof Sword) sword = (Sword) crt.getBrawler(a);
			if (loot instanceof Shield) shield = (Shield) crt.getBrawler(a);
			if (loot instanceof Ring) ring = (Ring) crt.getBrawler(a);	
		}
		boolean bonusFunction = (faust instanceof Rogue);
		int[] bribeList = buildTargetList(crt, faust);
		boolean options1[] = new boolean[3];
		boolean confirm = false;
		
		if (bribeList.length >= 1) { // There is at least one brawler available to bribe
			options1[0] = true;
			for (int m = 0; m < bribeList.length; m++) if (crt.getBrawler(bribeList[m]) instanceof Merchant) options1[1] = true;
		}
		options1[2] = bonusFunction;
		
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
			Brawler bribed = crt.getBrawler(bribeList[opt2]);
			interact.tellPlayer(interact.msgBuild(crt, 32, bribed.getPieceID()), false); // "You have selected to bribe the " + BRWLRS[bribed] + " into moving to a new space."
			interact.tellPlayer(8,  1, true); // "Enter the 3-digit number of any unoccupied space: "
			int[] nwSpc = interact.getUnoccupiedSpaceNumber(crt);
			if (nwSpc[0] == -1) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			int newSpace = nwSpc[0] * 100 + nwSpc[1] * 10 + nwSpc[2];
			String result = "You have selected to use your" + BRWLRS[faust.getPieceID()] + " to bribe the " + BRWLRS[bribed.getPieceID()]+ " to space #" + newSpace + ".";
			interact.tellPlayer(result, false);
			confirm = interact.confirm();
			if (!confirm) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			interact.space();
			interact.tellPlayer(8,  2, false); // "The Diamond has been spent, your bribe was successful."
			interact.space();
			dispossess(crt, mephisto.getPieceID());
			crt.getBrawler(bribed.getPieceID()).setFloor(nwSpc[0]);
			crt.getBrawler(bribed.getPieceID()).setColumn(nwSpc[1]);
			crt.getBrawler(bribed.getPieceID()).setRow(nwSpc[2]);
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
			for (int bb = 0; bb < crt.getAllBrawlers().length; bb++) if (merchandise.getPieceID() == crt.getBrawler(bb).getPossession()) dispossess(crt, merchandise.getPieceID());
			crt.getBrawler(faust.getPieceID()).setPossession(merchandise.getPieceID());
			crt.getBrawler(faust.getPieceID()).setHandsFull(true);
			crt.getBrawler(merchandise.getPieceID()).setFloor(faust.getFloor());
			crt.getBrawler(merchandise.getPieceID()).setColumn(faust.getColumn());
			crt.getBrawler(merchandise.getPieceID()).setRow(faust.getRow());
			break;
		case 3: // invest
			interact.tellPlayer(8, 4, false); // "Your Rogue may 'invest' the Diamond, and in doing so will take possession of any one Treasure on its board level."
			boolean[] opts3 = {(sword.getFloor() == faust.getFloor()), (shield.getFloor() == faust.getFloor()), (ring.getFloor() == faust.getFloor())};
			if (!opts3[0] && !opts3[1] && !opts3[2]) {
				interact.tellPlayer(interact.msgBuild(crt, 34, faust.getPieceID()), false);
				interact.space();
				crt.setSuccessfulTurn(false);
				return crt;
			}
			interact.tellPlayer(interact.reportOnSpendOptions3(opts3), true);
			int yieldSelect = interact.getSpendSelection4(opts3);
			if (yieldSelect == -1) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			Brawler yield = new Diamond();
			if (yieldSelect == 1) yield = sword;
			if (yieldSelect == 2) yield = shield;
			if (yieldSelect == 3) yield = ring;
			result = "You have selected to invest the Diamond, which will yield the " + BRWLRS[yield.getPieceID()] + " to the " + BRWLRS[faust.getPieceID()];
			interact.tellPlayer(result, false);
			confirm = interact.confirm();
			if (!confirm) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			dispossess(crt, yield.getPieceID());
			crt.getBrawler(faust.getPieceID()).setPossession(yield.getPieceID());
			crt.getBrawler(faust.getPieceID()).setHandsFull(true);
			crt.getBrawler(yield.getPieceID()).setFloor(faust.getFloor());
			crt.getBrawler(yield.getPieceID()).setColumn(faust.getColumn());
			crt.getBrawler(yield.getPieceID()).setRow(faust.getRow());
			interact.space();
			interact.tellPlayer(8, 5, false); // "The Diamond has been spent, your investment was fruitful.";
			interact.space();
		}
		crt.setSuccessfulTurn(true);
		if (crt.isRedsTurn()) crt.setRedCanPlace(true);
		else crt.setBlueCanPlace(true);
		crt = updateLastUsed(crt, faust.getPieceID());
		crt.getBrawler(mephisto.getPieceID()).remove();
		return crt;
	} // end of execute()
	
	// whoever has the treasure in question gives it up.
	private static void dispossess(GameData crt, int loot) {
		int formerOwner = -1;
		for (int d = 0; d < crt.getAllBrawlers().length; d++) if (loot == crt.getBrawler(d).getPossession()) formerOwner = d;	
		if (formerOwner != -1) {
			crt.getBrawler(formerOwner).setHandsFull(false);
				crt.getBrawler(formerOwner).setPossession(-1);
		}	
	}	
	
} // end of Spend class
