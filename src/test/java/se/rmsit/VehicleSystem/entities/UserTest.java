package se.rmsit.VehicleSystem.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.FileHandler;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.TestUser;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
	@BeforeEach
	void setup() throws IOException {
		TestHelper.resetDataFiles();
	}

	@Test
	void canStoreUser() {
		User user = new TestUser("1", "Test", "something", "test@testin.se", "no_hashing");
		assertDoesNotThrow(() -> FileHandler.storeObject(user, "users"));
		assertTrue(new File(Configuration.getProperty("data_directory")+"/users/1.txt").exists());
	}

	@Test
	void canLoadUser() throws IOException {
		User user = new TestUser("1", "Test", "something", "test@testin.se", "no_hashing");
		assertDoesNotThrow(() -> FileHandler.storeObject(user, "users"));
		assertEquals(user, FileHandler.loadObject(new TestUser(), "1","users"));

		User admin = new TestUser("2", "AdminTest", "something", "admin@testin.se", "no_hashing");
		assertDoesNotThrow(() -> FileHandler.storeObject(admin, "users"));
		assertEquals(admin, FileHandler.loadObject(new TestUser(), "2","users"));
	}

	@Test
	void canLoadUserWithNullValue() throws IOException {
		User user = new TestUser("1", "Test", null, "test@testin.se", "no_hashing");
		assertDoesNotThrow(() -> FileHandler.storeObject(user, "users"));
		assertEquals(user, FileHandler.loadObject(new TestUser(), "1","users"));
	}
}