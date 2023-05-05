package se.rmsit.VehicleSystem;

import se.rmsit.VehicleSystem.entities.User;

/**
 * Implementerad version av User för testande
 */
public class TestUser extends User {
	public TestUser() {}

	public TestUser(long userId, String firstName, String lastName, String email, String hashedPassword, UserType userType) {
		super(userId, firstName, lastName, email, hashedPassword, userType);
	}
}
