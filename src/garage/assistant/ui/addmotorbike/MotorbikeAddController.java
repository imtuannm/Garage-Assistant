package garage.assistant.ui.addmotorbike;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import garage.assistant.alert.AlertMaker;
import garage.assistant.database.DatabaseHandler;
import garage.assistant.ui.listmotorbike.MotorbikeListController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import static garage.assistant.util.GarageAssistantUtil.VEHICLE_1;
import static garage.assistant.util.GarageAssistantUtil.VEHICLE_2;
import static garage.assistant.util.GarageAssistantUtil.VEHICLE_3;

public class MotorbikeAddController implements Initializable {

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
    @FXML
    private JFXTextField txtBaseFee;
    @FXML
    private JFXTextField txtFinePercent; 
    @FXML
    private JFXComboBox<String> cbbType;
    
    private boolean isInEditMode = Boolean.FALSE;
    DatabaseHandler databaseHandler = null;
    ObservableList<String> types = FXCollections.observableArrayList(VEHICLE_1, VEHICLE_2, VEHICLE_3);
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        checkData();
        new Thread(() -> {
            databaseHandler = DatabaseHandler.getInstance(); // <- caused the delay while opening the app
        }).start();
        cbbType.getItems().addAll(types);
    }    

    //push the data to the db
    @FXML
    private void actSave(ActionEvent event) {
        String id = txtId.getText();
        String producer = txtProducer.getText();
        String name = txtName.getText();
        int type = 0;
        
        String vie = cbbType.getSelectionModel().getSelectedItem().toString();
        if (vie.equals(VEHICLE_1))
            type = 1;
        else if (vie.equals(VEHICLE_2))
            type = 2;
        else if (vie.equals(VEHICLE_3))
            type = 3;
        
        String color = txtColor.getText();
        int baseFee = Integer.parseInt(txtBaseFee.getText());
        int finePercent = Integer.parseInt(txtFinePercent.getText());
        
        if (id.isEmpty() || producer.isEmpty() || name.isEmpty()
                || color.isEmpty() || baseFee < 0 || finePercent < 0 || cbbType.getSelectionModel().getSelectedItem().isEmpty() ) {
            
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
                + "'" + baseFee + "',"
                + "'" + finePercent + "',"
                + "" + "1" + "" //available whenever added
                + ")";
        
        System.out.println(qu); //print debug
        
        if( databaseHandler.excAction(qu) ) {
            AlertMaker.showSimpleInforAlert("Success", "New Vehicle added!");
        } else {
            AlertMaker.showSimpleErrorMessage("Failed", "Can not add Vehicle!");
        }
    }

    @FXML
    private void actCancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    
    //reuse in MotorbikeListController
    public void inflateUI(MotorbikeListController.Motorbike motorbike) {
        txtId.setText(motorbike.getId());
        txtProducer.setText(motorbike.getProducer());
        txtName.setText(motorbike.getName());
        cbbType.getItems().add(motorbike.getType());
        txtColor.setText(motorbike.getColor());
        txtBaseFee.setText(motorbike.getFee());
        txtFinePercent.setText(motorbike.getFine());
        
        txtId.setEditable(false);//cant edit the primary key
        isInEditMode = Boolean.TRUE;
    }
    
    private void handleEditOperation() {
    }
}