package se.rmsit.VehicleSystem.entities;

import se.rmsit.VehicleSystem.authentication.Loginable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public abstract class User implements Loginable, Fetchable {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String hashedPassword;

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public User() {}

	public User(String userId, String firstName, String lastName, String email, String hashedPassword) {
		this.id = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.hashedPassword = hashedPassword;
	}

	@Override
	public void load(BufferedReader reader) throws IOException {
		// Skapar objekt från reader data (samma som store metoden)
		while (true) {
			String line = reader.readLine();
			if(line == null) {
				break;
			}
			// Delar upp raden i nyckel-data par
			String[] tokens = line.split(": ");
			String data = tokens[1];
			if(data.equals("null")) {
				data = null;
			}
			switch (tokens[0]) {
				case "user_id" -> setId(data);
				case "first_name" -> setFirstName(data);
				case "last_name" -> setLastName(data);
				case "email" -> setEmail(data);
				case "hashed_password" -> setHashedPassword(data);
			}
		}
	}

	@Override
	public void store(PrintWriter printWriter) {
		printWriter.println("class: " + getClass().getName());
		printWriter.println("user_id: " + getId());
		printWriter.println("first_name: " + getFirstName());
		printWriter.println("last_name: " + getLastName());
		printWriter.println("email: " + getEmail());
		printWriter.println("hashed_password: " + getHashedPassword());
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
