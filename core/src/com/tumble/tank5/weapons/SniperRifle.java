package com.tumble.tank5.weapons;

import com.tumble.tank5.events.MovementEvent;
import com.tumble.tank5.tiles.Tile;

/**
 * Fires a single, high-damage bullet at a target cell after the firer has
 * stopped moving, and hesitated for a brief pause.
 * 
 * @author Tumbl
 *
 */
public class SniperRifle extends Weapon {
	
	public SniperRifle() {
		super(
				80, // High damage.
				(int) (MovementEvent.MOVEMENT_TICKS * 1.3), // Hesitate briefly after moving, then fire.
				0, // Single-fire (interval between shots is irrelevant).
				1, // 1-round reload.
				1, // Single-fire (i.e., 1-shot burst).
				4, // 4-shot magazine.
				16, // Start with an extra 4 magazines' worth of ammo.
				9 * Tile.TILE_SIZE, // Long range.
				0.25 * Tile.TILE_SIZE, // Very consistent range.
				1 // High accuracy.
		);
	}
	
}
