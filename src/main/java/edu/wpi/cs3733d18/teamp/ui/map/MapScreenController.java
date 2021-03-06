package edu.wpi.cs3733d18.teamp.ui.map;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Settings;
import com.sun.scenario.effect.Effect;
import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import edu.wpi.cs3733d18.teamp.ui.Originator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import edu.wpi.cs3733d18.teamp.ui.home.BounceTransition;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.stage.Stage;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import jdk.nashorn.internal.runtime.regexp.joni.constants.NodeType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.*;

import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import java.math.*;
import java.util.List;

public class MapScreenController {

    private int X_OFFSET = -523;
    private int Y_OFFSET = 0;
    private double X_SCALE = 1588.235294 / 5000.0;
    private double Y_SCALE = 1080.0 / 3400.0;
    private final double NODE_RADIUS = 3.0;
    private final double EDGE_WIDTH = 1.0;
    private final int IMG_WIDTH = 5000;
    private int IMG_HEIGHT = 3400;
    private final double ZOOM_3D_MIN = 1.013878875;
    private final double ZOOM_2D_MIN = 1.208888889;
    private double zoomForTranslate = 0;
    private static int MAP_ICON_SIZE = 18;

    double maxXCoord = 0;
    double maxYCoord = 0;
    double minXCoord = 5000;
    double minYCoord = 3400;

    double nodeIconScale = 1.2;
    private Node startNode;
    private Node endNode;
    private Edge selectedEdge;

    //this is for creating new edges
    private Node firstSelect = null;
    private Node secondSelect = null;
    private Boolean firstChoice = true;

    private Boolean pathDrawn = false;
    private Boolean toggleOn = false;

    private static HashMap<String, Circle> nodeDispSet = new HashMap<>();
    private static HashMap<String, javafx.scene.Node> iconDispSet = new HashMap<>();
    private static ArrayList<BounceTransition> animSet = new ArrayList<>();
    private static ArrayList<Polygon> arrowDispSet = new ArrayList<>();
    private static ArrayList<String> arrowFloorSet = new ArrayList<>();
    private static HashMap<String, Line> edgeDispSet = new HashMap<>();
    private ArrayList<Node> stairNodeSet = new ArrayList<Node>();
    private ArrayList<Node> recentStairNodeSet = new ArrayList<Node>();
    private ArrayList<Node> recentElevNodeSet = new ArrayList<Node>();
    private ArrayList<Node.floorType> floorsList = new ArrayList<>();
    private ArrayList<JFXButton> floorSequenceList = new ArrayList<>();

    Thread thread;

    DBSystem db = DBSystem.getInstance();

    Node.floorType currentFloor;
    String floorState;

    @FXML
    BorderPane buttonOverlayPane;

    @FXML
    BorderPane searchBarOverlayPane;

    @FXML
    JFXButton backButton;

    @FXML
    JFXSlider zoomSlider;

    @FXML
    StackPane mapPane;

    @FXML
    ImageView mapImage;

    @FXML
    AnchorPane nodesPane;

    @FXML
    AnchorPane edgePane;

    @FXML
    AnchorPane arrowPane;

    @FXML
    HBox floorSequenceHBox;

    @FXML
    JFXButton floorL2Button;

    @FXML
    JFXButton floorL1Button;

    @FXML
    JFXButton floorGButton;

    @FXML
    JFXButton floor1Button;

    @FXML
    JFXButton floor2Button;

    @FXML
    JFXButton floor3Button;

    @FXML
    static PopOver popOver;
    Boolean popOverHidden = true;

    @FXML
    AnchorPane mapScreenAnchorPane;

    SearchBarOverlayController searchBarOverlayController = null;
    MapScreenController mapScreenController;

    /**
     * intializes values such as
     * adminMapViewController to this object
     * sets the current floor
     * initializes the zoom slider and binds it to the map
     * draws the initial screen and sets mouse events
     */
    @FXML
    public void onStartUp() {
        mapScreenController = this;

        floorState = floor2Button.getText();
        currentFloor = Node.floorType.LEVEL_2;
        zoomSlider.setValue(1);

        zoomSlider.setMin(1.20888889);
        zoomSlider.setValue(zoomSlider.getMin());
        zoomForTranslate = zoomSlider.getValue();

        Image newImage = new Image("/img/maps/2d/02_thesecondfloor.png");
        mapImage.setImage(newImage);
        mapImage.scaleXProperty().bind(zoomSlider.valueProperty());
        mapImage.scaleYProperty().bind(zoomSlider.valueProperty());
        nodesPane.scaleXProperty().bind(zoomSlider.valueProperty());
        nodesPane.scaleYProperty().bind(zoomSlider.valueProperty());
        edgePane.scaleXProperty().bind(zoomSlider.valueProperty());
        edgePane.scaleYProperty().bind(zoomSlider.valueProperty());
        arrowPane.scaleXProperty().bind(zoomSlider.valueProperty());
        arrowPane.scaleYProperty().bind(zoomSlider.valueProperty());

        firstSelected = true;

        mapImage.addEventHandler(MouseEvent.ANY, mouseEventEventHandler);
        drawNodes();
        getMap();
        addOverlay();

        searchBarOverlayController.setSourceSearchBar("Current Kiosk");
        mapScreenAnchorPane.addEventHandler(MouseEvent.ANY, testMouseEvent);
        thread = new Thread(task);
        thread.start();
    }

    @FXML
    public void onStartUp3D(Boolean pathDrawn, ArrayList<Node> pathMade, String startLocation, String endLocation) {
        this.pathDrawn = pathDrawn;
        this.pathMade = pathMade;
        searchBarOverlayController.setSourceSearchBar(startLocation);
        searchBarOverlayController.setDestinationSearchBar(endLocation);
        drawPath(pathMade);
    }

    @FXML
    public void onNoPathStartup(String startLocation, String endLocation) {
        searchBarOverlayController.setSourceSearchBar(startLocation);
        searchBarOverlayController.setDestinationSearchBar(endLocation);
    }

    public ArrayList<Node> getPathMade() {
        return this.pathMade;
    }

    /**
     * Creates new thread that increments a counter while mouse is inactive, revert to homescreen if
     * timer reaches past a set value by administrator
     */
    Task task = new Task() {
        @Override
        protected Object call() throws Exception {
            try {
                int timeout = Settings.getTimeDelay();
                int counter = 0;

                while(counter <= timeout) {
                    Thread.sleep(5);
                    counter += 5;
                }
                Scene scene;
                Parent root;
                FXMLLoader loader;
                scene = backButton.getScene();

                loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
                try {
                    root = loader.load();
                    scene.setRoot(root);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            } catch (InterruptedException v) {
                System.out.println(v);
                thread = new Thread(task);
                thread.start();
                return null;
            }
            return null;
        }
    };

    /**
     * Handles active mouse events by interrupting the current thread and setting a new thread and timer
     * when the mouse moves. This makes sure that while the user is active, the screen will not time out.
     */
    EventHandler<MouseEvent> testMouseEvent = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            Originator localOriginator = new Originator();
            long start, now;
            localOriginator.setState("Active");
            localOriginator.saveStateToMemento();
            thread.interrupt();

            try{
                thread.join();
            } catch (InterruptedException ie){
                System.out.println(ie);
            }

            Task task2 = new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        int timeout = Settings.getTimeDelay();
                        int counter = 0;

                        while(counter <= timeout) {
                            Thread.sleep(5);
                            counter += 5;
                        }
                        Scene scene;
                        Parent root;
                        FXMLLoader loader;
                        scene = backButton.getScene();

                        loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
                        try {
                            root = loader.load();
                            scene.setRoot(root);
                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }
                    } catch (InterruptedException v) {
                        System.out.println(v);
                        thread = new Thread(task);
                        thread.start();
                        return null;
                    }
                    return null;
                }
            };

            thread = new Thread(task2);
            thread.start();
        }
    };




    @FXML
    public void backButtonOp() {
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }

        backButton.getScene().setRoot(root);
    }


    @FXML
    public void floorL2ButtonOp(ActionEvent e) {
        floorState = floorL2Button.getText();
        currentFloor = Node.floorType.LEVEL_L2;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    @FXML
    public void floorL1ButtonOp(ActionEvent e) {
        floorState = floorL1Button.getText();
        currentFloor = Node.floorType.LEVEL_L1;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    @FXML
    public void floorGButtonOp(ActionEvent e) {
        floorState = floorGButton.getText();
        currentFloor = Node.floorType.LEVEL_G;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    @FXML
    public void floor1ButtonOp(ActionEvent e) {
        floorState = floor1Button.getText();
        currentFloor = Node.floorType.LEVEL_1;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    @FXML
    public void floor2ButtonOp(ActionEvent e) {
        floorState = floor2Button.getText();
        currentFloor = Node.floorType.LEVEL_2;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    @FXML
    public void floor3ButtonOp(ActionEvent e) {
        floorState = floor3Button.getText();
        currentFloor = Node.floorType.LEVEL_3;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    /**
     * switch statement that determines which map is loaded in
     */
    public void getMap() {
        Image image;
        Image oldImage = mapImage.getImage();

        if (toggleOn) {
            X_OFFSET = 0;
            Y_OFFSET = -19;
            X_SCALE = 1920.0 / 5000.0;
            Y_SCALE = 1065.216 / 2774.0;
            IMG_HEIGHT = 2774;
            zoomSlider.setMin(ZOOM_3D_MIN);
            zoomSlider.setValue(ZOOM_3D_MIN);
            zoomForTranslate = zoomSlider.getValue();
            switch (floorState) {
                case "3":
                    image = new Image("/img/maps/3d/3-NO-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_3);
                    break;
                case "2":
                    image = new Image("/img/maps/3d/2-NO-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_2);
                    break;
                case "1":
                    image = new Image("/img/maps/3d/1-NO-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_1);
                    break;
                case "G":
                    image = new Image("/img/maps/3d/1-NO-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_G);
                    break;
                case "L1":
                    image = new Image("/img/maps/3d/L1-NO-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_L1);
                    break;
                default:
                    image = new Image("/img/maps/3d/L2-NO-ICONS.png");
                    setFloorStyleClass(Node.floorType.LEVEL_L2);
                    break;
            }
        } else {
            X_OFFSET = -523;
            Y_OFFSET = 0;
            X_SCALE = 1588.235294 / 5000.0;
            Y_SCALE = 1080.0 / 3400.0;
            IMG_HEIGHT = 3400;
            zoomSlider.setMin(ZOOM_2D_MIN);
            zoomSlider.setValue(ZOOM_2D_MIN);
            zoomForTranslate = zoomSlider.getValue();
            switch (floorState) {
                case "3":
                    image = new Image("/img/maps/2d/03_thethirdfloor.png");
                    setFloorStyleClass(Node.floorType.LEVEL_3);
                    break;
                case "2":
                    image = new Image("/img/maps/2d/02_thesecondfloor.png");
                    setFloorStyleClass(Node.floorType.LEVEL_2);
                    break;
                case "1":
                    image = new Image("/img/maps/2d/01_thefirstfloor.png");
                    setFloorStyleClass(Node.floorType.LEVEL_1);
                    break;
                case "G":
                    image = new Image("/img/maps/2d/00_thegroundfloor.png");
                    setFloorStyleClass(Node.floorType.LEVEL_G);
                    break;
                case "L1":
                    image = new Image("/img/maps/2d/00_thelowerlevel1.png");
                    setFloorStyleClass(Node.floorType.LEVEL_L1);
                    break;
                default:
                    image = new Image("/img/maps/2d/00_thelowerlevel2.png");
                    setFloorStyleClass(Node.floorType.LEVEL_L2);
                    break;
            }
        }
        if (!oldImage.equals(image))
            mapImage.setImage(image);
        autoTranslateZoom(zoomSlider.getMin(), zoomSlider.getMin(), 0, 0);
    }


    double orgSceneX = 0;
    double orgSceneY = 0;
    double orgTranslateX = 0;
    double orgTranslateY = 0;

    /**
     * this gets the current position of the mouse before using the mouse
     * to pan the map
     *
     * @param t
     */
    @FXML
    public void getMouseValue(MouseEvent t) {
        orgSceneX = t.getSceneX();
        orgSceneY = t.getSceneY();
        orgTranslateX = mapImage.getTranslateX();
        orgTranslateY = mapImage.getTranslateY();
    }


    double newTranslateX = 0;
    double newTranslateY = 0;

    /**
     * this lets the user scroll the wheel to move the zoom slider
     * This also readjusts the screen based on the bounds
     *
     * @param s
     */
    @FXML
    public void zoomScrollWheel(ScrollEvent s) {
        double newValue = (s.getDeltaY()) / 200.0 + zoomSlider.getValue();
        double change = 0;

        if ((s.getDeltaY() < 0) && (zoomSlider.getValue() != zoomSlider.getMin())) change = 1;
        if ((s.getDeltaY() > 0) && (zoomSlider.getValue() != zoomSlider.getMax())) change = 1;

        double mouseX = s.getSceneX();
        double mouseY = s.getSceneY();

        double orgTranslateX = mapImage.getTranslateX();
        double orgTranslateY = mapImage.getTranslateY();

        double mouseAdjustX = (orgTranslateX + (IMG_WIDTH * zoomSlider.getValue() * X_SCALE * (mouseX / 1920.0)));
        double mouseAdjustY = (orgTranslateY + (IMG_HEIGHT * zoomSlider.getValue() * Y_SCALE * (mouseY / 1080.0)));

        double imageCenterX = (orgTranslateX + (IMG_WIDTH * zoomSlider.getValue() * X_SCALE * 0.5));
        double imageCenterY = (orgTranslateY + (IMG_HEIGHT * zoomSlider.getValue() * Y_SCALE * 0.5));

        double mouseChangeX = mouseAdjustX - imageCenterX;
        double mouseChangeY = mouseAdjustY - imageCenterY;

        newTranslateX = (orgTranslateX * zoomSlider.getValue() / zoomForTranslate) - (change * mouseChangeX * s.getDeltaY() / 256.0);
        newTranslateY = (orgTranslateY * zoomSlider.getValue() / zoomForTranslate) - (change * mouseChangeY * s.getDeltaY() / 256.0);

        zoomSlider.setValue(newValue);

        zoomForTranslate = zoomSlider.getValue();

        double translateSlopeX = X_SCALE * mapImage.getScaleX() * IMG_WIDTH;
        double translateSlopeY = Y_SCALE * mapImage.getScaleY() * IMG_HEIGHT;

        if (newTranslateX > (translateSlopeX - 1920) / 2)
            newTranslateX = (translateSlopeX - 1920) / 2;
        if (newTranslateX < -(translateSlopeX - 1920) / 2)
            newTranslateX = -(translateSlopeX - 1920) / 2;
        if (newTranslateY > (translateSlopeY - 1080) / 2)
            newTranslateY = (translateSlopeY - 1080) / 2;
        if (newTranslateY < -(translateSlopeY - 1080) / 2)
            newTranslateY = -(translateSlopeY - 1080) / 2;

        mapImage.setTranslateX(newTranslateX);
        mapImage.setTranslateY(newTranslateY);
        nodesPane.setTranslateX(newTranslateX);
        nodesPane.setTranslateY(newTranslateY);
        edgePane.setTranslateX(newTranslateX);
        edgePane.setTranslateY(newTranslateY);
        arrowPane.setTranslateX(newTranslateX);
        arrowPane.setTranslateY(newTranslateY);
    }

    /**
     * Draws the nodes according to what was given back from the database
     */
    public void drawNodes() {
        HashMap<String, Node> nodeSet;
        nodeSet = db.getAllNodes();
        System.out.println("drawing icons");

        long startTime = System.currentTimeMillis();
        List<Node> orderedNodes = new ArrayList<>();
        for (Node node : nodeSet.values()) {
            orderedNodes.add(node);
        }
        orderedNodes.sort(Comparator.comparing(Node::getY));

        long endTime = System.currentTimeMillis();
        System.out.println("Ordering nodes took " + (endTime - startTime) + " ms");

        for (Node node : orderedNodes) {
            if (node.getType() != Node.nodeType.HALL) {
                StackPane container = constructIcon(node.getType());
                if (!toggleOn) {
                    container.setLayoutX((node.getX() - X_OFFSET) * X_SCALE - MAP_ICON_SIZE * 0.5);
                    container.setLayoutY((node.getY() - Y_OFFSET) * Y_SCALE - MAP_ICON_SIZE * 1.25);
                } else {
                    container.setLayoutX((node.getxDisplay() - X_OFFSET) * X_SCALE - MAP_ICON_SIZE * 0.5);
                    container.setLayoutY((node.getyDisplay() - Y_OFFSET) * Y_SCALE - MAP_ICON_SIZE * 1.25);
                }
                if (node.getFloor() == currentFloor) {
                    nodesPane.getChildren().add(container);
                    container.addEventHandler(MouseEvent.ANY, nodeClickHandler);
                    container.setOnScroll(nodeScrollHandler);
                }
                iconDispSet.put(node.getID(), container);
            }
        }
    }

    int nodeState = 0;
    /**
     * this handles all mouse events related to nodes that exist in the database
     * and have been drawn
     */
    Boolean firstSelected = false;
    Boolean secondSelected = false;
    EventHandler<MouseEvent> nodeClickHandler = new EventHandler<MouseEvent>() {
        Boolean isDragging = false;
        double orgMouseX;
        double orgMouseY;
        double orgCenterX;
        double orgCenterY;
        double newCenterX;
        double newCenterY;

        @Override
        public void handle(MouseEvent event) {
            HashMap<String, Node> nodeSet;
            nodeSet = db.getAllNodes();
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                if (searchBarOverlayController.isSourceFocused()) {
                    clearStartNode();
                    for (String string : iconDispSet.keySet()) {
                        if (iconDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            searchBarOverlayController.setSourceSearchBar(node.getLongName());
                        }
                    }
                    removeFocus();
                } else if (searchBarOverlayController.isDestinationFocused()) {
                    clearEndNode();
                    for (String string : iconDispSet.keySet()) {
                        if (iconDispSet.get(string) == event.getSource()) {
                            Node node = nodeSet.get(string);
                            searchBarOverlayController.setDestinationSearchBar(node.getLongName());

                        }
                    }
                    removeFocus();
                } else {
                    Boolean foundStair = false;
                    for (String string : iconDispSet.keySet()) {
                        if (iconDispSet.get(string).equals(event.getSource())) {
                            //if this is the icon that was pressed

                            for (int i = 0; i < stairNodeSet.size(); i += 2) {
                                if (stairNodeSet.get(i).getID().equals(string)) {
                                    //going down a stair node
                                    currentFloor = stairNodeSet.get(i + 1).getFloor();
                                    floorState = currentFloor.toString();
                                    foundStair = true;
                                    updateMap();
                                    if (pathDrawn) {
                                        drawPath(pathMade);
                                    }
                                    break;
                                }
                            }
                            for (int i = 1; i < stairNodeSet.size(); i += 2) {
                                if (stairNodeSet.get(i).getID().equals(string)) {
                                    //going back up a stair node
                                    currentFloor = stairNodeSet.get(i - 1).getFloor();
                                    floorState = currentFloor.toString();
                                    foundStair = true;
                                    updateMap();
                                    if (pathDrawn) {
                                        drawPath(pathMade);
                                    }
                                    break;
                                }
                            }
                            if (!foundStair) {
                                clearEndNode();
                                Node node = nodeSet.get(string);
                                searchBarOverlayController.setDestinationSearchBar(node.getLongName());
                            }
                            foundStair = false;
                            break;
                        }
                    }
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_ENTERED && popOverHidden) {
                for (String string : iconDispSet.keySet()) {
                    if (iconDispSet.get(string) == event.getSource()) {
                        if (popOver != null && popOver.getOpacity() == 0) {
                            popOver.hide();
                            popOver = null;
                        }
                        Node node = nodeSet.get(string);
                        iconDispSet.get(node.getID()).toFront();
                        String type = node.getType().toString().toUpperCase();
                        if (type.equals("STAIR")) {
                            type = "STAIRS";
                        } else if (type.equals("CONFERENCE")) {
                            type = "CONFERENCE ROOM";
                        } else if (type.equals("HALL")) {
                            type = "HALLWAY";
                        } else if (type.equals("INFORMATION")) {
                            type = "INFORMATION DESK";
                        } else if (type.equals("LABS")) {
                            type = "LABORATORY";
                        } else if (type.equals("SERVICE")) {
                            type = "SERVICES";
                        }
                        Label nodeTypeLabel = new Label(type);
                        Label nodeLongNameLabel = new Label("Name: " + node.getLongName());
                        Label nodeBuildingLabel = new Label("Building: " + node.getBuilding().toString());
                        nodeTypeLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #0b2f5b; -fx-font-weight: 700; -fx-padding: 10px 10px 0 10px;");
                        nodeTypeLabel.setAlignment(Pos.CENTER);
                        nodeLongNameLabel.setStyle("-fx-font-size: 24px; -fx-padding: 0 10px 0 10px;");
                        nodeBuildingLabel.setStyle("-fx-font-size: 24px; -fx-padding: 0 10px 10px 10px;");
                        VBox popOverVBox = new VBox(nodeTypeLabel, nodeLongNameLabel, nodeBuildingLabel);
                        popOver = new PopOver(popOverVBox);

                        if (event.getSceneX() < 1920 / 2) {
                            if (event.getSceneY() > 1080 / 2) {
                                popOver.setArrowLocation(ArrowLocation.LEFT_BOTTOM);
                            } else {
                                popOver.setArrowLocation(ArrowLocation.LEFT_TOP);
                            }
                        } else {
                            if (event.getSceneY() > 1080 / 2) {
                                popOver.setArrowLocation(ArrowLocation.RIGHT_BOTTOM);
                            } else {
                                popOver.setArrowLocation(ArrowLocation.RIGHT_TOP);
                            }
                        }

                        popOver.show((javafx.scene.Node) event.getSource(), -6);

                        popOverHidden = false;
                        popOver.setCloseButtonEnabled(false);
                        popOver.setAutoFix(true);
                        popOver.setDetachable(false);
                    }
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
                popOver.hide();
                popOverHidden = true;
                for (String string : nodeDispSet.keySet()) {
                    if (nodeDispSet.get(string) == event.getSource()) {
                        nodeDispSet.get(string).setStroke(Color.BLACK);
                    }

                }
            }
            event.consume();
        }
    };

    public StackPane constructIcon(Node.nodeType type) {
        Rectangle iconShape = new Rectangle(MAP_ICON_SIZE, MAP_ICON_SIZE);
        iconShape.setArcHeight(5);
        iconShape.setArcWidth(5);
        MaterialIconView icon = null;
        MaterialDesignIconView designIcon = null;
        Boolean usingDesignIcon = false;
        Polygon iconArrow = new Polygon();
        VBox iconShapeVBox = new VBox(iconShape, iconArrow);
        iconArrow.getPoints().addAll(new Double[]{
                (MAP_ICON_SIZE * 0.25), 0.0,
                (MAP_ICON_SIZE * 0.75), 0.0,
                (MAP_ICON_SIZE * 0.5), (MAP_ICON_SIZE * 0.25)
        });

        switch (type) {
            case KIOS: {
                iconShape.setStyle("-fx-fill: LIGHTGRAY;");
                iconArrow.setStyle("-fx-fill: LIGHTGRAY;");
                icon = new MaterialIconView(MaterialIcon.HOME);
                break;
            }
            case CONF: {
                iconShape.setStyle("-fx-fill: TAN;");
                iconArrow.setStyle("-fx-fill: TAN;");
                icon = new MaterialIconView(MaterialIcon.INSERT_CHART);
                break;
            }
            case DEPT: {
                iconShape.setStyle("-fx-fill: TOMATO;");
                iconArrow.setStyle("-fx-fill: TOMATO;");
                icon = new MaterialIconView(MaterialIcon.RECENT_ACTORS);
                break;
            }
            case INFO: {
                iconShape.setStyle("-fx-fill: VIOLET;");
                iconArrow.setStyle("-fx-fill: VIOLET;");
                icon = new MaterialIconView(MaterialIcon.INFO);
                break;
            }
            case LABS: {
                iconShape.setStyle("-fx-fill: CYAN;");
                iconArrow.setStyle("-fx-fill: CYAN;");
                icon = new MaterialIconView(MaterialIcon.HEALING);
                break;
            }
            case REST: {
                iconShape.setStyle("-fx-fill: DODGERBLUE;");
                iconArrow.setStyle("-fx-fill: DODGERBLUE;");
                icon = new MaterialIconView(MaterialIcon.WC);
                break;
            }
            case SERV: {
                iconShape.setStyle("-fx-fill: LIGHTPINK;");
                iconArrow.setStyle("-fx-fill: LIGHTPINK;");
                icon = new MaterialIconView(MaterialIcon.ROOM_SERVICE);
                break;
            }
            case STAI: {
                iconShape.setStyle("-fx-fill: DARKSEAGREEN;");
                iconArrow.setStyle("-fx-fill: DARKSEAGREEN;");
                designIcon = new MaterialDesignIconView(MaterialDesignIcon.STAIRS);
                usingDesignIcon = true;
                break;
            }
            case EXIT: {
                iconShape.setStyle("-fx-fill: RED;");
                iconArrow.setStyle("-fx-fill: RED;");
                icon = new MaterialIconView(MaterialIcon.EXIT_TO_APP);
                break;
            }
            case RETL: {
                iconShape.setStyle("-fx-fill: GREEN;");
                iconArrow.setStyle("-fx-fill: GREEN;");
                icon = new MaterialIconView(MaterialIcon.SHOPPING_CART);
                break;
            }
            case ELEV: {
                iconShape.setStyle("-fx-fill: LIGHTSEAGREEN;");
                iconArrow.setStyle("-fx-fill: LIGHTSEAGREEN;");
                designIcon = new MaterialDesignIconView(MaterialDesignIcon.ELEVATOR);
                usingDesignIcon = true;
                break;
            }
            default: {
                return null;
            }
        }
        iconShapeVBox.setAlignment(Pos.CENTER);
        iconShapeVBox.setSpacing(-1);
        if (usingDesignIcon) {
            designIcon.setSize(Integer.toString(MAP_ICON_SIZE - 3));
            designIcon.setTranslateY(-2);
            StackPane sp = new StackPane(iconShapeVBox, designIcon);
            sp.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 2.0, 0.7, 0.0, 0.0)");
            return sp;
        } else {
            icon.setSize(Integer.toString(MAP_ICON_SIZE - 3));
            icon.setTranslateY(-2);
            StackPane sp = new StackPane(iconShapeVBox, icon);
            sp.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 2.0, 0.7, 0.0, 0.0)");
            return sp;
        }
    }

    EventHandler<ScrollEvent> nodeScrollHandler = new EventHandler<ScrollEvent>() {
        @Override
        public void handle(ScrollEvent event) {
            zoomScrollWheel(event);
            event.consume();
        }
    };

    /**
     * this is the event handler for clicking on the map to create a node
     */
    EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
        Boolean isDragging;

        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                getMouseValue(event);
                isDragging = false;
            } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                isDragging = true;
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                double offsetX = event.getSceneX() - orgSceneX;
                double offsetY = event.getSceneY() - orgSceneY;
                newTranslateX = orgTranslateX + offsetX;
                newTranslateY = orgTranslateY + offsetY;

                zoomForTranslate = zoomSlider.getValue();

                double translateSlopeX = X_SCALE * mapImage.getScaleX() * IMG_WIDTH;
                double translateSlopeY = Y_SCALE * mapImage.getScaleY() * IMG_HEIGHT;

                if (newTranslateX > (translateSlopeX - 1920) / 2)
                    newTranslateX = (translateSlopeX - 1920) / 2;
                if (newTranslateX < -(translateSlopeX - 1920) / 2)
                    newTranslateX = -(translateSlopeX - 1920) / 2;
                if (newTranslateY > (translateSlopeY - 1080) / 2)
                    newTranslateY = (translateSlopeY - 1080) / 2;
                if (newTranslateY < -(translateSlopeY - 1080) / 2)
                    newTranslateY = -(translateSlopeY - 1080) / 2;

                mapImage.setTranslateX(newTranslateX);
                mapImage.setTranslateY(newTranslateY);
                nodesPane.setTranslateX(newTranslateX);
                nodesPane.setTranslateY(newTranslateY);
                edgePane.setTranslateX(newTranslateX);
                edgePane.setTranslateY(newTranslateY);
                arrowPane.setTranslateX(newTranslateX);
                arrowPane.setTranslateY(newTranslateY);
            }
        }
    };

    EventHandler<MouseEvent> labelEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            System.out.println("mouseEvent: " + event.getEventType().toString());
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED){
                System.out.println("Label Clicked");

            }
        }
    };

    public void floorSequenceButtonOp(ActionEvent e) {
        String floor = "";
        for (JFXButton button : floorSequenceList) {
            button.setOpacity(0.5);
            if (e.getSource().equals(button)) {
                String regex = "Floor ";
                floor = button.getText();
                floor = floor.replace(regex, "");
                button.setOpacity(1.0);
            }
        }
        switch (floor) {
            case "3":
                floor3ButtonOp(null);
                break;
            case "2":
                floor2ButtonOp(null);
                break;
            case "1":
                floor1ButtonOp(null);
                break;
            case "G":
                floorGButtonOp(null);
                break;
            case "L1":
                floorL1ButtonOp(null);
                break;
            case "L2":
                floorL2ButtonOp(null);
                break;
        }
    }

    public void addOverlay() {
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/map/SearchBarOverlay.fxml"));
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }
        searchBarOverlayController = loader.getController();
        searchBarOverlayController.startUp(mapScreenController);
        searchBarOverlayPane.setTop(root);
    }

    /**
     * this clears the anchor pane, draws the edges and the nodes and loads in the new map
     * this also brings in the basic overlays
     */
    public void updateMap() {
        nodeDispSet.clear();
        nodesPane.getChildren().clear();
        edgePane.getChildren().clear();
        arrowPane.getChildren().clear();
        getMap();
        drawNodes();
    }

    ArrayList<Node> pathMade;

    /**
     * Used to draw the list of nodes returned by AStar
     * it will also create the start and end labels, color the path, change the color of certain nodes
     * (end = red, start = green, stair = blue) and it will create the label for the stairs
     *
     * @param path List of Nodes to be drawn
     */
    public void drawPath(ArrayList<Node> path) {
        double width, height, angle;
        double distanceCounter = 0;

        Node currentNode = null, pastNode;
        if (pathDrawn) {
            resetPath();
        } else {
            Node.floorType floor = path.get(0).getFloor();
            floorState = floor.toString();
            currentFloor = floor;
            updateMap();
        }

        startNode = path.get(0);
        if (startNode.getFloor().equals(currentFloor)) {
            javafx.scene.Node iconNode = iconDispSet.get(startNode.getID());
//            ((GlyphIcon) ((StackPane) iconNode).getChildren().get(1)).setSize(Double.toString(MAP_ICON_SIZE * nodeIconScale));
            iconNode.setScaleX(nodeIconScale);
            iconNode.setScaleY(nodeIconScale);
            iconNode.toFront();
            BounceTransition anim1 = new BounceTransition(iconNode);
            animSet.add(anim1);
            anim1.playFromStart();
        } else {
            javafx.scene.Node node = iconDispSet.get(startNode.getID());
            node.setScaleX(nodeIconScale);
            node.setScaleY(nodeIconScale);
            node.setOpacity(0.6);
            node.toFront();
            BounceTransition anim2 = new BounceTransition(node);
            animSet.add(anim2);
            anim2.playFromStart();
            if (!toggleOn) {
                node.setLayoutX((startNode.getX() - X_OFFSET) * X_SCALE - MAP_ICON_SIZE * 0.5);
                node.setLayoutY((startNode.getY() - Y_OFFSET) * Y_SCALE - MAP_ICON_SIZE * 1.25);
            } else {
                node.setLayoutX((startNode.getxDisplay() - X_OFFSET) * X_SCALE - MAP_ICON_SIZE * 0.5);
                node.setLayoutY((startNode.getyDisplay() - Y_OFFSET) * Y_SCALE - MAP_ICON_SIZE * 1.25);
            }
            nodesPane.getChildren().add(node);
        }

        endNode = path.get(path.size() - 1);
        if (endNode.getFloor().equals(currentFloor)) {
            javafx.scene.Node iconNode2 = iconDispSet.get(endNode.getID());
            iconNode2.setScaleX(nodeIconScale);
            iconNode2.setScaleY(nodeIconScale);
            iconNode2.toFront();
            BounceTransition anim3 = new BounceTransition(iconNode2);
            animSet.add(anim3);
            anim3.playFromStart();
        } else {
            javafx.scene.Node node2 = iconDispSet.get(endNode.getID());
            node2.setScaleX(nodeIconScale);
            node2.setScaleY(nodeIconScale);
            node2.setOpacity(0.6);
            node2.toFront();
            BounceTransition anim4 = new BounceTransition(node2);
            animSet.add(anim4);
            anim4.playFromStart();
            if (!toggleOn) {
                node2.setLayoutX((endNode.getX() - X_OFFSET) * X_SCALE - MAP_ICON_SIZE * 0.5);
                node2.setLayoutY((endNode.getY() - Y_OFFSET) * Y_SCALE - MAP_ICON_SIZE * 1.25);
            } else {
                node2.setLayoutX((endNode.getxDisplay() - X_OFFSET) * X_SCALE - MAP_ICON_SIZE * 0.5);
                node2.setLayoutY((endNode.getyDisplay() - Y_OFFSET) * Y_SCALE - MAP_ICON_SIZE * 1.25);
            }
            nodesPane.getChildren().add(node2);
        }

        stairNodeSet.clear();
        this.pathMade = path;

        double maxXCoord = 0;
        double maxYCoord = 0;
        double minXCoord = 5000;
        double minYCoord = 3400;
        double xDistance = 0;
        double yDistance = 0;

        for (Node n : path) {
            if (toggleOn) {
                if (n.getxDisplay() < minXCoord) minXCoord = n.getxDisplay();
                if (n.getxDisplay() > maxXCoord) maxXCoord = n.getxDisplay();
                if (n.getyDisplay() < minYCoord) minYCoord = n.getyDisplay();
                if (n.getyDisplay() > maxYCoord) maxYCoord = n.getyDisplay();
            } else {
                if (n.getX() < minXCoord) minXCoord = n.getX();
                if (n.getX() > maxXCoord) maxXCoord = n.getX();
                if (n.getY() < minYCoord) minYCoord = n.getY();
                if (n.getY() > maxYCoord) maxYCoord = n.getY();
            }

            pastNode = currentNode;
            currentNode = n;

            //checks if if the current node should go in stair set
            checkStairNodeSet(currentNode);


            //set start node to Green and end node to red
            if (path.get(0).equals(n)) {

            } else if (path.get(path.size() - 1).equals(n)) {

                //if the last node was a stair or an elevator then it should check the else in the checkStairNode function
                if (currentNode.getType().equals(Node.nodeType.ELEV) || currentNode.getType().equals(Node.nodeType.STAI)) {
                    addToStairNodeSet();
                }
            }
            //Color in the path appropriately
            drawEdge(currentNode, pastNode);

            //total up the x and y distance of the path
            if (pastNode != null) {
                if (toggleOn) {
                    xDistance += (currentNode.getxDisplay() - pastNode.getxDisplay());
                    yDistance += (currentNode.getyDisplay() - pastNode.getyDisplay());
                } else {
                    xDistance += (currentNode.getX() - pastNode.getX());
                    yDistance += (currentNode.getY() - pastNode.getY());
                }
            }
        }
        if (searchBarOverlayController.getDirectionsVisible()) {
            searchBarOverlayController.expandDirections(currentFloor);
        }

        getFloors();
        createFloorSequence();


        System.out.println("list of stair nodes: " + stairNodeSet.toString());
        minXCoord -= 400;
        minYCoord -= 400;
        maxXCoord += 400;
        maxYCoord += 400;
        double rangeX = maxXCoord - minXCoord;
        double rangeY = maxYCoord - minYCoord;

        double desiredZoomX = 1920 / (rangeX * X_SCALE);
        double desiredZoomY = 1080 / (rangeY * Y_SCALE);
        System.out.println("desired X zoom: " + desiredZoomX + " desired Zoom Y: " + desiredZoomY);

        double centerX = (maxXCoord + minXCoord) / 2;
        double centerY = (maxYCoord + minYCoord) / 2;

        autoTranslateZoom(desiredZoomX, desiredZoomY, centerX, centerY);

        System.out.println(toggleOn.toString());

        pathDrawn = true;
        //Draws the arrows
        if (toggleOn) {
            width = path.get(1).getxDisplay() - path.get(0).getxDisplay();
            height = path.get(1).getyDisplay() - path.get(0).getyDisplay();
        } else {
            width = path.get(1).getX() - path.get(0).getX();
            height = path.get(1).getY() - path.get(0).getY();
        }
        angle = Math.atan2(height, width);
        //increment the distanceCounter
        double totalDistance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
        int arrowNumber = (int) (totalDistance * 1 / 75.0);
        for (int i = 0; i < arrowNumber; i++) {
            arrowFloorSet.add(currentNode.getFloor().toString());
            drawTriangle();
        }

        int counter = 0;
        for (Polygon shape : arrowDispSet) {
            counter++;
            ArrowPathTransition arrowPathTransition = new ArrowPathTransition(pathMade, shape, arrowDispSet.size(),
                    currentFloor, toggleOn);
            arrowPathTransition.setPathAnim();
            arrowPathTransition.jumpToDuration(counter);
            arrowPathTransition.playAnim();
        }
    }

    public void drawEdge(Node currentNode, Node pastNode) {
        for (Edge e : currentNode.getEdges()) {
            if (pastNode != null) {
                if (e.contains(pastNode)) {
                    Line line = new Line();
                    edgePane.getChildren().add(line);
                    edgeDispSet.put(e.getID(), line);
                    line.setStroke(Color.rgb(11, 70, 120));
                    line.setStrokeWidth(6.0);
                    if (!toggleOn) {
                        line.setStartX((e.getStart().getX() - X_OFFSET) * X_SCALE);
                        line.setStartY((e.getStart().getY() - Y_OFFSET) * Y_SCALE);
                        line.setEndX((e.getEnd().getX() - X_OFFSET) * X_SCALE);
                        line.setEndY((e.getEnd().getY() - Y_OFFSET) * Y_SCALE);
                        if (e.getStart().getFloor() == currentFloor && e.getEnd().getFloor() == currentFloor) {
                            line.setOpacity(1.0);
                        } else {
                            line.getStrokeDashArray().addAll(1.0, 10.0);
                            line.setOpacity(0.75);
                        }
                    } else {
                        line.setStartX((e.getStart().getxDisplay() - X_OFFSET) * X_SCALE);
                        line.setStartY((e.getStart().getyDisplay() - Y_OFFSET) * Y_SCALE);
                        line.setEndX((e.getEnd().getxDisplay() - X_OFFSET) * X_SCALE);
                        line.setEndY((e.getEnd().getyDisplay() - Y_OFFSET) * Y_SCALE);
                        if (e.getStart().getFloor() == currentFloor && e.getEnd().getFloor() == currentFloor) {
                            line.setOpacity(1.0);
                        } else {
                            line.getStrokeDashArray().addAll(1.0, 10.0);
                            line.setStrokeDashOffset(0);
                            line.setOpacity(0.75);
                        }
                    }
                }
            }
        }
    }

    /**
     * drawTriangle is the function that draws directional arrows for the function
     *
     *              <p>
     *              This outputs an arrow on the screen along the line.
     */
    public void drawTriangle() {

        Polygon arrow = new Polygon();

        arrow.getPoints().addAll(new Double[]{
                2.5, 3.0,
                0.0, 0.0,
                9.0, 3.0,
                0.0, 6.0});
        arrow.setFill(Color.rgb(250, 150, 0));

        arrowPane.getChildren().add(arrow);
        arrowDispSet.add(arrow);
    }


    public void checkStairNodeSet(Node currentNode) {

        if (currentNode.getType().equals(Node.nodeType.STAI)) {
            if (recentElevNodeSet.size() > 1)
                addToStairNodeSet();
            recentElevNodeSet.clear();
            recentStairNodeSet.add(currentNode);
        } else if (currentNode.getType().equals(Node.nodeType.ELEV)) {
            if (recentStairNodeSet.size() > 1)
                addToStairNodeSet();
            recentStairNodeSet.clear();
            recentElevNodeSet.add(currentNode);
        } else {
            addToStairNodeSet();
        }
    }

    public void addToStairNodeSet() {
        if (recentStairNodeSet.size() > 1) {
            stairNodeSet.add(recentStairNodeSet.get(0));
            stairNodeSet.add(recentStairNodeSet.get(recentStairNodeSet.size() - 1));
        } else if (recentElevNodeSet.size() > 1) {
            stairNodeSet.add(recentElevNodeSet.get(0));
            stairNodeSet.add(recentElevNodeSet.get(recentElevNodeSet.size() - 1));
        }
        recentElevNodeSet.clear();
        recentStairNodeSet.clear();
    }


    /**
     * Used to draw the list of nodes returned by AStar
     */
    public void resetPath() {
        iconDispSet.get(startNode.getID()).setScaleX(1);
        iconDispSet.get(startNode.getID()).setScaleY(1);
        iconDispSet.get(endNode.getID()).setScaleX(1);
        iconDispSet.get(endNode.getID()).setScaleY(1);

        stairNodeSet.clear();
        edgeDispSet.clear();
        edgePane.getChildren().clear();
        arrowDispSet.clear();
        arrowFloorSet.clear();
        arrowPane.getChildren().clear();
        pathDrawn = false;

        for (BounceTransition anim : animSet) {
            anim.stop();
        }
        animSet.clear();
        nodesPane.getChildren().clear();
        drawNodes();
    }

    public void setToggleOn(Boolean toggleOn) {
        this.toggleOn = toggleOn;

        updateMap();
        if (pathDrawn) {
            drawPath(pathMade);
        }
    }

    public void clearStartNode() {
        if (pathDrawn) resetPath();
    }

    public void clearEndNode() {
        if (pathDrawn) resetPath();
    }

    public void removeFocus() {
        searchBarOverlayController.setSearchButtonFocus();
    }


    public void autoTranslateZoom(double zoomX, double zoomY, double centerX, double centerY) {

        double zoom;
        if (zoomX > zoomY)
            zoom = zoomY;
        else
            zoom = zoomX;

        if (zoom > zoomSlider.getMax()) zoom = zoomSlider.getMax();
        if (zoom < zoomSlider.getMin()) zoom = zoomSlider.getMin();


        zoomSlider.setValue(zoom);

        double screenX = (centerX - X_OFFSET) * X_SCALE;
        double screenY = (centerY - Y_OFFSET) * Y_SCALE;

        double translateX = 960 - screenX;
        double translateY = 540 - screenY;
        double screenTranslateX = (translateX * zoom);
        double screenTranslateY = (translateY * zoom);

        double translateSlopeX = X_SCALE * mapImage.getScaleX() * IMG_WIDTH;
        double translateSlopeY = Y_SCALE * mapImage.getScaleY() * IMG_HEIGHT;
        if (screenTranslateX > (translateSlopeX - 1920) / 2)
            screenTranslateX = (translateSlopeX - 1920) / 2;
        if (screenTranslateX < -(translateSlopeX - 1920) / 2)
            screenTranslateX = -(translateSlopeX - 1920) / 2;
        if (screenTranslateY > (translateSlopeY - 1080) / 2)
            screenTranslateY = (translateSlopeY - 1080) / 2;
        if (screenTranslateY < -(translateSlopeY - 1080) / 2)
            screenTranslateY = -(translateSlopeY - 1080) / 2;

        mapImage.setTranslateX(screenTranslateX);
        mapImage.setTranslateY(screenTranslateY);
        nodesPane.setTranslateX(screenTranslateX);
        nodesPane.setTranslateY(screenTranslateY);
        edgePane.setTranslateX(screenTranslateX);
        edgePane.setTranslateY(screenTranslateY);
        arrowPane.setTranslateX(screenTranslateX);
        arrowPane.setTranslateY(screenTranslateY);
    }

    public Boolean getPathDrawn() {
        return this.pathDrawn;
    }

    /**
     * this method will determine what floors the path goes on
     */
    public void getFloors() {
        floorsList.clear();
        for (int i = 0; i < stairNodeSet.size(); i+=2){
                floorsList.add(stairNodeSet.get(i).getFloor());
        }
        if (stairNodeSet.size() > 1)
            floorsList.add(stairNodeSet.get(stairNodeSet.size()-1).getFloor());
    }

    /**
     * creates the labels and puts them in the hbox
     */
    public void createFloorSequence() {
        clearFloorSequenceHBox();
        floorSequenceList.clear();
        Polygon arrowHead = new Polygon();
        Polygon arrowEnd = new Polygon();
        Polygon arrow = new Polygon();
        arrowHead.getPoints().addAll(new Double[]{
                0.0, 0.0,
                200.0, 0.0,
                300.0, 50.0,
                200.0, 100.0,
                0.0, 100.0
        });

        arrowEnd.getPoints().addAll(new Double[]{
                0.0, 0.0,
                300.0, 0.0,
                300.0, 100.0,
                0.0, 100.0,
                100.0, 50.0
        });

        arrow.getPoints().addAll(new Double[]{
                0.0, 0.0,
                200.0, 0.0,
                300.0, 50.0,
                200.0, 100.0,
                0.0, 100.0,
                100.0, 50.0
        });


        if (!floorsList.equals(null)) {
            for (int i = 0; i < floorsList.size(); i++) {

                JFXButton button = new JFXButton();
                floorSequenceHBox.getChildren().add(button);
                floorSequenceList.add(button);
                button.setPadding(new Insets(30));
                if (i == 0) {
                    button.setShape(arrowHead);
                    button.setAlignment(Pos.CENTER_LEFT);
                } else if (i == floorsList.size() - 1) {
                    button.setShape(arrowEnd);
                    button.setAlignment(Pos.CENTER_RIGHT);
                    button.setPadding(new Insets(20));
                } else {
                    button.setShape(arrow);
                    button.setAlignment(Pos.CENTER_RIGHT);
                }

                button.setTextFill(Color.color(0.96,0.737,0.227));
                button.setStyle("-fx-background-color: #0b2f5b;");
                if (!currentFloor.equals(floorsList.get(i))) {
                    button.setTextFill(Color.BLACK);
                    button.setStyle("-fx-background-color: #DADADA;");
                }
                button.setMinHeight(75);
                button.setMinWidth(150);

                button.setButtonType(JFXButton.ButtonType.RAISED);
                button.setFont(new Font(18));
                button.setOnAction(e -> floorSequenceButtonOp(e));
                button.setText("Floor " + floorsList.get(i).toString());

            }
        }

    }

    public void clearFloorSequenceHBox() {
        floorSequenceHBox.getChildren().clear();
    }


    public void setFloorStyleClass(Node.floorType floor) {

        switch (floor) {
            case LEVEL_1:
                floor1Button.getStyleClass().removeAll("floor-button");
                floor1Button.getStyleClass().add("highlight-floor-button");

                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floor2Button.getStyleClass().add("floor-button");
                floor3Button.getStyleClass().removeAll("highlight-floor-button");
                floor3Button.getStyleClass().add("floor-button");
                floorGButton.getStyleClass().removeAll("highlight-floor-button");
                floorGButton.getStyleClass().add("floor-button");
                floorL1Button.getStyleClass().removeAll("highlight-floor-button");
                floorL1Button.getStyleClass().add("floor-button");
                floorL2Button.getStyleClass().removeAll("highlight-floor-button");
                floorL2Button.getStyleClass().add("floor-button");
                break;

            case LEVEL_2:
                floor2Button.getStyleClass().removeAll("floor-button");
                floor2Button.getStyleClass().add("highlight-floor-button");


                floor1Button.getStyleClass().removeAll("highlight-floor-button");
                floor1Button.getStyleClass().add("floor-button");
                floor3Button.getStyleClass().removeAll("highlight-floor-button");
                floor3Button.getStyleClass().add("floor-button");
                floorGButton.getStyleClass().removeAll("highlight-floor-button");
                floorGButton.getStyleClass().add("floor-button");
                floorL1Button.getStyleClass().removeAll("highlight-floor-button");
                floorL1Button.getStyleClass().add("floor-button");
                floorL2Button.getStyleClass().removeAll("highlight-floor-button");
                floorL2Button.getStyleClass().add("floor-button");
                break;

            case LEVEL_3:
                floor3Button.getStyleClass().removeAll("floor-button");
                floor3Button.getStyleClass().add("highlight-floor-button");

                floor1Button.getStyleClass().removeAll("highlight-floor-button");
                floor1Button.getStyleClass().add("floor-button");
                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floor2Button.getStyleClass().add("floor-button");
                floorGButton.getStyleClass().removeAll("highlight-floor-button");
                floorGButton.getStyleClass().add("floor-button");
                floorL1Button.getStyleClass().removeAll("highlight-floor-button");
                floorL1Button.getStyleClass().add("floor-button");
                floorL2Button.getStyleClass().removeAll("highlight-floor-button");
                floorL2Button.getStyleClass().add("floor-button");
                break;

            case LEVEL_G:
                floorGButton.getStyleClass().removeAll("floor-button");
                floorGButton.getStyleClass().add("highlight-floor-button");

                floor1Button.getStyleClass().removeAll("highlight-floor-button");
                floor1Button.getStyleClass().add("floor-button");
                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floor2Button.getStyleClass().add("floor-button");
                floor3Button.getStyleClass().removeAll("highlight-floor-button");
                floor3Button.getStyleClass().add("floor-button");
                floorL1Button.getStyleClass().removeAll("highlight-floor-button");
                floorL1Button.getStyleClass().add("floor-button");
                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floorL2Button.getStyleClass().add("floor-button");
                break;

            case LEVEL_L1:
                floorL1Button.getStyleClass().removeAll("floor_button");
                floorL1Button.getStyleClass().add("highlight-floor-button");

                floor1Button.getStyleClass().removeAll("highlight-floor-button");
                floor1Button.getStyleClass().add("floor-button");
                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floor2Button.getStyleClass().add("floor-button");
                floor3Button.getStyleClass().removeAll("highlight-floor-button");
                floor3Button.getStyleClass().add("floor-button");
                floorGButton.getStyleClass().removeAll("highlight-floor-button");
                floorGButton.getStyleClass().add("floor-button");
                floorL2Button.getStyleClass().removeAll("highlight-floor-button");
                floorL2Button.getStyleClass().add("floor-button");
                break;

            case LEVEL_L2:
                floorL2Button.getStyleClass().removeAll("floor_button");
                floorL2Button.getStyleClass().add("highlight-floor-button");

                floor1Button.getStyleClass().removeAll("highlight-floor-button");
                floor1Button.getStyleClass().add("floor-button");
                floor2Button.getStyleClass().removeAll("highlight-floor-button");
                floor2Button.getStyleClass().add("floor-button");
                floor3Button.getStyleClass().removeAll("highlight-floor-button");
                floor3Button.getStyleClass().add("floor-button");
                floorGButton.getStyleClass().removeAll("highlight-floor-button");
                floorGButton.getStyleClass().add("floor-button");
                floorL1Button.getStyleClass().removeAll("highlight-floor-button");
                floorL1Button.getStyleClass().add("floor-button");
                break;

        }
    }

    public JFXButton getBackButton() {
        return this.backButton;
    }

    public Node.floorType getCurrentFloor() {
        return currentFloor;
    }
    public ArrayList<Node.floorType> getFloorsList(){
        return floorsList;
    }
}
