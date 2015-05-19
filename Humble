package rpgGridBrawl;

class Humble extends GridBrawl {
	Brawler warrior, cleric, mage, rogue;
	
	public Humble() {}
	
	public GameData execute(GameData crtGame) {
		warrior = new Warrior(0);
		cleric = new Cleric(1);
		mage = new Mage(2);
		rogue = new Rogue(3);
		int hSelect = 0;
		for (int j = 0; j < crtGame.getAllBrawlers().length; j++) {
			Brawler jDog = crtGame.getBrawler(j);
			boolean rightColor = (jDog.isRed() && crtGame.isRedsTurn()) || (jDog.isBlue() && !crtGame.isRedsTurn());
			if (rightColor && jDog instanceof Warrior) warrior = crtGame.getBrawler(j);
			if (rightColor && jDog instanceof Cleric) cleric = crtGame.getBrawler(j);
			if (rightColor && jDog instanceof Mage) mage = crtGame.getBrawler(j);
			if (rightColor && jDog instanceof Rogue) rogue = crtGame.getBrawler(j);
		}
		interact.tellPlayer(5, 0, false); // "You may humble your Cleric before one of your other characters."
		interact.tellPlayer(5, 1, false); // "Please select from among the following options:"
		interact.space();
		String clericResult = "reduced to CL " + (cleric.getLevel() - 1);
		if (cleric.getLevel() == 1) clericResult = "removed";
		boolean[] choices = new boolean[3];
		if (warrior.isOnBoard() && warrior.getLevel() < 4) choices[0] = true;
		if (mage.isOnBoard() && mage.getLevel() < 4) choices[1] = true;
		if (rogue.isOnBoard() && rogue.getLevel() < 4) choices[2] = true;
		String report = "";
		if (choices[0]) report += ("1. Your Cleric will be " + clericResult + " and your Warrior will be raised to CL " + (warrior.getLevel() + 1) + ".\n");
		if (choices[1]) report += ("2. Your Cleric will be " + clericResult + " and your Mage will be raised to CL " + (mage.getLevel() + 1) + ".\n");
		if (choices[2]) report += ("3. Your Cleric will be " + clericResult + " and your Rogue will be raised to CL " + (rogue.getLevel() + 1) + ".\n");
		report += ("4. Return to main menu\n");
		report +=("Enter your selection: ");
		interact.space();
		interact.tellPlayer(report, true);
		hSelect = interact.getHumbleSelection();
		Brawler recipientB = new Diamond(); // diamond is arbitrary
		
		switch(hSelect) {
		case 1: interact.tellPlayer(5, 2, false); // "You have chosen to humble your Cleric before your Warrior."
			recipientB = warrior;
			break;
		case 2: interact.tellPlayer(5, 3, false); // "You have chosen to humble your Cleric before your Mage."
			recipientB = mage;
			break;
		case 3: interact.tellPlayer(5, 4, false); // "You have chosen to humble your Cleric before your Rogue."
			recipientB = rogue;
			break;
		default: crtGame.setSuccessfulTurn(false);
			return crtGame;
		}
		boolean confirm = interact.confirm();
		if (!confirm) {
			crtGame.setSuccessfulTurn(false);
			return crtGame;
		}
		// at this point, the humbling has been selected and successfully confirmed
		int cler = cleric.getPieceID();
		int recipient = recipientB.getPieceID();
		
		if (crtGame.getBrawler(cler).getLevel() == 1) crtGame = clearSpace(crtGame, cler);
		else crtGame.getBrawler(cler).setLevel(crtGame.getBrawler(cler).getLevel() - 1);
		crtGame.getBrawler(recipient).setLevel(crtGame.getBrawler(cler).getLevel() + 1);
		if (crtGame.isRedsTurn()) crtGame.setRedCanPlace(true);
		else crtGame.setBlueCanPlace(true);
		crtGame = updateLastUsed(crtGame, cler);
		interact.space();
		interact.tellPlayer(5, 5, false); // "Humbling was successful."
		interact.space();
		crtGame.setSuccessfulTurn(true);
		return crtGame;
	} // end of execute()
} // end of Humble class
