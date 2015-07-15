package rpgGridBrawl;

/**
 * This class is intended to provide different AI "personalities" that emphasize different strategies.
 * The first index is for the differnt Brawlers, and is arranged as follows:
 * 
 * 0: Warrior	1: Cleric	2: Mage		3: Rogue
 * 4: Nemeses and Monsters
 * 5: Princess, Leper, and Ghost
 * 6: Merchant and Treasures
 * 
 * This object is contained within the ScoringProfile object, and will be called from it.
 * comments on the lines: +/- = inFavor/against
 */
public class AIconditionProfile {
	private final double[][] preC, postC;
	
	public AIconditionProfile(String profile) {
		preC = new double[7][7];
		postC = new double[7][9];
		
		if (profile.equals("standard")) {
			preC[0][0] = 0.1; 	// +: place Warrior when you have <3 Characters on the board
			preC[0][1] = 0.15; 	// +: The more Characters opponent has on board, the more encouragement to place the Warrior
			preC[0][2] = 3;		// +: Warrior attacking anything is encouraged
			preC[0][3] = 100;	// +: The "must glorify" rule applied to the Warrior
			preC[0][4] = 3;		// +: Encourage Warrior to purchase the Sword if it is possessed by an enemy Character
			preC[1][0] = 0.1; 	// +: place Cleric when you have <3 Characters on the board
			preC[1][1] = 0.15; 	// +: The more Characters opponent has on board, the more encouragement to place the Cleric
			preC[1][2] = 3;		// +: Cleric attacking anything is encouraged
			preC[1][3] = 100;	// +: The "must glorify" rule applied to the Cleric
			preC[1][4] = 3;		// +: Encourage Cleric to purchase the Shield if it is possessed by an enemy Character
			preC[2][0] = 0.1; 	// +: place Mage when you have <3 Characters on the board
			preC[2][1] = 0.15; 	// +: The more Characters opponent has on board, the more encouragement to place the Mage
			preC[2][2] = 3;		// +: Mage attacking anything is encouraged
			preC[2][3] = 100;	// +: The "must glorify" rule applied to the Mage
			preC[2][4] = 3;		// +: Encourage Mage to purchase the Ring if it is possessed by an enemy Character
			preC[3][0] = 0.1; 	// +: place Rogue when you have <3 Characters on the board
			preC[3][1] = 0.15; 	// +: The more Characters opponent has on board, the more encouragement to place the Rogue
			preC[3][2] = 3;		// +: Rogue attacking anything is encouraged
			preC[3][3] = 100;	// +: The "must glorify" rule applied to the Rogue
			preC[4][0] = 3;		// -: Don't place a Nemesis if opponent's same-suit Character is not on level 4
			preC[4][1] = 4;		// +: Having a Nemesis flee is encouraged if you have off-board Characters
			preC[4][2] = 5;		// -: Having level 1 or 2 Monsters flee is discouraged
			preC[5][0] = 1;		// +: Placing the Princess on round 1 is encouraged
			preC[5][1] = 2;		// -: Placing the Princess is discouraged if my Warrior possesses Treasure
			preC[5][2] = 4;		// -: Moving the Princess at all is discouraged unless it is on space #400 (blocking Glory space)
			preC[5][3] = 3;		// +: Placing the Leper is encouraged if my Cleric is off-board and opponent's Cleric is on-board
			preC[5][4] = 7;		// -: Moving the Leper at all is discouraged unless it is in opponent Cleric's playground
			preC[5][5] = 1;		// +/-: Placing the Ghost is a good or bad idea, depending on how many Characters opponent has on-board
			preC[5][6] = 4;		// +: Moving the Ghost is encouraged if it is adjacent to my Mage
			preC[6][0] = 3;		// +: Placing the Merchant is encouraged if my Rogue is off-board and opponent's Rogue is on-board
			preC[6][1] = 7;		// -: Moving the Merchant at all is discouraged unless it is in opponent Rogue's playground
			preC[6][2] = 10;	// +: Sacrifice is encouraged any time it is possible to do
			
			postC[0][0] = 0.75;	// -: Don't place Warrior on the floor 1 frontier
			postC[0][1] = 0.1;	// +: Place the Warrior closer to the center of the board
			postC[0][2]	= 1;	// -: If the Ghost is off-board, don't place the Warrior adjacent to a friendly Character	
			postC[0][3] = 2;	// -: Don't place the Warrior where it can easily be attacked right away
			postC[0][4] = 1;	// +: Move the Warrior up if not glorified and level > floor, especially if floor == 1
			postC[0][5] = 5;	// +: Move the Warrior into the "glory corridor" if level == 4 and unglorified
			postC[0][6] = 100;	// +: The "must kill" rule applied to the Warrior
			postC[1][0] = 0.75;	// -: Don't place Cleric on the floor 1 frontier
			postC[1][1] = 1;	// +: placing the Cleric directly underneath the Leper is encouraged
			postC[1][2] = 1;	// -: If the Ghost is off-board, don't place the Cleric adjacent to a friendly Character
			postC[1][3] = 2;	// -: Don't place the Cleric where it can easily be attacked right away
			postC[1][4] = 1;	// +: Move the Cleric up if not glorified and level > floor, especially if floor == 1
			postC[1][5] = 5;	// +: Move the Cleric into the "glory corridor" if level == 4 and unglorified
			postC[1][6] = 100;	// +: The "must kill" rule applied to the Cleric
			postC[1][7] = 3;	// +: Curing the Leper is encouraged for the Cleric
			postC[2][0] = 0.75;	// -: Don't place Mage on the floor 1 frontier
			postC[2][2]	= 1;	// -: If the Ghost is off-board, don't place the Mage adjacent to a friendly Character
			postC[2][3] = 5;	// -: Don't place the Mage adjacent to the Ghost
			postC[2][4] = 2;	// -: Don't place the Mage where it can easily be attacked right away
			postC[2][5] = 1;	// +: Move the Mage up if not glorified and level > floor, especially if floor == 1
			postC[2][6] = 5;	// +: Move the Mage into the "glory corridor" if level == 4 and unglorified
			postC[2][7] = 100;	// +: The "must kill" rule applied to the Mage
			postC[2][8] = 3;	// +: Using a Mage to fireball a Nemesis is encouraged
			postC[3][0] = 0.75;	// -: Don't place Rogue on the floor 1 frontier
			postC[3][1]	= 1;	// -: If the Ghost is off-board, don't place the Rogue adjacent to a friendly Character
			postC[3][2] = 1;	// -: Don't place the Rogue where its movement will be blocked on all sides
			postC[3][3] = 2;	// -: Don't place the Rogue where it can easily be attacked right away
			postC[3][4] = 1;	// +: Move the Rogue up if not glorified and level > floor, especially if floor == 1
			postC[3][5] = 5;	// +: Move the Rogue into the "glory corridor" if level == 4 and unglorified
			postC[3][6] = 100;	// +: The "must kill" rule applied to the Rogue
			postC[3][7] = 3;	// +: Robbing the Merchant is encouraged for the Rogue
			postC[3][8] = 5;	// +: Bribing a friendly level 4 character to the Glorification space is encouraged for the Rogue
			postC[4][0] = 2;	// -: Placing a Nemesis directly above an opponent's level 4 Character is discouraged
			postC[4][1] = 5;	// -: Having a Nemesis attack an NPC or a Treasure is discouraged
			postC[4][2] = 100;	// +: The "must kill" rule applied to Monsters
			postC[5][1] = 1;	// +/-: Placing the Princess in the correct spot is important when Warriors are in the area
			postC[5][2] = 4;	// -: If opponent's Cleric is off-board, do not place the Leper on floor 2 above an empty space unless my Cleric is on floor 2
			postC[5][3] = 3;	// +: Placing the Leper directly above my Cleric is encouraged
			postC[5][4] = 2;	// -: Placing the Ghost adjacent to my Characters is discouraged
			postC[5][5] = 4;	// +: Placing the Ghost adjacent to the opponent's Mage is encouraged
			postC[5][6] = 5;	// -: Moving the Ghost to a space adjacent to my Mage is discouraged
			postC[6][0] = 4;	// -: Placing the Merchant anywhere above or below opponent's Rogue is discouraged
			postC[6][1] = 2;	// -: Placing the Merchant anywhere above or below my Rogue is encouraged
		}
	}
	
	public double getPre(int indexA, int indexB) { return this.preC[indexA][indexB]; }
	public double getPost(int indexA, int indexB) { return this.postC[indexA][indexB]; }
	public double[][] getWholePre() { return this.preC; }
	public double[][] getWholePost() { return this.postC; }
}
