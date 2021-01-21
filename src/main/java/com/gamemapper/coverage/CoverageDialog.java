package com.gamemapper.coverage;

import com.gamemapper.components.ColorChooserPanel;
import com.gamemapper.components.zoomablecomponents.MarkerComponent;
import com.gamemapper.components.zoomablecomponents.RoomComponent;
import com.gamemapper.components.zoomablepanel.OverrideStyle;
import com.gamemapper.components.zoomablepanel.ZoomableComponent;
import com.gamemapper.components.zoomablepanel.ZoomablePanel;
import com.gamemapper.data.VariablesStorage;
import com.gamemapper.settings.SettingsManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

/**
 * @author Dmitry
 */
public class CoverageDialog extends JDialog {

    private static final String ACCESSIBLE_NOT_VISITED_SETTING = "coverage.accessibleNotVisitedColorChooser";
    private static final String ACCESSIBLE_VISITED_SETTING = "coverage.accessibleVisitedColorChooser";
    private final JTextField initialMarkerNameTextField;
    private MarkerComponent initialMarker;
    private final ZoomablePanel zoomablePanel;
    private Color accessibleVisitedColor;
    private Color accessibleNotVisitedColor;

    public CoverageDialog(JFrame parent, ZoomablePanel zoomablePanel) {
        super(parent, false);
        this.zoomablePanel = zoomablePanel;
        setTitle("Coverage");
        getContentPane().setLayout(new MigLayout());
        initialMarkerNameTextField = new JTextField();
        initialMarkerNameTextField.setEditable(false);
        initialMarkerNameTextField.setPreferredSize(new Dimension(100, 24));
        JButton getSelectedMarkerButton = new JButton("Get Selected");
        getSelectedMarkerButton.addActionListener((ActionEvent arg0) -> {
            getSelectedMarker();
        });

        JButton showCoverageButton = new JButton("Show Coverage");
        showCoverageButton.addActionListener((ActionEvent arg0) -> {
            showCoverage();
        });

        JButton clearCoverageButton = new JButton("Clear Coverage");
        clearCoverageButton.addActionListener((ActionEvent arg0) -> {
            clearCoverage();
        });

        accessibleNotVisitedColor = new Color(SettingsManager.readGlobalPreference(ACCESSIBLE_NOT_VISITED_SETTING, new Color(0, 0, 255, 100).getRGB()), true);
        ColorChooserPanel accessibleNotVisitedColorChooser = new ColorChooserPanel(accessibleNotVisitedColor);
        accessibleNotVisitedColorChooser.setOnColorChoosedAction((Color t) -> {
            SettingsManager.writeGlobalPreference(ACCESSIBLE_NOT_VISITED_SETTING, t.getRGB());
            accessibleNotVisitedColor = t;
        });

        accessibleVisitedColor = new Color(SettingsManager.readGlobalPreference(ACCESSIBLE_VISITED_SETTING, new Color(0, 255, 0, 100).getRGB()), true);
        ColorChooserPanel accessibleVisitedColorChooser = new ColorChooserPanel(Color.YELLOW);
        accessibleVisitedColorChooser.setOnColorChoosedAction((Color t) -> {
            SettingsManager.writeGlobalPreference(ACCESSIBLE_VISITED_SETTING, t.getRGB());
            accessibleVisitedColor = t;
        });

        getContentPane().add(new JLabel("Initial marker"), "span 2, wrap");
        getContentPane().add(initialMarkerNameTextField);
        getContentPane().add(getSelectedMarkerButton, "wrap");
        getContentPane().add(new JLabel("Accessible not visited"));
        getContentPane().add(accessibleNotVisitedColorChooser, "wrap");
        getContentPane().add(new JLabel("Accessible visited"));
        getContentPane().add(accessibleVisitedColorChooser, "wrap");

        getContentPane().add(showCoverageButton);
        getContentPane().add(clearCoverageButton, "wrap");
        pack();
        setLocationRelativeTo(parent);
    }

    private void getSelectedMarker() {
        ZoomableComponent currentSelectedComponent = zoomablePanel.getSelectedComponent();
        if (currentSelectedComponent == null) {
            JOptionPane.showMessageDialog(this, "Please select marker to track");
            return;
        }

        if (!(currentSelectedComponent instanceof MarkerComponent)) {
            JOptionPane.showMessageDialog(this, "You can track only markers");
            return;
        }

        initialMarker = (MarkerComponent) currentSelectedComponent;
        initialMarkerNameTextField.setText(initialMarker.getName());
    }

    private void clearCoverage() {
        zoomablePanel.getOverrideStyle().clear();
        zoomablePanel.repaint();
    }

    private void showCoverage() {
        try {
            if (initialMarker == null) {
                JOptionPane.showMessageDialog(rootPane, "Please select initial marker");
                return;
            }

            GraphConverter graphConverter = new GraphConverter();
            Node node = graphConverter.convertGraph(zoomablePanel.getZoomableComponents(), initialMarker, VariablesStorage.get());
            Map<MarkerComponent, RoomComponent> marker2Room = graphConverter.getMarker2RoomInfo();
            paintAccessibleRoomsAndMarkers(node, marker2Room);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void paintAccessibleRoomsAndMarkers(Node rootNode, Map<MarkerComponent, RoomComponent> marker2Room) {
        zoomablePanel.getOverrideStyle().clear();

        Set<Node> allAccessibleNodes = getAllAccessibleNodes(rootNode);
        OverrideStyle accessibleVisitedStyle = new OverrideStyle(accessibleVisitedColor, false);
        OverrideStyle accessibleNotVisitedStyle = new OverrideStyle(accessibleNotVisitedColor, false);
        OverrideStyle accessibleMarkerStyle = new OverrideStyle(Color.WHITE, true);
        for (Node accessibleNode : allAccessibleNodes) {
            OverrideStyle style = accessibleNode.getGui().isVisited() ? accessibleVisitedStyle : accessibleNotVisitedStyle;
            zoomablePanel.getOverrideStyle().put(accessibleNode.getGui(), style);
        }

        Set<RoomComponent> allAccessibleRooms = allAccessibleNodes.stream().map(node -> node.getGui()).collect(Collectors.toSet());
        for (MarkerComponent marker : marker2Room.keySet()) {
            //if marker is interactable and marker is not interacted and in accessible room
            if (marker.isInteractable() && !marker.isInteracted() && allAccessibleRooms.contains(marker2Room.get(marker))) {
                zoomablePanel.getOverrideStyle().put(marker, accessibleMarkerStyle);
            }
        }

        zoomablePanel.repaint();
    }

    private Set<Node> getAllAccessibleNodes(Node root) {
        Set<Node> processedNodes = new HashSet<>();
        List<Node> front = Arrays.asList(root);
        while (!front.isEmpty()) {
            front = traverseNodes(front, processedNodes);
        }
        return processedNodes;
    }

    private List<Node> traverseNodes(List<Node> front, Set<Node> processedNodes) {
        List<Node> newFront = new ArrayList<>();
        for (Node node : front) {
            if (processedNodes.contains(node)) {
                continue;
            }
            processedNodes.add(node);
            for (Connection connection : node.getConnections()) {
                if (!processedNodes.contains(connection.getNextNode())) {
                    newFront.add(connection.getNextNode());
                }
            }
        }

        return newFront;
    }
}
