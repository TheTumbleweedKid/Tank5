package com.tumble.tank5.weapons;

import java.util.HashMap;
import java.util.Map;

import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.util.GameUtils;
import com.tumble.tank5.world_logic.GameObject;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

/**
 * Fires a fast burst of bullets in a sweeping arc from one target cell to
 * another whilst the firer is moving (finishing after they have stopped).
 * 
 * @author Tumbl
 *
 */
public class SubMachineGun extends Weapon {
	private int damage, burstSize;
	private double betweenShots, spread, baseRange, rangeVariation;
	
	public SubMachineGun() {
		super(0, 1, 30, 60);
		
		// Low damage.
		damage = 10;
		
		// Extended burst.
		burstSize = 14;
		
		// Tiny interval between shots (very fast fire rate).
		betweenShots = 0.05;
		
		// Decent-ish accuracy.
		spread = Math.toRadians(8);
		
		// Short range.
		baseRange = 3.5 * Tile.TILE_SIZE;
		rangeVariation = 0.75 * Tile.TILE_SIZE;
	}
	
	@Override
	public Map<GameObject, Integer> fire(int ownerId, GameWorld gW, Position... positions) {
		Map<GameObject, Integer> victims = new HashMap<GameObject, Integer>();
		
		double baseAngle = Math.atan2(
				positions[1].y - positions[0].y,
				positions[1].x - positions[0].x);
		
		for (int i = 0; i < burstSize; i++) {
			double range = baseRange + GameUtils.random(rangeVariation);
			double angle = baseAngle + GameUtils.random(spread);
			
			Weapon.singleBullet(
					ownerId,
					0.5 + i * betweenShots,
					gW,
					positions[0],
					new Position(
							positions[0].x + range * Math.cos(angle),
							positions[0].y + range * Math.sin(angle),
							positions[0].z),
					damage,
					victims);
		}
		
		return victims;
	}

}
