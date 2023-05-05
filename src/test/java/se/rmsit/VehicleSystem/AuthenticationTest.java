package se.rmsit.VehicleSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.authentication.Authentication;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.exceptions.DuplicateEntityException;
import se.rmsit.VehicleSystem.exceptions.InvalidLoginCredentials;
import se.rmsit.VehicleSystem.exceptions.NoLoggedInUser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticationTest {
	private Authentication authentication;

	@BeforeEach
	void setup() throws IOException {
		TestHelper.resetDataFiles();
		authentication = new Authentication();
	}

	@Test
	void canLogin() throws DuplicateEntityException, IOException, InvalidLoginCredentials {
		// Skapar ny användare
		User user = new TestUser("1", "Test", null, "test@testing.se", "a_password");
		user.save();

		assertEquals(user, authentication.login("test@testing.se", "a_password"));
	}

	@Test
	void throwsOnInvalidCredentials() throws DuplicateEntityException, IOException {
		// Skapar ny användare
		User user = new TestUser("1", "Test", null, "test@testing.se", "a_password");
		user.save();

		// Felaktig lösenord
		assertThrows(InvalidLoginCredentials.class, () -> authentication.login("test@testing.se", "a_wrong_password"));

		// Felaktig email
		assertThrows(InvalidLoginCredentials.class, () -> authentication.login("test2@testing.se", "a_password"));
	}

	@Test
	void canGetLoggedInUser() throws InvalidLoginCredentials, DuplicateEntityException, IOException, NoLoggedInUser {
		// Skapar ny användare
		User user = new TestUser("1", "Test", null, "test@testing.se", "a_password");
		user.save();

		// Loggar in användare
		authentication.login("test@testing.se", "a_password");

		assertEquals(user, authentication.getUser());
	}

	@Test
	void cantGetLoggedInUserWhenNotLoggedIn() throws IOException {
		// Skapar ny användare
		User user = new TestUser("1", "Test", null, "test@testing.se", "a_password");
		user.save();

		assertThrows(NoLoggedInUser.class, () -> authentication.getUser());
	}

	@Test
	void canLogOut() throws InvalidLoginCredentials, DuplicateEntityException, IOException, NoLoggedInUser {
		// Skapar ny användare
		User user = new TestUser("1", "Test", null, "test@testing.se", "a_password");
		user.save();

		// Loggar in användare
		authentication.login("test@testing.se", "a_password");

		assertDoesNotThrow(() -> authentication.logout());

		assertThrows(NoLoggedInUser.class, () -> authentication.getUser());
	}

}