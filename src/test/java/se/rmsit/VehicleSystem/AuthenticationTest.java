package se.rmsit.VehicleSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.authentication.Authentication;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.exceptions.DuplicateEntityException;
import se.rmsit.VehicleSystem.exceptions.InvalidLoginCredentials;
import se.rmsit.VehicleSystem.exceptions.NoLoggedInUser;
import se.rmsit.VehicleSystem.repositories.UserRepository;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticationTest {
	private UserRepository userRepository;
	private Authentication authentication;

	@BeforeEach
	void setup() throws IOException {
		TestHelper.resetDataFiles();
		userRepository = new UserRepository();
		authentication = new Authentication(userRepository);
	}

	@Test
	void canLogin() throws DuplicateEntityException, IOException, InvalidLoginCredentials {
		// Skapar ny användare
		User user = new TestUser("1", "Test", null, "test@testing.se", "a_password");
		userRepository.update(user);

		assertEquals(user, authentication.login("test@testing.se", "a_password"));
	}

	@Test
	void throwsOnInvalidCredentials() throws DuplicateEntityException, IOException {
		// Skapar ny användare
		User user = new TestUser("1", "Test", null, "test@testing.se", "a_password");
		userRepository.update(user);

		// Felaktig lösenord
		assertThrows(InvalidLoginCredentials.class, () -> authentication.login("test@testing.se", "a_wrong_password"));

		// Felaktig email
		assertThrows(InvalidLoginCredentials.class, () -> authentication.login("test2@testing.se", "a_password"));
	}

	@Test
	void canGetLoggedInUser() throws InvalidLoginCredentials, DuplicateEntityException, IOException, NoLoggedInUser {
		// Skapar ny användare
		User user = new TestUser("1", "Test", null, "test@testing.se", "a_password");
		userRepository.update(user);

		// Loggar in användare
		authentication.login("test@testing.se", "a_password");

		assertEquals(user, authentication.getUser());
	}

	@Test
	void cantGetLoggedInUserWhenNotLoggedIn() throws DuplicateEntityException, IOException {
		// Skapar ny användare
		User user = new TestUser("1", "Test", null, "test@testing.se", "a_password");
		userRepository.update(user);

		assertThrows(NoLoggedInUser.class, () -> authentication.getUser());
	}

	@Test
	void canLogOut() throws InvalidLoginCredentials, DuplicateEntityException, IOException, NoLoggedInUser {
		// Skapar ny användare
		User user = new TestUser("1", "Test", null, "test@testing.se", "a_password");
		userRepository.update(user);

		// Loggar in användare
		authentication.login("test@testing.se", "a_password");

		assertDoesNotThrow(() -> authentication.logout());

		assertThrows(NoLoggedInUser.class, () -> authentication.getUser());
	}

}