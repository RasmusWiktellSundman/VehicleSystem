package se.rmsit.VehicleSystem;

import se.rmsit.VehicleSystem.entities.User;

/**
 * Implementerad version av User för testande
 */
public class TestUser extends User {
	public TestUser() {}

	public TestUser(String firstName, String lastName, String email, String hashedPassword) {
		super(firstName, lastName, email, hashedPassword);
	}

	public TestUser(String userId, String firstName, String lastName, String email, String hashedPassword) {
		super(userId, firstName, lastName, email, hashedPassword);
	}
}
