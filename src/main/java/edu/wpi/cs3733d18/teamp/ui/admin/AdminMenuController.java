package edu.wpi.cs3733d18.teamp.ui.admin;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733d18.teamp.Main;
import edu.wpi.cs3733d18.teamp.ui.service.ServiceRequestScreen;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class AdminMenuController {

    private Parent root;
    private FXMLLoader loader;
    private MapBuilderController mapBuilderController;

    @FXML
    JFXButton employeeButton;

    @FXML
    JFXButton mapManagementButton;

    @FXML
    JFXButton serviceRequestScreenButton;

    @FXML
    JFXButton settingsButton;

    @FXML
    JFXButton backButton;

    @FXML
    Label helloMessage;

    /**
     * initializes welcome label
     */
    @FXML
    public void onStartup() {
        //displays name of user on startup
        helloMessage.setText("Hello " + Main.currentUser.getFirstName() + " " + Main.currentUser.getLastName() + ", ");
    }

    /**
     * loads the manage employees screen
     * @param e
     */
    @FXML
    public void employeeButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        ManageEmployeeScreenController manageEmployeeScreenController;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/ManageEmployeeScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        manageEmployeeScreenController = loader.getController();
        manageEmployeeScreenController.onStartUp();
        employeeButton.getScene().setRoot(root);
    }

    /**
     * loads the map management screen
     * @param e
     */
    @FXML
    public void mapManagementButtonOp(ActionEvent e) {
        mapManagementButton.getScene().setCursor(Cursor.WAIT); //Change cursor to wait style

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        loader = new FXMLLoader(getClass().getResource("/FXML/admin/MapBuilder.fxml"));
                        try {
                            root = loader.load();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                            return;
                        }
                        mapBuilderController = loader.getController();
                        mapBuilderController.startUp();
                        mapManagementButton.getScene().setRoot(root);

                        mapBuilderController.formOverlayPane.getScene().setCursor(Cursor.DEFAULT); //Change cursor to default style
                    }
                });
                return null;
            }
        };

        Thread th = new Thread(task);
        th.start();
    }

    /**
     * loads the service request screen
     * @param e
     */
    @FXML
    public void serviceRequestScreenButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        ServiceRequestScreen serviceRequestScreen;

        loader = new FXMLLoader(getClass().getResource("/FXML/service/ServiceRequestScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        serviceRequestScreen = loader.getController();
        serviceRequestScreen.onStartup();
        serviceRequestScreenButton.getScene().setRoot(root);
    }

    /**
     * brings you back to the home page
     * @param e
     */
    @FXML
    public void backButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        Main.logoutCurrentUser();
        backButton.getScene().setRoot(root);
    }

    /**
     * brings you to the settings page
     * @param e
     */
    @FXML
    public void settingsButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        SettingsController settingsController;

        loader = new FXMLLoader(getClass().getResource("/FXML/admin/SettingsScreen.fxml"));
        try {
            root = loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        settingsController = loader.getController();
        settingsController.onStartUp();
        settingsButton.getScene().setRoot(root);
    }
}
