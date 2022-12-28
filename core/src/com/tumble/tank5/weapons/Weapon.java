package com.tumble.tank5.weapons;

import com.badlogic.gdx.utils.Queue;
import com.tumble.tank5.events.FiringEvent;
import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.game_object.tiles.Tile;
import com.tumble.tank5.util.GameUtils;
import com.tumble.tank5.util.Pair;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.game_n_world.GameObject;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

/**
 * Encompasses all weapons in the game - those wielded by NPCs and <code>Player</code>s alike.
 * All weapons are ranged hitscan weapons (technically the RPG is a form of hitscan).
 * 
 * @author Tumbl
 */
public abstract class Weapon {
	protected final int damage, fireDelay, cooldown, reloadDuration, burstSize, magSize;
	protected final double baseRange, rangeVariation, spread;
	protected int lastFire, reloadStart, magBullets, reserveBullets;
	
	private boolean isReloading;
	
	/**
	 * 
	 * @param damage         - the amount of damage (or less, depending on
	 *                       penetration) to be done to a target upon contact.
	 * 
	 * @param fireDelay      - minimum number of ticks that should pass before first
	 *                       shot is fired in a round.
	 * 
	 * @param cooldown       - minimum number of ticks that should pass before next
	 *                       shot is fired in a round.
	 * 
	 * @param reloadDuration - number of <code>Round</code>s that reloading should
	 *                       last for.
	 * 
	 * @param burstSize      - number of shots that should be fired in round.
	 * 
	 * @param magSize        - number of shots before weapon must be reloaded (from
	 *                       a full magazine).
	 * 
	 * @param reserveBullets - number of shots in user's reserve (i.e., that aren't
	 *                       currently loaded in the weapon.
	 * 
	 * @param baseRange      - how far (in world units) the average shot should go
	 *                       (if not obstructed).
	 * 
	 * @param rangeVariation - maximum amount of variation from the
	 *                       <code>baseRange</code> for each shot (range is
	 *                       ~uniformly chosen from [<code>baseRange</code> -
	 *                       <code>rangeVariation</code>, <code>baseRange</code> +
	 *                       <code>rangeVariation</code>))
	 * 
	 * @param spread         - maximum deviation angle from each shot's target angle
	 *                       in <b>degrees</b>.
	 */
	protected Weapon(
			int damage,
			int fireDelay,
			int cooldown,
			int reloadDuration,
			int burstSize,
			int magSize,
			int reserveBullets,
			double baseRange,
			double rangeVariation,
			double spread) {
		this.damage = damage;
		
		this.fireDelay = fireDelay;
		this.cooldown = cooldown;
		lastFire = 0;

		this.reloadDuration = reloadDuration;
		reloadStart = 0;
		
		this.burstSize = burstSize;
		
		this.magSize = magSize;
		magBullets = magSize;
		this.reserveBullets = reserveBullets;
		
		this.baseRange = baseRange;
		this.rangeVariation = rangeVariation;
		this.spread = spread;
		
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
	
	public boolean isInRange(Position from, Position to) {
		return (to.x - from.x) * (to.x - from.x) + (to.y - from.y) * (to.y - from.y)
				+ (to.z - from.z) * (to.z - from.z) <= baseRange * baseRange;
	}
	
	public FiringEvent[] getFiringEvents(int ownerId, GameWorld gW, Position... positions) {
		FiringEvent[] firingEvents = new FiringEvent[burstSize];
		
		double baseAngle = Math.atan2(
				positions[1].y - positions[0].y,
				positions[1].x - positions[0].x);
		double zRatio = 
				(positions[1].z - positions[0].z)
				/ Math.sqrt(
						(positions[1].x - positions[0].x) * (positions[1].x - positions[0].x)
						+ (positions[1].y - positions[0].y) * (positions[1].y - positions[0].y));
		
		for (int i = 0; i < burstSize; i++) {
			double range = baseRange + GameUtils.random(rangeVariation);
			double angle = baseAngle + GameUtils.random(spread);
			
			firingEvents[i] = new FiringEvent(
					fireDelay + i * cooldown,
					gW.getEntity(ownerId),
					positions[0],
					new Position(
							positions[0].x + range * Math.cos(angle),
							positions[0].y + range * Math.sin(angle),
							positions[0].z + range * zRatio));
		}
		
		return firingEvents;
	}
	
	/**
	 * 
	 * @param ownerId - the ID number of the <code>Entity</code> who is firing the
	 *                weapon (i.e., its 'owner').
	 * 
	 * @param gW      - the <code>GameWorld</code> the owner is a part of.
	 * 
	 * @param from    - the <code>Position</code> the bullet should start at.
	 * 
	 * @param to      - the <code>Position</code> the bullet should end at (if it
	 *                doesn't end up hitting anything).
	 * 
	 * @return a list of all the <code>Damage</code>s that would be done if this
	 *         <code>Weapon</code> fired from <code>from</code> to <code>to</code>.
	 */
	public Damage[] fire(int ownerId,
			GameWorld gW,
			Position from,
			Position to) {
		return Weapon.singleBullet(
						ownerId,
						gW,
						from,
						to,
						damage);
	}
	
	/**
	 * Should be <code>protected</code>.
	 * 
	 * @param ownerId - the ID
	 * 
	 * @param time
	 * 
	 * @param gW
	 * 
	 * @param from    - the start <code>Position</code> of the bullet. Presumably
	 *                slightly off-centre from the <code>Tile</code> it is being
	 *                fired from, but it makes no difference;
	 *                <code>GameObject</code>s on the same <code>Tile</code> as this
	 *                location (including the <code>Tile</code> itself) are ignored
	 *                by {@link GameWorld#getLineObstructions(Position, Position)}.
	 * 
	 * @param to      - the end <code>Position</code> of the bullet. Not
	 *                (necessarily) where the used clicked to aim it, but where the
	 *                bullet should stop if it doesn't hit anything (that it can't
	 *                penetrate though). All range and angle variations should have
	 *                already have been applied to derive the location of this
	 *                <code>Position</code>.
	 * 
	 * @param damage  - the TOTAL amount of damage the bullet has to dish out to its
	 *                victim(s).
	 * 
	 * @return
	 */
	public static Damage[] singleBullet(
			int ownerId,
			GameWorld gW,
			Position from,
			Position to,
			int damage) {
		Entity owner = gW.getEntity(ownerId);
		Tile standingIn = gW.tileAt(owner.getPosition());
		// Inefficient, as may collect more Tiles than necessary.
		Pair<Queue<GameObject>, Queue<Position>> hits = gW.getLineObstructions(from, to);
		
		int damageRemaining = damage;
		int numHits = 0;
		
		for (int i = 0; i < hits.first().size; i++) {
			if (!owner.equals(hits.first().get(i)) && !standingIn.equals(hits.first().get(i))) {
				numHits++;
				if (hits.first().get(i).getHealth() < damageRemaining) {
					damageRemaining -= hits.first().get(i).getHealth();
				} else {
					break;
				}
			}
		}
		
		Damage[] damages = new Damage[numHits];

		for (int i = 0; i < numHits; i++) {
			damages[i] = new Damage(
					hits.first().get(i),
					i < numHits - 1 ? hits.first().get(i).getHealth() : damageRemaining,
					hits.second().get(i));
		}
		
		return damages;
	}
}
