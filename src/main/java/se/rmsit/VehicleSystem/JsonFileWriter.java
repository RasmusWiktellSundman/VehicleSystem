package se.rmsit.VehicleSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class JsonFileWriter {
	public static void createDataFolders() {
		String dataDirectoryPath = Configuration.getProperty("data_directory");

		// create user data folder
		File usersDataFolder = new File(dataDirectoryPath + "\\users");
		if(!usersDataFolder.exists())
			usersDataFolder.mkdirs();
	}

	public static void writeJson(Object object, File file) throws IOException {
		String json = new Gson().toJson(object);
		if(!file.exists())
			file.createNewFile();
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		writer.write(json);
	}

	public static JsonObject loadJson(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		return JsonParser.parseString(reader.readLine()).getAsJsonObject();
	}
}
