import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Diese Klasse implementiert die Anzeige fuer ein Konfigurationsfenster.
 * 
 * @author Lucas Gross-Hardt
 * @category View
 */
public class ConfigView extends JFrame {
	/** Instanz der Controller-Klasse */
	private Controller con;
	/** Button Konfiguration Schliessen */
	private JButton btnCloseConfig = new JButton("Speichern");
	/** Button Bild auswaehlen */
	private JButton btnSelectImage = new JButton("<html>Bild waehlen</html>");
	/** Hauptpanel */
	private JPanel panel = new JPanel(new BorderLayout());
	/** Unteres Panel */
	private JPanel panelBot = new JPanel();
	/** Oberes Panel */
	private JPanel panelTop = new JPanel();
	/** Textfeld Name */
	private JTextField txtName = new JTextField();
	/** Label Bildanzeige */
	private JLabel imageLabel;
	/** Label Bild fehlt */
	private JLabel lblImageMissing = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("noimg.png"))
			.getImage().getScaledInstance(90, 90, java.awt.Image.SCALE_SMOOTH)));
	/** Checkbox batteriebetrieben */
	private JCheckBox cBoxBatteryPowered = new JCheckBox();
	/** Zugnummer */
	private int index;
	/** Bildpfad */
	private String imagePath;
	/** Datenarray Zugdaten */
	private String[] data;
	/** Vorheriger Name */
	private String oldName;

	/**
	 * Konstruktor. Setzt index und laedt Daten aus Textdatei.
	 * 
	 * @param index
	 *            Zugnummer
	 * @param con
	 *            Controller-Instanz
	 * @category Constructor
	 */
	public ConfigView(int index, Controller con) {
		this.con = con;
		this.index = index;
		try {
			data = con.readDataOnOpen(this.index);
			txtName.setText(data[0]);
			imagePath = data[1];
		} catch (Exception e) {
		}
		// Wert setzen, um Vergleichswert zu haben
		oldName = con.getListOfTrains().get(index).getName();
		buildWindow();
	}

	/**
	 * Baut das Konfigurationsfenster auf.
	 */
	private void buildWindow() {
		// Fenstereigenschaften festlegen
		setTitle("Eisenbahn: Konfiguration");
		setContentPane(panel);
		setSize(400, 300);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				con.closeConfig(index, con.getListOfTrains().get(index).getImagePath(),
						con.getListOfTrains().get(index).isBatteryPowered(), con.getListOfTrains().get(index).getName(),
						con.getListOfTrains().get(index).getName());
			}
		});
		// Elemente zum Fenster hinzufuegen
		panelTop.setLayout(new GridLayout(2, 2, 5, 5));
		panelTop.add(new JLabel(" Name eingeben"));
		panelTop.add(txtName);
		panelTop.add(new JLabel(" Batteriebetrieben"));
		panelTop.add(cBoxBatteryPowered);

		// Bild von gesetztem Pfad lesen
		try {
			imageLabel = new JLabel(prepareImage());
			panel.add(imageLabel);
			if (imageLabel.getIcon().getIconWidth() < 1)
				panel.add(lblImageMissing);
		} catch (Exception e) {
		}
		panelBot.setLayout(new GridLayout(1, 2));
		panelBot.add(btnSelectImage);
		panelBot.add(btnCloseConfig);
		panel.add(panelTop, BorderLayout.NORTH);
		panel.add(panelBot, BorderLayout.SOUTH);

		setVisible(true);
	}

	/**
	 * Liest das Bild vom Pfad und gibt ein Icon mit diesem Bild zurueck.
	 * 
	 * @return ImageIcon fuer Bildanzeige
	 */
	public ImageIcon prepareImage() {
		ImageIcon icon = new ImageIcon(imagePath);
		Image img = icon.getImage();
		img = img.getScaledInstance(90, 90, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		return icon;
	}

	/**
	 * Gibt die beiden JButtons des Fensters in einem Array zurueck.
	 * 
	 * @return ButtonArray
	 * @category Getter
	 */
	public JButton[] getJButtons() {
		return new JButton[] { btnCloseConfig, btnSelectImage };
	}

	/**
	 * Gibt das Hauptpanel des Fensters zurueck.
	 * 
	 * @return Hauptpanel
	 * @category Getter
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Gibt den Speicherpfad des Bildes zurueck.
	 * 
	 * @return Speicherpfad
	 * @category Getter
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * Ueberschreibt den Imagepfad des Bildes.
	 * 
	 * @param Speicherpfad
	 * @category Setter
	 */
	public void setImagePath(String path) {
		imagePath = path;
	}

	/**
	 * Gibt die Checkbox des Fensters zurueck.
	 * 
	 * @return Checkbox Batterienutzung
	 * @category Getter
	 */
	public JCheckBox getCheckBox() {
		return cBoxBatteryPowered;
	}

	/**
	 * Gibt das Textfeld des Fensters zurueck.
	 * 
	 * @return Textfeld
	 * @category Getter
	 */
	public JTextField getTextField() {
		return txtName;
	}

	/**
	 * Gibt die Labels des Fensters zurueck.
	 * 
	 * @return Array von Labels
	 * @category Getter
	 */
	public JLabel[] getLabels() {
		return new JLabel[] { imageLabel, lblImageMissing };
	}

	/**
	 * Gibt die Zugnummer zurueck.
	 * 
	 * @return Zugnummer
	 * @category Getter
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Gibt den vorherigen Namen zurueck.
	 * 
	 * @category Getter
	 * @return vorheriger Name
	 */
	public String getOldName() {
		return oldName;
	}
}
