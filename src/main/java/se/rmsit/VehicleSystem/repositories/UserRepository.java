package se.rmsit.VehicleSystem.repositories;

import com.google.gson.Gson;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.JsonFileWriter;
import se.rmsit.VehicleSystem.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
	private List<User> users = new ArrayList<>();

	public UserRepository() throws IOException {
		loadUsers();
	}

	private void loadUsers() throws IOException {
		// Hämta lista på filer i users data mapp
//		final File[] userFiles = new File(Configuration.getProperty("data_directory") + "\\users").listFiles();
//		for (File userFile : userFiles) {
//			users.add(new Gson().fromJson(
//					JsonFileWriter.loadJson(userFile),
//					User.class
//			));
//		}
	}

	public Optional getById(long id) {
		return Optional.empty();
	}

	public Optional getByName(String name) {
		return Optional.empty();
	}

	public List getAll() {
		return null;
	}

	public void update(User user) throws IOException {
		File userFile = new File(Configuration.getProperty("data_directory")+"\\users\\"+user.getUserId()+".json");
		JsonFileWriter.writeJson(user, userFile);
	}

	public void delete(User user) {

	}
}
