package com.tumble.tank5.world_logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Queue;
import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.tiles.Air;
import com.tumble.tank5.tiles.Ladder;
import com.tumble.tank5.tiles.StairCase;
import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.tiles.Wall;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.util.GameUtils;

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
	
	public RubbleManager getRubbleManager() {
		return rubbleManager;
	}

	/**
	 * Attempts to load a new world from a given file name (via
	 * {@link GameWorld#loadWorld}).
	 * 
	 * @param fileName - the name of the file to read from.
	 * 
	 * @return <code>true</code> if the world was successfully loaded, otherwise
	 *         <code>false</code>.
	 */
	public boolean loadFromFile(String fileName) {
		try {
			loadWorld(Gdx.files.internal(fileName).readString());
			return true;
		} catch (GdxRuntimeException | IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Attempts to load a new world from a given map <code>String</code> (via
	 * {@link GameWorld#loadWorld}).
	 * 
	 * @param map - the map <code>String</code> to load the world from.
	 * 
	 * @return <code>true</code> if the world was successfully loaded, otherwise
	 *         <code>false</code>.
	 */
	public boolean loadFromString(String map) {
		try {
			loadWorld(map);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Loads a new world from a given map <code>String</code>, by validating the
	 * world dimensions, constructing the multidimensional <code>tiles</code> array
	 * and populating it with <code>Tile</code>s read from the map, and activating a
	 * marker variable to indicate to future calls to the other methods of this
	 * class (e.g., <code>Tile</code>-retrieval methods) that a valid world has been
	 * successfully loaded.
	 * 
	 * @param map - the map <code>String</code> to load the world from.
	 * 
	 * @throws IllegalArgumentException if the given map has inconsistent or invalid
	 *                                  dimensions, or if an unregistered
	 *                                  tile-character is encountered.
	 */
	private void loadWorld(String newWorld) {
		loaded = false;
		
		if (!validWorldDimensions(newWorld)) {
			throw new IllegalArgumentException("Inconsistent/invalid map dimensions!");
		}

		int x = 0;
		int y = 0;
		int z = 0;
		
		tiles = new Tile[newWorld.split("~").length][][];

		for (String level : newWorld.split("~")) {
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

		worldDimensions = new int[] { z, y, x };
		
		rubbleManager.clear();
		
		loaded = true;
	}

	/**
	 * Validates that a given map <code>String</code> is non-null, non-empty, and
	 * contains at least one layer of non-zero dimensions, and that all the layers
	 * dimensions are identical to the first layer's.
	 * 
	 * @param newWorld - the map <code>String</code> to validate.
	 * 
	 * @return <code>true</code> if the map is of valid dimensions, otherwise
	 *         <code>false</code>.
	 */
	private boolean validWorldDimensions(String newWorld) {
		if (newWorld == null || newWorld.isEmpty())
			return false;

		int[] dimensions = { newWorld.split("~").length, // Can't be 0
				newWorld.split("~")[0].split("\n").length, // Can't be 0
				newWorld.split("~")[0].split("\n")[0].length() };

		
		for (String level : newWorld.split("~")) {
			String[] rows = level.split("\n");
			if (rows.length != dimensions[1]) return false;

			for (int y = rows.length - 1; y >= 0; y--) {
				if (rows[y].length() != dimensions[2] || rows[y].length() == 0)
					return false;

			}			
		}

		return true;
	}

	/**
	 * Returns a new instance (except in the case of the singleton <code>Air</code>)
	 * <code>Tile</code> from a given <code>char</code>.
	 * 
	 * @param c   - the <code>char</code> (used to decide what kind of
	 *            <code>Tile</code> to return).
	 * 
	 * @param pos - the <code>Position</code> to spawn the <code>Tile</code> at (not
	 *            used for <code>Air</code>).
	 * 
	 * @return a new <code>Tile</code> of a type determined by the given
	 *         <code>char</code>.
	 * 
	 * @throws IllegalArgumentException if the <code>char</code> is unregistered.
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
			throw new IllegalArgumentException("Unknown tile character '" + c + "' detected!");
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
	
	public boolean spawnEntity(Entity e, Position pos, Game g) {
		if (g.getWorld() != this || outOfBounds(pos) || entityAt(pos) != null) return false;
		
		for (Entity existing : entities) {
			if (existing.getID() == e.getID()) return false;
		}
		
		e.spawn(pos);
		entities.add(e);
		
		return true;
	}
	
	public void setTile(Position position, Tile tile) {
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
		return entities.contains(e);
	}
	
	public Entity getEntity(int id) {
		for (Entity e : entities) {
			if (e.getID() == id) {
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
			if (position.sameTile(e.getPosition()))
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
	
	public GameObject[] getObstructions(Position from, Position to, double time) {
		/*
		 * Tile fromTile = tileAt(from); Tile toTile = tileAt(to);
		 * 
		 * // Don't allow shooting between different z-layers... yet! if (fromTile ==
		 * null || toTile == null || fromTile == toTile || from.getZ() != to.getZ())
		 * return new Queue<GameObject>();
		 */
		if (outOfBounds(from) || to == null)
			throw new GameError("Invalid start/end Positions for line obstruction!");
		
		Queue<GameObject> queue = rayTrace3D(from, to);
		
		Entity e = entityAt(from);
		Tile t = tileAt(from);

		// Check that the first object in the queue is not at the 'from' Position.
		if (queue.notEmpty() && (queue.first().equals(e) || queue.first().equals(t))) {
			queue.removeFirst();
		}
		// Check that the second (now first) object in the queue is not at the 'from'
		// Position.
		if (queue.notEmpty() && (queue.first().equals(e) || queue.first().equals(t))) {
			queue.removeFirst();
		}
		
		GameObject[] obstructions = new GameObject[queue.size];
		
		int i = 0;
		while (queue.notEmpty()) {
			obstructions[i] = queue.removeFirst();
			i++;
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
	private Queue<GameObject> rayTrace3D(Position from, Position to) {
		Queue<GameObject> hits = new Queue<GameObject>();
		
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
						&& !tile.equals(hits.isEmpty() ? null : hits.first())
						&& tile.stopsBullets()) {
					hits.addLast(tile);
				}
				
				if (entity != null
						&& !entity.equals(hits.isEmpty() ? null : hits.first())
						&& GameUtils.collideEntityBullet(entity, from, to)) {
					hits.addLast(entity);
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
