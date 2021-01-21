package com.gamemapper.components.zoomablecomponents;

import com.gamemapper.components.zoomablepanel.ImageZoomableComponent;
import com.gamemapper.data.FileBufferedImage;
import com.gamemapper.data.SerializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author Dmitry
 */
public class BackgroundComponent extends ImageZoomableComponent {

    public static final String TYPE = "background_image";

    public BackgroundComponent(FileBufferedImage image, float x, float y, float width, float height) {
        super(image, x, y, width, height);
        setSortOrder(0);
    }

    @Override
    public JsonObject serialize(SerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(TYPE));
        result.add("image", new JsonPrimitive(context.createRelativePath(getImage().getPath())));
        result.add("x", new JsonPrimitive(x));
        result.add("y", new JsonPrimitive(y));
        result.add("width", new JsonPrimitive(width));
        result.add("height", new JsonPrimitive(height));
        result.add("marginX", new JsonPrimitive(getMarginX()));
        result.add("marginY", new JsonPrimitive(getMarginY()));

        return result;
    }

    public static BackgroundComponent deserialize(SerializationContext context, JsonObject serializedComponent, int version) {
        float x = serializedComponent.getAsJsonPrimitive("x").getAsFloat();
        float y = serializedComponent.getAsJsonPrimitive("y").getAsFloat();
        float width = serializedComponent.getAsJsonPrimitive("width").getAsFloat();
        float height = serializedComponent.getAsJsonPrimitive("height").getAsFloat();
        String imagePath = context.resolveRelativePath(serializedComponent.getAsJsonPrimitive("image").getAsString());
        FileBufferedImage bufferedImage = FileBufferedImage.load(imagePath);
        BackgroundComponent image = new BackgroundComponent(bufferedImage, x, y, width, height);
        image.setMarginX(serializedComponent.getAsJsonPrimitive("marginX").getAsInt());
        image.setMarginY(serializedComponent.getAsJsonPrimitive("marginY").getAsInt());
        return image;
    }
}
