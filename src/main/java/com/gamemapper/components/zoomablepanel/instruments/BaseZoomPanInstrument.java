package com.gamemapper.components.zoomablepanel.instruments;

import com.gamemapper.components.zoomablepanel.ZoomablePanel;
import java.awt.Cursor;

/**
 * @author Dmitry
 */
public class BaseZoomPanInstrument extends BaseInstrument {

    private int lastX;
    private int lastY;
    private int lastInnerX;
    private int lastInnerY;
    private boolean pan = false;
    private Cursor lastCursor;

    public BaseZoomPanInstrument(ZoomablePanel panel) {
        super(panel);
    }

    @Override
    public boolean onMousePressed(int x, int y, int innerX, int innerY, int button) {
        if (button == 2) {
            lastCursor = panel.getCursor();
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            lastX = x;
            lastY = y;
            pan = true;
        }
        return true;
    }

    @Override
    public boolean onMouseReleased(int x, int y, int innerX, int innerY, int button) {
        if (button == 2) {
            pan = false;
            panel.setCursor(lastCursor);
        }
        return true;
    }

    @Override
    public boolean onMouseMove(int x, int y, int innerX, int innerY, boolean drag) {
        if (pan) {
            int deltaX = x - lastX;
            int deltaY = y - lastY;
            panel.setScrollX(panel.getScrollX() + deltaX);
            panel.setScrollY(panel.getScrollY() + deltaY);
            panel.repaint();
        }
        lastX = x;
        lastY = y;
        lastInnerX = innerX;
        lastInnerY = innerY;
        return true;
    }

    @Override
    public boolean onMouseWheel(int rotatedWheel) {
        float zoomLevel = panel.getZoomLevel();
        float previousZoomLevel = zoomLevel;
        zoomLevel += rotatedWheel / 30.f;
        if (zoomLevel < 0.05) {
            zoomLevel = 0.05f;
        }
        panel.setZoomLevel(zoomLevel);

        //correct scroll to center zooming on mouse cursor
        //to do this I calculate where cursor points before zoom, then where it points after zoom, and adjust scrolling on difference
        int _dx1 = Math.round (lastInnerX / previousZoomLevel + panel.getScrollX());
        int _dy1 = Math.round (lastInnerY / previousZoomLevel + panel.getScrollY());

        int _dx2 = Math.round (lastInnerX / zoomLevel + panel.getScrollX());
        int _dy2 = Math.round (lastInnerY / zoomLevel + panel.getScrollY());
        panel.setScrollX(panel.getScrollX() + (_dx1 - _dx2));
        panel.setScrollY(panel.getScrollY() + (_dy1 - _dy2));

        panel.repaint();
        return true;
    }
}
