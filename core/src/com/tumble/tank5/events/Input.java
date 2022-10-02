package com.tumble.tank5.events;

import com.tumble.tank5.world_logic.GameWorld;

/**
 * Represents a request for an event to occur in the game; this could be
 * the movement or action of an <code>Entity</code>, the sudden death (as in, a
 * player quitting mid-game) or creation of one (NPC/player spawning in), or
 * signaling the end of game. This should not be used for gravity or normal
 * in-game deaths - these are handled by the <code>GameWorld</code>.
 * 
 * @author Tumbl
 *
 */
public interface Input {
	
	/**
	 * Apply this <code>Input</code> to the <code>GameWorld</code>, checking it is
	 * is valid. Should only be called by {@link Game#addInput(Input)}.
	 * 
	 * @param gW - the <code>GameWorld</code> to apply this <code>Input</code> to.
	 * 
	 * @return <code>true</code> if the <code>Input</code> was valid (applied
	 *         successfully), or <code>false</code> if it wasn't.
	 */
	public boolean apply(GameWorld gW);
}
