package com.tumble.tank5.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.tumble.tank5.world_logic.Game;

/**
 * Enforces the (hopefully) rigorous system of ID numbers for any and all
 * <code>Game</code>s that are being run in this application, as well as every
 * <code>Entity</code> in each of their <code>GameWorld</code>s. These ID
 * numbers are used to uniquely identify each <code>Entity</code> for accurate
 * networking messages (i.e., so a server-side <code>Game</code> can send a
 * message to a client to update an <code>Entity</code> using the
 * <code>Entity</code>'s server-side ID number, and know for certain that it
 * will either refer to the correct <code>Entity</code>, or instantly and
 * predictably throw an error so that the bad game-state cannot propagate).
 * 
 * @author Tumbl
 *
 */
public class IDManager {
	// The ID number to give to the next Game that gets registered with the
	// IDManager. Realistically, should never be > 2 (one client-side and
	// one server-side simultaneously per run of this application).
	private static int nextGameId = 0;
	// Keep a record of the Games that have already been registered to avoid
	// double-ups.
	private static Set<Game> alreadyRegistered = new HashSet<Game>();
	// Map from each registered Game to the next ID number to give to the next
	// Entity that gets registered under the Game.
	private static Map<Game, Integer> games = new HashMap<Game, Integer>();

	/**
	 * Hidden constructor to prevent instantiation - only the static functionality
	 * of this class should be used.
	 */
	private IDManager() {

	}

	/**
	 * Registers a newly-created <code>Game</code> with a unique ID number (to be
	 * used in the {@link Game#equals(Object)} and {@link Game#hashCode()}. A
	 * <code>Game</code> <i>must</i> be registered via this method before an
	 * <code>Entity</code> can be added to its <code>GameWorld</code>. Called in
	 * {@link Game#Game(boolean, int)}; i.e., every <code>Game</code> calls this
	 * method immediately upon instantiation.
	 * 
	 * @param game - the <code>Game</code> to register.
	 * 
	 * @return an <code>int</code> for the registered <code>Game</code> to use as
	 *         its <code>gameID</code>.
	 * 
	 * @throws GameError if the given <code>Game</code> is
	 * <code>null</code> or has already been registered with the
	 * <code>IDManager</code>.
	 */
	public static void registerGame(Game game) {
		if (game == null || alreadyRegistered.contains(game)) {
			throw new GameError("Cannot register a null or already-registered Game (" + game + ")!");
		}
		
		game.setGameID(++nextGameId);
		alreadyRegistered.add(game);
		games.put(game, 0);
	}

	/**
	 * <b>SERVER-SIDE <code>Game</code>S ONLY!</b> <br>
	 * 
	 * Generates and registers a new <code>Entity</code> ID number under a given
	 * (server-side) <code>Game</code>. Client-side <code>Game</code>s are not
	 * permitted to use this method, as they should have their <code>Entity</code>'s
	 * ID numbers given to them by their server-side versions.
	 * 
	 * @param game - the <code>Game</code> to register the new ID number under.
	 * 
	 * @return the ID number that was just generated and registered (presumably to
	 *         be used in Entity.init(), but it won't break anything if the ID
	 *         number just goes unused - aside from slowly filling up the possible
	 *         Integer values...).
	 * 
	 * @throws GameError if the <code>Game</code> is <code>null</code>,
	 * client-side or unregistered.
	 */
	public static int nextID(Game game) {
		if (game == null || !games.containsKey(game)) {
			throw new GameError("Null/unregistered Game (" + game + ") given to IDManager.nextID()!");
		}
		if (!game.isServer) {
			throw new GameError("Client-side Games are not permitted to generate their own Entity ID numbers!");
		}

		int latestID = games.get(game);
		
		games.put(game, latestID + 1);
		
		return latestID;
	}

	/**
	 * <b>CLIENT-SIDE <code>Game</code>S ONLY!</b> <br>
	 * 
	 * Registers an externally-generated ID number under a given (client-side)
	 * <code>Game</code> (received from the server-side <code>Game</code>).
	 * 
	 * @param game - the <code>Game</code> to register the external ID under.
	 * 
	 * @param id   - the ID number to register.
	 * 
	 * @throws GameError if:
	 * <li>the <code>Game</code> is <code>null</code> or unregistered</li>
	 * <li>the <code>Game</code> is on the server-side (hence should be generating
	 * its own ID numbers via {@link IDManager#nextID(Game)} rather than using
	 * externally-generated ones)</li>
	 * <li>the ID number has already been used for the <code>Game</code></li>
	 */
	public static void addExternalID(Game game, Integer id) {
		if (game == null || !games.containsKey(game)) {
			throw new GameError("Null/unregistered Game (" + game + ") given to IDManager.addExternalID()!");
		}
		if (game.isServer) {
			throw new GameError("Server-side Games are not permitted to use externally-determined Entity ID numbers!");
		}
		if (games.get(game) >= id) {
			throw new GameError("External ID (" + id + ") is already in use by " + game + "!");
		}
		games.put(game, id);
	}

	/**
	 * Check whether an ID number is currently registered under a given
	 * <code>Game</code> to an <code>Entity</code>.
	 * 
	 * @param game - the <code>Game</code> under which to check if the ID is
	 *             registered.
	 * 
	 * @param id   - the ID number to check.
	 * 
	 * @return <code>true</code> if the ID number has already been used for the
	 *         <code>Game</code>, or <code>false</code> if it hasn't.
	 * 
	 * @throws GameError if the given <code>Game</code> is
	 * <code>null</code> or unregistered.
	 */
	public static boolean alreadyUsedID(Game game, int id) {
		if (game == null || !games.containsKey(game)) {
			throw new GameError("Null/unregistered Game (" + game + ") given to IDManager.alreadyUsedID()!");
		}

		return id + (game.isServer ? 1 : 0) < games.get(game);
	}
}
