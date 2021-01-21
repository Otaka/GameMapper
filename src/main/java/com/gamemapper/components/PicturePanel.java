package com.gamemapper.components;

import com.gamemapper.data.FileBufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Dmitry
 */
public class PicturePanel extends JPanel {

    private FileBufferedImage image;

    public PicturePanel() {
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    }

    public void setImage(FileBufferedImage image) {
        this.image = image;
        repaint();
    }

    public FileBufferedImage getImage() {
        return image;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (image != null) {
            g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }
}
