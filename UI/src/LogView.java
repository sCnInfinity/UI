import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * This class functions as a viewable log file that can be updated with new information. 
 * @author Lucas Groﬂ-Hardt
 * @category View
 */
public class LogView extends JFrame {
	/** Text area to display log information*/
	public JTextArea txtLog;
	
	JScrollPane scrollPane;

	/** 
	 * Constructor Method.
	 * Builds the log window.
	 * @category Constructor
	 */
	public LogView() {
		buildWindow();
	}

	/**
	 * Adds new information to the text area.
	 * @param upd Text which is to be added to the log
	 * @category Setter
	 */
	
	public JTextArea getLogFile(){
		return txtLog;
	}
	
	public void updateLog(String upd){
		txtLog.setText(txtLog.getText()+"\n"+upd);
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}

	/**
	 * Builds the log window.
	 */
	private void buildWindow() {
		setSize(500, 300);
		setTitle("Log-File");
		JPanel panel = new JPanel();

		txtLog = new JTextArea();
		txtLog.setBounds(1, 1, getWidth(), getHeight());
		txtLog.setEditable(false);

		//Adds a Scrollpane so that the user can look through older and newer information.
		 scrollPane = new JScrollPane(txtLog, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(5, 5, 475, 250);

		panel.add(scrollPane);
		panel.setLayout(null);
		setContentPane(panel);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

}
