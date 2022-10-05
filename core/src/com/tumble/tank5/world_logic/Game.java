package com.tumble.tank5.world_logic;

import java.util.Set;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.events.Input;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.util.IDManager;

/**
 * 
 * @author Tumbl
 *
 */
public class Game {
	/**
	 * Will be <code>true</code> if this is a server-side <code>Game</code>, or
	 * <code>false</code> if it is client-side.
	 */
	public final boolean isServer;
	
	private final int gameId;
	
	private int minPlayers;
	
	private Set<Entity> entities;
	private GameWorld world;
	
	private long startTime;
	private boolean started = false;

	private Round round;
	
	/**
	 * Constructs a new <code>Game</code> (without loading a <code>GameWorld</code>
	 * or any of the <code>Player</code>s), and registers it with the
	 * <code>IDManager</code>. Note that this call of
	 * {@link IDManager#registerGame(Game)} cannot cause a <code>GameError</code> to
	 * be thrown, since the new <code>Game</code> instance can't be registered yet,
	 * and certainly isn't <code>null</code>!
	 * 
	 * @param isServer   - whether this is a server-side <code>Game</code> or not.
	 * 
	 * @param minPlayers - the minimum number of <code>Player</code>s the
	 *                   <code>Game</code> should wait for to join before allowing
	 *                   it to be started. Must be > 0.
	 * 
	 * @throws GameError if <code>minPlayers</code> <= 0.
	 */
	public Game(boolean isServer, int minPlayers) {
		this.isServer = isServer;
		
		if (minPlayers <= 0) {
			throw new GameError("A Game must require at least one Player to start ("
					+ minPlayers + " is invalid)!");
		}
		
		this.minPlayers = minPlayers;
		
		gameId = IDManager.registerGame(this);
	}
	
	/**
	 * Attempts to start the <code>Game</code>, using the parameters to define the
	 * sine wave of <code>Round</code> durations by constructing the first
	 * <code>Round</code> with them.
	 * 
	 * @param baseRoundDuration - the mean duration of each <code>Round</code> in a full cycle.
	 * 
	 * @param amplitude
	 * 
	 * @param startingPhase - the phase shift of the first <code>Round</code> in the series.
	 * 
	 * @param period - the number of <code>Round</code>s in a cycle (i.e., how many should pass before the same duration re-occurs).
	 * 
	 * @see Round#Round(int, int, int, int) for full parameter T's & C's.
	 * 
	 * @return
	 */
	public boolean start(int baseRoundDuration, int amplitude, int startingPhase, int period) {
		try {
			round = new Round(baseRoundDuration, amplitude, startingPhase, period);
			round.start();
			
			startTime = System.currentTimeMillis();
			started = true;
			return true;
		} catch (GameError e) {
			System.out.println(round);
			return false;
		}
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean addInput(Input i) {
		if (i == null) return false;
		
		if (!started) {
			// accept special inputs (add player, player-quit, NPC-spawn, etc.).
			return false;
		}
		
		return round.shouldAccept(i) && i.apply(world);
	}
	
	public void update() {
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) return false;
		
		Game other = (Game) obj;
		
		return gameId == other.gameId;
	}
	
	@Override
	public int hashCode() {
		int prime = 37;
		int hash = getClass().hashCode();
		
		hash = hash * prime + gameId;
		
		return hash;
	}
}
