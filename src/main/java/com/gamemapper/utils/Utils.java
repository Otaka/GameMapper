package com.gamemapper.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Dmitry
 */
public class Utils {

    public static JsonArray serializePolygon(Polygon p) {
        JsonArray polygonPointsArray = new JsonArray();
        for (int i = 0; i < p.npoints; i++) {
            polygonPointsArray.add(p.xpoints[i]);
            polygonPointsArray.add(p.ypoints[i]);
        }
        return polygonPointsArray;
    }

    public static void deserializePolygon(Polygon p, JsonArray serializedPolygon) {
        p.reset();
        int pointsCount = serializedPolygon.size() / 2;
        int index = 0;
        for (int i = 0; i < pointsCount; i++, index += 2) {
            int x = serializedPolygon.get(index + 0).getAsInt();
            int y = serializedPolygon.get(index + 1).getAsInt();
            p.addPoint(x, y);
        }
        p.invalidate();
    }

    public static JsonArray serializePoint(Point point) {
        JsonArray serializedPoint = new JsonArray();
        serializedPoint.add(new JsonPrimitive(point.x));
        serializedPoint.add(new JsonPrimitive(point.y));
        return serializedPoint;
    }

    public static Point deserializePoint(JsonArray serializedPoint) {
        Point point = new Point(serializedPoint.get(0).getAsInt(), serializedPoint.get(1).getAsInt());
        return point;
    }

    public static JsonArray serializeColor(Color color) {
        JsonArray serializedColor = new JsonArray();
        serializedColor.add(new JsonPrimitive(color.getRed()));
        serializedColor.add(new JsonPrimitive(color.getGreen()));
        serializedColor.add(new JsonPrimitive(color.getBlue()));
        serializedColor.add(new JsonPrimitive(color.getAlpha()));

        return serializedColor;
    }

    public static Color deserializeColor(JsonArray serializedColor) {
        Color color = new Color(
                serializedColor.get(0).getAsInt(),
                serializedColor.get(1).getAsInt(),
                serializedColor.get(2).getAsInt(),
                serializedColor.get(3).getAsInt()
        );
        return color;
    }

    public static <T, V> List<V> getListFromMap(Map<T, List<V>> map, T key) {
        List<V> value = map.get(key);
        if (value == null) {
            value = new ArrayList<>();
            map.put(key, value);
        }
        return value;
    }
    
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static InputStream getInputStream(String path) {
        final String resourcePrefix = "resource:";
        if (path.startsWith(resourcePrefix)) {
            path = path.substring(resourcePrefix.length());
            InputStream stream = Utils.class.getResourceAsStream(path);
            if (stream == null) {
                throw new IllegalArgumentException("Cannot find resource [" + path + "]");
            }
            return stream;
        } else {
            File file = new File(path);
            if (!file.exists()) {
                throw new IllegalArgumentException("File [" + path + "] does not exists");
            }
            try {
                return new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static boolean segmentsIntersect(Point a, Point b, Point c, Point d) {
        float det = (b.x - a.x) * (d.y - c.y) - (d.x - c.x) * (b.y - a.y);
        if (det == 0) {
            return false; //Lines are parallel
        }
        float lambda = ((d.y - c.y) * (d.x - a.x) + (c.x - d.x) * (d.y - a.y)) / det;
        float gamma = ((a.y - b.y) * (d.x - a.x) + (b.x - a.x) * (d.y - a.y)) / det;
        return (0 < lambda && lambda < 1) && (0 < gamma && gamma < 1);
    }

    public static boolean segmentsIntersect(Point2D.Float a, Point2D.Float b, Point2D.Float c, Point2D.Float d) {
        float det = (b.x - a.x) * (d.y - c.y) - (d.x - c.x) * (b.y - a.y);
        if (det == 0) {
            return false; //Lines are parallel
        }
        float lambda = ((d.y - c.y) * (d.x - a.x) + (c.x - d.x) * (d.y - a.y)) / det;
        float gamma = ((a.y - b.y) * (d.x - a.x) + (b.x - a.x) * (d.y - a.y)) / det;
        return (0 < lambda && lambda < 1) && (0 < gamma && gamma < 1);
    }

    public static void shiftArrayFromBack1Element(int[] arr, int elementsCount) {
        int index = arr.length - 2;
        for (int i = 0; i < elementsCount; i++, index--) {
            arr[index + 1] = arr[index];
        }
    }

    public static void insertDotInPolygon(Polygon polygon, Point point, int index) {
        if (index >= polygon.npoints) {
            throw new IllegalArgumentException("Argument index [" + index + "] cannot be more than polygon points count");
        }
        index++;
        polygon.xpoints = Arrays.copyOf(polygon.xpoints, polygon.npoints + 1);
        polygon.ypoints = Arrays.copyOf(polygon.ypoints, polygon.npoints + 1);
        Utils.shiftArrayFromBack1Element(polygon.xpoints, polygon.npoints - index);
        Utils.shiftArrayFromBack1Element(polygon.ypoints, polygon.npoints - index);
        polygon.xpoints[index] = point.x;
        polygon.ypoints[index] = point.y;
        polygon.npoints++;
    }
}
