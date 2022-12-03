package com.tumble.tank5.testing;

import org.junit.jupiter.api.Test;

import com.tumble.tank5.entities.Player;
import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.util.IDManager;
import com.tumble.tank5.world_logic.Game;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;


/**
 * Tests to ensure that the <code>GameWorld</code> loads and behaves properly.
 * 
 * @author Tumbl
 *
 */
public class WorldTests {

	/**
	 * Makes sure unloaded (and incorrectly-loaded) maps return an "unloaded" from their
	 * {@link GameWorld#toString()}, rather than <code>null</code> or throw an
	 * exception.
	 */
	@Test
	public void test_01() {
		GameWorld gW = new GameWorld();

		// Nothing loaded yet.
		assert compare("unloaded", gW.toString());

		// Null map string.
		gW.loadFromString(null);
		assert compare("unloaded", gW.toString());
		
		// Empty map string.
		gW.loadFromString("");
		assert compare("unloaded", gW.toString());

		// Invalid map string (inconsistent level x-dimensions).
		gW.loadFromString(
				"WWW\n" +
				"WWW" +
				"~" +
				"WW\n"+
				"WWW");
		assert compare("unloaded", gW.toString());
		// Invalid map string (inconsistent level y-dimensions).
				gW.loadFromString(
						"WWW\n" +
						"WWW" +
						"~" +
						"WWW\n"+
						"WWW\n" +
						"WWW");
				assert compare("unloaded", gW.toString());
	}

	/**
	 * Makes sure a vertically- and horizontally-asymmetrical multi-layered map is
	 * loaded, stored and printed (converted to a <code>String</code>
	 * representation) correctly.
	 */
	@Test
	public void test_02() {
		String mapString =
				"  W>\n" +
				" <WW\n" +
				"  v \n" +
				"^  W\n" +
				"# W " +
				"~" +
				" W> \n" +
				"  <W\n" +
				"    \n" +
				"  W \n" +
				"# W ";
		
		GameWorld gW = new GameWorld();
		
		gW.loadFromString(mapString);
		
		assert compare(mapString, gW.toString());
	}
	
	/**
	 * Tests {@link GameWorld#outOfBounds(double, double, double)}.
	 */
	@Test
	public void test_03() {
		String mapString =
				"    \n" +
				"    \n" +
				"    " +
				"~" +
				"    \n" +
				"    \n" +
				"    ";
		
		GameWorld gW = new GameWorld();
		
		gW.loadFromString(mapString);
		
		assert !gW.outOfBounds(
				 3 * Tile.TILE_SIZE,
				 2 * Tile.TILE_SIZE,
				 0.5 * Tile.TILE_SIZE);
		assert !gW.outOfBounds(
				 0 * Tile.TILE_SIZE,
				 0 * Tile.TILE_SIZE,
				 1.99999999 * Tile.TILE_SIZE);
		assert gW.outOfBounds(
				 10 * Tile.TILE_SIZE,
				 0.5 * Tile.TILE_SIZE,
				 0.5 * Tile.TILE_SIZE);
		assert gW.outOfBounds(
				 0.5 * Tile.TILE_SIZE,
				 10 * Tile.TILE_SIZE,
				 0.5 * Tile.TILE_SIZE);
		assert gW.outOfBounds(
				 0.5 * Tile.TILE_SIZE,
				 0.5 * Tile.TILE_SIZE,
				 10 * Tile.TILE_SIZE);
		assert gW.outOfBounds(
				 -1 * Tile.TILE_SIZE,
				 0.5 * Tile.TILE_SIZE,
				 0.5 * Tile.TILE_SIZE);
		assert gW.outOfBounds(
				 0.5 * Tile.TILE_SIZE,
				 -1 * Tile.TILE_SIZE,
				 0.5 * Tile.TILE_SIZE);
		assert gW.outOfBounds(
				 0.5 * Tile.TILE_SIZE,
				 0.5 * Tile.TILE_SIZE,
				 -1 * Tile.TILE_SIZE);
	}
	
	/**
	 * Tests {@link GameWorld#tileAt(double, double, double)}, by randomly generating coordinates
	 * inside grid-cubes in the world and checking they still correspond to the same
	 * (unique) <code>Tile</code>.
	 */
	@Test
	public void test_04() {
		String mapString =
				"   v \n" +
				"  W  \n" +
				"# W  " +
				"~" +
				"     \n" +
				"     \n" +
				"  #  ";
		
		GameWorld gW = new GameWorld();
		
		gW.loadFromString(mapString);
		
		int repeats = 20;
		
		for (int i = 0; i < repeats; i++) {
			assert compare(
					"#",
					gW.tileAt(
							Math.random() * Tile.TILE_SIZE,
							Math.random() * Tile.TILE_SIZE,
							Math.random() * Tile.TILE_SIZE).toString());
			assert compare(
					"v",
					gW.tileAt(
							(3 + Math.random()) * Tile.TILE_SIZE,
							(2 + Math.random()) * Tile.TILE_SIZE,
							Math.random() * Tile.TILE_SIZE).toString());
			assert compare(
					"#",
					gW.tileAt(
							(2 + Math.random()) * Tile.TILE_SIZE,
							Math.random() * Tile.TILE_SIZE,
							(1 + Math.random()) * Tile.TILE_SIZE).toString());
		}
	}
	
	/**
	 * Makes sure <code>Entities</code> are spawned in (or not) properly.
	 * Also (by proxy) tests {@link GameWorld#entityAt(Position)}.
	 */
	@Test
	public void test_05() {
		String mapString =
				"  W \n" +
				"  WW\n" +
				"    \n" +
				"   W\n" +
				"  W ";
		
		Position[] spawnLocations = {
				new Position(
						2 * Tile.TILE_SIZE,
						2 * Tile.TILE_SIZE,
						0 * Tile.TILE_SIZE),
				new Position(
						1 * Tile.TILE_SIZE,
						1 * Tile.TILE_SIZE,
						0 * Tile.TILE_SIZE)
		};
		
		Game g = new Game(true, 1);
		GameWorld gW = g.getWorld();
		
		gW.loadFromString(mapString);
		
		// No Entity should be found (as none have been spawned yet).
		assert compare("", gW.entityAt(spawnLocations[0]));
		
		// Spawn empty-name Player at the first location.
		assert gW.spawnEntity(
				new Player(
						g,
						IDManager.nextID(g),
						""),
				spawnLocations[0],
				g);
		assert compare("P", gW.entityAt(spawnLocations[0]));
		
		// Spawn named Player at the second location.
		assert gW.spawnEntity(
				new Player(
						g,
						IDManager.nextID(g),
						"A"),
				spawnLocations[1],
				g);
		assert compare("A", gW.entityAt(spawnLocations[1]));
		
		// Fail to spawn named Player in a populated location,
		// i.e., "A" remains at the second location, while "B"
		// is not spawned.
		assert !gW.spawnEntity(
				new Player(
						g,
						IDManager.nextID(g),
						"B"),
				spawnLocations[1],
				g);
		assert compare("A", gW.entityAt(spawnLocations[1]));
	}

	private static boolean compare(String desired, Object actual) {
		if (desired.equals(actual != null ? actual.toString() : ""))
			return true;

		System.out.println("Test " + determineTestNumber() + " failed!");
		System.out.println("Desired:\n" + desired);
		System.out.println("Actual:\n" + actual);

		return false;
	}

	/**
	 * Determine the number of the test being executed based on its method name.
	 *
	 * @return the number of the test being executed.
	 */
	private static int determineTestNumber() {
		StackTraceElement[] e = Thread.currentThread().getStackTrace();
		String line = e[3].toString();
		int numStart = line.indexOf('_') + 1;
		return Integer.valueOf(line.substring(numStart, numStart + 2));
	}
}
