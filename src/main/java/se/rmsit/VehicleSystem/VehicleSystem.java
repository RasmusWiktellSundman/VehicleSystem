package se.rmsit.VehicleSystem;

import se.rmsit.VehicleSystem.views.MainFrame;

public class VehicleSystem {
	public static void main(String[] args) {
		FileHandler.createDataFolders();
		System.out.println(Configuration.getProperty("data_directory"));

		// Startar GUI
		new MainFrame("VehicleSystem");
	}
}
