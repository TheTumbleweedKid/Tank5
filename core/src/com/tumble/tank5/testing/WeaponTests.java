package com.tumble.tank5.testing;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.utils.Queue;
import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.entities.Player;
import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.tiles.Wall;
import com.tumble.tank5.util.IDManager;
import com.tumble.tank5.weapons.AssaultRifle;
import com.tumble.tank5.weapons.Damage;
import com.tumble.tank5.weapons.DevWeapon;
import com.tumble.tank5.weapons.Shotgun;
import com.tumble.tank5.weapons.SniperRifle;
import com.tumble.tank5.weapons.SubMachineGun;
import com.tumble.tank5.weapons.Weapon;
import com.tumble.tank5.world_logic.Game;
import com.tumble.tank5.world_logic.GameObject;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;


public class WeaponTests {
	
	private static final int WALL_HEALTH = new Wall(null).getHealth();

	/**
	 * Ensures a simple, single-level world registers hits (on
	 * <code>GameObject</code>s) and <code>Damage</code>s correctly for bullets
	 * fired from the centre of <code>Tile</code>s to the centres of others.
	 */
	@Test
	public void test_01() {
		Game g = new Game(true, 1);
		GameWorld gW = g.getWorld();
		
		gW.loadFromString(
				"  W    \n" +
				"  W    \n" +
				"       \n" +
				"     WW\n" +
				"  W WW ");
		
		Weapon weapon = new DevWeapon(
				0,
				0,
				1,
				0,
				0,
				1,
				0.0,
				10 * Tile.TILE_SIZE);
		
		Player shooter = new Player(
				g,
				IDManager.nextID(g),
				"A",
				weapon);
		
		Position playerPos = new Position(
				2.5 * Tile.TILE_SIZE,
				1.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		gW.spawnEntity(shooter, playerPos, g);
		
		// A position directly north of the shooter.
		Position shootAt1 = new Position(
				2.5 * Tile.TILE_SIZE,
				4.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		// A position directly east of the shooter.
		Position shootAt2 = new Position(
				6.5 * Tile.TILE_SIZE,
				1.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		// A position in a non-cardinal direction from the shooter.
		Position shootAt3 = new Position(
				5.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);

		// OBSTRUCTION-DETECTION:
		
		// Check the right GameObjects are hit (identified as obstructions) for a bullet
		// fired in a vertical line.
		assert correctValues(
				"a",
				gW.getObstructions(playerPos, shootAt1, 0),
				gW.tileAt(new Position(
						2.5 * Tile.TILE_SIZE,
						3.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)),
				gW.tileAt(new Position(
						2.5 * Tile.TILE_SIZE,
						4.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
		// Check the right GameObjects are hit (identified as obstructions) for a bullet
		// fired in a horizontal line.
		assert correctValues(
				"b",
				gW.getObstructions(playerPos, shootAt2, 0),
				gW.tileAt(new Position(
						5.5 * Tile.TILE_SIZE,
						1.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)),
				gW.tileAt(new Position(
						6.5 * Tile.TILE_SIZE,
						1.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
		// Check the right GameObjects are hit (identified as obstructions) for a bullet
		// fired at an angle.
		assert correctValues(
				"c",
				gW.getObstructions(playerPos, shootAt3, 0),
				gW.tileAt(new Position(
						4.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)),
				gW.tileAt(new Position(
						5.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
		
		// DAMAGE-REGISTRY:

		// Check that only the first of two targets is damaged by a non-penetrating
		// bullet (we shouldn't need to test individual directions, as
		// Weapon.singleBullet() relies on the above tests working).
		assert correctValues(
				"d",
				Weapon.singleBullet(
						shooter.getID(),
						0.0,
						gW,
						playerPos,
						shootAt1,
						WALL_HEALTH / 2),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								3.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						22));
		// EDGE CASE: Check that a bullet with exactly enough damage to kill the first
		// target does not continue on to do 0 damage to a second one.
		assert correctValues(
				"e",
				Weapon.singleBullet(
						shooter.getID(),
						0.0,
						gW,
						playerPos,
						shootAt1,
						WALL_HEALTH),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								3.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						45));
		// Check that both targets are hit by a penetrating bullet (one that has more
		// than enough damage to kill the first target, as well as damaging the second).
		assert correctValues(
				"f",
				Weapon.singleBullet(
						shooter.getID(),
						0.0,
						gW,
						playerPos,
						shootAt1,
						WALL_HEALTH * 3 / 2),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								3.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						45),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								4.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						22));
	}
	
	/**
	 * Ensures a more complex, multiple-storeyed world registers hits (on
	 * <code>GameObject</code>s) and <code>Damage</code>s correctly (not necessarily
	 * for bullets fired from or to the centres of <code>Tile</code>s).
	 */
	@Test
	public void test_02() {
		Game g = new Game(true, 1);
		GameWorld gW = g.getWorld();
		
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
		
		Weapon weapon = new DevWeapon(
				0,
				0,
				1,
				0,
				0,
				1,
				0.0,
				10 * Tile.TILE_SIZE);
		
		Player shooter = new Player(
				g,
				IDManager.nextID(g),
				"A",
				weapon);
		
		Position playerPos = new Position(
				2.5 * Tile.TILE_SIZE,
				1.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		gW.spawnEntity(shooter, playerPos, g);
		
		// A position directly north of the shooter.
		Position shootAt1 = new Position(
				2.5 * Tile.TILE_SIZE,
				4.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		// A position directly east of the shooter.
		Position shootAt2 = new Position(
				2.5 * Tile.TILE_SIZE,
				4.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		// A position in a non-cardinal direction from the shooter.
		Position shootAt3 = new Position(
				4.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);

		// OBSTRUCTION-DETECTION:
		
		// Check the right GameObjects are hit (identified as obstructions) for a bullet
		// fired in a vertical line.
		assert correctValues(
				"a",
				gW.getObstructions(playerPos, shootAt1, 0),
				gW.tileAt(new Position(
						2.5 * Tile.TILE_SIZE,
						3.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)),
				gW.tileAt(new Position(
						2.5 * Tile.TILE_SIZE,
						4.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
		// Check the right GameObjects are hit (identified as obstructions) for a bullet
		// fired in a horizontal line.
		assert correctValues(
				"b",
				gW.getObstructions(playerPos, shootAt2, 0),
				gW.tileAt(new Position(
						4.5 * Tile.TILE_SIZE,
						1.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)),
				gW.tileAt(new Position(
						5.5 * Tile.TILE_SIZE,
						1.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
		// Check the right GameObjects are hit (identified as obstructions) for a bullet
		// fired at an angle.
		assert correctValues(
				"c",
				gW.getObstructions(playerPos, shootAt3, 0),
				gW.tileAt(new Position(
						2.5 * Tile.TILE_SIZE,
						3.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)),
				gW.tileAt(new Position(
						2.5 * Tile.TILE_SIZE,
						4.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
		
		// DAMAGE-REGISTRY:

		// Check that only the first of two targets is damaged by a non-penetrating
		// bullet (we shouldn't need to test individual directions, as
		// Weapon.singleBullet() relies on the above tests working).
		assert correctValues(
				"d",
				Weapon.singleBullet(
						shooter.getID(),
						0.0,
						gW,
						playerPos,
						shootAt1,
						WALL_HEALTH / 2),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								3.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						0));
		// EDGE CASE: Check that a bullet with exactly enough damage to kill the first
		// target does not continue on to do 0 damage to a second one.
		assert correctValues(
				"e",
				Weapon.singleBullet(
						shooter.getID(),
						0.0,
						gW,
						playerPos,
						shootAt1,
						WALL_HEALTH),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								3.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						0));
		// Check that both targets are hit by a penetrating bullet (one that has more
		// than enough damage to kill the first target, as well as damaging the second).
		assert correctValues(
				"f",
				Weapon.singleBullet(
						shooter.getID(),
						0.0,
						gW,
						playerPos,
						shootAt1,
						WALL_HEALTH * 3 / 2),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								3.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						0),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								4.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						0));
	}
	
	@Test
	public void test_03() {
		Game g = new Game(true, 1);
		GameWorld gW = g.getWorld();
		
		gW.loadFromString(
				"  W  \n" +
				"  W  \n" +
				"     \n" +
				"     \n" +
				"  W  ");
		
		Weapon weapon = new DevWeapon(
				0,
				0,
				1,
				0,
				0,
				1,
				0.0,
				10 * Tile.TILE_SIZE);
		
		Player shooter = new Player(
				g,
				IDManager.nextID(g),
				"A",
				weapon);
		
		Position playerPos = new Position(
				2.5 * Tile.TILE_SIZE,
				1.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		gW.spawnEntity(shooter, playerPos, g);
		
		Position shootAt = new Position(
				2.5 * Tile.TILE_SIZE,
				4.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		gW.tileAt(new Position(
				2.5 * Tile.TILE_SIZE,
				3.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE));
		gW.tileAt(new Position(
				2.5 * Tile.TILE_SIZE,
				4.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE));
		assert correctValues(
				"",
				gW.getObstructions(playerPos, shootAt, 0),
				shooter,
				gW.tileAt(new Position(
						2.5 * Tile.TILE_SIZE,
						3.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)),
				gW.tileAt(new Position(
						2.5 * Tile.TILE_SIZE,
						4.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE)));
	}
	
	@Test
	public void test_04() {
		Game g = new Game(true, 1);
		GameWorld gW = g.getWorld();
		
		gW.loadFromString(
				"  W  \n" +
				"  W  \n" +
				"     \n" +
				"     \n" +
				"  W  ");
		
		Weapon weapon = new DevWeapon(
				0,
				0,
				1,
				0,
				0,
				1,
				0.0,
				10 * Tile.TILE_SIZE);
		
		Player shooter = new Player(
				g,
				IDManager.nextID(g),
				"A",
				weapon);
		
		Position playerPos = new Position(
				2.5 * Tile.TILE_SIZE,
				1.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		gW.spawnEntity(shooter, playerPos, g);
		
		Position shootAt = new Position(
				2.5 * Tile.TILE_SIZE,
				4.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		
		assert correctValues(
				"",
				Weapon.singleBullet(
						shooter.getID(),
						0.0,
						gW,
						playerPos,
						shootAt,
						WALL_HEALTH * 3 / 2),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								3.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						0),
				new Damage(
						gW.tileAt(new Position(
								2.5 * Tile.TILE_SIZE,
								4.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)),
						0));
	}

	@SafeVarargs
	private static <T> boolean correctValues(String testSubCode, T[] hits, T ... desiredHits) {
		boolean failed = false;
		
		int i = 0;
		for (; i < hits.length; i++) {
			if (i >= desiredHits.length) {
				printIncorrect(testSubCode, "Extra (unplanned)", hits[i], failed);
				failed = true;
			} else if (!hits[i].equals(desiredHits[i])) {
				printIncorrect(testSubCode, "Desired", desiredHits[i], failed);
				printIncorrect(testSubCode, "Actual", hits[i], true);
				failed = true;
			}
		}
		
		for (; i < desiredHits.length; i++) {
			printIncorrect(testSubCode, "Missed", desiredHits[i], failed);
			failed = true;
		}
		
		return !failed;
	}
	
	private static <T> void printIncorrect(String testSubCode, String message, T value, boolean alreadyFailed) {
		if (!alreadyFailed) {
			System.out.println("Test " + determineTestNumber(1) + testSubCode + " failed!");
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
