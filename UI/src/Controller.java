import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
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
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Controller-Klasse. Verarbeitet Events.
 * 
 * @author Lucas Gross-Hardt
 * @category Controller
 */
public class Controller implements ActionListener, ChangeListener, MouseListener {
	/** Log-Fenster */
	public LogView lView;
	/** Liste, die alle erzeugten Zuege enthaelt */
	private ArrayList<Train> listOfTrains = new ArrayList<>();
	/** Anzahl der Zuege, die erstellt werden sollen */
	private int numberOfTrains;
	/** Control panel (Hauptfenster) */
	private ControlPanelView cPanel;
	/** Visualisierungsfenster */
	private TrackView tView;
	/** Konfigurationsfenster */
	private ConfigView config;
	/** Weiche 1 */
	private Switch switch1;
	/** Weiche 2 */
	private Switch switch2;
	/** Weiche 3 */
	private Switch switch3;

	/**
	 * Konstruktor. Erzeugt alle zu erzeugenden Zug-Objekte und fuegt sie der
	 * Liste hinzu. Erstellt dann drei Weichen. Fuegt dem Log initiale Eintraege
	 * hinzu.
	 * 
	 * @category Constructor
	 */
	public Controller(int numberOfTrains) {
		this.numberOfTrains = numberOfTrains;
		switch1 = new Switch(1, this);
		switch2 = new Switch(2, this);
		switch3 = new Switch(3, this);

		for (int i = 0; i < numberOfTrains; i++) {
			String[] input = readDataOnOpen(i);
			if (input[0] != "") {
				listOfTrains.add(new Train(input[0], i, this));
			} else {
				listOfTrains.add(new Train("Zug " + i, i, this));
			}
		}
	}

	/**
	 * Hinterlegt und praepariert das Logfenster, das vom Controller auf dem
	 * neusten Stand gehalten wird. Startet einen neuen LogWriter fuer das Log,
	 * der letzters in regelmaessigen Abstaenden speichert.
	 * 
	 * @param lView
	 *            Logfenster
	 * @category Setter
	 */
	public void setLogView(LogView lView) {
		this.lView = lView;
		prepareLogView();
		new Thread(new LogWriter(this)).start();
	}

	/**
	 * Gibt das Log-Fenster zurueck, das im Controller hinterlegt ist.
	 * 
	 * @return Log-Fenster
	 * @category Getter
	 */
	public LogView getLogView() {
		return lView;
	}

	/**
	 * Hinterlegt und praepariert das Control Panel, dessen Events vom
	 * Controller behandelt werden.
	 * 
	 * @param cPanel
	 *            Kontrollfenster
	 * @category Setter
	 */
	public void setCPanel(ControlPanelView cPanel) {
		this.cPanel = cPanel;
		prepareControlPanel();
	}

	/**
	 * Gibt das Control Panel zurueck, das im Controller hinterlegt ist.
	 * 
	 * @return Kontrollfenster
	 * @category Getter
	 */
	public ControlPanelView getCPanel() {
		return cPanel;
	}

	/**
	 * Hinterlegt die Visualisierung, die der Controller auf dem neusten Stand
	 * haelt.
	 * 
	 * @param tView
	 *            Visualisierungsfenster
	 * @category Setter
	 */
	public void setTrackView(TrackView tView) {
		this.tView = tView;
	}

	/**
	 * Gibt die hinterlegte Visualisierung zurueck.
	 * 
	 * @return Visualisierungsfenster
	 * @category Getter
	 */
	public TrackView getTrackView() {
		return tView;
	}

	/**
	 * Gibt die Liste alle vom Controller erzeugten Zuege zurueck.
	 * 
	 * @return Liste aller Zuege
	 * @category Getter
	 */
	public ArrayList<Train> getListOfTrains() {
		return listOfTrains;
	}

	/**
	 * Setzt einen Geschwindigkeitsslider zurueck. Dabei wird der Wert auf 0
	 * gesetzt, sowie der Tooltip und das darunter befindliche JLabel mit der
	 * neuen Geschwindigkeit aktualisiert.
	 * 
	 * @param i
	 *            Zugnummer
	 * @category Setter
	 */
	private void resetSlider(int i) {
		cPanel.getSliders().get(i).setValue(0);
		cPanel.getSliders().get(i)
				.setToolTipText("Geschwindigkeit: " + cPanel.getSliders().get(i).getValue() + " km/h");
		cPanel.getLabelTempo().get(i)[0].setText(cPanel.getSliders().get(i).getValue() + "km/h");
	}

	/**
	 * Aendert den Zustand der beiden JToggleButtons Vorwaerts und Rueckwaerts
	 * fuer einen Zug. Fuer beide kann angegeben werden, ob sie aktiviert und ob
	 * sie ausgewaehlt sein sollen.
	 * 
	 * @param enBtnForward
	 *            Gibt an, ob der Vorwaertsbutton aktiviert sein soll
	 * @param selBtnForward
	 *            Gibt an, ob der Vorwaertsbutton ausgewaehlt sein soll
	 * @param enBtnBack
	 *            Gibt an, ob der Rueckwaertsbutton aktiviert sein soll
	 * @param selBtnBack
	 *            Gibt an, ob der Rueckwaertsbutton ausgewaehlt sein soll
	 * @param i
	 *            Zugnummer
	 * @category Setter
	 */
	private void setDirectionButtons(boolean enBtnForward, boolean selBtnForward, boolean enBtnBack, boolean selBtnBack,
			int i) {
		cPanel.getTrainToggleButtons().get(i)[0].setEnabled(enBtnForward);
		cPanel.getTrainToggleButtons().get(i)[0].setSelected(selBtnForward);
		cPanel.getTrainToggleButtons().get(i)[2].setEnabled(enBtnBack);
		cPanel.getTrainToggleButtons().get(i)[2].setSelected(selBtnBack);
	}

	/**
	 * Schreibt die Informationen ueber einen Zug in eine Textdatei. Pro Zug
	 * wird eine Textdatei erstellt.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 * @param imagePath
	 *            Bild-Dateipfad
	 * @param trainName
	 *            Name des Zuges
	 * @category Setter
	 */
	public void writeConfigDataToFile(int index, String imagePath, String trainName) {
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
	 * Startet einen neuen Counter- oder Charger-Thread. Counter setzt
	 * Akkuladung iterativ herab. Charger hebt Akkuladung iterativ an.
	 * 
	 * @param index
	 *            Nummer des Zuges, an den der Thread angebunden werden soll.
	 * @param charge
	 *            Parameter, der anzeigt, ob ein Zug aufgeladen werden soll.
	 */
	public void startBatteryWorker(int index, boolean charge) {
		new Thread(new BatteryWorker(index, this, charge)).start();
	}

	/**
	 * Bereitet das Log-Fenster vor. Laedt aeltere Log-Eintraege (wenn
	 * vorhanden) vom Desktop in das neu gestartete Log-Fenster. Fuegt dem Log
	 * initiale Eintraege hinzu.
	 */
	public void prepareLogView() {
		String line;
		try {
			// Daten per BufferedReader aus TextDatei auslesen
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream("C:/Users/Lucas/Desktop/Log.txt")));
			while ((line = br.readLine()) != null) {
				lView.updateLog(line);
			}
		} catch (Exception e) {
		}
		lView.getLogFile();
		lView.updateLog("  \n------ Neue Session -------");
		lView.updateLog("Neues ControlPanel geoeffnet");
		lView.updateLog("Allen Weiche sind standardmaessig nach links ausgerichtet.");
	}

	/**
	 * Fuegt alle benoetigten Action-, Mouse- und Changelistener hinzu.
	 */
	public void prepareControlPanel() {
		for (int i = 0; i < listOfTrains.size(); i++) {
			cPanel.getTrainToggleButtons().get(i)[0].addActionListener(this);
			cPanel.getTrainToggleButtons().get(i)[1].addActionListener(this);
			cPanel.getTrainToggleButtons().get(i)[2].addActionListener(this);
			cPanel.getTrainButtons().get(i)[0].addActionListener(this);
			cPanel.getTrainButtons().get(i)[1].addActionListener(this);
			cPanel.getTrainButtons().get(i)[2].addActionListener(this);
			cPanel.getSliders().get(i).addMouseListener(this);
			cPanel.getSliders().get(i).addChangeListener(this);
			cPanel.getProgressBars().get(i).addChangeListener(this);
		}
		cPanel.getBtnStopOnEmergency().addActionListener(this);
		cPanel.getBtnAddTrain().addActionListener(this);
		for (int i = 0; i < cPanel.getSwitchButtons().length; i++) {
			cPanel.getSwitchButtons()[i].addActionListener(this);
		}
		for (int i = 0; i < cPanel.getMenuItems().length; i++) {
			cPanel.getMenuItems()[i].addActionListener(this);
		}
	}

	/**
	 * Erstellt ein neues Objekt vom Typ ConfigView. Dieses oeffnet ein neues
	 * Konfigurationsfenster fuer einen Zug.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 */
	public void openConfig(int index) {
		cPanel.setFocusableWindowState(false);
		cPanel.setEnabled(false);
		config = new ConfigView(index, this);
		config.getJButtons()[0].addActionListener(this);
		config.getJButtons()[1].addActionListener(this);
	}

	/**
	 * Schliesst das Konfigurationsfenster und speichert Aenderungen.
	 * Aktualisiert Bilder und Namen im Visualisierungsfenster.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 * @param imagePath
	 *            Dateipfad des Bildes, das fuer den Zug eingestellt wurde
	 * @param isBatteryPowered
	 *            Legt fest, ob Zug batteriebetrieben wird
	 * @param trainName
	 *            Name des Zuges
	 * @param oldName
	 *            Alter Zugname fuer Vergleiche
	 */
	public void closeConfig(int index, String imagePath, boolean isBatteryPowered, String trainName, String oldName) {
		boolean oldBatteryMode = listOfTrains.get(index).isBatteryPowered();
		cPanel.setFocusableWindowState(true);
		cPanel.setEnabled(true);
		config.dispose();
		if (!oldBatteryMode && isBatteryPowered)
			startBatteryWorker(index, false);
		listOfTrains.get(index).setName(trainName, oldName);
		tView.getLabelsName().get(index).setText(trainName);
		listOfTrains.get(index).setBatteryMode(isBatteryPowered);
		// Auflade-Button aktivieren oder deaktivieren
		cPanel.getTrainButtons().get(index)[1].setEnabled(isBatteryPowered);
		writeConfigDataToFile(index, imagePath, trainName);
		tView.getLabelsImg().get(index).setIcon(new ImageIcon(
				new ImageIcon(imagePath).getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
	}

	/**
	 * Liest gespeicherte Daten aus einer Textdatei falls vorhanden.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 * @return String, der alle Informationen zum Zug enthaelt
	 * @category Getter
	 */
	public String readDataFromFile(int index) {
		String line;
		String output = "";
		try {
			// Neuer BufferedReader, um Datei auszulesen
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("C:/Users/Lucas/Desktop/SettingsTrain_" + index + ".txt")));
			// Jede Zeile einzeln den den Output-String anhaengen
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
	 * Informationen auf und speichert diese in einem Array. Gibt das
	 * Informationsarray zurueck.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 * @return Array mit Informationen (0 Name, 1 ImagePath)
	 * @category Getter
	 */
	public String[] readDataOnOpen(int index) {
		String[] parts = new String[5];
		try {
			parts = readDataFromFile(index).split(";");
		} catch (Exception e) {
		}
		return parts;
	}

	/**
	 * Oeffnet einen FileChooser und gibt anschliessend den Dateipfad des
	 * ausgewaehlten Bildes zurueck.
	 * 
	 * @param comp
	 *            Komponente, in der der Dialog angezeigt werden soll.
	 * @return String Bild-Dateipfad
	 * @category Getter
	 */
	public String selectImage(Component comp) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Bild auswaehlen..");
		// Filter hinzufueuegen, um nur PNG, JPG und GIF zuzulassen
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
		fc.setFileFilter(filter);
		fc.addChoosableFileFilter(new FileNameExtensionFilter("JPG Images", "jpg"));
		fc.addChoosableFileFilter(new FileNameExtensionFilter("GIF Images", "gif"));
		int returnVal = fc.showDialog(comp, "Auswaehlen..");
		File file = null;

		String imagePath = "";
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			imagePath = file.getPath();
		}
		return imagePath;
	}

	/**
	 * Fuehrt Aktionen fuer Komponenten aus, denen ein ActionListener
	 * hinzugefuegt wurde.
	 * 
	 * @param ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Quelle der Aktion auslesens
		Object s = e.getSource();
		// Durchlaufen der ActionEvents fuer alle Zuege (Aktionen
		// Kontrollfenster)
		for (int i = 0; i < listOfTrains.size(); i++) {
			// Konfiguration oeffnen
			if (s == cPanel.getTrainButtons().get(i)[2])
				openConfig(i);
			// Aufladen
			else if (s == cPanel.getTrainButtons().get(i)[1]) {
				setDirectionButtons(true, false, true, false, i);
				listOfTrains.get(i).setRunning(false);
				listOfTrains.get(i).setTempo(0);
				listOfTrains.get(i).setCharging(true);
				resetSlider(i);
				startBatteryWorker(i, true);

			}
			// Button Vorwaerts
			else if (s == cPanel.getTrainToggleButtons().get(i)[0]) {
				listOfTrains.get(i).setDirection("forward");
				if (!listOfTrains.get(i).isRunning()) {
					listOfTrains.get(i).setRunning(true);
					if (listOfTrains.get(i).getBatteryLifeTime() > 5) {
						startBatteryWorker(i, false);
					}
				}
				setDirectionButtons(false, true, true, false, i);
			}
			// Button Zurueck
			else if (s == cPanel.getTrainToggleButtons().get(i)[2]) {
				if (!listOfTrains.get(i).isRunning()) {
					listOfTrains.get(i).setRunning(true);
					if (listOfTrains.get(i).getBatteryLifeTime() > 5) {
						startBatteryWorker(i, false);
					}
				}
				listOfTrains.get(i).setDirection("backward");
				setDirectionButtons(true, false, false, true, i);
				cPanel.repaint();
			}
			// Button Licht
			else if (s == cPanel.getTrainToggleButtons().get(i)[1]) {
				if (!getListOfTrains().get(i).lightIsOn()) {
					if (!listOfTrains.get(i).isRunning()) {
						listOfTrains.get(i).setRunning(true);
						if (listOfTrains.get(i).getBatteryLifeTime() > 5) {
							startBatteryWorker(i, false);
						}
					}
					cPanel.getTrainToggleButtons().get(i)[1].setSelected(true);
					listOfTrains.get(i).setLightOn(true);

				} else {
					listOfTrains.get(i).setLightOn(false);
					cPanel.getTrainToggleButtons().get(i)[1].setSelected(false);
					if (!cPanel.getTrainToggleButtons().get(i)[0].isSelected()
							&& !cPanel.getTrainToggleButtons().get(i)[2].isSelected())
						listOfTrains.get(i).setRunning(false);
				}
				// Button Stopp
			} else if (s == cPanel.getTrainButtons().get(i)[0]) {
				listOfTrains.get(i).setRunning(false);
				listOfTrains.get(i).setTempo(0);
				listOfTrains.get(i).setLightOn(false);
				resetSlider(i);
				setDirectionButtons(true, false, true, false, i);
			}

			// Buttons Zug hinzufuegen und entfernene
		}

		// Button Nothalt
		if (s == cPanel.getBtnStopOnEmergency() || s == cPanel.getMenuItems()[0]) {
			for (int i = 0; i < getListOfTrains().size(); i++) {
				listOfTrains.get(i).setRunning(false);
				listOfTrains.get(i).setTempo(0);
				resetSlider(i);
				setDirectionButtons(true, false, true, false, i);
				cPanel.getTrainButtons().get(i)[1].setSelected(false);
				listOfTrains.get(i).setLightOn(false);
			}
		}
		// Button Zug hinzufuegen
		else if (s == cPanel.getBtnAddTrain()) {
			if (numberOfTrains < 6) {
				numberOfTrains++;
				int i = numberOfTrains - 1;
				listOfTrains.add(new Train("", i, this));
				if (i == 0)
					tView.getColors().add(Color.BLUE);
				else if (i == 1)
					tView.getColors().add(Color.BLACK);
				else if (i == 2)
					tView.getColors().add(Color.GREEN);
				else if (i == 3)
					tView.getColors().add(Color.RED);
				else if (i == 4)
					tView.getColors().add(Color.GRAY);
				else if (i == 5) {
					tView.getColors().add(Color.YELLOW);
				}

				cPanel.getTrainToggleButtons()
						.add(new JToggleButton[] { new JToggleButton(new ImageIcon(getClass().getResource("up.png"))),
								new JToggleButton(new ImageIcon(getClass().getResource("light.png"))),
								new JToggleButton(new ImageIcon(getClass().getResource("down.png"))) });
				cPanel.getTrainButtons()
						.add(new JButton[] { new JButton(new ImageIcon(getClass().getResource("stop.png"))),
								new JButton(new ImageIcon(getClass().getResource("battery.png"))),
								new JButton(new ImageIcon(getClass().getResource("edit.png"))) });
				cPanel.getSliders().add(new JSlider(SwingConstants.VERTICAL, 0, 200, 0));
				cPanel.getSliders().get(i).setMinorTickSpacing(5);
				cPanel.getSliders().get(i).setMajorTickSpacing(100);
				cPanel.getSliders().get(i).setPaintTicks(true);
				cPanel.getSliders().get(i).setPaintLabels(true);
				cPanel.getSliders().get(i).setSnapToTicks(true);
				cPanel.getLabelTempo()
						.add(new JLabel[] { new JLabel(), new JLabel("<html><p align ='center'>Akku</p></html>") });
				cPanel.getLabelTempo().get(i)[0].setText(
						"<html><p align ='center'>" + cPanel.getSliders().get(i).getValue() + " <br>km/h</p></html>");
				cPanel.getProgressBars().add(new JProgressBar(JProgressBar.HORIZONTAL, 0, 100));
				cPanel.getProgressBars().get(i).setValue(100);
				cPanel.getProgressBars().get(i).setString(100 + " %");
				cPanel.getProgressBars().get(i).setStringPainted(true);
				cPanel.getFillerPanelsTempo().add(new JPanel(new BorderLayout()));
				cPanel.getFillerPanelsTempo().get(i).add(cPanel.getLabelTempo().get(i)[0], BorderLayout.NORTH);
				cPanel.getFillerPanelsTempo().get(i).add(new JLabel("<html><b>Zug " + i + "</html>"),
						BorderLayout.CENTER);
				cPanel.getFillerPanelsBattery().add(new JPanel());
				cPanel.getFillerPanelsBattery().get(i).add(cPanel.getLabelTempo().get(i)[1]);

				cPanel.getTrainToggleButtons().get(i)[0].addActionListener(this);
				cPanel.getTrainToggleButtons().get(i)[1].addActionListener(this);
				cPanel.getTrainToggleButtons().get(i)[2].addActionListener(this);
				cPanel.getTrainButtons().get(i)[0].addActionListener(this);
				cPanel.getTrainButtons().get(i)[1].addActionListener(this);
				cPanel.getTrainButtons().get(i)[2].addActionListener(this);
				cPanel.getSliders().get(i).addMouseListener(this);
				cPanel.getSliders().get(i).addChangeListener(this);
				cPanel.getProgressBars().get(i).addChangeListener(this);

				cPanel.getTrainPanels().add(new JPanel());
				cPanel.getTrainPanels().get(i).setLayout(new GridLayout(5, 2));
				cPanel.getTrainPanels().get(i).add(cPanel.getTrainToggleButtons().get(i)[0]);
				cPanel.getTrainPanels().get(i).add(cPanel.getTrainToggleButtons().get(i)[1]);
				cPanel.getTrainPanels().get(i).add(cPanel.getTrainToggleButtons().get(i)[2]);
				cPanel.getTrainPanels().get(i).add(cPanel.getTrainButtons().get(i)[0]);
				cPanel.getTrainPanels().get(i).add(cPanel.getSliders().get(i));
				cPanel.getTrainPanels().get(i).add(cPanel.getProgressBars().get(i));
				cPanel.getTrainPanels().get(i).add(cPanel.getFillerPanelsTempo().get(i));
				cPanel.getTrainPanels().get(i).add(cPanel.getFillerPanelsBattery().get(i));
				cPanel.getTrainButtons().get(i)[1].setEnabled(false);
				cPanel.getTrainPanels().get(i).add(cPanel.getTrainButtons().get(i)[1]);
				cPanel.getTrainPanels().get(i).add(cPanel.getTrainButtons().get(i)[2]);

				cPanel.getMainPanel().add(cPanel.getTrainPanels().get(i));
				cPanel.getMainPanel().revalidate();
				cPanel.getMainPanel().repaint();

				tView.getListPanelsTrains().add(new JPanel(new GridLayout(1, 3, 5, 5)));
				tView.getLabelsName().add(new JLabel(listOfTrains.get(i).getName() + " "));
				tView.getListPanelsTrains().get(i).add(tView.getLabelsName().get(i));

				tView.getLabelsImg().add(new JLabel(new ImageIcon(new ImageIcon(listOfTrains.get(i).getImagePath())
						.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH))));
				tView.getListPanelsTrains().get(i).add(tView.getLabelsImg().get(i));

				tView.getLabelsColor().add(new JLabel());
				tView.getLabelsColor().get(i).setBackground(tView.getColors().get(i));
				tView.getLabelsColor().get(i).setOpaque(true);
				tView.getListPanelsTrains().get(i).add(tView.getLabelsColor().get(i));
				tView.getPanelRight().add(tView.getListPanelsTrains().get(i));
				tView.getPanelRight().setLayout(new GridLayout(listOfTrains.size(), 1, 5, 5));

				tView.getPanelTrains().add(listOfTrains.get(i));
				new Thread(listOfTrains.get(i)).start();
				tView.revalidate();
				tView.repaint();
			}
			if (numberOfTrains == 6) {
				cPanel.getBtnAddTrain().setEnabled(false);
			}

		}
		// Weichen-Buttons
		else if (s == cPanel.getSwitchButtons()[0] || s == cPanel.getMenuItems()[1]) {
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

		// Aktionen Konfigurationsfenster
		try {
			if (s == config.getJButtons()[1]) {
				config.setImagePath(selectImage(config));
				config.getLabels()[0].setIcon(config.prepareImage());
				config.getPanel().add(config.getLabels()[0]);
				config.getPanel().remove(config.getLabels()[1]);
				config.revalidate();
				config.repaint();
			}

			// Compare old name to new name. Save name if oldName and newName
			// differ
			if (s == config.getJButtons()[0]) {
				if (config.getTextField().getText().equals("")) {
					JOptionPane.showMessageDialog(config, "Der Name muss mindestens ein Zeichen enthalten.");
				} else
					closeConfig(config.getIndex(), config.getImagePath(), config.getCheckBox().isSelected(),
							config.getTextField().getText(), config.getOldName());
			}
		} catch (Exception e5) {

		}

	}

	/**
	 * Mouse clicked fuehrt Aktion fuer solche Komponenten aus, zu denen einen
	 * MouseListener, hinzugefuegt wurde. Diese Aktionen werden ausgefuehrt,
	 * sobald ein Klick beendet wurde.
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	/**
	 * Mouse entered fuehrt Aktion fuer solche Komponenten aus, zu denen einen
	 * MouseListener, hinzugefuegt wurde. Diese Aktionen werden ausgefuehrt,
	 * sobald ein Klick beendet wurde.
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	/**
	 * Mouse exited fuehrt Aktion fuer solche Komponenten aus, zu denen einen
	 * MouseListener, hinzugefuegt wurde. Diese Aktionen werden ausgefuehrt,
	 * sobald der Cursor einen Bereich betreten hat.
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * Mouse pressed fuehrt Aktion fuer solche Komponenten aus, zu denen einen
	 * MouseListener, hinzugefuegt wurde. Diese Aktionen werden ausgefuehrt,
	 * sobald eine Maustaste heruntergedrueckt wird.
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	/**
	 * Mouse released fuehrt Aktion fuer solche Komponenten aus, zu denen einen
	 * MouseListener, hinzugefuegt wurde. Diese Aktionen werden ausgefuehrt,
	 * sobald eine Maustaste losgelassen wird.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		Object s = e.getSource();
		// MouseEvents fuer alle Zuege durchlaufen
		for (int i = 0; i < listOfTrains.size(); i++) {
			if (s == cPanel.getSliders().get(i)) {
				listOfTrains.get(i).setTempo(cPanel.getSliders().get(i).getValue());
				if (!listOfTrains.get(i).isRunning()) {
					listOfTrains.get(i).setRunning(true);
					if (listOfTrains.get(i).getBatteryLifeTime() > 5)
						startBatteryWorker(i, false);
				}
				if (listOfTrains.get(i).getDirection().equals("forward"))
					setDirectionButtons(false, true, true, false, i);
				else
					setDirectionButtons(true, false, false, true, i);
			}
		}
	}

	/**
	 * State Changes wird benoetigt, das ein Changelistener hinzugefuegt wurde.
	 * Die Methode fuehrt Aktionen fuer Objekte aus, denen ein ChangeListener
	 * hinzugefuegt wurde.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object s = e.getSource();
		// ChangeEvents fuer alle Zuege durchlaufen
		for (int i = 0; i < listOfTrains.size(); i++) {
			if (s == cPanel.getSliders().get(i)) {
				cPanel.getSliders().get(i)
						.setToolTipText("Geschwindigkeit: " + cPanel.getSliders().get(i).getValue() + " km/h");
				cPanel.getLabelTempo().get(i)[0].setText(
						"<html><p align ='center'>" + cPanel.getSliders().get(i).getValue() + " <br>km/h</p></html>");
			} else if (s == cPanel.getProgressBars().get(i)) {
				if (cPanel.getProgressBars().get(i).getValue() < 1) {
					resetSlider(i);
					listOfTrains.get(i).setTempo(0);
					listOfTrains.get(i).setRunning(false);
					setDirectionButtons(true, false, true, false, i);
				}
			}
		}
	}
}
