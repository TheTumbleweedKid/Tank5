package com.tumble.tank5.testing;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.tumble.tank5.game_object.entities.Player;
import com.tumble.tank5.game_object.tiles.Tile;
import com.tumble.tank5.inputs.EntityMove;
import com.tumble.tank5.util.DirectionVector.Direction;
import com.tumble.tank5.util.IDManager;
import com.tumble.tank5.util.Pair;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.MapData;
import com.tumble.tank5.world_logic.game_n_world.Game;
import com.tumble.tank5.world_logic.game_n_world.Game.Phase;

/**
 * Tests to ensure that the <code>Game</code> doesn't accept Inputs it
 * shouldn't, and correctly generates <code>Event</code>s from the ones it
 * should.
 * 
 * @author Tumbl
 *
 */
public class InputTests {
	private Game g;
	private Player[] players;

	/**
	 * Tests the {@link Game#start(int, int, int, int, int)} method for server-side
	 * <code>Game</code>s in relation to internal preconditions (loaded
	 * <code>GameWorld</code>, valid <code>Round</code>-parameters, number of
	 * <code>Player</code>s joined).
	 * 
	 */
	@Test
	public void test_01() {
		// (Valid) Round-parameters:
		int baseDuration = 10;
		int amplitude = 5;
		int phaseShift = 0;
		int period = 1;
		
		// i =
		// 0: valid Round-parameters, but no preconditions met.
		// 1: valid Round-parameters, GameWorld loaded, but no Players joined.
		// 2: valid Round-parameters, GameWorld loaded, but only 1(/2) Players joined.
		// 3: GameWorld loaded, sufficient Players joined, but invalid amplitude/baseDuration.
		// 4: GameWorld loaded, sufficient Players joined, valid amplitude/baseDuration, but invalid period.
		for (int i = 0; i < 4; i++) {
			
			g = new Game(true, 2);
			
			if (i > 0) {
				g.loadMap(new MapData(
						"W \n" +
						"  \n"));
			}
			if (i > 1) {
				g.addEntity(
						new Player(g, IDManager.nextID(g), "A"),
						new Position(
								0.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE));
			}
			if (i > 2) {
				g.addEntity(
						new Player(g, IDManager.nextID(g), "B"),
						new Position(
								1.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE));

				amplitude = 15; // invalid ( > baseDuration)!
			}
			if (i > 3) {
				amplitude = 5; // back to being valid.
				period = 0; // invalid!
			}
			
			
			assert !g.start(
					baseDuration,
					amplitude,
					phaseShift,
					period,
					0); // patienceWait is always valid.
		}
		
		period = 1; // back to being valid.
		
		// Valid Round-parameters, GameWorld loaded, sufficient Players joined.
		assert g.start(
					baseDuration,
					amplitude,
					phaseShift,
					period,
					0);
	}
	
	// Basic horizontal EntityMove Inputs:

	/**
	 * Check that valid cardinal-direction movement <code>Input</code>s are accepted
	 * by the <code>Game</code> and correctly applied to the <code>GameWorld</code>.
	 */
	@Test
	public void test_02() {
		// Set up Game and single Player.
		g = new Game(true, 1);
		g.loadMap(new MapData(
				"  \n" +
				"  "));
		
		players = new Player[] {new Player(g, IDManager.nextID(g), "A")};
		
		g.addEntity(
				players[0],
				new Position(
						0.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE));
		g.start(50, 0, 0, 1, 0); // 50 ms acceptance phase
		
		Map<Direction, Pair<String, Position>> mapStates = new HashMap<Direction, Pair<String, Position>>();
		
		mapStates.put(
				Direction.N,
				new Pair<String, Position>(
						"A \n" +
						"  ",
						new Position(
								0.5 * Tile.TILE_SIZE,
								1.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)));
		mapStates.put(
				Direction.E,
				new Pair<String, Position>(
						" A\n" +
						"  ",
						new Position(
								1.5 * Tile.TILE_SIZE,
								1.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)));
		mapStates.put(
				Direction.S,
				new Pair<String, Position>(
						"  \n" +
						" A",
						new Position(
								1.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)));
		mapStates.put(
				Direction.W,
				new Pair<String, Position>(
						"  \n" +
						"A ",
						new Position(
								0.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)));
		
		for (Direction dir : mapStates.keySet()) {
			assert g.addInput(
					new EntityMove(
							System.currentTimeMillis(),
							players[0],
							dir.asVector()));
			
			assert g.getMove(players[0]).equals(dir.asVector());
			
			// Let the round pass
			while (g.getPhase() == Phase.ACCEPTANCE) g.update(false);
			while (g.getPhase() == Phase.ENACTMENT) g.update(false);
			
			assert g.getWorld().toString().equals(mapStates.get(dir).first());
			assert g.getWorld().entityAt(mapStates.get(dir).second()) == players[0];
		}
	}

	/**
	 * Check that off-map cardinal-direction movement <code>Input</code>s are
	 * rejected by the <code>Game</code> and not applied to the
	 * <code>GameWorld</code>.
	 */
	@Test
	public void test_03() {
		// Set up Game and single Player.
		g = new Game(true, 1);
		g.loadMap(new MapData(
				" "));
		
		players = new Player[] {new Player(g, IDManager.nextID(g), "A")};
		
		Position playerPos = new Position(
				0.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		g.addEntity(
				players[0],
				playerPos);
		g.start(50, 0, 0, 1, 0); // 50 ms acceptance phase
		
		Direction[] directions = {Direction.N, Direction.E, Direction.S, Direction.W};
		
		for (Direction dir : directions) {
			assert !g.addInput(
					new EntityMove(
							System.currentTimeMillis(),
							players[0],
							dir.asVector()));
			
			assert g.getMove(players[0]) == null;
			
			// Let the round pass
			while (g.getPhase() == Phase.ACCEPTANCE) g.update(false);
			while (g.getPhase() == Phase.ENACTMENT) g.update(false);
			
			assert g.getWorld().toString().equals("A");
			assert g.getWorld().entityAt(playerPos) == players[0];
		}
	}
	
	/**
	 * Check that blocked cardinal-direction movement <code>Input</code>s are
	 * rejected by the <code>Game</code> and not applied to the
	 * <code>GameWorld</code>.
	 */
	@Test
	public void test_04() {
		// Set up Game and single Player.
		g = new Game(true, 1);
		g.loadMap(new MapData(
				" W \n" +
				"W W\n" +
				" W "));
		
		players = new Player[] {new Player(g, IDManager.nextID(g), "A")};
		
		Position playerPos = new Position(
				0.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE,
				0.5 * Tile.TILE_SIZE);
		g.addEntity(
				players[0],
				playerPos);
		g.start(50, 0, 0, 1, 0); // 50 ms acceptance phase
		
		Direction[] directions = {Direction.N, Direction.E, Direction.S, Direction.W};
		
		for (Direction dir : directions) {
			assert !g.addInput(
					new EntityMove(
							System.currentTimeMillis(),
							players[0],
							dir.asVector()));
			
			assert g.getMove(players[0]) == null;
			
			// Let the round pass
			while (g.getPhase() == Phase.ACCEPTANCE) g.update(false);
			while (g.getPhase() == Phase.ENACTMENT) g.update(false);
			
			assert g.getWorld().toString().equals(
					" W \n" +
					"WAW\n" +
					" W ");
			assert g.getWorld().entityAt(playerPos) == players[0];
		}
	}
	
	// Basic vertical EntityMove Inputs:

	/**
	 * Check that valid <code>Ladder</code>-based movement <code>Input</code>s are
	 * accepted by the <code>Game</code> and correctly applied to the
	 * <code>GameWorld</code>.
	 */
	@Test
	public void test_05() {
		// Set up Game and single Player.
		g = new Game(true, 1);
		g.loadMap(new MapData(
				" W \n" +
				"W#W\n" +
				" W " +
				"~" +
				"  "));
		
		players = new Player[] {new Player(g, IDManager.nextID(g), "A")};
		
		g.addEntity(
				players[0],
				new Position(
						0.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE,
						0.5 * Tile.TILE_SIZE));
		g.start(50, 0, 0, 1, 0); // 50 ms acceptance phase
		
		Map<Direction, Pair<String, Position>> mapStates = new HashMap<Direction, Pair<String, Position>>();
		
		mapStates.put(
				Direction.N,
				new Pair<String, Position>(
						"A \n" +
						"  ",
						new Position(
								0.5 * Tile.TILE_SIZE,
								1.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)));
		mapStates.put(
				Direction.E,
				new Pair<String, Position>(
						" A\n" +
						"  ",
						new Position(
								1.5 * Tile.TILE_SIZE,
								1.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)));
		mapStates.put(
				Direction.S,
				new Pair<String, Position>(
						"  \n" +
						" A",
						new Position(
								1.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)));
		mapStates.put(
				Direction.W,
				new Pair<String, Position>(
						"  \n" +
						"A ",
						new Position(
								0.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE,
								0.5 * Tile.TILE_SIZE)));
		
		for (Direction dir : mapStates.keySet()) {
			assert g.addInput(
					new EntityMove(
							System.currentTimeMillis(),
							players[0],
							dir.asVector()));
			
			assert g.getMove(players[0]).equals(dir.asVector());
			
			// Let the round pass
			while (g.getPhase() == Phase.ACCEPTANCE) g.update(false);
			while (g.getPhase() == Phase.ENACTMENT) g.update(false);
			
			assert g.getWorld().toString().equals(mapStates.get(dir).first());
			assert g.getWorld().entityAt(mapStates.get(dir).second()) == players[0];
		}
	}

	/**
	 * Tests the {@link Game#addInput(Input)} method for multiple <code>Player</code>s.
	 */
	@Test
	public void test_10() {
		// Set up game.
		Game g = new Game(true, 3);
		g.loadMap(new MapData(" W \n" + "  \n"));

		// Create Players (the last one is not added to the game).
		Player[] players = {
				new Player(g, IDManager.nextID(g), "A"),
				new Player(g, IDManager.nextID(g), "B"),
				new Player(g, IDManager.nextID(g), "C"),
				new Player(g, IDManager.nextID(g), "D")};

		for (int i = 0; i < 3; i++) {
			g.addEntity(
					players[i],
					new Position(
							0.5 * Tile.TILE_SIZE,
							0.5 * Tile.TILE_SIZE,
							0.5 * Tile.TILE_SIZE));
		}
	}
}
