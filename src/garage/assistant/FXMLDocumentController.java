package garage.assistant;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import garage.assistant.database.DatabaseHandler;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {

    @FXML
    private JFXTextField txtId;
    @FXML
    private JFXTextField txtProducer;
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextField txtColor;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private AnchorPane rootPane;
    
    DatabaseHandler dbHandler;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dbHandler = new DatabaseHandler();
        
        checkData();
    }    

    //push the data to the db
    @FXML
    private void actSave(ActionEvent event) {
        String id = txtId.getText();
        String producer = txtProducer.getText();
        String name = txtName.getText();
        String color = txtColor.getText();
        
        if (id.isEmpty() || producer.isEmpty() || name.isEmpty() || color.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields!");
            alert.showAndWait();
            return;
        }
        
        String qu = "INSERT INTO MOTORBIKE VALUES ("
                + "'" + id + "',"
                + "'" + producer + "',"
                + "'" + name + "',"
                + "'" + color + "',"
                + "" + "true" + "" //available whenever added
                + ")";
        System.out.println(qu);
        
        if( dbHandler.excAction(qu) ) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Success");
            alert.setContentText("New Motorbike added!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Failed");
            alert.setContentText("Can not add Motorbike!");
            alert.showAndWait();
        }
    }

    @FXML
    private void actCancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void checkData() {
        String qu = "SELECT name FROM MOTORBIKE";
        ResultSet rs = dbHandler.excQuery(qu);
        try {
            while( rs.next() ) {
                String namez = rs.getString("name");
                System.out.println(namez);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}