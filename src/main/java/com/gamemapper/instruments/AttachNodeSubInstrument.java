package com.gamemapper.instruments;

import com.gamemapper.utils.Utils;
import com.gamemapper.components.zoomablepanel.GraphicZ2d;
import com.gamemapper.components.zoomablepanel.ZoomablePanel;
import com.gamemapper.components.zoomablecomponents.base.PolygonZoomableComponent;
import com.gamemapper.components.zoomablepanel.instruments.sub.BaseSubInstrument;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Polygon;

/**
 * @author Dmitry
 */
public class AttachNodeSubInstrument extends BaseSubInstrument {

    private final Point shadowNewNode = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
    private final Point[] tempPoints;
    private int selectedPolygonEdgeForNewNode = -1;

    public AttachNodeSubInstrument(ZoomablePanel panel) {
        super(panel);
        tempPoints = new Point[6];
        for (int i = 0; i < 6; i++) {
            tempPoints[i] = new Point();
        }
    }

    @Override
    public void onActivateInstrument() {
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void onDeactivateInstrument() {
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public boolean onMousePressed(int x, int y, int innerX, int innerY, int button) {
        if (button == 1) {
            if (shadowNewNode.x != Integer.MIN_VALUE) {
                attachNewPointToPolygon();
            }
            return false;
        }
        if (button == 3) {
            parentInstrument.removeSubInstrument(this);
            return false;
        }
        return true;
    }

    @Override
    public boolean onMouseMove(int x, int y, int innerX, int innerY, boolean drag) {
        shadowNewNode.move(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Point crossS1 = tempPoints[0];
        Point crossD1 = tempPoints[1];
        Point crossS2 = tempPoints[2];
        Point crossD2 = tempPoints[3];

        Point polygonLineS1 = tempPoints[4];
        Point polygonLineD1 = tempPoints[5];

        crossS1.move(innerX - 3, innerY - 3);
        crossD1.move(innerX + 3, innerY + 3);
        crossS2.move(innerX + 3, innerY - 3);
        crossD2.move(innerX - 3, innerY + 3);
        boolean intersected = false;
        PolygonZoomableComponent selectedPolygon = (PolygonZoomableComponent) parentInstrument.getSelectedComponent();
        Polygon polygon = selectedPolygon.getPolygon();
        float offsetX = selectedPolygon.getX();
        float offsetY = selectedPolygon.getY();
        for (int i = 0; i < polygon.npoints; i++) {
            polygonLineS1.move(Math.round (polygon.xpoints[i] + offsetX), Math.round (polygon.ypoints[i] + offsetY));
            if (i == polygon.npoints - 1) {
                polygonLineD1.move(Math.round (polygon.xpoints[0] + offsetX), Math.round(polygon.ypoints[0] + offsetY));
            } else {
                polygonLineD1.move(Math.round (polygon.xpoints[i + 1] + offsetX), Math.round (polygon.ypoints[i + 1] + offsetY));
            }
            intersected = Utils.segmentsIntersect(polygonLineS1, polygonLineD1, crossS1, crossD1);
            if (intersected == false) {
                intersected = Utils.segmentsIntersect(polygonLineS1, polygonLineD1, crossS2, crossD2);
            }
            if (intersected == true) {
                selectedPolygonEdgeForNewNode = i;
                break;
            }
        }

        if (intersected) {
            shadowNewNode.move(innerX, innerY);
        }
        return true;
    }

    private void attachNewPointToPolygon() {
        PolygonZoomableComponent polygonZoomableComponent = (PolygonZoomableComponent) parentInstrument.getSelectedComponent();
        shadowNewNode.translate(Math.round(-polygonZoomableComponent.getX()), Math.round(-polygonZoomableComponent.getY()));
        Utils.insertDotInPolygon(polygonZoomableComponent.getPolygon(), shadowNewNode, selectedPolygonEdgeForNewNode);
        polygonZoomableComponent.getPolygon().invalidate();
        polygonZoomableComponent.recalcBoundaries();
        shadowNewNode.move(Integer.MIN_VALUE, Integer.MIN_VALUE);
        parentInstrument.fireSelectedObjectModified();
    }

    @Override
    public void onDraw(GraphicZ2d g) {
        if (shadowNewNode.x != Integer.MIN_VALUE) {
            g.drawCenteredNonZoomableRectangle(shadowNewNode.x, shadowNewNode.y, 3, Color.BLACK, Color.ORANGE);
        }
    }
}
