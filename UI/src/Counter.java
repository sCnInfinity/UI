/**
 * This class functions as a Counter that is used to drain the energy out of
 * trains while running.
 * 
 * @author Lucas Groß-Hardt
 */
public class Counter implements Runnable {
	/** Battery lifetime in %. */
	private int battery;
	/** Number of the train for that the counter is used. */
	private int index;

	/**
	 * Constructor Method. Sets the index and creates a new Counter.
	 * 
	 * @param index
	 *            Index to match the Counter to a Train.
	 * @category Constructor
	 */
	public Counter(int index) {
		this.index = index;
	}

	/**
	 * Run Method. Decreases the battery lifetime once every walkthrough. Writes
	 * Changes to temporary Log Display.
	 */
	@Override
	public void run() {
		// Stop the Train from charging.
		Controller.getListOfTrains().get(index).setCharging(false);
		// Refresh the battery lifetime once every walkthrough
		battery = Controller.getListOfTrains().get(index).getBatteryLifeTime();
		// Only perform the following actions if charging and battery lifetime
		// >= 1
		while (battery > 1 && !Controller.getListOfTrains().get(index).isCharging()
				&& Controller.getListOfTrains().get(index).isRunning()) {
			try {
				// Sleep to give user time to react.
				Thread.sleep(2000);
				// Decreases Battery Lifetime
				battery = battery - 5;
				Controller.getListOfTrains().get(index).setBatteryLifeTime(battery);
				// Update Slider if indizes match
				Controller.getCPanel().getProgressBars(index).setValue(battery);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Write battery Lifetime to the Temporary Log Display when reaching
			// the milestones 50 or 25 %
			if ((battery == 50 && Controller.getListOfTrains().get(index).isRunning())
					|| (battery == 25 && Controller.getListOfTrains().get(index).isRunning()))
				Controller.getLogView().updateLog(
						Controller.getListOfTrains().get(index).getName() + ": " + battery + "% Batterieleistung");
		}
		// Write Message to the Temporary Log Display if battery is empty
		if (battery < 5){
			Controller.getListOfTrains().get(index).setRunning(false);
			Controller.getListOfTrains().get(index).setTempo(0);
			Controller.getLogView().updateLog(Controller.getListOfTrains().get(index).getName() + ": Batterie leer.");
		}
			
	}
}