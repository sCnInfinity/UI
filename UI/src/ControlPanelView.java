import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * Diese Klasse wird als anzeigbares Control Panel für die Züge verwendet.
 * 
 * @author Lucas Groß-Hardt
 * @category View
 */
public class ControlPanelView extends JFrame {
	/** Button, der den ausgewählten Zug anhält */
	private JButton btnStopOnEmergency = new JButton(new ImageIcon(getClass().getResource("emergency.png")));
	/** Button, der den Aufladevorgang für einen Zug startet */
	private JButton btnCharge = new JButton(new ImageIcon(getClass().getResource("battery.png")));
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
	/** Right Panel. For battery, train control and emergency button */
	private JPanel panelTrains;
	/** Left Panel. For train selection, configuration and switch alignment. */
	private JPanel panelTrainLeft;
	/** Contentpane. Contains panelTrainSettings and panelTrainSelection */
	private JPanel panel;
	/** Label for switch 1 */
	private JLabel lblSwitch1 = new JLabel("Weiche 1");
	/** Label for switch 2 */
	private JLabel lblSwitch2 = new JLabel("Weiche 2");
	/** Label for switch 3 */
	private JLabel lblSwitch3 = new JLabel("Weiche 3");

	/** Menüitem zur Shortcut-Anbindung (Alle Züge stoppen) */
	private static JMenuItem itemStopAll = new JMenuItem();
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

	/** Number of trains to be created */
	private int numberOfTrains;
	/** String array to store the names of all trains created */
	private String[] trains;
	private static ArrayList<JPanel> trainPanels = new ArrayList<>();
	private static ArrayList<JToggleButton[]> trainToggleButtons = new ArrayList<>();
	private static ArrayList<JButton[]> trainButtons = new ArrayList<>();
	private static ArrayList<JSlider> trainSliders = new ArrayList<>();
	private static ArrayList<JLabel[]> trainLabels = new ArrayList<>();
	private static ArrayList<JProgressBar> trainProgressBars = new ArrayList<>();

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
		// Panels für die Komponentenanordnung erstellen
		panel = new JPanel();
		panelTrainLeft = new JPanel();
		panelTrains = new JPanel();
		JPanel panelSelection = new JPanel();
		JPanel panelSwitches = new JPanel();

		// Linkes Panel: Layout und Elemente hinzufügen

		panelSelection.setLayout(new GridLayout());
		panelSelection.add(btnStopOnEmergency);

		panelSwitches.setLayout(new GridLayout(6, 2, 5, 5));
		panelSwitches.add(lblSwitch1);
		panelSwitches.add(new JPanel());
		panelSwitches.add(btnSwitch1Left);
		panelSwitches.add(btnSwitch1Right);
		panelSwitches.add(lblSwitch2);
		panelSwitches.add(new JPanel());
		panelSwitches.add(btnSwitch2Left);
		panelSwitches.add(btnSwitch2Right);
		panelSwitches.add(lblSwitch3);
		panelSwitches.add(new JPanel());
		panelSwitches.add(btnSwitch3Left);
		panelSwitches.add(btnSwitch3Right);

		panelTrainLeft.setLayout(new GridLayout(3, 1));
		panelTrainLeft.add(panelSelection);
		panelTrainLeft.add(new JPanel());
		panelTrainLeft.add(panelSwitches);

		for (int i = 0; i < numberOfTrains; i++) {
			trainToggleButtons
					.add(new JToggleButton[] { new JToggleButton(new ImageIcon(getClass().getResource("up.png"))),
							new JToggleButton(new ImageIcon(getClass().getResource("light.png"))),
							new JToggleButton(new ImageIcon(getClass().getResource("down.png"))) });
			trainButtons.add(new JButton[] { new JButton(new ImageIcon(getClass().getResource("stop.png"))),
					new JButton(new ImageIcon(getClass().getResource("battery.png"))),
					new JButton(new ImageIcon(getClass().getResource("edit.png"))) });
			trainSliders.add(new JSlider(SwingConstants.VERTICAL, 0, 200, 0));
			trainLabels.add(new JLabel[] { new JLabel(), new JLabel("Akku") });
			trainLabels.get(i)[0].setText(trainSliders.get(i).getValue() + "km/h");
			trainProgressBars.add(new JProgressBar(JProgressBar.VERTICAL, 0, 100));
			trainProgressBars.get(i).setValue(100);
		}

		panel.add(panelTrainLeft);
		// Rechtes Panel: Layout und Elemente hinzufügen
		for (int i = 0; i < numberOfTrains; i++) {
			trainPanels.add(new JPanel());
			trainPanels.get(i).setLayout(new GridLayout(5, 2));
			trainPanels.get(i).add(trainToggleButtons.get(i)[0]);
			trainPanels.get(i).add(trainToggleButtons.get(i)[1]);
			trainPanels.get(i).add(trainToggleButtons.get(i)[2]);
			trainPanels.get(i).add(trainButtons.get(i)[0]);
			trainPanels.get(i).add(trainSliders.get(i));
			trainPanels.get(i).add(trainProgressBars.get(i));
//			trainPanels.get(i).add(trainSliders.get(i)[1]);
//			trainSliders.get(i)[1].setEnabled(false);
			trainPanels.get(i).add(trainLabels.get(i)[0]);
			trainPanels.get(i).add(trainLabels.get(i)[1]);
			trainPanels.get(i).add(trainButtons.get(i)[1]);
			trainPanels.get(i).add(trainButtons.get(i)[2]);
			panel.add(trainPanels.get(i));
		}
		panel.setLayout(new GridLayout(1, numberOfTrains, 5, 5));

		JMenuBar listenerBar = new JMenuBar();
		JMenu listenerMenu = new JMenu();

		getMenuItems()[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		listenerMenu.add(getMenuItems()[0]);
		getMenuItems()[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK));
		listenerMenu.add(getMenuItems()[1]);
		getMenuItems()[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.SHIFT_MASK));
		listenerMenu.add(getMenuItems()[2]);
		getMenuItems()[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.CTRL_MASK));
		listenerMenu.add(getMenuItems()[3]);
		getMenuItems()[4].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.SHIFT_MASK));
		listenerMenu.add(getMenuItems()[4]);
		getMenuItems()[5].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, Event.CTRL_MASK));
		listenerMenu.add(getMenuItems()[5]);
		getMenuItems()[6].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, Event.SHIFT_MASK));
		listenerMenu.add(getMenuItems()[6]);

		listenerBar.add(listenerMenu);

		// Frame Einstellungen
		setSize(700, 400);
		setTitle("Control-Panel");
		setMinimumSize(new Dimension(700, 400));
		// Fenster in der Mitte des Bildschirms anzeigen
		// setLocationRelativeTo(null);
		setLocation(600, 300);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		setContentPane(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setJMenuBar(listenerBar);
		setVisible(true);

		btnSwitch1Left.setEnabled(false);
		btnSwitch1Left.setSelected(true);
		btnSwitch2Left.setEnabled(false);
		btnSwitch2Left.setSelected(true);
		btnSwitch3Left.setEnabled(false);
		btnSwitch3Left.setSelected(true);

		btnStopOnEmergency.setToolTipText("Alle Züge anhalten");
	}

	/**
	 * Gibt das Rechte Panel des Control Panel-Fensters zurück.
	 * 
	 * @return Rechtes Panel
	 */
	public JPanel getPanelSwitches() {
		return panelTrainLeft;
	}

	public JToggleButton[] getSwitchButtons() {
		JToggleButton[] output = { btnSwitch1Left, btnSwitch1Right, btnSwitch2Left, btnSwitch2Right, btnSwitch3Left,
				btnSwitch3Right };
		return output;
	}

	public JToggleButton[] getTrainToggleButtons(int i) {
		return trainToggleButtons.get(i);
	}

	public JButton[] getTrainButtons(int i) {
		return trainButtons.get(i);
	}

	public JProgressBar getProgressBars(int i){
		return trainProgressBars.get(i);
	}
	
	public JSlider getSliders(int i) {
		return trainSliders.get(i);
	}

	public JLabel getLabelTempo(int i) {
		return trainLabels.get(i)[0];
	}

	public JMenuItem[] getMenuItems() {
		return new JMenuItem[] { itemStopAll, itemSwitch1Left, itemSwitch1Right, itemSwitch2Left, itemSwitch2Right,
				itemSwitch3Left, itemSwitch3Right };
	}

	public JButton getBtnStopOnEmergency() {
		return btnStopOnEmergency;
	}

}
