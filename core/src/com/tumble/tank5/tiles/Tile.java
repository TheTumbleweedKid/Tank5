package com.tumble.tank5.tiles;

import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.utils.Queue;
import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.events.Event;
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

	private TileType type;
	protected int weight;

	public enum TileType {
		AIR,
		WALL,
		LADDER,
		STAIRS,
		RUBBLE
	}
	
	public Tile(TileType type, Position pos, int health, int weight) {
		this.type = type;
		this.weight = weight;
		
		spawn(pos, health);
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
	
	public abstract boolean stopsBullets();

	public abstract boolean stopsFalling();

	public boolean damage(int damage, Entity attacker, GameWorld gW, Queue<Event> eventStream) {
		if (damage(damage, attacker)) {
			for (Tile t : supports) {
				t.removeSupport(this, attacker, gW, eventStream);
			}
			return true;
		}
		return false;
	}
	
	public void addSupport(Tile t) {
		if (t != null && t != this) {
			supportedBy.add(t);
			t.supports.add(this);
		}
	}
	
	public void removeSupport(Tile t, Entity attacker, GameWorld gW, Queue<Event> eventStream) {
		supportedBy.remove(t);
		
		if (supportedBy.size() == 0 && type != TileType.RUBBLE) {
			gW.getRubbleManager().makeRubble(this, attacker, eventStream);
			
			for (Tile supported : supports) {
				supported.removeSupport(this, attacker, gW, eventStream);
			}
			
		}
	}

	public TileType getType() {
		return type;
	}
	
	public abstract String toString();
}
