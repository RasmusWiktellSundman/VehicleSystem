package se.rmsit.VehicleSystem;

import se.rmsit.VehicleSystem.entities.Fetchable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileHandler {
	private static final String dataDirectoryPath = Configuration.getProperty("data_directory");
	public static void createDataFolders() {
		// Skapar mappar för användare och fordon
		createFolder("users");
		createFolder("vehicles");
	}

	private static void createFolder(String folder) {
		File vehiclesDataFolder = new File(dataDirectoryPath + "\\" + folder);
		if (!vehiclesDataFolder.exists())
			vehiclesDataFolder.mkdirs();
	}

	public static void storeObject(Fetchable object, String subPath) throws IOException {
		File file = new File(dataDirectoryPath+"\\"+subPath+"\\"+object.getId()+".txt");
		if(!file.exists())
			file.createNewFile();
		// Använder try-with-resource för att automatiskt stänga läsaren
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
			writer.println("class: " + object.getClass().getName());
			String serialized = object.serialize();
			writer.write(serialized);
		}
	}

	public static void storeObject(Fetchable object) throws IOException {
		storeObject(object, "");
	}

	public static Fetchable loadObject(Fetchable fetchable, String id, String subPath) throws IOException {
		File file = new File(dataDirectoryPath+"\\"+subPath+"\\"+id+".txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			fetchable.load(reader);
			return fetchable;
		}
	}

	public static Fetchable loadObject(Fetchable fetchable, String id) throws IOException {
		return loadObject(fetchable, id, "users");
	}

	/**
	 * Laddar in objekt, hämtar objekt typ från class: i presistent lagring.
	 * @param id Id:t av objektet att ladda in
	 * @param subPath Undermapp objektet är lagrat under
	 * @return Inläst objekt
	 * @throws IOException
	 */
	public static Fetchable loadObject(String id, String subPath) throws IOException {
		File file = new File(dataDirectoryPath+"\\"+subPath+"\\"+id+".txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {


			// Skapar objekt utifrån klass i datafil
			// Första raden ska följa formatet class: klass-namn
			// Skapar nytt objekt från clazz
			//TODO: Kolla att den innehåller class:
			String className = reader.readLine().split(" ")[1];
			Class clazz = Class.forName(className);
			Constructor constructor = clazz.getConstructor();
			Fetchable fetchable = (Fetchable) constructor.newInstance();

			// Läser in data från filen och sparar i objektet
			fetchable.load(reader);
			return fetchable;
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Missing default constructor");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Invalid class in data-file, " + file.getPath());
		} catch (FileNotFoundException e) {
			return null;
		}


	}

	public static void deleteObject(String id, String subPath) throws IOException {
		File file = new File(dataDirectoryPath+"\\"+subPath+"\\"+id+".txt");
		Files.delete(Path.of(file.toURI()));
	}

	/**
	 * Returnerar en lista på id:n för alla lagrade objekt inom en mapp
	 * @param subPath Mappen att söka igenom
	 * @return
	 */
	public static List<String> listObjectIds(String subPath) {
		File directory = new File(dataDirectoryPath+"\\"+subPath);
		return Arrays.stream(directory.list()).map(s -> s.replace(".txt", "")).toList();
	}

	public static List<Fetchable> getAllObjects(String subPath) throws IOException {
		File directory = new File(dataDirectoryPath+"\\"+subPath);
		List<Fetchable> fetchables = new ArrayList<>();
		for (File file : directory.listFiles()) {
			// Använder try-with-resource för att automatiskt stänga läsaren
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {


				// Skapar objekt utifrån klass i datafil
				// Första raden ska följa formatet class: klass-namn
				// Skapar nytt objekt från clazz
				//TODO: Kolla att den innehåller class:
				String className = reader.readLine().split(" ")[1];
				Class clazz = Class.forName(className);
				Constructor constructor = clazz.getConstructor();
				Fetchable fetchable = (Fetchable) constructor.newInstance();

				// Läser in data från filen och sparar i objektet
				fetchable.load(reader);
				fetchables.add(fetchable);
			} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("Missing default constructor");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Invalid class in data-file, " + file.getPath());
			}
		}

		return fetchables;
	}
}
