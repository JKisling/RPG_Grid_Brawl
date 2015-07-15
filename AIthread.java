package rpgGridBrawl;

import java.util.concurrent.Callable;

public class AIthread implements Callable<AIresult> {
	private final String nodeString;
	private final int id;
	private final boolean redsTurn;
	
	public AIthread(String ns, int i, boolean rt) {
		this.nodeString = ns;
		this.id = i;
		this.redsTurn = rt;
	}
	
	public AIresult call() throws InterruptedException {
		AIsensors sensor = new AIsensors(this.nodeString);
		AIresult result = sensor.bestActionByBrawler(id, redsTurn);
		result.turnOn();
		if (result.getAction() == 0) result.turnOff();
		return result;
	}
}
