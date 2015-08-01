import java.awt.Frame;
public class Window extends Frame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1324363758675184283L;
	// Create a frame inside the window
	Window () {
		setTitle("Dots");
		setSize(800, 600);
		setLocation(100, 100);
		setResizable(false);
		add(new Field());
		setVisible(true);
	}
}
