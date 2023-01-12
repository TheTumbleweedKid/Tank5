package com.tumble.tank5.game_object.entities;

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
import com.tumble.tank5.world_logic.game_n_world.GameObject;
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
		weight = 1;
		
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
	 * Switches the <code>Entity</code>'s active <code>Weapon</code> to the next one
	 * in its arsenal (going back to the start of the list if it runs off the end).
	 */
	public void switchWeapon() {
		if (weapons.length < 2) return;
		
		if (++weaponIndex == weapons.length) weaponIndex = 0;
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
	public boolean canStep(Move move, GameWorld gW) {
		if (move == null || gW == null || !gW.hasEntity(this))
			return false;

		DirectionVector moveVector = move.direction.asVector();
		Tile currentFootTile = gW.tileAt(getFootPosition());

		Position newCentrePos = position.step(moveVector, 1);
		Position newFootPos = getFootPosition().step(moveVector, 1);
		
		// Ladder movements.
		if (move.direction == Direction.UP) {
			Tile newTile = gW.tileAt(
					move.direction == Direction.UP
							? newCentrePos
							: newFootPos);
			return currentFootTile.getType() == TileType.LADDER && newTile != null && !newTile.isObstruction(moveVector);
		}

		if (move.direction == Direction.DOWN) {
			Tile newTile = gW.tileAt(newFootPos);

			// Invalid move!
			if (newTile == null || newTile.isObstruction(moveVector))
				return false;

			if (currentFootTile.getType() == TileType.LADDER) {
				// Drop off the bottom of a Ladder into empty space/climb down onto
				// another Ladder below.
				return true;
			}

			// Move from standing on top of a Ladder to climbing down onto it.
			if (newTile.getType() == TileType.LADDER) {
				return true;
			}

			// Keep moving down after having dropped off a Ladder into empty space.
			Tile tileAbove = gW.tileAt(position.move(Direction.UP));
			if (tileAbove != null && tileAbove.getType() == TileType.LADDER)
				return true;

			return false;
		}
		
		// Do StairCase altitude adjustments.
		Tile belowNewFootTile = gW.tileAt(
				newFootPos.step(Direction.DOWN, 1));
		
		// Going up StairCases:
		if (currentFootTile.getType() == TileType.STAIRS
				&& ((StairCase) currentFootTile).upDirection.equals(moveVector)) {
			newCentrePos = newCentrePos.step(Direction.UP, 1);
		}
		// Going down StairCases (note that this can't push Entities through the bottom of the map):
		if (belowNewFootTile != null
				&& belowNewFootTile.getType() == TileType.STAIRS
				&& ((StairCase) belowNewFootTile).downDirection.equals(moveVector)) {
			newCentrePos = newCentrePos.step(Direction.DOWN, 1);
		}
		
		// Check for obstructions/not moving off the map.
		Tile newTile = gW.tileAt(newCentrePos);
		if (newTile == null) return false;
		
		// Are we trying to enter/exit a Ladder through its bars?
		if ((newTile.getType() == TileType.LADDER && newTile.isObstruction(moveVector.reverse())
				|| (currentFootTile.getType() == TileType.LADDER && currentFootTile.isObstruction(moveVector))))
			return false;
		// For normal Tiles/move cases.
		return !newTile.isObstruction(moveVector);
	}
	
	/**
	 * 
	 * @param direction
	 * 
	 * @param gW
	 * 
	 * @return
	 */
	public boolean canMove(Direction direction, GameWorld gW) {
		if (direction == null || !direction.validEntityMove() || gW == null || !gW.hasEntity(this))
			return false;
		
		if (direction == Direction.NONE) return true;

		DirectionVector moveVector = direction.asVector();
		Tile currentTile = gW.tileAt(position);
		
		if (currentTile.isObstruction(moveVector)) return false;
		
		Position newPos = position.move(moveVector);
		
		if (direction == Direction.UP) {
			// Can only move up an (unobstructed) Ladder.
			Tile newTile = gW.tileAt(newPos);
			return currentTile.getType() == TileType.LADDER && newTile != null && !newTile.isObstruction(moveVector);
		}
		
		if (direction == Direction.DOWN) {
			Tile newTile = gW.tileAt(newPos);

			// Drop off the bottom of a Ladder into empty space/climb down onto
			// another Ladder below.
			if (currentTile.getType() == TileType.LADDER && newTile != null && !newTile.isObstruction(moveVector))
				return true;

			// Move from standing on top of a Ladder to climbing down onto it.
			return !currentTile.isObstruction(moveVector) && newTile != null && newTile.getType() == TileType.LADDER;
		}
		
		if (currentTile.getType() == TileType.LADDER) {
			Tile newTile = gW.tileAt(newPos);
			return !currentTile.isObstruction(moveVector) && newTile != null && !newTile
			
		}
		
		Position belowNewFootPos = getFootPosition().move(moveVector).step(Direction.DOWN, 1);

		if (currentTile.getType() == TileType.STAIRS) {
			newPos = newPos.move(((StairCase) currentTile).getHeightChange(moveVector));
			belowNewFootPos = belowNewFootPos.move(((StairCase) currentTile).getHeightChange(moveVector));
		}
		
		Tile belowNewTile = gW.tileAt(belowNewFootPos);
		
		if (belowNewTile != null && belowNewTile.getType() == TileType.STAIRS) {
			return ((StairCase) belowNewTile).downDirection.equals(moveVector);
		}

		Tile newTile = gW.tileAt(newPos);

		return (newTile.getType() != TileType.STAIRS && newTile.isObstruction(moveVector))
				|| (newTile.getType() == TileType.STAIRS && ((StairCase) newTile).upDirection.equals(moveVector));
	}

	/**
	 * Return whether the <code>Entity</code> can validly make an attack next turn
	 * using its active <code>Weapon</code>.
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
		
		return !isDead();
	}

	/**
	 * Return whether the <code>Entity</code> can validly switch which
	 * <code>Weapon</code> it has active as an action (hint: it can't switch it it
	 * has less than two <code>Weapon</code>s in its arsenal).
	 * 
	 * @return <code>true</code> if the <code>Entity</code> is able to switch weapon, otherwise <code>false</code>.
	 */
	public final boolean canSwitchWeapon() {
		return !isDead() && weapons.length >= 2;
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
	 * <code>Ladder</code> or <code>StairCase</code> in its <code>GameWorld</code>.
	 * 
	 * @param gW - the <code>GameWorld</code> this <code>Entity</code> exists in.
	 * 
	 * @return <code>true</code> if and only if this <code>Entity</code>:
	 *         <li>exists in the given <code>GameWorld</code></li>
	 *         <li>has been given its starting location (i.e., has a valid
	 *         <code>Position</code> in the <code>GameWorld</code>)</li>
	 *         <li>the <code>Tile</code> at the <code>Entity</code>'s location in
	 *         the <code>GameWorld</code> is a <code>Ladder</code> or
	 *         <code>StairCase</code></li> (otherwise <code>false</code>).
	 */
	public final boolean onClimbingTile(GameWorld gW) {
		if (getFootPosition() == null || gW == null || !gW.hasEntity(this))
			return false;

		return gW.tileAt(getFootPosition()).getType() == TileType.LADDER
				|| gW.tileAt(getFootPosition()).getType() == TileType.STAIRS;
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
	
	public Weapon getWeapon() {
		if (weaponIndex == -1) return null;
		
		return weapons[weaponIndex];
	}
}
