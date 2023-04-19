package se.rmsit.VehicleSystem.entities;

public class Customer extends User {
	public Customer(long userId, String username, String email, String hashedPassword) {
		super(userId, username, email, hashedPassword);
	}
}
