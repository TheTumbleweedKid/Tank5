package com.tumble.tank5.weapons;

import java.util.HashMap;
import java.util.Map;

import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.util.GameUtils;
import com.tumble.tank5.world_logic.GameObject;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

/**
 * Fires a burst of bullets at a single cell after the firer has stopped moving.
 * 
 * @author Tumbl
 *
 */
public class AssaultRifle extends Weapon {
	private int damage, burstSize;
	private double betweenShots, spread, baseRange, rangeVariation;
	
	public AssaultRifle() {
		super(0, 1, 30, 60);
		
		damage = 20;
		
		burstSize = 5;
		
		betweenShots = 0.0625;
		
		spread = Math.toRadians(5);
		
		baseRange = 4.5 * Tile.TILE_SIZE;
		rangeVariation = 0.5 * Tile.TILE_SIZE;
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
