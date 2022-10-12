package com.tumble.tank5.tiles;

import com.tumble.tank5.world_logic.GameWorld;

import java.util.HashSet;
import java.util.Set;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.DirectionVector;
import com.tumble.tank5.world_logic.GameObject;

/**
 * Controls/stores the properties of a tile on the game board.
 * 
 * @author Tumble
 *
 */
public abstract class Tile extends GameObject {
	public static final double TILE_SIZE = 10.0;
	
	private Set<Tile> supports = new HashSet<Tile>();
	private Set<Tile> supportedBy = new HashSet<Tile>();

	protected TileType type;

	public enum TileType {
		AIR,
		WALL,
		LADDER,
		STAIRS,
		RUBBLE
	}
	
	/**
	 * Whether an object can move into the Tile in a given direction - i.e., via a
	 * given <code>DirectionVector</code> - (the obstructiveness may depend on the
	 * direction of approach for <code>Stairs</code> and <code>Ladders</code>,
	 * etc.).
	 * 
	 * @param dir the <code>DirectionVector</code> the object is moving toward the
	 *            <code>Tile</code> in.
	 * 
	 * @return <code>true</code> if an object can't move into the <code>Tile</code>
	 *         via the given <code>DirectionVector</code>, or <code>false</code> if can.
	 */
	public abstract boolean isObstruction(DirectionVector dir);

	public abstract boolean providesSupport();

	public boolean damage(int newHealth, Entity attacker, GameWorld gW) {
		if (damage(getHealth() - newHealth, attacker)) {
			for (Tile t : supports) {
				t.removeSupport(this, gW);
			}
		}
	}
	
	public void addSupport(Tile t) {
		if (t != null && t != this) {
			supportedBy.add(t);
			t.supports.add(this);
		}
	}
	
	public void removeSupport(Tile t, GameWorld gW) {
		supportedBy.remove(t);
		
		if (supportedBy.size() == 0 && type != TileType.RUBBLE) {
			gW.getRubbleManager().makeRubble(this);
			
			for (Tile supported : supports) {
				supported.removeSupport(this, gW);
			}
			
		}
	}

	public TileType getType() {
		return type;
	}
	
	public abstract String toString();
}
