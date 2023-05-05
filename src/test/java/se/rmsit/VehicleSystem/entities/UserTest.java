package se.rmsit.VehicleSystem.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.FileHandler;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.UserType;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
	@BeforeEach
	void setup() {
		TestHelper.resetDataFiles();
	}

	@Test
	void canStoreUser() {
		User user = new User(1, "Test", "test@testin.se", "no_hashing", UserType.CUSTOMER);
		assertDoesNotThrow(() -> FileHandler.storeObject(user, "users"));
		assertTrue(new File(Configuration.getProperty("data_directory")+"/users/1.txt").exists());
	}

	@Test
	void canLoadUser() throws IOException {
		User user = new User(1, "Test", "test@testin.se", "no_hashing", UserType.CUSTOMER);
		assertDoesNotThrow(() -> FileHandler.storeObject(user, "users"));
		assertEquals(user, FileHandler.loadObject(new User(), 1,"users"));

		User admin = new User(2, "AdminTest", "admin@testin.se", "no_hashing", UserType.ADMIN);
		assertDoesNotThrow(() -> FileHandler.storeObject(admin, "users"));
		assertEquals(admin, FileHandler.loadObject(new User(), 2,"users"));
	}
}