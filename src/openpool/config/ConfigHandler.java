package openpool.config;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface ConfigHandler {
    public String getTitle();

    public void draw();

    public void mouseEvent(MouseEvent e);

    public void keyEvent(KeyEvent e);
}
