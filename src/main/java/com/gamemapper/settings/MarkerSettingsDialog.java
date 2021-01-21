package com.gamemapper.settings;

import com.gamemapper.components.InteractionsEditorPanel;
import com.gamemapper.components.PicturePanel;
import com.gamemapper.components.zoomablecomponents.MarkerComponent;
import com.gamemapper.data.FileBufferedImage;
import com.gamemapper.utils.FileChooser;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;

/**
 * @author Dmitry
 */
public class MarkerSettingsDialog extends JDialog {

    private static final Set<MarkerComponent> openedMarkers = new HashSet<>();
    private InteractionsEditorPanel interactionsEditorPanel;
    private JTextField picturePathTextField;
    private JTextField markerNameTextField;
    private JCheckBox interactedCheckBox;
    private JTextArea notesTextArea;
    private MarkerComponent marker;
    private boolean closed = false;

    public MarkerSettingsDialog(JFrame parent, MarkerComponent marker) {
        super(parent, false);
        setTitle("Marker settings");
        if (openedMarkers.contains(marker)) {
            closed = true;
            dispose();
            return;
        }
        openedMarkers.add(marker);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                openedMarkers.remove(marker);
            }
        });

        this.marker = marker;
        getContentPane().setLayout(new MigLayout());

        markerNameTextField = new JTextField();
        markerNameTextField.setText(marker.getName());
        markerNameTextField.setPreferredSize(new Dimension(100, 24));
        markerNameTextField.getDocument().addDocumentListener(createCheckModificationDocumentListener());
        interactedCheckBox = new JCheckBox("Interacted");
        interactedCheckBox.setSelected(marker.isInteracted());
        interactedCheckBox.addItemListener((ItemEvent e) -> {
            SwingUtilities.invokeLater(() -> {
                if (interactedCheckBox.isSelected()) {
                    int result = JOptionPane.showConfirmDialog(MarkerSettingsDialog.this, "Execute interaction actions?", "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        marker.interact();
                    }
                    if (result == JOptionPane.CANCEL_OPTION) {
                        interactedCheckBox.setSelected(false);
                    }
                }

                marker.setInteracted(interactedCheckBox.isSelected());
            });

        });
        notesTextArea = new JTextArea();
        notesTextArea.setText(marker.getNotes());
        notesTextArea.setFont(markerNameTextField.getFont());
        notesTextArea.getDocument().addDocumentListener(createCheckModificationDocumentListener());
        JScrollPane notesScrollPane = new JScrollPane(notesTextArea);

        interactionsEditorPanel = new InteractionsEditorPanel(marker.getInteractions());

        getContentPane().add(new JLabel("Name:"));
        getContentPane().add(markerNameTextField, "wrap,width 100%");
        getContentPane().add(interactedCheckBox, "wrap,span 2");
        getContentPane().add(createImageSelectionPanel(), "wrap,span 2,width 100%");
        getContentPane().add(notesScrollPane, "wrap,span 2,width 100%, wmin 300,height 100");
        getContentPane().add(interactionsEditorPanel, "wrap,span 2,width 100%,height 100");

        pack();
    }

    private JPanel createImageSelectionPanel() {
        JPanel panel = new JPanel(new MigLayout());
        PicturePanel picturePanel = new PicturePanel();
        picturePanel.setPreferredSize(new Dimension(32, 32));
        picturePanel.setMinimumSize(new Dimension(32, 32));
        picturePanel.setImage(marker.getImage());
        picturePathTextField = new JTextField();
        picturePathTextField.setText(marker.getImage().getPath());
        JButton selectNewImageButton = new JButton("Select");
        selectNewImageButton.addActionListener((ActionEvent e) -> {
            loadNewImage(picturePanel);
        });

        panel.add(picturePanel, "wrap");
        panel.add(picturePathTextField, "width 100%");
        panel.add(selectNewImageButton);
        return panel;
    }

    private void loadNewImage(PicturePanel picturePanel) {
        FileChooser fileChooser = new FileChooser("imageFolder");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images(jpg,jpeg,png,bmp)", "jpg", "jpeg", "png", "bmp"));
        if (fileChooser.showOpenDialog(this) == FileChooser.APPROVE_OPTION) {
            FileBufferedImage image = FileBufferedImage.load(fileChooser.getSelectedFile().getAbsolutePath());
            picturePathTextField.setText(image.getPath());
            picturePanel.setImage(image);
            marker.setImage(image);
            marker.setWidth(image.getWidth());
            marker.setHeight(image.getHeight());
            marker.setMarginX(-image.getWidth() / 2);
            marker.setMarginY(-image.getHeight() / 2);
            fileChooser.rememberFolder();
        }
    }

    private DocumentListener createCheckModificationDocumentListener() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                marker.setNotes(notesTextArea.getText());
                marker.setName(markerNameTextField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                marker.setNotes(notesTextArea.getText());
                marker.setName(markerNameTextField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                marker.setNotes(notesTextArea.getText());
                marker.setName(markerNameTextField.getText());
            }
        };
    }

    public void showSettingsDialog(int x, int y) {
        if (!closed) {
            setLocation(x, y);
            setVisible(true);
        }
    }
}
