package com.tumble.tank5.world_logic;

import com.tumble.tank5.util.GameError;

public class Round {
	public final int duration;
	private final int baseDuration, amplitude, phase, period;
	
	public Round(int baseDuration, int amplitude, int phase, int period) {
		if (baseDuration < amplitude) {
			throw new GameError(
					"Round duration (" + baseDuration
					+ ") must be >= amplitude (" + amplitude + ")!");
		}
		if (period == 0) {
			throw new GameError("Round period (" + period + " must be > 0!");
		}
		
		duration = baseDuration + (int) (amplitude * Math.sin(phase * 2 * Math.PI / period));
		
		/*System.out.println("NEW ROUND: ");
		System.out.println("\tb: " + baseDuration);
		System.out.println("\tA:" + amplitude);
		System.out.println("\tp: " + phase);
		System.out.println("\tT: " + period);
		System.out.println("\ttheta: " + (double) (phase * 2 * Math.PI / period));
		System.out.println("\tDURATION: " + duration);*/
		
		this.baseDuration = baseDuration;
		this.amplitude = amplitude;
		this.phase = phase;
		this.period = period;
	}

	public Round next() {
		return new Round(baseDuration, amplitude, phase + 1, period);
	}
}
