package edu.wpi.cs3733d18.teamp.ui.service;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733d18.teamR.RaikouAPI;
import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.RequestNotFoundException;
import edu.wpi.cs3733d18.teamp.api.Exceptions.ServiceException;
import edu.wpi.cs3733d18.teamp.api.TransportationRequest;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ServiceRequestScreen implements Initializable{
    PopUpController popUpController;
    DBSystem db = DBSystem.getInstance();
    ArrayList<Request> requests;
    ArrayList<Request> emergencyRequests = new ArrayList<>();
    ArrayList<Request> otherRequests = new ArrayList<>();

    @FXML
    JFXButton backButton;

    @FXML
    JFXButton claimRequestButton;

    @FXML
    JFXButton recordsButton;

    @FXML
    JFXButton completeRequestButton;

    @FXML
    Label serviceRequestErrorLabel;

    @FXML
    Label timeCreatedLabel;

    @FXML
    Label timeCompletedLabel;

    @FXML
    Label requestTypeLabel;

    @FXML
    Label locationLabel;

    @FXML
    Label createdByLabel;

    @FXML
    Label assignedToLabel;

    @FXML
    Label additionalInfoLabel;

    @FXML
    Label helloMessage;

    @FXML
    TableView<ServiceRequestTable> newRequestTable;

    @FXML
    TableView<ServiceRequestTable> inProgRequestTable;

    @FXML
    TableView<ServiceRequestTable> completedRequestTable;

    @FXML
    TableColumn<ServiceRequestTable, Integer> rID1;

    @FXML
    TableColumn<ServiceRequestTable, String> rType1;

    @FXML
    TableColumn<ServiceRequestTable, Integer> rID2;

    @FXML
    TableColumn<ServiceRequestTable, String> rType2;

    @FXML
    TableColumn<ServiceRequestTable, Integer> rID3;

    @FXML
    TableColumn<ServiceRequestTable, String> rType3;

    @FXML
    JFXComboBox newRequestComboBox;

    final ObservableList<ServiceRequestTable> newRequests = FXCollections.observableArrayList();
    final ObservableList<ServiceRequestTable> inProgRequests = FXCollections.observableArrayList();
    final ObservableList<ServiceRequestTable> completedRequests = FXCollections.observableArrayList();
    static ObservableList<String> requestTypes = FXCollections.observableArrayList(
            "Create New Service Request",
            "Language Interpreter Request",
            "Religious Request",
            "Computer Service Request",
            "Security Request",
            "Maintenance Request",
            "Sanitation Request",
            "Audio or Visual Help Request",
            "Gift Delivery Request",
            "Transportation Request",
            "Prescription Request"
    );

    MenuItem mi1 = new MenuItem("Claim Request");
    MenuItem mi2 = new MenuItem("Complete Request");
    MenuItem mi3 = new MenuItem("Delete Request");
    MenuItem mi4 = new MenuItem("Claim Request");
    MenuItem mi5 = new MenuItem("Complete Request");
    MenuItem mi6 = new MenuItem("Delete Request");
    ContextMenu menu1 = new ContextMenu();
    ContextMenu menu2 = new ContextMenu();

    /**
     * Initializes the hello message
     */
    @FXML
    public void onStartup() {
        helloMessage.setText("Hello, " + Main.currentUser.getFirstName() + " " + Main.currentUser.getLastName());
    }

    /**
     * Gets the list of service requests from the database and puts them in the appropriate tables
     */
    public void populateTableViews() {

        try {
            serviceRequestErrorLabel.setText("");
            requests = db.getAllRequests();

            for (Request r: requests) {
                if (r.getPriority() == 1) {
                    emergencyRequests.add(r);
                }
                else {
                    otherRequests.add(r);
                }
            }

        } catch (RequestNotFoundException rnfe) {
            serviceRequestErrorLabel.setText("There are currently no service requests.");
        }

        for (Request r: emergencyRequests) {
            if (r.isCompleted() == 0) {
                newRequests.add(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString()));
            }
            else if (r.isCompleted() == 1) {
                inProgRequests.add(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString()));
            }
            else {
                completedRequests.add(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString()));
            }
        }

        for (Request r: otherRequests) {
            if (r.isCompleted() == 0) {
                newRequests.add(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString()));
            }
            else if (r.isCompleted() == 1) {
                inProgRequests.add(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString()));
            }
            else {
                completedRequests.add(new ServiceRequestTable(r.getRequestID(), r.getRequestType().toString()));
            }
        }

        newRequestTable.setItems(newRequests);
        inProgRequestTable.setItems(inProgRequests);
        completedRequestTable.setItems(completedRequests);
    }

    /**
     * Initializes the tableviews
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        rID1.setCellValueFactory(new PropertyValueFactory<ServiceRequestTable, Integer>("requestID"));
        rType1.setCellValueFactory(new PropertyValueFactory<ServiceRequestTable, String>("requestType"));
        rID2.setCellValueFactory(new PropertyValueFactory<ServiceRequestTable, Integer>("requestID"));
        rType2.setCellValueFactory(new PropertyValueFactory<ServiceRequestTable, String>("requestType"));
        rID3.setCellValueFactory(new PropertyValueFactory<ServiceRequestTable, Integer>("requestID"));
        rType3.setCellValueFactory(new PropertyValueFactory<ServiceRequestTable, String>("requestType"));

        populateTableViews();

        newRequestComboBox.setItems(requestTypes);

        mi1.setOnAction((ActionEvent event) -> {
            claimRequestButtonOp(event);
        });

        mi3.setOnAction((ActionEvent event) -> {
            deleteRequestOp();
        });

        mi5.setOnAction((ActionEvent event) -> {
            completeRequestButtonOp(event);
        });

        menu1.getItems().add(mi1);
        menu1.getItems().add(mi2);
        menu1.getItems().add(mi3);
        menu2.getItems().add(mi4);
        menu2.getItems().add(mi5);
        menu2.getItems().add(mi6);

        newRequestTable.setContextMenu(menu1);
        inProgRequestTable.setContextMenu(menu2);
    }

    /**
     * Sets the available right click options based on the users information for the newRequestTable
     */
    public void disableMenu1OptionsOp() {
        menu1.getItems().get(1).setDisable(true);
        int requestID = newRequestTable.getSelectionModel().getSelectedItem().getRequestID();
        boolean claim = false;
        boolean delete = false;
        try {
            Request r = db.getOneRequest(requestID);
            if (r.getRequestType() == Request.requesttype.LANGUAGEINTERP || r.getRequestType() == Request.requesttype.HOLYPERSON) {
                if (Main.currentUser.getIsAdmin() ||
                        (db.EmployeeTypeToString(Main.currentUser.getEmployeeType()).equals(db.RequestTypeToString(r.getRequestType())) &&
                                Main.currentUser.getSubType().equals(r.getSubType()))) {
                    claim = true;
                }
                String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
                if (Main.currentUser.getIsAdmin() || firstAndLastName.equals(r.getMadeBy())) {
                    delete = true;
                }
            }
            else {
                if (Main.currentUser.getIsAdmin() || db.EmployeeTypeToString(Main.currentUser.getEmployeeType()).equals(db.RequestTypeToString(r.getRequestType()))) {
                    claim = true;
                }
                String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
                if (Main.currentUser.getIsAdmin() || firstAndLastName.equals(r.getMadeBy())) {
                    delete = true;
                }
            }
        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }

        if (!claim) {
            menu1.getItems().get(0).setDisable(true);
        }
        if (!delete) {
            menu1.getItems().get(2).setDisable(true);
        }
    }

    /**
     * Sets the available right click options based on the users information for the inProgRequestTable
     */
    public void disableMenu2OptionsOp() {
        menu2.getItems().get(0).setDisable(true);
        menu2.getItems().get(2).setDisable(true);
        int requestID = inProgRequestTable.getSelectionModel().getSelectedItem().getRequestID();
        boolean emptype = false;
        try {
            Request r = db.getOneRequest(requestID);
            String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
            if (Main.currentUser.getIsAdmin() || firstAndLastName.equals(r.getCompletedBy())) {
                emptype = true;
            }
        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }

        if (!emptype) {
            menu2.getItems().get(1).setDisable(true);
        }
    }

    public void deleteRequestOp() {
        int requestID = newRequestTable.getSelectionModel().getSelectedItem().getRequestID();
        try {
            Request r = db.getOneRequest(requestID);
            db.deleteRequest(r);
        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }
        refresh();
    }

    /**
     * Fills in the gridpane with info from requests in the newRequestTable
     */
    public void onNewRequestTableClickOp() {
        int requestID = newRequestTable.getSelectionModel().getSelectedItem().getRequestID();
        try {
            Request r = db.getOneRequest(requestID);
            timeCreatedLabel.setText(r.convertTime(r.getTimeMade().getTime()));
            requestTypeLabel.setText(r.toString());
            locationLabel.setText(r.getLocation());
            createdByLabel.setText(r.getMadeBy());
            assignedToLabel.setText(r.getCompletedBy());
            additionalInfoLabel.setText(r.getAdditionalInfo());
        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }
    }

    /**
     * Fills in the gridpane with info from requests in the inProgRequestTable
     */
    public void onInProgRequestTableClickOp() {
        int requestID = inProgRequestTable.getSelectionModel().getSelectedItem().getRequestID();
        try {
            Request r = db.getOneRequest(requestID);
            timeCreatedLabel.setText(r.convertTime(r.getTimeMade().getTime()));
            timeCompletedLabel.setText("");
            requestTypeLabel.setText(r.toString());
            locationLabel.setText(r.getLocation());
            createdByLabel.setText(r.getMadeBy());
            assignedToLabel.setText(r.getCompletedBy());
            additionalInfoLabel.setText(r.getAdditionalInfo());
        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }
    }

    /**
     * Fills in the gridpane with info from requests in the completedRequestTable
     */
    public void onCompletedRequestTableClickOp() {
        int requestID = completedRequestTable.getSelectionModel().getSelectedItem().getRequestID();
        try {
            Request r = db.getOneRequest(requestID);
            timeCreatedLabel.setText(r.convertTime(r.getTimeMade().getTime()));
            timeCompletedLabel.setText(r.convertTime(r.getTimeCompleted().getTime()));
            requestTypeLabel.setText(r.toString());
            locationLabel.setText(r.getLocation());
            createdByLabel.setText(r.getMadeBy());
            assignedToLabel.setText(r.getCompletedBy());
            additionalInfoLabel.setText(r.getAdditionalInfo());
        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }
    }

    /**
     * This button marks a service request as in progress
     * @param e action event
     */
    @FXML
    public void claimRequestButtonOp(ActionEvent e) {
        serviceRequestErrorLabel.setText("");
        int requestID = newRequestTable.getSelectionModel().getSelectedItem().getRequestID();
        try {
            Request r = db.getOneRequest(requestID);
            if (r.getRequestType() == Request.requesttype.LANGUAGEINTERP || r.getRequestType() == Request.requesttype.HOLYPERSON) {
                if (Main.currentUser.getIsAdmin() ||
                        (db.EmployeeTypeToString(Main.currentUser.getEmployeeType()).equals(db.RequestTypeToString(r.getRequestType())) &&
                                Main.currentUser.getSubType().equals(r.getSubType()))) {
                    r.setCompleted(1);
                    String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
                    r.setCompletedBy(firstAndLastName);
                    db.modifyRequest(r);
                } else {
                    serviceRequestErrorLabel.setText("You are not authorized to claim this service request");
                }
            }
            else {
                if (Main.currentUser.getIsAdmin() || db.EmployeeTypeToString(Main.currentUser.getEmployeeType()).equals(db.RequestTypeToString(r.getRequestType()))) {
                    r.setCompleted(1);
                    String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
                    r.setCompletedBy(firstAndLastName);
                    db.modifyRequest(r);
                } else {
                    System.out.println("hi");
                    serviceRequestErrorLabel.setText("You are not authorized to claim this service request");
                    serviceRequestErrorLabel.setVisible(true);
                }
            }

        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }
        refresh();
    }

    /**
     * This button marks a service request as complete
     * @param e action event
     */
    @FXML
    public void completeRequestButtonOp(ActionEvent e) {
        serviceRequestErrorLabel.setText("");
        int requestID = inProgRequestTable.getSelectionModel().getSelectedItem().getRequestID();
        try {
            Request r = db.getOneRequest(requestID);
            String firstAndLastName = Main.currentUser.getFirstName() + Main.currentUser.getLastName();
            if (Main.currentUser.getIsAdmin() || firstAndLastName.equals(r.getCompletedBy())) {
                r.setCompleted(2);
                r.setCompletedBy(firstAndLastName);
                db.completeRequest(r);
            }
            else {
                serviceRequestErrorLabel.setText("You are not authorized to claim this service request");
            }
        }
        catch (RequestNotFoundException re) {
            re.printStackTrace();
        }
        refresh();
    }

    /**
     * When the back button is pressed the stage is brought back to the home screen
     * @param e action event
     * @throws IOException
     */
    @FXML
    public Boolean backButtonOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;

        stage = (Stage) backButton.getScene().getWindow();
        if(Main.currentUser.getIsAdmin()) {
            loader = new FXMLLoader(getClass().getResource("/FXML/admin/AdminMenuScreen.fxml"));
        }else{
            loader = new FXMLLoader(getClass().getResource("/FXML/home/HomeScreen.fxml"));
            Main.logoutCurrentUser();
        }
        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }

        backButton.getScene().setRoot(root);
        return true;
    }

    /**
     * When the records button is pressed the stages is brought to the records screen
     */
    @FXML
    public Boolean recordsButtonOp(ActionEvent e) {
        Parent root;
        FXMLLoader loader;
        RecordScreenController recordScreenController;

        loader = new FXMLLoader(getClass().getResource("/FXML/service/RecordScreen.fxml"));

        try {
            root = loader.load();
        } catch (IOException ie) {
            ie.printStackTrace();
            return false;
        }
        recordScreenController = loader.getController();
        recordScreenController.onStartUp();
        recordsButton.getScene().setRoot(root);
        return true;
    }

    /**
     * opens the pop up for the new service request form
     * @param e action event
     */
    @FXML
    public Boolean selectNewRequestOp(ActionEvent e) {
        Stage stage;
        Parent root;
        FXMLLoader loader;
        String selection = newRequestComboBox.getSelectionModel().getSelectedItem().toString();

        if (selection.equals("Create New Service Request")) {
            return false;
        }
        else if (selection.equals("Transportation Request")) {

            TransportationRequest tr = new TransportationRequest();

            try {
                tr.run(0,0,0,0, null, null, null);
                stage = (Stage) newRequestComboBox.getScene().getWindow();
                stage.setFullScreen(true);
            }
            catch (ServiceException se) {
                se.printStackTrace();
            }
            newRequestComboBox.setValue("Create New Service Request");
            return true;
        }
        else if (selection.equals("Prescription Request")) {

            RaikouAPI r = new RaikouAPI();

            try {
                r.run(0,0,1920,1080, null, null, null);
                stage = (Stage) newRequestComboBox.getScene().getWindow();
                stage.setFullScreen(true);
            }
            catch (edu.wpi.cs3733d18.teamR.ServiceException se) {
                se.printStackTrace();
            }
            newRequestComboBox.setValue("Create New Service Request");
            return true;
        }
        else {
            stage = new Stage();
            loader = new FXMLLoader(getClass().getResource("/FXML/service/FormContainer.fxml"));

            try {
                root = loader.load();
            } catch (IOException ie) {
                ie.printStackTrace();
                return false;
            }


            popUpController = loader.getController();
            popUpController.StartUp(this, selection);
            stage.setScene(new Scene(root, 1000, 950));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(newRequestComboBox.getScene().getWindow());
            stage.show();
            newRequestComboBox.setValue("Create New Service Request");
            return true;
        }
    }

    /**
     * Called by various functions to clear the TableViews and calls populateTableViews() again
     */
    public void refresh() {
        for (int i = 0; i < newRequestTable.getItems().size(); i++) {
            newRequestTable.getItems().clear();
        }
        for (int i = 0; i < inProgRequestTable.getItems().size(); i++) {
            inProgRequestTable.getItems().clear();
        }
        for (int i = 0; i < completedRequestTable.getItems().size(); i++) {
            completedRequestTable.getItems().clear();
        }
        emergencyRequests.clear();
        otherRequests.clear();
        populateTableViews();
    }
}

