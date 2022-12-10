package com.tumble.tank5.testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.badlogic.gdx.utils.Queue;
import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.entities.Player;
import com.tumble.tank5.events.MovementEvent;
import com.tumble.tank5.game_object.GameObject;
import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.tiles.Wall;
import com.tumble.tank5.util.IDManager;
import com.tumble.tank5.weapons.Damage;
import com.tumble.tank5.weapons.DevWeapon;
import com.tumble.tank5.weapons.Weapon;
import com.tumble.tank5.world_logic.Game;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WeaponTests2 {
	
	private static final int WALL_HEALTH = new Wall(null).getHealth();
	
	Game g;
	GameWorld gW;
	Weapon weapon;
	Entity shooter;
	Position shooterPos, shootAt;
	
	public WeaponTests2() {
		g = new Game(true, 1);
		gW = g.getWorld();
	
		weapon = new DevWeapon(
			0,
			MovementEvent.MOVEMENT_TICKS,
			0,
			0,
			1,
			1,
			10,
			10 * Tile.TILE_SIZE);
		
		shooter = new Player(
			g,
			IDManager.nextID(g),
			"A",
			weapon);
	}
	
	// **** SIMPLE OBSTRUCTION DETECTION ****
	
	/**
	 * Checks that the right <code>GameObject</code>s are hit (identified as
	 * obstructions) for a bullet fired in a vertical line.
	 */
	@Test
	public void test_01() {
		gW.loadFromString(
				"  W    \n" +
				"  W    \n" +
				"       \n" +
				"     WW\n" +
				"  W WW ");
		
		shooterPos = new Position(
				2.5 * Tile.TILE_SIZE,
				1.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);

		gW.spawnEntity(shooter, shooterPos, g);
		
		// A position directly north of the shooter.
		shootAt = new Position(
				2.5 * Tile.TILE_SIZE,
				4.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		assert correctValues(
				toArray(gW.getLineObstructions(shooterPos, shootAt).first()),
				gW.tileAt(new Position(
						2.5 * Tile.TILE_SIZE,
						3.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)),
				gW.tileAt(new Position(
						2.5 * Tile.TILE_SIZE,
						4.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
	}
	
	/**
	 * Checks that the right <code>GameObject</code>s are hit (identified as
	 * obstructions) for a bullet fired in a horizontal line.
	 * 
	 * Uses the world conditions defined in {@link #test_01()}.
	 */
	@Test
	public void test_02() {
		// A position directly east of the shooter.
		shootAt = new Position(
				6.5 * Tile.TILE_SIZE,
				1.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		assert correctValues(
				toArray(gW.getLineObstructions(shooterPos, shootAt).first()),
				gW.tileAt(new Position(
						5.5 * Tile.TILE_SIZE,
						1.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)),
				gW.tileAt(new Position(
						6.5 * Tile.TILE_SIZE,
						1.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
	}

	/**
	 * Checks that the right <code>GameObject</code>s are hit (identified as obstructions) for a
	 * bullet fired at an angle.
	 * 
	 * Uses the world conditions defined in {@link #test_01()}.
	 */
	@Test
	public void test_03() {
		// A position in a non-cardinal direction from the shooter.
		shootAt = new Position(
				5.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		assert correctValues(
				toArray(gW.getLineObstructions(shooterPos, shootAt).first()),
				gW.tileAt(new Position(
						4.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)),
				gW.tileAt(new Position(
						5.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
	}
	
	/**
	 * Checks that <code>GameObject</code>s that are on the right line but beyond
	 * the target location are not detected as obstructions.
	 * 
	 * Uses the world conditions defined in {@link #test_01()}.
	 */
	@Test
	public void test_04() {
		// A position directly north of the shooter.
		shootAt = new Position(
				2.5 * Tile.TILE_SIZE,
				2.999999 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		assert correctValues(
				toArray(gW.getLineObstructions(shooterPos, shootAt).first()));
	}
	
	// **** ADVANCED OBSTRUCTION DETECTION ****
	
	@Test
	public void test_05() {
		gW.loadFromString(
				"  W    \n" +
				"  W    \n" +
				"       \n" +
				"     <W\n" +
				"  # W  " +
				"~" +
				"  W    \n" +
				"       \n" +
				"       \n" +
				"     <W\n" +
				"  # W  " +
				"~" +
				"  W    \n" +
				"       \n" +
				"       \n" +
				"     <W\n" +
				"  # W  ");
		
		shooterPos = new Position(
				2.5 * Tile.TILE_SIZE,
				1.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		gW.spawnEntity(shooter, shooterPos, g);
		
		shootAt = new Position(
				0.0 * Tile.TILE_SIZE,
				0.0 * Tile.TILE_SIZE,
				0.0 * Tile.TILE_SIZE);
		
		assert correctValues(
				toArray(gW.getLineObstructions(shooterPos, shootAt).first()),
				gW.tileAt(new Position(
						0.0 * Tile.TILE_SIZE,
						1.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
		
	}
	
	// **** SIMPLE DAMAGE REGISTRY ****
	
	/**
	 * Checks that only the first of two targets is damaged by a non-penetrating
	 * bullet (we shouldn't need to test individual directions, as
	 * {@link Weapon#singleBullet(int, double, GameWorld, Position, Position, int)}
	 * relies on the above tests working).
	 */
	@Test
	public void test_20() {
		gW.loadFromString(
				"  W    \n" +
				"  W    \n" +
				"       \n" +
				"     WW\n" +
				"  W WW ");
		
		shooterPos = new Position(
				2.5 * Tile.TILE_SIZE,
				1.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);

		gW.spawnEntity(shooter, shooterPos, g);
		
		// A position directly north of the shooter.
		shootAt = new Position(
				2.5 * Tile.TILE_SIZE,
				4.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		assert correctValues(
				Weapon.singleBullet(
						shooter.getID(),
						gW,
						shooterPos,
						shootAt,
						WALL_HEALTH / 2),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								3.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						WALL_HEALTH / 2,
						null));
	}
	
	/**
	 * EDGE CASE: Check that a bullet with exactly enough damage to kill the first
	 * target does not continue on to do 0 damage to a second one.
	 */
	@Test
	public void test_21() {
		assert correctValues(
				Weapon.singleBullet(
						shooter.getID(),
						gW,
						shooterPos,
						shootAt,
						WALL_HEALTH),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								3.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						WALL_HEALTH,
						null));
	}
	
	/**
	 * Check that both targets are hit by a penetrating bullet (one that has more
	 * than enough damage to kill the first target, as well as damaging the second).
	 */
	@Test
	public void test_22() {
		assert correctValues(
				Weapon.singleBullet(
						shooter.getID(),
						gW,
						shooterPos,
						shootAt,
						WALL_HEALTH * 3 / 2),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								3.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						WALL_HEALTH,
						null),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								4.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						WALL_HEALTH / 2,
						null));
	}
	
	private static <T> T[] toArray(Queue<T> queue) {
		@SuppressWarnings("unchecked")
		T[] array = (T[]) new Object[queue.size];
		
		int i = 0;
		for (T item : queue) {
			array[i] = item;
			i++;
		}
		
		return array;
	}
	
	@SafeVarargs
	private static <T> boolean correctValues(T[] hits, T ... desiredHits) {
		boolean failed = false;
		
		int i = 0;
		for (; i < hits.length; i++) {
			if (i >= desiredHits.length) {
				printIncorrect("Extra (unplanned)", hits[i], failed);
				failed = true;
			} else if (!hits[i].equals(desiredHits[i])) {
				printIncorrect("Desired", desiredHits[i], failed);
				printIncorrect("Actual", hits[i], true);
				failed = true;
			}
		}
		
		for (; i < desiredHits.length; i++) {
			printIncorrect("Missed", desiredHits[i], failed);
			failed = true;
		}
		
		return !failed;
	}
	
	private static <T> void printIncorrect(String message, T value, boolean alreadyFailed) {
		if (!alreadyFailed) {
			System.out.println("Test " + determineTestNumber(1) + " failed!");
		}
		
		if (value instanceof GameObject) {
			GameObject hit = (GameObject) value;
			System.out.println(
					message +
					" hit of " +
					hit +
					"(" +
					(hit instanceof Entity ? "E" : "T") +
					") at location " +
					(hit.getPosition() != null ? hit.getPosition() : "null"));
		} else if (value instanceof Damage) {
			System.out.println(
					message +
					" damage of " +
					value);
		}
	}
	
	/**
	 * Determine the number of the test being executed based on its method name.
	 * 
	 * @param nestedCall - how much this method should adjust the index in the
	 *                   call-stack to search for the actual test method (normally
	 *                   searches at an index of 3 - make <code>nestedCall</code> 0
	 *                   to search at this level).
	 *
	 * @return the number of the test being executed.
	 */
	private static int determineTestNumber(int nestedCall) {
		StackTraceElement[] e = Thread.currentThread().getStackTrace();
		String line = e[3 + nestedCall].toString();
		int numStart = line.indexOf('_') + 1;
		return Integer.valueOf(line.substring(numStart, numStart + 2));
	}
}
