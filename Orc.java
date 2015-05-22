package rpgGridBrawl;

class Orc extends Brawler implements Monster {
	private int floor, column, row, level, pieceID, possession;
	private int[] playground;
	private boolean onBoard, usedLast, handsFull;
	private final boolean isRed, isBlue;
	private final String letterName;
	private char suit;
	
	public Orc() {
		pieceID = 12;
		level = 1;
		floor = 0;
		row = 0;
		column = 0;
		onBoard = false;
		usedLast = false;
		handsFull = false;
		possession = -1;
		letterName = "O";
		isRed = false;
		isBlue = false;
		suit = 'W';
		playground = new int[32];
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
	public boolean isPoweredUp() { return false; }
	public boolean isHandsFull() { return this.handsFull; }
	public boolean isGlorified() { return false; }
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
	public void setHandsFull(boolean x) { this.handsFull = x; }
	public void setGlorified(boolean x) {return;}
	
	public boolean canAttack(Brawler victim) {
		boolean answer = false;
		int defense = (victim.level + victim.shieldBonus + victim.clericDefenseBonus);
		int attack = this.level;
		if (victim instanceof Warrior) attack--; // Orc is weak vs. Warrior
		boolean overcome = (attack >= defense);
		
		if (overcome && (victim instanceof Character || victim instanceof Nemesis)) answer = true;
		else if (victim instanceof Monster && (attack > defense)) answer = true;
		else if (victim instanceof NPC) answer = false;
		else if (!this.handsFull && victim instanceof Treasure) answer = true;
		return answer;
	}
	
	public void getsBumped() {
		if (this.getFloor() == 1) this.remove();
		else {
			if (this.getLevel() == 1) this.remove();
			else {
				this.setLevel(this.getLevel() - 1);
				this.setFloor(this.getFloor() - 1);
			}
		}
	}
} // end of Orc class
