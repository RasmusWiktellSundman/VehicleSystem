package se.rmsit.VehicleSystem.repositories;

import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.FileHandler;
import se.rmsit.VehicleSystem.entities.Fetchable;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.exceptions.DuplicateEntityException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserRepository {
	private List<User> users = new ArrayList<>();

	public UserRepository() throws IOException {
		loadUsers();
	}

	private void loadUsers() throws IOException {
		// Populerar users array med data från persistent lagring
		for (Fetchable fetchable : FileHandler.getAllObjects(User.class, "users")) {
			users.add((User) fetchable);
		}
	}

	public Optional<User> getById(long id) {
		for (User user : users) {
			if(user.getId() == id) {
				return Optional.of(user);
			}
		}
		return Optional.empty();
	}

	public Optional<User> getByUsername(String username) {
		for (User user : users) {
			if(user.getUsername().equals(username)) {
				return Optional.of(user);
			}
		}
		return Optional.empty();
	}

	public Optional<User> getByEmail(String email) {
		for (User user : users) {
			if(user.getEmail().equals(email)) {
				return Optional.of(user);
			}
		}
		return Optional.empty();
	}

	public List<User> getAll() {
		return users;
	}

	public void update(User user) throws IOException, DuplicateEntityException {
		// Kollar om användarnamnet redan är upptaget av annan användare
		Optional<User> userWithSameUsernameOptional = this.getByUsername(user.getUsername());
		if(userWithSameUsernameOptional.isPresent() && user.getId() != userWithSameUsernameOptional.get().getId()) {
			throw new DuplicateEntityException("Username is already in use");
		}

		Optional<User> userWithSameEmailOptional = this.getByEmail(user.getEmail());
		if(userWithSameEmailOptional.isPresent() && user.getId() != userWithSameEmailOptional.get().getId()) {
			throw new DuplicateEntityException("E-mail is already in use");
		}

		if(!users.contains(user)) {
			users.add(user);
		}
		FileHandler.storeObject(user,"users");
	}

	public void delete(User user) {
		users.remove(user);
		FileHandler.deleteObject(user.getId(), "users");
	}
}
