import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.UIManager;

public class Start {
	private TrackView tView;
	private ControlPanelView cPanel;
	private LogView lView;
	private static final int numberOfTrains = 4;

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

	public static void main(String[] args) {
		Start start = new Start();
	}

	private synchronized void buildUI() {
		Controller con = new Controller();
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
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cPanel.setVisible(true);
		lView.setVisible(true);
		tView.setVisible(true);
	}

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
		this.notifyAll();
	}
}
