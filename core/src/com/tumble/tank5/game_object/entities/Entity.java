package com.tumble.tank5.game_object.entities;

import com.tumble.tank5.game_object.GameObject;
import com.tumble.tank5.game_object.tiles.StairCase;
import com.tumble.tank5.game_object.tiles.Tile;
import com.tumble.tank5.game_object.tiles.Tile.TileType;
import com.tumble.tank5.util.DirectionVector;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.util.IDManager;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.util.DirectionVector.Direction;
import com.tumble.tank5.weapons.Weapon;
import com.tumble.tank5.world_logic.game_n_world.Game;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

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
public abstract class Entity extends GameObject {
	private final int entityID;
	
	// Radius of the Entity's cylindrical hit-box.
	private float radius;
	
	private Weapon[] weapons;
	private int weaponIndex;

	protected Position startPos;
	protected DirectionVector plannedMove = new DirectionVector(0, 0, 0);
	
	protected boolean shouldRemove;

	/**
	 * Initialises this <code>Entity</code> with an ID number (not necessarily
	 * determined by the <code>entityID</code> parameter here), which is then
	 * registered with the <code>IDManager</code> under the given <code>Game</code>
	 * (i.e., the <code>Game</code> which the <code>Entity</code> is a part of).
	 * This ensures that every <code>Entity</code> in any <code>Game</code>'s
	 * <code>GameWorld</code> is <i>guaranteed</i> to have a different ID number to
	 * every other <code>Entity</code> that has ever been in that
	 * <code>GameWorld</code>.
	 * 
	 * @param entityID - the ID number of this <code>Entity</code> - must be unique
	 *                 amongst the ID numbers of every other <code>Entity</code> in
	 *                 the given <code>Game</code>. <b>ONLY</b> used if
	 *                 <code>game</code> is a client-side <code>Game</code>.
	 * 
	 * @param game     - the <code>Game</code> under which this
	 *                 <code>Entity</code>'s ID number should be registered with the
	 *                 <code>IDManager</code>.
	 * 
	 * @param radius   - the radius of the <code>Entity</code>'s cylindrical hit-box
	 *                 - may be greater than {@link Tile#TILE_SIZE}, but this will
	 *                 not cause the Entity to be hit by bullets outside of the
	 *                 <code>Tile</code> it is standing on.
	 * 
	 * @param weapons  - the <code>Weapon</code>s this <code>Entity</code> is armed
	 *                 with (may be unarmed).
	 * 
	 * @throws GameError if <code>game</code> is <code>null</code> or the given ID
	 *                   has already been registered (to another
	 *                   <code>Entity</code>) with the <code>IDManager</code> under
	 *                   this <code>Game</code>.
	 */
	public Entity(int entityID, Game game, float radius, Weapon ... weapons) {
		if (game == null) {
			throw new GameError("Can't initialise an Entity with a null Game!");
		}

		if (!game.isServer && IDManager.alreadyUsedID(game, entityID)) {
			throw new GameError("Can't re-use an Entity's ID number (" + entityID + ")!");
		}
		
		this.entityID = game.isServer ? IDManager.nextID(game) : entityID;
		
		this.radius = radius;
		
		this.weapons = weapons;
		weaponIndex = weapons.length > 0 ? 0 : -1;
		
		shouldRemove = false;
	}
	
	final void setPosition(Position newPosition) {
		// Package-private.
		if (newPosition != null) position = newPosition;
	}
	
	public abstract void spawn(Position pos);
		
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
	public boolean canStep(Move move, GameWorld gW) {
		if (move == null || gW == null || !gW.hasEntity(this))
			return false;
		
		
		
		return true;
	}
	
	/**
	 * Should only be called during the input phase.
	 * 
	 * @param direction
	 * @param gW
	 * @return
	 */
	public boolean canMove(Direction direction, GameWorld gW) {
		if (direction == null || gW == null || !gW.hasEntity(this))
			return false;
		
		if (direction == Direction.NONE) return true;

		Tile currentTile = gW.tileAt(getPosition());
		
		Position newPos = getPosition().move(direction);

		if (currentTile.getType() == TileType.STAIRS) {
			newPos = newPos.move(((StairCase) currentTile).getHeightChange(direction.asVector()));
		}

		Tile newTile = gW.tileAt(newPos);

		if (newTile.isObstruction(direction.asVector()))
			return false;

		if ((direction == Direction.UP || direction == Direction.DOWN) && currentTile.getType() != TileType.LADDER)
			return false;
		
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
	public final boolean canAttack(Game g, Position... positions) {
		if (g == null || !g.getWorld().hasEntity(this) || positions.length < 2 || weaponIndex == -1
				|| !weapons[weaponIndex].ableToFire(g.getRoundNumber()))
			return false;
		
		for (int i = 1; i < positions.length; i++) {
			if (!weapons[weaponIndex].isInRange(positions[0], positions[i])) return false;
		}
		
		return true;
	}

	/**
	 * Attempts to make a weapon-switch the planned action for this
	 * <code>Entity</code> for the next turn. If the <code>Entity</code> can validly
	 * switch its weapon, whatever existing action it had planned will be forgotten.
	 * 
	 * @return <code>true</code> if the weapon-switching was successfully planned,
	 *         or <code>false</code> if it was invalid in some way.
	 */
	public final boolean canSwitchWeapon() {
		return weaponIndex != -1 && weapons.length != 0;
	}

	/**
	 * Attempts to make reloading the <code>Entity</code>'s equipped weapon the
	 * planned action for this <code>Entity</code> for the next turn. If the
	 * <code>Entity</code> can validly reload its equipped weapon, whatever existing
	 * action it had planned will be forgotten.
	 * 
	 * @return <code>true</code> if the weapon-reload was successfully planned, or
	 *         <code>false</code> if it was invalid in some way.
	 */
	public final boolean canReload() {
		return weaponIndex != -1 && weapons[weaponIndex].ableToReload();
	}
	
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
		if (startPos == null || gW == null || !gW.hasEntity(this))
			return false;

		return gW.tileAt(startPos).getType() == TileType.LADDER;
	}
	
	public final boolean shouldRemove() {
		return shouldRemove;
	}
	
	/**
	 * Gets the unique ID number this <code>Entity</code> is registered with the
	 * <code>IDManager</code> with.
	 * 
	 * @return the Entity's ID number.
	 */
	public final int getID() {
		return entityID;
	}
	
	public final float getRadius() {
		return radius;
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
		if (startPos == null) return null;
		
		return startPos.move(plannedMove);
	}
	
	public Weapon getWeapon() {
		if (weaponIndex == -1) return null;
		
		return weapons[weaponIndex];
	}
}