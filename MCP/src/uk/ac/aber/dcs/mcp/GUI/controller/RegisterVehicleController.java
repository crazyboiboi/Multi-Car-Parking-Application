package uk.ac.aber.dcs.mcp.GUI.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import uk.ac.aber.dcs.mcp.GUI.model.Constants;
import uk.ac.aber.dcs.mcp.enums.VehicleType;
import uk.ac.aber.dcs.mcp.utility.*;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * The controller that handles activities for Vehicle
 *
 * @author Sean Seow
 */
public class RegisterVehicleController implements Initializable {

    private ApplicationController main;

    private Vehicle currentVehicleObject;

    @FXML private TextField licensePlateNumberTextField;
    @FXML private TextArea vehicleRegisterTextArea;
    @FXML private Button registerVehicleButton;
    @FXML private ChoiceBox<VehicleType> vehicleTypeChoiceBox;
    @FXML private RadioButton yesRadioButton;
    @FXML private RadioButton noRadioButton;
    private ToggleGroup yesAndNoToggleGroup;



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
    public void resetToDefault() {
        currentVehicleObject = null;
        licensePlateNumberTextField.setText("");
        vehicleRegisterTextArea.setText("");
        registerVehicleButton.setDisable(true);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetToDefault();
        //Set up the values in the choice box (Vehicle Type)
        for(VehicleType type : VehicleType.values()) {
            vehicleTypeChoiceBox.getItems().add(type);
        }
        vehicleTypeChoiceBox.setValue(vehicleTypeChoiceBox.getItems().get(0));


        //Set up Yes and No Radio button so that only one button can be selected at times
        yesAndNoToggleGroup = new ToggleGroup();
        this.yesRadioButton.setToggleGroup(yesAndNoToggleGroup);
        this.noRadioButton.setToggleGroup(yesAndNoToggleGroup);
        this.noRadioButton.setSelected(true);
    }


    /**
     * Displays the information in text area when the button is pressed
     */
    @FXML public void confirmRegister() {
        String licensePlateNumber = licensePlateNumberTextField.getText();
        boolean valid = Constants.validateInput(licensePlateNumber, "[A-Z]+[0-9 ]+", 11);
        if (!licensePlateNumber.equals("")) {
            if(valid) {
                VehicleType vehicleType = vehicleTypeChoiceBox.getValue();
                boolean hasDisabledCard = yesAndNoToggleGroup.getSelectedToggle().equals(yesRadioButton);
                Vehicle vehicle = null;
                switch(vehicleType.toString()) {
                    case "STANDARD":
                        vehicle = new Standard(licensePlateNumber, hasDisabledCard);
                        break;
                    case "HIGHER":
                        vehicle = new Higher(licensePlateNumber, hasDisabledCard);
                        break;
                    case "LONGER":
                        vehicle = new Longer(licensePlateNumber, hasDisabledCard);
                        break;
                    case "COACH":
                        vehicle = new Coach(licensePlateNumber);
                        break;
                    case "MOTORBIKE":
                        vehicle = new Motorbike(licensePlateNumber, hasDisabledCard);
                        break;
                }
                currentVehicleObject = vehicle;
                vehicleRegisterTextArea.setText(String.format("License Plate Number: %s\nVehicle Type: %s\nDisabled: %s",
                        licensePlateNumber, vehicleType, hasDisabledCard));
                registerVehicleButton.setDisable(false);
            } else {
                Constants.displayErrorWindow("License Plate Number must have alphabets and numbers and cannot have more than 10 characters!!");
            }
        } else {
            Constants.displayErrorWindow("License Plate Number Field must not be empty");
        }
    }


    /**
     * Register the vehicle and assign a space in the carpark
     */
    @FXML public void registerVehicle() {
        main.setVehicleToSpace(currentVehicleObject);
        licensePlateNumberTextField.setText("");
        vehicleRegisterTextArea.setText("");
        vehicleTypeChoiceBox.setValue(vehicleTypeChoiceBox.getItems().get(0));
        currentVehicleObject = null;
        registerVehicleButton.setDisable(true);
    }


    /**
     * A pop-up window indicating carpark is full
     */
    public void outputCarparkFullMessage() {
        Constants.displayErrorWindow("Carpark is full. Register failed!!");
    }

}
