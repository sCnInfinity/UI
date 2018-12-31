import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.LayoutManager;
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

/**
 * Diese Klasse implementiert die Anzeige des Control Panels.
 * 
 * @author Lucas Gross-Hardt
 * @category View
 */
public class ControlPanelView extends JFrame {
	/** Button, der den ausgewaehlten Zug anhaelt */
	private JButton btnStopOnEmergency = new JButton(new ImageIcon(getClass().getResource("emergency.png")));
	/** Button, der den Aufladevorgang fuer einen Zug startet */
	private JButton btnCharge = new JButton(new ImageIcon(getClass().getResource("battery.png")));
	/** Button, der einen Zug hinzufuegt */
	private JButton btnAddTrain = new JButton(new ImageIcon(getClass().getResource("plus.png")));
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
	/** Rechtes Panel. Beinhaltet alle Zugpanel */
	private JPanel panelTrains;
	/**
	 * Linkes Panel. Beinhaltet Weicheneinstellungen, Zugaddition und Nothalt
	 */
	private JPanel panelTrainLeft;
	/** Contentpane. Beinhaltet panelTrains und panelTrainLeft. */
	private JPanel panel;
	/** Label Weiche 1 */
	private JLabel lblSwitch1 = new JLabel("Weiche 1");
	/** Label Weiche 2 */
	private JLabel lblSwitch2 = new JLabel("Weiche 2");
	/** Label Weiche 3 */
	private JLabel lblSwitch3 = new JLabel("Weiche 3");

	/** Menueitem zur Shortcut-Anbindung (Alle Zuege stoppen) */
	private JMenuItem itemStopAll = new JMenuItem();
	/** Menueitem zur Shortcut-Anbindung (Weiche 1 links) */
	private JMenuItem itemSwitch1Left = new JMenuItem();
	/** Menueitem zur Shortcut-Anbindung (Weiche 1 rechts) */
	private JMenuItem itemSwitch1Right = new JMenuItem();
	/** Menueitem zur Shortcut-Anbindung (Weiche 2 links) */
	private JMenuItem itemSwitch2Left = new JMenuItem();
	/** Menueitem zur Shortcut-Anbindung (Weiche 2 rechts) */
	private JMenuItem itemSwitch2Right = new JMenuItem();
	/** Menueitem zur Shortcut-Anbindung (Weiche 3 links) */
	private JMenuItem itemSwitch3Left = new JMenuItem();
	/** Menueitem zur Shortcut-Anbindung (Weiche 3 rechts) */
	private JMenuItem itemSwitch3Right = new JMenuItem();

	/** Anzahl der bestehenden Zuege */
	private int numberOfTrains;

	/** Liste mit allen Zugpanels */
	private ArrayList<JPanel> trainPanels = new ArrayList<>();
	/** Liste mit allen Tempo-Fuellerpanels */
	private ArrayList<JPanel> fillerPanelsTempo = new ArrayList<>();
	/** Liste mit allen Akku-Fuellerpanels */
	private ArrayList<JPanel> fillerPanelsBattery = new ArrayList<>();
	/** Liste mit allen Arrays von JToggleButtons fuer Zuege */
	private ArrayList<JToggleButton[]> trainToggleButtons = new ArrayList<>();
	/** Liste mit allen Arrays von JButtons fuer Zuege */
	private ArrayList<JButton[]> trainButtons = new ArrayList<>();
	/** Liste mit allen JSliders fuer Zuege */
	private ArrayList<JSlider> trainSliders = new ArrayList<>();
	/** Liste mit allen Arrays von JLabels fuer Zuege */
	private ArrayList<JLabel[]> trainLabels = new ArrayList<>();
	/** Liste mit allen JProgressBars fuer Zuege */
	private ArrayList<JProgressBar> trainProgressBars = new ArrayList<>();

	/**
	 * Konstruktor. Uebernimmt die Anzahl der Zuege und baut das Fenster ueber
	 * einen Aufruf von buildWindow auf.
	 * 
	 * @param numberOfTrains
	 *            Anzahl der Zuege, die erstellt werden sollen
	 * @category Constructor
	 */
	public ControlPanelView(int numberOfTrains) {
		this.numberOfTrains = numberOfTrains;
		buildWindow();
	}

	/**
	 * Baut das Fenster des Control Panels auf.
	 */
	private void buildWindow() {
		// Panels fuer die Komponentenanordnung erstellen
		panel = new JPanel();
		panelTrainLeft = new JPanel();
		panelTrains = new JPanel();
		JPanel panelSelection = new JPanel();
		JPanel panelSwitches = new JPanel();

		// Linkes Panel: Layout und Elemente hinzufuegen
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
		panelTrainLeft.add(btnAddTrain);
		panelTrainLeft.add(panelSwitches);

		for (int i = 0; i < numberOfTrains; i++) {
			trainToggleButtons
					.add(new JToggleButton[] { new JToggleButton(new ImageIcon(getClass().getResource("up.png"))),
							new JToggleButton(new ImageIcon(getClass().getResource("light.png"))),
							new JToggleButton(new ImageIcon(getClass().getResource("down.png"))) });
			trainButtons.add(new JButton[] { new JButton(new ImageIcon(getClass().getResource("stop.png"))),
					new JButton(new ImageIcon(getClass().getResource("battery.png"))),
					new JButton(new ImageIcon(getClass().getResource("edit.png"))),
					new JButton(new ImageIcon(getClass().getResource("trash.png")))});
			trainSliders.add(new JSlider(SwingConstants.VERTICAL, 0, 200, 0));
			trainSliders.get(i).setMinorTickSpacing(5);
			trainSliders.get(i).setMajorTickSpacing(100);
			trainSliders.get(i).setPaintTicks(true);
			trainSliders.get(i).setPaintLabels(true);
			trainSliders.get(i).setSnapToTicks(true);
			trainLabels.add(new JLabel[] { new JLabel(), new JLabel("<html><p align ='center'>Akku</p></html>") });
			trainLabels.get(i)[0]
					.setText("<html><p align ='center'>" + trainSliders.get(i).getValue() + " <br>km/h</p></html>");
			trainProgressBars.add(new JProgressBar(JProgressBar.HORIZONTAL, 0, Constants.MAXBATTERYLIFE));
			trainProgressBars.get(i).setValue(Constants.MAXBATTERYLIFE);
			trainProgressBars.get(i).setString(Constants.MAXBATTERYLIFE / Constants.BATTERYDIVISOR + " %");
			trainProgressBars.get(i).setStringPainted(true);
			fillerPanelsTempo.add(new JPanel(new BorderLayout()));
			fillerPanelsTempo.get(i).add(trainLabels.get(i)[0], BorderLayout.NORTH);
			fillerPanelsTempo.get(i).add(new JLabel("<html><b>Zug " + i + "</html>"), BorderLayout.CENTER);
			fillerPanelsBattery.add(new JPanel());
			fillerPanelsBattery.get(i).add(trainLabels.get(i)[1]);
			fillerPanelsBattery.get(i).add(trainButtons.get(i)[3], BorderLayout.EAST);
		}

		panel.add(panelTrainLeft);
		// Rechtes Panel: Layout und Elemente hinzufuegen
		for (int i = 0; i < numberOfTrains; i++) {
			trainPanels.add(new JPanel());
			trainPanels.get(i).setLayout(new GridLayout(5, 2));
			trainPanels.get(i).add(trainToggleButtons.get(i)[0]);
			trainPanels.get(i).add(trainToggleButtons.get(i)[1]);
			trainPanels.get(i).add(trainToggleButtons.get(i)[2]);
			trainPanels.get(i).add(trainButtons.get(i)[0]);
			trainPanels.get(i).add(trainSliders.get(i));
			trainPanels.get(i).add(trainProgressBars.get(i));
			trainPanels.get(i).add(fillerPanelsTempo.get(i));
			trainPanels.get(i).add(fillerPanelsBattery.get(i));
			trainButtons.get(i)[1].setEnabled(false);
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
		setLocation(600, 300);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		setContentPane(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setJMenuBar(listenerBar);

		btnSwitch1Left.setEnabled(false);
		btnSwitch1Left.setSelected(true);
		btnSwitch2Left.setEnabled(false);
		btnSwitch2Left.setSelected(true);
		btnSwitch3Left.setEnabled(false);
		btnSwitch3Left.setSelected(true);

		btnStopOnEmergency.setToolTipText("Alle Zuege anhalten");
	}

	/**
	 * Gibt das Rechte Panel des Control Panel-Fensters zurueck.
	 * 
	 * @return Rechtes Panel
	 * @category Getter
	 */
	public JPanel getPanelSwitches() {
		return panelTrainLeft;
	}

	/**
	 * Gibt alle Zugpanels in einer ArrayList zurueck.
	 * 
	 * @return ArrayList Zugpanels
	 * @category Getter
	 */
	public ArrayList<JPanel> getTrainPanels() {
		return trainPanels;
	}

	/**
	 * Gibt alle Buttons zurueck, mit denen die Weichen verstellt werden.
	 * 
	 * @return Weichenstellbuttons
	 * @category Getter
	 */
	public JToggleButton[] getSwitchButtons() {
		JToggleButton[] output = { btnSwitch1Left, btnSwitch1Right, btnSwitch2Left, btnSwitch2Right, btnSwitch3Left,
				btnSwitch3Right };
		return output;
	}

	/**
	 * Gibt die Arraylist aller ToggleButtons fuer Zuege zurueck.
	 * 
	 * @return ArrayList ToggleButtons
	 * @category Getter
	 */
	public ArrayList<JToggleButton[]> getTrainToggleButtons() {
		return trainToggleButtons;
	}

	/**
	 * Gibt die ArrayList aller Buttons fuer Zuege zurueck
	 * 
	 * @return ArrayList JButtons
	 * @category Getter
	 */
	public ArrayList<JButton[]> getTrainButtons() {
		return trainButtons;
	}

	/**
	 * Gibt die ArrayList aller ProgressBars fuer Zuege zurueck.
	 * 
	 * @return ArrayList ProgressBars
	 * @category Getter
	 */
	public ArrayList<JProgressBar> getProgressBars() {
		return trainProgressBars;
	}

	/**
	 * Gibt die ArrayList aller JSliders fuer Zuege zurueck.
	 * 
	 * @return ArrayList JSliders
	 * @category Getter
	 */
	public ArrayList<JSlider> getSliders() {
		return trainSliders;
	}

	/**
	 * Gibt die ArrayList aller Zugpanels zurueck.
	 * 
	 * @return ArrayList Zugpanels
	 * @category Getter
	 */
	public ArrayList<JLabel[]> getLabelTempo() {
		return trainLabels;
	}

	/**
	 * Gibt alle JMenuItems in einem Array zurueck.
	 * 
	 * @return Array JMenuItems
	 * @category Getter
	 */
	public JMenuItem[] getMenuItems() {
		return new JMenuItem[] { itemStopAll, itemSwitch1Left, itemSwitch1Right, itemSwitch2Left, itemSwitch2Right,
				itemSwitch3Left, itemSwitch3Right };
	}

	/**
	 * Gibt den Nothalt-Button zurueck.
	 * 
	 * @return Nothalt-Button
	 * @category Getter
	 */
	public JButton getBtnStopOnEmergency() {
		return btnStopOnEmergency;
	}

	/**
	 * Gibt den Button Zug hinzufuegen zurueck.
	 * 
	 * @return Button Zug hinzufuegen
	 * @category Getter
	 */
	public JButton getBtnAddTrain() {
		return btnAddTrain;
	}

	/**
	 * Gibt das Hauptpanel zurueck.
	 * 
	 * @return Hauptpanel
	 * @category Getter
	 */
	public JPanel getMainPanel() {
		return panel;
	}

	/**
	 * Gibt die ArrayList aller Tempo-Fuellerpanels zurueck
	 * 
	 * @return ArrayList Fuellerpanels Tempo
	 * @category Getter
	 */
	public ArrayList<JPanel> getFillerPanelsTempo() {
		return fillerPanelsTempo;
	}

	/**
	 * Gibt die ArraList aller Akku-Fuellerpanels zurueck
	 * 
	 * @return ArrayList Fuellerpanels Akku
	 * @category Getter
	 */
	public ArrayList<JPanel> getFillerPanelsBattery() {
		return fillerPanelsBattery;
	}
}
