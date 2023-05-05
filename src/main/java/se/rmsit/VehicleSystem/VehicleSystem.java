package se.rmsit.VehicleSystem;

public class VehicleSystem {
	public static void main(String[] args) {
		FileHandler.createDataFolders();
		System.out.println(Configuration.getProperty("data_directory"));
	}
}
