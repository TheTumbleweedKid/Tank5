package com.tumble.tank5.weapons;

import java.util.ArrayList;
import java.util.List;

import com.tumble.tank5.events.FiringEvent;
import com.tumble.tank5.events.MovementEvent;
import com.tumble.tank5.game_object.tiles.Tile;
import com.tumble.tank5.util.GameUtils;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

/**
 * Fires a spray of (simultaneous) bullets at a target cell (and probably to
 * cells either side of it!) as soon as the firer has stopped moving.
 * 
 * @author Tumbl
 *
 */
public class Shotgun extends Weapon {
	
	public Shotgun() {
		super(
				18, // Medium/low per-pellet damage (very high damage overall).
				MovementEvent.MOVEMENT_TICKS, // Fire as soon as movement is done.
				0, // No interval between shots (it's a shotgun).
				1, // 1-round reload.
				8, // Here, 'burstSize' refers to the number of pellets fired per shot.
				5, // 5-shot magazine.
				10, // Start with 2 extra magazines' worth of ammo.
				2.75 * Tile.TILE_SIZE, // Short/abysmal range.
				1.0 * Tile.TILE_SIZE, // Poor range consistency.
				10 // Wide pellet spread.
		);
	}
	
	@Override
	public FiringEvent[] getFiringEvents(int ownerId, GameWorld gW, Position... positions) {
		double angle = Math.atan2(
				positions[1].y - positions[0].y,
				positions[1].x - positions[0].x);
		double zRatio = 
				(positions[1].z - positions[0].z)
				/ Math.sqrt(
						(positions[1].x - positions[0].x) * (positions[1].x - positions[0].x)
						+ (positions[1].y - positions[0].y) * (positions[1].y - positions[0].y));
		
		return new FiringEvent[] {
				new FiringEvent(
						fireDelay,
						gW.getEntity(ownerId),
						positions[0],
						new Position(
								positions[0].x + baseRange * Math.cos(angle),
								positions[0].y + baseRange * Math.sin(angle),
								positions[0].z + baseRange * zRatio))
		};
	}
	
	@Override
	public Damage[] fire(int ownerId, GameWorld gW, Position from, Position to) {
		List<Damage> damages = new ArrayList<Damage>();
		
		double baseAngle = Math.atan2(
				to.y - from.y,
				to.x - from.x);
		double zRatio = 
				(to.z - from.z)
				/ Math.sqrt(
						(to.x - from.x) * (to.x - from.x)
						+ (to.y - from.y) * (to.y - from.y));
		
		for (int i = 0; i < burstSize; i++) {
			double range = baseRange + GameUtils.random(rangeVariation);
			double angle = baseAngle + GameUtils.random(spread);
			
			for (Damage damage : Weapon.singleBullet(
					ownerId,
					gW,
					from,
					new Position(
							from.x + range * Math.cos(angle),
							from.y + range * Math.sin(angle),
							from.z + range * zRatio),
					damage)) {
				damages.add(damage);
			}
		}

		return damages.toArray(new Damage[0]);
	}

}
