package com.tumble.tank5.weapons;

import com.tumble.tank5.events.MovementEvent;
import com.tumble.tank5.tiles.Tile;

/**
 * Fires a fast burst of bullets at a single cell as soon as the firer has
 * stopped moving.
 * 
 * @author Tumbl
 *
 */
public class AssaultRifle extends Weapon {
	
	public AssaultRifle() {
		super(
				20, // Medium/low damage.
				MovementEvent.MOVEMENT_TICKS, // Fire as soon as movement is done.
				6, // Small interval between shots (fast fire rate). ~60ms?
				1, // 1-round reload.
				5, // Short burst.
				30, // 30-shot magazine.
				60, // Start with an extra 2 magazines' worth of ammo.
				4.5 * Tile.TILE_SIZE, // Short/medium range.
				0.5 * Tile.TILE_SIZE, // Fairly consistent range.
				5 // Decent accuracy.
		);
	}
	
}
