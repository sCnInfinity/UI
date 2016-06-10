import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * Diese Klasse wird als anzeigbares Control Panel für die Züge verwendet.
 * 
 * @author Lucas Groß-Hardt
 * @category View
 */
public class ControlPanelView extends JFrame {
	/** Button, der die Zugauswahl aktviert */
	private JButton btnActivateTrainSelection = new JButton(new ImageIcon(getClass().getResource("edit.png")));
	/** Button, der Änderungen in der Zugauswahl übernimmt */
	private JButton btnFinishTrainSelection = new JButton(new ImageIcon(getClass().getResource("accept.png")));
	/** Button der ein neues Konfigurationsfenster öffnet */
	private JButton btnOpenConfig = new JButton("Konfig");
	/** Button, der den ausgewählten Zug anhält */
	private JButton btnStopTrain = new JButton(new ImageIcon(getClass().getResource("stop.png")));
	/** Button, der sofort alle Aktionen aller Züge stoppt */
	private JButton btnStopOnEmergency = new JButton(new ImageIcon(getClass().getResource("emergency.png")));
	/** Button, der den Aufladevorgang für einen Zug startet */
	private JButton btnCharge = new JButton(new ImageIcon(getClass().getResource("battery.png")));
	/** Slider, der die Geschwindigkeit einstellt */
	private JSlider sldTempo = new JSlider(SwingConstants.VERTICAL, 0, 200, 0);
	/** Slider, der die Batterielaufzeit eines Zuges anzeigt */
	private static JSlider sldBattery = new JSlider(SwingConstants.VERTICAL, 0, 100, 100);
	/** Button, der den Zug vorwärts fahren lässt */
	private JToggleButton btnForward = new JToggleButton(new ImageIcon(getClass().getResource("up.png")));
	/** Button, der den Zug rückwärts fahren lässt */
	private JToggleButton btnBack = new JToggleButton(new ImageIcon(getClass().getResource("down.png")));
	/** Button, der das Licht eines Zuges ein- oder ausschaltet */
	private JToggleButton btnLight = new JToggleButton(new ImageIcon(getClass().getResource("light.png")));
	/** Button, der Weiche 1 nach links ausrichtet */
	private JToggleButton btnSwitch1Left = new JToggleButton(new ImageIcon(getClass().getResource("left.png")));
	/** Button, der Weiche 1 nach rechts ausrichtet */
	private JToggleButton btnSwitch1Right = new JToggleButton(new ImageIcon(getClass().getResource("right.png")));
	/** Button, der Weiche 2 nach links ausrichtet */
	private JToggleButton btnSwitch2Left = new JToggleButton(new ImageIcon(getClass().getResource("left.png")));
	/** Button, der Weiche 2 nach rechts ausrichtet */
	private JToggleButton btnSwitch2Right = new JToggleButton(new ImageIcon(getClass().getResource("right.png")));
	/** Button, der Weiche 3 nach links ausrichtet */
	private JToggleButton btnSwitch3Left = new JToggleButton(new ImageIcon(getClass().getResource("left.png")));
	/** Button, der Weiche 3 nach rechts ausrichtet */
	private JToggleButton btnSwitch3Right = new JToggleButton(new ImageIcon(getClass().getResource("right.png")));
	/** List to choose a train to control */
	private JList trainSelection;
	/** Right Panel. For battery, train control and emergency button */
	private JPanel panelTrainSettings;
	/** Left Panel. For train selection, configuration and switch alignment. */
	private JPanel panelTrainSelection;
	/** Contentpane. Contains panelTrainSettings and panelTrainSelection */
	private JPanel panel;
	/** Label for switch 1 */
	private JLabel lblSwitch1 = new JLabel("Weiche 1");
	/** Label for switch 2 */
	private JLabel lblSwitch2 = new JLabel("Weiche 2");
	/** Label for switch 3 */
	private JLabel lblSwitch3 = new JLabel("Weiche 3");
	/** Label für den Geschwindigkeitsslider */
	private JLabel lblTempo = new JLabel("");
	/** Label for the battery slider */
	private JLabel lblBattery = new JLabel("<html><p> <br>Akku</p></html>");
	/** Number of trains to be created */
	private int numberOfTrains;
	/** String array to store the names of all trains created */
	private String[] trains;

	/**
	 * Konstruktor. Übernimmt übergebene Komponenten und baut das Fenster über
	 * einen Aufruf von buildWindow auf.
	 * 
	 * @param numberOfTrains
	 *            Anzahl der Züge, die erstellt werden sollen
	 * @category Constructor
	 */
	public ControlPanelView(int numberOfTrains) {
		this.numberOfTrains = numberOfTrains;
		this.trains = new String[this.numberOfTrains];
		buildWindow();
	}

	/**
	 * Baut das Fenster des Control Panels auf.
	 */
	private void buildWindow() {
		// Zugnamen auslesen und zur Auswahlliste hinzufügen
		for (int i = 0; i < numberOfTrains; i++) {
			trains[i] = Controller.getListOfTrains().get(i).getName();
		}
		trainSelection = new JList(trains);

		// Panels für die Komponentenanordnung erstellen
		panel = new JPanel();
		panelTrainSelection = new JPanel();
		panelTrainSettings = new JPanel();

		// Linkes Panel: Layout und Elemente hinzufügen
		panelTrainSelection.setLayout(null);
		panelTrainSelection.add(btnFinishTrainSelection);
		panelTrainSelection.add(btnActivateTrainSelection);
		panelTrainSelection.add(trainSelection);
		panelTrainSelection.add(btnOpenConfig);
		panelTrainSelection.add(btnSwitch1Left);
		panelTrainSelection.add(btnSwitch1Right);
		panelTrainSelection.add(btnSwitch2Left);
		panelTrainSelection.add(btnSwitch2Right);
		panelTrainSelection.add(btnSwitch3Left);
		panelTrainSelection.add(btnSwitch3Right);
		panelTrainSelection.add(lblSwitch1);
		panelTrainSelection.add(lblSwitch2);
		panelTrainSelection.add(lblSwitch3);

		// Rechtes Panel: Layout und Elemente hinzufügen
		panelTrainSettings.setLayout(null);
		panelTrainSettings.add(btnForward);
		panelTrainSettings.add(btnBack);
		panelTrainSettings.add(sldTempo);
		panelTrainSettings.add(sldBattery);
		panelTrainSettings.add(btnLight);
		panelTrainSettings.add(lblTempo);
		panelTrainSettings.add(lblBattery);
		panelTrainSettings.add(btnStopOnEmergency);
		panelTrainSettings.add(btnCharge);
		panelTrainSettings.add(btnStopTrain);

		// Content Pane: Layout und Linkes, Rechtes Panel hinzufügen
		panel.setLayout(new GridLayout(1, 3));
		panel.add(panelTrainSelection, BorderLayout.WEST);
		panel.add(panelTrainSettings, BorderLayout.EAST);

		// Frame Einstellungen
		setResizable(false);
		setSize(500, 400);
		setTitle("Control-Panel");
		// Fenster in der Mitte des Bildschirms anzeigen
		setLocationRelativeTo(null);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		setContentPane(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// Komponenten-Positionen und Größen setzen
		// Texte und Verhalten anpassen, falls erforderlich
		sldTempo.setBounds(panelTrainSettings.getWidth() - 90, 10, 30, 150);
		sldTempo.setToolTipText("Geschwindigkeit: " + sldTempo.getValue() + " km/h");
		lblTempo.setText("<html><p align ='center'>" + sldTempo.getValue() + " <br>km/h</p></html>");
		lblTempo.setBounds(sldTempo.getX(), sldTempo.getY() + sldTempo.getHeight() + 10, 40, 30);

		sldBattery.setBounds(sldTempo.getX() + sldTempo.getWidth() + 20, sldTempo.getY(), sldTempo.getWidth(),
				sldTempo.getHeight());
		sldBattery.setEnabled(false);
		lblBattery.setBounds(sldBattery.getX(), sldBattery.getY() + sldBattery.getHeight() + 10, 40, 30);

		btnForward.setBounds((int) Math.round(panelTrainSettings.getWidth() - 100 - 70), sldTempo.getY() + 10, 70, 70);

		btnBack.setBounds((int) Math.round(panelTrainSettings.getWidth() - 100 - 70),
				sldTempo.getY() + 10 + btnForward.getHeight() + 10, 70, 70);

		btnLight.setBounds((int) Math.round(panelTrainSettings.getWidth() - 100 - 70),
				btnBack.getY() + btnBack.getHeight() + 10, 70, 70);

		// Listengröße an Nummer der Züge anpassen
		trainSelection.setVisibleRowCount(4);
		trainSelection.setFont(new Font(trainSelection.getFont().getName(), Font.BOLD, 14));
		trainSelection.setBounds(10, 10, 50,
				(int) Math.round(trainSelection.getPreferredScrollableViewportSize().getHeight()));
		trainSelection.setSelectedIndex(0);
		trainSelection.setEnabled(false);

		btnActivateTrainSelection.setBounds(trainSelection.getX() + trainSelection.getWidth() + 10,
				trainSelection.getY(), 32, 32);

		btnFinishTrainSelection.setBounds(trainSelection.getX() + trainSelection.getWidth() + 10,
				trainSelection.getY() + btnActivateTrainSelection.getHeight() + 10, 32, 32);
		btnFinishTrainSelection.setEnabled(false);

		btnOpenConfig.setBounds(trainSelection.getX(), trainSelection.getY() + trainSelection.getHeight() + 10,
				(int) btnOpenConfig.getPreferredSize().getWidth(), 25);

		lblSwitch1.setBounds(btnOpenConfig.getX(), btnOpenConfig.getY() + btnOpenConfig.getHeight() + 20, 120, 32);
		btnSwitch1Left.setBounds(lblSwitch1.getX(), lblSwitch1.getY() + lblSwitch1.getHeight() + 2, 60, 20);
		btnSwitch1Right.setBounds(btnSwitch1Left.getX() + btnSwitch1Left.getWidth() + 2, btnSwitch1Left.getY(),
				btnSwitch1Left.getWidth(), btnSwitch1Left.getHeight());

		lblSwitch2.setBounds(lblSwitch1.getX(), btnSwitch1Left.getY() + btnSwitch1Left.getHeight() + 10,
				lblSwitch1.getWidth(), lblSwitch1.getHeight());
		btnSwitch2Left.setBounds(lblSwitch2.getX(), lblSwitch2.getY() + lblSwitch2.getHeight() + 2,
				btnSwitch1Left.getWidth(), btnSwitch1Left.getHeight());
		btnSwitch2Right.setBounds(btnSwitch2Left.getX() + btnSwitch2Left.getWidth() + 2, btnSwitch2Left.getY(),
				btnSwitch2Left.getWidth(), btnSwitch2Left.getHeight());

		lblSwitch3.setBounds(lblSwitch1.getX(), btnSwitch2Left.getY() + btnSwitch2Left.getHeight() + 10,
				lblSwitch1.getWidth(), lblSwitch1.getHeight());
		btnSwitch3Left.setBounds(lblSwitch3.getX(), lblSwitch3.getY() + lblSwitch3.getHeight() + 2,
				btnSwitch2Left.getWidth(), btnSwitch2Left.getHeight());
		btnSwitch3Right.setBounds(btnSwitch3Left.getX() + btnSwitch3Left.getWidth() + 2, btnSwitch3Left.getY(),
				btnSwitch3Left.getWidth(), btnSwitch3Left.getHeight());

		btnSwitch1Left.setEnabled(false);
		btnSwitch1Left.setSelected(true);
		btnSwitch2Left.setEnabled(false);
		btnSwitch2Left.setSelected(true);
		btnSwitch3Left.setEnabled(false);
		btnSwitch3Left.setSelected(true);

		btnStopOnEmergency.setBounds(btnLight.getX(), btnLight.getY() + btnLight.getHeight() + 10, 70, 70);
		btnStopOnEmergency.setToolTipText("Alle Züge anhalten");

		btnStopTrain.setBounds(btnForward.getX() - 10 - 40, btnForward.getY(), 40, 40);

		btnCharge.setBounds(lblBattery.getX() - 5, btnLight.getY() + btnLight.getHeight() - 40, 40, 40);
		// repaint();
	}

	/**
	 * Gibt das Rechte Panel des Control Panel-Fensters zurück.
	 * 
	 * @return Rechtes Panel
	 */
	public JPanel getPanelSettings() {
		return panelTrainSettings;
	}

	public JToggleButton[] getSwitchButtons() {
		JToggleButton[] output = { btnSwitch1Left, btnSwitch1Right, btnSwitch2Left, btnSwitch2Right, btnSwitch3Left,
				btnSwitch3Right };
		return output;
	}

	public JToggleButton[] getSteeringButtons() {
		JToggleButton[] output = { btnForward, btnBack, btnLight };
		return output;
	}

	public JButton[] getJButtons() {
		JButton[] output = { btnActivateTrainSelection, btnFinishTrainSelection, btnCharge, btnOpenConfig, btnStopTrain,
				btnStopOnEmergency };
		return output;
	}

	public JSlider[] getSliders() {
		JSlider[] output = { sldTempo, sldBattery };
		return output;
	}

	public JLabel getLabelTempo() {
		return lblTempo;
	}

	/**
	 * Gibt die Zugwauswahlliste zurück.
	 * 
	 * @return Zugauswahlliste
	 * @category Getter
	 */
	public JList getTrainSelection() {
		return trainSelection;
	}
}
