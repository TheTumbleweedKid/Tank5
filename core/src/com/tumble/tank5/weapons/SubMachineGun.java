package com.tumble.tank5.weapons;

import com.tumble.tank5.events.MovementEvent;
import com.tumble.tank5.tiles.Tile;

/**
 * Fires a fast burst of bullets in a sweeping arc from one target cell to
 * another whilst the firer is moving (finishing after they have stopped).
 * 
 * @author Tumbl
 *
 */
public class SubMachineGun extends Weapon {
	
	public SubMachineGun() {
		super(
				10, // Low damage.
				(int) (0.1 * MovementEvent.MOVEMENT_TICKS), // Fire as shortly after round begins.
				2, // Small interval between shots (very fast fire rate). ~5-10ms?
				1, // 1-round reload.
				14, // Extended burst.
				30, // 30-shot magazine.
				60, // Start with an extra 2 magazines' worth of ammo.
				3.5 * Tile.TILE_SIZE, // Short range.
				0.75 * Tile.TILE_SIZE, // Moderately consistent range.
				8 // Decent-ish accuracy.
		);
	}
}
