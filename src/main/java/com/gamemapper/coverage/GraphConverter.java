package com.gamemapper.coverage;

import com.gamemapper.utils.Utils;
import com.gamemapper.components.zoomablecomponents.ArrowComponent;
import com.gamemapper.components.zoomablecomponents.MarkerComponent;
import com.gamemapper.components.zoomablecomponents.RoomComponent;
import com.gamemapper.components.zoomablepanel.ZoomableComponent;
import com.gamemapper.data.VariablesStorage;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitry
 */
public class GraphConverter {

    List<ZoomableComponent> zoomableComponents;
    Map<RoomComponent, List<ArrowComponent>> room2StartOfArrow = new HashMap<>();
    Map<RoomComponent, List<ArrowComponent>> room2EndOfArrow = new HashMap<>();
    Map<ArrowComponent, List<RoomComponent>> arrow2Room = new HashMap<>();
    Map<MarkerComponent, RoomComponent> marker2Room = new HashMap<>();
    Map<RoomComponent, Node> roomToNode = new HashMap<>();
    Set<RoomComponent> processedRooms = new HashSet<>();
    VariablesStorage variables;

    public Node convertGraph(List<ZoomableComponent> zoomableComponents, MarkerComponent initialMarker, VariablesStorage variables) {
        this.variables = variables;
        this.zoomableComponents = zoomableComponents;
        extractRoomsToArrowsAndMarkerRelations();
        if (!marker2Room.containsKey(initialMarker)) {
            throw new IllegalStateException("Initial marker should be in some room, but it is not.");
        }

        zoomableComponents.stream()
                .filter(comp -> comp instanceof RoomComponent)
                .forEach(room -> roomToNode.put((RoomComponent) room, new Node((RoomComponent) room)));

        RoomComponent initialRoom = marker2Room.get(initialMarker);
        Node initialNode = roomToNode.get(initialRoom);
        List<RoomComponent> front = new ArrayList<>();
        front.add(initialRoom);
        while (!front.isEmpty()) {
            List<RoomComponent> newFront = traverseNodes(front);
            front = newFront;
        }

        return initialNode;
    }

    public Map<MarkerComponent, RoomComponent> getMarker2RoomInfo() {
        return marker2Room;
    }

    
    private List<RoomComponent> traverseNodes(List<RoomComponent> front) {
        List<RoomComponent> newFront = new ArrayList<>();
        for (RoomComponent room : front) {
            if (processedRooms.contains(room)) {
                continue;
            }
            Node roomNode = roomToNode.get(room);
            processedRooms.add(room);
            List<ArrowComponent> arrowsFromNode = getArrowsFromRoom(room);
            for (ArrowComponent arrow : arrowsFromNode) {
                if (variables.checkConditions(arrow.getConditions())) {
                    RoomComponent nextRoom = getNextNode(arrow, room);
                    roomNode.getConnections()
                            .add(new Connection(arrow, roomToNode.get(nextRoom)));
                    if (!processedRooms.contains(nextRoom)) {
                        newFront.add(nextRoom);
                    }
                }
            }
        }

        return newFront;
    }

    private RoomComponent getNextNode(ArrowComponent arrow, RoomComponent oneSideRoom) {
        List<RoomComponent> rooms = arrow2Room.get(arrow);
        for (RoomComponent roomComponent : rooms) {
            if (roomComponent != oneSideRoom) {
                return roomComponent;
            }
        }
        return null;
    }

    private List<ArrowComponent> getArrowsFromRoom(RoomComponent room) {
        Set<ArrowComponent> arrows = new HashSet<>(room2StartOfArrow.getOrDefault(room, new ArrayList<>()));
        room2EndOfArrow.getOrDefault(room, new ArrayList<>()).stream()
                .filter(arrow -> arrow.isBidirectional())
                .forEach(arrow -> arrows.add(arrow));

        return new ArrayList<>(arrows);
    }

    private void extractRoomsToArrowsAndMarkerRelations() {
        List<RoomComponent> rooms = zoomableComponents
                .stream().filter(comp -> comp instanceof RoomComponent)
                .map(comp -> (RoomComponent) comp).collect(Collectors.toList());

        List<MarkerComponent> markers = zoomableComponents
                .stream().filter(comp -> comp instanceof MarkerComponent)
                .map(comp -> (MarkerComponent) comp).collect(Collectors.toList());
        List<MarkerComponent> markersToRemove = new ArrayList<>();
        for (RoomComponent room : rooms) {
            List<ArrowComponent> arrows = listPassableArrows();
            for (ArrowComponent arrow : arrows) {
                ARROW_TO_ROOM_RELATION relation = checkArrowInRoom(room, arrow);
                if (relation == ARROW_TO_ROOM_RELATION.START) {
                    Utils.getListFromMap(room2StartOfArrow, room).add(arrow);
                    Utils.getListFromMap(arrow2Room, arrow).add(room);
                } else if (relation == ARROW_TO_ROOM_RELATION.END) {
                    Utils.getListFromMap(room2EndOfArrow, room).add(arrow);
                    Utils.getListFromMap(arrow2Room, arrow).add(room);
                }
            }
            for (MarkerComponent marker : markers) {
                if (checkMarkerInRoom(room, marker)) {
                    marker2Room.put(marker, room);
                    markersToRemove.add(marker);
                }
            }
            if (!markersToRemove.isEmpty()) {
                markers.removeAll(markersToRemove);
                markersToRemove.clear();
            }
        }
    }

    private boolean checkMarkerInRoom(RoomComponent room, MarkerComponent marker) {
        return room.containsPoint(Math.round(marker.getX()), Math.round(marker.getY()));
    }

    private ARROW_TO_ROOM_RELATION checkArrowInRoom(RoomComponent room, ArrowComponent arrow) {
        Point arrowStartPoint = arrow.getGlobalStartPoint();
        if (room.containsPoint(arrowStartPoint.x, arrowStartPoint.y)) {
            return ARROW_TO_ROOM_RELATION.START;
        }

        Point arrowEndPoint = arrow.getGlobalEndPoint();
        if (room.containsPoint(arrowEndPoint.x, arrowEndPoint.y)) {
            return ARROW_TO_ROOM_RELATION.END;
        }
        return ARROW_TO_ROOM_RELATION.NONE;
    }

    private List<ArrowComponent> listPassableArrows() {
        return zoomableComponents.stream()
                .filter(comp -> comp instanceof ArrowComponent)
                .map(comp -> (ArrowComponent) comp)
                .filter(arrow -> checkIfPassable(arrow))
                .collect(Collectors.toList());
    }

    private boolean checkIfPassable(ArrowComponent arrow) {
        //return true;
        return VariablesStorage.get().checkConditions(arrow.getConditions());
    }

    private enum ARROW_TO_ROOM_RELATION {
        START, END, NONE
    }
}
