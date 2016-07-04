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
 * This class functions as a viewable configuration window.
 * 
 * @author Lucas Groﬂ-Hardt
 * @category View
 */
public class ConfigView extends JFrame {
	private Controller con;
	/** Button to close the configuration window */
	private JButton btnCloseConfig = new JButton("Speichern");
	/** Button to select an image for a train */
	private JButton btnSelectImage = new JButton("<html>Bild w‰hlen</html>");
	/** Contentpane */
	private JPanel panel = new JPanel(new BorderLayout());
	private JPanel panelBot = new JPanel();
	private JPanel panelTop = new JPanel();
	/** Textfield to change a trains name */
	private JTextField txtName = new JTextField();
	/** Label to display a trains image */
	private JLabel imageLabel;
	/** */
	private JLabel lblImageMissing = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("noimg.png"))
			.getImage().getScaledInstance(90, 90, java.awt.Image.SCALE_SMOOTH)));
	private JCheckBox cBoxBatteryPowered = new JCheckBox();
	/** Number to match a configuration window to a train */
	private int index;
	/** Path to the location of a trains image */
	private String imagePath;
	/** String array to store train data */
	private String[] data;
	/** String for comparison purposes when the name of a train is changed */
	private String oldName;

	/**
	 * Constructor method. Sets the index and reads previously saved data from
	 * files if existing.
	 * 
	 * @param index
	 *            Number to match the configuration window
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
		// set a value for oldName to have a valid comparison
		oldName = con.getListOfTrains().get(index).getName();
		buildWindow();
	}

	/**
	 * Method to build the configuration window
	 */
	private void buildWindow() {
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
		panelTop.setLayout(new GridLayout(2, 2, 5, 5));
		panelTop.add(new JLabel(" Name eingeben"));
		panelTop.add(txtName);
		panelTop.add(new JLabel(" Batteriebetrieben"));
		panelTop.add(cBoxBatteryPowered);

		// Read image from set Path
		try {
			imageLabel = new JLabel(prepareImage());
			// adjust label size to image size
			panel.add(imageLabel);
			if (imageLabel.getIcon().getIconWidth() < 0)
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

	public ImageIcon prepareImage() {
		ImageIcon icon = new ImageIcon(imagePath);
		Image img = icon.getImage();
		img = img.getScaledInstance(90, 90, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		return icon;
	}

	public JButton[] getJButtons() {
		return new JButton[] { btnCloseConfig, btnSelectImage };
	}

	public JPanel getPanel() {
		return panel;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String path) {
		imagePath = path;
	}

	public JCheckBox getCheckBox() {
		return cBoxBatteryPowered;
	}

	public JTextField getTextField() {
		return txtName;
	}

	public JLabel[] getLabels() {
		return new JLabel[] { imageLabel, lblImageMissing };
	}

	public int getIndex() {
		return index;
	}

	public String getOldName() {
		return oldName;
	}
}
