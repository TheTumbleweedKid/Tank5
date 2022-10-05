package com.tumble.tank5.events;

import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;

import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Round;

/**
 * Represents a request for an event to occur in a <code>Game</code>; this could
 * be the movement or action of an <code>Entity</code>, the sudden death (as in,
 * a player quitting mid-game) or creation of one (NPC/player spawning in), or
 * signaling the end of game. This should not be used for gravity or normal
 * in-game deaths - these are handled by the <code>GameWorld</code>.
 * 
 * @author Tumbl
 *
 */
public abstract class Input {
	protected long time;

	/**
	 * Gets when the input this <code>Input</code> represents was first requested.
	 * This is used in {@link Round#shouldAccept(Input)} to determine if an
	 * <code>Input</code> is valid (due to latency, it may arrive after the
	 * <code>Round</code> - i.e., the interval in which new <code>Input</code>s may
	 * be created - has finished, in the 'breathing time', but it should still be applied).
	 * 
	 * @return when this <code>Input</code> was first requested.
	 */
	public final long getTime() {
		return time;
	}

	/**
	 * Applies this <code>Input</code> to the <code>GameWorld</code>, checking it is
	 * is valid. Should only be called by {@link Game#addInput(Input)}.
	 * 
	 * @param gW - the <code>GameWorld</code> to apply this <code>Input</code> to.
	 * 
	 * @return <code>true</code> if the <code>Input</code> was valid (applied
	 *         successfully), or <code>false</code> if it wasn't.
	 */
	public abstract boolean apply(GameWorld gW);
}
