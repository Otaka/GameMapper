package com.gamemapper.components.zoomablecomponents.base;

import com.gamemapper.components.zoomablepanel.GraphicZ2d;
import com.gamemapper.components.zoomablepanel.Manipulator;
import com.gamemapper.components.zoomablepanel.ZoomableComponent;
import com.gamemapper.instruments.AttachNodeSubInstrument;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;

/**
 * @author Dmitry
 */
public class PolygonZoomableComponent extends ZoomableComponent {

    private static final Color DEFAULT_COLOR = new Color(127, 127, 127, 127);
    private Color color = DEFAULT_COLOR;
    private final Polygon polygon;

    public PolygonZoomableComponent() {
        super(0, 0, 0, 0);
        polygon = new Polygon();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void paint(GraphicZ2d g2d) {
        g2d.drawPolygon(polygon, x, y, getColor());
    }

    public void defaultShape() {
        polygon.addPoint(0, 0);
        polygon.addPoint(20, 0);
        polygon.addPoint(20, 20);
        polygon.addPoint(0, 20);
        polygon.invalidate();
        recalcBoundaries();
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void recalcBoundaries() {
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int i = 0; i < polygon.npoints; i++) {
            int _x = polygon.xpoints[i];
            int _y = polygon.ypoints[i];
            if (_x > maxX) {
                maxX = _x;
            }
            if (_y > maxY) {
                maxY = _y;
            }
        }
        setWidth(maxX);
        setHeight(maxY);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        return polygon.contains(x - this.x, y - this.y);
    }

    @Override
    public List<Manipulator> createManipulators() {
        List<Manipulator> manipulators = new ArrayList<>();
        for (int i = 0; i < polygon.npoints; i++) {
            manipulators.add(new Manipulator(Math.round(polygon.xpoints[i] + x), Math.round(polygon.ypoints[i] + y), i));
        }
        return manipulators;
    }

    @Override
    public void onManipulatorMoved(List<Manipulator> manipulators, int indexOfMovedElement, int dx, int dy) {
        Manipulator m = manipulators.get(indexOfMovedElement);
        m.setX(m.getX() + dx);
        m.setY(m.getY() + dy);
        polygon.xpoints[indexOfMovedElement] += dx;
        polygon.ypoints[indexOfMovedElement] += dy;
        polygon.invalidate();
        recalcBoundaries();
    }

    @Override
    public void createPopupMenu(List<JMenuItem> menu) {
        JMenuItem attachNodesToPolygonMenu = new JMenuItem("Attach points");
        attachNodesToPolygonMenu.addActionListener((ActionEvent e) -> {
            owner.getSelectInstrument().addSubInstrument(new AttachNodeSubInstrument(owner));
        });
        menu.add(attachNodesToPolygonMenu);
    }
}
