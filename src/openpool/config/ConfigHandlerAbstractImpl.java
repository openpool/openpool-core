package openpool.config;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

public abstract class ConfigHandlerAbstractImpl implements ConfigHandler {

	@Override
	public String getTitle() {
		return "";
	}

	@Override
	public void draw() {
	}

	@Override
	public void mouseEvent(MouseEvent e) {
	}

	@Override
	public void keyEvent(KeyEvent e) {
	}
}
