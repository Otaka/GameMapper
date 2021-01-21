package com.gamemapper.settings;

import com.gamemapper.components.ConditionsEditorPanel;
import com.gamemapper.components.zoomablecomponents.ArrowComponent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Dmitry
 */
public class ArrowsSettingsDialog extends JDialog {

    private boolean closed = false;
    private final ArrowComponent arrow;
    private ConditionsEditorPanel conditionsEditor;
    private static final Set<ArrowComponent> openedArrows = new HashSet<>();

    public ArrowsSettingsDialog(JFrame parent, ArrowComponent arrow) {
        super(parent, false);
        setTitle("Arrow settings");
        this.arrow = arrow;
        //if the dialog for particular arrow already opened - exit
        if (openedArrows.contains(arrow)) {
            closed = true;
            dispose();
            return;
        }

        openedArrows.add(arrow);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                openedArrows.remove(arrow);
            }
        });

        getContentPane().setLayout(new MigLayout());
        JCheckBox bidirectionalCheckbox = new JCheckBox("Bidirectional");
        bidirectionalCheckbox.setSelected(arrow.isBidirectional());
        bidirectionalCheckbox.addActionListener((ActionEvent e) -> {
            arrow.setBidirectional(bidirectionalCheckbox.isSelected());
            repaintParent();
        });

        getContentPane().add(bidirectionalCheckbox, "wrap");
        getContentPane().add(new JLabel("Conditions"), "wrap");
        conditionsEditor = new ConditionsEditorPanel(arrow.getConditions());
        getContentPane().add(conditionsEditor, "width 100%,wmin 300px, wrap");
        pack();
    }

    private void repaintParent() {
        arrow.getOwner().repaint();
    }

    public void showSettingsDialog(int x, int y) {
        if (!closed) {
            setLocation(x, y);
            setVisible(true);
        }
    }
}
