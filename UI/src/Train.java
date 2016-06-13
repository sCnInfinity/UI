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
	private boolean isFinishedUp;
	private boolean isFinishedDown;
	private boolean isFinishedRight;
	private boolean isFinishedLeft;
	private boolean isFinishedUpBack;
	private boolean isFinishedDownBack;
	private boolean isFinishedRightBack;
	private boolean isFinishedLeftBack;
	private boolean charging;
	private String imagePath;
	private double rotation = 0;

	private int x = 75;
	private int y = 25;
	// private int w = 50;
	// private int h = 10;
	private int w = 60;
	private int h = 60;

	private int stepSize = 2;
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
	public Train(String name, int index) {
		try {
			this.index = index;
			String[] data = Controller.readDataOnOpen(this.index);
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
		g2d.drawRoundRect(10, 20, 50, 10, w / 2, h / 2);
		g2d.fillRoundRect(10, 20, 50, 10, w / 2, h / 2);

		g2d.setTransform(old);
		// if (light) {
		// g.setColor(Color.YELLOW);
		// if (lastStepUp || lastStepLeft) {
		// g2d.drawRoundRect(3, 3, 4, 4, 1, 1);
		// g2d.fillRoundRect(3, 3, 4, 4, 1, 1);
		// } else if (lastStepRight) {
		// g2d.drawRoundRect(w - 8, h - 7, 4, 4, 1, 1);
		// g2d.fillRoundRect(w - 8, h - 7, 4, 4, 1, 1);
		// } else {
		// g2d.drawRoundRect(w - 7, h - 8, 4, 4, 1, 1);
		// g2d.fillRoundRect(w - 7, h - 8, 4, 4, 1, 1);
		// }
		//
		// }
		g.setColor(Color.BLACK);
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
			Controller.getLogView().updateLog("Name geändert: " + oldName + " --> " + name + "( index " + index + ")");
			Controller.setTextListOfTrains();
		}
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
			Controller.getLogView().updateLog(name + " fährt nun vorwärts ");
		} else if (direction.equals("backward")) {
			backward = true;
			forward = false;
			Controller.getLogView().updateLog(name + " fährt nun rückwärts ");
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
			Controller.getLogView().updateLog(name + ": Nicht ausreichend geladen.");
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
				Controller.getLogView().updateLog(name + ": Licht an");
				light = status;
			} else {
				Controller.getLogView().updateLog(name + ": Nicht ausreichend geladen.");
				light = false;
			}
		} else {
			// only display log info if the state is changed
			if (!(tmp == status)) {
				Controller.getLogView().updateLog(name + ": Licht aus");
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
			Controller.getLogView().updateLog(name + " fährt jetzt mit einer Geschwindigkeit von " + tempo + " km/h.");
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

				if (!isFinishedRightBack) {
					moveRight();
				} else if (!isFinishedUpBack)
					moveUp();
				else if (isFinishedUpBack && !isFinishedLeftBack) {
					moveLeft();
				} else
					moveDown();

			}
			if (getDirection().equals("forward")) {
				cX = x + w / 2;
				cY = y + h / 2;
				isFinishedUp = true;
				if (isFinishedUp && !isFinishedRight && !isFinishedLeft)
					moveRight();
				else if (isFinishedRight && !isFinishedDown)
					moveDown();
				else if (isFinishedDown && !isFinishedLeft)
					moveLeft();
				else
					moveUp();
			}
			try {
				Thread.sleep(500 / tempo);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void moveRight() {
		x = cX - w / 2;
		y = cY - h / 2;
		if (getDirection().equals("forward")) {
			if (cX > 360) {
				rotation = rotation + 4.5;
				x = x + stepSize;
				y = y + stepSize;
			} else {
				x = x + stepSize;
			}
			cX = x + w / 2;
			cY = y + h / 2;
			if (cX > 400)
				isFinishedRight = true;
			repaint();
		} else if (getDirection().equals("backward")) {
			if (cX > 360 && cX <= 400) {
				rotation = rotation - 4.5;
				x = x + stepSize;
				y = y - stepSize;
			} else {
				x = x + stepSize;
			}
			cX = x + w / 2;
			cY = y + h / 2;
			if (cX > 400)
				isFinishedRightBack = true;
			repaint();
		}

	}

	public void moveDown() {
		isFinishedDown = false;
		x = cX - w / 2;
		y = cY - h / 2;
		if (cY >= 360) {
			rotation = rotation + 4.5;
			x = x - stepSize;
			y = y + stepSize;
		} else
			y = y + stepSize;
		cX = x + w / 2;
		cY = y + h / 2;
		if (cY > 400)
			isFinishedDown = true;
		repaint();
	}

	public void moveLeft() {
		isFinishedUp = false;
		x = cX - w / 2;
		y = cY - h / 2;
		if(getDirection().equals("forward")){
			if (cX <= 95) {
				rotation = rotation + 4.5;
				x = x - stepSize;
				y = y - stepSize;
			} else
				x = x - stepSize;
			cX = x + w / 2;
			cY = y + h / 2;
			if (cX <= 55) {
				isFinishedLeft = true;
			}
		}
		else if(getDirection().equals("backward")){
			if (cX <= 95) {
				rotation = rotation - 4.5;
				x = x - stepSize;
				y = y + stepSize;
			} else
				x = x - stepSize;
			cX = x + w / 2;
			cY = y + h / 2;
			if (cX <= 55) {
				isFinishedLeftBack = true;
			}
		}
		
		repaint();
	}

	public void moveUp() {
		isFinishedRight = false;
		isFinishedUp = false;
		isFinishedDown = false;
		x = cX - w / 2;
		y = cY - h / 2;
		if (getDirection().equals("forward")) {
			if (cY < 95) {
				rotation = rotation + 4.5;
				x = x + stepSize;
				y = y - stepSize;
			} else
				y = y - stepSize;
			cX = x + w / 2;
			cY = y + h / 2;
			if (cX >= 95) {
				isFinishedUp = true;
				isFinishedRight = false;
				isFinishedLeft = false;
			}
		} else if (getDirection().equals("backward")) {
			if (cY < 96) {
				rotation = rotation - 4.5;
				x = x - stepSize;
				y = y - stepSize;
			} else
				y = y - stepSize;
			cX = x + w / 2;
			cY = y + h / 2;
			if (cY <= 55) {
				isFinishedUpBack = true;
				isFinishedLeftBack = false;
			}
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
