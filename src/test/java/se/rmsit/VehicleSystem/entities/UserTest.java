package se.rmsit.VehicleSystem.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.TestUser;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
	@BeforeEach
	void setup() throws IOException {
		TestHelper.resetDataFiles();
		User.recalculateNextEmailFromStorage();
	}

	@Test
	void canStoreUser() {
		User user = new TestUser("1", "Test", "something", "test@testin.se", "no_hashing");
		assertDoesNotThrow(user::save);
		assertTrue(new File(Configuration.getProperty("data_directory")+"/users/1.txt").exists());
	}

	@Test
	void canGetUserFromStorage() throws IOException {
		User expected = new TestUser("1", "Test", "something", "test@testin.se", "no_hashing");
		expected.save();

		assertEquals(expected, User.getById("1"));
	}

	@Test
	void canGetUserByEmail() throws IOException {
		User expected = new TestUser("1", "Test", "something", "test@testin.se", "no_hashing");
		expected.save();

		assertEquals(expected, User.getByEmail("test@testin.se"));
	}

	@Test
	void canLoadUserWithNullValue() throws IOException {
		User user = new TestUser("1", "Test", null, "test@testin.se", "no_hashing");
		assertDoesNotThrow(user::save);
		assertEquals(user, User.getById("1"));
	}

	@Test
	void canUpdateUser() throws IOException {
		User user = new TestUser("1", "Test", null, "test@testin.se", "no_hashing");
		assertDoesNotThrow(user::save);
		assertEquals(user, User.getById("1"));

		user.setEmail("test2@testin.se");
		user.setFirstName("Hello");
		user.save();

		assertEquals(user, User.getById("1"));
	}

	@Test
	void canDeleteUser() throws IOException {
		User testUser = new TestUser("1", "Test", "something", "test@testing.se", "aHash");
		User testUser2 = new TestUser("2", "Test2", "something", "test2@testing.se", "aHash");
		testUser.save();
		testUser2.save();
		assertDoesNotThrow(testUser::delete);
		assertNull(User.getById("1"));
		assertFalse(new File(Configuration.getProperty("data_directory")+"/users/1.txt").exists());
	}

	@Test
	void canGetNextId() throws IOException {
		User testUser = new TestUser("1", "Test", "something", "test@testing.se", "aHash");
		User testUser2 = new TestUser("2", "Test2", "something", "test2@testing.se", "aHash");
		testUser.save();
		testUser2.save();
		testUser2.save();

		assertEquals("3", User.getNextId());

		User.recalculateNextEmailFromStorage();
		assertEquals("3", User.getNextId());
	}
}