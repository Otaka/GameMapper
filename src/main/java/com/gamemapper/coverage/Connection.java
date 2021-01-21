package com.gamemapper.coverage;

import com.gamemapper.components.zoomablecomponents.ArrowComponent;
import java.util.Objects;

/**
 *
 * @author Dmitry
 */
public class Connection {

    private ArrowComponent gui;
    private Node nextNode;

    public Connection() {
    }

    public Connection(ArrowComponent gui, Node nextNode) {
        this.gui = gui;
        this.nextNode = nextNode;
    }

    public float getWeight() {
        return gui.getWeight();
    }

    public ArrowComponent getGui() {
        return gui;
    }

    public Node getNextNode() {
        return nextNode;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.gui);
        hash = 53 * hash + Objects.hashCode(this.nextNode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Connection other = (Connection) obj;
        if (!Objects.equals(this.gui, other.gui)) {
            return false;
        }
        if (!Objects.equals(this.nextNode, other.nextNode)) {
            return false;
        }
        return true;
    }

}
