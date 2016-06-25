import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Controller-Klasse. Startet das Programm. Erzeugt Control Panel, Log-Fenster
 * und Visualisierung. Erzeugt alle nötigen Komponenten.
 * 
 * @author Lucas Groß-Hardt
 * @category Controller
 */
public class Controller implements ActionListener, ChangeListener, MouseListener {
	/** Log-Fenster */
	public static LogView lView;
	/** Liste, die alle erzeugten Züge enthält */
	private static ArrayList<Train> listOfTrains = new ArrayList<>();
	/** Anzahl der Züge, die erstellt werden sollen */
	private static final int numberOfTrains = 4;
	/** Control panel (Hauptfenster) */
	private static ControlPanelView cPanel;
	/** Konfigurationsfenster */
	private static ConfigView config;
	/** Weiche 1 */
	private static Switch switch1;
	/** Weiche 2 */
	private static Switch switch2;
	/** Weiche 3 */
	private static Switch switch3;

	private static TrackView tView = new TrackView();

	/**
	 * Gibt das Log-Fenster zurück, das im Controller erzeugt wird.
	 * 
	 * @return Log-Fenster
	 * @category Getter
	 */
	public static LogView getLogView() {
		return lView;
	}

	/**
	 * Gibt das Control Panel zurück, das im Controller erzeugt wird.
	 * 
	 * @return Control panel
	 * @category Getter
	 */
	public static ControlPanelView getCPanel() {
		return cPanel;
	}

	/**
	 * Gibt die Liste alle vom Controller erzeugten Züge zurück.
	 * 
	 * @return Liste aller Züge
	 * @category Getter
	 */
	public static ArrayList<Train> getListOfTrains() {
		return listOfTrains;
	}

	/**
	 * Konstruktor. Erzeugt alle zu erzeugenden Zug-Objekte und fügt sie der
	 * Liste hinzu. Erstellt dann drei Weichen, das Log-Fenster und das Control
	 * Panel. Fügt dem Log initiale Einträge hinzu. Startet den LogWriter, der
	 * das Log in regelmäßigen Abständen speichert. Startet eines neues
	 * TrackView-Fenster, in dem die Züge visualisiert werden.
	 * 
	 * @category Constructor
	 */
	public Controller() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch1 = new Switch(1);
		switch2 = new Switch(2);
		switch3 = new Switch(3);
		showLoadingScreen();

		for (int i = 0; i < numberOfTrains; i++) {
			String[] input = readDataOnOpen(i);
			if (input[0] != "") {
				listOfTrains.add(new Train(input[0], i));
			} else {
				listOfTrains.add(new Train("Zug " + i, i));
			}
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				prepareLogView();
				prepareControlPanel();
				new Thread(new LogWriter()).start();
				new Thread(tView).start();
			}
		});

	}

	private void showLoadingScreen() {
		JWindow loadingScreen = new JWindow();
		JPanel content = new JPanel(new BorderLayout());
		JProgressBar progressBar = new JProgressBar(0, 100);

		content.add(new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("loadingScreenBG.jpg")).getImage()
				.getScaledInstance(300, 200, java.awt.Image.SCALE_SMOOTH))), BorderLayout.CENTER);
		content.add(progressBar, BorderLayout.SOUTH);

		loadingScreen.add(content);
		loadingScreen.pack();
		loadingScreen.setLocationRelativeTo(null);

		progressBar.setStringPainted(true);
		loadingScreen.setVisible(true);
		for (int i = 0; i < 100; i++) {
			progressBar.setValue(i);
			progressBar.setString(i + "%");
			try {
				Thread.sleep(20);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		loadingScreen.dispose();
	}

	/**
	 * Startet einen neuen Counter- oder Charger-Thread. Counter setzt
	 * Akkuladung iterativ herab. Charger hebt Akkuladung iterativ an.
	 * 
	 * @param index
	 *            Nummer des Zuges, an den der Thread angebunden werden soll.
	 * @param charge
	 *            Parameter, der anzeigt, ob ein Zug aufgeladen wird.
	 */
	public void startThread(int index, boolean charge) {
		if (charge)
			new Thread(new Charger(index)).start();
		else {
			listOfTrains.get(index).setCharging(false);
			new Thread(new Counter(index)).start();
		}
	}

	/**
	 * Bereitet das Log-Fenster vor. Lädt ältere Log-Einträge (wenn vorhanden)
	 * vom Desktop in das neu gestartete Log-Fenster.
	 */
	public void prepareLogView() {
		lView = new LogView();
		String line;
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream("C:/Users/Lucas/Desktop/Log.txt")));
			while ((line = br.readLine()) != null) {
				lView.updateLog(line);
			}
		} catch (Exception e) {
		}
		lView.getLogFile();
		lView.updateLog("  \n------ Neue Session -------");
		lView.updateLog("Neues ControlPanel geöffnet");
		lView.updateLog("Allen Weiche sind standardmäßig nach links ausgerichtet.");
		lView.updateLog(Controller.getListOfTrains().get(0).getName() + " wird jetzt gesteuert");
	}

	/**
	 * Fügt alle benötigten Action-, Mouse- und Changelistener hinzu. Erzeugt
	 * dann das Control Panel und übergibt diesem alle nötigen Elemente. Erzeugt
	 * dann eine JMenuBar, ein JMenu und mehrere JMenuItems, die allesamt
	 * unsichtbar hinugefügt werden, um Shortcuts zu realiseren (via
	 * setAcceleratior). Fügt die JMenuBar dem Control Panel hinzu.
	 */
	public void prepareControlPanel() {
		// create control panel
		cPanel = new ControlPanelView(numberOfTrains);
		// add all Listeners
		for (int i = 0; i < listOfTrains.size(); i++) {
			cPanel.getTrainToggleButtons(i)[0].addActionListener(this);
			cPanel.getTrainToggleButtons(i)[1].addActionListener(this);
			cPanel.getTrainToggleButtons(i)[2].addActionListener(this);
			cPanel.getTrainButtons(i)[0].addActionListener(this);
			cPanel.getTrainButtons(i)[1].addActionListener(this);
			cPanel.getTrainButtons(i)[2].addActionListener(this);
			cPanel.getSliders(i).addMouseListener(this);
			cPanel.getSliders(i).addChangeListener(this);
			cPanel.getProgressBars(i).addChangeListener(this);
		}
		cPanel.getBtnStopOnEmergency().addActionListener(this);

		for (int i = 0; i < cPanel.getSwitchButtons().length; i++) {
			cPanel.getSwitchButtons()[i].addActionListener(this);
		}
		for (int i = 0; i < cPanel.getMenuItems().length; i++) {
			cPanel.getMenuItems()[i].addActionListener(this);
		}
	}

	/**
	 * Erstellt ein neues Objekt vom Typ ConfigView. Dieses öffnet ein neues
	 * Konfigurationsfenster für einen beliebigen Zug. Deaktiviert das Control
	 * Panel solange das Konfigurationsfenster offen ist.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 */
	public void openConfig(int index) {
		cPanel.setFocusableWindowState(false);
		cPanel.setEnabled(false);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				config = new ConfigView(index);
			}
		});
	}

	/**
	 * Schließt das Konfigurationsfenster und speichert Änderungen. Reaktiviert
	 * das Control Panel.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 * @param imagePath
	 *            Dateipfad des Bildes, das für den Zug eingestellt wurde
	 * @param trainName
	 *            Name des Zuges
	 * @param oldName
	 *            Alter Zugname für Vergleiche
	 */
	public static void closeConfig(int index, String imagePath, boolean isBatteryPowered,String trainName, String oldName) {
		cPanel.setFocusableWindowState(true);
		cPanel.setEnabled(true);
		config.dispose();
		listOfTrains.get(index).setName(trainName, oldName);
		listOfTrains.get(index).setBatteryMode(isBatteryPowered);
		writeConfigDataToFile(index, imagePath, trainName);
		tView.getLabelsImg()[index].setIcon(new ImageIcon(
				new ImageIcon(imagePath).getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
	}

	/**
	 * Liest gespeicherte Daten aus einer Textdatei falls vorhanden.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 * @return String, der alle Informationen zum Zug enthält
	 * @category Getter
	 */
	public static String readDataFromFile(int index) {
		String line;
		String output = "";
		try {
			// Neuer BufferedReader, um Datei auszulesen
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("C:/Users/Lucas/Desktop/SettingsTrain_" + index + ".txt")));
			// Jede Zeile einzeln den den Output-String anhängen
			while ((line = br.readLine()) != null) {
				output += line + ";";
			}
		} catch (Exception e) {
		}
		return output;
	}

	/**
	 * Ruft readDataFromFile auf und liest somit Informationen aus einer
	 * Textdatei aus, falls vorhanden. Teilt den Output-String in wichtige
	 * Informationen auf un speichert diese in einem Array. Gibt das
	 * Informationsarray zurück
	 * 
	 * @param index
	 *            Nummer des Zuges
	 * @return Array mit Informationen (0 Name, 1 ImagePath)
	 * @category Getter
	 */
	public static String[] readDataOnOpen(int index) {
		String[] parts = new String[5];
		try {
			parts = readDataFromFile(index).split(";");
		} catch (Exception e) {
		}
		return parts;
	}

	/**
	 * Öffnet einen FileChooser und gibt anschließend den Dateipfad des
	 * ausgewählten Bildes zurück.
	 * 
	 * @param comp
	 *            Komponente, in der der Dialog angezeigt werden soll.
	 * @return String Bild-Dateipfad
	 * @category Getter
	 */
	public static String selectImage(Component comp) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Bild auswählen..");
		// Filter hinzufüügen, um nur PNG, JPG und GIF zuzulassen
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
		fc.setFileFilter(filter);
		fc.addChoosableFileFilter(new FileNameExtensionFilter("JPG Images", "jpg"));
		fc.addChoosableFileFilter(new FileNameExtensionFilter("GIF Images", "gif"));
		int returnVal = fc.showDialog(comp, "Auswählen..");
		File file = null;

		String imagePath = "";
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			imagePath = file.getPath();
		}
		return imagePath;
	}

	/**
	 * Schreibt die Informationen über einen Zug in eine Textdatei. Pro Zug wird
	 * eine Textdatei erstellt.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 * @param imagePath
	 *            Bild-Dateipfad
	 * @param trainName
	 *            Name des Zuges
	 * @category Setter
	 */
	public static void writeConfigDataToFile(int index, String imagePath, String trainName) {
		// Es wird pro Index, also pro Zug, eine Texdatei erstellt.
		Path path = Paths.get("C:/Users/Lucas/Desktop/SettingsTrain_" + index + ".txt");
		try {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		} catch (FileAlreadyExistsException e) {
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			PrintWriter writer = new PrintWriter("C:/Users/Lucas/Desktop/SettingsTrain_" + index + ".txt");
			writer.println(trainName);
			writer.println(imagePath);
			writer.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

	}

	/**
	 * Main-Methode. Erstellt einen neuen Controller. Dies startet das Programm.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Controller controller = new Controller();
	}

	/**
	 * Setzt den Geschwindigkeitsslider zurück. Dabei wird der Wert auf 0
	 * gesetzt, sowie der Tooltip und das darunter befindliche JLabel mit der
	 * neuen Geschwindigkeit aktualisiert.
	 * 
	 * @category Setter
	 */
	private void resetSlider(int i) {
		cPanel.getSliders(i).setValue(0);
		cPanel.getSliders(i).setToolTipText("Geschwindigkeit: " + cPanel.getSliders(i).getValue() + " km/h");
		cPanel.getLabelTempo(0).setText(cPanel.getSliders(i).getValue() + "km/h");
	}

	/**
	 * Führt Aktionen für Komponenten aus, denen ein ActionListener hinzugefügt
	 * wurde. Diese Komponenten beinhalten sowohl JButtons, als auch
	 * JToggleButtons und JMenuItems.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Quelle der Aktion auslesens
		Object s = e.getSource();
		for (int i = 0; i < listOfTrains.size(); i++) {
			// Konfiguration öffnen
			if (s == cPanel.getTrainButtons(i)[2])
				openConfig(i);
			// Aufladen
			else if (s == cPanel.getTrainButtons(i)[1]) {
				setDirectionButtons(true, false, true, false, i);
				listOfTrains.get(i).setRunning(false);
				listOfTrains.get(i).setTempo(0);
				listOfTrains.get(i).setCharging(true);
				resetSlider(i);
				startThread(i, true);

			}
			// Button Vorwärts
			else if (s == cPanel.getTrainToggleButtons(i)[0]) {
				listOfTrains.get(i).setDirection("forward");
				if (!listOfTrains.get(i).isRunning()) {
					listOfTrains.get(i).setRunning(true);
					if (listOfTrains.get(i).getBatteryLifeTime() > 5) {
						startThread(i, false);
					}
				}
				setDirectionButtons(false, true, true, false, i);
			}
			// Button Zurück
			else if (s == cPanel.getTrainToggleButtons(i)[2]) {
				if (!listOfTrains.get(i).isRunning()) {
					listOfTrains.get(i).setRunning(true);
					if (listOfTrains.get(i).getBatteryLifeTime() > 5) {
						startThread(i, false);
					}
				}
				listOfTrains.get(i).setDirection("backward");
				setDirectionButtons(true, false, false, true, i);
				cPanel.repaint();
			}
			// Button Licht
			else if (s == cPanel.getTrainToggleButtons(i)[1]) {
				if (!Controller.getListOfTrains().get(i).lightIsOn()) {
					if (!listOfTrains.get(i).isRunning()) {
						listOfTrains.get(i).setRunning(true);
						if (listOfTrains.get(i).getBatteryLifeTime() > 5) {
							startThread(i, false);
						}
					}
					cPanel.getTrainToggleButtons(i)[1].setSelected(true);
					listOfTrains.get(i).setLightOn(true);

				} else {
					listOfTrains.get(i).setLightOn(false);
					cPanel.getTrainToggleButtons(i)[1].setSelected(false);
					if (!cPanel.getTrainToggleButtons(i)[0].isSelected()
							&& !cPanel.getTrainToggleButtons(i)[2].isSelected())
						listOfTrains.get(i).setRunning(false);
				}
				// Button Stopp
			} else if (s == cPanel.getTrainButtons(i)[0]) {
				listOfTrains.get(i).setRunning(false);
				listOfTrains.get(i).setTempo(0);
				listOfTrains.get(i).setLightOn(false);
				resetSlider(i);
				setDirectionButtons(true, false, true, false, i);
			}
		}
		if (s == cPanel.getBtnStopOnEmergency() || s == cPanel.getMenuItems()[0]) {
			for (int i = 0; i < getListOfTrains().size(); i++) {
				listOfTrains.get(i).setRunning(false);
				listOfTrains.get(i).setTempo(0);
				resetSlider(i);
				setDirectionButtons(true, false, true, false, i);
				cPanel.getTrainButtons(i)[1].setSelected(false);
				listOfTrains.get(i).setLightOn(false);
			}
		} else if (s == cPanel.getSwitchButtons()[0] || s == cPanel.getMenuItems()[1]) {
			cPanel.getSwitchButtons()[0].setEnabled(false);
			cPanel.getSwitchButtons()[1].setEnabled(true);
			cPanel.getSwitchButtons()[1].setSelected(false);
			cPanel.getSwitchButtons()[0].setSelected(true);
			switch1.setAlignment(true);
		} else if (s == cPanel.getSwitchButtons()[1] || s == cPanel.getMenuItems()[2]) {
			cPanel.getSwitchButtons()[1].setEnabled(false);
			cPanel.getSwitchButtons()[0].setEnabled(true);
			cPanel.getSwitchButtons()[0].setSelected(false);
			cPanel.getSwitchButtons()[1].setSelected(true);
			switch1.setAlignment(false);
		} else if (s == cPanel.getSwitchButtons()[2] || s == cPanel.getMenuItems()[3]) {
			cPanel.getSwitchButtons()[2].setEnabled(false);
			cPanel.getSwitchButtons()[3].setEnabled(true);
			cPanel.getSwitchButtons()[3].setSelected(false);
			cPanel.getSwitchButtons()[2].setSelected(true);
			switch2.setAlignment(true);
		} else if (s == cPanel.getSwitchButtons()[3] || s == cPanel.getMenuItems()[4]) {
			cPanel.getSwitchButtons()[3].setEnabled(false);
			cPanel.getSwitchButtons()[2].setEnabled(true);
			cPanel.getSwitchButtons()[2].setSelected(false);
			cPanel.getSwitchButtons()[3].setSelected(true);
			switch2.setAlignment(false);
		} else if (s == cPanel.getSwitchButtons()[4] || s == cPanel.getMenuItems()[5]) {
			cPanel.getSwitchButtons()[4].setEnabled(false);
			cPanel.getSwitchButtons()[5].setEnabled(true);
			cPanel.getSwitchButtons()[5].setSelected(false);
			cPanel.getSwitchButtons()[4].setSelected(true);
			switch3.setAlignment(true);
		} else if (s == cPanel.getSwitchButtons()[5] || s == cPanel.getMenuItems()[6]) {
			cPanel.getSwitchButtons()[5].setEnabled(false);
			cPanel.getSwitchButtons()[4].setEnabled(true);
			cPanel.getSwitchButtons()[4].setSelected(false);
			cPanel.getSwitchButtons()[5].setSelected(true);
			switch3.setAlignment(false);
		}
	}

	/**
	 * Ändert den Zustand der beiden JToggleButtons btnForward und btnBack. Für
	 * beide kann angegeben werden, ob sie aktiviert und ob sie ausgewählt sein
	 * sollen.
	 * 
	 * @param enBtnForward
	 *            Gibt an, ob der btnForward aktiviert sein soll
	 * @param selBtnForward
	 *            Gibt an, ob der btnForward ausgewählt sein soll
	 * @param enBtnBack
	 *            Gibt an, ob der btnBack aktiviert sein soll
	 * @param selBtnBack
	 *            Gibt an, ob der btnBack ausgewählt sein soll
	 * @category Setter
	 */
	private void setDirectionButtons(boolean enBtnForward, boolean selBtnForward, boolean enBtnBack, boolean selBtnBack,
			int i) {
		cPanel.getTrainToggleButtons(i)[0].setEnabled(enBtnForward);
		cPanel.getTrainToggleButtons(i)[0].setSelected(selBtnForward);
		cPanel.getTrainToggleButtons(i)[2].setEnabled(enBtnBack);
		cPanel.getTrainToggleButtons(i)[2].setSelected(selBtnBack);
	}

	/**
	 * Mouse-Clicked führt Aktion für solche Komponenten aus, zu denen einen
	 * MouseListener, hinzugefügt wurde. Diese Aktionen werden ausgeführt,
	 * sobald ein Klick beendet wurde.
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	/**
	 * Mouse-Clicked führt Aktion für solche Komponenten aus, zu denen einen
	 * MouseListener, hinzugefügt wurde. Diese Aktionen werden ausgeführt,
	 * sobald ein Klick beendet wurde.
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	/**
	 * Mouse-Clicked führt Aktion für solche Komponenten aus, zu denen einen
	 * MouseListener, hinzugefügt wurde. Diese Aktionen werden ausgeführt,
	 * sobald der Cursor einen Bereich betreten hat.
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * Mouse-Clicked führt Aktion für solche Komponenten aus, zu denen einen
	 * MouseListener, hinzugefügt wurde. Diese Aktionen werden ausgeführt,
	 * sobald eine Maustaste heruntergedrückt wird.
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	/**
	 * Mouse-Clicked führt Aktion für solche Komponenten aus, zu denen einen
	 * MouseListener, hinzugefügt wurde. Diese Aktionen werden ausgeführt,
	 * sobald eine Maustaste losgelassen wird.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		Object s = e.getSource();

		for (int i = 0; i < listOfTrains.size(); i++) {
			if (s == cPanel.getSliders(i)) {
				listOfTrains.get(i).setTempo(cPanel.getSliders(i).getValue());
				if (!listOfTrains.get(i).isRunning()) {
					listOfTrains.get(i).setRunning(true);
					if (listOfTrains.get(i).getBatteryLifeTime() > 5)
						startThread(i, false);
				}
				if (listOfTrains.get(i).getDirection().equals("forward"))
					setDirectionButtons(false, true, true, false, i);
				else
					setDirectionButtons(true, false, false, true, i);
			}
		}
	}

	/**
	 * State Changes wird benötigt, da ein Changelistener hinzugefügt wurde. Die
	 * Methode führt Aktionen für Objekte aus, denen ein ChangeListener
	 * hinzugefügt wurde.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object s = e.getSource();
		for (int i = 0; i < listOfTrains.size(); i++) {
			if (s == cPanel.getSliders(i)) {
				cPanel.getSliders(i).setToolTipText("Geschwindigkeit: " + cPanel.getSliders(i).getValue() + " km/h");
				cPanel.getLabelTempo(i).setText(
						"<html><p align ='center'>" + cPanel.getSliders(i).getValue() + " <br>km/h</p></html>");
			} else if (s == cPanel.getProgressBars(i)) {
				if (cPanel.getProgressBars(i).getValue() < 1) {
					resetSlider(i);
					listOfTrains.get(i).setTempo(0);
					listOfTrains.get(i).setRunning(false);
					setDirectionButtons(true, false, true, false, i);
				}
			}

		}
	}

}
