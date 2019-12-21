package uk.ac.aber.dcs.mcp.GUI;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The required class to start the GUI application
 *
 * @author Sean Seow
 */
public class GUIMain extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        //Set the file to Constants later
        Parent root = FXMLLoader.load(getClass().getResource("/uk/ac/aber/dcs/mcp/GUI/view/LoginView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("MCP Program");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}