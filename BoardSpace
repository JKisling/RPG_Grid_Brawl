package rpgGridBrawl;

class BoardSpace {
	private final int column, row, floor, locationID;
	private int occupiedBy;
	private boolean occupied, leperEffect, sentinelEffect, ghostEffect;
		
	public BoardSpace(int floorInput, int columnInput, int rowInput) {
		this.floor = floorInput;
		this.column = columnInput;
		this.row = rowInput;
		this.locationID = ((100 * this.floor) + (10 * this.column) + this.row);
		this.occupied = false;
		this.leperEffect = false;
		this.sentinelEffect = false;
		this.occupiedBy = -1;
	}
	
	public int getFloor() { return this.floor; }
	public int getColumn() { return this.column; }
	public int getRow() { return this.row; }
	public int getLocationID() { return this.locationID; }
	public int getOccupiedBy() { return this.occupiedBy; }
	public boolean isGhostEffect() { return this.ghostEffect; }
	public boolean isLeperEffect() { return this.leperEffect; }
	public boolean isOccupied() { return this.occupied; }
	public boolean isSentinelEffect() { return this.sentinelEffect; }
	
	public void removeBrawler() {
		this.setOccupied(false);
		this.setOccupiedBy(-1);
	}
	
	public void setGhostEffect(boolean state) { this.ghostEffect = state; }
	public void setLeperEffect(boolean state) { this.leperEffect = state; }
	public void setOccupied(boolean state) { this.occupied = state; }
	public void setOccupiedBy(int pieceID) { this.occupiedBy = pieceID; }
	public void setSentinelEffect(boolean state) { this.sentinelEffect = state; }
	
}
