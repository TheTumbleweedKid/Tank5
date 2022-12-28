package com.tumble.tank5.world_logic.game_n_world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.tumble.tank5.events.DeathEvent;
import com.tumble.tank5.events.Event;
import com.tumble.tank5.events.MovementEvent;
import com.tumble.tank5.events.ReloadEvent;
import com.tumble.tank5.events.SwitchWeaponEvent;
import com.tumble.tank5.events.TriggerPullEvent;
import com.tumble.tank5.game_object.entities.Action;
import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.game_object.entities.Player;
import com.tumble.tank5.game_object.entities.Action.ActionType;
import com.tumble.tank5.inputs.Input;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.util.IDManager;
import com.tumble.tank5.util.Pair;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.GodEntity;
import com.tumble.tank5.world_logic.MapData;
import com.tumble.tank5.world_logic.Round;
import com.tumble.tank5.util.DirectionVector;

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
	
	private GodEntity god;

	private int minPlayers, playerCount;
	
	private GameWorld world;
	
	public enum Phase {
		ACCEPTANCE,
		PATIENCE,
		ENACTMENT,
		PLAY_PAUSED
	}
	
	private Phase phase;
	private long patienceStart;

	private long startTime;
	private boolean started = false;

	private Round round;
	private int patienceWait, tickNumber;
	
	private Map<Entity, DirectionVector> moves;
	private Map<Entity, Action> actions;

	private PriorityQueue<Event> events;

	/**
	 * Constructs a new <code>Game</code> (without loading a <code>GameWorld</code>
	 * or any of the <code>Player</code>s), and registers it with the
	 * <code>IDManager</code>. Note that this call of
	 * {@link IDManager#registerGame(Game)} cannot cause a <code>GameError</code> to
	 * be thrown, since the new <code>Game</code> instance can't be registered yet,
	 * and certainly isn't <code>null</code>!
	 * 
	 * @param isServer     - whether this is a server-side <code>Game</code> or not.
	 * 
	 * @param minPlayers   - the minimum number of <code>Player</code>s the
	 *                     <code>Game</code> should wait for to join before allowing
	 *                     it to be started. Must be > 0.
	 * 
	 * @throws GameError if <code>minPlayers</code> <= 0.
	 */
	public Game(boolean isServer, int minPlayers) {
		this.isServer = isServer;

		if (minPlayers <= 0) {
			throw new GameError("A Game must require at least one Player to start (" + minPlayers + " is invalid)!");
		}

		this.minPlayers = minPlayers;
		playerCount = 0;
		
		IDManager.registerGame(this);
		
		god = new GodEntity(IDManager.nextID(this), this);

		world = new GameWorld();
		
		phase = Phase.PLAY_PAUSED;
		
		moves = new HashMap<Entity, DirectionVector>();
		actions = new HashMap<Entity, Action>();
		
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
	 * @param amplitude         - the maximum variation (positive or negative) from
	 *                          the <code>baseRoundDuration</code>.
	 * 
	 * @param startingPhase     - the phase shift of the first <code>Round</code> in
	 *                          the series.
	 * 
	 * @param period            - the number of <code>Round</code>s in a cycle
	 *                          (i.e., how many should pass before the same duration
	 *                          re-occurs).
	 * 
	 * @param patienceWait      - how long to wait between the
	 *                          {@link Phase#ACCEPTANCE} and {@link Phase#ENACTMENT}
	 *                          phases in a round (for <code>Input</code>s and
	 *                          <code>Event</code>s to arrive over the network). May
	 *                          be any <code>int</code>; values < 0 have the same
	 *                          effect as a value of 0 (no waiting <i>at all</i> -
	 *                          not even waiting for the next call of the
	 *                          {@link Game#update() method} - useful for testing).
	 * 
	 * @see Round#Round(int, int, int, int) for full <code>Round</code>-parameter
	 *      T's & C's.
	 * 
	 * @return <code>true</code> if the <code>Game</code> was successfully started,
	 *         or <code>false</code> if it was already in progress, doesn't have
	 *         enough <code>Player</code>s joined to start, or has been given
	 *         invalid <code>Round</code> parameters.
	 */
	public boolean start(
			int baseRoundDuration,
			int amplitude,
			int startingPhase,
			int period,
			int patienceWait) {
		if (playerCount < minPlayers) return false;
		
		try {
			round = new Round(baseRoundDuration, amplitude, startingPhase, period);
			round.start(this);
			
			phase = Phase.ACCEPTANCE;
			
			moves.clear();
			actions.clear();
			
			events.clear();
			
			tickNumber = 0;

			startTime = System.currentTimeMillis();
			started = true;
			return true;
		} catch (GameError e) {
			System.out.println("Can't start Game with bad Round data: " + e.getMessage());
			return false;
		}
	}

	public boolean isStarted() {
		return started;
	}
	
	public long getStartTime() {
		return started ? startTime : -1;
	}
	
	public boolean loadMap(MapData mD) {
		if (phase != Phase.PLAY_PAUSED) return false;
		return world.loadWorld(mD);
	}
	
	public boolean addEntity(Entity entity, Position spawnAt) {
		if (phase != Phase.PLAY_PAUSED) return false;
		
		if (world.spawnEntity(entity, spawnAt)) {
			actions.put(entity, new Action(ActionType.NONE));
			
			if (entity instanceof Player) playerCount += 1;
			
			return true;
		}
		
		return false;
	}
	
	public boolean smiteEntity(Entity entity) {
		if (entity == null || !world.hasEntity(entity)) return false;
		
		if (entity instanceof Player) playerCount--;
		
		events.add(
				new DeathEvent(
						tickNumber,
						entity,
						god));
		return true;
	}

	public boolean addInput(Input i) {
		if (i == null)
			return false;

		if (!started || round.shouldAccept(i)) {
			// accept special inputs (add player, player-quit, NPC-spawn, etc.).
			return false;
		}

		Pair<Entity, Object> pair = i.apply(this);
		
		if (pair == null) return false;
		
		if (pair.second() instanceof DirectionVector) {
			moves.put(pair.first(), (DirectionVector) pair.second());
			return true;
		}
		
		if (pair.second() instanceof Action) {
			actions.put(pair.first(), (Action) pair.second());
			return true;
		}
		
		return false;
	}

	/**
	 * 
	 * 
	 * @param logEvents - whether to return a (potentially) non-empty
	 *                  <code>String[]</code> containing information about the
	 *                  events that were processed.
	 * 
	 * @return
	 */
	public String[] update(boolean logEvents) {
		List<String> eventsLog = new ArrayList<String>();
		
		if (round.isFinished()) {
			if (phase == Phase.ACCEPTANCE) {
				phase = Phase.PATIENCE; // Wait for extra Inputs and Events to come in.
				
				patienceStart = round.getFinishTime();
				
				world.clearDeadEntities();
				
				return eventsLog.toArray(new String[0]);
			}
			
			if (phase == Phase.PATIENCE) {
				if (System.currentTimeMillis() - patienceStart < patienceWait)
					return eventsLog.toArray(new String[0]);

				// Process first tick in this very call (won't wait for next update() call).
				phase = Phase.ENACTMENT;
				
				for (MovementEvent mE : MovementEvent.createMovementSeries(moves)) events.offer(mE);
				
				for (Entity entity : world.getEntities()) {
					// Rely on invariant that all dead Entities will be cleared up by next call of
					// update().
					switch (actions.get(entity).getType()) {
					case FIRE:
						events.offer(new TriggerPullEvent(entity, actions.get(entity).getPositions()));
						break;
					case SWITCH_WEAPON:
						events.offer(new SwitchWeaponEvent(entity));
						break;
					case RELOAD:
						events.offer(new ReloadEvent(entity));
						break;
					case NONE:
						// Do nothing - only here to avoid incomplete-switch warnings!
					}
				}
			}
			
			if (phase == Phase.ENACTMENT) {
				if (!events.isEmpty()) {
					while (!events.isEmpty() && events.peek().tickNumber <= tickNumber) {
						events.peek().apply(world, tickNumber, events);
						
						if (events.peek().isFinished()) events.poll();
						world.cleanUp(events);
					}
					tickNumber++;
				} else {
					round = round.next();
					round.start(this);
					// Note: events has already been emptied!
					tickNumber = 0;
				}
			}
		}
		
		return eventsLog.toArray(new String[0]);
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

	public DirectionVector getMove(Entity e) {
		return moves.get(e);
	}
	
	public Action getAction(Entity e) {
		return actions.get(e);
	}
	
	public Phase getPhase() {
		return phase;
	}
	
	public int getRoundNumber() {
		if (!started || round == null) return -1;
		
		return round.roundNumber;
	}
	
	public boolean isCurrentRound(Round r) {
		if (round == null) return r == null;
		
		return round.equals(r);
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
