package com.gamemapper;

import com.gamemapper.utils.Utils;
import java.awt.Point;
import java.awt.Polygon;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class UtilsTest {

    @Test
    public void testShiftArrayFromBack1Element() {
        int[] arr = new int[]{1, 2, 3, 4, 5, 6};
        Utils.shiftArrayFromBack1Element(arr, 3);
        assertArrayEquals(new int[]{1, 2, 3, 3, 4, 5}, arr);

        arr = new int[]{1, 2, 3, 4, 5, 6};
        Utils.shiftArrayFromBack1Element(arr, 0);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6}, arr);
    }

    @Test
    public void testInsertDotInPolygon() {
        Polygon p = new Polygon();
        p.addPoint(1, 1);
        p.addPoint(2, 2);
        p.addPoint(3, 3);
        Utils.insertDotInPolygon(p, new Point(8, 8), 0);
        assertEquals(4, p.npoints);
        assertArrayEquals(new int[]{1, 8, 2, 3}, p.xpoints);
        assertArrayEquals(new int[]{1, 8, 2, 3}, p.ypoints);

        p = new Polygon();
        p.addPoint(1, 1);
        p.addPoint(2, 2);
        p.addPoint(3, 3);
        Utils.insertDotInPolygon(p, new Point(8, 8), 1);
        assertEquals(4, p.npoints);
        assertArrayEquals(new int[]{1, 2, 8, 3}, p.xpoints);
        assertArrayEquals(new int[]{1, 2, 8, 3}, p.ypoints);

        p = new Polygon();
        p.addPoint(1, 1);
        p.addPoint(2, 2);
        p.addPoint(3, 3);
        Utils.insertDotInPolygon(p, new Point(8, 8), 2);
        assertEquals(4, p.npoints);
        assertArrayEquals(new int[]{1, 2, 3, 8}, p.xpoints);
        assertArrayEquals(new int[]{1, 2, 3, 8}, p.ypoints);
    }
}
