import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class functions as a viewable configuration window.
 * 
 * @author Lucas Groﬂ-Hardt
 * @category View
 */
public class ConfigView extends JFrame implements ActionListener {
	/** Button to close the configuration window */
	private JButton btnCloseConfig = new JButton("Speichern");
	/** Button to select an image for a train */
	private JButton btnSelectImage = new JButton("Durchsuchen");
	/** Contentpane */
	private JPanel panel = new JPanel();
	/** Textfield to change a trains name */
	private JTextField txtName = new JTextField();
	/** Label to display a trains image */
	private JLabel imageLabel;
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
	public ConfigView(int index) {
		this.index = index;
		try {
			data = Controller.readDataOnOpen(this.index);
			txtName.setText(data[0]);
			imagePath = data[1];
		} catch (Exception e) {
		}
		// set a value for oldName to have a valid comparison
		oldName = Controller.getListOfTrains().get(index).getName();
		buildWindow();
	}

	/**
	 * Method to build the configuration window
	 */
	private void buildWindow() {
		setTitle("Eisenbahn: Konfiguration");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setContentPane(panel);
		setSize(500, 300);
		setVisible(true);

		panel.setLayout(null);
		panel.add(btnCloseConfig);
		panel.add(txtName);
		panel.add(btnSelectImage);
		// Read image from set Path
		try {

			imageLabel = new JLabel(prepareImage());
			// adjust label size to image size
			imageLabel.setBounds(120, 120, (int) imageLabel.getPreferredSize().getWidth(),
					(int) imageLabel.getPreferredSize().getHeight());
			panel.add(imageLabel);
		} catch (Exception e) {
		}

		txtName.setBounds(40, 40, 50, 20);
		txtName.setText(data[0]);

		btnSelectImage.addActionListener(this);
		btnSelectImage.setBounds(1, 90, 100, 20);

		btnCloseConfig.setBounds(1, 1, (int) btnCloseConfig.getPreferredSize().getWidth(), 25);
		btnCloseConfig.addActionListener(this);
	}

	public ImageIcon prepareImage() {
		ImageIcon icon = new ImageIcon(imagePath);
		Image img = icon.getImage();
		img = img.getScaledInstance(90, 90, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		return icon;
	}

	/**
	 * Method required due to implementing an action listener. Performs actions
	 * for clicking buttons.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();

		// if button for image selection is clicked, read out new Imagepath and
		// repaint
		if (s == btnSelectImage) {
			imagePath = Controller.selectImage(this);
			imageLabel.setIcon(prepareImage());
			repaint();
		}

		// Compare old name to new name. Save name if oldName and newName differ
		if (s == btnCloseConfig) {
			if (txtName.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "Der Name muss mindestens ein Zeichen enthalten.");
			} else
				Controller.closeConfig(index, imagePath, txtName.getText(), oldName);
		}

	}

}
