import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
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

	/** Menüitem zur Shortcut-Anbindung (Einen Zug stoppen) */
	private static JMenuItem itemStop = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Alle Züge stoppen) */
	private static JMenuItem itemStopAll = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Vorwärts fahren) */
	private static JMenuItem itemForward = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Rückwärts fahren) */
	private static JMenuItem itemBack = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Konfiguration öffnen) */
	private static JMenuItem itemOpenConfig = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Licht an/aus) */
	private static JMenuItem itemToggleLight = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Aufladen) */
	private static JMenuItem itemCharge = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Weiche 1 links) */
	private static JMenuItem itemSwitch1Left = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Weiche 1 rechts) */
	private static JMenuItem itemSwitch1Right = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Weiche 2 links) */
	private static JMenuItem itemSwitch2Left = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Weiche 2 rechts) */
	private static JMenuItem itemSwitch2Right = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Weiche 3 links) */
	private static JMenuItem itemSwitch3Left = new JMenuItem();
	/** Menüitem zur Shortcut-Anbindung (Weiche 3 rechts) */
	private static JMenuItem itemSwitch3Right = new JMenuItem();
	
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
		for (int i = 0; i < numberOfTrains; i++) {
			String[] input = readDataOnOpen(i);
			if (input[0] != "") {
				listOfTrains.add(new Train(input[0], i));
			} else {
				listOfTrains.add(new Train("Zug " + i, i));
			}
		}
		switch1 = new Switch(1);
		switch2 = new Switch(2);
		switch3 = new Switch(3);
		lView = new LogView();

		prepareLogView();
		prepareControlPanel();

		lView.updateLog("  \n------ Neue Session -------");
		lView.updateLog("Neues ControlPanel geöffnet");
		lView.updateLog("Allen Weiche sind standardmäßig nach links ausgerichtet.");
		lView.updateLog(Controller.getListOfTrains().get(cPanel.getTrainSelection().getSelectedIndex()).getName()
				+ " wird jetzt gesteuert");
		new Thread(new LogWriter()).start();
		new Thread(tView).start();
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
		cPanel.getSteeringButtons()[0].addActionListener(this);
		cPanel.getSteeringButtons()[1].addActionListener(this);
		cPanel.getSteeringButtons()[2].addActionListener(this);
		cPanel.getSwitchButtons()[0].addActionListener(this);
		cPanel.getSwitchButtons()[1].addActionListener(this);
		cPanel.getSwitchButtons()[2].addActionListener(this);
		cPanel.getSwitchButtons()[3].addActionListener(this);
		cPanel.getSwitchButtons()[4].addActionListener(this);
		cPanel.getSwitchButtons()[5].addActionListener(this);
		cPanel.getJButtons()[0].addActionListener(this);
		cPanel.getJButtons()[1].addActionListener(this);
		cPanel.getJButtons()[2].addActionListener(this);
		cPanel.getJButtons()[3].addActionListener(this);
		cPanel.getJButtons()[4].addActionListener(this);
		cPanel.getJButtons()[5].addActionListener(this);
		cPanel.getSliders()[0].addMouseListener(this);
		cPanel.getSliders()[0].addChangeListener(this);
		cPanel.getSliders()[1].addChangeListener(this);

		JMenuBar listenerBar = new JMenuBar();
		JMenu listenerMenu = new JMenu();

		itemBack.addActionListener(this);
		itemBack.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.CTRL_MASK));
		listenerMenu.add(itemBack);
		itemForward.addActionListener(this);
		itemForward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.CTRL_MASK));
		listenerMenu.add(itemForward);
		itemStop.addActionListener(this);
		itemStop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		listenerMenu.add(itemStop);
		itemStopAll.addActionListener(this);
		itemStopAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, Event.CTRL_MASK));
		listenerMenu.add(itemStopAll);
		itemOpenConfig.addActionListener(this);
		itemOpenConfig.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		listenerMenu.add(itemOpenConfig);
		itemToggleLight.addActionListener(this);
		itemToggleLight.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
		listenerMenu.add(itemToggleLight);
		itemCharge.addActionListener(this);
		itemCharge.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
		listenerMenu.add(itemCharge);
		itemSwitch1Left.addActionListener(this);
		itemSwitch1Left.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK));
		listenerMenu.add(itemSwitch1Left);
		itemSwitch1Right.addActionListener(this);
		itemSwitch1Right.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.SHIFT_MASK));
		listenerMenu.add(itemSwitch1Right);
		itemSwitch2Left.addActionListener(this);
		itemSwitch2Left.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.CTRL_MASK));
		listenerMenu.add(itemSwitch2Left);
		itemSwitch2Right.addActionListener(this);
		itemSwitch2Right.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.SHIFT_MASK));
		listenerMenu.add(itemSwitch2Right);
		itemSwitch3Left.addActionListener(this);
		itemSwitch3Left.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, Event.CTRL_MASK));
		listenerMenu.add(itemSwitch3Left);
		itemSwitch3Right.addActionListener(this);
		itemSwitch3Right.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, Event.SHIFT_MASK));
		listenerMenu.add(itemSwitch3Right);

		listenerBar.add(listenerMenu);

		cPanel.getPanelSettings().add(listenerBar);
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
		config = new ConfigView(index);
		cPanel.getJButtons()[3].setEnabled(false);
		cPanel.repaint();
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
	public static void closeConfig(int index, String imagePath, String trainName, String oldName) {
		cPanel.setFocusableWindowState(true);
		cPanel.setEnabled(true);
		config.dispose();
		cPanel.getJButtons()[3].setEnabled(true);
		listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setName(trainName, oldName);
		writeConfigDataToFile(index, imagePath, trainName);
		tView.getLabelsImg()[index].setIcon(new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)));
	}

	/**
	 * Updated die Zugauswahlliste mit den aktuellen Zugnamen.
	 * 
	 * @category Setter
	 */
	public static void setTextListOfTrains() {
		String[] data = new String[listOfTrains.size()];
		for (int i = 0; i < listOfTrains.size(); i++) {
			data[i] = listOfTrains.get(i).getName();
		}
		// Speichert den Ausgewählten Eintrag, um diesen danach
		// wiederherzustellen
		int z = cPanel.getTrainSelection().getSelectedIndex();
		cPanel.getTrainSelection().setListData(data);
		cPanel.getTrainSelection().setSelectedIndex(z);
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
	private void resetSlider() {
		cPanel.getSliders()[0].setValue(0);
		cPanel.getSliders()[0].setToolTipText("Geschwindigkeit: " + cPanel.getSliders()[0].getValue() + " km/h");
		cPanel.getLabelTempo()
				.setText("<html><p align ='center'>" + cPanel.getSliders()[0].getValue() + " <br>km/h</p></html>");
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
		if (s == cPanel.getJButtons()[3] || s == itemOpenConfig)
			openConfig(cPanel.getTrainSelection().getSelectedIndex());

		else if (s == cPanel.getJButtons()[2] || s == itemCharge) {
			setDirectionButtons(true, false, true, false);
			listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setRunning(false);
			listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setTempo(0);
			listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setCharging(true);
			resetSlider();
			startThread(cPanel.getTrainSelection().getSelectedIndex(), true);
		}

		else if (s == cPanel.getJButtons()[5] || s == itemStopAll) {
			for (int i = 0; i < getListOfTrains().size(); i++) {
				listOfTrains.get(i).setRunning(false);
				listOfTrains.get(i).setTempo(0);
				resetSlider();
				cPanel.getSteeringButtons()[2].setSelected(false);
				setDirectionButtons(true, false, true, false);
				listOfTrains.get(i).setLightOn(false);
			}
		}

		else if (s == cPanel.getJButtons()[0]) {
			cPanel.getJButtons()[0].setEnabled(false);
			cPanel.getTrainSelection().setEnabled(true);
			cPanel.getJButtons()[1].setEnabled(true);
			cPanel.getJButtons()[3].setEnabled(false);

			cPanel.getSteeringButtons()[0].setEnabled(false);
			cPanel.getSteeringButtons()[1].setEnabled(false);
			cPanel.getSliders()[0].setEnabled(false);
			cPanel.getSteeringButtons()[2].setEnabled(false);
			cPanel.getSwitchButtons()[0].setEnabled(false);
			cPanel.getSwitchButtons()[1].setEnabled(false);
			cPanel.getSwitchButtons()[2].setEnabled(false);
			cPanel.getSwitchButtons()[3].setEnabled(false);
			cPanel.getSwitchButtons()[4].setEnabled(false);
			cPanel.getSwitchButtons()[5].setEnabled(false);
		} else if (s == cPanel.getJButtons()[1]) {
			lView.updateLog(Controller.getListOfTrains().get(cPanel.getTrainSelection().getSelectedIndex()).getName()
					+ " wird jetzt gesteuert");
			cPanel.getJButtons()[0].setEnabled(true);
			cPanel.getTrainSelection().setEnabled(false);
			cPanel.getJButtons()[1].setEnabled(false);

			cPanel.getJButtons()[3].setEnabled(true);

			cPanel.getSteeringButtons()[2].setEnabled(true);
			if (listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).lightIsOn())
				cPanel.getSteeringButtons()[2].setSelected(true);
			else
				cPanel.getSteeringButtons()[2].setSelected(false);

			cPanel.getSliders()[0].setEnabled(true);
			cPanel.getSliders()[0].setValue(listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).getTempo());
			cPanel.getSliders()[0].setToolTipText("Geschwindigkeit: " + cPanel.getSliders()[0].getValue() + " km/h");
			cPanel.getLabelTempo()
					.setText("<html><p align ='center'>" + cPanel.getSliders()[0].getValue() + " <br>km/h</p></html>");

			cPanel.getSliders()[1]
					.setValue(listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).getBatteryLifeTime());

			if (cPanel.getSwitchButtons()[0].isSelected())
				cPanel.getSwitchButtons()[1].setEnabled(true);
			else
				cPanel.getSwitchButtons()[0].setEnabled(true);
			if (cPanel.getSwitchButtons()[2].isSelected())
				cPanel.getSwitchButtons()[3].setEnabled(true);
			else
				cPanel.getSwitchButtons()[2].setEnabled(true);
			if (cPanel.getSwitchButtons()[4].isSelected())
				cPanel.getSwitchButtons()[5].setEnabled(true);
			else
				cPanel.getSwitchButtons()[4].setEnabled(true);

			if (listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).getDirection().equals("forward")) {
				if (listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).isRunning())
					setDirectionButtons(false, true, true, false);
				else
					setDirectionButtons(true, false, true, false);
			}
			if (listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).getDirection().equals("backward")) {
				if (listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).isRunning())
					setDirectionButtons(true, false, false, true);
				else
					setDirectionButtons(true, false, true, false);
			}
		}

		else if (s == cPanel.getSwitchButtons()[0] || s == itemSwitch1Left) {
			cPanel.getSwitchButtons()[0].setEnabled(false);
			cPanel.getSwitchButtons()[1].setEnabled(true);
			cPanel.getSwitchButtons()[1].setSelected(false);
			cPanel.getSwitchButtons()[0].setSelected(true);
			switch1.setAlignment(true);
		} else if (s == cPanel.getSwitchButtons()[1] || s == itemSwitch1Right) {
			cPanel.getSwitchButtons()[1].setEnabled(false);
			cPanel.getSwitchButtons()[0].setEnabled(true);
			cPanel.getSwitchButtons()[0].setSelected(false);
			cPanel.getSwitchButtons()[1].setSelected(true);
			switch1.setAlignment(false);
		} else if (s == cPanel.getSwitchButtons()[2] || s == itemSwitch2Left) {
			cPanel.getSwitchButtons()[2].setEnabled(false);
			cPanel.getSwitchButtons()[3].setEnabled(true);
			cPanel.getSwitchButtons()[3].setSelected(false);
			cPanel.getSwitchButtons()[2].setSelected(true);
			switch2.setAlignment(true);
		} else if (s == cPanel.getSwitchButtons()[3] || s == itemSwitch2Right) {
			cPanel.getSwitchButtons()[3].setEnabled(false);
			cPanel.getSwitchButtons()[2].setEnabled(true);
			cPanel.getSwitchButtons()[2].setSelected(false);
			cPanel.getSwitchButtons()[3].setSelected(true);
			switch2.setAlignment(false);
		} else if (s == cPanel.getSwitchButtons()[4] || s == itemSwitch3Left) {
			cPanel.getSwitchButtons()[4].setEnabled(false);
			cPanel.getSwitchButtons()[5].setEnabled(true);
			cPanel.getSwitchButtons()[5].setSelected(false);
			cPanel.getSwitchButtons()[4].setSelected(true);
			switch3.setAlignment(true);
		} else if (s == cPanel.getSwitchButtons()[5] || s == itemSwitch3Right) {
			cPanel.getSwitchButtons()[5].setEnabled(false);
			cPanel.getSwitchButtons()[4].setEnabled(true);
			cPanel.getSwitchButtons()[4].setSelected(false);
			cPanel.getSwitchButtons()[5].setSelected(true);
			switch3.setAlignment(false);
		}

		else if (s == cPanel.getSteeringButtons()[0] || s == itemForward) {
			listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setDirection("forward");
			if (!listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).isRunning()) {
				listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setRunning(true);
				if (listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).getBatteryLifeTime() > 5) {
					startThread(cPanel.getTrainSelection().getSelectedIndex(), false);
				}
			}
			setDirectionButtons(false, true, true, false);
		}

		else if (s == cPanel.getSteeringButtons()[1] || s == itemBack) {
			if (!listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).isRunning()) {
				listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setRunning(true);
				if (listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).getBatteryLifeTime() > 5) {
					startThread(cPanel.getTrainSelection().getSelectedIndex(), false);
				}
			}
			listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setDirection("backward");
			setDirectionButtons(true, false, false, true);
		}

		else if (s == cPanel.getSteeringButtons()[2] || s == itemToggleLight) {
			if (!Controller.getListOfTrains().get(cPanel.getTrainSelection().getSelectedIndex()).lightIsOn()) {
				if (!listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).isRunning()) {
					listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setRunning(true);
					if (listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).getBatteryLifeTime() > 5) {
						startThread(cPanel.getTrainSelection().getSelectedIndex(), false);
					}
				}
				cPanel.getSteeringButtons()[2].setSelected(true);
				listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setLightOn(true);

			} else {
				listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setLightOn(false);
				cPanel.getSteeringButtons()[2].setSelected(false);
				if (!cPanel.getSteeringButtons()[0].isSelected() && !cPanel.getSteeringButtons()[1].isSelected())
					listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setRunning(false);
			}
		} else if (s == cPanel.getJButtons()[4] || s == itemStop) {
			listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setRunning(false);
			listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setTempo(0);
			listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setLightOn(false);
			resetSlider();
			setDirectionButtons(true, false, true, false);
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
	private void setDirectionButtons(boolean enBtnForward, boolean selBtnForward, boolean enBtnBack,
			boolean selBtnBack) {
		cPanel.getSteeringButtons()[0].setEnabled(enBtnForward);
		cPanel.getSteeringButtons()[0].setSelected(selBtnForward);
		cPanel.getSteeringButtons()[1].setEnabled(enBtnBack);
		cPanel.getSteeringButtons()[1].setSelected(selBtnBack);
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

		if (s == cPanel.getSliders()[0] && cPanel.getSliders()[0].isEnabled()) {
			listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setTempo(cPanel.getSliders()[0].getValue());
			if (!listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).isRunning()) {
				listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setRunning(true);
				if (listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).getBatteryLifeTime() > 5)
					startThread(cPanel.getTrainSelection().getSelectedIndex(), false);
			}
			if (listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).getDirection().equals("forward"))
				setDirectionButtons(false, true, true, false);
			else
				setDirectionButtons(true, false, false, true);
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
		if (s == cPanel.getSliders()[0]) {
			cPanel.getSliders()[0].setToolTipText("Geschwindigkeit: " + cPanel.getSliders()[0].getValue() + " km/h");
			cPanel.getLabelTempo()
					.setText("<html><p align ='center'>" + cPanel.getSliders()[0].getValue() + " <br>km/h</p></html>");
		} else if (s == cPanel.getSliders()[1]) {
			if (cPanel.getSliders()[1].getValue() == 0) {
				resetSlider();
				listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setTempo(0);
				listOfTrains.get(cPanel.getTrainSelection().getSelectedIndex()).setRunning(false);
				setDirectionButtons(true, false, true, false);
			}
		}

	}

}
