package uk.ac.aber.dcs.mcp.GUI.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import uk.ac.aber.dcs.mcp.GUI.model.Constants;
import uk.ac.aber.dcs.mcp.enums.AttendantStatus;
import uk.ac.aber.dcs.mcp.utility.GSONHandler;
import uk.ac.aber.dcs.mcp.utility.GarageAttendant;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller that handles activities for Garage Attendant
 *
 * @author Sean Seow
 */
public class AttendantManagerController implements Initializable {


    private ApplicationController main;

    private ObservableList<GarageAttendant> garageAttendantList = FXCollections.observableArrayList();

    @FXML private TextField nameTextField;
    @FXML private TableView<GarageAttendant> garageAttendantTable;
    @FXML private TableColumn<GarageAttendant, String> nameColumn;
    @FXML private TableColumn<GarageAttendant, AttendantStatus> statusColumn;
    @FXML private TableColumn<GarageAttendant, String> vehicleColumn;



    /**
     * Set up the Application controller
     * @param applicationController The main controller
     */
    public void init(ApplicationController applicationController) {this.main = applicationController;}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("licensePlateNumber"));
    }


    /**
     * Gets the name from the text field and adds the new attendant to the list
     * and updates the table when the add button is clicked
     */
    @FXML public void addAttendant() {
        String name = nameTextField.getText().trim();
        boolean valid = Constants.validateInput(name, "[A-z ]+", 15);
        if(!name.equals("")) {
            if(valid) {
                GarageAttendant attendant = new GarageAttendant(name);
                garageAttendantList.add(attendant);
                garageAttendantTable.setItems(garageAttendantList);
                nameTextField.setText("");
            } else {
                Constants.displayErrorWindow("Please enter a valid name!!");
            }
        } else {
            Constants.displayErrorWindow("Name must not be empty!!");
        }
    }


    /**
     * The selected garage attendant in the table will be deleted when the button
     * is clicked
     */
    @FXML public void deleteAttendant() {
        ObservableList<GarageAttendant> attendants;
        attendants = garageAttendantTable.getItems();
        GarageAttendant attendantToRemove = garageAttendantTable.getSelectionModel().getSelectedItem();
        garageAttendantList.remove(attendantToRemove);
        attendants.remove(attendantToRemove);
    }


    /**
     * Set the status of the selected garage attendant in the table to AVAILABLE
     */
    @FXML public void setStatusToAvailable() {
        GarageAttendant attendant = garageAttendantTable.getSelectionModel().getSelectedItem();
        attendant.setStatus(AttendantStatus.AVAILABLE);
        garageAttendantTable.refresh();
    }


    /**
     * Set the status of the selected garage attendant in the table to BUSY
     */
    @FXML public void setStatusToBusy() {
        GarageAttendant attendant = garageAttendantTable.getSelectionModel().getSelectedItem();
        attendant.setStatus(AttendantStatus.BUSY);
        garageAttendantTable.refresh();
    }


    /**
     * Set the status of the selected garage attendant in the table to OFF
     */
    @FXML public void setStatusToOff() {
        GarageAttendant attendant = garageAttendantTable.getSelectionModel().getSelectedItem();
        attendant.setStatus(AttendantStatus.OFF);
        garageAttendantTable.refresh();
    }


    /**
     * Sets a garage attendant to the vehicle and set the status to the status specified
     * Can be used for relieve the garage attendant from a vehicle too
     * @param attendant The garage attendant to be modified
     * @param status The status to be set
     * @param licensePlateNumber The license plate number to be assigned to the garage attendant
     */
    public void setAttendantStatus(GarageAttendant attendant, AttendantStatus status, String licensePlateNumber) {
        for(GarageAttendant a : garageAttendantList) {
            if(a.getName().equals(attendant.getName())) {
                a.setStatus(status);
                a.setLicensePlateNumber(licensePlateNumber);
                garageAttendantTable.refresh();
            }
        }
    }


    /**
     * Checks if there are any garage attendant whose status is AVAILABLE
     * @return The garage attendant that is AVAILABLE
     */
    public GarageAttendant getAvailableAttendant() {
        for(GarageAttendant attendant : garageAttendantList) {
            if(attendant.getStatus() == AttendantStatus.AVAILABLE) {
                return attendant;
            }
        } return null;
    }


    /**
     * Prints Garage Attendant List to the console. Used for debugging in ApplicationController
     */
    public void printAttendantList() {
        for(GarageAttendant attendant : garageAttendantList) {
            System.out.println(attendant.toString());
        }
    }


    /**
     * Save operation for Garage Attendant
     */
    public void save() {
        GSONHandler.save(Constants.ATTENDANT_LIST_DATA_FILE, garageAttendantList);
    }


    /**
     * Load operation for Garage Attendant
     */
    public void load() {
        garageAttendantList = FXCollections.observableArrayList(
                GSONHandler.loadGarageAttendant(Constants.ATTENDANT_LIST_DATA_FILE)) ;
        garageAttendantTable.setItems(garageAttendantList);
        garageAttendantTable.refresh();
    }


}
