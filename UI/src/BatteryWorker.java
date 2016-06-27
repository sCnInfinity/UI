/**
 * Diese Klasse funktioniert wie ein Akkuladegerät. Wird ein Thread dieser
 * Klasse gestartet, wird ein Zug aufgeladen.
 * 
 * @author Lucas Groß-Hardt
 */

public class BatteryWorker implements Runnable {
	private boolean charge;
	private Controller con;
	/** Akkulaufzeit in % */
	private int battery;
	/** Nummer des Zuges. */
	private int index;

	/**
	 * Konstruktor. Übernimmt den übergebenen Index.
	 * 
	 * @param index
	 *            Nummer des Zuges
	 * @category Constructor
	 */
	public BatteryWorker(int index, Controller con, boolean charge) {
		this.charge = charge;
		this.con = con;
		this.index = index;
	}

	/**
	 * Run-Methode. Erhöht iterativ die Akkulaufzeit und schreibt den
	 * Fortschritt in das Log-Fenster.
	 */
	@Override
	public void run() {
		if (con.getListOfTrains().get(index).isBatteryPowered()) {
			if (!charge) {
				// Stop the Train from charging.
				con.getListOfTrains().get(index).setCharging(false);
				// Refresh the battery lifetime once every walkthrough
				battery = con.getListOfTrains().get(index).getBatteryLifeTime();
				// Only perform the following actions if charging and battery
				// lifetime
				// >= 1
				while (battery > 1 && !con.getListOfTrains().get(index).isCharging()
						&& con.getListOfTrains().get(index).isRunning()) {
					try {
						// Sleep to give user time to react.
						Thread.sleep(2000);
						// Decreases Battery Lifetime
						battery = battery - 5;

						con.getListOfTrains().get(index).setBatteryLifeTime(battery);
						// Update Slider if indizes match
						con.getCPanel().getProgressBars(index).setValue(battery);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Write battery Lifetime to the Temporary Log Display when
					// reaching
					// the milestones 50 or 25 %
					if ((battery == 50 && con.getListOfTrains().get(index).isRunning())
							|| (battery == 25 && con.getListOfTrains().get(index).isRunning()))
						con.getLogView().updateLog(
								con.getListOfTrains().get(index).getName() + ": " + battery + "% Batterieleistung");
				}
				// Write Message to the Temporary Log Display if battery is empty
				if (battery < 5) {
					con.getListOfTrains().get(index).setRunning(false);
					con.getListOfTrains().get(index).setTempo(0);
					con.getLogView().updateLog(con.getListOfTrains().get(index).getName() + ": Batterie leer.");
				}

			} else {
				// Akkulaufzeit bei jedem Durchlauf aktualisieren
				battery = con.getListOfTrains().get(index).getBatteryLifeTime();
				// solange Akku nicht voll geladen, der Zug auflädt und der Zug
				// nicht
				// läuft
				while (battery < 91 && con.getListOfTrains().get(index).isCharging()
						&& !con.getListOfTrains().get(index).isRunning()) {
					// Sleep-Aufruf, um das Programm für den Nutzer bedienbar zu
					// machen.
					try {
						Thread.sleep(1000);
						// Akkulaufzeit erhöhen
						battery = battery + 10;
						con.getListOfTrains().get(index).setBatteryLifeTime(battery);
						// Aktualisiert den Slider, wenn der im Control Panel
						// ausgewählte Zug dem in diesem Thread bearbeiteten Zug
						// entspricht
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