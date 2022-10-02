package com.tumble.tank5.testing;

import org.junit.jupiter.api.Test;

import com.tumble.tank5.world_logic.Round;

public class RoundDurationTest {
	/**
	 * Test that <code>Round</code> durations remain consistent for a constant
	 * amplitude of 400ms, starting phase of 0, period of 15 turns,
	 * and a variable base duration ranging (in steps of 50ms) from 500 to 3500ms.
	 * 
	 * Runs over 25 cycles to be absolutely sure of consistency.
	 */
	@Test
	public void durationTest_01() {
		for (int i = 500; i <= 3500; i += 50) {
			assert testDuration(i, 400, 0, 15, 25);
		}
	}
	
	/**
	 * Test that <code>Round</code> durations remain consistent for a constant
	 * base duration of 1000ms, period of 15 turns, starting phase of 0,
	 * and a variable amplitude ranging (in steps of 50ms) from 50 to 1000ms.
	 * 
	 * Runs over 25 cycles to be absolutely sure of consistency.
	 */
	@Test
	public void durationTest_02() {
		for (int i = 50; i <= 1000; i += 50) {
			assert testDuration(1000, i, 0, 15, 25);
		}
	}
	
	/**
	 * Test that <code>Round</code> durations remain consistent for a constant base
	 * duration of 1000ms, amplitude of 400ms, starting phase of 0, and a variable
	 * period ranging (in steps of 1 turn) from 5 to 50 turns. Note that
	 * <b>multiples of 12 must be skipped</b>, because these lead to
	 * inconsistencies.
	 * 
	 * Runs over 25 cycles to be absolutely sure of consistency.
	 */
	@Test
	public void durationTest_03() {
		for (int i = 5; i <= 50; i++) {
			if (i % 12 != 0) {
				assert testDuration(1000, 400, 0, i, 25);
			}
		}
	}
	
	/**
	 * Test that <code>Round</code> durations remain consistent for a constant base
	 * duration of 1000ms, amplitude of 400ms, period of 15, and a variable
	 * starting phase ranging (in steps of 1 turn) from 0 to 14 (i.e., period - 1) turns. 
	 * 
	 * Runs over 25 cycles to be absolutely sure of consistency.
	 */
	@Test
	public void durationTest_04() {
		for (int i = 0; i <= 14; i++) {
			assert testDuration(1000, 400, i, 15, 25);
		}
	}
	
	private static boolean testDuration(int baseDuration, int amplitude, int startingPhase, int period, int numPeriods) {
		Round r = new Round(baseDuration, amplitude, startingPhase, period);
		// Figure out the duration of each round in a full cycle.
		int[] durations = new int[period];
		
		for (int i = 0; i < period; i++) {
			durations[i] = r.duration;
			r = r.next();
		}
		
		// Check that the calculated durations are consistently adhered to.
		for (int i = 0; i < period * numPeriods; i++) {
			if (durations[i % period] != r.duration) {
				System.out.println("Invalid duration ("
						+ r.duration
						+ " != "
						+ durations[i % period]
						+ ") for Round #"
						+ i + "!");
				System.out.println("Details:\n"
						+ "\tbase duration: " + baseDuration + "ms\n"
						+ "\tamplitude: " + amplitude + "ms\n"
						+ "\tphase: " + (startingPhase + i) + " turns\n"
						+ "\tperiod: " + period + " turns");
				System.out.println("\ttheta: " + (double) ((startingPhase + i) * 2 * Math.PI / period));
				System.out.println("\tsin: " + Math.sin((startingPhase + i) * 2 * Math.PI / period));
				System.out.println("\tdelta: " + (int) (amplitude * Math.sin((startingPhase + i) * 2 * Math.PI / period)));
				return false;
			}
			r = r.next();
		}
		
		return true;
	}
}
