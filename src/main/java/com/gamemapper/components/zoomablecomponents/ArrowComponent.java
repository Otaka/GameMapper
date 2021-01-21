package com.gamemapper.components.zoomablecomponents;

import com.gamemapper.utils.Utils;
import com.gamemapper.components.zoomablecomponents.base.ArrowsZoomableComponent;
import com.gamemapper.data.Conditions;
import com.gamemapper.data.SerializationContext;
import com.gamemapper.settings.ArrowsSettingsDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author Dmitry
 */
public class ArrowComponent extends ArrowsZoomableComponent {

    public static final String TYPE = "arrow";
    private float weight;
    private final Conditions conditions = new Conditions();

    public ArrowComponent() {
        super(0, 0, 0, 0);
        setSortOrder(30);
    }

    public ArrowComponent(float x, float y) {
        super(x, y, 0, 0);
        setSortOrder(30);
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public Conditions getConditions() {
        return conditions;
    }

    @Override
    public void onOpenSettings(int x, int y) {
        ArrowsSettingsDialog dialog = new ArrowsSettingsDialog((JFrame) SwingUtilities.getWindowAncestor(owner), this);
        dialog.showSettingsDialog(x, y);
    }

    @Override
    public JsonObject serialize(SerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(TYPE));
        result.add("weight", new JsonPrimitive(weight));
        result.add("conditions", conditions.serialize());
        result.add("bidirectional", new JsonPrimitive(isBidirectional()));
        result.add("x", new JsonPrimitive(x));
        result.add("y", new JsonPrimitive(y));
        JsonArray pointsArray = new JsonArray();
        for (Point p : points) {
            pointsArray.add(Utils.serializePoint(p));
        }
        result.add("points", pointsArray);

        return result;
    }

    public static ArrowComponent deserialize(SerializationContext context, JsonObject serializedComponent, int version) {
        float x = serializedComponent.getAsJsonPrimitive("x").getAsFloat();
        float y = serializedComponent.getAsJsonPrimitive("y").getAsFloat();
        ArrowComponent arrow = new ArrowComponent(x, y);
        arrow.setBidirectional(serializedComponent.getAsJsonPrimitive("bidirectional").getAsBoolean());
        arrow.setWeight(serializedComponent.getAsJsonPrimitive("weight").getAsFloat());
        JsonArray pointsArray = serializedComponent.getAsJsonArray("points");
        Point[] points = new Point[pointsArray.size()];
        arrow.setPoints(points);
        for (int i = 0; i < points.length; i++) {
            points[i] = Utils.deserializePoint(pointsArray.get(i).getAsJsonArray());
        }
        arrow.conditions.deserialize(serializedComponent.getAsJsonArray("conditions"), version);
        return arrow;
    }
}
