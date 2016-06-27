import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

public class TrackDraw extends JComponent {

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRoundRect(44, 40, 369, 373, 120, 120);
		g.drawRoundRect(66, 65, 323, 323, 100, 100);
		g.setColor(Color.BLACK);
	}
}