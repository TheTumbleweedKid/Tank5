package com.tumble.tank5.world_logic.game_n_world;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.utils.Queue;
import com.tumble.tank5.game_object.GameObject;
import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.game_object.tiles.Air;
import com.tumble.tank5.game_object.tiles.Ladder;
import com.tumble.tank5.game_object.tiles.StairCase;
import com.tumble.tank5.game_object.tiles.Tile;
import com.tumble.tank5.game_object.tiles.Wall;
import com.tumble.tank5.game_object.tiles.Tile.TileType;
import com.tumble.tank5.util.DirectionVector;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.util.GameUtils;
import com.tumble.tank5.util.Pair;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.MapData;
import com.tumble.tank5.util.DirectionVector.Direction;

/**
 * Stores and provides access to each <code>Entity</code> and <code>Tile</code>
 * in the game world, and the dimensions of the world.
 * 
 * @author Tumbl
 *
 */
public class GameWorld {
	// The entities (mobile, non-Tile objects - either NPCs or Players) in the
	// GameWorld.
	private Set<Entity> entities;
	// The tiles in the GameWorld. All layers must be of identical dimensions.
	private Tile[][][] tiles;
	// The number of layers (0), the north/south size of each layer (1) and the
	// east/west size of each layer (2).
	private int[] worldDimensions = new int[3];
	// Whether a map is currently loaded into the GameWorld.
	private boolean loaded;
	
	private RubbleManager rubbleManager;

	/**
	 * Creates a <code>GameWorld</code> for a <code>Game</code> to take place in.
	 * Does <b>NOT</b> load a world, so this <code>GameWorld</code> will be empty
	 * until one of the load methods is (successfully) called on it
	 * ({@link GameWorld#loadFromFile(String)} or
	 * {@link GameWorld#loadFromString(String)});
	 */
	public GameWorld() {
		entities = new HashSet<Entity>();

		loaded = false;
		
		rubbleManager = new RubbleManager(this);
	}
	
	Set<Entity> getEntities() {
		return Collections.unmodifiableSet(entities);
	}
	
	public RubbleManager getRubbleManager() {
		return rubbleManager;
	}
	
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Loads a new world from a given <code>MapData</code> object, by validating the
	 * world dimensions, constructing the multidimensional <code>tiles</code> array
	 * and populating it with <code>Tile</code>s read from the map, and activating a
	 * marker variable to indicate to future calls to the other methods of this
	 * class (e.g., <code>Tile</code>-retrieval methods) that a valid world has been
	 * successfully loaded. An invalid <code>MapData</code> object will have no
	 * effect.
	 * 
	 * @param map - the <code>MapData</code> object to load the world from.
	 * 
	 * @return <code>true</code> if a 'new' (sort of - it could have the same
	 *         layout) map was successfully loaded, otherwise <code>false</code>.
	 */
	boolean loadWorld(MapData mD) {
		if (mD == null || !mD.isValid()) return false;

		int x = 0;
		int y = 0;
		int z = 0;
		
		tiles = new Tile[mD.getData().split("~").length][][];
		entities.clear();

		for (String level : mD.getData().split("~")) {
			String[] rows = level.split("\n");
			tiles[z] = new Tile[rows.length][];
			
			y = 0;
			for (String row : rows) {
				tiles[z][rows.length - 1 - y] = new Tile[row.length()];

				x = 0;
				for (char tile : row.toCharArray()) {
					tiles[z][rows.length - 1 - y][x] = tileFromChar(
							tile,
							new Position(
									(x + 0.5) * Tile.TILE_SIZE,
									(rows.length - 1 - y + 0.5) * Tile.TILE_SIZE,
									(z + 0.5) * Tile.TILE_SIZE));
					x++;
				}

				y++;
			}

			z++;
		}

		worldDimensions = new int[] { mD.getXDimension(), mD.getYDimension(), mD.getZDimension() };
		
		rubbleManager.clear();
		
		loaded = true;
		
		return true;
	}

	/**
	 * Returns a new instance (except in the case of the singleton {@link Air#AIR})
	 * of a <code>Tile</code> from a given <code>char</code>.
	 * 
	 * @param c   - the <code>char</code> (used to decide what kind of
	 *            <code>Tile</code> to return).
	 * 
	 * @param pos - the <code>Position</code> to spawn the <code>Tile</code> at (not
	 *            used for <code>Air</code>).
	 * 
	 * @return a new <code>Tile</code> of a type determined by the given
	 *         <code>char</code>, or {@link Air#AIR} if the char was not recognised.
	 */
	private Tile tileFromChar(char c, Position pos) {
		switch (c) {
		case ' ':
			return Air.AIR;
		case 'W':
			return new Wall(pos);
		case '#':
			// North-facing ladder.
			return new Ladder(pos, 0);
		case 'v':
			// North-facing stairs.
			return new StairCase(pos, new DirectionVector(0, 1, 0));
		case '<':
			// East-facing stairs.
			return new StairCase(pos, new DirectionVector(1, 0, 0));
		case '^':
			// South-facing stairs.
			return new StairCase(pos, new DirectionVector(0, -1, 0));
		case '>':
			// West-facing stairs.
			return new StairCase(pos, new DirectionVector(-1, 0, 0));
		default:
			return Air.AIR;
		}
	}
	
	@Override
	public String toString() {
		if (!loaded) return "unloaded";
		
		String toReturn = "";
		
		for (int z = 0; z < tiles.length; z++) {
			for (int y = tiles[z].length - 1; y >= 0; y--) {
				for (int x = 0; x < tiles[z][y].length; x++) {
					Entity entity = entityAt(
							new Position(
									x * Tile.TILE_SIZE,
									y * Tile.TILE_SIZE,
									z * Tile.TILE_SIZE));
					if (entity != null) {
						toReturn += entity;
					} else {
						toReturn += tiles[z][y][x];
					}
				}
				
				if (y != 0) toReturn += "\n";
			}
			
			if (z != tiles.length - 1) toReturn += "~";
		}
		
		return toReturn;
	}
	
	boolean spawnEntity(Entity e, Position pos) {
		if (outOfBounds(pos) || entityAt(pos) != null
				|| tiles[pos.getZ()][pos.getY()][pos.getX()].isObstruction(Direction.NONE.asVector()))
			return false;

		for (Entity existing : entities) {
			if (existing.getID() == e.getID() || existing.getPosition().sameTile(pos)) return false;
		}
		
		e.spawn(pos);
		entities.add(e);
		
		return true;
	}
	
	void setTile(Position position, Tile tile) {
		if (position == null || outOfBounds(position) || tile == null)
			return;

		tiles[position.getZ()][position.getY()][position.getX()] = tile;
	}
	
	/**
	 * Finds out whether a given <code>Entity</code> exists in this
	 * <code>GameWorld</code>.
	 * 
	 * @param e - the <code>Entity</code> to look for.
	 * 
	 * @return <code>true</code> if the <code>Entity</code> was found in this
	 *         <code>GameWorld</code>, or <code>false</code> if it wasn't.
	 */
	public boolean hasEntity(Entity e) {
		return e != null && !e.isDead() && entities.contains(e);
	}
	
	public Entity getEntity(int id) {
		for (Entity e : entities) {
			if (!e.isDead() && e.getID() == id) {
				return e;
			}
		}
		
		return null;
	}

	/**
	 * Finds the <code>Entity</code> whose <code>Tile</code>-coordinates are the
	 * same as those of a given <code>Position</code>.
	 * 
	 * @param position - the location to look for an <code>Entity</code> at.
	 * 
	 * @return the <code>Entity</code> found at the location, or <code>null</code>
	 *         if nothing was found (due to invalid coordinates, unloaded game
	 *         world, <code>null Position</code>, etc.).
	 */
	public Entity entityAt(Position position) {
		if (position == null || outOfBounds(position))
			return null;

		for (Entity e : entities) {
			if (!e.isDead() && position.sameTile(e.getPosition()))
				return e;
		}

		return null;
	}

	/**
	 * Finds the <code>Tile</code> at a given <code>Position</code> in this
	 * <code>GameWorld</code> (via
	 * {@link GameWorld#tileAt(double, double, double)}).
	 * 
	 * @param position - the location to look for a <code>Tile</code> at.
	 * 
	 * @return the <code>Tile</code> found at the location, or <code>null</code> if
	 *         nothing was found (due to invalid coordinates, unloaded game world,
	 *         <code>null Position</code>, etc.).
	 */
	public Tile tileAt(Position position) {
		if (position == null) return null;
		return tileAt(position.x, position.y, position.z);
	}

	/**
	 * Gets the <code>Tile</code> at a given location in world-coordinates (as
	 * opposed to indices of the <code>Tile</code> in the <code>tiles</code> array.
	 * 
	 * @param x - the x-coordinate to look for a <code>Tile</code> at.
	 * 
	 * @param y - the y-coordinate to look for a <code>Tile</code> at.
	 * 
	 * @param z - the z-coordinate to look for a <code>Tile</code> at.
	 * 
	 * @return the <code>Tile</code> found at the location, or <code>null</code> if
	 *         nothing was found (due to invalid coordinates, unloaded game world,
	 *         etc.).
	 */
	public Tile tileAt(double x, double y, double z) {
		if (!loaded || outOfBounds(x, y, z))
			return null;

		return tiles[(int) Math.floor(z / Tile.TILE_SIZE)][(int) Math.floor(y / Tile.TILE_SIZE)][(int) Math.floor(x / Tile.TILE_SIZE)];
	}
	
	public void clearDeadEntities() {
		entities.removeIf((Entity e) -> e.isDead());
	}
	
	public Pair<Queue<GameObject>, Queue<Position>> getLineObstructions(Position from, Position to) {
		/*
		 * Tile fromTile = tileAt(from); Tile toTile = tileAt(to);
		 * 
		 * // Don't allow shooting between different z-layers... yet! if (fromTile ==
		 * null || toTile == null || fromTile == toTile || from.getZ() != to.getZ())
		 * return new Queue<GameObject>();
		 */
		if (outOfBounds(from) || to == null)
			throw new GameError("Invalid start/end Positions for line obstruction!");
		
		Pair<Queue<GameObject>, Queue<Position>> queues = rayTrace3D(from, to);
		
		Entity e = entityAt(from);
		Tile t = tileAt(from);

		// Check that the first object in the queue is not at the 'from' Position.
		if (queues.first().notEmpty() && (queues.first().first().equals(e) || queues.first().first().equals(t))) {
			queues.first().removeFirst();
			queues.second().removeFirst();
		}
		// Check that the second (now first) object in the queue is not at the 'from'
		// Position.
		if (queues.first().notEmpty() && (queues.first().first().equals(e) || queues.first().first().equals(t))) {
			queues.first().removeFirst();
			queues.second().removeFirst();
		}
		
		return queues;
	}
	
	public Queue<Pair<GameObject, Double>> getSphereObstructions(Position centre, double radius) {
		Queue<Pair<GameObject, Double>> obstructions = new Queue<Pair<GameObject, Double>>();
		
		for (int z = Math.max(0, (int) Math.floor((centre.z - radius) / Tile.TILE_SIZE)); z < Math.min(worldDimensions[0], (int) Math.ceil((centre.z + radius) / Tile.TILE_SIZE)); z++) {
			for (int y = Math.max(0, (int) Math.floor((centre.y - radius) / Tile.TILE_SIZE)); y < Math.min(worldDimensions[1], (int) Math.ceil((centre.y + radius) / Tile.TILE_SIZE)); y++) {
				for (int x = Math.max(0, (int) Math.floor((centre.x - radius) / Tile.TILE_SIZE)); x < Math.min(worldDimensions[2], (int) Math.ceil((centre.x + radius) / Tile.TILE_SIZE)); x++) {
					double dist = Tile.TILE_SIZE * Math.sqrt(
							(centre.x - x + 0.5) * (centre.x - x + 0.5) + 
							(centre.y - y + 0.5) * (centre.y - y + 0.5) +
							(centre.z - z + 0.5) * (centre.z - z + 0.5));
					if (dist <= radius) {
						if (tiles[z][y][x].getType() != TileType.AIR) {
							obstructions.addLast(
									new Pair<GameObject, Double>(tiles[z][y][x], dist));
						}

						Entity entity = entityAt(new Position(
								x * Tile.TILE_SIZE,
								y * Tile.TILE_SIZE,
								z * Tile.TILE_SIZE));
						if (entity != null) {
							obstructions.addLast(
									new Pair<GameObject, Double>(entity, dist));
						}
					}
				}
			}
		}
		
		return obstructions;
	}
	
	/**
	 * Collects every <code>Tile</code> (that stop bullets) and <code>Entity</code>
	 * between a given start (the <code>Tile</code>/<code>Entity</code> at which is
	 * <i>excluded</i>) and end (the <code>Tile</code>/<code>Entity</code> at which
	 * is <i>included</i>) <code>Position</code>.
	 * <br>
	 * Credit to skrjablin's comment at:
	 * https://playtechs.blogspot.com/2007/03/raytracing-on-grid.html
	 * 
	 * @param from
	 * 
	 * @param to
	 * 
	 * @return
	 */
	private Pair<Queue<GameObject>, Queue<Position>> rayTrace3D(Position from, Position to) {
		Pair<Queue<GameObject>, Queue<Position>> hits =
				new Pair<Queue<GameObject>, Queue<Position>>(
						new Queue<GameObject>(),
						new Queue<Position>());
		
		double from_x = from.x / Tile.TILE_SIZE;
		double from_y = from.y / Tile.TILE_SIZE;
		double from_z = from.z / Tile.TILE_SIZE;
		
		double to_x = to.x / Tile.TILE_SIZE;
		double to_y = to.y / Tile.TILE_SIZE;
		double to_z = to.z / Tile.TILE_SIZE;
		
		double dx = Math.abs(to.x - from.x) / Tile.TILE_SIZE;
		double dy = Math.abs(to.y - from.y) / Tile.TILE_SIZE;
		double dz = Math.abs(to.z - from.z) / Tile.TILE_SIZE;

		int x = (int) (Math.floor(from_x));
		int y = (int) (Math.floor(from_y));
		int z = (int) (Math.floor(from_z));

		double dt_dx = 1.0 / dx;
		double dt_dy = 1.0 / dy;
		double dt_dz = 1.0 / dz;

		@SuppressWarnings("unused")
		double t = 0;

		int n = 1;
		int x_inc, y_inc, z_inc;
		double t_next_y, t_next_x, t_next_z;

		if (dx == 0) {
			x_inc = 0;
			t_next_x = dt_dx; // infinity
		} else if (to_x > from_x) {
			x_inc = 1;
			n += (int) (Math.floor(to_x)) - x;
			t_next_x = (Math.floor(from_x) + 1 - from_x) * dt_dx;
		} else {
			x_inc = -1;
			n += x - (int) (Math.floor(to_x));
			t_next_x = (from_x - Math.floor(from_x)) * dt_dx;
		}

		if (dy == 0) {
			y_inc = 0;
			t_next_y = dt_dy; // infinity
		} else if (to_y > from_y) {
			y_inc = 1;
			n += (int) (Math.floor(to_y)) - y;
			t_next_y = (Math.floor(from_y) + 1 - from_y) * dt_dy;
		} else {
			y_inc = -1;
			n += y - (int) (Math.floor(to_y));
			t_next_y = (from_y - Math.floor(from_y)) * dt_dy;
		}

		if (dz == 0) {
			z_inc = 0;
			t_next_z = dt_dz; // infinity
		} else if (to_z > from_z) {
			z_inc = 1;
			n += (int) (Math.floor(to_z)) - z;
			t_next_z = (Math.floor(from_z) + 1 - from_z) * dt_dz;
		} else {
			z_inc = -1;
			n += z - (int) (Math.floor(to_z));
			t_next_z = (from_z - Math.floor(from_z)) * dt_dz;
		}

		for (; n > 0; --n) {
			Position pos = new Position(
					x * Tile.TILE_SIZE,
					y * Tile.TILE_SIZE,
					z * Tile.TILE_SIZE);
			if (!pos.sameTile(from)) {
				Tile tile = tileAt(pos);
				Entity entity = entityAt(pos);
				
				if (tile != null
						&& !tile.equals(hits.first().isEmpty() ? null : hits.first().first())
						&& tile.stopsBullets()) {
					hits.first().addLast(tile);
					hits.second().addLast(pos);
				}
				
				if (entity != null
						&& !entity.equals(hits.first().isEmpty() ? null : hits.first().first())
						&& GameUtils.collideEntityBullet(entity, from, to)) {
					hits.first().addLast(entity);
					hits.second().addLast(pos);
				}
			}
			
			if (t_next_x <= t_next_y && t_next_x <= t_next_z) {
				// t_next_x is smallest
				x += x_inc;
				t = t_next_x;
				t_next_x += dt_dx;
			} else if (t_next_y <= t_next_x && t_next_y <= t_next_z) {
				// t_next_y is smallest
				y += y_inc;
				t = t_next_y;
				t_next_y += dt_dy;
			} else {
				// t_next_y is smallest
				z += z_inc;
				t = t_next_z;
				t_next_z += dt_dz;
			}
		}
		
		return hits;
	}

	/**
	 * Check whether a given <code>Position</code> represents a valid
	 * <code>Tile</code> in the <code>tiles</code> array (if it has been loaded!),
	 * via {@link GameWorld#outOfBounds(double, double, double)}.
	 * 
	 * @param position - the <code>Position</code> to test.
	 * 
	 * @return <code>true</code> found at the <code>Position</code>, or
	 *         <code>false</code> if the location was out of bounds (due to invalid
	 *         coordinates, unloaded game world, <code>null Position</code>, etc.).
	 */
	public boolean outOfBounds(Position position) {
		if (position == null) return false;
		return outOfBounds(position.x, position.y, position.z);
	}

	/**
	 * Check whether a given location in world-coordinates represents a valid
	 * <code>Tile</code> in the <code>tiles</code> array (if it has been loaded!).
	 * 
	 * @param x - the x-coordinate of the location to test.
	 * 
	 * @param y - the y-coordinate of the location to test.
	 * 
	 * @param z - the z-coordinate of the location to test.
	 * 
	 * @return <code>true</code> found at the location, or <code>false</code> if the
	 *         location was out of bounds (due to invalid coordinates, unloaded game
	 *         world, etc.).
	 */
	public boolean outOfBounds(double x, double y, double z) {
		if (!loaded)
			return false;

		return x < 0
				|| x >= worldDimensions[2] * Tile.TILE_SIZE
				|| y < 0
				|| y >= worldDimensions[1] * Tile.TILE_SIZE
				|| z < 0
				|| z >= worldDimensions[0] * Tile.TILE_SIZE;
	}
}
