
import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.UIManager;

/**
 * Hauptklasse, die das Programm startet und die Hauptfenster erzeugt.
 * 
 * @author Lucas
 * @category Main
 */
public class Start {
	/** Visualisierung der Zuege */
	private TrackView tView;
	/** Kontrollfenster der Zuege */
	private ControlPanelView cPanel;
	/** Log-Anzeige */
	private LogView lView;
	/** Anzahl der Zuege, die erzeugt werden soll. */
	private static final int numberOfTrains = 1;

	/**
	 * Konstruktor. Passt das Look-And-Feel an, startet den Ladebildschirm und
	 * baut das User Interface auf.
	 * 
	 * @category Constructor
	 */
	public Start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				showLoadingScreen();
			}
		}).start();
		buildUI();
	}

	/**
	 * Main-Methode. Startet bei Ausfuehrung das Programm, indem eine neue
	 * Instanz der Klasse Start erzeugt wird.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Start start = new Start();
	}

	/**
	 * Baut das UI auf. Diese Methode ist mit der Methode showLoadingScreen
	 * synchronisiert, um den Aufbau zeutlich abhaengig vom Ladebildschirm zu
	 * gestalten.
	 */
	private synchronized void buildUI() {
		Controller con = new Controller(numberOfTrains);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tView = new TrackView(con);
				new Thread(tView).start();
				con.setTrackView(tView);
				cPanel = new ControlPanelView(numberOfTrains);
				con.setCPanel(cPanel);
				lView = new LogView();
				con.setLogView(lView);
			}
		});
		// Auf die Beendigung des Ladebildschirms warten.
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cPanel.setVisible(true);
		lView.setVisible(true);
		tView.setVisible(true);
	}

	/**
	 * Zeigt den Ladebildschirm an. Diese Methode ist mit der Methode buildUI
	 * synchronisiert, um den um den Aufbau des UIs zeutlich abhaengig vom Ladebildschirm
	 * zu gestalten.
	 */
	private synchronized void showLoadingScreen() {
		JWindow loadingScreen = new JWindow();
		JPanel content = new JPanel(new BorderLayout());
		JProgressBar progressBar = new JProgressBar(0, 100);

		content.add(new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("loadingScreenBG.jpg")).getImage()
				.getScaledInstance(300, 200, java.awt.Image.SCALE_SMOOTH))), BorderLayout.CENTER);
		content.add(progressBar, BorderLayout.SOUTH);

		loadingScreen.add(content);
		loadingScreen.pack();
		loadingScreen.setLocationRelativeTo(null);

		progressBar.setStringPainted(true);
		loadingScreen.setVisible(true);
		for (int i = 0; i < 100; i++) {
			progressBar.setValue(i);
			progressBar.setString(i + "%");
			try {
				Thread.sleep(20);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		loadingScreen.dispose();
		//notify, um showLoadingsScreen() zu wecken
		this.notifyAll();
	}
}
