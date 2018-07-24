package garage.assistant.ui.addmotorbike;

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

public class MotorbikeAddController implements Initializable {

    @FXML
    private JFXTextField txtId;
    @FXML
    private JFXTextField txtProducer;
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextField txtType;
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
        dbHandler = DatabaseHandler.getInstance();
        
        checkData();
    }    

    //push the data to the db
    @FXML
    private void actSave(ActionEvent event) {
        String id = txtId.getText();
        String producer = txtProducer.getText();
        String name = txtName.getText();
        int type = Integer.parseInt(txtType.getText());
        String color = txtColor.getText();
        
        if (id.isEmpty() || producer.isEmpty() || name.isEmpty()
                || color.isEmpty() || (txtType.getText().length() == 0) ) {
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields!");
            alert.showAndWait();
            return;
        }
        
        String qu = "INSERT INTO MOTORBIKE VALUES ("
                + "'" + id + "', "
                + "'" + producer + "',"
                + "'" + name + "',"
                + "'" + type + "' ,"
                + "'" + color + "',"
                + "" + "true" + "" //available whenever added
                + ")";
        
        System.out.println(qu); //print debug
        
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
                String mbName = rs.getString("name");
                System.out.println(mbName);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorbikeAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}