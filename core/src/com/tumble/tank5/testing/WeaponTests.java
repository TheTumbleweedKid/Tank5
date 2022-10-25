package com.tumble.tank5.testing;

import org.junit.jupiter.api.Test;

import com.tumble.tank5.world_logic.GameWorld;


public class WeaponTests {
	@Test
	public void test_01() {
		GameWorld gW = new GameWorld();
		
		gW.loadFromString(
				"  W  \n" +
				"  W  \n" +
				"     \n" +
				"  W  \n" +
				"  W  \n");
	}
}
