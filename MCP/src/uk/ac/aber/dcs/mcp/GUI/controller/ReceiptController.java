package uk.ac.aber.dcs.mcp.GUI.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import uk.ac.aber.dcs.mcp.GUI.model.Constants;
import uk.ac.aber.dcs.mcp.enums.AttendantStatus;
import uk.ac.aber.dcs.mcp.enums.ReceiptStatus;
import uk.ac.aber.dcs.mcp.utility.GSONHandler;
import uk.ac.aber.dcs.mcp.utility.GarageAttendant;
import uk.ac.aber.dcs.mcp.utility.ParkingReceipt;
import uk.ac.aber.dcs.mcp.utility.ParkingSpace;
import java.util.Calendar;


/**
 * The controller that handles activities for Parking Receipt
 *
 * @author Sean Seow
 */
public class ReceiptController {

    //Reference to the main controller, ApplicationController
    private ApplicationController main;

    private ObservableList<ParkingReceipt> receipts = FXCollections.observableArrayList();
    private ParkingReceipt currentReceiptObject;
    private double totalAmount = 0;
    private double amountPaid = 0;
    private int receiptCount = 0;
    private double[] zoneRates;


    @FXML private TextField receiptNumberTextField;
    @FXML private TextArea receiptTextArea;
    @FXML private VBox paymentSection;
    @FXML private Label receiptMessageLabel;
    @FXML private Label totalAmountLabel;
    @FXML private Label amountPaidLabel;
    @FXML private Label changeMessageLabel;
    @FXML private TextField amountTextField;
    @FXML private Button requestVehicleCollectionButton;
    @FXML private Button exitCarparkButton;
    @FXML private Button paymentButton;
    @FXML private Button requestParkingAssistanceButton;


    /**
     * Set up the Application controller
     * @param applicationController The main controller
     */
    public void init(ApplicationController applicationController) {
        this.main = applicationController;
    }


    /**
     * How the scene initially looked like when it first started
     */
    public void resetTabToDefault() {
        currentReceiptObject = null;
        receiptMessageLabel.setText("");
        receiptNumberTextField.setText("");
        receiptTextArea.setText("");
        changeMessageLabel.setText("");
        paymentSection.setDisable(true);
        totalAmountLabel.setText(String.format("Total amount: 0.00 unit(s)"));
        amountPaidLabel.setText("Amount paid: 0.00 unit(s)");
        requestParkingAssistanceButton.setDisable(false);
        paymentButton.setDisable(true);
        requestVehicleCollectionButton.setDisable(true);
        exitCarparkButton.setDisable(true);
        totalAmount = 0;
        amountPaid = 0;
    }

    /**
     * Generates a receipt number for new receipt
     * @return A receipt number in String
     */
    public String generateReceiptNumber() {return String.format("R%s", receiptCount++);}


    /**
     * Gets the value from the receipt input text field and finds the receipt
     * in the list of receipt
     */
    @FXML public void searchForReceipt() {
        String receiptNumber = receiptNumberTextField.getText();
        boolean valid = Constants.validateInput(receiptNumber, "^R[0-9]*", 10);
        if(!receiptNumber.equals("")) {
            if(valid) {
                ParkingReceipt receipt = getReceipt(receiptNumber);
                if(receipt != null) {
                    currentReceiptObject = receipt;
                    receiptMessageLabel.setText("");
                    displayReceipt();
                    enableFunctionality();
                } else {
                    receiptMessageLabel.setTextFill(Color.RED);
                    receiptMessageLabel.setText("Receipt not found!!");
                    receiptTextArea.setText("");
                }
                receiptNumberTextField.setText("");
            } else {
                Constants.displayErrorWindow("Receipt Number must starts with 'R' followed by numbers");
                receiptTextArea.setText("");
            }
        } else {
            Constants.displayErrorWindow("Receipt number must not be empty!!");
        }
    }


    /*
     * When a receipt is found and depending on the Receipt, enables and disables
     * certain functionality
     */
    private void enableFunctionality() {
        requestParkingAssistanceButton.setDisable(true);
        if(currentReceiptObject.getVehicle().isAllowAssistedParking() && currentReceiptObject.getAttendant() == null) {
            requestVehicleCollectionButton.setDisable(false);
        } else {
            requestVehicleCollectionButton.setDisable(true);
        }
        if(currentReceiptObject.isTokenised()) {
            exitCarparkButton.setDisable(false);
            paymentButton.setDisable(true);
            requestVehicleCollectionButton.setDisable(true);
        } else {
            exitCarparkButton.setDisable(true);
            paymentButton.setDisable(false);
            requestVehicleCollectionButton.setDisable(false);
        }
    }


    /**
     * Checks the garage attendant list and outputs a message to user
     */
    @FXML
    public void requestParkingAssistance() {
        if(currentReceiptObject == null) {
            GarageAttendant attendant = main.assignGarageAttendant();
            if(attendant != null) {
                receiptMessageLabel.setText("Attendant " + attendant.getName() +
                        " is on the way.");
            } else {
                Constants.displayErrorWindow("There are no attendants available");
            }
        }
    }


    /**
     * Checks if there is any attendant available and assign the attendant
     * to the vehicle
     */
    @FXML
    public void requestVehicleCollection() {
        //Get an available attendant
        //Set the attendant to the receipt
        //Set the status of the attendant
        //Set receipt status to collecting
        GarageAttendant attendant = main.assignGarageAttendant();
        if(attendant != null) {
            currentReceiptObject.setAttendant(attendant);
            String licensePlateNumber = currentReceiptObject.getVehicle().getLicensePlateNumber();
            main.assignVehicle(attendant, AttendantStatus.BUSY, licensePlateNumber);
            currentReceiptObject.setStatus(ReceiptStatus.COLLECTING);
            requestVehicleCollectionButton.setDisable(true);
            receiptMessageLabel.setText("");
        } else {
            Constants.displayErrorWindow("No attendants available!!");
        }
    }


    /**
     * When the owner of the receipt has paid and clicks on the Exit Carpark
     * Button, then it will remove the receipt from the list and reset the
     * attendant and the parking space
     */
    @FXML
    public void exitCarpark() {
        //Check if there is an attendant in charge
        //If so, update the status of the attendant to AVAILABLE and set null to the vehicle
        //Update assigned parking space to null
        //Remove the receipt from the list
        GarageAttendant attendant = currentReceiptObject.getAttendant();
        ParkingSpace assignedSpace = currentReceiptObject.getSpace();
        if(attendant != null) {
            main.assignVehicle(attendant, AttendantStatus.AVAILABLE, "");
        }
        if(assignedSpace != null) {
            main.resetParkingSpace(assignedSpace);
        }
        receipts.remove(currentReceiptObject);
        resetTabToDefault();
        receiptMessageLabel.setText("Thank you and have a nice day!");
    }


    /**
     * Start payment process
     */
    @FXML
    public void initialisePayment() {
        paymentSection.setDisable(false);
        paymentButton.setDisable(true);
        long timeExit = System.currentTimeMillis();
        currentReceiptObject.setTimeExit(timeExit);
        int zone = currentReceiptObject.getSpace().getZone();
        totalAmount = calculateAmount(zone, timeExit);
        totalAmountLabel.setText(String.format("Total amount: %.2f unit(s)", totalAmount));
        amountPaidLabel.setText("Amount paid: 0.00 unit(s)");
    }


    /*
     * Calculates the total amount depending on the time spent in the carpark and zone
     */
    private double calculateAmount(int zone, long timeExit) {
        double rate = zoneRates[zone];
        if(currentReceiptObject.getVehicle().hasDisabledCard()) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(currentReceiptObject.getTimeEntered());
            int day = c.get(Calendar.DAY_OF_WEEK);
            if(day == Calendar.SUNDAY) {
                rate = 0;
            } else {
                rate /= 2;
            }
        }
        long timeEntered = currentReceiptObject.getTimeEntered();
        long timeSpent = timeExit - timeEntered;
        long secondsSpent = timeSpent/1000;
        int minutesSpent = (int)(secondsSpent/60);
        int hours = (minutesSpent/60) + 1;
        return rate * hours;
    }


    /**
     * The user enters the amount they wish to pay in the text field and
     * calculates the change after payment
     */
    @FXML
    public void enterAmount() {
        try {
            String amount = amountTextField.getText();
            amountPaid += Double.parseDouble(amount);
            String msg = String.format("Amount paid: %.2f unit(s)", amountPaid);
            amountPaidLabel.setText(msg);
            if(amountPaid >= totalAmount) {
                System.out.println("Calculate change");
                currentReceiptObject.setTokenised(true);
                paymentSection.setDisable(true);
                exitCarparkButton.setDisable(false);
                requestVehicleCollectionButton.setDisable(true);
                currentReceiptObject.setStatus(ReceiptStatus.PAID);
                currentReceiptObject.setAmount(totalAmount);
                changeMessageLabel.setText(String.format("Your change: %.2f", Math.abs(totalAmount - amountPaid)));
            }
            amountTextField.setText("");
        } catch (NumberFormatException ex) {
            Constants.displayErrorWindow("Please enter numbers only!!");
            amountTextField.setText("");
        }

    }


    /* Updates the ReceiptTextArea to display the current status of the receipt */
    private void displayReceipt() {
        String receiptNumber = currentReceiptObject.getReceiptNumber();
        String licensePlateNumber = currentReceiptObject.getVehicle().getLicensePlateNumber();
        String spaceID = currentReceiptObject.getSpace().getSpaceID();
        String timeEntered = Constants.convertTimeToString(currentReceiptObject.getTimeEntered());
        String status = currentReceiptObject.getStatus().toString();
        String timeExit = "-";
        String amountPaid = "-";
        if(currentReceiptObject.isTokenised()) {
            timeExit = Constants.convertTimeToString(currentReceiptObject.getTimeExit());
            amountPaid = Double.toString(currentReceiptObject.getAmount());
        }
        String msg = String.format("Receipt Number: %s\nIssue To: %s\nSpaceID: %s\nTime Entered: %s\nTimeExit: %s" +
                        "\nStatus: %s\nAmount Paid: %s", receiptNumber, licensePlateNumber, spaceID, timeEntered,
                timeExit, status, amountPaid);
        receiptTextArea.setText(msg);
    }


    /* Gets the receipt object from the list of receipts */
    private ParkingReceipt getReceipt(String receiptNumber) {
        for(ParkingReceipt receipt : receipts) {
            if(receipt.getReceiptNumber().equals(receiptNumber)) {
                return receipt;
            }
        }
        return null;
    }


    /**
     * Used in Application Controller (main)
     * Update the list in the Receipt Controller
     * @param receipt The receipt created from main
     */
    public void addReceiptToList(ParkingReceipt receipt) {
        receipts.add(receipt);
    }


    /**
     * Used in Application Controller (main)
     * A useful print() bind to the PrintStatusToControl Button to output the
     * current list of receipt in the console for debugging purpose
     */
    public void printReceiptList() {
        for(ParkingReceipt receipt : receipts) {
            System.out.println(receipt.toString());
        }
    }


    /**
     * Save operation for Parking Receipt
     */
    public void save() {
        GSONHandler.save(Constants.PARKING_RECEIPTS_DATA_FILE, receipts);
    }


    /**
     * Load operation for Parking Receipt
     */
    public void load() {
        receipts = FXCollections.observableArrayList(
                GSONHandler.loadParkingReceipts(Constants.PARKING_RECEIPTS_DATA_FILE));
        if(!receipts.isEmpty()) {
            String lastReceiptNumber = receipts.get(receipts.size()-1).getReceiptNumber();
            receiptCount = Integer.parseInt(lastReceiptNumber.substring(1));
        }
        zoneRates = GSONHandler.loadZoneRates(Constants.PARKING_RATE_DATA_FILE);

    }



}