package garage.assistant.ui.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import garage.assistant.database.DatabaseHandler;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MemberAddController implements Initializable {

    DatabaseHandler handler;
    
    @FXML
    private JFXTextField txtId;
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextField txtMobile;
    @FXML
    private JFXTextField txtEmail;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private AnchorPane rootPanez;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler  = new DatabaseHandler();
    }    

    @FXML
    private void actSave(ActionEvent event) {
        String id = txtId.getText();
        String name = txtName.getText();
        String mobile = txtMobile.getText();
        String email = txtEmail.getText();
        
        Boolean flag = id.isEmpty() || name.isEmpty() || mobile.isEmpty() || email.isEmpty();
        
        if ( flag ) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields!");
            alert.showAndWait();
            return;
        }
        
        String qu = "INSERT INTO MEMBER VALUES ("
                + "'" + id + "',"
                + "'" + name + "',"
                + "'" + mobile + "',"
                + "'" + email + "'"
                + ")";
        System.out.println(qu);//print debug
        
        if( handler.excAction(qu) ) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Success");
            alert.setContentText("New Member added!");
            alert.showAndWait();   
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Failed");
            alert.setContentText("Can not add Member!");
            alert.showAndWait();
        }
    }

    @FXML
    private void actCancel(ActionEvent event) {
        Stage stage = (Stage) rootPanez.getScene().getWindow();
        stage.close();
    }
    
}