package rpgGridBrawl;

public class Flee extends GridBrawl {
	GameData game;
	
	public Flee() {}
	
	public GameData execute(GameData crt) {
		game = crt;
		interact.tellPlayer(7,  4, false); // "Choose a Brawler to be removed from the brawl:"
		boolean[] cowards = this.sortCowards(game);
		String options = interact.reportOnFleeOptions(cowards);
		interact.tellPlayer(options, true);
		int selection = interact.getFleeSelection(cowards);
		if (selection == -1) {
			game.setSuccessfulTurn(false);
			return game;
		}
		interact.tellPlayer(interact.msgBuild(game, 53, selection), false); // "You have selected to remove the " + BRWLRS[selection] + " from the board.";
		boolean confirm = interact.confirm();
		if (!confirm) {
			game.setSuccessfulTurn(false);
			return game;
		}
		game = clearSpace(game, selection);
		if (game.isRedsTurn()) game.setRedCanPlace(true);
		else game.setBlueCanPlace(true);
		interact.space();
		interact.tellPlayer(interact.msgBuild(game, 54, selection), false); // "The " + BRWLRS[selection] + " has fled the brawl!"
		interact.space();
		game = updateLastUsed(game, selection);
		game.setSuccessfulTurn(true);
		return game;
	}
	
	private boolean[] sortCowards(GameData game) {
		int bLength = game.getAllBrawlers().length;
		boolean[] cowards = new boolean[bLength];
		boolean colorMatch = false;
		for (int b = 0; b < bLength; b++) {
			Brawler jimbo = game.getBrawler(b);
			colorMatch = ((game.isRedsTurn() && jimbo.isRed()) || (!game.isRedsTurn() && jimbo.isBlue()));
			if ((jimbo instanceof Character) && !colorMatch) cowards[b] = false; // you can't make your opponent's Characters flee.
			else if (jimbo.isOnBoard() && (1 == jimbo.getFloor()) && !jimbo.isUsedLast()) cowards[b] = true;
		}
		return cowards;
	}
} // end of Flee class
