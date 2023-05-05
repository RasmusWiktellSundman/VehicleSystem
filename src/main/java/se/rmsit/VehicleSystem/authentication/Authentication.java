package se.rmsit.VehicleSystem.authentication;

import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.exceptions.InvalidLoginCredentials;
import se.rmsit.VehicleSystem.exceptions.NoLoggedInUser;

import java.io.IOException;

public class Authentication {
	private User loggedInUser;


	// Auth klassen följer singleton designmönstret
	public Authentication() {}

	public User login(String email, String password) throws InvalidLoginCredentials, IOException {
		User user = User.getByEmail(email);
		if(user == null) {
			throw new InvalidLoginCredentials();
		}
		if(user.getHashedPassword().equals(password)) {
			loggedInUser = user;
			return user;
		}
		throw new InvalidLoginCredentials();
	}


	/**
	 * Hämtar den inloggad användaren
	 * @return Den inloggade användaren
	 * @throws NoLoggedInUser Ifall ingen är inloggad
	 */
	public User getUser() throws NoLoggedInUser {
		if(loggedInUser == null)
			throw new NoLoggedInUser();
		return loggedInUser;
	}

	public void logout() {
		loggedInUser = null;
	}
}
