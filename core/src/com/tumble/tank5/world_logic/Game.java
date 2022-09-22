package com.tumble.tank5.world_logic;

import java.util.Set;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.util.IDManager;

public class Game {
	public final boolean isServer;
	private final int gameId;
	
	private Set<Entity> entities;
	private GameWorld world;
	
	private long startTime;
	private boolean started = false;
	
	public Game(boolean isServer) {
		this.isServer = isServer;
		gameId = IDManager.addGame(this);
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
