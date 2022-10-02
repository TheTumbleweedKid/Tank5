package com.tumble.tank5.world_logic;

import java.util.Set;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.events.Input;
import com.tumble.tank5.util.IDManager;

public class Game {
	public final boolean isServer;
	private final int gameId;
	
	private Set<Entity> entities;
	private GameWorld world;
	
	private long startTime;
	private boolean started = false;
	
	private int roundNumber, roundDuration;
	private long roundStartTime;
	private boolean acceptingInput = false;
	
	public Game(boolean isServer, int minPlayers) {
		this.isServer = isServer;
		gameId = IDManager.registerGame(this);
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean isAcceptingInput() {
		return acceptingInput;
	}
	
	public boolean addInput(Input i) {
		if (i == null) return false;
		
		if (!started) {
			// accept special inputs (add player, player-quit, NPC-spawn, etc.).
			return false;
		}
		
		return acceptingInput && i.apply(world);
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
