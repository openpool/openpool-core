package openpool.config;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

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
