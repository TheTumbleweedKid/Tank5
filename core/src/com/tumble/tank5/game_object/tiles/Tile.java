package com.tumble.tank5.game_object.tiles;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import com.tumble.tank5.events.DeathEvent;
import com.tumble.tank5.events.Event;
import com.tumble.tank5.game_object.GameObject;
import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.util.DirectionVector;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

/**
 * Controls/stores the properties of a tile on the game board.
 * 
 * @author Tumble
 *
 */
public abstract class Tile extends GameObject {
	public static final double TILE_SIZE = 10.0;
	
	protected static final DirectionVector NO_MOVEMENT = DirectionVector.Direction.NONE.asVector();
	
	private boolean hasDied = false;
	
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

	public boolean die(int atTick, Entity attacker, GameWorld gW, Queue<Event> eventStream) {
		// Rubble and Air Tiles can't be killed.
		if (type == TileType.RUBBLE || this == Air.AIR || hasDied) return false;
		
		damage(Math.max(1, getHealth()), attacker);
		
		for (Tile t : supports) {
			if (t.removeSupport(this, gW)) {
				eventStream.offer(
						new DeathEvent(
								atTick,
								t,
								attacker));
			}
		}
		
		gW.getRubbleManager().makeRubble(this, getAttacker(), eventStream);
		hasDied = true;
		
		return true;
	}

	/**
	 * Adds a supporting <code>Tile</code> to this <code>Tile</code>'s set of
	 * supports - a set of <i>other</i> <code>Tile</code>s that hold this one up;
	 * that, if they are all destroyed, will cause the collapse of this
	 * <code>Tile</code> (and potentially any other <code>Tile</code>s that it
	 * supports). If this is an <code>Air Tile</code> then this method will have no
	 * effect.
	 * 
	 * @param t  - the <code>Tile</code> that supports this <code>Tile</code> (that
	 *           this one is supported <i>by</i>). Will not be added if it is
	 *           <code>null</code> or this <code>Tile</code> itself (to avoid
	 *           infinite looping in {@link Tile#hashCode()} - as well as the
	 *           nonsensical situation in which a lone <code>Tile</code> supports
	 *           itself).
	 * 
	 * @param gW - the <code>GameWorld</code> that both <code>Tile</code>s are a
	 *           part of (no action will be taken if either does not exist in the
	 *           given world).
	 */
	public void addSupport(Tile t, GameWorld gW) {
		if (this == Air.AIR) return;
		
		if (t != null
				&& t != this
				&& gW.tileAt(getPosition()) == this
				&& (t.type == TileType.AIR || gW.tileAt(t.getPosition()) == t)) {
			supportedBy.add(t);
			t.supports.add(this);
		}
	}

	/**
	 * Removes a supporting <code>Tile</code> from this <code>Tile</code>'s set of
	 * supports. If this is an <code>Air</code> or <code>Rubble Tile</code> then
	 * this method will have no effect.
	 * 
	 * @param t  - the supporting <code>Tile</code> to remove (that has been
	 *           destroyed).
	 * 
	 * 
	 * @param gW - the <code>GameWorld</code> that both <code>Tile</code>s are a
	 *           part of (no action will be taken if either does not exist in the
	 *           given world).
	 * 
	 */
	public boolean removeSupport(Tile t, GameWorld gW) {
		return this != Air.AIR
				&& supportedBy.remove(t)
				&& gW.tileAt(getPosition()) == this
				&& supportedBy.size() == 0
				&& type != TileType.RUBBLE;
	}

	public TileType getType() {
		return type;
	}
	
	public boolean isDead() {
		return hasDied;
	}
	
	public abstract String toString();
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) return false;
		
		Tile other = (Tile) obj;
		
		return other.type == type
				&& other.toString().equals(toString())
				&& other.supports.containsAll(supports)
				&& other.supports.size() == supports.size()
				&& other.supportedBy.containsAll(supportedBy)
				&& other.supportedBy.size() == supportedBy.size()
				&& (getPosition() == null || getPosition().sameTile(other.getPosition()))
				&& other.getHealth() == getHealth();
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int hash = getClass().hashCode();

		hash = hash * prime + type.hashCode();
		hash = hash * prime + toString().hashCode();
		
		// Possibly expensive calls? See HashSet.hashCode().
		hash = hash * prime + supports.hashCode();
		hash = hash * prime + supportedBy.hashCode();
		
		hash = hash * prime + (getPosition() == null ? 0 : getPosition().hashCode());
		
		hash = hash * prime + getHealth();
		
		return hash;
	}
}
