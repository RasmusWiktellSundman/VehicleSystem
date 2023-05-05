package se.rmsit.VehicleSystem.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.TestUser;
import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.exceptions.DuplicateEntityException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest {
	private UserRepository userRepository;

	@BeforeEach
	void setup() throws IOException {
		TestHelper.resetDataFiles();
		userRepository = new UserRepository();
	}

	@Test
	void repositoryCanStoreUser() {
		User testUser = new TestUser("1", "Test", "something", "test@testing.se", "aHash");
		assertDoesNotThrow(() -> userRepository.update(testUser));
		assertTrue(new File(Configuration.getProperty("data_directory")+"/users/1.txt").exists());
	}

	@Test
	void canGetUserByIdFromRepository() throws IOException, DuplicateEntityException {
		User testCustomer = new TestUser("1", "Test", "something", "test@testing.se", "aHash");
		User testAdmin = new TestUser("2", "Admin", "something", "admin@testing.se", "no_hashing");
		userRepository.update(testCustomer);
		userRepository.update(testAdmin);

		assertEquals(testCustomer, userRepository.getById("1").get());
		assertEquals(testAdmin, userRepository.getById("2").get());
	}

	@Test
	void cantGetNonExistingUser() {
		assertThrows(NoSuchElementException.class, () -> userRepository.getById("1").get());
	}

	@Test
	void canGetUserByEmailFromRepository() throws IOException, DuplicateEntityException {
		User testCustomer = new TestUser("1", "Test", "something", "test@testing.se", "aHash");
		User testAdmin = new TestUser("2", "Admin", "something", "admin@testing.se", "no_hashing");
		userRepository.update(testCustomer);
		userRepository.update(testAdmin);

		assertEquals(testCustomer, userRepository.getByEmail("test@testing.se").get());
		assertEquals(testAdmin, userRepository.getByEmail("admin@testing.se").get());
	}

	@Test
	void canGetAllUsersFromRepository() throws IOException, DuplicateEntityException {
		User testCustomer = new TestUser("1", "Test", "something", "test@testing.se", "aHash");
		User testAdmin = new TestUser("2", "Admin", "something", "admin@testing.se", "no_hashing");
		userRepository.update(testCustomer);
		userRepository.update(testAdmin);

		List<User> expected = List.of(testCustomer, testAdmin);
		assertEquals(expected, userRepository.getAll());
	}

	@Test
	void cantAddSameEmailMultipleTimes() throws IOException, DuplicateEntityException {
		User testCustomer = new TestUser("1", "Test", "something", "test@testing.se", "aHash");
		User testCustomer2 = new TestUser("2", "Test2", "something", "test@testing.se", "aHash");
		userRepository.update(testCustomer);
		assertThrows(DuplicateEntityException.class, () -> userRepository.update(testCustomer2));
	}

	@Test
	void canDeleteUser() throws IOException, DuplicateEntityException {
		User testCustomer = new TestUser("1", "Test", "something", "test@testing.se", "aHash");
		User testCustomer2 = new TestUser("2", "Test2", "something", "test2@testing.se", "aHash");
		userRepository.update(testCustomer);
		userRepository.update(testCustomer2);
		assertDoesNotThrow(() -> userRepository.delete(testCustomer2));
		assertFalse(userRepository.getAll().contains(testCustomer2));
		assertTrue(() -> userRepository.getById("2").isEmpty());
		assertFalse(new File(Configuration.getProperty("data_directory")+"/users/2.txt").exists());
	}

	@Test
	void canLoadAllFromFile() throws DuplicateEntityException, IOException {
		// Använder Customer, då UserRepository inläsning av alla användare från filer inte stödjer TestUser
		User testUser = new TestUser("1", "Test", "something", "test@testing.se", "aHash");
		User testUser2 = new TestUser("2", "Test2", "something", "test2@testing.se", "aHash");
		userRepository.update(testUser);
		userRepository.update(testUser2);

		// Skapar nytt repository för att ladda in från fil
		userRepository = new UserRepository();

		List<User> expected = List.of(testUser, testUser2);
		assertIterableEquals(expected, userRepository.getAll());
	}

	@Test
	void canLoadCustomer() throws DuplicateEntityException, IOException {
		Customer testCustomer = new Customer("1", "Customer", "something", "an address", "Cyberspace", "12345", "0701234567", false, "customer@testing.se", "no_hashing");
		Customer testCustomer2 = new Customer("2", "Customer", "something2", "an address2", "Cyberspace2", "12346", "0701234568", true, "customer2@testing.se", "no_hashing2");
		userRepository.update(testCustomer);
		userRepository.update(testCustomer2);

		// Skapar nytt repository för att ladda in från fil
		userRepository = new UserRepository();

		assertDoesNotThrow(() -> (Customer) userRepository.getById("1").get());
		assertEquals(testCustomer, userRepository.getById("1").get());
	}
}
