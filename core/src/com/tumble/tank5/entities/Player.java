package com.tumble.tank5.entities;

import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.world_logic.Game;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

public class Player extends Entity {
	private static final int STARTING_HEALTH = 100;
	private String name;
	
	public Player(Game game, int id, String name) {
		super(id, game, (float) (0.25 * Tile.TILE_SIZE));
		
		this.name = name != null ? name : "";
	}
	
	@Override
	public void spawn(Position pos) {
		spawn(pos, STARTING_HEALTH);
	}

	@Override
	public boolean addAttack(GameWorld gW, Position... positions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addWeaponSwitch(GameWorld gW) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addReload(GameWorld gW) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		return false;
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