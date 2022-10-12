package com.tumble.tank5.weapons;

import java.util.HashMap;
import java.util.Map;

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

	@Override
	public Map<GameObject, Integer> fire(int ownerId, GameWorld gW, Position... positions) {
		Map<GameObject, Integer> victims = new HashMap<GameObject, Integer>();
		
		for (int i = 0; i < ; i++) {
			Weapon.singleBullet(ownerId, gW, positions[0], positions[1], , victims);
		}
		
		return victims;
	}

}
