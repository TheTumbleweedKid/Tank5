package com.tumble.tank5.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tumble.tank5.world_logic.Game;

/**
 * Enforces the (hopefully) rigorous system of ID numbers for any and all
 * <code>Game</code>s that are being run in this application, as well as every
 * <code>Entity</code> in each of their <code>GameWorld</code>s.
 * 
 * @author Tumbl
 *
 */
public class IDManager {
	private static int lastGameId = 0;
	private static Map<Game, Set<Integer>> games = new HashMap<Game, Set<Integer>>();

	private IDManager() {

	}

	public static int addGame(Game game) {
		games.put(game, new HashSet<Integer>());
		return ++lastGameId;
	}

	/**
	 * <b>SERVER-SIDE <code>Game</code>S ONLY!</b> <br>
	 * 
	 * Generates and registers a new Entity ID number under a given (server-side)
	 * <code>Game</code>. Client-side <code>Game</code>s are not permitted to use
	 * this method, as they should have their Entity ID numbers given to them by
	 * their server-side versions.
	 * 
	 * @param game - the <code>Game</code> to register the new ID number under.
	 * 
	 * @return the ID number that was just generated and registered (presumably to
	 *         be used in Entity.init(), but it won't break anything if the ID
	 *         number just goes unused - aside from slowly filling up the possible
	 *         Integer values...).
	 * 
	 * @throws <code>GameError</code> if the <code>Game</code> is <code>null</code>,
	 * client-side or unregistered.
	 */
	public static int nextID(Game game) {
		if (game == null || !games.containsKey(game)) {
			throw new GameError("Null/unregistered Game (" + game + ") given to IDManager.nextID()!");
		}
		if (!game.isServer) {
			throw new GameError("Client-side Games are not permitted to generate their own Entity ID numbers!");
		}

		Set<Integer> existingIDs = games.get(game);
		int i = 0;
		while (true) {
			
			if (!existingIDs.contains(i)) {
				existingIDs.add(i);
				return i;
			}
			i++;
		}
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
	 * @throws <code>GameError</code> if:
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
		if (games.get(game).contains(id)) {
			throw new GameError("External ID (" + id + ") is already in use by " + game + "!");
		}
		games.get(game).add(id);
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
	 * @throws <code>GameError</code> if the given <code>Game</code> is
	 * <code>null</code> or unregistered.
	 */
	public static boolean alreadyUsedID(Game game, Integer id) {
		if (game == null || !games.containsKey(game)) {
			throw new GameError("Null/unregistered Game (" + game + ") given to IDManager.alreadyUsedID()!");
		}

		return games.get(game).contains(id);

	}
}
