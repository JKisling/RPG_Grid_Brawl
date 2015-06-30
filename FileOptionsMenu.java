package rpgGridBrawl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JFileChooser;

class FileOptionsMenu extends GridBrawl {
	private String display;
	public final int areYouSure = 1;
	public FileOptionsMenu() {
		this.display = "Please select from among the following options:\n" +
				"0. Go back to previous menu\n" +
				"1. Create a new game\n" +
				"2. Load a saved game\n" +
				"3. Save current game\n" +
				"4. Generate a random mayhem\n" +
				"5. Generate a random regular brawl\n" +
				"6. Get help or consult the rules\n" +
				"7. Change AI options\n" +
				"8. Exit game\n\n" + 
				"Your selection: ";
	}
		
	public GameData execute(GameData crtOld) {
		GameData crtNew = new GameData();
		boolean confirmedAction = false;
		
		interact.tellPlayer(this.display, true);
		int input = interact.getFileOptionsMenuSelection();
		switch(input) {
		case 0: return crtOld;	// return to previous menu
		case 1: // new game
			confirmedAction = interact.confirm();
			if (confirmedAction) crtNew = this.newGame();
			else return crtOld;
			break;
		case 2: // load game
			confirmedAction = interact.confirm();
			if (confirmedAction) crtNew = this.loadGame();
			else return crtOld;
			break;
		case 3: // save game
			saveGame(crtOld);
			return crtOld;
		case 4: // random mayhem
			confirmedAction = interact.confirm();
			if (confirmedAction) crtNew = this.newRandomGame(crtOld, true);
			else return crtOld;
			break;
		case 5: // random regular brawl
			confirmedAction = interact.confirm();
			if (confirmedAction) crtNew = this.newRandomGame(crtOld, false);
			else return crtOld;
			break;
		case 6:
			Help help = new Help();
			help.execute();
			break;
		case 7: // AI Options submenu
			crtNew = AIoptionsMenu(crtOld);
			break;
		case 8: System.exit(0);
			break;
		}	
		crtNew.setSuccessfulTurn(false); // successfulTurn is only set to true when a player makes a move in the game.
		return crtNew;
	}
	
	public void exitGame() {
		System.exit(0);
	}
		
	public String getDisplay() {
		return this.display;
	}
	
	public GameData loadGame() {
		Scanner frank = new Scanner(System.in);
		JFileChooser chooser = new JFileChooser();
		File f = new File("../RPG Grid Brawl");
		File gameToLoad = new File("");
		int checker;
		chooser.setCurrentDirectory(f);
		chooser.setDialogTitle("Select a game file to load.");
		checker = chooser.showOpenDialog(null);
		if (checker == JFileChooser.APPROVE_OPTION) {
			// File nameDir = chooser.getCurrentDirectory();
			chooser.getCurrentDirectory();
			gameToLoad = chooser.getSelectedFile();
		}
		else {
			System.out.println("Okay, bye then.");
			System.exit(0);
		}
		String loadData = "";
		GameData GM = new GameData();
		frank.close();
		try {
			frank = new Scanner(gameToLoad);
			loadData = frank.next();
			GM = new GameData(loadData);
			frank.close();
		}
		catch (FileNotFoundException fNF) {
			interact.tellPlayer(1, 5, false); // "File Not Found!"
			interact.space();
			GM = newGame();
		}
		return GM;
	}
	
	public GameData newGame() {
		GameData crtGame = new GameData();
		String[] names = interact.getNames();
		crtGame.getConfig().setGameName(names[0]);
		crtGame.getConfig().setRedPlayerName(names[1]);
		crtGame.getConfig().setBluePlayerName(names[2]);
		return crtGame;
	}
	
	public GameData newRandomGame(GameData crt, boolean mayhem) {
		double rando = 2.5; // arbitrary--this is just to signal the correct GameData constructor
		GameData randoGame = new GameData();
		if (mayhem) randoGame = new GameData(rando);
		else randoGame = new GameData(0, true);
		String[] names = interact.getNames();
		randoGame.getConfig().setGameName(names[0]);
		randoGame.getConfig().setRedPlayerName(names[1]);
		randoGame.getConfig().setBluePlayerName(names[2]);
		interact.space();
		if (mayhem) {
			interact.tellPlayer(1, 3, false); // "The game has been reset to round 11, and all brawlers are on the board..."
			interact.tellPlayer(1, 4, false); // "RANDOMIZED FOR MAYHEM..."
		}
		else interact.tellPlayer(1, 8, false); // "The game has been reset to round 11, with a random regular Square-off."
		interact.space();
		return randoGame;
	}
	
	
	public static void saveGame(GameData gameToSave) {
		boolean proceed = false;
		String saveFile = gameToSave.getConfig().getGameName() + ".txt";
		String saveData = gameToSave.toString();
		File fileObject = new File(saveFile);
		if (fileObject.exists()) {
			interact.tellPlayer(1, 6, true); // "Save game already exists, would you like to overwrite it?");
			proceed = interact.getYesOrNo();
			if(proceed) {
					fileObject.delete();
					fileObject = new File(saveFile);
			}	
		}
		else proceed = true;
		if (proceed) {
			try {
				PrintWriter output = new PrintWriter(fileObject);
				output.println(saveData);
				interact.tellPlayer(1, 7, false); // "Game saved successfully."
				interact.space();
				output.close();
			}
			catch(FileNotFoundException fNF) {
				// not sure if anything is needed here.
			}
		}	
	}
	
	public void setDisplay(String y) {
		this.display = y;
	}
	
	public static GameData AIoptionsMenu(GameData crt) {
		String redAIOpt, blueAIOpt;
		if(crt.getConfig().isAIRed()) redAIOpt = "ON";
		else redAIOpt = "OFF";
		if(crt.getConfig().isAIBlue()) blueAIOpt = "ON";
		else blueAIOpt = "OFF";
		interact.tellPlayer(interact.reportOnAIOptionMenu(redAIOpt, blueAIOpt), true);
		int aios = interact.getAIOptionMenuSelection();
		if (aios == -1) return crt;
		while (aios != -1) {
			if (aios == 1) {
				if(crt.getConfig().isAIRed()) {
					crt.getConfig().setAIRed(false);
					redAIOpt = "OFF";
				}
				else {
					crt.getConfig().setAIRed(true);
					redAIOpt = "ON";
				}
			}
			else if (aios == 2) {
				if(crt.getConfig().isAIBlue()) {
					crt.getConfig().setAIBlue(false);
					blueAIOpt = "OFF";
				}
				else {
					crt.getConfig().setAIBlue(true);
					blueAIOpt = "ON";
				}
			}
			if (aios != -1) {
				interact.tellPlayer(interact.reportOnAIOptionMenu(redAIOpt, blueAIOpt), true);
				aios = interact.getAIOptionMenuSelection();
			}
		}
		return crt;
	}
	
} // end of FileOptionsMenu class
