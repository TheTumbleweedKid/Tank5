package com.tumble.tank5.weapons;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.utils.Queue;
import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.util.Pair;
import com.tumble.tank5.world_logic.GameObject;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

/**
 * Encompasses all weapons in the game - those wielded by NPCs and <code>Player</code>s alike.
 * All weapons are ranged hitscan weapons (technically the RPG is a form of hitscan).
 * 
 * @author Tumbl
 */
public abstract class Weapon {
	protected final int cooldown, reloadDuration, magSize;
	protected int lastFire, reloadStart, magBullets, reserveBullets;
	
	private boolean isReloading;
	
	protected Weapon(int cooldown, int reloadDuration, int magSize, int reserveBullets) {
		this.cooldown = cooldown;
		lastFire = 0;

		this.reloadDuration = reloadDuration;
		reloadStart = 0;
		
		this.magSize = magSize;
		magBullets = magSize;
		this.reserveBullets = reserveBullets;
		
		isReloading = false;
	}

	public final boolean manualReload(int currentRound) {
		if (!isReloading && magBullets < magSize && reserveBullets > 0) {
			reloadStart = currentRound;
			reserveBullets += magBullets;
			magBullets = 0;
			isReloading = true;

			return true;
		}
		return false;
	}

	public final void updateReload(int currentRound) {
		if (isReloading) {
			if (currentRound - reloadStart >= reloadDuration) {
				magBullets = Math.min(magSize, reserveBullets);
				reserveBullets -= Math.min(magSize, reserveBullets);
				isReloading = false;
			}
		} else if (magBullets <= 0 && reserveBullets > 0) {
			reloadStart = currentRound;
			isReloading = true;
		}
	}

	public final boolean ableToFire(int currentRound) {
		return !isReloading && magBullets > 0 && currentRound - lastFire >= cooldown;
	}

	public final boolean ableToReload() {
		return !isReloading && magBullets < magSize && reserveBullets > 0;
	}
	
	/**
	 * Server-side method. Fires the weapon, returning a map of all the victims to
	 * the amounts of damage they took from the firing (this can then be handled
	 * properly (e.g., with gravity for Tiles and Entities who are now standing on a
	 * destroyed block) by the Game. Called once per <code>Round</code>, at the
	 * start of when the input is enacted (although bullets may be fired throughout
	 * the enactment phase).
	 * 
	 * @param ownerId   - the ID number of the <code>Entity</code> who is firing the
	 *                  weapon (i.e., its 'owner').
	 * 
	 * @param gW        - the <code>GameWorld</code> the owner is a part of.
	 * 
	 * @param positions - any <code>Position</code>s that may be useful for
	 *                  calculating damage. The number and order of these arguments
	 *                  required may vary depending on the type of weapon.
	 * 
	 * @return a map of all the victims to their damages.
	 */
	public abstract List<Damage> fire(
			int ownerId,
			GameWorld gW,
			Position ... positions);
	
	
	public static Damage[] singleBullet(
			int ownerId,
			double time,
			GameWorld gW,
			Position from,
			Position to,
			int damage) {
		Entity owner = gW.getEntity(ownerId);
		Tile standingOn = gW.tileAt(owner.getPosition());
		// Inefficient, as may collect more Tiles than necessary.
		GameObject[] hits = gW.getObstructions(from, to, time);
		
		int damageRemaining = damage;
		int numHits = 0;
		
		for (GameObject gO : hits) {
			if (!owner.equals(gO) && !standingOn.equals(gO)) {
				if (gO.getHealth() <= damageRemaining) {
					damageRemaining -= gO.getHealth();
					numHits++;
				} else {
					numHits++;
				}
				
				if (damageRemaining == 0) {
					break;
				}
			}
		}
		
		Damage[] damages = new Damage[numHits];

		for (int i = 0; i < numHits; i++) {
			damages[i] = new Damage(
					hits[i],
					i < numHits - 1 ? hits[i].getHealth() : damageRemaining);
		}
		
		return damages;
	}
}
