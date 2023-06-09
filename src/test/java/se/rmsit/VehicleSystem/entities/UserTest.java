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
		User.recalculateNextUserIdFromStorage();
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

		User.recalculateNextUserIdFromStorage();
		assertEquals("3", User.getNextId());
	}

	@Test
	void canCreateUserWithAutomaticId() throws IOException {
		User testUser1 = new TestUser("Test", "something", "test@testing.se", "aHash");
		testUser1.save();
		User testUser2 = new TestUser("Test2", "something", "test2@testing.se", "aHash");
		testUser2.save();

		User expected1 = new TestUser("1", "Test", "something", "test@testing.se", "aHash");
		User expected2 = new TestUser("2", "Test2", "something", "test2@testing.se", "aHash");

		assertEquals(expected1, testUser1);
		assertEquals(expected2, testUser2);
	}

	@Test
	void cantUseInvalidEmail() {
		assertThrows(IllegalArgumentException.class, () -> new TestUser("Testing", "something", "test.se", "password"));
		assertDoesNotThrow(() -> new TestUser("Testing", "something", "testTesting@test.se", "password"));
		assertDoesNotThrow(() -> new TestUser("Testing", "something", "testTestingåäöÅÄÖ@test.se", "password"));
	}

	@Test
	void canGetFullName() {
		User testUser1 = new TestUser("Test", "something", "test@testing.se", "aHash");
		User testUser2 = new TestUser("Test", null, "test@testing.se", "aHash");
		assertEquals("Test something", testUser1.getFullName());
		assertEquals("Test", testUser2.getFullName());
	}
}