package se.rmsit.VehicleSystem.entities;

public class Admin extends User {
	public Admin(String firstName, String lastName, String email, String hashedPassword) {
		super(firstName, lastName, email, hashedPassword);
	}

	public Admin(String userId, String firstName, String lastName, String email, String hashedPassword) {
		super(userId, firstName, lastName, email, hashedPassword);
	}

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public Admin() {
	}
}
