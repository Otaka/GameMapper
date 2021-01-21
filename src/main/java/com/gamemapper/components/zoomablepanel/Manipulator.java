package com.gamemapper.components.zoomablepanel;

/**
 * @author Dmitry
 */
public class Manipulator {

    private int x;
    private int y;
    private Object tag;

    public Manipulator(int x, int y, Object tag) {
        this.x = x;
        this.y = y;
        this.tag = tag;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

}
