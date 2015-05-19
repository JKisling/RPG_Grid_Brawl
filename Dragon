package rpgGridBrawl;

public class Dragon extends Brawler implements Nemesis {
	private int floor, column, row, level, pieceID;
	private int[] playground;
	private boolean onBoard, usedLast;
	private final boolean isRed, isBlue;
	private final String letterName;
	
	public Dragon() {
		pieceID = 8;
		onBoard = false;
		letterName = "DG";
		isRed = false;
		isBlue = false;
		suit = 'W';
		playground = new int[32];
		level = 4;
		poweredUp = false;
		handsFull = false;
		glorified = false;
	}
	
	public int getFloor() {return this.floor;}
	public int getColumn() {return this.column;}
	public int getRow() {return this.row;}
	public int getLevel() {return this.level;}
	public int getSwordBonus() {return this.swordBonus;}
	public int getShieldBonus() {return this.shieldBonus;}
	public int getClericDefenseBonus() {return this.clericDefenseBonus;}
	public int getPossession() {return this.possession;}
	public int getPieceID() {return this.pieceID;}
	public int[] getPlayground() {return this.playground;}
	public char getSuit() {return this.suit;}
	public boolean isOnBoard() {return this.onBoard;}
	public boolean isMoveable() {return true;}
	public boolean isUsedLast() {return this.usedLast;}
	public boolean isRed() {return this.isRed;}
	public boolean isBlue() {return this.isBlue;}
	public boolean isPoweredUp() {return false;}
	public boolean isHandsFull() {return false;}
	public boolean isGlorified() {return false;}
	public String getLetterName() {return this.letterName;}
	
	public void setFloor(int x) {this.floor = x;}
	public void setColumn(int x) {this.column = x;}
	public void setRow(int x) {this.row = x;}
	public void setLevel(int x) {this.level = x;} 
	public void setSwordBonus(int x) {this.swordBonus = x;} 
	public void setShieldBonus(int x) {this.shieldBonus = x;} 
	public void setClericDefenseBonus(int x) {this.clericDefenseBonus = x;} 
	public void setPossession(int x) {this.possession = x;}  
	public void setPieceID(int x) {this.pieceID = x;}
	public void setPlayground(int[] x) {this.playground = x;}
	public void setOnBoard(boolean x) {this.onBoard = x;}
	public void setUsedLast(boolean x) {this.usedLast = x;}
	public void setPoweredUp(boolean x) {return;}
	public void setHandsFull(boolean x) {return;}
	public void setGlorified(boolean x) {return;}
	
	public boolean canAttack(Brawler victim) {
		if (victim instanceof Ghost) return false;
		if (victim instanceof Character) {
			if ((victim.level + victim.clericDefenseBonus + victim.shieldBonus) > 4) return false;
		}
		return true;
	}

	public void getsBumped() {
		this.remove();
	}
}
