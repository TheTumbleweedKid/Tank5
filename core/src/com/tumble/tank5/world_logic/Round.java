package com.tumble.tank5.world_logic;

import com.tumble.tank5.inputs.Input;
import com.tumble.tank5.util.GameError;

/**
 * Represents a period of time in which input from the user should accepted by
 * the <code>Game</code>. The duration of each <code>Round</code> in a
 * <code>Game</code> varies sinusoidally (will be the same for any two
 * <code>Round</code>s in the same position on the wave (e.g., the 3rd
 * <code>Round</code>s of any two cycles will have the same durations).
 * <code>Round</code>s come in 'series', starting from one constructed via
 * {@link Round#Round(int, int, int, int)} and continuing via calls of
 * {@link Round#next()} to generate the successive <code>Round</code>s.
 * 
 * @author Tumbl
 *
 */
public class Round {
	/**
	 * How many <code>Round</code>s in the series have passed before this one.
	 * */
	public final int roundNumber;
	/**
	 * The length of time after its start time in which this <code>Round</code> will
	 * validate <code>Input</code>s. Varies sinusoidally throughout each cycle of
	 * the series.
	 */
	public final int duration;

	private final int baseDuration, amplitude, phase, period;
	
	private long startTime;
	private boolean started;

	/**
	 * Constructs a new <code>Round</code> (the first in a series) from the given
	 * information (does not start it though).
	 * 
	 * @param baseDuration - the mean duration of a <code>Round</code> in the
	 *                     <code>Game</code> that this <code>Round</code> belongs
	 *                     to, in milliseconds.
	 * 
	 * @param amplitude    - the maximum variation in milliseconds of each
	 *                     <code>Round</code>'s duration from the
	 *                     <code>baseDuration</code> for this series of
	 *                     <code>Round</code>s. Must be <=
	 *                     <code>baseDuration</code>.
	 * 
	 * @param phase        - the phase shift (just a number of <code>Round</code>s)
	 *                     of this <code>Round</code>, i.e., where on the sine wave
	 *                     the duration comes from. This is useful for creating the
	 *                     first <code>Round</code> because, for example, a
	 *                     <code>Game</code> may want to start off quickly, in which
	 *                     case it would want have a phase of ~-<code>period</code>
	 *                     / 4.
	 * 
	 * @param period       - the number of <code>Round</code>s in one complete cycle
	 *                     of durations.
	 * 
	 * @throws GameError if <code>baseDuration</code> < <code>amplitude</code> or
	 *                   <code>period</code> == 0 (if you want no oscillations, set
	 *                   <code>amplitude</code> = 0).
	 */
	public Round(int baseDuration, int amplitude, int phase, int period) {
		if (baseDuration < amplitude) {
			throw new GameError(
					"Round duration (" + baseDuration
					+ ") must be >= amplitude (" + amplitude + ")!");
		}
		if (period <= 0) {
			throw new GameError("Round period (" + period + ") must be > 0!");
		}

		roundNumber = 0;
		
		this.baseDuration = baseDuration;
		this.amplitude = amplitude;
		this.phase = phase;
		this.period = period;

		duration = baseDuration + (int) (amplitude * Math.sin(phase * 2 * Math.PI / period));
	}

	/**
	 * Constructs the next <code>Round</code> in a series, given the preceding one,
	 * but does not start it. Only called by {@link Round#next()}.
	 * 
	 * @param preceding - the <code>Round</code> before this one in the series.
	 */
	private Round(Round preceding) {
		this.roundNumber = preceding.roundNumber + 1;
		
		baseDuration = preceding.baseDuration;
		amplitude = preceding.amplitude;
		phase = preceding.phase;
		period = preceding.period;

		duration = baseDuration + (int) (amplitude * Math.sin(phase * 2 * Math.PI / period));
	}
	
	/**
	 * Starts this <code>Round</code> (server-side call only!), returning the start
	 * time to be passed to clients, so their <code>Round</code>s can be
	 * synchronised (via {@link Round#start(long)}).
	 * 
	 * @return the time at which the Round started, or -1 if it was already in
	 *         progress.
	 */
	public long start() {
		if (!started) {
			startTime = System.currentTimeMillis();
			started = true;
			
			return startTime;
		}
		
		return -1;
	}
	
	/**
	 * Starts this <code>Round</code> (client-side call only!) with a given start
	 * time passed from the server (from {@link Round#start()}), so that the start
	 * times are synchronised.
	 * 
	 * @param startTime - the time the server-side <code>Round</code> started.
	 */
	public void start(long startTime) {
		if (!started && startTime != -1) {
			this.startTime = startTime;
			started = true;
		}
	}
	
	/**
	 * Whether this <code>Round</code> has started accepting <code>Input</code>s
	 * yet.
	 * 
	 * @return <code>true</code> if the <code>Round</code> has been started
	 *         (otherwise <code>false</code>).
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Whether this <code>Round</code> has stopped accepting "new"
	 * <code>Input</code>s yet (by which I mean, <code>Input</code>s created on or
	 * after the current time).
	 * 
	 * @return <code>true</code> if the <code>Round</code> has finished (otherwise
	 *         <code>false</code>).
	 */
	public boolean isFinished() {
		return started && System.currentTimeMillis() - startTime >= duration;
	}
	
	/**
	 * Finds out if a given <code>Input</code> was requested during the accepting
	 * interval of this <code>Round</code>, i.e., whether it should be accepted as a
	 * valid input for the round.
	 * 
	 * @param i - the <code>Input</code> to test.
	 * 
	 * @return <code>true</code> if the <code>Round</code> has been started and the
	 *         <code>Input</code>'s request time (from {@link Input#getTime()} is
	 *         both later than the start time and earlier than the finish time ( =
	 *         start time + duration) of this <code>Round</code> (otherwise
	 *         <code>false</code>).
	 */
	public boolean shouldAccept(Input i) {
		return started && i.getTime() >= startTime && i.getTime() <= startTime + duration;
	}

	/**
	 * Creates a new <code>Round</code> that comes immediately after this one in the
	 * series.
	 * 
	 * @return the next <code>Round</code> in the series.
	 */
	public Round next() {
		return new Round(this);
	}
}
