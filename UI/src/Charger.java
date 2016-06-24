/**
 * Diese Klasse funktioniert wie ein Akkuladegerät. Wird ein Thread dieser
 * Klasse gestartet, wird ein Zug aufgeladen.
 * 
 * @author Lucas Groß-Hardt
 */

public class Charger implements Runnable {
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
	public Charger(int index) {
		this.index = index;
	}

	/**
	 * Run-Methode. Erhöht iterativ die Akkulaufzeit und schreibt den
	 * Fortschritt in das Log-Fenster.
	 */
	@Override
	public void run() {
		// Akkulaufzeit bei jedem Durchlauf aktualisieren
		battery = Controller.getListOfTrains().get(index).getBatteryLifeTime();
		// solange Akku nicht voll geladen, der Zug auflädt und der Zug nicht
		// läuft
		while (battery < 91 && Controller.getListOfTrains().get(index).isCharging()
				&& !Controller.getListOfTrains().get(index).isRunning()) {
			// Sleep-Aufruf, um das Programm für den Nutzer bedienbar zu machen.
			try {
				Thread.sleep(1000);
				// Akkulaufzeit erhöhen
				battery = battery + 10;
				Controller.getListOfTrains().get(index).setBatteryLifeTime(battery);
				// Aktualisiert den Slider, wenn der im Control Panel
				// ausgewählte Zug dem in diesem Thread bearbeiteten Zug
				// entspricht
				Controller.getCPanel().getProgressBars(index).setValue(battery);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Nachricht im Log: Batterie aufgeladen, wenn Akku voll geladen
		Controller.getLogView().updateLog(Controller.getListOfTrains().get(index).getName() + ": Batterie aufgeladen.");
		// Ladevorgang beenden
		Controller.getListOfTrains().get(index).setCharging(false);
	}
}