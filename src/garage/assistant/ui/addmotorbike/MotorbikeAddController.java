package garage.assistant.ui.addmotorbike;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import garage.assistant.alert.AlertMaker;
import garage.assistant.database.DatabaseHandler;
import garage.assistant.ui.listmotorbike.MotorbikeListController;
import java.net.URL;
import java.util.ResourceBundle;
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

    private boolean isInEditMode = Boolean.FALSE;
    DatabaseHandler databaseHandler;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        databaseHandler = DatabaseHandler.getInstance();
//        checkData();
    }    

    //push the data to the db
    @FXML
    private void actSave(ActionEvent event) {
        String id = txtId.getText().replaceAll("[^\\w\\s]","");
        String producer = txtProducer.getText().replaceAll("[^\\w\\s]","");
        String name = txtName.getText().replaceAll("[^\\w\\s]","");
        int type = Integer.parseInt(txtType.getText().replaceAll("[^\\w\\s]",""));
        String color = txtColor.getText().replaceAll("[^\\w\\s]","");
        
        if (id.isEmpty() || producer.isEmpty() || name.isEmpty()
                || color.isEmpty() || (txtType.getText().length() == 0) ) {
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields!");
            alert.showAndWait();
            return;
        }
        
        if (isInEditMode) {
            handleEditOperation();
            return;
        }
        
        String qu = "INSERT INTO MOTORBIKE VALUES ("
                + "'" + type + "' ,"
                + "'" + id + "', "
                + "'" + producer + "',"
                + "'" + name + "',"
                + "'" + color + "',"
                + "" + "1" + "" //available whenever added
                + ")";
        
        System.out.println(qu); //print debug
        
        if( databaseHandler.excAction(qu) ) {
            AlertMaker.showSimpleInforAlert("Success", "New Motorbike added!");
        } else {
            AlertMaker.showSimpleErrorMessage("Failed", "Can not add Motorbike!");
        }
    }

    @FXML
    private void actCancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

//    private void checkData() {
//        String qu = "SELECT name FROM MOTORBIKE";
//        ResultSet rs = databaseHandler.excQuery(qu);
//        try {
//            while( rs.next() ) {
//                String mbName = rs.getString("name");
//                System.out.println(mbName);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(MotorbikeAddController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    //reuse in MotorbikeListController
    public void inflateUI(MotorbikeListController.Motorbike motorbike) {
        txtId.setText(motorbike.getId());
        txtProducer.setText(motorbike.getProducer());
        txtName.setText(motorbike.getName());
        txtType.setText(motorbike.getType());
        txtColor.setText(motorbike.getColor());
        
        txtId.setEditable(false);//cant edit the primary key
        isInEditMode = Boolean.TRUE;
    }
    
    private void handleEditOperation() {
    }
}