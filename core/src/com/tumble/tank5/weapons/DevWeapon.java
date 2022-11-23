package com.tumble.tank5.weapons;

import java.util.ArrayList;
import java.util.List;

import com.tumble.tank5.util.GameUtils;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

public class DevWeapon extends Weapon {
	private int damage, burstSize;
	private double betweenShots, spread, baseRange, rangeVariation;
	
	public DevWeapon(
			int cooldown,
			int reloadDuration,
			int magSize,
			int reserveBullets,
			int damage,
			int burstSize,
			double betweenShots,
			double range) {
		super(cooldown, reloadDuration, magSize, reserveBullets);
		
		// Custom damage.
		this.damage = damage;
		
		// Custom burst size.
		this.burstSize = burstSize;
		
		// Custom interval between shots.
		this.betweenShots = betweenShots;
		
		// Perfect accuracy (for consistent test results).
		spread = 0;
		
		// Custom range (with no variation, for consistent test results).
		baseRange = range;
		rangeVariation = 0;
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
