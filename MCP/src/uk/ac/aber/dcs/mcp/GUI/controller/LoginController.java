package uk.ac.aber.dcs.mcp.GUI.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uk.ac.aber.dcs.mcp.GUI.model.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * The controller that handles all login activities
 *
 * @author Sean Seow
 */
public class LoginController {

    @FXML private PasswordField code;
    @FXML private Label invalidMessageLabel;

    /*
     * Check if the content of the password field matches the admin login code
     */
    @FXML void checkValidAdminCode(ActionEvent event) throws IOException {
        String value = code.getText();
        if(value.equals("admin")){
            loadScene(event, true);
        } else {
            invalidMessageLabel.setTextFill(Color.rgb(255,0,0));
            invalidMessageLabel.setText("Invalid code");
        }
    }


    /*
     * Loads customer scene
     */
    @FXML void loadCustomerViewScene(ActionEvent event) throws IOException {
        loadScene(event, false);
    }


    /*
     * Loads the scene overall and set the window to always be in the center
     */
    private void loadScene(ActionEvent event, boolean loadAdminScene) throws IOException{
        //Gets the FXML scene
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/uk/ac/aber/dcs/mcp/GUI/view/ApplicationView.fxml"));
        Parent mainApplicationParent = loader.load();
        Scene mainApplicationScene = new Scene(mainApplicationParent);

        //Get controller so we can use the methods from there
        ApplicationController main = loader.getController();
        main.loadData();
        if(loadAdminScene) {
            main.loadAdminTabs();
        } else {
            main.loadCustomerTabs();
        }

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainApplicationScene);

        Constants.resetWindowPosition(window);
    }


    /**
     * Exits the application
     */
    @FXML public void exitApplication() {
        Platform.exit();
    }



}
