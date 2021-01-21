package com.gamemapper.components.zoomablecomponents;

import com.gamemapper.components.zoomablecomponents.base.PolygonZoomableComponent;
import com.gamemapper.components.zoomablepanel.OverrideStyle;
import com.gamemapper.data.SerializationContext;
import com.gamemapper.settings.SettingsManager;
import com.gamemapper.utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JMenuItem;

/**
 *
 * @author Dmitry
 */
public class RoomComponent extends PolygonZoomableComponent {

    public static final String TYPE = "room";
    private boolean visited = false;
    private float weight = 1;

    public RoomComponent() {
        setSortOrder(5);
    }

    @Override
    public Color getColor() {
        OverrideStyle overrideStyle = owner.getOverrideStyle().get(this);
        if (overrideStyle != null) {

            return overrideStyle.getColor();
        }
        return visited ? SettingsManager.visitedRoomsColor : SettingsManager.notVisitedRoomsColor;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    @Override
    public void createPopupMenu(List<JMenuItem> menu) {
        JMenuItem visitedMenu = new JMenuItem(isVisited() ? "Not visited" : "Visited");
        menu.add(visitedMenu);
        visitedMenu.addActionListener((ActionEvent e) -> {
            visited = !visited;
            owner.repaint();
        });

        super.createPopupMenu(menu);
    }

    @Override
    public JsonObject serialize(SerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(TYPE));
        result.add("x", new JsonPrimitive(x));
        result.add("y", new JsonPrimitive(y));
        result.add("weight", new JsonPrimitive(weight));
        result.add("visited", new JsonPrimitive(isVisited()));
        result.add("color", Utils.serializeColor(getColor()));
        result.add("polygon", Utils.serializePolygon(getPolygon()));
        return result;
    }

    public static RoomComponent deserialize(SerializationContext context, JsonObject serializedComponent, int version) {
        RoomComponent room = new RoomComponent();
        room.setX(serializedComponent.getAsJsonPrimitive("x").getAsFloat());
        room.setY(serializedComponent.getAsJsonPrimitive("y").getAsFloat());
        room.setWeight(serializedComponent.getAsJsonPrimitive("weight").getAsFloat());
        room.setVisited(serializedComponent.getAsJsonPrimitive("visited").getAsBoolean());
        room.setColor(Utils.deserializeColor(serializedComponent.getAsJsonArray("color")));
        Utils.deserializePolygon(room.getPolygon(), serializedComponent.getAsJsonArray("polygon"));
        room.recalcBoundaries();
        return room;
    }
}
