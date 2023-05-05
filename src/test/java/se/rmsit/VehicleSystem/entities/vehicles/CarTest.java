package se.rmsit.VehicleSystem.entities.vehicles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.TestVehicle;
import se.rmsit.VehicleSystem.entities.Customer;

import java.io.IOException;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CarTest {
	private Car car;

	@BeforeEach
	void setUp() {
		Calendar today = Calendar.getInstance();
		car = new Car(new Customer(), "ABC123", 4, 4, today, (Calendar) today.clone(), 50000, "something");
	}

	@Test
	void throwsOnNegativeMaxPassengers() {
		Calendar today = Calendar.getInstance();
		assertThrows(IllegalArgumentException.class, () -> new Car(new Customer(), "ABC123", -1, 4, today, (Calendar) today.clone(), 10, "something"));
		assertThrows(IllegalArgumentException.class, () -> car.setMaximumPassengers(-1));
	}

	@Test
	void canGetFromStorage() throws IOException {
		Customer testCustomer = new Customer("1", "Test", "Dev", null, null, null, null, false, "test@testing.se", "hej");
		testCustomer.save();
		Vehicle vehicle = new Car(testCustomer, "ABC123", 4, 4, Calendar.getInstance(), Calendar.getInstance(), 100, "an address");
		Vehicle vehicle2 = new Car(testCustomer, "ABC124", 1, 2, Calendar.getInstance(), Calendar.getInstance(), 100, "an address2");
		assertDoesNotThrow(vehicle::save);
		assertDoesNotThrow(vehicle2::save);
		assertEquals(vehicle, Vehicle.getByRegistrationNumber("ABC123"));
		assertEquals(vehicle2, Vehicle.getByRegistrationNumber("ABC124"));
	}

	@Test
	void throwsOnNegativeWheels() {
		Calendar today = Calendar.getInstance();
		assertThrows(IllegalArgumentException.class, () -> new Car(new Customer(), "ABC123", 1, -4, today, (Calendar) today.clone(), 10, "something"));
		assertThrows(IllegalArgumentException.class, () -> car.setWheels(-1));
	}

	@Test
	void throwsOnBoughDateOlderThanConstructionDate() {
		Calendar today = Calendar.getInstance();
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);

		assertThrows(IllegalArgumentException.class, () -> new Car(new Customer(), "abc", 1, 4, today, yesterday, 10, "something"));
		assertThrows(IllegalArgumentException.class, () -> car.setBoughtDate(yesterday));
	}
}