package se.rmsit.VehicleSystem.authentication;

import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.exceptions.InvalidLoginCredentials;
import se.rmsit.VehicleSystem.exceptions.NoLoggedInUser;
import se.rmsit.VehicleSystem.repositories.UserRepository;

public class Authentication {
	private static Authentication single_instance;
	private UserRepository userRepository;
	private Loginable loggedInUser;


	// Auth klassen följer singleton designmönstret
	public Authentication(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Loginable login(String email, String password) throws InvalidLoginCredentials {
		User user = userRepository.getByEmail(email).orElseThrow(InvalidLoginCredentials::new);
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
	public Loginable getUser() throws NoLoggedInUser {
		if(loggedInUser == null)
			throw new NoLoggedInUser();
		return loggedInUser;
	}

	public void logout() {
		loggedInUser = null;
	}
}
