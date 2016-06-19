import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

public class TrackDraw extends JComponent {

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRoundRect(39, 35, 379, 383, 120, 120);
		g.drawRoundRect(61, 60, 333, 333, 100, 100);
		g.setColor(Color.BLACK);

	}
}