package rpgGridBrawl;

public class ScoringProfile {
	private int[] squareOff, brawl;
	private String name;
	
	public ScoringProfile(String nom, int[] sqo, int[] brw) { // this is for making a new Scoring profile
		this.name = nom;
		this.squareOff = new int[7];
		this.brawl = new int[22];
		this.squareOff[0] = sqo[0]; // pts for OS monster -> opponent's character
		this.squareOff[1] = sqo[1]; // pts for character -> OS Treasure
		this.squareOff[2] = sqo[2]; // pts for character -> SS monster
		this.squareOff[3] = sqo[3]; // pts for opponent's character trapped by Ghost
		this.squareOff[4] = sqo[4]; // pts for Warrior -> opponent's Cleric, Mage, or Rogue
		this.squareOff[5] = sqo[5]; // pts for character -> SS Treasure
		this.squareOff[6] = sqo[6]; // pts for character -> SS NPC
		this.brawl[0] = brw[0];   // // pts multiplied by the number of the floor for each character on the board.
		this.brawl[1] = brw[1];   // pts for each powered up character
		this.brawl[2] = brw[2];   // pts for having a character on the glorification space
		this.brawl[3] = brw[3];   // pts for each monster -> opponent's character but not vice versa
		this.brawl[4] = brw[4];   // pts for each character -> monster
		this.brawl[5] = brw[5];   // pts for each character -> OS treasure
		this.brawl[6] = brw[6];   // pts for each character that could purchase from the Merchant
		this.brawl[7] = brw[7];   // pts for each character -> SS treasure
		this.brawl[8] = brw[8];   // pts for each character -> opponent's character but not vice versa (victim hands free)
		this.brawl[9] = brw[9];  // extra pts for each character -> opp's char || mons when victim possesses treasure that is OS for attacker
		this.brawl[10] = brw[10]; // extra pts for each character -> opp's char || mons when victim possesses treasure that is SS for attacker
		this.brawl[11] = brw[11]; // pts for each character -> SS NPC
		this.brawl[12] = brw[12]; // pts if Rogue could pickpocket OS treasure
		this.brawl[13] = brw[13]; // pts for each character that possesses OS treasure
		this.brawl[14] = brw[14]; // pts for each character that possesses SS treasure
		this.brawl[15] = brw[15]; // pts for each Nemesis that is on the same floor as opponent's SS character
		this.brawl[16] = brw[16]; // pts for each opponent's character that is trapped by the ghost
		this.brawl[17] = brw[17]; // extra pts if (nemesis -> opponent's character) or (monster -> opp's char) if char posseses OS treasure
		this.brawl[18] = brw[18]; // extra pts if (nemesis -> opponent's character) or (monster -> opp's char) if char posseses SS treasure 
		this.brawl[19] = brw[19]; // pts for each glorified character off-board
		this.brawl[20] = brw[20]; // extra pts for having a glorified character on the board
		this.brawl[21] = brw[21]; // pts if Rogue could pickpocket the Diamond	
	}
	
	public ScoringProfile(String named) {
		this.name = named;
		if (this.name.equals("standard")) {
			this.squareOff = new int[7];
			this.brawl = new int[21];
			this.squareOff[0] = 1; // pts for OS monster -> opponent's character
			this.squareOff[1] = 2; // pts for character -> OS Treasure
			this.squareOff[2] = 3; // pts for character -> SS monster
			this.squareOff[3] = 3; // pts for opponent's character trapped by Ghost
			this.squareOff[4] = 4; // pts for Warrior -> opponent's Cleric, Mage, or Rogue
			this.squareOff[5] = 5; // pts for character -> SS Treasure
			this.squareOff[6] = 6; // pts for character -> SS NPC
			this.brawl[0] = 1;   // pts multiplied by the number of the floor for each character on the board.
			this.brawl[1] = 1;   // pts for each level of each character on the board
			this.brawl[2] = 1;   // pts for each powered up character
			this.brawl[3] = 1;   // pts for each monster or nemesis -> opponent's character
			this.brawl[4] = 1;   // pts for each character -> monster
			this.brawl[5] = 1;   // pts for each character -> OS treasure
			this.brawl[6] = 2;   // pts for each character that could purchase from the Merchant
			this.brawl[7] = 2;   // pts for each character -> SS treasure
			this.brawl[8] = 3;   // pts for having a character on the glorification space, ready to Glorify
			this.brawl[9] = 2;   // pts for each character -> opponent's character (victim hands free)
			this.brawl[10] = 1;  // extra pts for each character -> opp's char when victim possesses treasure that is OS for attacker
			this.brawl[11] = 2; // extra pts for each character -> opp's char when victim possesses treasure that is SS for attacker
			this.brawl[12] = 3; // pts for each character -> SS NPC
			this.brawl[13] = 3; // pts if Rogue could pickpocket OS treasure
			this.brawl[14] = 5; // pts for each character that possesses a treasure
			this.brawl[15] = 2; // extra pts if a character's possession is SS treasure
			this.brawl[16] = 4; // pts for each Nemesis that is on the same floor as opponent's SS character
			this.brawl[17] = 4; // pts for each opponent's character that is trapped by the ghost
			this.brawl[18] = 6; // pts for each glorified character off-board
			this.brawl[19] = 4; // extra pts for having a glorified character on the board
			this.brawl[20] = 6; // pts if Rogue could pickpocket the Diamond	
		}
	}
	
	public int getSQO(int index) {
		return this.squareOff[index];
	}
	
	public int getB(int index) {
		return this.brawl[index];
	}
	
}
