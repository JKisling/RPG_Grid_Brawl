package rpgGridBrawl;

public class Ring extends Brawler implements Treasure {
	private int floor, column, row, level, pieceID;
	private boolean onBoard, usedLast;
	private final boolean isRed, isBlue;
	private final String letterName;
	private final char suit;
	
	public Ring() {
		pieceID = 22;
		onBoard = false;
		letterName = "Ri";
		suit = 'M';
		isRed = false;
		isBlue = false;
	}
	
	public int getFloor() 				{ return this.floor; }
	public int getColumn() 				{ return this.column; }
	public int getRow() 				{ return this.row; }
	public int getLevel() 				{ return this.level; }
	public int getSwordBonus() 			{ return this.swordBonus; }
	public int getShieldBonus() 		{ return this.shieldBonus; }
	public int getClericDefenseBonus() 	{ return this.clericDefenseBonus; }
	public int getPossession() 			{ return this.possession; }
	public int getPieceID() 			{ return this.pieceID; }
	public char getSuit() 				{ return this.suit; }
	public boolean isOnBoard() 			{ return this.onBoard; }
	public boolean isMoveable() 		{ return true; }
	public boolean isUsedLast() 		{ return this.usedLast; }
	public boolean isRed() 				{ return this.isRed; }
	public boolean isBlue() 			{ return this.isBlue; }
	public boolean isPoweredUp() 		{ return this.poweredUp; }
	public boolean isHandsFull() 		{ return this.handsFull; }
	public boolean isGlorified() 		{ return this.glorified; }
	public String getLetterName() 		{ return this.letterName; }
	
	public void setFloor(int x) 			{ this.floor = x; }
	public void setColumn(int x) 			{ this.column = x; }
	public void setRow(int x) 				{ this.row = x; }
	public void setLevel(int x) 			{ this.level = x; } 
	public void setSwordBonus(int x) 		{ this.swordBonus = x; } 
	public void setShieldBonus(int x) 		{ this.shieldBonus = x; } 
	public void setClericDefenseBonus(int x){ this.clericDefenseBonus = x; } 
	public void setPossession(int x) 		{ this.possession = x; } 
	public void setPieceID(int x) 			{ this.pieceID = x; }
	public void setOnBoard(boolean x) 		{ this.onBoard = x; }
	public void setUsedLast(boolean x) 		{ this.usedLast = x; }
	public void setPoweredUp(boolean x) 	{ this.poweredUp = x; }
	public void setHandsFull(boolean x) 	{ this.handsFull = x; }
	public void setGlorified(boolean x) 	{ this.glorified = x; }
	
	public boolean canAttack(Brawler victim) { return false; }
	
	public void getsBumped() { this.remove(); }

} // end of Ring class
