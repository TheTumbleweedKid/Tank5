package com.tumble.tank5.weapons;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Queue;
import com.tumble.tank5.events.MovementEvent;
import com.tumble.tank5.game_object.GameObject;
import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.game_object.tiles.Tile;
import com.tumble.tank5.util.Pair;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

/**
 * Fires a hitscan rocket that deals high direct-hit ('contact') and
 * area-of-effect ('blast') damage to nearby <code>Tile</code>s and
 * <code>Entities</code>. Cannot be fired if the shooter moves during their
 * movement turn. Watch out for splash damage!
 * 
 * @author Tumbl
 *
 */
public class RPG extends Weapon {
	private int blastDamage;
	private double blastRadius, tileDamageBonus;

	protected RPG() {
		super(
				80, // High damage. Is this a bit high? 40?
				(int) (MovementEvent.MOVEMENT_TICKS * 1.7), // Hesitate briefly after moving, then fire.
				0, // Single-fire (interval between shots is irrelevant).
				3, // 1-round reload.
				1, // Single-fire (i.e., 1-shot burst).
				1, // 1-shot "magazine".
				4, // Start with an extra 4 rockets.
				6 * Tile.TILE_SIZE, // Medium/long range.
				0.25 * Tile.TILE_SIZE, // Very consistent range.
				1.5 // High accuracy.
		);
		
		blastDamage = 45;
		blastRadius = 3 * Tile.TILE_SIZE;
		
		tileDamageBonus = 1.3;
	}

	@Override
	public Damage[] fire(int ownerId, GameWorld gW, Position from, Position to) {		
		Pair<Queue<GameObject>, Queue<Position>> lineHits = gW.getLineObstructions(from, to);
		
		if (lineHits.first().size == 0) return new Damage[0]; // Do not detonate on a miss.
		
		GameObject hit = lineHits.first().first();
		Position epicentre = lineHits.second().first();

		List<Damage> damages = new ArrayList<Damage>();
		
		damages.add(
				new Damage(
						hit,
						(int) (damage * (hit instanceof Entity ? 1 : tileDamageBonus)),
						epicentre));
		
		// TODO: Make this less bloody primitive!
		for (Pair<GameObject, Double> pair : gW.getSphereObstructions(epicentre, blastRadius)) {
			damages.add(
					new Damage(
							pair.first(),
							(int) ((int) (blastDamage * pair.second() / blastRadius) * (hit instanceof Entity ? 1 : tileDamageBonus)),
							pair.first().getPosition()));
		}
		
		return damages.toArray(new Damage[0]);
	}

}
