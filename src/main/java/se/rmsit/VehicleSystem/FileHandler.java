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
		File vehiclesDataFolder = new File(dataDirectoryPath + "/" + folder);
		if (!vehiclesDataFolder.exists())
			vehiclesDataFolder.mkdirs();
	}

	public static void storeObject(Fetchable object, String subPath) throws IOException {
		File file = new File(dataDirectoryPath+"/"+subPath+"/"+object.getId()+".txt");
		if(!file.exists())
			file.createNewFile();
		// Använder try-with-resource för att automatiskt stänga läsaren
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
			writer.println("class: " + object.getClass().getName());
			// Serialiserar och sparar det serialiserade objektet i filen
			String serialized = object.serialize();
			writer.write(serialized);
		}
	}

	public static void storeObject(Fetchable object) throws IOException {
		storeObject(object, "");
	}

	public static void appendObjectToFile(Fetchable object, String fileName) throws IOException {
		boolean fileExisted = true;
		File file = new File(dataDirectoryPath+"/"+fileName+".txt");
		// Skapar ny fil ifall den inte redan finns
		if(!file.exists()) {
			file.createNewFile();
			fileExisted = false;
		}

		// Använder try-with-resource för att automatiskt stänga läsaren
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			// Sparar klassen som används, sparas högst upp i filen då alla objekt är av samma typ
			if(!fileExisted) {
				writer.println("class: " + object.getClass().getName());
			} else {
				// Skriver objektseparerare till filen, endast mellan objekt, det vill säga inte direkt efter class:
				writer.write("\n----\n");
			}

			// Serialiserar och sparar det serialiserade objektet i filen
			String serialized = object.serialize();
			writer.write(serialized);
		}
	}

	public static Fetchable loadObject(Fetchable fetchable, String id, String subPath) throws IOException {
		File file = new File(dataDirectoryPath+"/"+subPath+"/"+id+".txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			// Läser in data från filen och sparar i objektet
			loadOneObjectFromReader(fetchable, reader);
			return fetchable;
		// Filen innehåller endast ett objekt, är därför irrelevant att filen nått slutet.
		} catch (FileNotFoundException ignored) {}
		return null;
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
		File file = new File(dataDirectoryPath+"/"+subPath+"/"+id+".txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			// Skapar tom klass som implementerar Fetchable. Klasstypen är given i filen.
			Fetchable fetchable = createEmptyFetchable(reader);

			// Läser in data från filen och sparar i objektet
			loadOneObjectFromReader(fetchable, reader);
			return fetchable;
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static void deleteObject(String id, String subPath) throws IOException {
		File file = new File(dataDirectoryPath+"/"+subPath+"/"+id+".txt");
		Files.delete(Path.of(file.toURI()));
	}

	/**
	 * Returnerar en lista på id:n för alla lagrade objekt inom en mapp
	 * @param subPath Mappen att söka igenom
	 * @return
	 */
	public static List<String> listObjectIds(String subPath) {
		File directory = new File(dataDirectoryPath+"/"+subPath);
		return Arrays.stream(directory.list()).map(s -> s.replace(".txt", "")).toList();
	}

	public static List<Fetchable> getAllObjects(String subPath) throws IOException {
		File directory = new File(dataDirectoryPath+"/"+subPath);
		List<Fetchable> fetchables = new ArrayList<>();
		for (File file : directory.listFiles()) {
			// Använder try-with-resource för att automatiskt stänga läsaren
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				// Skapar tom klass som implementerar Fetchable. Klasstypen är given i filen.
				Fetchable fetchable = createEmptyFetchable(reader);

				// Läser in data från filen och sparar i objektet
				loadOneObjectFromReader(fetchable, reader);
				fetchables.add(fetchable);
			}
		}

		return fetchables;
	}

	public static List<Fetchable> getAllObjectsFromOneFile(String fileName) throws IOException {
		File dataFile = new File(dataDirectoryPath+"/"+fileName+".txt");
		List<Fetchable> fetchables = new ArrayList<>();

		// Kollar så filen finns, returnerar tom lista ifall den inte finns
		if(!dataFile.exists())
			return fetchables;

		// Använder try-with-resource för att automatiskt stänga läsaren
		try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
			// TODO: Kolla om filen innehåller class:
			// Hämtar klasstyp för alla klasser som ska laddas in
			String className = reader.readLine().split("class: ")[1];

			// Upprepar tills alla objekt har lästs in.
			while(true) {
				// Skapar tom klass som implementerar Fetchable.
				Fetchable fetchable = createEmptyFetchable(className);

				// Läser in data från filen och sparar i objektet, metoden läser endast in till nästa ----, det vill säga ett objekt
				// Metoden returnerar false när alla objekt är inlästa
				if(!loadOneObjectFromReader(fetchable, reader)) {
					fetchables.add(fetchable);
					break;
				}
				fetchables.add(fetchable);
			}
		}

		return fetchables;
	}

	/**
	 *
	 * @param fetchable
	 * @param reader Läsaren att hämta data ifrån
	 * @return True ifall filen innehåller fler objekt, annars false
	 * @throws IOException
	 */
	private static boolean loadOneObjectFromReader(Fetchable fetchable, BufferedReader reader) throws IOException {
		// Skapar objekt från reader data (samma som store metoden)
		while (true) {
			String line = reader.readLine();
			// Om filen innehåller flera objekt separeras de av "----"
			if(line == null) {
				return false;
			} else if(line.equals("----")) {
				return true;
			}
			// Delar upp raden i nyckel-data par
			String[] tokens = line.split(": ");

			String data;
			if(tokens.length == 1) {
				// Tom string sparad
				data = null;
			} else {
				data = tokens[1];
				if(data.equals("null")) {
					data = null;
				}
			}
			fetchable.loadData(tokens[0], data);
		}
	}

	/**
	 * Skapar ett Fetchable objekt med den specifika klassen som står på första raden reader läser in.
	 * Klassens standardkonstruktor används för att skapa objektet
	 * @param reader
	 * @return En klass som implementerar Fetchable
	 * @throws IOException
	 */
	private static Fetchable createEmptyFetchable(BufferedReader reader) throws IOException {
		//TODO: Kolla att den innehåller class:
		String line = reader.readLine();
		if(line == null) {
			return null;
		}
		String className = line.split(" ")[1];
		return createEmptyFetchable(className);
	}

	private static Fetchable createEmptyFetchable(String className) throws IOException {
		try {
			Constructor constructor = Class.forName(className).getConstructor();
			return (Fetchable) constructor.newInstance();
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Missing default constructor");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Invalid class in data-file");
		}
	}
}
