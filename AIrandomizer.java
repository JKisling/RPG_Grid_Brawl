package rpgGridBrawl;
/**
 * The AIrandomizer is responsible for building an ArrayList of all of the possible actions a player could make on their turn,
 * and then returning an AIresult object based on one of those actions, selected at random.  Because there are usually more empty
 * spaces that a Brawler could be placed on than there are spaces an on-board Brawler could move to or attack, the Randomizer
 * will select a Place action more frequently on average than anything else.
 */
import java.util.ArrayList;
import java.util.Random;

public class AIrandomizer {
	private static AInode myNode;
	private ArrayList<AIresult> optionList;
	private boolean redsTurn;
	
	
	public AIrandomizer(String nodeString) {
		myNode = new AInode(nodeString);
		optionList = new ArrayList<AIresult>();
		redsTurn = myNode.isRedsTurn();
	}
	
	public AIresult randomAction() {
		AIresult rando = new AIresult();
		AIresult fallback = new AIresult();
		boolean[] onSwitches = setOnSwitches();
		AIsensors sensor = new AIsensors(myNode.toString());
		for (int i = 0; i < 24; i++) {
			if (onSwitches[i]) {
				if (!myNode.getBrawler(i).isOnBoard()) {
					// find all the spots to place them, make AIresult objects for each, add to optionList
					int[] placementSpots = sensor.getAllLegitPlacementTargets(i);
					for (int j = 0; j < placementSpots.length; j++) {
						rando = new AIresult(i, 1, placementSpots[j], -1, 0, redsTurn);
						optionList.add(rando);
						if (fallback.getAction() == 0) fallback = rando;
					}
				}
				else { // determine all available moves for brawler, make AIresult objects for each, add to optionList
					int[] playground = myNode.buildPlayground(i);
					int[] movePG = sensor.sortPlaygroundForMove(myNode, playground, i);
					for (int k = 0; k < movePG.length; k++) {
						optionList.add(new AIresult(i, 2, movePG[k], -1, 0, redsTurn));
					}
					if (i < 16) { // Character, Monster, or Nemesis
						int[] attPG = sensor.sortPlaygroundForAttack(myNode, playground, i);
						for (int m = 0; m < attPG.length; m++) {
							int victim = myNode.getLocation(attPG[m]).getOccupiedBy();
							rando = new AIresult(i, 3, attPG[m], victim, 0, redsTurn);
							optionList.add(rando);
							if (fallback.getAction() == 0) fallback = rando;
						}
					}
					if (myNode.getBrawler(i).getFloor() == 1 && (i > 7) && (i < 20)) { // don't make your Characters or any Treasures flee
						optionList.add(new AIresult(i, 5, 0, -1, 0, redsTurn)); // flee
					}
					if (myNode.getBrawler(i) instanceof Cleric) { // humble
						Brawler myWar = myNode.getBrawler("My Warrior");
						Brawler myMag = myNode.getBrawler("My Mage");
						Brawler myRog = myNode.getBrawler("My Rogue");
						if (myMag.isOnBoard() && myWar.getLevel() < 4) optionList.add(new AIresult(i, 11, 0, myWar.getPieceID(), 0, redsTurn));
						if (myMag.isOnBoard() && myMag.getLevel() < 4) optionList.add(new AIresult(i, 11, 0, myMag.getPieceID(), 0, redsTurn));
						if (myRog.isOnBoard() && myRog.getLevel() < 4) optionList.add(new AIresult(i, 11, 0, myRog.getPieceID(), 0, redsTurn));
					}
				}
			}
		}
		// now choose a random AIresult from optionList and return it
		Random randy = new Random();
		int index = 0;
		try {
			index = randy.nextInt(optionList.size());
		} catch(IllegalArgumentException ex) { return fallback; }
		
		return optionList.get(index);			
	}
	
	private static boolean[] setOnSwitches() {
		if (myNode.getRound() < 11) return setSQOSwitches();
		return setBrawlSwitches();
	}
	
	// this works for the SquareOff only
	private static boolean[] setSQOSwitches() { 
		boolean[] swtchs = new boolean[24];
		
		for (int k = 0; k < 24; k++) {
			Brawler bb = myNode.getBrawler(k);
			swtchs[k] = true;
			if (bb.isOnBoard() || bb instanceof Nemesis) swtchs[k] = false;
			else if ((myNode.isRedsTurn() && bb.isBlue()) || (!myNode.isRedsTurn() && bb.isRed())) swtchs[k] = false;
		}
		int chOnBrd = 0;
		if (myNode.isRedsTurn()) {
			for (int r = 0; r <= 3; r++) if (myNode.getBrawler(r).isOnBoard()) chOnBrd++;
		}
		else {
			for (int b = 4; b <= 7; b++) if (myNode.getBrawler(b).isOnBoard()) chOnBrd++;
		}
		if ((myNode.getRound() + (4 - chOnBrd)) > 10) {
			for (int x = 8; x <= 23; x++) swtchs[x] = false; // keeps the AI from procrastinating.
		}
		return swtchs;
	}		
		
	private static boolean[] setBrawlSwitches() {
		boolean[] switches = new boolean[24];
		for (int m = 0; m < 24; m++) {
			Brawler dd = myNode.getBrawler(m);
			if (myNode.getRedLU() == m) switches[m] = false;
			else if (myNode.getBlueLU() == m) switches[m] = false;
			else if ((myNode.isRedsTurn() && dd.isBlue()) || (!myNode.isRedsTurn() && dd.isRed())) switches[m] = false;
			else if (!myNode.canRedPlace() && !dd.isOnBoard() && myNode.isRedsTurn()) switches[m] = false;
			else if (!myNode.canBluePlace() && !dd.isOnBoard() && !myNode.isRedsTurn()) switches[m] = false;
			else if (dd instanceof Nemesis && !canNemesisBePlaced()) switches[m] = false;
			else if (dd.isOnBoard() && myNode.getLocation(dd.getLoc()).isGhostEffect()) switches[m] = false;
			else if (dd instanceof Rogue && (myNode.getBrawler("Sentinel").getFloor() == dd.getFloor())) switches[m] = false;
			else switches[m] = true;
		}
		return switches;
	}	
	
	private static boolean canNemesisBePlaced() {
		boolean placeNemesisOkay = false;
		int cl4CharsOnBoard = 0;
		int nemesesOnBoard = 0;
		for (int c = 0; c <= 7; c++) if (myNode.getBrawler(c).isOnBoard() && myNode.getBrawler(c).getLevel() == 4) cl4CharsOnBoard++;
		for (int n = 8; n <= 11; n++) if (myNode.getBrawler(n).isOnBoard()) nemesesOnBoard++;
		if (nemesesOnBoard <= cl4CharsOnBoard && myNode.getRound() > 10) placeNemesisOkay = true;
		return placeNemesisOkay;
	}
} // end of AIrandomizer class
