package se.rmsit.VehicleSystem;

import se.rmsit.VehicleSystem.entities.User;

public interface Loginable {
	User login(String userNameOrEmail, String password);
}
