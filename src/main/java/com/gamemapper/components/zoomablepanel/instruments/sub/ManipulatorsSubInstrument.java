package com.gamemapper.components.zoomablepanel.instruments.sub;

import com.gamemapper.components.zoomablepanel.GraphicZ2d;
import com.gamemapper.components.zoomablepanel.Manipulator;
import com.gamemapper.components.zoomablepanel.ZoomablePanel;
import com.gamemapper.components.zoomablepanel.ZoomableComponent;
import com.gamemapper.components.zoomablepanel.instruments.BaseSelectAndManipInstrument;
import java.awt.Color;
import java.util.List;

/**
 * @author Dmitry
 */
public class ManipulatorsSubInstrument extends BaseSubInstrument {

    private int lastX;
    private int lastY;
    private int draggedManipulator = -1;
    private List<Manipulator> manipulators;

    public ManipulatorsSubInstrument(ZoomablePanel panel) {
        super(panel);
    }

    public void recreateManipulatorsForSelectedComponent() {
        ZoomableComponent component = parentInstrument.getSelectedComponent();
        if (component != null) {
            manipulators = component.createManipulators();
        } else {
            manipulators = null;
        }
    }

    public void removeManipulators() {
        manipulators = null;
    }

    @Override
    public boolean onMousePressed(int x, int y, int innerX, int innerY, int button) {
        if (button == 1) {
            if (selectManipulator(innerX, innerY)) {
                lastX = innerX;
                lastY = innerY;
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onMouseMove(int x, int y, int innerX, int innerY, boolean drag) {
        if (drag && draggedManipulator != -1) {
            int deltaX = innerX - lastX;
            int deltaY = innerY - lastY;
            lastX = innerX;
            lastY = innerY;
            parentInstrument.getSelectedComponent().onManipulatorMoved(manipulators, draggedManipulator, deltaX, deltaY);
            panel.repaint();
            return false;
        }

        return true;
    }

    @Override
    public boolean onMouseReleased(int x, int y, int innerX, int innerY, int button) {
        draggedManipulator = -1;
        recreateManipulatorsForSelectedComponent();
        return true;
    }

    private boolean selectManipulator(int x, int y) {
        if (manipulators == null) {
            return false;
        }
        for (int i = 0; i < manipulators.size(); i++) {
            Manipulator m = manipulators.get(i);
            if (x >= m.getX() - 3 && x < m.getX() + 3 && y >= m.getY() - 3 && y < m.getY() + 3) {
                draggedManipulator = i;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDraw(GraphicZ2d g) {
        if (manipulators != null) {
            for (Manipulator m : manipulators) {
                g.drawCenteredNonZoomableRectangle(m.getX(), m.getY(), 3, Color.GREEN, Color.WHITE);
            }
        }
    }

    @Override
    public void onSelectedObjectModified() {
        recreateManipulatorsForSelectedComponent();
    }
}
