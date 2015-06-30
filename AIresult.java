package rpgGridBrawl;

/**
 * Action Codes:
 * 0: do nothing	1: place	2: move		3: attack
 * 4: fireball		5: flee		6: glorify	7: pickpocket
 * 8: purchase		9: sack		10: transfer
 * 11: humble		12: bribe
 * 
 * What the AIresult tells us is: given the current AInode, the best move for a particular Brawler at this moment.  AIresult objects
 * are generated by the AI, each one gives all the data necessary to make any move.  AIresult objects can be fed into
 * AIhands.performDesiredAction(), which causes the AIhands object to make whatever move is dictated by AIresult.
 */

public class AIresult {
	private int brawlerID, action, idealPlace, secondaryTarget;
	private double bestAdv;
	private boolean iAmRed;
	
	public AIresult() {}
	
	public AIresult(double currentAdv, boolean redsTurn) {
		this.brawlerID = -1;
		this.action = 0;
		this.idealPlace = 0;
		this.secondaryTarget = -1;
		this.iAmRed = redsTurn;
		this.bestAdv = currentAdv;	
	}
	
	public AIresult(int a, int b, int c, int d, double f, boolean g) {
		this.brawlerID = a;
		this.action = b;
		this.idealPlace = c;
		this.secondaryTarget = d;
		this.bestAdv = f;
		this.iAmRed = g;
	}
	
	public int     getAction()          { return this.action; }
	public int     getID()              { return this.brawlerID; }
	public int     getIdealPlace()      { return this.idealPlace; }
	public int     getSecondaryTarget() { return this.secondaryTarget; }
	public double  getBestAdv()         { return this.bestAdv; }
	public boolean amIRed()             { return this.iAmRed; }
	
	public void setAction(int a)          { this.action = a; }
	public void setID(int b)              { this.brawlerID = b; }
	public void setIdealPlace(int c)      { this.idealPlace = c; }
	public void setSecondaryTarget(int d) { this.secondaryTarget = d; }
	public void setBestAdv(double f)      { this.bestAdv = f; }
	public void setIAmRed(boolean g)      { this.iAmRed = g; }
	
	public void updateMyself(int id, int action, int maybeIdealPlace, int secondary, double maybeBestAdv, boolean amRed) {
		boolean weChanged = false;
		if (amRed && (this.getIdealPlace() == 0 || maybeBestAdv > this.getBestAdv())) weChanged = true;
		else if (!amRed && (this.getIdealPlace() == 0 || maybeBestAdv < this.getBestAdv())) weChanged = true;
		else if (maybeBestAdv == this.getBestAdv()) {
			int getRandy = (int) (Math.random() * 100);
			int getSerious = 10;
			if (id >= 16 && id <= 19 && maybeIdealPlace < 300) getSerious = 25; // NPC, more likely to randomly place on floor 1 or 2
			if (id > 19) getSerious = 15; // Treasure
			int[] x = GridBrawl.splitBoardSpace(maybeIdealPlace);
			if (id < 8 && (x[1] == 4 || x[2] == 4)) getSerious = 0;
			if (getRandy < getSerious) weChanged = true;	
		}
		
		if (weChanged) {
			this.setID(id);
			this.setBestAdv(maybeBestAdv);
			this.setIdealPlace(maybeIdealPlace);
			this.setAction(action);
			this.setSecondaryTarget(secondary);
		}
	}
	
} // end of class AIresult