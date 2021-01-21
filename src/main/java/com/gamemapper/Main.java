package com.gamemapper;

import com.gamemapper.utils.Utils;
import java.awt.HeadlessException;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 * @author Dmitry
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Started game mapper");
        SwingUtilities.invokeLater(() -> {
            try {
                Utils.setSystemLookAndFeel();
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } catch (HeadlessException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
