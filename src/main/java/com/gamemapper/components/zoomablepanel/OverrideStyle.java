package com.gamemapper.components.zoomablepanel;

import java.awt.Color;

/**
 *
 * @author Dmitry
 */
public class OverrideStyle {

    private Color color;
    private boolean blink;

    public OverrideStyle(Color color, boolean blink) {
        this.color = color;
        this.blink = blink;
    }

    public OverrideStyle() {
    }

    public Color getColor() {
        return color;
    }

    public boolean isBlink() {
        return blink;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
