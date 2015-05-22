package rpgGridBrawl;

interface Character {
	final boolean moveable = true;
}

interface Nemesis {
	final int level = 4;
	final boolean moveable = true;
}

interface Monster {
	final boolean moveable = true;
}

interface NPC {
	final int level = 0;
	final boolean moveable = true;
}

interface Treasure {
	final int level = 0;
	final boolean moveable = false;
}


