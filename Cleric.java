package rpgGridBrawl;

public class Cleric extends Brawler implements Character {
	private int floor, column, row, level, pieceID, possession;
	private int[] playground;
	private boolean onBoard, usedLast, isRed, isBlue;
	private final String letterName;
	private final char suit;
	
	public Cleric(int pieceNum) {
		pieceID = pieceNum;
		if (pieceNum == 1) {
			this.isRed = true;
			this.isBlue = false;
		}
		if (pieceNum == 5) { 
			this.isRed = false;
			this.isBlue = true;
		}
		level = 1;
		floor = 0;
		row = 0;
		column = 0;
		onBoard = false;
		usedLast = false;
		poweredUp = false;
		handsFull = false;
		glorified = false;
		swordBonus = 0;
		shieldBonus = 0;
		possession = -1;
		letterName = "C";
		suit = 'C';
		playground = new int[24];
	}
	
	public int getFloor() 				{ return this.floor; }
	public int getColumn() 				{ return this.column; }
	public int getRow() 				{ return this.row; }
	public int getLevel() 				{ return this.level; }
	public int getSwordBonus() 			{ return this.swordBonus; }
	public int getShieldBonus() 			{ return this.shieldBonus; }
	public int getClericDefenseBonus() 		{ return this.clericDefenseBonus; }
	public int getPossession() 			{ return this.possession; }
	public int getPieceID() 			{ return this.pieceID; }
	public int[] getPlayground() 			{ return this.playground; }
	public char getSuit() 				{ return this.suit; }
	public boolean isOnBoard() 			{ return this.onBoard; }
	public boolean isMoveable() 			{ return true; }
	public boolean isUsedLast() 			{ return this.usedLast; }
	public boolean isRed() 				{ return this.isRed; }
	public boolean isBlue() 			{ return this.isBlue; }
	public boolean isPoweredUp() 			{ return this.poweredUp; }
	public boolean isHandsFull() 			{ return this.handsFull; }
	public boolean isGlorified() 			{ return this.glorified; }
	public String getLetterName() 			{ return this.letterName; }
	
	public void setFloor(int x) 			{ this.floor = x; }
	public void setColumn(int x) 			{ this.column = x; }
	public void setRow(int x) 			{ this.row = x; }
	public void setLevel(int x) 			{ this.level = x; } 
	public void setSwordBonus(int x) 		{ this.swordBonus = x; } 
	public void setShieldBonus(int x) 		{ this.shieldBonus = x; } 
	public void setClericDefenseBonus(int x)	{ this.clericDefenseBonus = x; }
	public void setPossession(int x) 		{ this.possession = x; } 
	public void setPieceID(int x) 			{ this.pieceID = x; }
	public void setPlayground(int[] x) 		{ this.playground = x; }
	public void setOnBoard(boolean x) 		{ this.onBoard = x; }
	public void setUsedLast(boolean x) 		{ this.usedLast = x; }
	public void setPoweredUp(boolean x) 		{ this.poweredUp = x; }
	public void setHandsFull(boolean x) 		{ this.handsFull = x; }
	public void setGlorified(boolean x) 		{ this.glorified = x; }
	
	public boolean canAttack(Brawler victim) {
		boolean answer = false;
		int isZombie = 0;
		if (victim instanceof Zombie) isZombie = 1;
		int defense = (victim.getLevel() + victim.getShieldBonus() + victim.getClericDefenseBonus() - isZombie);
		int attack = (this.getLevel() + this.getSwordBonus());
		boolean overcome = (attack > defense);
		if (victim instanceof Character && overcome) {
			if ((this.isRed() && victim.isBlue()) || (this.isBlue() && victim.isRed())) answer = true;
		}
		else if (victim instanceof Monster || victim instanceof Nemesis) {
			overcome = (attack >= defense);
			if (overcome) answer = true;
		}
		else if (victim instanceof Princess) answer = false;
		else if (victim instanceof Leper) answer = true;
		else if (victim instanceof Ghost || victim instanceof Merchant) answer = false;
		else if (victim instanceof Treasure && !this.isHandsFull()) answer = true;
		return answer;
	}
		
	public void getsBumped() {
		if (this.getFloor() == 1) this.remove();
		else {
			if (this.getLevel() == 1) this.remove();
			else {
				if (this.getShieldBonus() == 0 && !this.isGlorified()) this.setLevel(this.getLevel() - 1);
				this.setFloor(this.getFloor() - 1);
			}
		}
	}
	
} // end of Cleric class
