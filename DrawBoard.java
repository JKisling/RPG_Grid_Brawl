package rpgGridBrawl;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

class DrawBoard extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private GameData game;
	private int dtBL;
	private String[] brwlrs;
	private Color spaceNumbers = new Color(0,0,0);
	private Color redLastUsedColor = new Color(255,190,220);
	private Color blueLastUsedColor = new Color(170,220,255);
	private Color nemesisColor = new Color(0,170,0);
	private Color monsterColor = new Color(255,174,0);
	private Color npcColor = new Color(160,100,40);
	private Color treasureColor = new Color(200,95,245);
	public JButton button1,button2,button3,button4;
	Font boardNumberFont = new Font("Serif", Font.BOLD, 18);
	Font levelFont = new Font("Serif", Font.BOLD, 23);
	Font glorified = new Font("Serif", Font.ITALIC, 40);
	Font brawlerFont = new Font("Serif", Font.BOLD, 48);
	
	public DrawBoard(GameData crtDB, int bl) {
		this.game = crtDB;
		this.dtBL = bl;
		this.brwlrs = getBrawlerNames(crtDB);
		if (crtDB.isRedsTurn()) setBackground(Color.RED);
		else setBackground(Color.BLUE);
		setPreferredSize(new Dimension(1475, 1000));
		this.button1 = new JButton();
		this.button2 = new JButton();
		this.button3 = new JButton();
		this.button4 = new JButton();		
		int bSize = 40;
		JPanel buttonPanel = new JPanel(new GridLayout(0,4));
		this.button1.setSize(bSize, bSize);			this.button2.setSize(bSize, bSize);
		this.button3.setSize(bSize, bSize);			this.button4.setSize(bSize, bSize);
		this.button1.addActionListener(this);		this.button2.addActionListener(this);
		this.button3.addActionListener(this);		this.button4.addActionListener(this);
		this.button1.setBackground(Color.WHITE);	this.button1.setText("1");
		this.button2.setBackground(Color.WHITE);	this.button2.setText("2");
		this.button3.setBackground(Color.WHITE);	this.button3.setText("3");
		this.button4.setBackground(Color.WHITE);	this.button4.setText("4");
		buttonPanel.add(button1);					buttonPanel.add(button2);
		buttonPanel.add(button3);					buttonPanel.add(button4);
		add(buttonPanel);
	}
	
	public void actionPerformed(ActionEvent event) {
		Object sourceObject = event.getSource();
		if(sourceObject == button1) 		this.setDTBL(1);
		else if(sourceObject == button2)	this.setDTBL(2);
		else if(sourceObject == button3) 	this.setDTBL(3);
		else if(sourceObject == button4)	this.setDTBL(4);
		repaint();		
	}
	
	private void drawCharacterSketch(GameData GM, Graphics drawMe, int pieceID, int xOrigin, int yOrigin, int sketchScale, int squareScale) {
		Brawler zeke = this.game.getBrawler(pieceID);
		if (zeke.isRed()) drawMe.setColor(Color.RED);
		else drawMe.setColor(Color.BLUE);
		int getSketch = -1;
		if (zeke instanceof Warrior) {
			switch(zeke.getLevel()){
			case 1: getSketch = 0;	break;
			case 2: getSketch = 4;	break;
			case 3: getSketch = 24;	break;
			case 4: getSketch = 28;	break;
			}
		}
		else if (zeke instanceof Cleric) {
			switch(zeke.getLevel()){
			case 1: getSketch = 1;	break;
			case 2: getSketch = 5;	break;
			case 3: getSketch = 25;	break;
			case 4: getSketch = 29;	break;
			}
		}
		else if (zeke instanceof Mage) {
			switch(zeke.getLevel()){
			case 1: getSketch = 2;	break;
			case 2: getSketch = 6;	break;
			case 3: getSketch = 26;	break;
			case 4: getSketch = 30;	break;
			}
		}
		else if (zeke instanceof Rogue) {
			switch(zeke.getLevel()){
			case 1: getSketch = 3;	break;
			case 2: getSketch = 7;	break;
			case 3: getSketch = 27;	break;
			case 4: getSketch = 31;	break;
			}
		}
		Sketch sketch = new Sketch(getSketch);
		int[] clPlacement = sketch.getclPlacement();
		int numberOfLines = sketch.getNumberOfLines();
		for (int k = 0; k < numberOfLines; k++) {
			int[] line = sketch.getLine(k);
			int[] xdrawS = getSketchLineCoordinates(xOrigin, yOrigin, line, sketchScale, squareScale, true);
			int[] ydrawS = getSketchLineCoordinates(xOrigin, yOrigin, line, sketchScale, squareScale, false);
			drawMe.fillPolygon(xdrawS, ydrawS, 4);	
		}
		drawMe.setFont(levelFont);
		drawMe.setColor(Color.WHITE);
		if (zeke.isGlorified()) {
			drawMe.setFont(glorified);
			clPlacement[0] -= 5;
			clPlacement[1] += 10;
			
		}
		String level = "" + zeke.getLevel();
		drawMe.drawString(level, (xOrigin + clPlacement[0]), (yOrigin + clPlacement[1]));
		
		// if zeke has a treasure, draw that
		if (zeke.isHandsFull()){
			Brawler loot = GM.getBrawler(zeke.getPossession());
			int sketchnum = -1;
			if (loot instanceof Sword)   sketchnum = 32;
			if (loot instanceof Shield)  sketchnum = 33;
			if (loot instanceof Ring)    sketchnum = 34;
			if (loot instanceof Diamond) sketchnum = 35;
			Sketch lootSketch = new Sketch(sketchnum);
			drawMe.setColor(treasureColor);
			numberOfLines = lootSketch.getNumberOfLines();
			for (int t = 0; t < numberOfLines; t++) {
				int[] line = lootSketch.getLine(t);
				int[] xdrawS = getSketchLineCoordinates(xOrigin, yOrigin, line, sketchScale, squareScale, true);
				int[] ydrawS = getSketchLineCoordinates(xOrigin, yOrigin, line, sketchScale, squareScale, false);
				drawMe.fillPolygon(xdrawS, ydrawS, 4);	
			}
		}
	}
	
	private void drawMonsterSketch(GameData game, Graphics drawMe, int pieceID, int xOrigin, int yOrigin, int sketchScale, int squareScale) {
		Sketch sketch = new Sketch(pieceID);
		int numberOfLines = sketch.getNumberOfLines();
		drawMe.setColor(monsterColor);
		for (int k = 0; k < numberOfLines; k++) {
			int[] line = sketch.getLine(k);
			int[] xdrawS = getSketchLineCoordinates(xOrigin, yOrigin, line, sketchScale, squareScale, true);
			int[] ydrawS = getSketchLineCoordinates(xOrigin, yOrigin, line, sketchScale, squareScale, false);
			drawMe.fillPolygon(xdrawS, ydrawS, 4);	
		}
		int correctMonster = pieceID;
		if (correctMonster > 24) correctMonster -= 24;
		Brawler billy = game.getBrawler(correctMonster);
		int[] clPlacement = sketch.getclPlacement();
		drawMe.setFont(levelFont);
		drawMe.setColor(Color.BLACK);
		String level = "" + billy.getLevel();
		drawMe.drawString(level, (xOrigin + clPlacement[0]), (yOrigin + clPlacement[1]));
		
		if (billy.isHandsFull()){
			System.out.println("Billy #" + billy.pieceID + " is hands full");
			Brawler loot = game.getBrawler(billy.getPossession());
			int sketchnum = -1;
			if (loot instanceof Sword)   sketchnum = 32;
			if (loot instanceof Shield)  sketchnum = 33;
			if (loot instanceof Ring)    sketchnum = 34;
			if (loot instanceof Diamond) sketchnum = 35;
			Sketch lootSketch = new Sketch(sketchnum);
			// this is black to indicate that Monsters gain no benefit from Treasure
			drawMe.setColor(Color.BLACK);
			numberOfLines = lootSketch.getNumberOfLines();
			for (int t = 0; t < numberOfLines; t++) {
				int[] line = lootSketch.getLine(t);
				int[] xdrawS = getSketchLineCoordinates(xOrigin, yOrigin, line, sketchScale, squareScale, true);
				int[] ydrawS = getSketchLineCoordinates(xOrigin, yOrigin, line, sketchScale, squareScale, false);
				drawMe.fillPolygon(xdrawS, ydrawS, 4);	
			}
		}
	}
	
	private void drawSketch(Graphics drawMe, int pieceID, int xOrigin, int yOrigin, int sketchScale, int squareScale) {
		Sketch sketch = new Sketch(pieceID);
		int numberOfLines = sketch.getNumberOfLines();
		Brawler zeke = this.game.getBrawler(pieceID);
		if (zeke instanceof Nemesis) drawMe.setColor(nemesisColor);
		else if (zeke instanceof NPC) drawMe.setColor(npcColor);
		else if (zeke instanceof Treasure) drawMe.setColor(treasureColor);
		for (int k = 0; k < numberOfLines; k++) {
			int[] line = sketch.getLine(k);
			int[] xdrawS = getSketchLineCoordinates(xOrigin, yOrigin, line, sketchScale, squareScale, true);
			int[] ydrawS = getSketchLineCoordinates(xOrigin, yOrigin, line, sketchScale, squareScale, false);
			drawMe.fillPolygon(xdrawS, ydrawS, 4);	
		}
	}
	
	private static int[] getBoardDimensions(int bl) {
		int[] BD = new int[3];
		switch(bl) {
		case 1: BD[0] = 1;	BD[1] = 4;		BD[2] = 4;		break;
		case 2: BD[0] = 2;	BD[1] = 3;		BD[2] = 3;		break;
		case 3: BD[0] = 3;	BD[1] = 2;		BD[2] = 2;		break;
		case 4: BD[0] = 4;	BD[1] = 2;		BD[2] = 2;		break;
		}
		return BD;
	}
	
	public int getDTBL() {
		return this.dtBL;
	}
	
	private static int[] getSquareCoordinates(int cg, int rg, int x, int z, boolean aSpaceX, boolean aSpaceY) {
		// square[] is xxxx  or yyyy going clockwise from upper left.  To make it yyyy pass in 4 for z
		// x is the variable we set to get the correct size overall.  xyz is for the space between spaces.
		double yz = Math.floor(x / 10);
		int xyz = (int)yz;
		int[] square = new int[8];
		int xShift = 200;
		int yShift = 200;
		square[0] = (x + xyz) * cg + xShift;	square[1] = (x + xyz) * cg + x + xShift;	square[2] = square[1];		square[3] = square[0];
		square[4] = (x + xyz) * rg + yShift;	square[5] = square[4];	square[6] = (x + xyz) * rg + x + yShift;		square[7] = square[6];
		int[] xORy = new int[4];
		xORy[0] = square[0 + z];	xORy[1] = square[1 + z];	xORy[2] = square[2 + z];	xORy[3] = square[3 + z];
		if (aSpaceX) {
			xORy[0] = xShift - x - xyz;
			xORy[1] = xShift - xyz;
			xORy[2] = xORy[1];
			xORy[3] = xORy[0];	
		}
		else if (aSpaceY) {
			xORy[0] = yShift - x - xyz;
			xORy[1] = xORy[0];
			xORy[2] = yShift - xyz;
			xORy[3] = xORy[2];	
		}
		return xORy;	
	}
	
	private static int[] getSketchLineCoordinates(int cg, int rg, int[] line, int scale, int squareScale, boolean aSpaceX) {
		int[] drawLine = new int[4];
		if (aSpaceX) {
			drawLine[0] = cg + (scale * 3) + (line[0] * scale) + 3;
			drawLine[1] = drawLine[0] + scale;
			drawLine[2] = drawLine[1];
			drawLine[3] = drawLine[0];	
		}
		else {
			drawLine[0] = rg + (scale * 3) + (line[1] * scale) + 3;
			drawLine[1] = drawLine[0];
			drawLine[2] = rg + (scale * 3) + (line[2] * scale) + 3;
			drawLine[3] = drawLine[2];
		}
		return drawLine;
	}
	
	private static String[] getBrawlerNames(GameData crtGBN) {
		String[] BN = new String[crtGBN.getAllBrawlers().length];
		for (int x = 0; x < BN.length; x++) {
			Brawler xyz = crtGBN.getBrawler(x);
			if (xyz instanceof Character || xyz instanceof Monster) BN[x] = (xyz.getLetterName() + xyz.getLevel());
			else BN[x] = xyz.getLetterName();
		}
		return BN;
	}
	
	// this method assumes the standard board configuration
	public void paintComponent(Graphics drawMe) {
		super.paintComponent(drawMe);
		int[] BD = getBoardDimensions(this.dtBL);
		int BL = BD[0];		int columns = BD[1];		int rows = BD[2];
		int c = 0;	int r = 0;
		
		this.button1.setBackground(Color.WHITE);
		this.button1.setEnabled(true);
		this.button2.setBackground(Color.WHITE);
		this.button2.setEnabled(true);
		this.button3.setBackground(Color.WHITE);
		this.button3.setEnabled(true);
		this.button4.setBackground(Color.WHITE);
		this.button4.setEnabled(true);
		switch(this.dtBL) {
		case 1: this.button1.setBackground(Color.GREEN);
			this.button1.setEnabled(false);
			break;
		case 2: this.button2.setBackground(Color.GREEN);
			this.button2.setEnabled(false);
			break;	
		case 3: this.button3.setBackground(Color.GREEN);
			this.button3.setEnabled(false);
			break;
		case 4: this.button4.setBackground(Color.GREEN);
			this.button4.setEnabled(false);
			break;	
		}
		int squareScale = 140;
		int sketchScale = 5;
		
		// Each trip through the inner for loop will draw one board space on the current level.
		for(c = 0; c <= columns; c++) {
			for(r = 0; r <= rows; r++) {
				int[] xdraw = getSquareCoordinates(c, r, squareScale, 0, false, false);
				int[] ydraw = getSquareCoordinates(c, r, squareScale, 4, false, false);
				
				// draw the space: make it pink or light blue if last used, otherwise white. Then draw the space numbers
				try {
					int rlu = this.game.getRedLastUsed();
					int blu = this.game.getBlueLastUsed();
					int brdOccBy = this.game.getLocation(BL, c, r).getOccupiedBy();
					if ((rlu != -1) && (brdOccBy == rlu)) drawMe.setColor(redLastUsedColor);
					else if ((blu != -1) && (brdOccBy == blu)) drawMe.setColor(blueLastUsedColor);
					else drawMe.setColor(Color.WHITE);
				}
				catch(NullPointerException ex) {
					drawMe.setColor(Color.WHITE);
				}
				drawMe.fillPolygon(xdraw, ydraw, 4);
				drawMe.setColor(spaceNumbers);
				drawMe.setFont(boardNumberFont);
				int spcNo = (BL * 100) + (c * 10) + r;
				String sspcNo = spcNo + "";
				drawMe.drawString(sspcNo, xdraw[0] + 2, ydraw[3] - 5);
		// if there is a piece on this space, draw it
				boolean occupado = false;
				try { occupado = this.game.getLocation(BL, c, r).isOccupied(); }
				catch(NullPointerException ex) { occupado = false; }
				if (occupado) {
					int BB = this.game.getLocation(BL, c, r).getOccupiedBy();
					try {
						Brawler jimbo = this.game.getBrawler(BB);
						if (jimbo instanceof Monster) {
							int bigOrSmall = jimbo.getPieceID();
							if (jimbo.getLevel() > 2) bigOrSmall += 24;
							drawMonsterSketch(this.game, drawMe, bigOrSmall, xdraw[0], ydraw[0], sketchScale, squareScale);
						}
						else if (jimbo instanceof Character) drawCharacterSketch(this.game, drawMe, jimbo.getPieceID(), xdraw[0], ydraw[0], sketchScale, squareScale);
						else drawSketch(drawMe, jimbo.getPieceID(), xdraw[0], ydraw[0], sketchScale, squareScale);
					}
					catch (ArrayIndexOutOfBoundsException ex) {}
				}
				// if there is a piece in the space directly above, draw that
				if (((BL == 1 && c < 4 && r < 4) || (BL == 2 && c < 3 && r < 3) || (BL == 3)) && this.game.getLocation((BL + 1), c, r).isOccupied()) {
					int BBu = this.game.getLocation((BL + 1), c, r).getOccupiedBy();
					try {
						String LNu = this.brwlrs[BBu];
						drawMe.setFont(boardNumberFont);
						Brawler jimbo = this.game.getBrawler(BBu);
						drawMe.setColor(setCorrectColor(jimbo));
						drawMe.drawString(LNu, xdraw[1] - 27, ydraw[0] + 20);
					}	
					catch (ArrayIndexOutOfBoundsException ex) {}
				}
				// if there is a piece in the space directly below, draw that
				if (BL > 1 && this.game.getLocation((BL - 1), c, r).isOccupied()) {
					int BBd = this.game.getLocation((BL - 1), c, r).getOccupiedBy();
					try {
						String LNd = this.brwlrs[BBd];
						drawMe.setFont(boardNumberFont);
						Brawler jimbo = this.game.getBrawler(BBd);
						drawMe.setColor(setCorrectColor(jimbo));
						drawMe.drawString(LNd, xdraw[1] - 27, ydraw[2] - 5);
					}	
					catch (ArrayIndexOutOfBoundsException ex) {}
				}
				BoardSpace examined = this.game.getLocation(BL, c, r);
				boolean ghst = examined.isGhostEffect();
				if (ghst) {
					drawMe.setColor(npcColor);
					drawMe.setFont(boardNumberFont);
					drawMe.drawString("g", xdraw[0] + 2, ydraw[0] + 60);
				}
			}
		} // end of for loop that draws all of the spaces
		
		if (BL == 4) { // draw the glorification space
			int[] xdrawA = getSquareCoordinates(0, 0, squareScale, 0, true, false);
			int[] ydrawA = getSquareCoordinates(0, 0, squareScale, 0, false, true);
			drawMe.setColor(Color.YELLOW);
			drawMe.fillPolygon(xdrawA, ydrawA, 4);
			drawMe.setColor(spaceNumbers);
			drawMe.setFont(boardNumberFont);
			drawMe.drawString("A", xdrawA[0] + 5, ydrawA[3] - 5);
			// i need to check if someone is on this space or on space below, and draw them.
			int ascender = -1;
			ascender = this.game.getLocation(5, 0, 0).getOccupiedBy();
			if (ascender != -1) {
				Brawler jimbo = this.game.getBrawler(ascender);
				drawCharacterSketch(this.game, drawMe, jimbo.getPieceID(), xdrawA[0], ydrawA[0], sketchScale, squareScale);
			}
		}
		// draw the pool of off-board brawlers
		drawMe.setColor(Color.BLACK);
		int[] xdrawP = {1000, 1375, 1375, 1000};
		int[] ydrawP = {350, 350, 750, 750};
		drawMe.fillPolygon(xdrawP, ydrawP, 4);
		int k = 0;
		
		for (int i = 0; i <= 5; i++) {
			for (int j = 0; j <= 3; j++) {
				int xdrawB = 1025 + (85 * j);
				int ydrawB = 385 + (70 * i);
				String LNo = this.brwlrs[k];
				Brawler jimbo = this.game.getBrawler(k);
				if (!jimbo.isOnBoard()) {
					if (jimbo.isGlorified()) drawMe.setFont(glorified);
					else drawMe.setFont(brawlerFont);
					drawMe.setColor(setCorrectColor(jimbo));
					drawMe.drawString(LNo, xdrawB, ydrawB);
				}
				k++;
			}
		} 
	}	// end of PaintComponent()
	
	private Color setCorrectColor(Brawler jimbo) {
		Color drawThis = Color.BLACK;
		if (jimbo.isRed()) drawThis = Color.RED;
		if (jimbo.isBlue()) drawThis = Color.BLUE;
		if (jimbo instanceof Nemesis) drawThis = nemesisColor;
		if (jimbo instanceof Monster) drawThis = monsterColor;
		if (jimbo instanceof NPC) drawThis = npcColor;
		if (jimbo instanceof Treasure) drawThis = treasureColor;
		return drawThis;
	}
	
	public void setDTBL(int x) {
		this.dtBL = x;
	}
	
} // end of drawboard class	
