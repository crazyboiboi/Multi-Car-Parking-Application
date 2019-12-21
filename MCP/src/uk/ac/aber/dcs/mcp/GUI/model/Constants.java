package uk.ac.aber.dcs.mcp.GUI.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A class that stores constant values
 *
 * @Author Sean Seow
 */
public class Constants {


    private static SimpleDateFormat formatter;

    public static final String PARKING_RATE_DATA_FILE = "HourlyRate.txt";
    public static final String CARPARK_DATA_FILE = "Carpark.json";
    public static final String ATTENDANT_LIST_DATA_FILE = "Attendants.json";
    public static final String PARKING_RECEIPTS_DATA_FILE = "Receipts.json";




    public static boolean validateInput (String input, String pattern, int maxLength) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        if(input.length() <= maxLength) {
            return m.matches();
        }
        return false;
    }


    public static String convertTimeToString(long time) {
        formatter = new SimpleDateFormat(uk.ac.aber.dcs.mcp.enums.Constants.ParkingReceiptTimeFormat);
        Date date = new Date(time);
        return formatter.format(date);
    }



    /*------------------------------------------- JAVA FX -------------------------------------------*/

    public static void displayErrorWindow(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }


    //Re-position the new window since it is different size
    //New position will be at the center of the screen
    public static void resetWindowPosition(Stage window) {
        Rectangle2D windowBounds = Screen.getPrimary().getVisualBounds();
        window.setX((windowBounds.getWidth() - window.getWidth())/2);
        window.setY((windowBounds.getHeight() - window.getHeight())/2);
        window.show();
    }





}
