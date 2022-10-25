package com.tumble.tank5.weapons;

import java.util.ArrayList;
import java.util.List;

import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.util.GameUtils;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

/**
 * Fires a fast burst of bullets at a single cell as soon as the firer has
 * stopped moving.
 * 
 * @author Tumbl
 *
 */
public class AssaultRifle extends Weapon {
	private int damage, burstSize;
	private double betweenShots, spread, baseRange, rangeVariation;
	
	public AssaultRifle() {
		super(0, 1, 30, 60);
		
		// Medium/low damage.
		damage = 20;
		
		// Short burst.
		burstSize = 5;
		
		// Small interval between shots (fast fire rate).
		betweenShots = 0.0625;
		
		// Decent accuracy.
		spread = Math.toRadians(5);
		
		// Short/medium range.
		baseRange = 4.5 * Tile.TILE_SIZE;
		rangeVariation = 0.5 * Tile.TILE_SIZE;
	}
	
	@Override
	public List<Damage> fire(int ownerId, GameWorld gW, Position... positions) {
		List<Damage> damages = new ArrayList<Damage>();
		
		double baseAngle = Math.atan2(
				positions[1].y - positions[0].y,
				positions[1].x - positions[0].x);
		
		for (int i = 0; i < burstSize; i++) {
			double range = baseRange + GameUtils.random(rangeVariation);
			double angle = baseAngle + GameUtils.random(spread);
			
			for (Damage damage : Weapon.singleBullet(
						ownerId,
						0.5 + i * betweenShots,
						gW,
						positions[0],
						new Position(
								positions[0].x + range * Math.cos(angle),
								positions[0].y + range * Math.sin(angle),
								positions[0].z),
						damage)) {
				damages.add(damage);
			}
		}
		
		return damages;
	}

}
