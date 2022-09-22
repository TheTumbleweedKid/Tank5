package com.tumble.tank5.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tumble.tank5.world_logic.Game;

public class IDManager {
	private static int lastGameId = 0;
	private static Map<Game, Set<Integer>> games = new HashMap<Game, Set<Integer>>();
	
	private IDManager() {
		
	}
	
	public static int addGame(Game game) {
		games.put(game, new HashSet<Integer>());
		return ++lastGameId;
	}
	
	public static int nextID(Game game) {
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
	
	public static void addUsedID(Game game, Integer usedID) {
		if (game == null || !games.containsKey(game)) {
			throw new GameError("Null/unregistered Game (" + game + ") given to IDManager.addUsedID()!");
		}
		if (game.isServer) {
			throw new GameError("Serverside Games are not permitted to use externally-determined Entity ID numbers!");
		}
		if (games.get(game).contains(usedID)) {
			throw new GameError("External ID (" + usedID + ") is already in use by " + game + "!");
		}
		games.get(game).add(usedID);
	}
	
	public static boolean alreadyUsedID(Game game, Integer id) {
		if (game == null || !games.containsKey(game)) {
			throw new GameError("Null/unregistered Game (" + game + ") given to IDManager.alreadyUsedID()!");
		}
		
		return games.get(game).contains(id);
		
	}
}
