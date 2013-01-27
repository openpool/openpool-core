package openpool.config;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

public interface ConfigHandler {
	public String getTitle();
	public void draw();
	public void mouseEvent(MouseEvent e);
	public void keyEvent(KeyEvent e);
}
