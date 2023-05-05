package se.rmsit.VehicleSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestHelper {
	public static void resetDataFiles() throws IOException {
		// Remove test data directory
		File dataFolder = new File(Configuration.getProperty("data_directory"));
		if(dataFolder.exists()) {
			for (File directoriesAndFiles : dataFolder.listFiles()) {
				if (directoriesAndFiles.isDirectory()) {
					for (File file : directoriesAndFiles.listFiles()) {
						Files.delete(Path.of(file.toURI()));
					}
				} else {
					Files.delete(Path.of(directoriesAndFiles.toURI()));
				}
			}
		}
		FileHandler.createDataFolders();
	}
}
