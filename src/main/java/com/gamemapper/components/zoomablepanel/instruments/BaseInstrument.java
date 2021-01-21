package com.gamemapper.components.zoomablepanel.instruments;

import com.gamemapper.components.zoomablepanel.GraphicZ2d;
import com.gamemapper.components.zoomablepanel.ZoomablePanel;
import java.util.Collections;
import java.util.List;
import javax.swing.JMenuItem;

/**
 * @author Dmitry
 */
public abstract class BaseInstrument {

    protected ZoomablePanel panel;

    public BaseInstrument(ZoomablePanel panel) {
        this.panel = panel;
    }

    public void onActivateInstrument() {
    }

    public void onDeactivateInstrument() {
    }

    public boolean onMouseMove(int x, int y, int innerX, int innerY, boolean drag) {
        return true;
    }

    public boolean onMouseWheel(int rotatedWheel) {
        return true;
    }

    public boolean onMousePressed(int x, int y, int innerX, int innerY, int button) {
        return true;
    }

    public boolean onMouseReleased(int x, int y, int innerX, int innerY, int button) {
        return true;
    }

    public boolean onDoubleClick(int x, int y, int innerX, int innerY, int button) {
        return true;
    }

    public boolean onKeyPressed(int key) {
        return true;
    }

    public boolean onKeyReleased(int key) {
        return true;
    }

    public List<JMenuItem> createPopupMenuItems() {
        return Collections.EMPTY_LIST;
    }

    public void onDraw(GraphicZ2d g) {

    }
}
