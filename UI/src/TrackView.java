import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Diese Klasse implementiert die Visualisierung der Zuege auf der Bahnstrecke.
 * Durch die Implementierung einer Runnable-Schnittstelle sind Instanzen der
 * Klasse ausfuehrbar.
 * 
 * @author Lucas
 * @category View
 */
public class TrackView extends JFrame implements Runnable {
	/** Controller-Instanz */
	private Controller con;
	/** ArrayList mit allen Farben fuer Zuege */
	private ArrayList<Color> colors = new ArrayList<>();
	/** ArrayList mit allen BilderLabeln fuer Zuege */
	private ArrayList<JLabel> labelsImg = new ArrayList<>();
	/** ArrayList mit allen Namenslabeln fuer Zuege */
	private ArrayList<JLabel> labelsName = new ArrayList<>();
	/** ArrayList mit allen FarbLabels fuer Zuege */
	private ArrayList<JLabel> labelsColor = new ArrayList<>();
	/** ArrayList mit allen ZugPanels */
	private ArrayList<JPanel> panelsTrains = new ArrayList<>();
	/** Rechtes Panel, das alle Zugpanels beinhaltet */
	private JPanel panelRight = new JPanel();
	/** Linkes Panel. Beinhaltet die fahrenden Zuege und die Strecke. */
	private JPanel panelLeft = new JPanel();

	/**
	 * Konstruktor. Erzeugt eine neue Instanz der Klasse TrackView und setzt die
	 * Controller-Instanz.
	 * 
	 * @param controller
	 *            Controller-Instanz
	 * @category Constructor
	 */
	public TrackView(Controller controller) {
		con = controller;
	}

	/**
	 * Gibt die ArrayList aller Farben zurueck, die fuer Zuege verwendet werden.
	 * 
	 * @return ArrayList Zugfarben
	 * @category Getter
	 */
	public ArrayList<Color> getColors() {
		return colors;
	}

	/**
	 * Gibt die ArrayList zurueck, in der alle BilderLabels gespeichert sind.
	 * 
	 * @return ArrayList BilderLabels
	 * @category Getter
	 */
	public ArrayList<JLabel> getLabelsImg() {
		return labelsImg;
	}

	/**
	 * Gibt die ArrayList zurueck, in der alle Namenslabels gespeichert sind.
	 * 
	 * @return ArrayList NamensLabels
	 * @category Getter
	 */
	public ArrayList<JLabel> getLabelsName() {
		return labelsName;
	}

	/**
	 * Gibt die ArrayList zurueck, in der alle Farblabels gespeichert sind.
	 * 
	 * @return ArrayList Farblabels
	 * @category Getter
	 */
	public ArrayList<JLabel> getLabelsColor() {
		return labelsColor;
	}

	/**
	 * Gibt die ArrayList zurueck, in der alle Zugpanels gespeichert sind.
	 * 
	 * @return ArrayList Zugpanels
	 * @category Getter
	 */
	public ArrayList<JPanel> getListPanelsTrains() {
		return panelsTrains;
	}

	/**
	 * Gibt das linke Panel zurueck, das die Zuege und die Strecke beinhaltet.
	 * 
	 * @return Linkes Panel
	 * @category Getter
	 */
	public JPanel getPanelTrains() {
		return panelLeft;
	}

	/**
	 * Gibt das rechte Panel zurueck, das alle Zugpanels beinhaltet.
	 * 
	 * @return Rechtes Panel
	 * @category Getter
	 */
	public JPanel getPanelRight() {
		return panelRight;
	}

	/**
	 * Run-Methode. Wird ausgefuehrt, wenn eine Runnable-Instanz dieser Klasse
	 * angestossen wird.
	 */
	@Override
	public void run() {
		for (int i = 0; i < con.getListOfTrains().size(); i++) {
			if (i == 0)
				colors.add(Color.BLUE);
			else if (i == 1)
				colors.add(Color.BLACK);
			else if (i == 2)
				colors.add(Color.GREEN);
			else if (i == 3)
				colors.add(Color.RED);
		}

		panelLeft.setLayout(null);

		setTitle("Modelleisenbahn: Smulation");
		setResizable(false);
		setSize(600, 600);
		setMinimumSize(new Dimension(600, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		TrackDraw tDraw = new TrackDraw();
		panelLeft.add(tDraw);

		panelsTrains.add(new JPanel(new GridLayout(1, 3, 5, 5)));

		labelsName.add(new JLabel(con.getListOfTrains().get(0).getName() + " "));
		panelsTrains.get(0).add(labelsName.get(0));

		ImageIcon icon = new ImageIcon(con.getListOfTrains().get(0).getImagePath());
		Image img = icon.getImage();
		img = img.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		labelsImg.add(new JLabel(icon));
		panelsTrains.get(0).add(labelsImg.get(0));

		labelsColor.add(new JLabel());
		labelsColor.get(0).setBackground(colors.get(0));
		labelsColor.get(0).setOpaque(true);
		panelsTrains.get(0).add(labelsColor.get(0));

		for (int j = 0; j < con.getListOfTrains().size(); j++) {
			panelLeft.add(con.getListOfTrains().get(j));
			new Thread(con.getListOfTrains().get(j)).start();
		}
		getContentPane().add(panelLeft);
		panelRight.setLayout(new GridLayout(con.getListOfTrains().size(), 1));
		panelRight.add(panelsTrains.get(0));
		getContentPane().add(panelRight, BorderLayout.EAST);
		pack();

		for (int i = 1; i < con.getListOfTrains().size(); i++) {
			panelsTrains.add(new JPanel(new GridLayout(1, 3, 5, 5)));
			labelsName.add(new JLabel(con.getListOfTrains().get(i).getName() + " "));
			panelsTrains.get(i).add(labelsName.get(i));

			labelsImg.add(new JLabel(new ImageIcon(new ImageIcon(con.getListOfTrains().get(i).getImagePath()).getImage()
					.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH))));
			panelsTrains.get(i).add(labelsImg.get(i));

			labelsColor.add(new JLabel());
			labelsColor.get(i).setBackground(colors.get(i));
			labelsColor.get(i).setOpaque(true);
			panelsTrains.get(i).add(labelsColor.get(i));
			panelRight.add(panelsTrains.get(i));
		}

		tDraw.setBounds(0, 0, panelLeft.getWidth(), panelLeft.getHeight());
		while (true) {
			for (int i = 0; i < labelsColor.size(); i++) {
				int x = con.getListOfTrains().get(i).getPositionParameters()[0];
				int y = con.getListOfTrains().get(i).getPositionParameters()[1];
				int w = con.getListOfTrains().get(i).getPositionParameters()[2];
				int h = con.getListOfTrains().get(i).getPositionParameters()[3];
				con.getListOfTrains().get(i).setBounds(x, y, w, h);
				con.getListOfTrains().get(i).setOpaque(false);
			}
		}
	}
}

/**
 * Diese Klasse zeichnet die Strecke, auf der die Zuege fahren.
 * 
 * @author Lucas
 * @category View
 */
class TrackDraw extends JComponent {

	/**
	 * Die paintComponent-Methode zeichnet die Strecke, auf der die Zuege fahren.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRoundRect(44, 40, 369, 373, 120, 120);
		g.drawRoundRect(66, 65, 323, 323, 100, 100);
		g.setColor(Color.BLACK);
	}
}