package com.tumble.tank5.game_object.entities;

import com.tumble.tank5.game_object.tiles.Tile;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.weapons.Weapon;
import com.tumble.tank5.world_logic.game_n_world.Game;

public class Player extends Entity {
	private static final int STARTING_HEALTH = 100;
	private String name;

	/**
	 * Creates a new <code>Player</code> with a given name and ID number (not
	 * necessarily determined by the <code>id</code> parameter given here).
	 * 
	 * @param game
	 * 
	 * @param id
	 * 
	 * @param name
	 * 
	 * @param weapons
	 * 
	 * @see Entity#Entity(int, Game, float, Weapon...)
	 */
	public Player(Game game, int id, String name, Weapon ... weapons) {
		super(id, game, (float) (0.25 * Tile.TILE_SIZE), weapons);
		
		this.name = name != null ? name : "";
	}
	
	@Override
	public void spawn(Position pos) {
		spawn(pos, STARTING_HEALTH);
	}

	@Override
	public boolean isDead() {
		return getHealth() <= 0;
	}
	
	@Override
	public String toString() {
		return name.isEmpty() ? "P" : ("" + name.charAt(0));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) return false;
		
		Player other = (Player) obj;
		
		return other.getID() == getID();
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int hash = getClass().hashCode();
		
		hash = hash * prime + getID();
		
		return hash;
	}
}
