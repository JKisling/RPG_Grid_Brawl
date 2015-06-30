package rpgGridBrawl;

/* Standard set of Brawlers will be numbered as follows:
 *0: red warrior	1: red cleric	 	2: red mage			3:red rogue
 *4: blue warrior	5: blue cleric		6: blue mage		7: blue rogue
 *8: dragon			9: vampire			10: abyss			11: sentinel
 *12: orc			13: zombie			14: goblin			15: serpent
 *16: princess		17: leper			18: ghost			19: merchant
 *20: sword			21: shield			22: ring			23: diamond
 */

public abstract class Brawler {
	
	protected int floor, column, row, level, swordBonus, shieldBonus, clericDefenseBonus, possession, pieceID;
	protected char suit;
	protected boolean onBoard, moveable, usedLast, isRed, isBlue, poweredUp, handsFull, glorified;
	protected String letterName;
	
	// remember that pieceID describes the type of brawler, and the slot number in GameData.brawlers[] only coincides
	// with Brawler.pieceID in the standard set of pieces.
	
	public abstract boolean canAttack(Brawler victim);
	public abstract int getFloor();
	public abstract int getColumn();
	public abstract int getRow();
	public abstract int getLevel();
	public abstract int getSwordBonus();
	public abstract int getShieldBonus();
	public abstract int getClericDefenseBonus();
	public abstract int getPossession();
	public abstract int getPieceID();
	public abstract char getSuit();
	public abstract boolean isOnBoard();
	public abstract boolean isMoveable();
	public abstract boolean isUsedLast();
	public abstract boolean isRed();
	public abstract boolean isBlue();
	public abstract boolean isPoweredUp();
	public abstract boolean isHandsFull();
	public abstract boolean isGlorified();
	public abstract String getLetterName();
	
	public int getLoc() {
		if (!this.isOnBoard()) return 0;
		return ((this.getFloor() * 100) + (this.getColumn() * 10) + this.getRow());
	}
	
	public abstract void setFloor(int x);
	public abstract void setColumn(int x);
	public abstract void setRow(int x);
	public abstract void setLevel(int x);
	public abstract void setSwordBonus(int x);
	public abstract void setShieldBonus(int x);
	public abstract void setClericDefenseBonus(int x);
	public abstract void setPossession(int x);
	public abstract void setPieceID(int x);
	public abstract void setOnBoard(boolean x);
	public abstract void setUsedLast(boolean x);
	public abstract void setPoweredUp(boolean x);
	public abstract void setHandsFull(boolean x);
	public abstract void setGlorified(boolean x);
	
	public void setAsRed(boolean x) { this.isRed = x; }
	public void setAsBlue(boolean x) { this.isBlue = x; }
	
	
	public abstract void getsBumped();
	
	public void remove() {
		this.setOnBoard(false);
		this.setFloor(0);
		this.setRow(0);
		this.setColumn(0);
		this.setUsedLast(false);
		if (this instanceof Character || this instanceof Monster) this.setLevel(1);
		if (this.isGlorified()) this.setLevel(4);
		this.setPossession(-1);
		this.setHandsFull(false);
	}
	
	public boolean isAdjacentToMe(int f, int c, int r) {
		int cc = this.getColumn();
		int rr = this.getRow();
		if (f != this.getFloor()) return false;
		if ((c == cc) || (c - 1 == cc) || (c + 1 == cc)) {
			if ((r + 1 == rr) || (r - 1 == rr)) return true;
		}
		else if (r == rr) {
			if ((c + 1 == cc) || (c - 1 == cc)) return true;
		}
		return false;
	}
	
	// this outputs a string that goes into the status array of GameData and AInode objects.
	public String toString() {
		String build = "";
		build = this.getPieceID() + ",";
		if (this.isOnBoard()) build += "T,";
		else build += "F,";
		
		if (this.isRed())build += "T,";
		else build += "F,";
	
		if (this.isBlue())build += "T,";
		else build += "F,";
		
		if (this.isHandsFull()) build += "T,";
		else build += "F,";
		
		if (this.isGlorified()) build += "T,";
		else build += "F,";
		
		if (this.isUsedLast()) build += "T,";
		else build += "F,";
		
		if (this.isPoweredUp()) build += "T,";
		else build += "F,";
		
		build += this.getLevel() + ",";
		build += this.getFloor() + ",";
		build += this.getColumn() + ",";
		build += this.getRow() + ",";
		build += this.getPossession() + ",";
		build += this.getSwordBonus() + ",";
		build += this.getShieldBonus();
		return build;
	}
}
