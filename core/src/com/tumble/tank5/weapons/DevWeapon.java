package com.tumble.tank5.weapons;

public class DevWeapon extends Weapon {
	
	public DevWeapon(
			int damage,
			int fireDelay,
			int cooldown,
			int reloadDuration,
			int burstSize,
			int magSize,
			int reserveBullets,
			double baseRange) {
		super(
				damage, // Custom damage.
				fireDelay, // Custom fire delay.
				cooldown, // Custom interval between shots.
				reloadDuration,
				burstSize, // Custom burst size.
				magSize, // Custom mag size.
				reserveBullets, // Custom ammo reserve.
				baseRange, // Custom range.
				0, // No range variation, for consistent test results.
				0 // Perfect accuracy, for consistent test results.
		);
	}
	
}
