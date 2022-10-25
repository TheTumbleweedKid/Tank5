package com.tumble.tank5.weapons;

import java.util.ArrayList;
import java.util.List;

import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.util.GameUtils;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

/**
 * Fires a spray of (simultaneous) bullets at a target cell (and probably to
 * cells either side of it!) as soon as the firer has stopped moving.
 * 
 * @author Tumbl
 *
 */
public class Shotgun extends Weapon {
	private int damage, burstSize;
	private double spread, baseRange, rangeVariation;
	
	public Shotgun() {
		super(0, 1, 30, 60);
		
		// Medium/low per-pellet damage (very high damage overall).
		damage = 18;
		
		// Here, 'burstSize' refers to the number of pellets fired.
		burstSize = 8;
		
		// Wide pellet spread.
		spread = Math.toRadians(5);
		
		// Short/abysmal range.
		baseRange = 2.75 * Tile.TILE_SIZE;
		rangeVariation = 1.0 * Tile.TILE_SIZE;
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
			
			for (Damage damage :Weapon.singleBullet(
					ownerId,
					0.5,
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
