package se.rmsit.VehicleSystem.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.TestHelper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerTest {
	@BeforeEach
	void setup() throws IOException {
		TestHelper.resetDataFiles();
		Customer.recalculateNextEmailFromStorage();
	}

	@Test
	void canCreateCustomerWithAutomaticId() throws IOException {
		User testUser1 = new Customer("Name", "Lastname", null, null, null, null, false, "test1@testing.se", "password");
		testUser1.save();
		User testUser2 = new Customer("Name2", "Lastname2", null, null, null, null, false, "test2@testing.se", "password2");
		testUser2.save();

		User expected1 = new Customer("1", "Name", "Lastname", null, null, null, null, false, "test1@testing.se", "password");
		User expected2 = new Customer("2", "Name2", "Lastname2", null, null, null, null, false, "test2@testing.se", "password2");

		assertEquals(expected1, testUser1);
		assertEquals(expected2, testUser2);
	}

	@Test
	void canCreateAndLoadCustomerWithEmptyAllowedValue() throws IOException {
		User testUser1 = new Customer("Name", "Lastname", "", "", "", "", false, "test1@testing.se", "password");
		testUser1.save();

		User expected1 = new Customer("1", "Name", "Lastname", "", "", "", "", false, "test1@testing.se", "password");

		assertEquals(expected1, testUser1);
	}

	@Test
	void canGetAllCustomers() throws IOException {
		Customer testCustomer1 = new Customer("Name", "Lastname", null, null, null, null, false, "test1@testing.se", "password");
		testCustomer1.save();
		Customer testCustomer2 = new Customer("Name2", "Lastname2", null, null, null, null, false, "test2@testing.se", "password2");
		testCustomer2.save();

		List<Customer> expected = List.of(testCustomer1, testCustomer2);
		assertEquals(expected, Customer.getAll());
	}
}