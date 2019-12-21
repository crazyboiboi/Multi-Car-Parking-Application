package uk.ac.aber.dcs.mcp.GUI.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import uk.ac.aber.dcs.mcp.enums.AttendantStatus;
import uk.ac.aber.dcs.mcp.GUI.model.Constants;
import uk.ac.aber.dcs.mcp.utility.GarageAttendant;
import uk.ac.aber.dcs.mcp.utility.ParkingReceipt;
import uk.ac.aber.dcs.mcp.utility.ParkingSpace;
import uk.ac.aber.dcs.mcp.utility.Vehicle;

import java.io.IOException;


/**
 * The main controller that handles the Application Window
 *
 * @author Sean Seow
 */
public class ApplicationController {

    @FXML private RegisterVehicleController registerVehicleController;
    @FXML private CarparkController carparkController;
    @FXML private ReceiptController receiptController;
    @FXML private AttendantManagerController attendantManagerController;

    @FXML private Tab manageGarageAttendantTab;
    @FXML private Tab manageReceiptTab;



    public void loadAdminTabs() {
        System.out.println("Loading admin tabs...");
        manageReceiptTab.setDisable(true);
    }

    public void loadCustomerTabs() {
        System.out.println("Loading customer tabs...");
        manageGarageAttendantTab.setDisable(true);
    }


    /**
     * Loads the back login scene when the user clicks on the button
     */
    @FXML public void loadLoginPage(ActionEvent event) throws IOException {
        saveData();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/uk/ac/aber/dcs/mcp/GUI/view/LoginView.fxml"));
        Parent mainApplicationParent = loader.load();
        Scene mainApplicationScene = new Scene(mainApplicationParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainApplicationScene);

        Constants.resetWindowPosition(window);
    }


    /*
     * Set up the controllers for the Application Window scene
     */
    @FXML private void initialize() {
        System.out.println("Application started");
        registerVehicleController.init(this);
        carparkController.init(this);
        receiptController.init(this);
        attendantManagerController.init(this);
    }


    /**
     * Exits and saves the application when the button is clicked
     */
    @FXML public void exitApplication() {
        saveData();
        Platform.exit();
    }


    /**
     * A button used for debugging purpose
     * Prints out the state of the carpark in the console
     */
    @FXML public void printStatusInConsole() {
        System.out.println("\n\nCarpark Status");
        carparkController.printCarpark();
        System.out.println("\nReceipt List");
        receiptController.printReceiptList();
        System.out.println("\nAttendant List");
        attendantManagerController.printAttendantList();
    }


    /**
     * Resets the tab every time the user leaves the tab
     */
    @FXML public void manageReceiptTabSelected() {
        receiptController.resetTabToDefault();
    }


    /**
     * Resets the tab every time the user leaves the tab
     */
    @FXML public void registerVehicleTabSelected() {
        registerVehicleController.resetToDefault();
    }


    /**
     * Use getAvailableAttendant() from the attendantManagerController
     * @return A garage attendant or null
     */
    public GarageAttendant assignGarageAttendant() {
        return attendantManagerController.getAvailableAttendant();
    }


    /**
     * Use resetParkingSpace() from the carparkController to reset a Parking Space
     * @param space The Parking Space to be reset
     */
    public void resetParkingSpace(ParkingSpace space) {carparkController.resetParkingSpace(space);}


    /**
     * Assign the vehicle to a Parking Space if any available and empty
     * @param vehicle The vehicle to be assigned to a space
     */
    public void setVehicleToSpace(Vehicle vehicle) {
        ParkingSpace space = carparkController.setVehicleToSpace(vehicle);
        if(space != null) {
            long timeEntered = System.currentTimeMillis();
            String receiptNumber = receiptController.generateReceiptNumber();
            ParkingReceipt receipt = new ParkingReceipt(receiptNumber, space, timeEntered, vehicle);
            receiptController.addReceiptToList(receipt);
        } else {
            registerVehicleController.outputCarparkFullMessage();
        }
    }


    /**
     * Assign vehicle to a garage attendant to be in charge for assisted collection
     * @param attendant The attendant to be assigned
     * @param status Status to be set for the attendant
     * @param licensePlateNumber The license plate number of the vehicle
     */
    public void assignVehicle (GarageAttendant attendant, AttendantStatus status, String licensePlateNumber) {
        attendantManagerController.setAttendantStatus(attendant, status, licensePlateNumber);
    }


    /**
     * Saves the state of the carpark to a JSON file
     */
    public void saveData() {
        System.out.println("Saving data to file....");
        attendantManagerController.save();
        receiptController.save();
        carparkController.save();
    }


    /**
     * Loads the state of the carpark from a JSON file
     * If there are no files to load, it will create new file store
     */
    public void loadData() {
        System.out.println("Loading data from file....");
        attendantManagerController.load();
        receiptController.load();
        carparkController.load();
    }








}
