import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Dies Klasse implementiert einen Zug, fuer den Einstellungen gespeichert werden
 * koennen und der auf der Strecke platziert werden kann.
 * 
 * @author Lucas Gross-Hardt
 * @category Model
 * @category View
 */
public class Train extends JPanel implements Runnable {
	/** Controller-Instanz */
	private Controller controller;
	/** Name des Zuges */
	private String name;
	/** Speicherpfad des Zugbildes */
	private String imagePath;
	/** Licht-Status, false <> aus, true <> an */
	private boolean light;
	/** Gibt an, ob der Zug vorwaerts faehrt. */
	private boolean forward;
	/** Gibt an, ob der Zug rueckwaerts faehrt. */
	private boolean backward;
	/** Gibt an, ob der Zug laeuft. */
	private boolean running;
	/** Gibt an, ob der Zug aufgeladen wird. */
	private boolean charging;
	/** Gibt an, ob der Zug batteriebetrieben ist */
	private boolean poweredByBattery;
	/** Rotationsgrad des Zuges */
	private double rotation = 0;
	/** Betrag, um den die Rotation bei einer Drehung geaendert wird. */
	private double rotationChange = 4.5;
	/** Bewegungsgeschwindigkeit. Min. 0 ,max. 200 */
	private int tempo;
	/** Zugnummer */
	private int index;
	/** Akkukapazitaet. Default-Wert ist 100 %. */
	private int battery = 100;
	/**
	 * Schrittgroesse, um die die Postion des Zuges bei der Bewegung verschoben
	 * wird.
	 */
	private int stepSize = 2;

	/** X-Position des Zuges */
	private int x = 75;
	/** Y-Position des Zuges */
	private int y = 23;
	/** Breite des Zuges */
	private int w = 60;
	/** Hoehe des Zuges */
	private int h = 60;
	/** X-Koordinate der Zugmitte */
	private int cX = 0;
	/** Y-Koordinate der Zugmitte */
	private int cY = 0;

	/**
	 * Konstruktor. Erzeugt eine neue Instanz der Klasse Train und setzt den
	 * Zugnamen, die Zugnummer und die Controller-Instanz des Zuges.
	 * 
	 * @param name
	 *            Zugname.
	 * @param index
	 *            Zugnummer
	 * @param con
	 *            Controller-Instanz
	 * @category Constructor
	 */
	public Train(String name, int index, Controller con) {
		this.controller = con;
		try {
			this.index = index;
			String[] data = con.readDataOnOpen(this.index);
			imagePath = data[1];
		} catch (Exception e) {
		}
		this.name = name;
		forward = true;
		backward = false;
		this.index = index;
	}

	/**
	 * Paint-Component Methode. Zeichnet den Zug in das Visualisierungsfenster.
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g);
		AffineTransform old = g2d.getTransform();
		g2d.rotate(Math.toRadians(rotation), w / 2, h / 2);
		g2d.setColor(controller.getTrackView().getColors().get(index));
		g2d.fillRect(12, 26, 30, 8);
		if (light)
			g2d.drawImage(new ImageIcon(getClass().getResource("trainlight.png")).getImage(), 5, 25, 50, 10, this);
		else
			g2d.drawImage(new ImageIcon(getClass().getResource("train.png")).getImage(), 5, 25, 50, 10, this);
		g2d.setTransform(old);
		repaint();
		g2d.dispose();
	}

	/**
	 * Aendert den Batteriemodus des Zuges.
	 * 
	 * @param mode
	 *            Batteriemodus
	 * @category Setter
	 */
	public void setBatteryMode(boolean mode) {
		poweredByBattery = mode;
		if (poweredByBattery)
			controller.getLogView().updateLog(name + " wird nun per Batterie betrieben.");
		else
			controller.getLogView().updateLog(name + " ist nun ans Stromnetz angeschlossen.");
	}

	/**
	 * Gibt zurueck, ob der Zug batteriebetrieben ist.
	 * 
	 * @return Batteriemodus
	 * @category Getter
	 */
	public boolean isBatteryPowered() {
		return poweredByBattery;
	}

	/**
	 * Gibt den Speicherpfad des Bildes zurueck.
	 * 
	 * @return Speicherpfad des Bildes.
	 * @category Getter
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * Setzt den Speicherpfad des Bildes.
	 * 
	 * @param path
	 *            Speicherpfad des Bildes.
	 * @category Setter
	 */
	public void setImagePath(String path) {
		imagePath = path;
	}

	/**
	 * Gibt die Akkulaufzeit des Zuges zurueck.
	 * 
	 * @return Akkulaufzeit
	 * @category Getter
	 */
	public int getBatteryLifeTime() {
		return battery;
	}

	/**
	 * Setzt die Akkulaufzeit des Zuges.
	 * 
	 * @param battery
	 *            Akkulaufzeit
	 * @category Setter
	 */
	public void setBatteryLifeTime(int battery) {
		this.battery = battery;
	}

	/**
	 * Gibt den Namen des Zuges zurueck.
	 * 
	 * @return Name des Zuges
	 * @category Getter
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt den Namen und schreibt die Aenderungen ggf. in das Log
	 * 
	 * @param name
	 *            Name des Zuges
	 * @param oldName
	 *            Vorheriger Name des Zuges
	 * @category Setter
	 */
	public void setName(String name, String oldName) {
		this.name = name;
		// Aenderungen nur anzeigen, wenn der neue Namen sich vom Alten
		// unterscheidet
		if (!oldName.equals(name)) {
			controller.getLogView().updateLog("Name geaendert: " + oldName + " --> " + name + "( index " + index + ")");
		}
	}

	/**
	 * Gibt die Richtung zurueck, in die der Zug faehrt.
	 * 
	 * @return Bewegungsrichtung
	 * @category Getter
	 */
	public String getDirection() {
		if (forward) {
			return "forward";
		} else if (backward) {
			return "backward";
		}
		return null;
	}

	/**
	 * Setzt die Bewegungsrichtung des Zuges und schreibt Aenderungen in das Log.
	 * 
	 * @param direction
	 *            Bewegungsrichtung
	 * @category Setter
	 */
	public void setDirection(String direction) {
		if (direction.equals("forward")) {
			forward = true;
			backward = false;
			controller.getLogView().updateLog(name + " faehrt nun vorwaerts ");
		} else if (direction.equals("backward")) {
			backward = true;
			forward = false;
			controller.getLogView().updateLog(name + " faehrt nun rueckwaerts ");
		}
	}

	/**
	 * Versetzt den Zug in den Status aufladen oder nicht aufladen.
	 * 
	 * @param charging
	 *            Gibt an, on der Zug auflaedt.
	 * @category Setter
	 */
	public void setCharging(boolean charging) {
		this.charging = charging;
	}

	/**
	 * Gibt zurueck, ob der Zug auflaedt oder nicht.
	 * 
	 * @return Gibt an, ob der Zug auflaedt
	 * @category Getter
	 */
	public boolean isCharging() {
		return charging;
	}

	/**
	 * Aktiviert oder deaktiviert den Zug. Schreibt ggf. Informationen in das
	 * Log.
	 * 
	 * @param running
	 *            Gibt an, ob der Zug laeuft.
	 * @category Setter
	 */
	public void setRunning(boolean running) {
		if (battery < 1 && !this.running && running)
			controller.getLogView().updateLog(name + ": Nicht ausreichend geladen.");
		else
			this.running = running;
	}

	/**
	 * Gibt zurueck, ob der Zug laeuft
	 * 
	 * @return Gibt an, ob der Zug laeuft.
	 * @category Getter
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Stellt das Licht an oder aus.
	 * 
	 * @param status
	 *            Lichtstatus
	 * @category Setter
	 */
	public void setLightOn(boolean status) {
		boolean tmp = light;
		if (status) {
			if (battery > 0) {
				controller.getLogView().updateLog(name + ": Licht an");
				light = status;
			} else {
				controller.getLogView().updateLog(name + ": Nicht ausreichend geladen.");
				light = false;
			}
		} else {
			if (!(tmp == status)) {
				controller.getLogView().updateLog(name + ": Licht aus");
				light = status;
			}
		}
	}

	/**
	 * Gibt zurueck, ob das Licht eingeschaltet ist.
	 * 
	 * @return Lichtstatus
	 * @category Getter
	 */
	public boolean lightIsOn() {
		return light;
	}

	/**
	 * Stellt die Bewegungsgeschwindigkeit des Zuges ein.
	 * 
	 * @param tempo
	 *            Bewegungsgeschwindigkeit
	 * @category Setter
	 */
	public void setTempo(int tempo) {
		this.tempo = tempo;
		if (battery > 1) {
			controller.getLogView().updateLog(name + " faehrt jetzt mit einer Geschwindigkeit von " + tempo + " km/h.");
		}
	}

	/**
	 * Gibt die Bewegungsgeschwindigkeit des Zuges zurueck.
	 * 
	 * @return Bewegungsgeschwindigkeit
	 * @category Getter
	 */
	public int getTempo() {
		return tempo;
	}

	/**
	 * Gibt die Positionsparamter des Zuges in einem Array zurueck.
	 * 
	 * @return Array mit Positionsparametern
	 * @category Getter
	 */
	public int[] getPositionParameters() {
		return new int[] { x, y, w, h };
	}

	/**
	 * Bewegt den Zug in die korrekte Richtung.
	 */
	public void moveTrain() {
		if (running && tempo > 0) {
			if (getDirection().equals("backward")) {
				cX = x + w / 2;
				cY = y + h / 2;
				if (cX < 400 && cX >= 95 && cY > 360)
					moveRight();
				else if (cX > 360 && cY >= 55)
					moveUp();
				else if (cX > 55 && cY < 95)
					moveLeft();
				else if (cX < 95 && cY <= 400)
					moveDown();
				System.out.println("move");
			}
			if (getDirection().equals("forward")) {
				cX = x + w / 2;
				cY = y + h / 2;
				if (cX >= 95 && cY <= 95 && cX < 400)
					moveRight();
				else if (cX >= 360 && cY <= 400)
					moveDown();
				else if (cX > 55 && cY > 360)
					moveLeft();
				else
					moveUp();
				System.out.println("move");
			}
			try {
				Thread.sleep(500 / tempo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}

	/**
	 * Bewegt den Zug nach rechts.
	 */
	public void moveRight() {
		x = cX - w / 2;
		y = cY - h / 2;
		if (getDirection().equals("forward")) {
			if (cX > 360) {
				rotation = rotation + rotationChange;
				x = x + stepSize;
				y = y + stepSize;
			} else {
				x = x + stepSize;
			}
			cX = x + w / 2;
			cY = y + h / 2;
			repaint();
		} else if (getDirection().equals("backward")) {
			if (cX > 360 && cX <= 400) {
				rotation = rotation - rotationChange;
				x = x + stepSize;
				y = y - stepSize;
			} else {
				x = x + stepSize;
			}
			cX = x + w / 2;
			cY = y + h / 2;
			repaint();
		}
		System.out.println("Right");

	}

	/**
	 * Bewegt den Zug nach unten.
	 */
	public void moveDown() {
		x = cX - w / 2;
		y = cY - h / 2;
		if (getDirection().equals("forward")) {
			if (cY >= 360) {
				rotation = rotation + rotationChange;
				x = x - stepSize;
				y = y + stepSize;
			} else
				y = y + stepSize;
			cX = x + w / 2;
			cY = y + h / 2;
			repaint();
		} else if (getDirection().equals("backward")) {
			if (cY >= 360) {
				rotation = rotation - rotationChange;
				x = x + stepSize;
				y = y + stepSize;
			} else
				y = y + stepSize;
			cX = x + w / 2;
			cY = y + h / 2;
			repaint();
		}
		
		System.out.println("Down");
	}

	/**
	 * Bewegt den Zug nach links.
	 */
	public void moveLeft() {
		x = cX - w / 2;
		y = cY - h / 2;
		if (getDirection().equals("forward")) {
			if (cX <= 95) {
				rotation = rotation + rotationChange;
				x = x - stepSize;
				y = y - stepSize;
			} else
				x = x - stepSize;
			cX = x + w / 2;
			cY = y + h / 2;
		} else if (getDirection().equals("backward")) {
			if (cX <= 95) {
				rotation = rotation - rotationChange;
				x = x - stepSize;
				y = y + stepSize;
			} else
				x = x - stepSize;
			cX = x + w / 2;
			cY = y + h / 2;
		}
		repaint();
		System.out.println("Left");
	}

	/**
	 * Bewegt den Zug nach Oben.
	 */
	public void moveUp() {
		x = cX - w / 2;
		y = cY - h / 2;
		if (getDirection().equals("forward")) {
			if (cY < 95) {
				rotation = rotation + rotationChange;
				x = x + stepSize;
				y = y - stepSize;
			} else
				y = y - stepSize;
			cX = x + w / 2;
			cY = y + h / 2;
		} else if (getDirection().equals("backward")) {
			if (cY < 95 && cY >= 55) {
				rotation = rotation - rotationChange;
				x = x - stepSize;
				y = y - stepSize;
			} else
				y = y - stepSize;
			cX = x + w / 2;
			cY = y + h / 2;
		}
		repaint();
		System.out.println("Up");
	}

	/**
	 * Run-Methode. Wird aufgerufen, sobald eine Runnable-Instant der Klasse Zug
	 * angestossen wird. Bewegt den Zug.
	 */
	@Override
	public void run() {
		while (true) {
			moveTrain();
		}

	}
}
