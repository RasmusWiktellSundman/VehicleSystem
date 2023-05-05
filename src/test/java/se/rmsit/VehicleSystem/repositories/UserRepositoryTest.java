package se.rmsit.VehicleSystem.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.FileHandler;
import se.rmsit.VehicleSystem.UserType;
import se.rmsit.VehicleSystem.entities.User;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
	private UserRepository userRepository;

	@BeforeEach
	void setup() throws IOException {
		// Remove test data directory
		File dataFolder = new File(Configuration.getProperty("data_directory"));
		if(dataFolder.exists()) {
			for (String file : dataFolder.list()) {
				System.out.println(file);
//				System.out.println(file.delete());
			}
		}
		FileHandler.createDataFolders();

		userRepository = new UserRepository();
	}

	@Test
	void canStoreCustomerUser() {
		System.out.println(Configuration.getProperty("data_directory"));
		// Använder Customer istället för User, då User är abstract
		User testUser = new TestUser(1, "Test", "test@testing.se", "aHash");
		assertDoesNotThrow(() -> userRepository.update(testUser));
		assertTrue(new File(Configuration.getProperty("data_directory")+"/users/1").exists());
	}

	private class TestUser extends User {
		public TestUser(long userId, String username, String email, String hashedPassword) {
			super(userId, username, email, hashedPassword, UserType.CUSTOMER);
		}
	}
}
