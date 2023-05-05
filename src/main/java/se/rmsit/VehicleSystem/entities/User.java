package se.rmsit.VehicleSystem.entities;

import se.rmsit.VehicleSystem.Loginable;
import se.rmsit.VehicleSystem.UserType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public abstract class User implements Loginable, Fetchable {
	private long id;
	private String firstName;
	private String lastName;
	private String email;
	private String hashedPassword;
	private UserType userType;

	@Override
	public User login(String userNameOrEmail, String password) {
		return null;
	}

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public User() {}

	public User(long userId, String firstName, String lastName, String email, String hashedPassword, UserType userType) {
		this.id = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.hashedPassword = hashedPassword;
		this.userType = userType;
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
			switch (tokens[0]) {
				case "user_id" -> setId(Long.parseLong(tokens[1]));
				case "first_name" -> setFirstName(tokens[1]);
				case "last_name" -> setLastName(tokens[1]);
				case "email" -> setEmail(tokens[1]);
				case "hashed_password" -> setHashedPassword(tokens[1]);
				case "user_type" -> setUserType(UserType.valueOf(tokens[1]));
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
		printWriter.println("user_type: " + getUserType().toString());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		User user = (User) o;
		if (id != user.id) return false;
		if (!Objects.equals(firstName, user.firstName)) return false;
		if (!Objects.equals(lastName, user.lastName)) return false;
		if (!Objects.equals(email, user.email)) return false;
		if (userType != user.userType) return false;
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
				", userType=" + userType +
				'}';
	}

	// Getters and setters
	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
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
		this.email = email;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
}
