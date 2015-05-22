package rpgGridBrawl;

// made Brawler update of 4/14/15

public class Fireball extends GridBrawl {
	Brawler tiamat, presto, shooter;
	GameData game;
	
	public Fireball() {}
	
	// type 1 = Dragonfire	type 2 = Ring Attack
	public GameData burn(GameData crt, int type) {
		game = crt;
		game.setSuccessfulTurn(false);
		int selection = -1;
		int[] tells = new int[3];
		int bLength = game.getAllBrawlers().length;
		switch(type) {
		case 1: // Dragonfire
			for (int a = 0; a < bLength; a++) if (game.getBrawler(a) instanceof Dragon) tiamat = game.getBrawler(a);
			shooter = tiamat;
			tells[0] = 0;	tells[1] = 26;	tells[2] = 28;
			break;
		case 2: // Ring Attack
			if (crt.isRedsTurn()){
				for (int a = 0; a < bLength; a++){
					if (game.getBrawler(a) instanceof Mage && game.getBrawler(a).isRed()) presto = (Mage) game.getBrawler(a);
					tells[0] = 1;	tells[1] = 29;	tells[2] = 30;
				}
			}
			else {
				for (int a = 0; a < bLength; a++){
					if (game.getBrawler(a) instanceof Mage && game.getBrawler(a).isBlue()) presto = game.getBrawler(a);
					tells[0] = 2;	tells[1] = 29; tells[2] = 31;
				}
			}
			shooter = presto;
			break;
		}
		int[] targetList = this.buildTargetList(game, shooter);
		if (targetList[0] == -1) {
			interact.tellPlayer(7, tells[0], false); 
			// "There are no available targets for the (Dragon's fiery breath)/((Red/Blue) Mage's fireball spell)."
			return game;
		}
		interact.tellPlayer(5, 1, false); // "Please select from among the following options:"
		interact.tellPlayer(interact.reportOnFireballOptions(crt, targetList, shooter.getPieceID()), true);
		selection = interact.getDragonFireSelection(targetList);
		if (selection == -1) return game;
		interact.tellPlayer(interact.msgBuild(game, tells[1], selection), false); 
			// "You have selected to have (the Dragon breathe fire on)/(your Mage hurl a fireball at) the " + BRWLRS[selection]
		boolean confirm = interact.confirm();
		if (!confirm) return game;
		Brawler victim = game.getBrawler(selection);
		int oldB = victim.getFloor();
		int oldC = victim.getColumn();
		int oldR = victim.getRow();
		if (!(victim instanceof Character || victim instanceof Monster) || victim.getLevel() == 1 || victim.getFloor() == 1) {
			game.getBrawler(selection).remove();
			game.getLocation(oldB, oldC, oldR).removeBrawler();
			interact.space();
			interact.tellPlayer(interact.msgBuild(game, 27, selection), false); // "The charred husk of the " + BRWLRS[selection] + " has been tossed off of the board!"
		}
		else {
			int tSpace = (oldB * 100) + (oldC * 10) + oldR;
			int bumps = countChainBumpVictims(game, tSpace);
			interact.space();
			interact.tellPlayer(interact.msgBuild(game, tells[2], selection), false); 
				// "The Dragon burns the " + BRWLRS[selection] + "with its fiery breath!"/"The (Red/Blue) Mage casts a fire spell, burning the " + BRWLRS[passIn] + "!";
			game = performBumpDowns(game, bumps, tSpace);
			game.getLocation(oldB, oldC, oldR).removeBrawler();
		}
		interact.space();
		if (game.isRedsTurn()) game.setRedCanPlace(true);
		else game.setBlueCanPlace(true);
		game = updateLastUsed(game, shooter.getPieceID());
		game.setSuccessfulTurn(true);
		return game;
	} // end of burn()
	
} // end of Fireball class
