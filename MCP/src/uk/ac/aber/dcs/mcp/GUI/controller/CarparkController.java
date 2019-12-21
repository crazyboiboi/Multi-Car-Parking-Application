package uk.ac.aber.dcs.mcp.GUI.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import uk.ac.aber.dcs.mcp.GUI.model.Constants;
import uk.ac.aber.dcs.mcp.enums.VehicleType;
import uk.ac.aber.dcs.mcp.utility.GSONHandler;
import uk.ac.aber.dcs.mcp.utility.ParkingSpace;
import uk.ac.aber.dcs.mcp.utility.Vehicle;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * The controller that handles activities of the CarPark
 *
 * @author Sean Seow
 */
public class CarparkController implements Initializable {

    private ApplicationController main;

    private ObservableList<ParkingSpace> carpark = FXCollections.observableArrayList();

    @FXML private TableView<ParkingSpace> carparkTable;
    @FXML private TableColumn<ParkingSpace, Integer> zoneColumn;
    @FXML private TableColumn<ParkingSpace, String> spaceIDColumn;
    @FXML private TableColumn<ParkingSpace, Boolean> availabilityColumn;
    @FXML private TableColumn<ParkingSpace, Vehicle> vehicleColumn;



    /**
     * Set up the Application controller
     * @param applicationController The main controller
     */
    public void init(ApplicationController applicationController) {
        main = applicationController;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set up the table for displaying parking spaces in the carpark
        zoneColumn.setCellValueFactory(new PropertyValueFactory<>("zone"));
        spaceIDColumn.setCellValueFactory(new PropertyValueFactory<>("spaceID"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
        //Gather vehicle and display the license plate number only -> uses toString()
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicle"));
    }


    /**
     * Used in ApplicationController to reset a Parking Space in the carpark
     * @param space The space to be reset
     */
    public void resetParkingSpace(ParkingSpace space) {
        for (ParkingSpace s : carpark) {
            if(s.getSpaceID().equals(space.getSpaceID())) {
                s.setVehicle(null);
                s.setAvailability(true);
                carparkTable.refresh();
            }
        }
    }


    /**
     * Used in Application Controller to set vehicle to a Parking Space
     * @param vehicle The vehicle to be set
     */
    public ParkingSpace setVehicleToSpace(Vehicle vehicle) {
        int zone = getZone(vehicle.getVehicleType());
        ParkingSpace space = getParkingSpace(zone);
        if(space != null) {
            int spaceIndex = carpark.indexOf(space);
            carpark.get(spaceIndex).setVehicle(vehicle);
            carpark.get(spaceIndex).setAvailability(false);
            carparkTable.refresh();
            return space;
        } else {
            return null;
        }

    }


    /*
     * Get an available and empty parking space from the zone
     */
    private ParkingSpace getParkingSpace (int zone) {
        for(ParkingSpace space : carpark) {
            if (space.getZone() == zone) {
                if (space.isAvailability()) {
                    return space;
                }
            } else if (space.getZone() > zone) {
                break;
            }
        }
        return null;
    }


    /*
     * Special case for Standard type vehicles because they have 2 zones
     */
    private int checkForStandard() {
        for(int i=0; i<2; i++) {
            //If there are parking in zone 1, return
            //Else return zone 4
            if(carpark.get(i).isAvailability()) {
                return 1;
            }
        }
        return 4;
    }


    /*
     * Get the zone for the type of vehicle specified
     */
    private int getZone(VehicleType type) {
        int zone;
        if(type == VehicleType.STANDARD) {
            zone = checkForStandard();
        } else if(type == VehicleType.HIGHER){
            zone = 1;
        } else if(type == VehicleType.LONGER){
            zone = 2;
        } else if(type == VehicleType.COACH){
            zone = 3;
        } else {
            zone = 5;
        }
        return zone;
    }


    /**
     * Prints carpark to the console. Used for debugging in ApplicationController
     */
    public void printCarpark() {
        for(ParkingSpace space : carpark) {
            System.out.println(space.toString());
        }
    }


    /**
     * Save operation for Carpark
     */
    public void save() {
        GSONHandler.save(Constants.CARPARK_DATA_FILE, carpark);
    }


    /**
     * Load operation for Carpark
     */
    public void load() {
        carpark = FXCollections.observableArrayList(GSONHandler.loadCarpark(Constants.CARPARK_DATA_FILE));
        if(carpark.isEmpty()) {
            for(int zone=1; zone<6; zone++) {
                for(int j=0; j<2; j++) {
                    String id = String.format("%s%03d", zone, j);
                    ParkingSpace space = new ParkingSpace(zone, id);
                    carpark.add(space);
                }
            }
            carparkTable.setItems(carpark);
        }
        carparkTable.setItems(carpark);
        carparkTable.refresh();
    }




}