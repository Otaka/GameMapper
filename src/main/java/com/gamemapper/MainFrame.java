package com.gamemapper;

import com.gamemapper.components.zoomablecomponents.ArrowComponent;
import com.gamemapper.components.zoomablecomponents.BackgroundComponent;
import com.gamemapper.components.zoomablecomponents.MarkerComponent;
import com.gamemapper.components.zoomablecomponents.RoomComponent;
import com.gamemapper.components.zoomablepanel.ZoomableComponent;
import com.gamemapper.components.zoomablepanel.ZoomablePanel;
import com.gamemapper.components.zoomablepanel.instruments.sub.BaseSubInstrument;
import com.gamemapper.coverage.CoverageDialog;
import com.gamemapper.data.FileBufferedImage;
import com.gamemapper.data.SerializationContext;
import com.gamemapper.data.VariablesStorage;
import com.gamemapper.settings.SettingsDialog;
import com.gamemapper.settings.SettingsManager;
import com.gamemapper.utils.ChunkedTextCollector;
import com.gamemapper.utils.FileChooser;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame {

    private ZoomablePanel zoomablePanel;
    private JCheckBoxMenuItem lockBackgroundMenu;
    private Point lastPopupMenuPoint;
    private File currentFile;
    private ChunkedTextCollector captionChunkedTextCollector;

    public MainFrame() throws HeadlessException, IOException {
        init();
    }

    private void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 500);
        setTitle("Game path tracker");
        captionChunkedTextCollector = new ChunkedTextCollector(this::setTitle);
        captionChunkedTextCollector.setTitles("appName", "Game path tracker", "dash", " - ", "fileName", "New");
        setLayout(new BorderLayout(0, 0));
        zoomablePanel = createZoomablePanel();
        add(zoomablePanel, BorderLayout.CENTER);
        createMenu();
        zoomablePanel.getSelectInstrument().addSubInstrument(createPopupMenuInstrument());
        zoomablePanel.getSelectInstrument().addSubInstrument(createOpenComponentInstrument());

        initKeyInput();
    }

    private void initKeyInput() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                zoomablePanel.getCurrentInstrument().onKeyPressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                zoomablePanel.getCurrentInstrument().onKeyReleased(e.getKeyCode());
            }
        });
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener((ActionEvent arg0) -> {
            open();
        });
        fileMenu.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener((ActionEvent arg0) -> {
            save();
        });
        fileMenu.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.addActionListener((ActionEvent arg0) -> {
            saveAs();
        });
        fileMenu.add(saveAsMenuItem);

        fileMenu.addSeparator();
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener((ActionEvent arg0) -> {
            exit();
        });
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(lockBackgroundMenu = new JCheckBoxMenuItem("Lock background"));
        lockBackgroundMenu.addActionListener((ActionEvent e) -> lockBackground(lockBackgroundMenu.isSelected()));
        menuBar.add(editMenu);
        editMenu.addSeparator();
        JMenuItem settingsMenu = new JMenuItem("Settings...");
        settingsMenu.addActionListener((ActionEvent e) -> showSettingsDialog());
        editMenu.add(settingsMenu);

        JMenu pathPlanningMenu = new JMenu("Planning");
        JMenuItem coverageAnalysis = new JMenuItem("Coverage...");
        coverageAnalysis.addActionListener((ActionEvent e) -> {
            showCoverageDialog();
        });
        pathPlanningMenu.add(coverageAnalysis);
        menuBar.add(pathPlanningMenu);
        setJMenuBar(menuBar);
    }

    private void showCoverageDialog() {
        CoverageDialog coverageDialog = new CoverageDialog(this, zoomablePanel);
        coverageDialog.setVisible(true);
    }

    private void showSettingsDialog() {
        SettingsDialog settings = new SettingsDialog(this);
        settings.setVisible(true);
    }

    private BaseSubInstrument createOpenComponentInstrument() {
        return new BaseSubInstrument(zoomablePanel) {
            @Override
            public boolean onDoubleClick(int x, int y, int innerX, int innerY, int button) {
                showComponentSettings(x, y);
                return true;
            }
        };
    }

    private BaseSubInstrument createPopupMenuInstrument() {
        return new BaseSubInstrument(zoomablePanel) {
            @Override
            public boolean onMousePressed(int x, int y, int innerX, int innerY, int button) {
                if (button == 3) {
                    showPopupMenu(x, y);
                    return false;
                }
                return true;
            }
        };
    }

    public void showComponentSettings(int x, int y) {
        ZoomableComponent selectedComponent = zoomablePanel.getSelectedComponent();
        if (selectedComponent != null) {
            selectedComponent.onOpenSettings(x, y);
        }
    }

    public void showPopupMenu(int x, int y) {
        lastPopupMenuPoint = new Point(x, y);
        List<JMenuItem> menusList = new ArrayList<>();
        ZoomableComponent selectedComponent = zoomablePanel.getSelectedComponent();
        if (selectedComponent != null) {
            selectedComponent.createPopupMenu(menusList);
        }
        createPopupMenu(menusList);

        JPopupMenu popupMenu = new JPopupMenu();
        for (JMenuItem menu : menusList) {
            popupMenu.add(menu);
        }

        popupMenu.show(zoomablePanel, x, y);
    }

    private void createPopupMenu(List<JMenuItem> menus) {
        if (!backgroundLocked()) {
            JMenuItem createBackgroundMenu = new JMenuItem("Create background");
            createBackgroundMenu.addActionListener((ActionEvent event) -> {
                createBackground();
            });
            menus.add(createBackgroundMenu);
        }

        JMenuItem createRoom = new JMenuItem("Create room");
        menus.add(createRoom);
        createRoom.addActionListener((ActionEvent event) -> {
            createRoom();
        });

        JMenuItem createArrow = new JMenuItem("Create arrow");
        menus.add(createArrow);
        createArrow.addActionListener((ActionEvent event) -> {
            createArrow();
        });

        JMenuItem createMarker = new JMenuItem("Create marker");
        menus.add(createMarker);
        createMarker.addActionListener((ActionEvent event) -> {
            createMarker();
        });

        for (JMenuItem menu : zoomablePanel.getCurrentInstrument().createPopupMenuItems()) {
            menus.add(menu);
        }
    }

    private void lockBackground(boolean lock) {
        for (ZoomableComponent component : zoomablePanel.getZoomableComponents()) {
            if (component instanceof BackgroundComponent) {
                ((BackgroundComponent) component).setSelectable(!lock);
            }
        }
    }

    public void createMarker() {
        FileBufferedImage image = FileBufferedImage.load("resource:/com/resources/icons/001-fish.png");
        MarkerComponent markerComponent = new MarkerComponent(image, 0, 0, image.getWidth(), image.getHeight());
        markerComponent.setX(zoomablePanel.screenPointXToInnerPoint(lastPopupMenuPoint.x));
        markerComponent.setY(zoomablePanel.screenPointYToInnerPoint(lastPopupMenuPoint.y));
        markerComponent.setMarginX(-image.getWidth() / 2);
        markerComponent.setMarginY(-image.getHeight() / 2);
        markerComponent.setName("Marker");

        zoomablePanel.addChild(markerComponent);
    }

    public void createRoom() {
        RoomComponent roomComponent = new RoomComponent();
        roomComponent.setX(zoomablePanel.screenPointXToInnerPoint(lastPopupMenuPoint.x));
        roomComponent.setY(zoomablePanel.screenPointYToInnerPoint(lastPopupMenuPoint.y));
        roomComponent.defaultShape();
        zoomablePanel.addChild(roomComponent);
        zoomablePanel.selectComponent(roomComponent);
    }

    public void createArrow() {
        ArrowComponent arrow = new ArrowComponent(0, 0);
        arrow.setX(zoomablePanel.screenPointXToInnerPoint(lastPopupMenuPoint.x));
        arrow.setY(zoomablePanel.screenPointYToInnerPoint(lastPopupMenuPoint.y));
        arrow.setStartPoint(new Point(0, 0));
        arrow.setEndPoint(new Point(20, 0));
        zoomablePanel.addChild(arrow);
        zoomablePanel.selectComponent(arrow);
    }

    private void createBackground() {
        FileChooser fileChooserDialog = new FileChooser("imageFolder");
        if (fileChooserDialog.showOpenDialog(this) == FileChooser.APPROVE_OPTION) {
            fileChooserDialog.rememberFolder();
            FileBufferedImage image = FileBufferedImage.load(fileChooserDialog.getSelectedFile().getAbsolutePath());
            BackgroundComponent backgroundComponent = new BackgroundComponent(image, 0, 0, image.getWidth(), image.getHeight());
            zoomablePanel.addChild(backgroundComponent);
            repaint();
        }
    }

    private void exit() {
        System.exit(0);
    }

    private boolean backgroundLocked() {
        return lockBackgroundMenu.isSelected();
    }

    private ZoomablePanel createZoomablePanel() {
        zoomablePanel = new ZoomablePanel();
        add(zoomablePanel, BorderLayout.CENTER);
        return zoomablePanel;
    }

    private void saveAs() {
        File tf = currentFile;
        currentFile = null;
        if (!save()) {
            currentFile = tf;
        }
    }

    private boolean save() {
        if (currentFile == null) {
            FileChooser fileChooser = new FileChooser("saveFileFolder");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Game Mapper (*.mpr)", "mpr"));
            if (fileChooser.showSaveDialog(this) != FileChooser.APPROVE_OPTION) {
                return false;
            }
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".mpr")) {
                path += ".mpr";
            }
            currentFile = new File(path);
            captionChunkedTextCollector.setTitle("fileName", currentFile.getName());
            fileChooser.rememberFolder();
        }

        return save(currentFile);
    }

    private boolean save(File file) {
        try {
            SerializationContext serializationContext = new SerializationContext();
            serializationContext.setFile(file);
            serializationContext.setFolder(file.getParentFile());
            JsonObject jsonRoot = new JsonObject();
            jsonRoot.add("version", new JsonPrimitive(1));
            jsonRoot.add("variables", VariablesStorage.get().serialize(serializationContext));
            jsonRoot.add("components", zoomablePanel.serialize(serializationContext));
            jsonRoot.add("settings", SettingsManager.serialize(serializationContext));
            jsonRoot.add("checkedMenus", serializeCheckedMenus(serializationContext));

            try (JsonWriter writer = new JsonWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                new GsonBuilder().setPrettyPrinting().create()
                        .toJson(jsonRoot, writer);
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Saving error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private JsonObject serializeCheckedMenus(SerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("lockedBackground", new JsonPrimitive(lockBackgroundMenu.isSelected()));
        return result;
    }

    private void deserializeCheckedMenus(JsonObject serializedMenuData) {
        if (serializedMenuData.has("lockedBackground")) {
            if (serializedMenuData.get("lockedBackground").getAsBoolean()) {
                lockBackground(true);
                lockBackgroundMenu.setSelected(true);
            }
        }
    }

    private void open() {
        FileChooser fileChooser = new FileChooser("saveFileFolder");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Game Mapper (*.mpr)", "mpr"));
        if (fileChooser.showOpenDialog(this) == FileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            File file = new File(path);
            open(file);
            currentFile = file;
            captionChunkedTextCollector.setTitle("fileName", currentFile.getName());
            fileChooser.rememberFolder();
        }
    }

    private void open(File file) {
        try {
            try (JsonReader reader = new JsonReader(new FileReader(file, StandardCharsets.UTF_8))) {
                SerializationContext serializationContext = new SerializationContext();
                serializationContext.setFile(file);
                serializationContext.setFolder(file.getParentFile());
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                int version = readVersion(root);

                if (root.has("variables")) {
                    JsonArray serializedVariables = root.getAsJsonArray("variables");
                    VariablesStorage.get().deserialize(serializedVariables, version);
                } else {
                    throw new IllegalStateException("File does not have [variables] section");
                }

                if (root.has("settings")) {
                    JsonObject serializedSettings = root.getAsJsonObject("settings");
                    SettingsManager.deserialize(serializedSettings, version);
                } else {
                    throw new IllegalStateException("File does not have [settings] section");
                }

                if (root.has("components")) {
                    JsonArray serializedData = root.getAsJsonArray("components");
                    zoomablePanel.deserialize(serializationContext, serializedData, version);
                } else {
                    throw new IllegalStateException("File does not have [components] section");
                }

                if (root.has("checkedMenus")) {
                    JsonObject serializedMenusSettings = root.getAsJsonObject("checkedMenus");
                    deserializeCheckedMenus(serializedMenusSettings);
                } else {
                    throw new IllegalStateException("File does not have [checkedMenus] section");
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Saving error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int readVersion(JsonObject root) {
        if (!root.has("version")) {
            throw new IllegalStateException("File does not have version information");
        }
        return root.get("version").getAsInt();
    }
}
