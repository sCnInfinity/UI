import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class functions as a train that can be controlled by commands via the
 * ControlPanel.
 * 
 * @author Lucas Groß-Hardt
 */
public class Train extends JPanel implements Runnable {
	private Controller controller;
	/** Name of the train */
	private String name;
	/** False = light turned off, true = light turned on */
	private boolean light;
	/** to check, whether train is moving forward */
	private boolean forward;
	/** to check, whether train is moving backward */
	private boolean backward;
	/** movement speed of the train. Min. 0 ,max. 200 */
	private int tempo;
	/** Number to match a train to configuration windows, chargers or counter */
	private int index;
	/** Battery capacity of a train. Default is 100 %. */
	private int battery = 100;
	/** to check, whether train is running */
	private boolean running;
	/** to check, whether train is charging */
	private boolean charging;
	private boolean poweredByBattery;
	private String imagePath;
	private double rotation = 0;
	private double rotationChange = 4.5;
	private int stepSize = 2;

	private int x = 75;
	private int y = 23;
	private int w = 60;
	private int h = 60;

	private int cX = 0;
	private int cY = 0;

	/**
	 * Constructor method. Sets name, index and direction. Default direction:
	 * forward
	 * 
	 * @param name
	 *            Name of the train.
	 * @param index
	 *            Number to match a train to configuration windows, chargers or
	 *            counter
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

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g);
		AffineTransform old = g2d.getTransform();
		g2d.rotate(Math.toRadians(rotation), w / 2, h / 2);
		g2d.setColor(TrackView.getColors(index));
		g2d.fillRect(12, 26, 30, 8);
		if (light)
			g2d.drawImage(new ImageIcon(getClass().getResource("trainlight.png")).getImage(), 5, 25, 50, 10, this);
		else
			g2d.drawImage(new ImageIcon(getClass().getResource("train.png")).getImage(), 5, 25, 50, 10, this);
		g2d.setTransform(old);
		repaint();
	}

	/**
	 * Sets the name for a train and writes changes to the temporary log
	 * display. Also updates the train selection list.
	 * 
	 * @param name
	 *            Name of the train.
	 * @param oldName
	 *            Old name of the train for comparison purposes.
	 * @category Setter
	 */
	public void setName(String name, String oldName) {
		this.name = name;
		// Only display name changes if the old name differs from the new one
		if (!oldName.equals(name)) {
			controller.getLogView().updateLog("Name geändert: " + oldName + " --> " + name + "( index " + index + ")");
		}
	}

	public void setBatteryMode(boolean mode) {
		poweredByBattery = mode;
		if (poweredByBattery)
			controller.getLogView().updateLog(name + " wird nun per Batterie betrieben.");
		else
			controller.getLogView().updateLog(name + " ist nun ans Stromnetz angeschlossen.");
	}

	public boolean isBatteryPowered() {
		return poweredByBattery;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String path) {
		imagePath = path;
	}

	/**
	 * Returns the lifetime remaining for a trains battery
	 * 
	 * @return Battery lifetime
	 * @category Getter
	 */
	public int getBatteryLifeTime() {
		return battery;
	}

	/**
	 * Sets the battery lifetime for a train
	 * 
	 * @param battery
	 *            Battery lifetime for a train
	 * @category Setter
	 */
	public void setBatteryLifeTime(int battery) {
		this.battery = battery;
	}

	/**
	 * Returns the name of a train.
	 * 
	 * @return Name of the train
	 * @category Getter
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the direction of a train. Also writes changes to the temporary log
	 * display.
	 * 
	 * @param direction
	 *            Direction in which a train is headed.
	 * @category Setter
	 */
	public void setDirection(String direction) {
		if (direction.equals("forward")) {
			forward = true;
			backward = false;
			controller.getLogView().updateLog(name + " fährt nun vorwärts ");
		} else if (direction.equals("backward")) {
			backward = true;
			forward = false;
			controller.getLogView().updateLog(name + " fährt nun rückwärts ");
		}
	}

	/**
	 * Sets a train to charging or not charging.
	 * 
	 * @param charging
	 *            To check if a train is charging.
	 * @category Setter
	 */
	public void setCharging(boolean charging) {
		this.charging = charging;
	}

	/**
	 * Returns whether a train is charging or not.
	 * 
	 * @return Charging state
	 * @category Getter
	 */
	public boolean isCharging() {
		return charging;
	}

	/**
	 * Sets the state of the train to running or not running. Also write updates
	 * to the temporary log display.
	 * 
	 * @param running
	 *            To check if the train is running or not
	 * @category Setter
	 */
	public void setRunning(boolean running) {
		if (battery < 1 && !this.running && running)
			controller.getLogView().updateLog(name + ": Nicht ausreichend geladen.");
		else
			this.running = running;
	}

	/**
	 * Return the state of the train. Either running (true) or not (false)
	 * 
	 * @return Running state
	 * @category Getter
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Returns the direction in which the train is headed.
	 * 
	 * @return Direction of the train
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
	 * Turns the light on or off. True for on, false for off.
	 * 
	 * @param status
	 *            Status to set for a trains light
	 * @category Setter
	 */
	public void setLightOn(boolean status) {
		// temporary variable to check for status changes.
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
			// only display log info if the state is changed
			if (!(tmp == status)) {
				controller.getLogView().updateLog(name + ": Licht aus");
				light = status;
			}
		}
	}

	/**
	 * Returns whether the light is turned on or off. True = On, false = Off
	 * 
	 * @return Light status
	 * @category Getter
	 */
	public boolean lightIsOn() {
		return light;
	}

	/**
	 * Sets the movement speed for a train. Also writes updates to the temporary
	 * log file.
	 * 
	 * @param tempo
	 *            Movement speed of a train
	 * @category Setter
	 */
	public void setTempo(int tempo) {
		this.tempo = tempo;
		if (battery > 1) {
			controller.getLogView().updateLog(name + " fährt jetzt mit einer Geschwindigkeit von " + tempo + " km/h.");
		}
	}

	/**
	 * Returns the movement speed of a train.
	 * 
	 * @return Movement speed of a train
	 * @category Getter
	 */
	public int getTempo() {
		return tempo;
	}

	public int[] getPositionParameters() {
		return new int[] { x, y, w, h };
	}

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
			}
			try {
				Thread.sleep(500 / tempo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

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

	}

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
	}

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
	}

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
	}

	@Override
	public void run() {
		while (true) {
			moveTrain();
		}

	}
}
