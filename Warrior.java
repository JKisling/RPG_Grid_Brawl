package rpgGridBrawl;

class Warrior extends Brawler implements Character {
	int isCharging; // how am I supposed to make this work?
	private int floor, column, row, level, pieceID, possession;
	private int[] playground;
	private boolean onBoard, usedLast, isRed, isBlue;
	private final String letterName;
	
	public Warrior(int pieceNum) {
		pieceID = pieceNum;
		if (pieceNum == 0) {
			isRed = true;
			isBlue = false;
		}
		if (pieceNum == 4) {
			isRed = false;
			isBlue = true;
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
		isCharging = 0;
		possession = -1;
		letterName = "W";
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
	public boolean isPoweredUp() {return this.poweredUp;}
	public boolean isHandsFull() {return this.handsFull;}
	public boolean isGlorified() {return this.glorified;}
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
	public void setPoweredUp(boolean x) {this.poweredUp = x;}
	public void setHandsFull(boolean x) {this.handsFull = x;}
	public void setGlorified(boolean x) {this.glorified = x;}
	
	// assumes standard set of brawlers
	public boolean canAttack(Brawler victim) {
		boolean answer = false;
		int isOrc = 0;
		if (victim instanceof Orc) isOrc = 1;
		int defense = (victim.level + victim.shieldBonus + victim.clericDefenseBonus - isOrc);
		int attack = (this.level + this.swordBonus + this.isCharging);
		// The Warrior has inherent Battle Skill, thus can attack Characters at CL=
		boolean overcome = (attack >= defense);
		if (victim instanceof Character && overcome) {
			if ((this.isRed && victim.isBlue) || (this.isBlue && victim.isRed)) answer = true;
		}
		else if (overcome && (victim instanceof Monster || victim instanceof Nemesis)) answer = true;
		else if (victim instanceof Princess && !this.handsFull) answer = true;
		else if (victim instanceof Leper && this.level > 1) answer = true;
		else if (victim instanceof Ghost || victim instanceof Merchant) answer = false;
		else if (victim instanceof Treasure && !this.handsFull) answer = true;
		return answer;
	}
	
	public void getsBumped() {
		if (this.getFloor() == 1) this.remove();
		else {
			if (this.getLevel() == 1) this.remove();
			else {
				this.setFloor(this.getFloor() - 1);
				if (!this.isGlorified()) this.setLevel(this.getLevel() - 1);	
			}
		}
	}
} // end of Warrior class
