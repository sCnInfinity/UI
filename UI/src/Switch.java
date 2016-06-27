/**
 * This Class functions as a Switch to change a trains direction.
 * @author Lucas Groﬂ-Hardt
 */
public class Switch {
	private Controller con;
	/**	ID to separate Switch form others. */
	private int id;
	/** Alignment setting. True <> Left, False <> Right*/
	private boolean alignment;
	
	/**
	 * Constructor Method. Sets the alignment and the ID.
	 * @param id Switch ID.
	 * @category Constructor
	 */
	public Switch(int id, Controller con) {
		this.con = con;
		//set default alignment to left
		alignment = true;
		this.id = id;
	}

	/**
	 * Sets the alignment to either left(true) or right(false). Writes changes to the Temporary Log Display.
	 * @param alignment
	 * @category Setter
	 */
	public void setAlignment(boolean alignment){
		this.alignment = alignment;
		if(alignment)con.getLogView().updateLog("Weiche "+ id + ": Nach links ausgerichtet.");
		else con.getLogView().updateLog("Weiche "+ id + ": Nach Rechts ausgerichtet.");
	}
}
