package com.tumble.tank5.game_object.tiles;

import java.util.ArrayList;
import java.util.List;

import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.util.DirectionVector;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.game_n_world.GameObject;

public class Rubble extends Tile {
	/**
	 * The minimum weight for a <code>Rubble Tile</code> to become an obstruction
	 * at.
	 */
	public static final int OBSTRUCTIVE_THRESHOLD = 5;
	
	List<TileType> types;

	/**
	 * 
	 * @param from - could be a destroyed Tile
	 * @param pos
	 * @param attacker
	 */
	public Rubble(GameObject from, Position pos, Entity attacker) {
		super(TileType.RUBBLE, pos, 1, from instanceof Tile ? ((Tile) from).weight : 1);
		
		damage(1, attacker); // Deals 1 damage to set our attacker.
		
		types = new ArrayList<TileType>();
		
		if (from instanceof Entity) {
			types.add(TileType.CORPSE);
		} else if (from instanceof Rubble) {
			types.addAll(((Rubble) from).types);
		} else {
			types.add(((Tile) from).getType());
		}
	}

	@Override
	public boolean isObstruction(DirectionVector dir) {
		return weight >= Rubble.OBSTRUCTIVE_THRESHOLD;
	}

	@Override
	public boolean stopsBullets() {
		return weight >= 5;
	}

	@Override
	public boolean stopsFalling() {
		return true;
	}

	@Override
	public String toString() {
		return "R";
	}

}
