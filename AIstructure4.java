/**
 * AIstructure4 is intended to be a multithreaded version of AIstructure2.
 */
package rpgGridBrawl;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AIstructure4 {
	private AInode node;
	private boolean[] onSwitches;
	private static AIresult masterDecision;
	private int[] currentScores;
	private double currentAdvantage;
	private static Scoring scorer;
	private final String nodeString;
	
	public AIstructure4(String ns, ScoringProfile sprof) {
		node = new AInode(ns);
		nodeString = ns;
		node = setNodeArtifacts(node);
		scorer = new Scoring(nodeString, sprof);
		if (node.getRound() < 11) {
			this.onSwitches = setSQOSwitches(node);
			currentScores = scorer.squareOffScore();
		}
		else {
			this.onSwitches = setBrawlSwitches(node);
			currentScores = scorer.brawlScore();
		}
		currentAdvantage = scorer.calculateAdvantage(currentScores);
	}
	
	public AInode getNode() { return this.node; }
	
	public AIresult makeLocalDecision() throws FileNotFoundException, InterruptedException, ExecutionException {
		int threads = 0;
		for (int t = 0; t < 24; t++) if (this.onSwitches[t]) threads++;
		double bestRunningAdvantage = currentAdvantage;
		AIresult[] futures = new AIresult[24];
		for (int k = 0; k < 24; k++) futures[k] = new AIresult();
		boolean gotInitial = false, goodResult = false;
		double rando = 0.1;
		do {
			ExecutorService pool = Executors.newFixedThreadPool(threads);
			for (int m = 0; m < 24; m++) {
				if (this.onSwitches[m]) {
					Future<AIresult> tester = pool.submit(new AIthread(nodeString, m, node.isRedsTurn(), threads));
					futures[m] = tester.get();
				}
				else futures[m].turnOff();
			}
			pool.shutdown();
			pool.awaitTermination(1000, TimeUnit.NANOSECONDS);
			boolean bestRedPlace, bestBluePlace, doIndeedGoForIt;
			boolean goForIt = (Math.random() < rando);
			for (int n = 0; n < 24; n++) {
				if (futures[n].isSwitchedOn()) {
					bestRedPlace = (node.isRedsTurn() && (futures[n].getBestAdv() > bestRunningAdvantage));
					bestBluePlace = (!node.isRedsTurn() && (futures[n].getBestAdv() < bestRunningAdvantage));
					doIndeedGoForIt = (goForIt && (futures[n].getBestAdv() == bestRunningAdvantage));
					if (bestRedPlace || bestBluePlace || doIndeedGoForIt || !gotInitial) {
						gotInitial = true;
						masterDecision = futures[n];
						bestRunningAdvantage = futures[n].getBestAdv();
					}
					if (masterDecision.getAction() != 0) goodResult = true;
				}
			}
			if (!goodResult) rando += .05;
		} while(!goodResult);
		return masterDecision;
	}
	
	public static boolean[] setSQOSwitches(AInode seed) { 
		boolean[] swtchs = new boolean[24];
		
		for (int k = 0; k < 24; k++) {
			Brawler bb = seed.getBrawler(k);
			swtchs[k] = true;
			if (bb.isOnBoard() || bb instanceof Nemesis) swtchs[k] = false;
			else if ((seed.isRedsTurn() && bb.isBlue()) || (!seed.isRedsTurn() && bb.isRed())) swtchs[k] = false;
		}
		int chOnBrd = 0;
		if (seed.isRedsTurn()) {
			for (int r = 0; r <= 3; r++) if (seed.getBrawler(r).isOnBoard()) chOnBrd++;
		}
		else {
			for (int b = 4; b <= 7; b++) if (seed.getBrawler(b).isOnBoard()) chOnBrd++;
		}
		if ((seed.getRound() + (4 - chOnBrd)) > 10) {
			for (int x = 8; x <= 23; x++) swtchs[x] = false; // keeps the AI from procrastinating.
		}
		return swtchs;
	}	
	
	public boolean[] setBrawlSwitches(AInode seed) {
		boolean[] switches = new boolean[24];
		for (int m = 0; m < 24; m++) {
			Brawler dd = seed.getBrawler(m);
			if (seed.getRedLU() == m) switches[m] = false;
			else if (seed.getBlueLU() == m) switches[m] = false;
			else if ((seed.isRedsTurn() && dd.isBlue()) || (!seed.isRedsTurn() && dd.isRed())) switches[m] = false;
			else if (!seed.canRedPlace() && !dd.isOnBoard() && seed.isRedsTurn()) switches[m] = false;
			else if (!seed.canBluePlace() && !dd.isOnBoard() && !seed.isRedsTurn()) switches[m] = false;
			else if (dd.isOnBoard() && seed.getLocation(dd.getLoc()).isGhostEffect()) switches[m] = false;
			else if (dd instanceof Rogue && (seed.getBrawler("Sentinel").getFloor() == dd.getFloor())) switches[m] = false;
			else switches[m] = true;
		}
		return switches;
	}
	
	private static AInode setNodeArtifacts(AInode fixNode) {
		Brawler ghost = fixNode.getBrawler("Ghost");
		if (!ghost.isOnBoard()) fixNode.removeEffects(ghost.getPieceID());
		else fixNode.addGhostEffect(ghost.getFloor(), ghost.getColumn(), ghost.getRow());
		Brawler leper = fixNode.getBrawler("Leper");
		if (!leper.isOnBoard()) fixNode.removeEffects(leper.getPieceID());
		else fixNode.addLeperOrSentinelEffect(leper.getFloor(), 'L');
		Brawler sentinel = fixNode.getBrawler("Sentinel");
		if (!sentinel.isOnBoard()) fixNode.removeEffects(sentinel.getPieceID());
		else fixNode.addLeperOrSentinelEffect(sentinel.getFloor(), 'S');
		return fixNode;
	}
} // end of AIstructure4 class
