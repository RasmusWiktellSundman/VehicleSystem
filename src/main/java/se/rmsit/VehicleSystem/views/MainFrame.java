package se.rmsit.VehicleSystem.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
	private JButton registerUserBtn;
	private JButton button2;
	private JPanel content;
	private final CardLayout cardLayout = new CardLayout();
	private JPanel mainPanel;

	public MainFrame(String title) {
		// Initiera JFrame
		super(title);
		// Sätt innehållet för JFrame till mainframe (.form filen)
		setContentPane(mainPanel);
		// Ändrar JFrames standard storlek
		setSize(600, 300);
		// center window
		setLocationRelativeTo(null);
		// Stänger av programmet när användaren stänger programmet. Annars kör den i bakgrunden.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Visar JFramen
		setVisible(true);

		// Sätter Panel till att vara CardLayout
		content.setLayout(cardLayout);

		// Lägger till paneler till innehållspanelen
		content.add(new RegisterUserPanel().getRegisterUser(), "register_user");
		content.add(new Testing3().getPanel(), "testing");

		// Aktiverar lyssnare (knappar)
		registerListeners();
	}

	private void registerListeners() {
		registerUserBtn.addActionListener(e -> {
			System.out.println("Test");
			cardLayout.show(content, "register_user");
		});
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(content, "testing");
			}
		});
	}


}
