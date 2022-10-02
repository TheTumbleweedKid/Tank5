package com.tumble.tank5.entities;

import com.tumble.tank5.tiles.StairCase;
import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.tiles.Tile.TileType;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.util.IDManager;
import com.tumble.tank5.world_logic.DirectionVector;
import com.tumble.tank5.world_logic.DirectionVector.Direction;
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

	protected Position currentPos;
	protected DirectionVector plannedMove = new DirectionVector(0, 0, 0);
	
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
	 * 
	 * @throws GameError 
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
	
	/**
	 * Finds the <code>Position</code> that this <code>Entity</code> is currently
	 * planning to move to on the next turn.
	 * 
	 * @return the <code>Position</code> that will move to (cannot be outside of the
	 *         <code>GameWorld</code> this <code>Entity</code> is part of). May be
	 *         <code>null</code> if the <code>Entity</code> has not been given a
	 *         starting location yet.
	 */
	public Position getPlannedPosition() {
		if (currentPos == null) return null;
		
		return currentPos.move(plannedMove);
	}
	
	/**
	 * Attempts to add a new movement on top of whatever movement is already planned
	 * for this <code>Entity</code> for the next turn.
	 * 
	 * @param move - the <code>DirectionVector</code> that the <code>Entity</code>
	 *             should try to add to its move.
	 * @param gW   - the <code>GameWorld</code> the <code>Entity</code> exists in.
	 * 
	 * @return <code>true</code> if the move was successfully added, or
	 *         <code>false</code> if it was invalid in some way.
	 */
	public boolean addMove(DirectionVector move, GameWorld gW) {
		if (move == null || gW == null || !gW.hasEntity(this))
			return false;

		DirectionVector newMove = plannedMove.combine(move);
		if (newMove.equals(plannedMove))
			return false;

		Direction newDir = Direction.asEnum(newMove);
		Position newPos = currentPos.move(newMove);

		Tile currentTile = gW.getTile(currentPos);

		if (currentTile.getType() == TileType.STAIRS) {
			newPos = newPos.move(((StairCase) currentTile).getHeightChange(newMove));
		}

		Tile newTile = gW.getTile(newPos);

		if (newDir != Direction.NONE) {
			if (newTile.isObstruction(newMove))
				return false;

			if ((newDir == Direction.UP || newDir == Direction.DOWN) && currentTile.getType() != TileType.LADDER)
				return false;
		}

		return true;
	}

	/**
	 * Attempts to make an attack the planned action for this <code>Entity</code>
	 * for the next turn. If the <code>Entity</code> can validly make an attack,
	 * whatever existing action it had planned will be forgotten.
	 * 
	 * @param gW        - the <code>GameWorld</code> the <code>Entity</code> exists
	 *                  in.
	 * 
	 * @param positions - the <code>Position</code>s to pass to the
	 *                  <code>Entity</code>'s equipped weapon to make its attack
	 *                  with.
	 * 
	 * @return <code>true</code> if the attack was successfully planned, or
	 *         <code>false</code> if it was invalid in some way.
	 */
	public abstract boolean addAttack(GameWorld gW, Position... positions);

	/**
	 * Attempts to make a weapon-switch the planned action for this
	 * <code>Entity</code> for the next turn. If the <code>Entity</code> can validly
	 * switch its weapon, whatever existing action it had planned will be forgotten.
	 * 
	 * @param gW - the <code>GameWorld</code> the <code>Entity</code> exists in.
	 * 
	 * @return <code>true</code> if the weapon-switching was successfully planned,
	 *         or <code>false</code> if it was invalid in some way.
	 */
	public abstract boolean addWeaponSwitch(GameWorld gW);

	/**
	 * Attempts to make reloading the <code>Entity</code>'s equipped weapon the
	 * planned action for this <code>Entity</code> for the next turn. If the
	 * <code>Entity</code> can validly reload its equipped weapon, whatever existing
	 * action it had planned will be forgotten.
	 * 
	 * @param gW - the <code>GameWorld</code> the <code>Entity</code> exists in.
	 * 
	 * @return <code>true</code> if the weapon-reload was successfully planned, or
	 *         <code>false</code> if it was invalid in some way.
	 */
	public abstract boolean addReload(GameWorld gW);
	
	/**
	 * Find out whether this <code>Entity</code> is currently on a
	 * <code>Ladder</code> in its <code>GameWorld</code>.
	 * 
	 * @param gW - the <code>GameWorld</code> this <code>Entity</code> exists in.
	 * 
	 * @return <code>true</code> if and only if this <code>Entity</code>:
	 *         <li>exists in the given <code>GameWorld</code></li>
	 *         <li>has been given its starting location (i.e., has a valid
	 *         <code>Position</code> in the <code>GameWorld</code>)</li>
	 *         <li>the <code>Tile</code> at the <code>Entity</code>'s location in
	 *         the <code>GameWorld</code> is a <code>Ladder</code></li> (otherwise
	 *         <code>false</code>).
	 */
	public final boolean onLadder(GameWorld gW) {
		if (currentPos == null || gW == null || !gW.hasEntity(this))
			return false;

		return gW.getTile(currentPos).getType() == TileType.LADDER;
	}

	public abstract boolean isDead();
	
	public final boolean shouldRemove() {
		if (!initialised) return true;
		
		return shouldRemove;
	}
}
