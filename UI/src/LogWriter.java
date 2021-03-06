import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Diese Klasse ist ausfuehrbar und speichert im Hintergrund den Inhalt des
 * Log-Fensters regelmaessig in eine Textdatei.
 * 
 * @author Lucas
 * @category Worker
 */
public class LogWriter implements Runnable {
	/** Controller-Instanz */
	private Controller con;

	/**
	 * Konstruktor. Setzt einen Wert fuer die Controller-Instanz.
	 * 
	 * @param con
	 *            Controller-Instanz
	 * @category Constructor
	 */
	public LogWriter(Controller con) {
		this.con = con;
	}

	/**
	 * Run-Methode. Wird ausgefuehrt, wenn ein Thread dieser Klasse ausgefuehrt
	 * wird.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e3) {
				e3.printStackTrace();
			}
			String textToSave = con.getLogView().getLogFile().getText();
			String[] parts = textToSave.split("\\n");
			Path path = Paths.get("C:/Users/Lucas/Desktop/Log.txt");
			try {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			} catch (FileAlreadyExistsException e) {
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				PrintWriter writer = new PrintWriter("C:/Users/Lucas/Desktop/Log.txt");
				for (int i = 0; i < parts.length; i++) {
					if (i == 0)
						writer.print(parts[i]);
					else
						writer.println(parts[i]);
				}
				writer.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}
}
