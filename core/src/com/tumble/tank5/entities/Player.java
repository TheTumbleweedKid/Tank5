package com.tumble.tank5.entities;

import com.tumble.tank5.world_logic.Game;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

public class Player extends Entity {
	private String name;
	
	public Player(Game game, int id, String name) {
		super(id, game);
		
		this.name = name != null ? name : "";
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
	
}
