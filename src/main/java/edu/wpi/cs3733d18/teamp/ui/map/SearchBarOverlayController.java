package edu.wpi.cs3733d18.teamp.ui.map;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Directions;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;
import edu.wpi.cs3733d18.teamp.ui.home.ShakeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SearchBarOverlayController implements Initializable{

    /*public static final int X_OFFSET = -523;
    public static final int Y_OFFSET = 0;
    public static final double X_SCALE = 1588.235294/5000.0;
    public static final double Y_SCALE = 1080.0/3400.0;
    public static final double NODE_RADIUS = 3.0;
    public static final double EDGE_WIDTH = 1.0;
    public static final int WIDTH = 1380;
    public static final int HEIGHT = 776;
    private static final double STRAIGHT_BOUNDS = 15;
    private static final double SLIGHT_BOUNDS = 60;
    private static final double NORMAL_BOUNDS = 120;
    private static final double HARD_BOUNDS = 165;
    private static final double TURN_AROUND_BOUNDS = 180;

    Label startLabel = new Label();
    Label endLabel = new Label();*/

    // Pathfinding variables
    private ArrayList<String> pathDirections; // The actual directions
    private Boolean directionsVisible = false; // Whether the box for directions are visible or not

    private Directions directionsList = new Directions();
    DBSystem db = DBSystem.getInstance();
    private Node endNode;

    @FXML
    Rectangle directionsRectangle;

    @FXML
    TableView<DirectionsTable> directionsTableView;

    @FXML
    TableColumn<DirectionsTable,String> directionsTableViewColumn;

    ObservableList<DirectionsTable> directions = FXCollections.observableArrayList();

    @FXML
    Button directionsButton;

    @FXML
    Button emailButton;

    @FXML
    Button phoneButton;

    @FXML
    Button goButton;

    @FXML
    JFXTextField sourceSearchBar;

    @FXML
    JFXTextField destinationSearchBar;

    @FXML
    ToggleButton mapToggleButton;

    private MapScreenController mapScreenController;
    private ThreeDMapScreenController threeDMapScreenController;
    private Boolean is3D = false;

    public void startUp(MapScreenController mapScreenController){
        this.mapScreenController = mapScreenController;
        if (!mapScreenController.getPathDrawn()) {
            directionsTableView.setVisible(false);
            directionsButton.setVisible(false);
            emailButton.setVisible(false);
            phoneButton.setVisible(false);
            directionsRectangle.setVisible(false);
        }

        mapToggleButtonOp();
    }

    public void startUp3D(ThreeDMapScreenController threeDMapScreenController){
        this.threeDMapScreenController = threeDMapScreenController;
        //if (!mapScreenController.getPathDrawn()) { // TODO uncomment when map is integrated with the rest of the app
            directionsTableView.setVisible(false);
            directionsButton.setVisible(false);
            emailButton.setVisible(false);
            phoneButton.setVisible(false);
            directionsRectangle.setVisible(false);
            is3D = true;
        //}

        //mapToggleButtonOp();
    }

    public Boolean isSourceFocused(){
        return sourceSearchBar.isFocused();
    }

    public Boolean isDestinationFocused(){
        return destinationSearchBar.isFocused();
    }

    public void setSourceSearchBar(String startNode){
        sourceSearchBar.setText(startNode);
    }

    public void setDestinationSearchBar(String endNode){
        destinationSearchBar.setText(endNode);
    }

    ArrayList<String> destinationWords = new ArrayList<>();

    ArrayList<String> sourceWords = new ArrayList<>();

    Boolean toggledOn;
    @FXML
    public void mapToggleButtonOp(){
        if(mapToggleButton.isSelected())
            toggledOn = true;
        else
            toggledOn = false;
        mapScreenController.setToggleOn(toggledOn);
    }

    /**
     * sets up the search bars so they can be autofilled
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){
        setWordArrays();

        AutoCompletionBinding<String> destBinding = TextFields.bindAutoCompletion(destinationSearchBar, destinationWords);
        AutoCompletionBinding<String> sourceBinding = TextFields.bindAutoCompletion(sourceSearchBar, sourceWords);

        destBinding.setPrefWidth(destinationSearchBar.getPrefWidth());
        sourceBinding.setPrefWidth(sourceSearchBar.getPrefWidth());
    }

    /**
     * sets up the word array both search bars will be using
     */
    private void setWordArrays() {
        HashMap<String, Node> nodeSet;

        nodeSet = db.getAllNodes();

        for (Node node : nodeSet.values()) {
            if (node.getType() != Node.nodeType.HALL) {
                sourceWords.add(node.getLongName());
            }
        }
        destinationWords.addAll(sourceWords);
        destinationWords.add("NEAREST HALLWAY");
        destinationWords.add("NEAREST ELEVATOR");
        destinationWords.add("NEAREST RESTROOM");
        destinationWords.add("NEAREST STAIRCASE");
        destinationWords.add("NEAREST DEPARTMENT");
        destinationWords.add("NEAREST LAB");
        destinationWords.add("NEAREST INFO DESK");
        destinationWords.add("NEAREST CONFERENCE ROOM");
        destinationWords.add("NEAREST EXIT");
        destinationWords.add("NEAREST SHOP");
        destinationWords.add("NEAREST SERVICE STATION");
    }


    Boolean pathDrawn = false;
    /**
     * Handles the pathfinding operation given destination and source.
     * @param e ActionEvent instance passed by JavaFX used to get source information
     * @return true if successful otherwise false
     */
    public Boolean searchButtonOp(ActionEvent e) {
        if (pathDrawn && !is3D) {
            mapScreenController.resetPath();
        }
        else if (pathDrawn) {
            threeDMapScreenController.resetPath();
        }
        refresh();

        System.out.println("get path");
        //get all nodes
        HashMap<String, Node> nodeSet = db.getAllNodes();
        System.out.println(" size: " + nodeSet.size());

        //Declare source node, destination node, and get the typed in inputs for both search boxes
        Node srcNode, dstNode;
        String src = sourceSearchBar.getText();
        String dst = destinationSearchBar.getText();

        // Check if the source node was input
        if (src.length() > 0 && !src.equals("Current Kiosk")) {
            // Source has been chosen by user, get Node entity from nodeID through NodeRepo
            srcNode = nodeSet.get(parseSourceInput(src).getID());
        } else {
            // Source is main.kiosk by default
            srcNode = nodeSet.get("PKIOS00102");
        }

        // Check if the destination node was input
        if (dst.length() > 0) {
            // Destination has been chosen by user, get Node entity from nodeID through NodeRepo
            destinationSearchBar.setUnFocusColor(Color.rgb(245,188,58));
            dstNode = nodeSet.get(parseDestinationInput(srcNode, dst).getID());
        } else {
            // Destination has not been set, set search bar to red and shake
            destinationSearchBar.setUnFocusColor(Color.rgb(255,0,0));
            ShakeTransition anim = new ShakeTransition(destinationSearchBar);
            anim.playFromStart();
            return false;
            //dstNode = nodeSet.get(endNode.getID());
        }

        Font font = new Font("verdana", 24.0);

        ArrayList<Node> path = Main.pathfindingContext.findPath(srcNode, dstNode);
        System.out.println(path);
        if (!is3D) {
            mapScreenController.drawPath(path);
        }
        else {
            threeDMapScreenController.drawPath(path);
        }
        System.out.println(path);
        pathDrawn = true;

        directionsList.generateTextDirections(path);
        ArrayList<String> directions = directionsList.getDirections();
        pathDirections = directions;
        for (String s : directions) {
            System.out.println(s);
        }

        pathDrawn = true;
        directionsButton.setVisible(true);
        return true;
    }

    /**
     * Parses the input from the destination text box and returns the node that we wanted
     * @param string
     * @return the node corresponding to the input string
     */
    public Node parseSourceInput(String string) {
        Node aNode = new Node();

        HashMap<String, Node> nodeSet = db.getAllNodes();

        for (Node node : nodeSet.values()) {
            if (node.getLongName().compareTo(string) == 0) {
                aNode = node;
            }
        }

        return aNode;
    }

    /**
     * Parses the input from the destination text box and returns the node that we wanted
     * @param srcNode
     * @param string
     * @return the node corresponding to the input string
     */
    public Node parseDestinationInput(Node srcNode, String string) {
        Node aNode = srcNode;
//        System.out.println("Input string: " + string);
//        System.out.println("source node:" + srcNode);

        switch (string) {
            case "NEAREST HALLWAY":
                aNode = getNearestOfType(srcNode, Node.nodeType.HALL);
                break;

            case "NEAREST ELEVATOR":
                aNode = getNearestOfType(srcNode, Node.nodeType.ELEV);
                break;

            case "NEAREST RESTROOM":
                aNode = getNearestOfType(srcNode, Node.nodeType.REST);
                break;

            case "NEAREST STAIRCASE":
                aNode = getNearestOfType(srcNode, Node.nodeType.STAI);
                break;

            case "NEAREST DEPARTMENT":
                aNode = getNearestOfType(srcNode, Node.nodeType.DEPT);
                break;

            case "NEAREST LAB":
                aNode = getNearestOfType(srcNode, Node.nodeType.LABS);
                break;

            case "NEAREST INFO DESK":
                aNode = getNearestOfType(srcNode, Node.nodeType.INFO);
                break;

            case "NEAREST CONFERENCE ROOM":
                aNode = getNearestOfType(srcNode, Node.nodeType.CONF);
                break;

            case "NEAREST EXIT":
                aNode = getNearestOfType(srcNode, Node.nodeType.EXIT);
                break;

            case "NEAREST SHOP":
                aNode = getNearestOfType(srcNode, Node.nodeType.RETL);
                break;

            case "NEAREST SERVICE STATION":
                aNode = getNearestOfType(srcNode, Node.nodeType.SERV);
                break;

            default:
                HashMap<String, Node> nodeSet = db.getAllNodes();

                for (Node node : nodeSet.values()) {
                    if (node.getLongName().compareTo(string) == 0) {
                        aNode = node;
                    }
                }
                break;
        }
        return aNode;
    }

    /**
     * Looks through entries in node database that math the type of input enum,
     * compares the distances to node,
     * returns the shortest one
     * @param srcNode
     * @param type
     * @return closestNode the closest node of a given type
     */
    private Node getNearestOfType(Node srcNode, Node.nodeType type) {
        HashMap<String, Node> nodeSet = db.getNodesOfType(type);

//        System.out.println(srcNode);
        Node shortestDistanceNode = srcNode;
        double distance = Double.POSITIVE_INFINITY;

        for (Node node : nodeSet.values()) {
//            System.out.println(node.getID());
//            System.out.println(srcNode.getID());
            if (srcNode.distanceBetweenNodes(node) < distance && srcNode.getFloor() == node.getFloor()) {
                shortestDistanceNode = node;
                distance = srcNode.distanceBetweenNodes(node);
//                System.out.println("distance: " + srcNode.distanceBetweenNodes(node));
            }
        }

        return shortestDistanceNode;
    }

    /**
     * Creates a popup for inputting email information to send the map.
     * @param e ActionEvent instance passed by JavaFX used to get source information
     * @return true if successful otherwise false
     */
    public Boolean emailButtonOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;
        EmailPopUpController emailPopUp;

        stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/FXML/map/EmailPopUp.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        emailPopUp = loader.getController();
        emailPopUp.startUp(pathDirections);
        stage.setScene(new Scene(root, 360, 250));
        stage.setX(1500);
        stage.setY(130);
        stage.setTitle("Set Email");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(emailButton.getScene().getWindow());
        stage.show();
        return true;
    }

    /**
     * Create a popup for inputting phone information to text user the path.
     * @param e ActionEvent instance passed by JavaFX used to get source information
     * @return true if successful otherwise false
     */
    public Boolean phoneButtonOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;
        PhonePopUpController phonePopUp;

        stage = new Stage();
        loader = new FXMLLoader(getClass().getResource("/FXML/map/PhonePopUp.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        phonePopUp = loader.getController();
        phonePopUp.startUp(pathDirections);
        stage.setScene(new Scene(root, 360, 250));
        stage.setX(1500);
        stage.setY(130);
        stage.setTitle("Set Phone");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(phoneButton.getScene().getWindow());
        stage.show();
        return true;
    }

    /**
     * Displays the text directions on the map screen
     *
     */
    public Boolean directionsButtonOp(ActionEvent e) {
        if (directionsVisible){
            refresh();

            directionsTableViewColumn.setCellValueFactory(new PropertyValueFactory<>("directions"));
            directionsTableView.setVisible(false);
            emailButton.setVisible(false);
            phoneButton.setVisible(false);
            directionsRectangle.setVisible(false);
            directionsButton.setText("Directions >");
            directionsVisible = false;
        }
        else {
            emailButton.setVisible(true);
            phoneButton.setVisible(true);
            directionsRectangle.setVisible(true);
            directionsTableView.setVisible(true);
            if (directionsList.getDirections() == null) {
                directions.add(new DirectionsTable("Path needs to be set first!"));
                directionsTableView.setItems(directions);
                directionsTableViewColumn.setCellValueFactory(new PropertyValueFactory<>("directions"));
                directionsButton.setText("Directions X");
                directionsVisible = true;
            }
            else {
                for (String line : directionsList.getDirections()) {
                    directions.add(new DirectionsTable(line));
                }
                directionsTableView.setItems(directions);
                directionsTableViewColumn.setCellValueFactory(new PropertyValueFactory<>("directions"));

                directionsButton.setText("Directions X");
                directionsVisible = true;
            }
        }
        return true;
    }

    /**
     * Generate text directions based on an ArrayList<Node> path
     * @param path
     * @return ArrayList of Strings containing directions
     */
    public ArrayList<String> generateTextDirections(ArrayList<Node> path) {
        ArrayList<String> directions = new ArrayList<>();
        double distance = 0.0;
        double pastDistance = 0.0;
        double angle = Math.toRadians(90.0);
        double pastAngle = Math.toRadians(90.0);
        double angleDiff = 0.0;
        String words = "";
        String pastWords = "Go forward ";
        String ft = "ft";
        Node pastNode = new Node();
        Node lastChangeNode = new Node();
        boolean changeDirections = false;
        boolean firstTime = true;
        double feetPerPixel = 85. / 260.; // Measured on the west wing of the hospital with Google maps and paint

        directions.add("Starting Route!");

        for (Node node : path) {
            if (pastNode.getID() != null) {
                angle = node.angleBetweenNodes(pastNode);
                angleDiff = angle - pastAngle;
                angleDiff = Math.toDegrees(angleDiff);

                if (angleDiff < -180) {
                    angleDiff += 360;
                }
                if (angleDiff > 180) {
                    angleDiff -= 360;
                }

//                System.out.println("Past node: " + pastNode + " current node: " + node + " Distance since last change: " + distance);
//                System.out.println("Angle: " + Math.toDegrees(angle) + " pastAngle: " + Math.toDegrees(pastAngle) + " angleDif: " + angleDiff);


                // todo figure out how to not add "Go forward 0ft"
//                if (changeDirections) {
//                    if (firstTime) {
////                        directions.add(words);
//                        firstTime = false;
//                    } else if (roundToNearest(feetPerPixel*lastChangeNode.distanceBetweenNodes(pastNode), 10) != 0) {
//                        directions.add("Go forward " + roundToNearest(feetPerPixel*lastChangeNode.distanceBetweenNodes(pastNode), 10) + ft);
//                        directions.add(words);
//                    }else{
//                        directions.add(words);
////                    }
//
//                    pastDistance = distance;
//                    distance = 0;
//                    changeDirections = false;
//                    lastChangeNode = pastNode;
//                } else {
//
//                }
//                distance += pastNode.distanceBetweenNodes(node);
//
////                System.out.println();
//            }
//            pastWords = words;
//            pastAngle = angle;
//            pastNode = node;
////            directions.add("------------");
//        }
//        //directions.add("Go forward " + roundToNearest(feetPerPixel*lastChangeNode.distanceBetweenNodes(pastNode), 10) + ft);
//        directions.add("You have arrived at your destination!");
//        return directions;
            }

        }
        return null;
    }

    public void refresh() {
        for (int i = 0; i < directionsTableView.getItems().size(); i++) {
            directionsTableView.getItems().clear();
        }
    }

    public int roundToNearest(double value, int roundTo) {
        value += roundTo/2;
        int retVal = (int) value / roundTo;
        return retVal * roundTo;
    }


    public Boolean isClear(){
        if (destinationSearchBar.getText().equals("") || sourceSearchBar.getText().equals("")){
            return true;
        }
        return false;
    }

    public void setSearchButtonFocus(){
        goButton.requestFocus();
    }

    public void setDirectionsVisible(Boolean directionsVisible) {
        this.directionsVisible = directionsVisible;
    }
}


