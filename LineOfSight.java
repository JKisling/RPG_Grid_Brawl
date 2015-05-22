package rpgGridBrawl;

public class LineOfSight extends GridBrawl {
	Brawler aaa, bbb;
	GameData game;
	
	public LineOfSight() {}
	
	public boolean calculate(GameData crt, Brawler aa, Brawler bb) {
		aaa = aa;	bbb = bb;	game = crt;
		boolean answer = false;
		boolean backslash = false;
		int distance = 0;
		if (aaa.getFloor() != bbb.getFloor() || aaa.getPieceID() == bbb.getPieceID()) return false;
		if (aaa.getColumn() == bbb.getColumn()) {
			distance = Math.abs(aaa.getRow() - bbb.getRow());
			if (distance == 1) return true; // they are adjacent in the same column
			answer = checkRow(game, aaa, bbb, distance);
		}
		else if (aaa.getRow() == bbb.getRow()) {
			distance = Math.abs(aaa.getColumn() - bbb.getColumn());
			if (distance == 1) return true; // they are adjacent in the same row
			answer = checkColumn(game, aaa, bbb, distance);
		}
		else if ((aaa.getRow() + aaa.getColumn()) == (bbb.getRow() + bbb.getColumn())) {
			distance = Math.abs(aaa.getColumn() - bbb.getColumn());
			if (distance == 1) return true; // they are adjacent in the same forward diagonal
			answer = checkForwardDiagonal(game, aaa, bbb, distance);
		}
		else {
			for (int j = 1; j <= 4; j++) { // this is how we find brawlers on the back slash diagonal
				if (aaa.getRow() - j == bbb.getRow() && aaa.getColumn() - j == bbb.getColumn()) backslash = true;
				else if ((aaa.getRow() + j == bbb.getRow()) && (aaa.getColumn() + j == bbb.getColumn())) backslash = true;
			}
			if (backslash) {
				distance = Math.abs(aaa.getColumn() - bbb.getColumn());
				if (distance == 1) return true; // they are adjacent in the same back diagonal
				answer = checkBackSlash(game, aaa, bbb, distance);
			}
		}
		return answer;
	}
	
	private static boolean checkBackSlash(GameData crt, Brawler aaa, Brawler bbb, int distance) {
		Brawler upperLeft;
		if (aaa.getColumn() < bbb.getColumn()) upperLeft = aaa;
		else upperLeft = bbb;
		for (int bd = 1; bd < distance; bd++) {
			boolean occupado = (crt.getLocation(upperLeft.getFloor(), upperLeft.getColumn() + bd, upperLeft.getRow() + bd).isOccupied());
			if (occupado) return false; // board space in between aaa and bbb is occupied by a different piece, therefore no LOS
		}
		return true;
	}
	
	private static boolean checkColumn(GameData crt, Brawler aaa, Brawler bbb, int distance) {
		Brawler left;
		if (aaa.getColumn() < bbb.getColumn()) left = aaa;
		else left = bbb;
		for (int c = 1; c < distance; c++) {
			
			
			// implement this for the rest of them, try to avoid that null pointer
			// maybe it doesn't matter?
			
			Boolean occupado = crt.getLocation(left.getFloor(), left.getColumn() + c, left.getRow()).isOccupied();
			if (occupado) return false; // board space in between aaa and bbb is occupied by a different piece, therefore no LOS
		}
		return true;
	}
	
	private static boolean checkForwardDiagonal(GameData crt, Brawler aaa, Brawler bbb, int distance) {
		Brawler lowerLeft;
		if (aaa.getColumn() < bbb.getColumn()) lowerLeft = aaa;
		else lowerLeft = bbb;
		for (int fd = 1; fd < distance; fd++) {
			boolean occupado = (crt.getLocation(lowerLeft.getFloor(), lowerLeft.getColumn() + fd, lowerLeft.getRow() - fd).isOccupied());
			if (occupado) return false; // board space in between aaa and bbb is occupied by a different piece, therefore no LOS
		}
		return true;
	}
	
	private static boolean checkRow(GameData crt, Brawler aaa, Brawler bbb, int distance) {
		Brawler top;
		if (aaa.getRow() < bbb.getRow()) top = aaa;
		else top = bbb;
		for (int r = 1; r < distance; r++) {
			boolean occupado = (crt.getLocation(top.getFloor(), top.getColumn(), top.getRow() + r).isOccupied());
			if (occupado) return false; // board space in between aaa and bbb is occupied by a different piece, therefore no LOS
		}
		return true;
	}	
	
	public boolean AIcalculate(AInode crt, Brawler aa, Brawler bb) {
		aaa = aa;	bbb = bb;
		boolean answer = false;
		boolean backslash = false;
		int distance = 0;
		if (aaa.getFloor() != bbb.getFloor() || aaa.getPieceID() == bbb.getPieceID()) return false;
		if (aaa.getColumn() == bbb.getColumn()) {
			distance = Math.abs(aaa.getRow() - bbb.getRow());
			if (distance == 1) return true; // they are adjacent in the same column
			answer = AIcheckRow(crt, aaa, bbb, distance);
		}
		else if (aaa.getRow() == bbb.getRow()) {
			distance = Math.abs(aaa.getColumn() - bbb.getColumn());
			if (distance == 1) return true; // they are adjacent in the same row
			answer = AIcheckColumn(crt, aaa, bbb, distance);
		}
		else if ((aaa.getRow() + aaa.getColumn()) == (bbb.getRow() + bbb.getColumn())) {
			distance = Math.abs(aaa.getColumn() - bbb.getColumn());
			if (distance == 1) return true; // they are adjacent in the same forward diagonal
			answer = AIcheckForwardDiagonal(crt, aaa, bbb, distance);
		}
		else {
			for (int j = 1; j <= 4; j++) { // this is how we find brawlers on the back slash diagonal
				if (aaa.getRow() - j == bbb.getRow() && aaa.getColumn() - j == bbb.getColumn()) backslash = true;
				else if ((aaa.getRow() + j == bbb.getRow()) && (aaa.getColumn() + j == bbb.getColumn())) backslash = true;
			}
			if (backslash) {
				distance = Math.abs(aaa.getColumn() - bbb.getColumn());
				if (distance == 1) return true; // they are adjacent in the same back diagonal
				answer = AIcheckBackSlash(crt, aaa, bbb, distance);
			}
		}
		return answer;
	}
	
	private static boolean AIcheckBackSlash(AInode crt, Brawler aaa, Brawler bbb, int distance) {
		Brawler upperLeft;
		BoardSpace[][][] gameBoard = crt.getBoard();
		if (aaa.getColumn() < bbb.getColumn()) upperLeft = aaa;
		else upperLeft = bbb;
		for (int bd = 1; bd < distance; bd++) {
			boolean occupado = (gameBoard[upperLeft.getFloor()][upperLeft.getColumn() + bd][upperLeft.getRow() + bd].isOccupied());
			if (occupado) return false; // board space in between aaa and bbb is occupied by a different piece, therefore no LOS
		}
		return true;
	}
	
	private static boolean AIcheckColumn(AInode crt, Brawler aaa, Brawler bbb, int distance) {
		Brawler left;
		BoardSpace[][][] gameBoard = crt.getBoard();
		if (aaa.getColumn() < bbb.getColumn()) left = aaa;
		else left = bbb;
		for (int c = 1; c < distance; c++) {
			BoardSpace examined = gameBoard[left.getFloor()][left.getColumn() + c][left.getRow()];
			if (examined.isOccupied()) return false; // board space in between aaa and bbb is occupied by a different piece, therefore no LOS
		}
		return true;
	}
	
	private static boolean AIcheckForwardDiagonal(AInode crt, Brawler aaa, Brawler bbb, int distance) {
		Brawler lowerLeft;
		BoardSpace[][][] gameBoard = crt.getBoard();
		if (aaa.getColumn() < bbb.getColumn()) lowerLeft = aaa;
		else lowerLeft = bbb;
		for (int fd = 1; fd < distance; fd++) {
			boolean occupado = (gameBoard[lowerLeft.getFloor()][lowerLeft.getColumn() + fd][lowerLeft.getRow() - fd].isOccupied());
			if (occupado) return false; // board space in between aaa and bbb is occupied by a different piece, therefore no LOS
		}
		return true;
	}
	
	private static boolean AIcheckRow(AInode crt, Brawler aaa, Brawler bbb, int distance) {
		Brawler top;
		BoardSpace[][][] gameBoard = crt.getBoard();
		if (aaa.getRow() < bbb.getRow()) top = aaa;
		else top = bbb;
		for (int r = 1; r < distance; r++) {
			
			boolean occupado = (gameBoard[top.getFloor()][top.getColumn()][top.getRow() + r].isOccupied());
			if (occupado) return false; // board space in between aaa and bbb is occupied by a different piece, therefore no LOS
		}
		return true;
	}	
} // end of LineOfSight class
