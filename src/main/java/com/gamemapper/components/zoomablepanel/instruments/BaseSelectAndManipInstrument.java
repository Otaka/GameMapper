package com.gamemapper.components.zoomablepanel.instruments;

import com.gamemapper.components.zoomablepanel.GraphicZ2d;
import com.gamemapper.components.zoomablepanel.ZoomableComponent;
import com.gamemapper.components.zoomablepanel.ZoomablePanel;
import com.gamemapper.components.zoomablepanel.instruments.sub.BaseSubInstrument;
import com.gamemapper.components.zoomablepanel.instruments.sub.ManipulatorsSubInstrument;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;

/**
 * @author Dmitry
 */
public class BaseSelectAndManipInstrument extends BaseZoomPanInstrument {
    private boolean componentDragged=false;
    private int lastX;
    private int lastY;
    private ZoomableComponent selectedComponent;
    private final ManipulatorsSubInstrument manipulatorSubInstrument;
    private final List<BaseSubInstrument> subInstruments = new ArrayList<>();

    public BaseSelectAndManipInstrument(ZoomablePanel panel) {
        super(panel);
        manipulatorSubInstrument = new ManipulatorsSubInstrument(panel);
        addSubInstrument(manipulatorSubInstrument);
    }

    public ZoomableComponent getSelectedComponent() {
        return selectedComponent;
    }

    public void removeSubInstrument(BaseSubInstrument instrument) {
        subInstruments.remove(instrument);
        instrument.onDeactivateInstrument();
    }

    public final void addSubInstrument(BaseSubInstrument instrument) {
        instrument.setParentInstrument(this);
        subInstruments.add(instrument);
        instrument.onActivateInstrument();
    }

    @Override
    public boolean onDoubleClick(int x, int y, int innerX, int innerY, int button) {
        for (int i = subInstruments.size() - 1; i >= 0; i--) {
            if (!subInstruments.get(i).onDoubleClick(x, y, innerX, innerY, button)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onMousePressed(int x, int y, int innerX, int innerY, int button) {
        for (int i = subInstruments.size() - 1; i >= 0; i--) {
            if (!subInstruments.get(i).onMousePressed(x, y, innerX, innerY, button)) {
                return false;
            }
        }
        if (button == 1) {
            if (select(innerX, innerY)) {
                componentDragged=true;
                return false;
            }
        }

        super.onMousePressed(x, y, innerX, innerY, button);
        return true;
    }

    @Override
    public boolean onMouseMove(int x, int y, int innerX, int innerY, boolean drag) {
        for (int i = subInstruments.size() - 1; i >= 0; i--) {
            if (!subInstruments.get(i).onMouseMove(x, y, innerX, innerY, drag)) {
                return false;
            }
        }

        if (componentDragged && selectedComponent != null) {
       // if (drag && selectedComponent != null) {
            manipulatorSubInstrument.removeManipulators();
            int deltaX = innerX - lastX;
            int deltaY = innerY - lastY;
            selectedComponent.move(deltaX, deltaY);
            panel.repaint();
        }
        super.onMouseMove(x, y, innerX, innerY, drag);
        lastX = innerX;
        lastY = innerY;
        return true;
    }

    @Override
    public boolean onMouseReleased(int x, int y, int innerX, int innerY, int button) {
        super.onMouseReleased(x, y, innerX, innerY, button);
        componentDragged=false;
        for (int i = subInstruments.size() - 1; i >= 0; i--) {
            if (!subInstruments.get(i).onMouseReleased(x, y, innerX, innerY, button)) {
                return false;
            }
        }
        panel.repaint();
        return true;
    }

    public void deselect() {
        selectedComponent = null;
        manipulatorSubInstrument.removeManipulators();
    }

    private boolean select(int x, int y) {
        deselect();
        for (int i = panel.getZoomableComponents().size() - 1; i >= 0; i--) {
            ZoomableComponent component = panel.getZoomableComponents().get(i);
            if (component.isSelectable() && component.containsPoint(x, y)) {
                select(component);
                return true;
            }
        }

        return false;
    }

    public void select(ZoomableComponent component) {
        selectedComponent = component;
        manipulatorSubInstrument.recreateManipulatorsForSelectedComponent();
        panel.repaint();
    }

    @Override
    public void onDraw(GraphicZ2d g) {
        for (int i = subInstruments.size() - 1; i >= 0; i--) {
            subInstruments.get(i).onDraw(g);
        }
    }

    private void deleteItem() {
        if (selectedComponent == null) {
            return;
        }
        if (panel.deleteZoomableComponent(selectedComponent)) {
            deselect();
        }
    }

    @Override
    public List<JMenuItem> createPopupMenuItems() {
        List<JMenuItem> menuItems = new ArrayList<>();
        for (int i = subInstruments.size() - 1; i >= 0; i--) {
            menuItems.addAll(subInstruments.get(i).createPopupMenuItems());
        }
        if (selectedComponent == null) {
            return menuItems;
        }

        /*if (selectedComponent instanceof PolygonZoomableComponent) {
            JMenuItem attachNodesToRoomInstrument = new JMenuItem("Attach nodes to polygon");
            attachNodesToRoomInstrument.addActionListener((ActionEvent event) -> {
                addSubInstrument(new AttachNodeSubInstrument(this, panel));
            });
            menuItems.add(attachNodesToRoomInstrument);
        }*/
        if (selectedComponent != null) {
            JMenuItem deleteMenuItem = new JMenuItem("Delete");
            deleteMenuItem.addActionListener((ActionEvent event) -> {
                deleteItem();
            });
            menuItems.add(deleteMenuItem);
        }
        return menuItems;
    }

    public void fireSelectedObjectModified() {
        for (int i = subInstruments.size() - 1; i >= 0; i--) {
            subInstruments.get(i).onSelectedObjectModified();
        }
        panel.repaint();
    }
}
