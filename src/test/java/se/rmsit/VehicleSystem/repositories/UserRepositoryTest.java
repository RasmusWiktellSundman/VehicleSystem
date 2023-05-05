package se.rmsit.VehicleSystem.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.UserType;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.exceptions.DuplicateEntityException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
	private UserRepository userRepository;

	@BeforeEach
	void setup() throws IOException {
		TestHelper.resetDataFiles();
		userRepository = new UserRepository();
	}

	@Test
	void repositoryCanStoreUser() {
		User testUser = new User(1, "Test", "test@testing.se", "aHash", UserType.CUSTOMER);
		assertDoesNotThrow(() -> userRepository.update(testUser));
		assertTrue(new File(Configuration.getProperty("data_directory")+"/users/1.txt").exists());
	}

	@Test
	void canGetUserByIdFromRepository() throws IOException, DuplicateEntityException {
		User testCustomer = new User(1, "Test", "test@testing.se", "aHash", UserType.CUSTOMER);
		User testAdmin = new User(2, "Admin", "admin@testing.se", "no_hashing", UserType.ADMIN);
		userRepository.update(testCustomer);
		userRepository.update(testAdmin);

		assertEquals(testCustomer, userRepository.getById(1).get());
		assertEquals(testAdmin, userRepository.getById(2).get());
	}

	@Test
	void cantGetNonExistingUser() {
		assertThrows(NoSuchElementException.class, () -> userRepository.getById(1).get());
	}

	@Test
	void canGetUserByUsernameFromRepository() throws IOException, DuplicateEntityException {
		User testCustomer = new User(1, "Test", "test@testing.se", "aHash", UserType.CUSTOMER);
		User testAdmin = new User(2, "Admin", "admin@testing.se", "no_hashing", UserType.ADMIN);
		userRepository.update(testCustomer);
		userRepository.update(testAdmin);

		assertEquals(testCustomer, userRepository.getByUsername("Test").get());
		assertEquals(testAdmin, userRepository.getByUsername("Admin").get());
	}

	@Test
	void canGetUserByEmailFromRepository() throws IOException, DuplicateEntityException {
		User testCustomer = new User(1, "Test", "test@testing.se", "aHash", UserType.CUSTOMER);
		User testAdmin = new User(2, "Admin", "admin@testing.se", "no_hashing", UserType.ADMIN);
		userRepository.update(testCustomer);
		userRepository.update(testAdmin);

		assertEquals(testCustomer, userRepository.getByEmail("test@testing.se").get());
		assertEquals(testAdmin, userRepository.getByEmail("admin@testing.se").get());
	}

	@Test
	void canGetAllUsersFromRepository() throws IOException, DuplicateEntityException {
		User testCustomer = new User(1, "Test", "test@testing.se", "aHash", UserType.CUSTOMER);
		User testAdmin = new User(2, "Admin", "admin@testing.se", "no_hashing", UserType.ADMIN);
		userRepository.update(testCustomer);
		userRepository.update(testAdmin);

		List<User> expected = List.of(testCustomer, testAdmin);
		assertEquals(expected, userRepository.getAll());
	}

	@Test
	void cantAddSameUsernameMultipleTimes() throws IOException, DuplicateEntityException {
		User testCustomer = new User(1, "Test", "test@testing.se", "aHash", UserType.CUSTOMER);
		User testCustomer2 = new User(2, "Test", "test2@testing.se", "aHash", UserType.CUSTOMER);
		userRepository.update(testCustomer);
		assertThrows(DuplicateEntityException.class, () -> userRepository.update(testCustomer2));
	}

	@Test
	void cantAddSameEmailMultipleTimes() throws IOException, DuplicateEntityException {
		User testCustomer = new User(1, "Test", "test@testing.se", "aHash", UserType.CUSTOMER);
		User testCustomer2 = new User(2, "Test2", "test@testing.se", "aHash", UserType.CUSTOMER);
		userRepository.update(testCustomer);
		assertThrows(DuplicateEntityException.class, () -> userRepository.update(testCustomer2));
	}

	@Test
	void canDeleteUser() throws IOException, DuplicateEntityException {
		User testCustomer = new User(1, "Test", "test@testing.se", "aHash", UserType.CUSTOMER);
		User testCustomer2 = new User(2, "Test2", "test2@testing.se", "aHash", UserType.CUSTOMER);
		userRepository.update(testCustomer);
		userRepository.update(testCustomer2);
		assertDoesNotThrow(() -> userRepository.delete(testCustomer2));
		assertFalse(userRepository.getAll().contains(testCustomer2));
		assertTrue(() -> userRepository.getById(2).isEmpty());
		assertFalse(new File(Configuration.getProperty("data_directory")+"/users/2.txt").exists());
	}

	@Test
	void vanLoadAllFromFile() throws DuplicateEntityException, IOException {
		User testCustomer = new User(1, "Test", "test@testing.se", "aHash", UserType.CUSTOMER);
		User testAdmin = new User(2, "Admin", "admin@testing.se", "no_hashing", UserType.ADMIN);
		userRepository.update(testCustomer);
		userRepository.update(testAdmin);

		// Skapar nytt repository för att ladda in från fil
		userRepository = new UserRepository();

		List<User> expected = List.of(testCustomer, testAdmin);
		assertEquals(expected, userRepository.getAll());
	}
}
