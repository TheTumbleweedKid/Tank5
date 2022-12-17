package com.tumble.tank5.testing;

import org.junit.jupiter.api.Test;

import com.tumble.tank5.world_logic.game_n_world.Game;


public class CoreTests {
	@Test
	public void test_01() {
		Game g = new Game(true, 1, 0);
		
		assert !g.start(0, 0, 0, 0);
	}
}
