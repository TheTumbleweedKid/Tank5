package com.tumble.tank5.game_object.entities;

import com.tumble.tank5.util.Position;

public class Action {
	public enum ActionType {
		FIRE,
		SWITCH_WEAPON,
		RELOAD,
		NONE
	}
	
	private ActionType type;
	private Position[] positions;
	
	public Action(ActionType type, Position ... positions) {
		if (type == null || (type == ActionType.FIRE && positions.length < 2)) {
			this.type = ActionType.NONE;
			this.positions = new Position[0];
		} else {
			this.type = type;
			
			this.positions = new Position[positions.length];
			for (int i = 0; i < positions.length; i++) this.positions[i] = positions[i];
		}
	}
	
	public ActionType getType() {
		return type;
	}
	
	public Position[] getPositions() {
		return positions;
	}
	
	public Action copy() {
		return new Action(type, positions);
	}
}
