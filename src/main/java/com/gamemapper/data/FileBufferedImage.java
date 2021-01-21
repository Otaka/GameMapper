package com.gamemapper.data;

import com.gamemapper.utils.Utils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Dmitry
 */
public class FileBufferedImage {

    private String path;
    private BufferedImage image;

    public String getPath() {
        return path;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }
    

    public static FileBufferedImage load(String path) {
        try {
            BufferedImage image = ImageIO.read(Utils.getInputStream(path));
            FileBufferedImage result = new FileBufferedImage();
            result.image = image;
            result.path = path;
            return result;
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read image file [" + path + "]", ex);
        }
    }
}
