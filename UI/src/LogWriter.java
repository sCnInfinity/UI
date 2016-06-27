import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Diese Klasse funktioniert wie ein Dienst, der im Hintergrund läuft und den Inhalt des Log-Fensters regelmäßig in eine Textdatei speichert.
 * @author Lucas
 *
 */
public class LogWriter implements Runnable{
	private Controller con;
	/**
	 * Run-Methode. Wird ausgeführt, wenn ein Thread
	*/
	
	public LogWriter(Controller con){
		this.con = con;
	}
	
	@Override
	public void run() {
		while(true){
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
				for(int i = 0; i < parts.length; i ++){
					if(i == 0)writer.print(parts[i]);
					else writer.println(parts[i]);
				}
				writer.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		
	}

}
