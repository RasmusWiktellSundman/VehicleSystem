package se.rmsit.VehicleSystem;

import se.rmsit.VehicleSystem.entities.Fetchable;
import se.rmsit.VehicleSystem.entities.User;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class FileHandler {
	private static final String dataDirectoryPath = Configuration.getProperty("data_directory");
	public static void createDataFolders() {

		// create user data folder
		File usersDataFolder = new File(dataDirectoryPath + "\\users");
		if(!usersDataFolder.exists())
			usersDataFolder.mkdirs();
	}

	public static void storeObject(Fetchable object, String subPath) throws IOException {
		File file = new File(dataDirectoryPath+"\\"+subPath+"\\"+object.getId()+".txt");
		if(!file.exists())
			file.createNewFile();
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		object.store(writer);
		writer.close();
	}

	public static void storeObject(Fetchable object) throws IOException {
		storeObject(object, "");
	}

	public static Fetchable loadObject(Fetchable fetchable, long id, String subPath) throws IOException {
		File file = new File(dataDirectoryPath+"\\"+subPath+"\\"+id+".txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));

		fetchable.load(reader);
		return fetchable;
	}

	public static Fetchable loadObject(Fetchable fetchable, long id) throws IOException {
		return loadObject(fetchable, id, "users");
	}
}
