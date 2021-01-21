package com.gamemapper.components.zoomablecomponents;

import com.gamemapper.components.zoomablepanel.GraphicZ2d;
import com.gamemapper.components.zoomablepanel.ImageZoomableComponent;
import com.gamemapper.components.zoomablepanel.OverrideStyle;
import com.gamemapper.data.FileBufferedImage;
import com.gamemapper.data.Interaction;
import com.gamemapper.data.Interactions;
import com.gamemapper.data.SerializationContext;
import com.gamemapper.data.Variable;
import com.gamemapper.settings.MarkerSettingsDialog;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/**
 * @author Dmitry
 */
public class MarkerComponent extends ImageZoomableComponent {

    public static final String TYPE = "marker";
    private String name;
    private boolean interacted;
    private String notes;
    private Interactions interactions = new Interactions();

    public MarkerComponent(FileBufferedImage image, float x, float y, float width, float height) {
        super(image, x, y, width, height);
        notes = "";
        setSortOrder(20);
    }

    public void setInteractions(Interactions interactions) {
        this.interactions = interactions;
    }

    public Interactions getInteractions() {
        return interactions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInteracted(boolean interacted) {
        this.interacted = interacted;
    }

    public boolean isInteracted() {
        return interacted;
    }

    public boolean isInteractable() {
        return !interactions.getInteractions().isEmpty();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public void paint(GraphicZ2d g2d) {
        OverrideStyle overrideStyle = owner.getOverrideStyle().get(this);
        if (overrideStyle != null) {
            if (overrideStyle.isBlink() && !owner.isBlinkVisible()) {
                //not draw marker in case if it is in blinking state
                return;
            }
        }

        super.paint(g2d);
        if (owner.getSelectedComponent() == this) {
            drawCenterPointMarker(g2d);
        }
    }

    private void drawCenterPointMarker(GraphicZ2d g2d) {
        float screenX = owner.innerPointXToScreenPoint(x);
        float screenY = owner.innerPointYToScreenPoint(y);
        g2d.getG2d().setColor(Color.BLACK);
        g2d.getG2d().drawLine(Math.round(screenX - 3), Math.round(screenY), Math.round(screenX + 3), Math.round(screenY));
        g2d.getG2d().drawLine(Math.round(screenX), Math.round(screenY - 3), Math.round(screenX), Math.round(screenY + 3));
    }

    @Override
    public void onOpenSettings(int x, int y) {
        MarkerSettingsDialog dialog = new MarkerSettingsDialog((JFrame) SwingUtilities.getWindowAncestor(owner), this);
        dialog.showSettingsDialog(x, y);
    }

    public void interact() {
        interacted = true;
        for (Interaction interaction : interactions.getInteractions()) {
            Variable var = interaction.getVariable();
            switch (interaction.getOperation()) {
                case ADD: {
                    var.setValue(var.getValue() + interaction.getValue());
                    break;
                }
            }
        }
    }

    @Override
    public void createPopupMenu(List<JMenuItem> menus) {
        super.createPopupMenu(menus);
        JMenuItem interactMenuItem = new JMenuItem("Interact");
        interactMenuItem.setEnabled(interacted == false && !interactions.getInteractions().isEmpty());
        interactMenuItem.addActionListener((ActionEvent e) -> {
            interact();
        });
        menus.add(interactMenuItem);
    }

    @Override
    public JsonObject serialize(SerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(TYPE));
        result.add("name", new JsonPrimitive(name));
        result.add("notes", new JsonPrimitive(notes));
        result.add("x", new JsonPrimitive(x));
        result.add("y", new JsonPrimitive(y));
        result.add("width", new JsonPrimitive(width));
        result.add("height", new JsonPrimitive(height));
        result.add("marginX", new JsonPrimitive(getMarginX()));
        result.add("marginY", new JsonPrimitive(getMarginY()));
        result.add("image", new JsonPrimitive(context.createRelativePath(getImage().getPath())));
        result.add("interacted", new JsonPrimitive(interacted));
        result.add("interactions", interactions.serialize());
        return result;
    }

    public static MarkerComponent deserialize(SerializationContext context, JsonObject serializedComponent, int version) {
        float x = serializedComponent.getAsJsonPrimitive("x").getAsFloat();
        float y = serializedComponent.getAsJsonPrimitive("y").getAsFloat();
        String name = serializedComponent.getAsJsonPrimitive("name").getAsString();
        String notes = serializedComponent.getAsJsonPrimitive("notes").getAsString();
        float width = serializedComponent.getAsJsonPrimitive("width").getAsFloat();
        float height = serializedComponent.getAsJsonPrimitive("height").getAsFloat();
        String imagePath = context.resolveRelativePath(serializedComponent.getAsJsonPrimitive("image").getAsString());
        FileBufferedImage bufferedImage = FileBufferedImage.load(imagePath);

        MarkerComponent marker = new MarkerComponent(bufferedImage, x, y, width, height);
        marker.setName(name);
        marker.setNotes(notes);
        marker.setMarginX(serializedComponent.getAsJsonPrimitive("marginX").getAsInt());
        marker.setMarginY(serializedComponent.getAsJsonPrimitive("marginY").getAsInt());
        marker.setInteracted(serializedComponent.getAsJsonPrimitive("interacted").getAsBoolean());

        marker.getInteractions().deserialize(serializedComponent.getAsJsonArray("interactions"), version);
        return marker;
    }
}
