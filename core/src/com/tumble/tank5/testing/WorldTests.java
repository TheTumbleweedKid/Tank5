package com.tumble.tank5.testing;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.tumble.tank5.game_object.entities.Player;
import com.tumble.tank5.game_object.tiles.Tile;
import com.tumble.tank5.util.IDManager;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.MapData;
import com.tumble.tank5.world_logic.game_n_world.Game;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;


/**
 * Tests to ensure that the <code>GameWorld</code> loads and behaves properly
 * (including access methods for the <code>GameWorld</code> in
 * <code>Game</code>).
 * 
 * @author Tumbl
 *
 */
public class WorldTests {

	/**
	 * Makes sure unloaded (and incorrectly-loaded) maps return an "unloaded" from
	 * their {@link GameWorld#toString()}, rather than <code>null</code> or throw an
	 * exception. Also tests {@link Game#loadMap(MapData)} (<i>only</i> for
	 * map-<code>String</code>s).
	 */
	@Test
	public void test_01() {
		Game g = new Game(true, 1);
		
		GameWorld gW = g.getWorld();

		// Nothing loaded yet.
		assert compare("unloaded", gW.toString());

		// Null map string.
		assert !g.loadMap(null);
		
		assert compare("unloaded", gW.toString());
		
		// Empty map string.
		assert !g.loadMap(new MapData(""));
		
		assert compare("unloaded", gW.toString());

		// Invalid map string (inconsistent level x-dimensions).
		assert !g.loadMap(
				new MapData(
						"WWW\n" +
						"WWW" +
						"~" +
						"WW\n"+
						"WWW"));
		
		assert compare("unloaded", gW.toString());
		
		// Invalid map string (inconsistent level y-dimensions).
		assert !g.loadMap(
				new MapData(
						"WWW\n" +
						"WWW" +
						"~" +
						"WWW\n"+
						"WWW\n" +
						"WWW"));
		
		assert compare("unloaded", gW.toString());
	}

	/**
	 * Makes sure a vertically- and horizontally-asymmetrical multi-layered map is
	 * loaded, stored and printed (converted to a <code>String</code>
	 * representation) correctly. Also tests {@link Game#loadMap(String, boolean)} (<i>only</i> for
	 * map-<code>String</code>s).
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

		Game g = new Game(true, 1);
		
		GameWorld gW = g.getWorld();
		
		assert g.loadMap(new MapData(mapString));
		
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

		Game g = new Game(true, 1);
		
		GameWorld gW = g.getWorld();

		g.loadMap(new MapData(mapString));
		
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
	@RepeatedTest(20)
	public void test_04() {
		String mapString =
				"   v \n" +
				"  W  \n" +
				"# W  " +
				"~" +
				"     \n" +
				"     \n" +
				"  #  ";

		Game g = new Game(true, 1);
		
		GameWorld gW = g.getWorld();

		g.loadMap(new MapData(mapString));
		
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

		g.loadMap(new MapData(mapString));
		
		// No Entity should be found (as none have been spawned yet).
		assert compare("", gW.entityAt(spawnLocations[0]));
		
		// Spawn empty-name Player at the first location.
		assert g.addEntity(
				new Player(
						g,
						IDManager.nextID(g),
						""),
				spawnLocations[0]);
		assert compare("P", gW.entityAt(spawnLocations[0]));
		
		// Spawn named Player at the second location.
		assert g.addEntity(
				new Player(
						g,
						IDManager.nextID(g),
						"A"),
				spawnLocations[1]);
		assert compare("A", gW.entityAt(spawnLocations[1]));
		
		// Fail to spawn named Player in a populated location,
		// i.e., "A" remains at the second location, while "B"
		// is not spawned.
		assert !g.addEntity(
				new Player(
						g,
						IDManager.nextID(g),
						"B"),
				spawnLocations[1]);
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
