package com.tumble.tank5.util;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.Position;

public class GameUtils {
	
	private GameUtils() {
		// Private constructor.
	}
	
	/**
	 * Returns a pseudo-random <code>double</code> value of a given (maximum)
	 * magnitude.
	 * 
	 * @param magnitude - the maximum magnitude the value should take.
	 * 
	 * @return a pseudo-random number from the interval [-<code>magnitude</code>,
	 *         <code>magnitude</code>).
	 *         
	 * @see Math#random()
	 */
	public static double random(double magnitude) {
		return (Math.random() - 0.5)  * magnitude;
	}

	private static Vector2 v0 = new Vector2();
	private static Vector2 v1 = new Vector2();
	private static Vector2 centre = new Vector2();
	
	public static boolean collideEntityBullet(
			Entity entity,
			Position from,
			Position to) {
		if (entity == null) {
			return false;
		}
		v0.set((float) from.x, (float) from.y);
		v1.set((float) to.x, (float) to.y);
		centre.set((float) entity.getPosition().x, (float) entity.getPosition().y);
		
		return Intersector.intersectSegmentCircle(v0, v1, centre, entity.getRadius() * entity.getRadius());
	}
}
