package com.gamemapper.components.zoomablecomponents.base;

import com.gamemapper.utils.Utils;
import com.gamemapper.components.zoomablepanel.GraphicZ2d;
import com.gamemapper.components.zoomablepanel.Manipulator;
import com.gamemapper.components.zoomablepanel.ZoomableComponent;
import com.gamemapper.settings.SettingsManager;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class ArrowsZoomableComponent extends ZoomableComponent {

    protected Point[] points;
    protected boolean bidirectional = true;
    private static final double ARROW_ANGLE_RADIANS = Math.toRadians(160);
    private static final Point p1 = new Point();
    private static final Point p2 = new Point();
    private static final Point p3 = new Point();
    private static final Point p4 = new Point();
    private static final Point p5 = new Point();
    private static final Point p6 = new Point();
    private static int pointLength = 3;

    public ArrowsZoomableComponent(float x, float y, float width, float height) {
        super(x, y, width, height);
        points = new Point[2];
        for (int i = 0; i < 2; i++) {
            points[i] = new Point();
        }
    }

    public Color getColor() {
        return SettingsManager.arrowsColor;
    }

    public void setPoints(Point[] points) {
        this.points = points;
    }

    public void setEndPoint(Point point) {
        points[points.length - 1].setLocation(point);
    }

    public void setStartPoint(Point point) {
        points[0].setLocation(point);
    }

    public boolean isBidirectional() {
        return bidirectional;
    }

    public void setBidirectional(boolean bidirectional) {
        this.bidirectional = bidirectional;
    }

    public Point getGlobalStartPoint() {
        return new Point(Math.round(points[0].x + x), Math.round(points[0].y + y));
    }

    public Point getGlobalEndPoint() {
        Point p = points[points.length - 1];
        return new Point(Math.round(p.x + x), Math.round(p.y + y));
    }

    @Override
    public boolean containsPoint(int x, int y) {
        p1.setLocation(x - 4, y - 4);
        p2.setLocation(x + 4, y + 4);
        p3.setLocation(x + 4, y - 4);
        p4.setLocation(x - 4, y + 4);
        for (int i = 0; i < points.length - 1; i++) {
            Point startPoint = points[i];
            Point endPoint = points[i + 1];
            p5.setLocation(startPoint.x + this.x, startPoint.y + this.y);
            p6.setLocation(endPoint.x + this.x, endPoint.y + this.y);
            if (Utils.segmentsIntersect(p5, p6, p1, p2)) {
                return true;
            }

            if (Utils.segmentsIntersect(p5, p6, p2, p3)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Manipulator> createManipulators() {
        List<Manipulator> manipulators = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            manipulators.add(new Manipulator(Math.round(p.x + x), Math.round(p.y + y), i));
        }
        return manipulators;
    }

    @Override
    public void onManipulatorMoved(List<Manipulator> manipulators, int indexOfMovedElement, int dx, int dy) {
        Manipulator m = manipulators.get(indexOfMovedElement);
        m.setX(m.getX() + dx);
        m.setY(m.getY() + dy);
        points[indexOfMovedElement].x += dx;
        points[indexOfMovedElement].y += dy;
    }

    @Override
    public void paint(GraphicZ2d g2d) {
        super.paint(g2d);
        //forward arrow
        Point endPoint = points[points.length - 1];
        Point beforeEndPoint = points[points.length - 2];
        g2d.drawLine(beforeEndPoint.x + x, beforeEndPoint.y + y, endPoint.x + x, endPoint.y + y, getColor());
        double lineAngle = Math.atan2(endPoint.y - beforeEndPoint.y, endPoint.x - beforeEndPoint.x);
        drawArrowLine(g2d, endPoint.x + x, endPoint.y + y, lineAngle + ARROW_ANGLE_RADIANS);
        drawArrowLine(g2d, endPoint.x + x, endPoint.y + y, lineAngle - ARROW_ANGLE_RADIANS);

        if (bidirectional) {
            //back arrow
            Point startPoint = points[0];
            Point secondPoint = points[1];
            lineAngle = Math.atan2(startPoint.y - secondPoint.y, startPoint.x - secondPoint.x);
            drawArrowLine(g2d, startPoint.x + x, startPoint.y + y, lineAngle + ARROW_ANGLE_RADIANS);
            drawArrowLine(g2d, startPoint.x + x, startPoint.y + y, lineAngle - ARROW_ANGLE_RADIANS);
        }
    }

    private void drawArrowLine(GraphicZ2d g2d, float dotX, float dotY, double angle) {
        double x1 = Math.cos(angle) * pointLength + dotX;
        double y1 = Math.sin(angle) * pointLength + dotY;
        g2d.drawLine(dotX, dotY, (float) x1, (float) y1, getColor());
    }
}
