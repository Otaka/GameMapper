package com.gamemapper.components.zoomablepanel;

import com.gamemapper.components.zoomablecomponents.ArrowComponent;
import com.gamemapper.components.zoomablecomponents.BackgroundComponent;
import com.gamemapper.components.zoomablecomponents.MarkerComponent;
import com.gamemapper.components.zoomablecomponents.RoomComponent;
import com.gamemapper.components.zoomablepanel.instruments.BaseInstrument;
import com.gamemapper.components.zoomablepanel.instruments.BaseSelectAndManipInstrument;
import com.gamemapper.data.SerializationContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * @author Dmitry
 */
public class ZoomablePanel extends JPanel {

    private final List<ZoomableComponent> components = new ArrayList<>();
    private boolean dirty = true;
    private float zoomLevel = 1;
    private float scrollX = 0;
    private float scrollY = 0;
    private final GraphicZ2d graphics = new GraphicZ2d();
    private final Stack<BaseInstrument> instruments = new Stack<>();
    private RenderingHints renderingHint;
    private Function<ZoomableComponent, Boolean> onComponentDelete;
    private BaseSelectAndManipInstrument selectInstrument;
    private final Map<ZoomableComponent, OverrideStyle> overrideStyle = new HashMap<>();
    private boolean blinkVisible = true;
    private Timer blinkTimer;

    public ZoomablePanel() {
        init();
    }

    public Map<ZoomableComponent, OverrideStyle> getOverrideStyle() {
        return overrideStyle;
    }

    private void init() {
        setOpaque(true);
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = Math.round((e.getX() - scrollX) * zoomLevel);
                int my = Math.round((e.getY() - scrollY) * zoomLevel);
                if (e.getClickCount() == 2) {
                    getCurrentInstrument().onDoubleClick(e.getX(), e.getY(), mx, my, e.getButton());
                } else {
                    getCurrentInstrument().onMousePressed(e.getX(), e.getY(), mx, my, e.getButton());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int mx = Math.round((e.getX() - scrollX) * zoomLevel);
                int my = Math.round((e.getY() - scrollY) * zoomLevel);
                getCurrentInstrument().onMouseReleased(e.getX(), e.getY(), mx, my, e.getButton());
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                int mx = Math.round((e.getX() - scrollX) * zoomLevel);
                int my = Math.round((e.getY() - scrollY) * zoomLevel);
                getCurrentInstrument().onMouseMove(e.getX(), e.getY(), mx, my, true);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int mx = Math.round((e.getX() - scrollX) * zoomLevel);
                int my = Math.round((e.getY() - scrollY) * zoomLevel);
                getCurrentInstrument().onMouseMove(e.getX(), e.getY(), mx, my, false);
                //tempMousePoint.move(e.getX(), e.getY());
                repaint();
            }
        });
        addMouseWheelListener((MouseWheelEvent event) -> {
            getCurrentInstrument().onMouseWheel(event.getUnitsToScroll());
        });

        renderingHint = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        renderingHint.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        selectInstrument = new BaseSelectAndManipInstrument(this);
        setInstrument(selectInstrument);
        blinkTimer = new Timer(500, this::processBlink);
        blinkTimer.setRepeats(true);
        blinkTimer.start();
    }

    public void processBlink(ActionEvent e) {
        blinkVisible = !blinkVisible;
        repaint();
    }

    public boolean isBlinkVisible() {
        return blinkVisible;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHints(renderingHint);
        graphics.setG2d(g2d);
        graphics.setZoomLevel(zoomLevel);
        graphics.setOffsetX(scrollX);
        graphics.setOffsetY(scrollY);
        if (dirty) {
            sortComponents();
            dirty = false;
        }

        for (ZoomableComponent component : components) {
            component.paint(graphics);
        }

        if (!instruments.isEmpty()) {
            instruments.peek().onDraw(graphics);
        }
    }

    private void sortComponents() {
        components.sort((ZoomableComponent left, ZoomableComponent right)
                -> left.getSortOrder() - right.getSortOrder()
        );
    }

    public float innerPointXToScreenPoint(float x) {
        return x / zoomLevel + scrollX;
    }

    public float innerPointYToScreenPoint(float y) {
        return y / zoomLevel + scrollY;
    }

    public float screenPointXToInnerPoint(float x) {
        return (-scrollX + x) * zoomLevel;
    }

    public float screenPointYToInnerPoint(float y) {
        return (-scrollY + y) * zoomLevel;
    }

    public List<ZoomableComponent> getZoomableComponents() {
        return components;
    }

    public float getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public float getScrollX() {
        return scrollX;
    }

    public float getScrollY() {
        return scrollY;
    }

    public void setScrollX(float scrollX) {
        this.scrollX = scrollX;
    }

    public void setScrollY(float scrollY) {
        this.scrollY = scrollY;
    }

    public BaseInstrument getCurrentInstrument() {
        if (instruments.isEmpty()) {
            throw new IllegalStateException("No instruments selected");
        }
        return instruments.peek();
    }

    private void deactivateCurrentInstrument() {
        if (!instruments.isEmpty()) {
            instruments.peek().onDeactivateInstrument();
        }
    }

    private void activateCurrentInstrument() {
        if (!instruments.isEmpty()) {
            instruments.peek().onActivateInstrument();
        }
    }

    public BaseSelectAndManipInstrument getSelectInstrument() {
        return selectInstrument;
    }

    public ZoomableComponent getSelectedComponent() {
        return selectInstrument.getSelectedComponent();
    }

    public void selectComponent(ZoomableComponent component) {
        selectInstrument.select(component);
    }

    public void setInstrument(BaseInstrument instrument) {
        deactivateCurrentInstrument();
        instruments.clear();
        instruments.push(instrument);
        activateCurrentInstrument();
    }

    public void pushInstrument(BaseInstrument instrument) {
        deactivateCurrentInstrument();
        instruments.push(instrument);
        activateCurrentInstrument();
    }

    public void popInstrument() {
        deactivateCurrentInstrument();
        instruments.pop();
        activateCurrentInstrument();
    }

    public void addChild(ZoomableComponent component) {
        component.setOwner(this);
        components.add(component);
        markDirty();
        repaint();
    }

    public boolean deleteZoomableComponent(ZoomableComponent component) {
        if (onComponentDelete != null) {
            if (!onComponentDelete.apply(component)) {
                return false;
            }
        }
        components.remove(component);
        markDirty();
        repaint();
        return true;
    }

    public void markDirty() {
        dirty = true;
    }

    public JsonArray serialize(SerializationContext context) {
        JsonArray resultArray = new JsonArray(components.size());
        for (ZoomableComponent component : components) {
            resultArray.add(component.serialize(context));
        }
        return resultArray;
    }

    public void deserialize(SerializationContext context, JsonArray serializedComponents, int version) {
        selectInstrument.deselect();
        components.clear();
        int componentIndex = -1;
        for (JsonElement compElement : serializedComponents) {
            componentIndex++;
            JsonObject componentObject = compElement.getAsJsonObject();
            if (!componentObject.has("type")) {
                throw new IllegalStateException("Component #" + componentIndex + " does not have [type] field");
            }
            String componentType = componentObject.getAsJsonPrimitive("type").getAsString();
            switch (componentType) {
                case ArrowComponent.TYPE: {
                    addChild(ArrowComponent.deserialize(context, componentObject, version));
                    break;
                }
                case BackgroundComponent.TYPE: {
                    addChild(BackgroundComponent.deserialize(context, componentObject, version));
                    break;
                }
                case RoomComponent.TYPE: {
                    addChild(RoomComponent.deserialize(context, componentObject, version));
                    break;
                }
                case MarkerComponent.TYPE: {
                    addChild(MarkerComponent.deserialize(context, componentObject, version));
                    break;
                }
            }
        }
    }
}
