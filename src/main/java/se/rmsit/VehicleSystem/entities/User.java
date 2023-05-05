package se.rmsit.VehicleSystem.entities;

import se.rmsit.VehicleSystem.FileHandler;

import java.io.IOException;
import java.util.Objects;

public abstract class User implements Fetchable {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String hashedPassword;

	private static long highestUsedId;

	static {
		try {
			recalculateNextEmailFromStorage();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void recalculateNextEmailFromStorage() throws IOException {
		long highestId = 0;
		for (Fetchable fetchable : FileHandler.getAllObjects("users")) {
			try {
				long id = Long.parseLong(fetchable.getId());
				if(id > highestId) {
					highestId = id;
				}

				// Ignorera ifall id:t inte är numeriskt (bör inte hända för användare)
			} catch (NumberFormatException ignored) {}
		}
		highestUsedId = highestId;
	}

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public User() {}

	public User(String firstName, String lastName, String email, String hashedPassword) {
		this(getNextId(), firstName, lastName, email, hashedPassword);
	}

	public User(String userId, String firstName, String lastName, String email, String hashedPassword) {
		this.id = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.hashedPassword = hashedPassword;
	}

	public static User getById(String id) throws IOException {
		// Hämtar användare från persistent lagring
		return (User) FileHandler.loadObject(id, "users");
	}

	public static User getByEmail(String email) throws IOException {
		for (Fetchable fetchable : FileHandler.getAllObjects("users")) {
			// Kollar om e-posten från den inlästa användaren är samma som den givna e-posten
			if(((User) fetchable).getEmail().equals(email)) {
				return (User) fetchable;
			}
		}
		return null;
	}

	/**
	 * Hämtar nästa lediga id
	 * @return Id (numerisk string)
	 */
	public static String getNextId() {
		return String.valueOf(highestUsedId+1);
	}

	/**
	 * Sparar objektet till persistent lagring. Skriver över ifall användare med samma id redan finns.
	 * @throws IOException
	 */
	public void save() throws IOException {
		FileHandler.storeObject(this, "users");
		long id = Long.parseLong(getId());
		if(id > highestUsedId) {
			highestUsedId = id;
		}
	}

	/**
	 * Raderar användaren från persistent lagring
	 * @throws IOException
	 */
	public void delete() throws IOException {
		FileHandler.deleteObject(getId(), "users");
	}

	/**
	 * Laddar in ett nyckel-värde par till objektet
	 * @param key Nyckeln för värdet
	 * @param value Värdet
	 */
	@Override
	public void loadData(String key, String value) {
		switch (key) {
			case "user_id" -> setId(value);
			case "first_name" -> setFirstName(value);
			case "last_name" -> setLastName(value);
			case "email" -> setEmail(value);
			case "hashed_password" -> setHashedPassword(value);
		}
	}

	@Override
	public String serialize() {
		return "user_id: " + getId() + "\n" +
				"first_name: " + getFirstName() + "\n" +
				"last_name: " + getLastName() + "\n" +
				"email: " + getEmail() + "\n" +
				"hashed_password: " + getHashedPassword();
	}

	@Override
	public boolean equals(Object customer) {
		if (this == customer) return true;
		if (customer == null || getClass() != customer.getClass()) return false;

		User user = (User) customer;
		if (!Objects.equals(id, user.id)) return false;
		if (!Objects.equals(firstName, user.firstName)) return false;
		if (!Objects.equals(lastName, user.lastName)) return false;
		if (!Objects.equals(email, user.email)) return false;
		return Objects.equals(hashedPassword, user.hashedPassword);
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", email='" + email + '\'' +
				", hashedPassword='" + hashedPassword + '\'' +
				'}';
	}

	// Getters and setters
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		//TODO: Validera format
		this.email = email;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
}
