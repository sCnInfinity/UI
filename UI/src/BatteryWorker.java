/**
 * Diese Klasse implementiert die Funktionen f�r das Aufladeger�t und die
 * Entladung eines Zuges. Sie implementiert Runnable und ist somit ausf�hrbar.
 * 
 * @author Lucas Gro�-Hardt
 */

public class BatteryWorker implements Runnable {
	private boolean charge;
	private Controller con;
	/** Akkulaufzeit in % */
	private int battery;
	/** Nummer des Zuges. */
	private int index;

	/**
	 * Konstruktor. �bernimmt den �bergebenen Index, den Controller und nimmt
	 * die Eigenschaft ladend oder entladend an.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 * @param con
	 *            Controller, �ber den die Daten angesteuert werden.
	 * @param charge
	 *            Gibt an, ob der Zug geladen (true) oder entladen (false)
	 *            werden soll.
	 * @category Constructor
	 */
	public BatteryWorker(int index, Controller con, boolean charge) {
		this.charge = charge;
		this.con = con;
		this.index = index;
	}

	/**
	 * Run-Methode. Implementiert zwei verschiedene Funktionen.
	 * 
	 * 1. Aufladen: Erh�ht iterativ die Akkulaufzeit und schreibt den
	 * Fortschritt in das Log-Fenster.
	 * 
	 * 2. Entladen: Verringet iterativ die Akkulaufzeit und schreibt den
	 * Fortschritt in das Log-Fenster.
	 */
	@Override
	public void run() {
		// nur Aktion durchf�hren, wenn der Zug Batteriebetrieben wird
		if (con.getListOfTrains().get(index).isBatteryPowered()) {
			if (!charge) {
				// ladevorgang stoppen
				con.getListOfTrains().get(index).setCharging(false);
				// Batterielaufzeit aktualisieren
				battery = con.getListOfTrains().get(index).getBatteryLifeTime();
				// Nur ausf�hren, wenn Batterielaufzeit > 1, der Zug nicht l�dt,
				// der Zug f�hrt, und der Zug batteriebetrieben wird
				while (battery > 1 && !con.getListOfTrains().get(index).isCharging()
						&& con.getListOfTrains().get(index).isRunning()
						&& con.getListOfTrains().get(index).isBatteryPowered()) {
					try {
						// Schlafen, um das Geschehen sichtbar zu machen
						Thread.sleep(2000);
						// Batterielaufzeit verringern
						battery = battery - 5;

						con.getListOfTrains().get(index).setBatteryLifeTime(battery);
						// JProgressBar updaten
						con.getCPanel().getProgressBars(index).setValue(battery);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Bei 25 und 50% Nachricht ins Log schreiben
					if ((battery == 50 && con.getListOfTrains().get(index).isRunning())
							|| (battery == 25 && con.getListOfTrains().get(index).isRunning()))
						con.getLogView().updateLog(
								con.getListOfTrains().get(index).getName() + ": " + battery + "% Batterieleistung");
				}
				// Nachricht ins Log wenn Batterie leer
				if (battery < 5) {
					con.getListOfTrains().get(index).setRunning(false);
					con.getListOfTrains().get(index).setTempo(0);
					con.getLogView().updateLog(con.getListOfTrains().get(index).getName() + ": Batterie leer.");
				}

			} else {
				// Akkulaufzeit bei jedem Durchlauf aktualisieren
				battery = con.getListOfTrains().get(index).getBatteryLifeTime();
				// solange Akku nicht voll geladen, der Zug aufl�dt und der Zug
				// nicht
				// l�uft
				while (battery < 91 && con.getListOfTrains().get(index).isCharging()
						&& !con.getListOfTrains().get(index).isRunning()) {
					// Sleep-Aufruf, um das Programm f�r den Nutzer bedienbar zu
					// machen.
					try {
						Thread.sleep(1000);
						// Akkulaufzeit erh�hen
						battery = battery + 10;
						con.getListOfTrains().get(index).setBatteryLifeTime(battery);
						// Aktualisiert die JProgressBar
						con.getCPanel().getProgressBars(index).setValue(battery);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// Nachricht im Log: Batterie aufgeladen, wenn Akku voll geladen
				con.getLogView().updateLog(con.getListOfTrains().get(index).getName() + ": Batterie aufgeladen.");
				// Ladevorgang beenden
				con.getListOfTrains().get(index).setCharging(false);
			}
		}
	}
}