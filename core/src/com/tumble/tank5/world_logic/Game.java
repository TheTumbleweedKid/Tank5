package com.tumble.tank5.world_logic;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.events.Event;
import com.tumble.tank5.game_object.Move;
import com.tumble.tank5.inputs.Input;
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

	private int gameID;
	// Used to make sure this Game is not considered equal to a Game that has
	// already been registered under a gameID of 0 with the IDManager before it has
	// been registered.
	private boolean isRegistered = false;

	private int minPlayers;

	private Set<Entity> entities;
	private GameWorld world;

	private long startTime;
	private boolean started = false;

	private Round round;
	private int tickNumber;
	
	private Map<Entity, Move> moves;

	private PriorityQueue<Event> events;

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
			throw new GameError("A Game must require at least one Player to start (" + minPlayers + " is invalid)!");
		}

		this.minPlayers = minPlayers;

		IDManager.registerGame(this);

		world = new GameWorld();
		
		events = new PriorityQueue<Event>();
	}
	
	/**
	 * Sets the <code>Game</code>'s <code>gameID</code> to a unique number assigned
	 * by the <code>IDManager</code>. Should only be called once, during the
	 * construction of this <code>Game</code> object, by
	 * {@link IDManager#registerGame(Game)}.
	 * 
	 * @param id - the unique <code>int</code> to use as this <code>Game</code>'s
	 *           identifying number.
	 * 
	 * @throws GameError if this method is called more than once on this
	 *                   <code>Game</code>.
	 */
	public void setGameID(int id) {
		if (isRegistered) {
			throw new GameError("Cannot re-set a Game's ID number once set!");
		}
		
		gameID = id;
		isRegistered = true;
	}

	/**
	 * Attempts to start the <code>Game</code>, using the parameters to define the
	 * sine wave of <code>Round</code> durations by constructing the first
	 * <code>Round</code> with them.
	 * 
	 * @param baseRoundDuration - the mean duration of each <code>Round</code> in a
	 *                          full cycle.
	 * 
	 * @param amplitude
	 * 
	 * @param startingPhase     - the phase shift of the first <code>Round</code> in
	 *                          the series.
	 * 
	 * @param period            - the number of <code>Round</code>s in a cycle
	 *                          (i.e., how many should pass before the same duration
	 *                          re-occurs).
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
		if (i == null)
			return false;

		if (!started) {
			// accept special inputs (add player, player-quit, NPC-spawn, etc.).
			return false;
		}

		return round.shouldAccept(i) && i.apply(this);
	}

	public Map<Entity, Move> getMoves() {
		return moves;
	}

	public void update() {
		if (round.isFinished()) {
			if (!events.isEmpty()) {
				events.peek().apply(world, tickNumber, events);
				
				if (events.peek().isFinished()) events.poll();
				
				tickNumber++;
			} else {
				round = round.next();
				round.start();
				// Note: events has already been emptied!
				tickNumber = 0;
			}
		}
	}

	/**
	 * Gets the <code>GameWorld</code> owned by this <code>Game</code>.
	 * 
	 * @return the <code>Game</code>'s <code>GameWorld</code>.
	 */
	public GameWorld getWorld() {
		// Is there a better way of doing this?
		return world;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass())
			return false;

		Game other = (Game) obj;

		return gameID == other.gameID;// && isRegistered == other.isRegistered;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int hash = getClass().hashCode();

		hash = hash * prime + gameID;
		return hash;
	}
}
