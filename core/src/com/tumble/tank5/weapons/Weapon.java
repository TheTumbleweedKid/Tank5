package com.tumble.tank5.weapons;

import java.util.List;
import java.util.Map;

import com.tumble.tank5.util.GameError;
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
	public abstract Map<GameObject, Integer> fire(
			int ownerId,
			GameWorld gW,
			Position ... positions);
	
	
	protected static void singleBullet(
			int ownerId,
			double time,
			GameWorld gW,
			Position from,
			Position to,
			int damage,
			Map<GameObject, Integer> victims) {
		List<GameObject> hits = gW.getObstructions(from, to, time);
	}
}
