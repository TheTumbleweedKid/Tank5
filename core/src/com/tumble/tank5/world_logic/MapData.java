package com.tumble.tank5.world_logic;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public final class MapData {
	
	private boolean validMap;
	
	private String data;
	
	private int xDimension, yDimension, zDimension;
	
	public MapData(FileHandle fromFile) {
		this(null, fromFile);
	}
	
	public MapData(String map) {
		this(map, null);
	}
	
	private MapData(String map, FileHandle fromFile) {
		if (fromFile != null) {
			try {
				map = fromFile.readString();
			} catch (GdxRuntimeException e) {
				
			}
		}
		
		if (validString(map)) {
			validMap = true;
			
			data = map;
			
			int[] dimensions = calculateDimensions(data);

			xDimension = dimensions[0];
			yDimension = dimensions[1];
			zDimension = dimensions[2];
		} else {
			validMap = false;
			
			data = null;

			xDimension = -1;
			yDimension = -1;
			zDimension = -1;
		}
	}
	
	/**
	 * Validates that a given map <code>String</code> is non-null,
	 * non-blank, and contains at least one layer of non-zero dimensions, and that
	 * all the layers dimensions are identical to the first layer's.
	 * 
	 * @param mD - the <code>MapData</code> object to validate.
	 * 
	 * @return <code>true</code> if the map is of valid dimensions, otherwise
	 *         <code>false</code>.
	 */
	private boolean validString(String mapString) {
		if (mapString == null || mapString.isBlank()) return false;
		
		int[] dimensions = calculateDimensions(mapString);
		
		for (String level : mapString.split("~")) {
			String[] rows = level.split("\n");
			if (rows.length != dimensions[1]) return false;

			for (int y = rows.length - 1; y >= 0; y--) {
				if (rows[y].length() != dimensions[2] || rows[y].length() == 0)
					return false;

			}			
		}

		return true;
	}
	
	private int[] calculateDimensions(String mapString) {
		return new int[] { 
				mapString.split("~")[0].split("\n")[0].length(), // x
				mapString.split("~")[0].split("\n").length, // y - is never 0
				mapString.split("~").length // z - is never 0
		};
	}
	
	public boolean isValid() {
		return validMap;
	}
	
	public String getData() {
		return data;
	}
	
	public int getXDimension() {
		return xDimension;
	}
	
	public int getYDimension() {
		return yDimension;
	}
	
	public int getZDimension() {
		return zDimension;
	}
}
