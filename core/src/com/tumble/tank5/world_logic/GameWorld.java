package com.tumble.tank5.world_logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.tiles.Air;
import com.tumble.tank5.tiles.Ladder;
import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.tiles.Wall;

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
		
		if (validWorldDimensions(newWorld)) {
			throw new IllegalArgumentException("Inconsistent/invalid map dimensions!");
		}

		int x = 0;
		int y = 0;
		int z = 0;

		for (String level : newWorld.split("~")) {
			tiles[z] = new Tile[level.split("\n").length][];

			y = 0;
			for (String row : level.split("\n")) {
				tiles[z][y] = new Tile[row.split(" ").length];

				x = 0;
				for (char tile : row.toCharArray()) {
					tiles[z][y][x] = tileFromChar(
							level.charAt(tile),
							new Position(
									x * Tile.TILE_SIZE,
									y * Tile.TILE_SIZE,
									z * Tile.TILE_SIZE));
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
			int y = 0;
			for (String row : level.split("\n")) {
				if (row.length() != dimensions[2] || row.length() == 0)
					return false;

				y++;
			}

			if (y != dimensions[1])
				return false;
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
			return new Ladder(pos, 0);
		case '^':
		default:
			throw new IllegalArgumentException("Unknown tile character '" + c + "' detected!");
		}
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
			if (position.sameTile(e.getPlannedPosition()))
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

		return tiles[(int) (z / Tile.TILE_SIZE)][(int) (y / Tile.TILE_SIZE)][(int) (x / Tile.TILE_SIZE)];
	}
	
	public List<GameObject> getObstructions(Position from, Position to, double time) {
		List<GameObject> obstructions = new ArrayList<GameObject>();
		
		Tile fromTile = tileAt(from);
		Tile toTile = tileAt(to);
		
		if (fromTile == null
				|| toTile == null
				|| fromTile == toTile
				|| from.getZ() != to.getZ()) return obstructions;
		
		int xDiff = Math.abs(to.getX() - from.getX());
		int yDiff = Math.abs(to.getY() - from.getY());
		
		if (xDiff > yDiff) {
			if (yDiff == 0) {
				double dx = Math.signum(to.getX() - from.getX()) * Tile.TILE_SIZE;
				
				for (int i = 0; i < xDiff; i++) {
					Tile t = tileAt(from.x + i * dx, from.y, from.z);
					
					if (t != null && !t.stopsBullets()) {
						obstructions.add(t);
					}
				}
			}
		} else {
			if (xDiff != 0) {
				
			}
		}
		
		return obstructions;
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

		return x < 0 || x >= worldDimensions[0] || y < 0 || y >= worldDimensions[1] || z < 0 || z >= worldDimensions[2];
	}
}
