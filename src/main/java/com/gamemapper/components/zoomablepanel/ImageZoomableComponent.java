package com.gamemapper.components.zoomablepanel;

import com.gamemapper.data.FileBufferedImage;

/**
 * @author Dmitry
 */
public class ImageZoomableComponent extends ZoomableComponent {

    private FileBufferedImage image;
    private int marginX;
    private int marginY;

    public ImageZoomableComponent(FileBufferedImage image, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.image = image;
    }

    @Override
    public boolean containsPoint(int x, int y) {
        float tx = this.x + marginX;
        float ty = this.y + marginY;

        return x >= tx && x <= tx + width && y >= ty && y <= ty + height;
    }

    public int getMarginY() {
        return marginY;
    }

    public int getMarginX() {
        return marginX;
    }

    public void setMarginY(int marginY) {
        this.marginY = marginY;
    }

    public void setMarginX(int marginX) {
        this.marginX = marginX;
    }

    public void setImage(FileBufferedImage image) {
        this.image = image;
    }

    public FileBufferedImage getImage() {
        return image;
    }

    @Override
    public void paint(GraphicZ2d g2d) {
        g2d.drawImage(image.getImage(), x + marginX, y + marginX, x + marginX + width, y + marginY + height, 0, 0, image.getImage().getWidth(), image.getImage().getHeight());
    }
}
