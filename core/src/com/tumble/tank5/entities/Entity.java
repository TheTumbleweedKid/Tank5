package com.tumble.tank5.entities;

import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.util.IDManager;
import com.tumble.tank5.world_logic.DirectionVector;
import com.tumble.tank5.world_logic.Game;

public abstract class Entity {
	private int entityID;
	
	protected Tile currentTile;
	protected DirectionVector lastMove = new DirectionVector(0, 0, 0);
	
	protected int health;
	
	private boolean initialised = false;
	
	protected final void init(Integer entityID, Game game) {
		if (game == null) {
			throw new GameError("Can't initialise an Entity with a null Game!");
		}
		
		if (initialised) {
			throw new GameError("Can't initialise the same Entity (" + entityID + ") twice!");
		}
		
		if (IDManager.alreadyUsedID(game, entityID)) {
			throw new GameError("Can't re-use an Entity's ID number (" + entityID + ")!");
		}
		
		this.entityID = entityID;
		initialised = true;
	}
	
	public final boolean onLadder() {
		if (currentTile == null) return false;
		
		return currentTile.isClimable();
	}
	
	public abstract boolean isDead();
	
	public final boolean shouldRemove() {
		if (!initialised) return true;
	}
}
