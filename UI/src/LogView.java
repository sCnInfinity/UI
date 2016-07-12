import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * Diese Klasse implementiert das Log-Anzeigefenster.
 * 
 * @author Lucas Gross-Hardt
 * @category View
 */
public class LogView extends JFrame {
	/** Textbereich, in den Logeintraege eingetragen werden */
	private JTextArea txtLog;
	/** ScrollPane. Ermoeglicht das Scrollen durch das Logfenster */
	private JScrollPane scrollPane;

	/**
	 * Kostruktor. Setzt das Log-Fenster zusammen, indem buildWindow aufgerufen
	 * wird.
	 * 
	 * @category Constructor
	 */
	public LogView() {
		buildWindow();
	}

	/**
	 * Gibt den Textbereich zurueck, in den Logeintraege geschrieben werden.
	 * 
	 * @return Textbereich mit Log-Eintraegen
	 * @category Getter
	 */
	public JTextArea getLogFile() {
		return txtLog;
	}

	/**
	 * Schreibt neue Informationen in den Textbereich.
	 * 
	 * @param upd
	 *            Text, der ins Log geschrieben werden soll.
	 * @category Setter
	 */
	public void updateLog(String upd) {
		txtLog.setText(txtLog.getText() + "\n" + upd);
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}

	/**
	 * Setzt das Log-Fenster zusammen
	 */
	private void buildWindow() {
		setSize(500, 300);
		setTitle("Log-File");
		setLocation(600, 0);
		JPanel panel = new JPanel(new GridLayout(1, 1));

		txtLog = new JTextArea();
		txtLog.setEditable(false);

		// Fuegt ein ScrollPane hinzu, das ein Scrollen durch die Eintraege
		// ermoeglicht
		scrollPane = new JScrollPane(txtLog, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setSize(this.getSize());
		panel.add(scrollPane);
		setContentPane(panel);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
