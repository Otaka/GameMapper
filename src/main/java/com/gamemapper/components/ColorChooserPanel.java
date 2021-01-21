package com.gamemapper.components;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

/**
 *
 * @author Dmitry
 */
public class ColorChooserPanel extends JPanel {

    private Color selectedColor;
    private Consumer<Color> onColorChoosed;

    public ColorChooserPanel(Color color) {
        init(color);
    }

    private void init(Color color) {
        setOpaque(true);
        setColor(color);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                chooseNewColor();
            }
        });
    }

    public void setOnColorChoosedAction(Consumer<Color> onColorChoosed) {
        this.onColorChoosed = onColorChoosed;
    }

    private void chooseNewColor() {
        Color color = JColorChooser.showDialog(this, "Select color", selectedColor, true);
        if (color != null) {
            setColor(color);
            if (onColorChoosed != null) {
                onColorChoosed.accept(getColor());
            }
        }
    }

    public void setColor(Color color) {
        selectedColor = color;
        setBackground(color);
        repaint();
    }

    public Color getColor() {
        return selectedColor;
    }
}
