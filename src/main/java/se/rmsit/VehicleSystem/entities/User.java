package se.rmsit.VehicleSystem.entities;

import se.rmsit.VehicleSystem.Loginable;

public abstract class User implements Loginable {
	private long userId;
	private String username;
	private String email;
	private String hashedPassword;

	@Override
	public User login(String userNameOrEmail, String password) {
		return null;
	}

	public User(long userId, String username, String email, String hashedPassword) {
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.hashedPassword = hashedPassword;
	}

	// Getters and setters
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
}
