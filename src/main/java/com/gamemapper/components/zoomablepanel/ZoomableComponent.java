package com.gamemapper.components.zoomablepanel;

import com.gamemapper.data.SerializationContext;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.List;
import javax.swing.JMenuItem;

/**
 * @author Dmitry
 */
public class ZoomableComponent {

    protected ZoomablePanel owner;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected boolean selectable;
    protected int sortOrder = 0;

    public ZoomableComponent(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        selectable = true;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        if (owner != null) {
            owner.markDirty();
        }
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean containsPoint(int x, int y) {
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public ZoomablePanel getOwner() {
        return owner;
    }

    public void setOwner(ZoomablePanel owner) {
        this.owner = owner;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void paint(GraphicZ2d g2d) {

    }

    public List<Manipulator> createManipulators() {
        return Collections.EMPTY_LIST;
    }

    public void onManipulatorMoved(List<Manipulator> manipulators, int indexOfMovedElement, int dx, int dy) {

    }

    public void createPopupMenu(List<JMenuItem> menus) {

    }

    public void onOpenSettings(int x, int y) {

    }

    public JsonObject serialize(SerializationContext context) {
        throw new IllegalStateException("Serialization is not implemented yet");
    }
}
