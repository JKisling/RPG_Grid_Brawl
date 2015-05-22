package rpgGridBrawl;

public class Glorification extends GridBrawl {
Brawler gloryHound;
	
	public Glorification() 	{}
	
	// prereq: current player has an eligible piece on the Ascension space.
	public GameData executeGlorification(GameData crt) {
		gloryHound = crt.getBrawler(crt.getLocation(500).getOccupiedBy());
		int id = gloryHound.getPieceID();
		interact.tellPlayer(interact.msgBuild(crt, 17, id), false); // "You are about to perform the ritual of Glorification on the " + BRWLRS[passIn] + "!"
		boolean confirm = interact.confirm();
		if (!confirm) return crt;
		if (gloryHound.isHandsFull()) {
			crt.dispossess(gloryHound.getPossession());
			interact.tellPlayer(interact.msgBuild(crt, 19, gloryHound.getPossession()), false); // "The " + BRWLRS[passIn] + " has returned to the pool."
		}
		crt = clearSpace(crt, id);
		crt.getBrawler(id).setGlorified(true);
		if (crt.isRedsTurn()) crt.setRedCanPlace(true);
		else crt.setBlueCanPlace(true);
		crt = updateLastUsed(crt, id);
		interact.tellPlayer(interact.msgBuild(crt, 18, id),  false); // "The " + BRWLRS[passIn] + " has left the board!"
		interact.tellPlayer(6, 0, false); // "This Character is now permanently CL4."
		crt.setSuccessfulTurn(true);
		crt.updateGameRecord("glorify", BRWLRS[id], 500, "none");
		interact.space();
		return crt;
	}
	
	// prereq: current player has a character on the ascension space that possesses treasure
	// this method assumes the standard set of brawlers
	public GameData executeSacrifice(GameData crt) {
		boolean confirm;
		int acolyte = crt.getLocation(5, 0, 0).getOccupiedBy();
		gloryHound = crt.getBrawler(acolyte);
		int lamb = gloryHound.getPossession();
		interact.tellPlayer(interact.msgBuild(crt, 20, lamb), false); // "You may sacrifice the " + BRWLRS[lamb]
		boolean[] choices1 = this.generateSacrificeOptions(crt, lamb);
		String choices2 = interact.reportOnSacrificeOptions(choices1);
		int lambtell = lamb - 19; // this is for the proper interact message
		interact.tellPlayer(6, lambtell, false); // one of the Glorification sacrifice messages
		interact.tellPlayer(choices2, true);
		int selection = interact.getSacrificeSelection(choices1);
		if (selection == -1) {
			crt.setSuccessfulTurn(false);
			return crt;
		}
		
		switch(lamb) {
		case 20: // sword
			interact.tellPlayer(interact.msgBuild(crt, 22, selection), false); // "You have selected to perform the ritual of sword sacrifice against the " + BRWLRS[selection]
			confirm = interact.confirm();
			if (!confirm) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			crt.getBrawler(selection).remove();
			crt.removeBrawler(selection);
			interact.tellPlayer(interact.msgBuild(crt, 21, selection), false); // "The " + BRWLRS[selection] + " has been bumped off of the board!"
			crt.dispossess(lamb);
			crt.setSuccessfulTurn(true);
			break;
		case 21: // shield
			interact.tellPlayer(interact.msgBuild(crt, 23, selection), false); // "You have selected to perform the ritual of shield sacrifice on the " + BRWLRS[selection]
			confirm = interact.confirm();
			if (!confirm) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			int newLevel = crt.getBrawler(selection).getLevel() + 1;
			crt.getBrawler(selection).setLevel(newLevel);
			String shieldReport = "The level of the " + BRWLRS[selection] + " is raised to " + crt.getBrawler(selection).getLevel() + ".";
			interact.tellPlayer(shieldReport, false);
			crt.dispossess(lamb);
			crt.setSuccessfulTurn(true);
			break;
		case 22: // ring
			interact.tellPlayer(interact.msgBuild(crt, 24, selection), false); // "You have selected to bestow the magic ring on the " + BRWLRS[selection]
			confirm = interact.confirm();
			if (!confirm) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			crt.dispossess(lamb);
			crt.takePossession(selection, lamb);
			interact.space();
			crt.setSuccessfulTurn(true);
			break;
		case 23: // diamond
			interact.tellPlayer(interact.msgBuild(crt, 25, selection), false); // "You have selected to perform the ritual of diamond sacrifice on the " + BRWLRS[selection]
			interact.tellPlayer(6, 5, true); // "Enter the 3-digit number of an unoccupied space to move that to: "
			int[] dstn = interact.getUnoccupiedSpaceNumber(crt);
			if (dstn[0] == -1) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			int destination = (dstn[0] * 100) + (dstn[1] * 10) + dstn[2];
			String report = "By sacrificing the diamond, you will move the " + BRWLRS[selection] + " to space " + destination + ".";
			interact.tellPlayer(report, false);
			confirm = interact.confirm();
			if (!confirm) {
				crt.setSuccessfulTurn(false);
				return crt;
			}
			crt.removeBrawler(selection);
			crt.getBrawler(selection).setFloor(dstn[0]);
			crt.getBrawler(selection).setColumn(dstn[1]);
			crt.getBrawler(selection).setRow(dstn[2]);
			crt.getLocation(destination).setOccupied(true);
			crt.getLocation(destination).setOccupiedBy(selection);
			crt.dispossess(lamb);
			crt.setSuccessfulTurn(true);
			break;
		}
		if (crt.isRedsTurn()) crt.setRedCanPlace(true);
		else crt.setBlueCanPlace(true);
		crt = updateLastUsed(crt, lamb);
		// produce separate game record updates for the four treasures
		// crt.updateGameRecord("sacrifice", BRWLRS[lamb], 500, "none");
		interact.space();
		interact.tellPlayer(6, 6, false); // "The ritual of sacrifice was successfully performed."
		interact.space();
		return crt;
	} // end of executeSacrifice()
	
	private boolean[] generateSacrificeOptions(GameData crt, int tzr) {
		int llength = crt.getAllBrawlers().length;
		boolean[] options = new boolean[llength];
		Brawler jimbo;
		switch(tzr) {
		case 20: // sword
			for (int i = 0; i < llength; i++) { // get all brawlers on board, except player's own characters
				jimbo = crt.getBrawler(i);
				boolean colorMatch = ((crt.isRedsTurn() && jimbo.isRed()) || (!crt.isRedsTurn() && jimbo.isBlue()));
				if (colorMatch || !jimbo.isOnBoard()) options[i] = false;
				else options[i] = true;
			}
		case 21: // shield
			for (int j = 0; j < llength; j++) { // get any of player's own characters or any monsters that are on board and not CL4
				jimbo = crt.getBrawler(j);
				boolean colorMatch = ((crt.isRedsTurn() && jimbo.isRed()) || (!crt.isRedsTurn() && jimbo.isBlue()));
				if ((colorMatch || jimbo instanceof Monster) && jimbo.isOnBoard() && jimbo.getLevel() < 4) options[j] = true;
				else options[j] = false;
			}
		case 22: // ring
			for (int k = 0; k < llength; k++) { // get any of player's own characters or any monsters that are on board and not handsfull
				jimbo = crt.getBrawler(k);
				boolean colorMatch = ((crt.isRedsTurn() && jimbo.isRed()) || (!crt.isRedsTurn() && jimbo.isBlue()));
				if (!jimbo.isHandsFull() && jimbo.isOnBoard()) {
					if ((jimbo instanceof Character && colorMatch) || jimbo instanceof Monster) options[k] = true;
				}
				else options[k] = false;
			}
		case 23: // diamond
			for (int m = 0; m < llength; m++) { // get any brawler that is on board
				jimbo = crt.getBrawler(m);
				if (jimbo.isOnBoard()) options[m] = true;
				else options[m] = false;
			}
		}
		return options;
	}
		
} // end of Glorification class
