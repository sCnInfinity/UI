import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Diese Klasse implementiert die Funktionen fuer das Aufladegeraet und die
 * Entladung eines Zuges. Sie implementiert Runnable und ist somit ausfuehrbar.
 * 
 * @author Lucas Gross-Hardt
 * @category Worker
 */

public class Backgroundworker extends JPanel implements Runnable {
	/** Instanz der Controller-Klasse */
	private Controller con;

	/**
	 * Konstruktor. Uebernimmt den Controller.
	 * 
	 * @param con
	 *            Controller, ueber den die Daten angesteuert werden.
	 * @category Constructor
	 */
	public Backgroundworker(Controller con) {
		this.con = con;
	}

	/**
	 * Run-Methode. Implementiert zwei verschiedene Funktionen.
	 * 
	 * 1. Aufladen: Erhoeht iterativ die Akkulaufzeit und schreibt den Fortschritt
	 * in das Log-Fenster.
	 * 
	 * 2. Entladen: Verringet iterativ die Akkulaufzeit und schreibt den Fortschritt
	 * in das Log-Fenster.
	 */

	@Override
	public void run() {
		while (true) {
			con.numberOfTrains = con.getListOfTrains().size();
			for (int i = 0; i < con.getListOfTrains().size(); i++) {
				con.getListOfTrains().get(i).moveTrain();

				con.getListOfTrains().get(i).calculateBatteryLife();
			}

			
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}