import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

public class TrackDraw extends JComponent {

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		g.drawRoundRect(69, 50, 336, 356, 20, 20);
//		g.drawRoundRect(79, 60, 316, 336, 20, 20);
		g.setColor(Color.BLACK);

	}
}