package rpgGridBrawl;

public class Transfer extends GridBrawl {
	Brawler pitcher, catcher, walt, clara, meg, roy;
	GameData game;

	public Transfer() {}
	
	public GameData execute(GameData crt) {
		game = crt;
		walt = new Warrior(0);
		clara = new Cleric(1);
		meg = new Mage(2);
		roy = new Rogue(3);
		boolean[] cases = new boolean[12]; // 12 scenarios: WC, WM, WR, CM, CR, MR & the opposites CW, MW, RW, MC, RC, RM
		int bLength = game.getAllBrawlers().length;
		if (game.isRedsTurn()){
			for (int t = 0; t < bLength; t++) {
				Brawler x = game.getBrawler(t);
				if (x.isRed() && x instanceof Warrior) walt = game.getBrawler(t);
				if (x.isRed() && x instanceof Cleric) clara = game.getBrawler(t);
				if (x.isRed() && x instanceof Mage) meg = game.getBrawler(t);
				if (x.isRed() && x instanceof Rogue) roy = game.getBrawler(t);
			}
		}
		else {
			for (int t = 0; t < bLength; t++) {
				Brawler y = game.getBrawler(t);
				if (y.isBlue() && y instanceof Warrior) walt = game.getBrawler(t);
				if (y.isBlue() && y instanceof Cleric) clara = game.getBrawler(t);
				if (y.isBlue() && y instanceof Mage) meg = game.getBrawler(t);
				if (y.isBlue() && y instanceof Rogue) roy = game.getBrawler(t);
			}
		}
		boolean wf = walt.isHandsFull();
		boolean cf = clara.isHandsFull();
		boolean mf = meg.isHandsFull();
		boolean rf = roy.isHandsFull();
		cases[0] =  (wf && !cf && los.calculate(game, walt, clara));
		cases[1] =  (wf && !mf && los.calculate(game, walt, meg));
		cases[2] =  (wf && !rf && los.calculate(game, walt, roy));
		cases[3] =  (cf && !mf && los.calculate(game, clara, meg));
		cases[4] =  (cf && !rf && los.calculate(game, clara, roy));
		cases[5] =  (mf && !rf && los.calculate(game, meg, roy));
		cases[6] =  (!wf && cf && los.calculate(game, walt, clara));
		cases[7] =  (!wf && mf && los.calculate(game, walt, meg));
		cases[8] =  (!wf && rf && los.calculate(game, walt, roy));
		cases[9] =  (!cf && mf && los.calculate(game, clara, meg));
		cases[10] = (!cf && rf && los.calculate(game, clara, roy));
		cases[11] = (!mf && rf && los.calculate(game, meg, roy));
		
		interact.tellPlayer(5, 1, false); // "Please select from among the following options:";
		String options = interact.reportOnTransferOptions(cases, walt, clara, meg, roy);
		interact.tellPlayer(options, true);
		int selection = interact.getTransferSelection(cases);
		if (selection == -1) {
			game.setSuccessfulTurn(false);
			return game;
		}
		boolean confirm = interact.confirm();
		if (!confirm) {
			game.setSuccessfulTurn(false);
			return game;
		}
		switch(selection) {
		case 0: pitcher = walt;		catcher = clara;		break;
		case 1: pitcher = walt;		catcher = meg;			break;
		case 2: pitcher = walt;		catcher = roy;			break;
		case 3: pitcher = clara;	catcher = meg;			break;
		case 4: pitcher = clara;	catcher = roy;			break;
		case 5: pitcher = meg;		catcher = roy;			break;
		case 6: pitcher = clara;	catcher = walt;			break;
		case 7: pitcher = meg;		catcher = walt;			break;
		case 8: pitcher = roy;		catcher = walt;			break;
		case 9: pitcher = meg;		catcher = clara;		break;
		case 10:pitcher = roy;		catcher = clara;		break;
		case 11:pitcher = roy;		catcher = meg;			break;
		}
		int ball = pitcher.getPossession();
		int ctchr = catcher.getPieceID();
		game.getBrawler(pitcher.getPieceID()).setPossession(-1);						// pitcher winds up
		game.getBrawler(pitcher.getPieceID()).setHandsFull(false);						// and the pitch...
		game.getBrawler(ball).setColumn(game.getBrawler(ctchr).getColumn());// the ball travels
		game.getBrawler(ball).setRow(game.getBrawler(ctchr).getRow());		// the ball travels
		game.getBrawler(ctchr).setHandsFull(true);							// the ball thumps into the mitt
		game.getBrawler(ctchr).setPossession(ball);							// steeerike!
		
		game.setSuccessfulTurn(true);
		game = updateLastUsed(game, pitcher.getPieceID());
		if (game.isRedsTurn()) game.setRedCanPlace(true);
		else game.setBlueCanPlace(true);
		
		interact.space();
		interact.tellPlayer(7, 3, false); // "Transfer of Treasure was successful."
		String result = "The " + BRWLRS[ctchr] + " has taken possession of the " + BRWLRS[ball] + ".";
		interact.tellPlayer(result, false);
		interact.space();
		
		return game;
	} // end of execute()	
} // end of Transfer class
