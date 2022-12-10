package com.tumble.tank5.tiles;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.events.Event;
import com.tumble.tank5.game_object.GameObject;
import com.tumble.tank5.world_logic.DirectionVector;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

/**
 * Controls/stores the properties of a tile on the game board.
 * 
 * @author Tumble
 *
 */
public abstract class Tile extends GameObject {
	public static final double TILE_SIZE = 10.0;
	
	protected static final DirectionVector NO_MOVEMENT = new DirectionVector(0, 0, 0);
	
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

	public boolean die(Entity attacker, GameWorld gW, Queue<Event> eventStream) {
		if (type == TileType.RUBBLE || getHealth() <= 0) return false;
		
		for (Tile t : supports) {
			t.removeSupport(this, attacker, gW, eventStream);
		}
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
	 * supports, passing on the attacking <code>Entity</code> who destroyed the
	 * support, and destroying this Tile if it is no longer supported (in which
	 * case, this method is called for all <code>Tile</code>s that were supported by
	 * this <code>Tile</code>). If this is an <code>Air Tile</code> then this method
	 * will have no effect.
	 * 
	 * @param t           - the supporting <code>Tile</code> to remove (that has
	 *                    been destroyed).
	 * 
	 * @param attacker    - the attacking <code>Entity</code> who caused the
	 *                    destruction of this <code>Tile</code>. May be
	 *                    <code>null</code> if this was a natural collapse of some
	 *                    sort.
	 * 
	 * @param gW          - the <code>GameWorld</code> that both <code>Tile</code>s
	 *                    are a part of (no action will be taken if either does not
	 *                    exist in the given world).
	 * 
	 * @param eventStream - the stream of <code>Event</code>s to add the
	 *                    <code>Tile</code>'s potential collapse (as well as those
	 *                    of any <code>Tile</code>s this one supports) to.
	 */
	public void removeSupport(Tile t, Entity attacker, GameWorld gW, Queue<Event> eventStream) {
		if (this == Air.AIR) return;
		
		if (supportedBy.remove(t) && gW.tileAt(getPosition()) == this) {
			if (supportedBy.size() == 0 && type != TileType.RUBBLE) {
				gW.getRubbleManager().makeRubble(this, attacker, eventStream);
				
				for (Tile supported : supports) {
					supported.removeSupport(this, attacker, gW, eventStream);
				}
				
			}
		}
	}

	public TileType getType() {
		return type;
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
