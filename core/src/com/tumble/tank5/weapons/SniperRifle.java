package com.tumble.tank5.weapons;

import java.util.HashMap;
import java.util.Map;

import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.util.GameUtils;
import com.tumble.tank5.world_logic.GameObject;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

/**
 * Fires a single, high-damage bullet at a target cell after the firer has
 * stopped moving, and hesitated for a brief pause.
 * 
 * @author Tumbl
 *
 */
public class SniperRifle extends Weapon {
	private int damage;
	private double betweenShots, spread, baseRange, rangeVariation;
	
	public SniperRifle() {
		super(1, 1, 4, 20);
		
		// High damage.
		damage = 80;

		// In this case, 'betweenShots' refers to the shooter's hesitation before firing
		// the single round, rather than the interval between subsequent rounds in a
		// burst.
		betweenShots = 0.3;

		// High accuracy.
		spread = Math.toRadians(1);
		// Long range.
		baseRange = 9 * Tile.TILE_SIZE;
		rangeVariation = 0.25 * Tile.TILE_SIZE;
	}
	
	@Override
	public Map<GameObject, Integer> fire(int ownerId, GameWorld gW, Position... positions) {
		Map<GameObject, Integer> victims = new HashMap<GameObject, Integer>();
		
		double baseAngle = Math.atan2(
				positions[1].y - positions[0].y,
				positions[1].x - positions[0].x);
		
			double range = baseRange + GameUtils.random(rangeVariation);
			double angle = baseAngle + GameUtils.random(spread);
			
			Weapon.singleBullet(
					ownerId,
					0.5 + betweenShots,
					gW,
					positions[0],
					new Position(
							positions[0].x + range * Math.cos(angle),
							positions[0].y + range * Math.sin(angle),
							positions[0].z),
					damage,
					victims);
		
		return victims;
	}

}
