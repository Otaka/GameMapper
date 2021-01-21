package com.gamemapper.settings;

import com.gamemapper.components.ColorChooserPanel;
import com.gamemapper.components.VariablesEditorPanel;
import com.gamemapper.data.VariablesStorage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Dmitry
 */
public class SettingsDialog extends JDialog {

    private ColorChooserPanel visitedColorChooserPanel;
    private ColorChooserPanel notVisitedColorChooserPanel;
    private ColorChooserPanel arrowsColorChooserPanel;

    public SettingsDialog(JFrame parent) {
        super(parent, false);
        init();
    }

    private void init() {
        setTitle("Settings");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(2, 2));

        JTabbedPane tabs = new JTabbedPane();
        tabs.add(createColorsPanel(), "Colors");
        tabs.add(createVariablesPanel(), "Variables");

        getContentPane().add(tabs);
        pack();
        setLocationRelativeTo(getParent());
    }

    private JPanel createColorsPanel() {
        visitedColorChooserPanel = new ColorChooserPanel(SettingsManager.visitedRoomsColor);
        visitedColorChooserPanel.setPreferredSize(new Dimension(20, 20));
        visitedColorChooserPanel.setOnColorChoosedAction((Color newColor) -> {
            SettingsManager.visitedRoomsColor = newColor;
        });

        notVisitedColorChooserPanel = new ColorChooserPanel(SettingsManager.notVisitedRoomsColor);
        notVisitedColorChooserPanel.setPreferredSize(new Dimension(20, 20));
        notVisitedColorChooserPanel.setOnColorChoosedAction((Color newColor) -> {
            SettingsManager.notVisitedRoomsColor = newColor;
        });

        arrowsColorChooserPanel = new ColorChooserPanel(SettingsManager.arrowsColor);
        arrowsColorChooserPanel.setPreferredSize(new Dimension(20, 20));
        arrowsColorChooserPanel.setOnColorChoosedAction((Color newColor) -> {
            SettingsManager.arrowsColor = newColor;
        });

        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel("Visited room"));
        panel.add(visitedColorChooserPanel, "wrap");
        panel.add(new JLabel("Not visited room"));
        panel.add(notVisitedColorChooserPanel, "wrap");

        panel.add(new JLabel("Arrows"));
        panel.add(arrowsColorChooserPanel, "wrap");
        return panel;
    }

    private JPanel createVariablesPanel() {
        return new VariablesEditorPanel(VariablesStorage.get());
    }
}
