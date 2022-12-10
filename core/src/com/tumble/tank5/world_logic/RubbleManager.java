package com.tumble.tank5.world_logic;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.events.DamageEvent;
import com.tumble.tank5.events.Event;
import com.tumble.tank5.tiles.Air;
import com.tumble.tank5.tiles.Rubble;
import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.tiles.Tile.TileType;
import com.tumble.tank5.weapons.Damage;

public class RubbleManager {
	private GameWorld gW;
	private Set<Rubble> rubble;
	
	public RubbleManager(GameWorld gW) {
		this.gW = gW;
		rubble = new HashSet<Rubble>();
	}

	DirectionVector down = new DirectionVector(0, 0, -1);
	
	public void makeRubble(Tile t, Entity attacker,  Queue<Event> eventStream) {
		if (t.getType() == TileType.AIR) return;
		
		Position pos = t.getPosition();
		Position newPos = t.getPosition();
		
		while (true) {
			gW.setTile(pos, Air.AIR);
			
			newPos = newPos.move(down);
			
			Tile tileBelow = gW.tileAt(newPos);
			Entity entityBelow = gW.entityAt(pos);
			
			if (tileBelow == null || tileBelow.stopsFalling()) {
				gW.setTile(pos, new Rubble(t, pos));
				break;
			} else if (tileBelow.getType() == TileType.RUBBLE) {
				((Rubble) tileBelow).addToPile(t);
				break;
			} else if (entityBelow != null) { // <- Technically optional 'else' here.
				// An Entity is on the Tile below, and the Tile below does not stop things
				// falling through it, so the Entity takes crushing damage (must be a lethal
				// dose if the Entity will be trapped inside an obstructive object).

				eventStream.offer(
						new DamageEvent(
								attacker,
								new Damage(
										entityBelow,
										entityBelow.getHealth(),
										newPos.tileCentre())));
			}
			
			pos = newPos;
		}
		
	}
	
	public void clear() {
		rubble.clear();
	}
}
