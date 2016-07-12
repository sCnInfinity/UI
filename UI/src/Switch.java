/**
 * Diese Klasse implementiert richtungsverstellbare Weichen.
 * 
 * @author Lucas Gross-Hardt
 * @category Model
 */
public class Switch {
	/** Controller-Instanz */
	private Controller con;
	/** ID, die die Weiche eindeutig identifizierbar macht */
	private int id;
	/** Ausrichtung. True <> Links, False <> Rechts */
	private boolean alignment;

	/**
	 * Konstruktor. Erstellt eine neue Instanz der Klasse Switch, fuegt eine
	 * Controller-Instanz hinzu, setzt die Ausrichtung und setzt die ID.
	 * 
	 * @param id
	 *            ID der Weiche
	 * @param con
	 *            Controller-Instanz
	 * @category Constructor
	 */
	public Switch(int id, Controller con) {
		this.con = con;
		alignment = true;
		this.id = id;
	}

	/**
	 * Aendert die Ausrichtung der Weiche und schreibt diese Aenderung in das Log.
	 * 
	 * @param alignment
	 * @category Setter
	 */
	public void setAlignment(boolean alignment) {
		this.alignment = alignment;
		if (alignment)
			con.getLogView().updateLog("Weiche " + id + ": Nach links ausgerichtet.");
		else
			con.getLogView().updateLog("Weiche " + id + ": Nach Rechts ausgerichtet.");
	}
}
