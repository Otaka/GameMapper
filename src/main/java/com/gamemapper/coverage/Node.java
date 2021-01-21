package com.gamemapper.coverage;

import com.gamemapper.components.zoomablecomponents.RoomComponent;
import java.util.ArrayList;
import java.util.List;

public class Node {

    private final RoomComponent gui;
    private final List<Connection> connections = new ArrayList<>();

    public Node(RoomComponent gui) {
        this.gui = gui;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public RoomComponent getGui() {
        return gui;
    }
}
