package com.tumble.tank5.entities;

import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.util.IDManager;
import com.tumble.tank5.world_logic.DirectionVector;
import com.tumble.tank5.world_logic.Game;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

/**
 * The core class of any mobile object in the game world. An entity is not a
 * <code>Tile</code>, so one entity cannot "provide support" for another above
 * it (i.e., stop it from falling). Indeed, if two entities move into the same
 * <code>Tile</code> via any means (either deliberate action or gravity), both
 * should die immediately, without getting to live out their action-rounds
 * first.
 * 
 * @author Tumbl
 *
 */
public abstract class Entity {
	private int entityID;

	protected Tile currentTile;
	protected DirectionVector lastMove = new DirectionVector(0, 0, 0);
	
	protected int health;
	protected boolean shouldRemove;
	
	private boolean initialised = false;
	
	/**
	 * <b>A VERY IMPORTANT METHOD!</b> <br>
	 * 
	 * Initialises this <code>Entity</code> with an ID number, which is then
	 * registered with the <code>IDManager</code> under the given <code>Game</code>
	 * (i.e., the <code>Game</code> which the <code>Entity</code> is a part of).
	 * <b>ONLY</b> this method can register the <code>Entity</code> correctly -
	 * failure to call this method will result in the instant removal of this
	 * <code>Entity</code> from any <code>Game</code> it is added to. Doing all this
	 * ensures that every <code>Entity</code> in any <code>Game</code>'s
	 * <code>GameWorld</code> is <i>guaranteed</i> to have a different ID number to
	 * every other Entity that has ever been in that <code>GameWorld</code>.
	 * 
	 * @param entityID - the ID number of this <code>Entity</code> - must be unique
	 *                 amongst the ID numbers of every other <code>Entity</code> in
	 *                 the given <code>Game</code>.
	 * 
	 * @param game     - the <code>Game</code> under which this
	 *                 <code>Entity</code>'s ID number should be registered with the
	 *                 <code>IDManager</code>.
	 */
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
		
		shouldRemove = false;
	}
	
	public abstract boolean addMove(DirectionVector dir, GameWorld gW);

	public abstract boolean addAttack(GameWorld gW, Position... positions);

	public abstract boolean addWeaponSwitch(GameWorld gW);

	public abstract boolean addReload(GameWorld gW);
	
	public final boolean onLadder() {
		if (currentTile == null) return false;
		
		return currentTile.isClimable();
	}
	
	public abstract boolean isDead();
	
	public final boolean shouldRemove() {
		if (!initialised) return true;
		
		return shouldRemove;
	}
}
