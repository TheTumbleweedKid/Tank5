package com.tumble.tank5.world_logic;

import java.util.concurrent.ThreadLocalRandom;

import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.game_n_world.Game;

public class GodEntity extends Entity {
	
	public final String name, verb;
	
	private Game dominion;

	public GodEntity(int entityID, Game game) {
		super(entityID, game, 0);
		
		name = generateGodName();
		verb = getSmitingVerb(name);
	}

	/**
	 * Do nothing. Gods can't be spawned!
	 */
	@Override
	public void spawn(Position pos) {
		
	}

	/**
	 * Gods can't be killed!
	 * 
	 * @return Always returns <code>false</code>.
	 */
	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) return false;
		
		GodEntity other = (GodEntity) obj;
		
		return name.equals(other.name) && dominion.equals(other.dominion);
	}

	@Override
	public int hashCode() {
		int prime = 1733;
		int hash = getClass().hashCode();
		
		hash = hash * prime + name.hashCode();
		return hash * prime + dominion.hashCode();
	}
	
	/** 
	 * The available names for the <code>GodEntities</code> that patrol the servers...
	 * 
	 * Internal invariant that this is the same length as {@link GodEntity#VERBS}.
	 */
	private static final String[] NAMES = {
			"GOD",
			"TITUS",
			"THE GREAT ALMIGHTY",
			"THE UNKNOWING VOID",
			"THE 5TH SET"
	};
	
	public static String generateGodName() {
		return NAMES[ThreadLocalRandom.current().nextInt(NAMES.length)];
	}
	
	/** 
	 * The available verbs for the acts of smiting that <code>GodEntities</code> can perform...
	 * 
	 * Internal invariant that this is the same length as {@link GodEntity#NAMES}.
	 */
	private static final String[] VERBS = {
			"smote", // GOD
			"was born on top of", // TITUS
			"abolished", // THE GREAT ALMIGHTY
			"removed", // THE UNKNOWING VOID
			"smoked" // THE 5TH SET
	};
	
	public static String getSmitingVerb(String godName) {
		int index = 0;
		
		for (int i = 0; i < NAMES.length; i++) {
			if (NAMES[i].equals(godName)) {
				index = i;
				break;
			}
		}
		
		return VERBS[index];
	}
}
