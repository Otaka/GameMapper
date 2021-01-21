package com.gamemapper.components.zoomablepanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;

/**
 * @author Dmitry
 */
public class GraphicZ2d {

    private Graphics2D g2d;
    private float scrollX = 0;
    private float scrollY = 0;
    private float zoomLevel;
    private int[] tArrayCache1 = new int[0];
    private int[] tArrayCache2 = new int[0];

    public Graphics2D getG2d() {
        return g2d;
    }

    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public void setOffsetX(float offsetX) {
        this.scrollX = offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.scrollY = offsetY;
    }

    public boolean drawImage(Image img, float dx1, float dy1, float dx2, float dy2, float sx1, float sy1, float sx2, float sy2) {
        int _dx1 = Math.round(dx1 / zoomLevel + scrollX);
        int _dy1 = Math.round(dy1 / zoomLevel + scrollY);
        int _dx2 = Math.round(dx2 / zoomLevel + scrollX);
        int _dy2 = Math.round(dy2 / zoomLevel + scrollY);

        return g2d.drawImage(img, _dx1, _dy1, _dx2, _dy2, Math.round(sx1), Math.round(sy1), Math.round(sx2), Math.round(sy2), null);
    }

    public void drawLine(float x1, float y1, float x2, float y2, Color color) {
        x1 = x1 / zoomLevel + scrollX;
        x2 = x2 / zoomLevel + scrollX;

        y1 = y1 / zoomLevel + scrollY;
        y2 = y2 / zoomLevel + scrollY;
        g2d.setColor(color);
        g2d.drawLine(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2));
    }

    /*
    public void drawPolygon(float[] xPoints, float[] yPoints, Color color) {
        int count = xPoints.length;
        if (tArrayCache1.length < count) {
            tArrayCache1 = new int[count];
        }
        if (tArrayCache2.length < count) {
            tArrayCache2 = new int[count];
        }
        for (int i = 0; i < count; i++) {
            tArrayCache1[i] = Math.round (xPoints[i] / zoomLevel + scrollX);
            tArrayCache2[i] = Math.round (yPoints[i] / zoomLevel + scrollY);
        }
        g2d.setColor(color);
        g2d.fillPolygon(tArrayCache1, tArrayCache2, count);
    }
     */
    public void drawPolygon(Polygon polygon, float polygonOffsetX, float polygonOffsetY, Color color) {

        int count = polygon.npoints;
        if (tArrayCache1.length < count) {
            tArrayCache1 = new int[count];
        }
        if (tArrayCache2.length < count) {
            tArrayCache2 = new int[count];
        }
        for (int i = 0; i < count; i++) {
            tArrayCache1[i] = Math.round(((float) polygon.xpoints[i] + polygonOffsetX) / zoomLevel + scrollX);
            tArrayCache2[i] = Math.round(((float) polygon.ypoints[i] + polygonOffsetY) / zoomLevel + scrollY);
        }

        g2d.setColor(color);
        g2d.fillPolygon(tArrayCache1, tArrayCache2, count);
    }

    public void drawCenteredNonZoomableRectangle(float x, float y, int radius, Color color, Color background) {
        int dx1 = Math.round(x / zoomLevel + scrollX);
        int dy1 = Math.round(y / zoomLevel + scrollY);
        g2d.setColor(background);
        g2d.fillRect(dx1 - radius, dy1 - radius, radius * 2, radius * 2);
        g2d.setColor(color);
        g2d.drawRect(dx1 - radius, dy1 - radius, radius * 2, radius * 2);
    }

    public void setG2d(Graphics2D g2d) {
        this.g2d = g2d;
    }
}
