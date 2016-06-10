import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class TrackView extends JFrame implements Runnable {
	private static Color[] colors;
	private String[] colorNames;
	private JLabel[] labelsImg;

	public TrackView() {

	}

	public static Color getColors(int index) {
		return colors[index];
	}

	public JLabel[] getLabelsImg() {
		return labelsImg;
	}

	@Override
	public void run() {
		colors = new Color[Controller.getListOfTrains().size()];
		for (int i = 0; i < Controller.getListOfTrains().size(); i++) {
			if (i > 3) {
				Random rand = new Random();
				float r = rand.nextFloat();
				float g = rand.nextFloat();
				float b = rand.nextFloat();
				colors[i] = new Color(r, g, b);
			} else if (i == 0)
				colors[i] = Color.BLUE;
			else if (i == 1)
				colors[i] = Color.BLACK;
			else if (i == 2)
				colors[i] = Color.GREEN;
			else if (i == 3)
				colors[i] = Color.RED;
		}

		TrackDraw tDraw = new TrackDraw();

		setTitle("Modelleisenbahn: Smulation");
		setResizable(false);
		setSize(600, 600);
		setMinimumSize(new Dimension(600, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		getContentPane().add(tDraw);

		JLabel[] labelsName = new JLabel[Controller.getListOfTrains().size()];
		labelsName[0] = new JLabel(Controller.getListOfTrains().get(0).getName() + " ");
		getContentPane().add(labelsName[0]);

		JLabel[] labelsColor = new JLabel[Controller.getListOfTrains().size()];
		labelsColor[0] = new JLabel();
		labelsColor[0].setBackground(colors[0]);
		labelsColor[0].setOpaque(true);
		getContentPane().add(labelsColor[0]);

		labelsImg = new JLabel[Controller.getListOfTrains().size()];
		ImageIcon icon = new ImageIcon(Controller.getListOfTrains().get(0).getImagePath());
		Image img = icon.getImage();
		img = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		labelsImg[0] = new JLabel(icon);
		getContentPane().add(labelsImg[0]);

		for (int j = 0; j < Controller.getListOfTrains().size(); j++) {
			getContentPane().add(Controller.getListOfTrains().get(j));
			new Thread(Controller.getListOfTrains().get(j)).start();
		}

		pack();
		setVisible(true);
		labelsName[0].setBounds(getContentPane().getWidth() - 120, 20, 100, 20);
		labelsColor[0].setBounds(labelsName[0].getX() + labelsName[0].getWidth() - 20, labelsName[0].getY(), 30, 20);
		labelsImg[0].setBounds(labelsName[0].getX() + labelsName[0].getWidth() - 50, labelsName[0].getY(), 30, 20);
		for (int i = 1; i < Controller.getListOfTrains().size(); i++) {
			labelsName[i] = new JLabel(Controller.getListOfTrains().get(i).getName() + " ");
			labelsName[i].setBounds(labelsName[i - 1].getX(), labelsName[i - 1].getY() + labelsName[i - 1].getHeight(),
					labelsName[i - 1].getWidth(), labelsName[i - 1].getHeight());
			getContentPane().add(labelsName[i]);

			labelsColor[i] = new JLabel();
			labelsColor[i].setBackground(colors[i]);
			labelsColor[i].setBounds(labelsColor[i - 1].getX(),
					labelsColor[i - 1].getY() + labelsColor[i - 1].getHeight(), labelsColor[i - 1].getWidth(),
					labelsColor[i - 1].getHeight());
			labelsColor[i].setOpaque(true);
			getContentPane().add(labelsColor[i]);

			labelsImg[i] = new JLabel(new ImageIcon(new ImageIcon(Controller.getListOfTrains().get(i).getImagePath()).getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)));
			labelsImg[i].setBounds(labelsImg[i - 1].getX(), labelsImg[i - 1].getY() + labelsImg[i - 1].getHeight(),
					labelsImg[i - 1].getWidth(), labelsImg[i - 1].getHeight());
			getContentPane().add(labelsImg[i]);
		}

		tDraw.setBounds(0, 0, getContentPane().getWidth(), getContentPane().getHeight());
		while (true) {
			for (int i = 0; i < Controller.getListOfTrains().size(); i++) {
				int x = Controller.getListOfTrains().get(i).getPositionParameters()[0];
				int y = Controller.getListOfTrains().get(i).getPositionParameters()[1];
				int w = Controller.getListOfTrains().get(i).getPositionParameters()[2];
				int h = Controller.getListOfTrains().get(i).getPositionParameters()[3];
				Controller.getListOfTrains().get(i).setBounds(x, y, w, h);
				Controller.getListOfTrains().get(i).setOpaque(false);
				labelsName[i].setText(Controller.getListOfTrains().get(i).getName());
			}
		}

	}
}