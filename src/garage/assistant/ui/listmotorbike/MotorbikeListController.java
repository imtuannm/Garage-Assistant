package garage.assistant.ui.listmotorbike;

import garage.assistant.alert.AlertMaker;
import garage.assistant.database.DatabaseHandler;
import garage.assistant.ui.addmotorbike.MotorbikeAddController;
import garage.assistant.ui.listmotorbike.MotorbikeListController.Motorbike;
import garage.assistant.ui.main.MainController;
import garage.assistant.util.GarageAssistantUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MotorbikeListController implements Initializable {

    ObservableList<Motorbike> list = FXCollections.observableArrayList();
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Motorbike> tblView;
    @FXML
    private TableColumn<Motorbike, String> idCol; //from the Motorbike, display String
    @FXML
    private TableColumn<Motorbike, String> producerCol;
    @FXML
    private TableColumn<Motorbike, String> nameCol;
    @FXML
    private TableColumn<Motorbike, String> typeCol;
    @FXML
    private TableColumn<Motorbike, String> colorCol;
    @FXML
    private TableColumn<Motorbike, String> statusCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }    

    private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        producerCol.setCellValueFactory(new PropertyValueFactory<>("producer"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadData() {//push data from db to app
        list.clear();
        
        DatabaseHandler handler = DatabaseHandler.getInstance();
        
        String qu = "SELECT * FROM MOTORBIKE";
        ResultSet rs = handler.excQuery(qu);
        
        try {
            while(rs.next()) {
                String mbId = rs.getString("idMotorbike");
                String mbProducer = rs.getString("producer");
                String mbNname = rs.getString("name");
                String mbColor = rs.getString("color");
                String mbType = GarageAssistantUtil.categorizeVehicle(rs.getInt("type"));//shorted
                String status = GarageAssistantUtil.vehicleStatus(rs.getInt("status"));
                
                list.add(new Motorbike(mbId, mbProducer, mbNname, mbType, mbColor, status));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorbikeListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblView.setItems(list); //refresh right after delete
    }

    @FXML
    private void handleMotorbikeDeleteOption(ActionEvent event) {
        //fetch the selected row
        Motorbike selectedForDeletion = tblView.getSelectionModel().getSelectedItem();
        
        if ( selectedForDeletion == null ) {//invalid row
            AlertMaker.showSimpleErrorMessage("No Motorbike selected", "Pls select a motor for deletion.");
            return;
        }
        
        if ( DatabaseHandler.getInstance().isMotorbikeAlreadyIssued(selectedForDeletion) ) {//in use
            AlertMaker.showSimpleErrorMessage("Cant delete", "This Motorbike is already in use!");
            return;
        }
        
        //confirmation
        Alert altCfm = new Alert(Alert.AlertType.CONFIRMATION);
        altCfm.setTitle("Deleting Motorbike");
        altCfm.setHeaderText(null);
        altCfm.setContentText("Are you sure want to delete " + selectedForDeletion.getName() + "?");
        Optional<ButtonType> answer = altCfm.showAndWait();
        if (answer.get() == ButtonType.OK) {//OK
            Boolean res = DatabaseHandler.getInstance().deleteMotorbike(selectedForDeletion);
            if (res) {//success
                AlertMaker.showSimpleInforAlert("Motorbike deleted", selectedForDeletion.getName() + " was deleted!");
                list.remove(selectedForDeletion);//remove selected one from the memory
            } else {//fail
                AlertMaker.showSimpleErrorMessage("Failed", selectedForDeletion.getName() + " could not be deleted!");
            }
        } else {//cancel
            AlertMaker.showSimpleInforAlert("Cancelled", "Motorbike is not deleted");
        }
    }

    //TODO
//    @FXML
//    private void handleMotorbikeEditOption(ActionEvent event) {
//        //fetch the selected motorbike
//        Motorbike selectedForEdit = tblView.getSelectionModel().getSelectedItem();
//        
//        if (selectedForEdit == null) {//invalid row
//            AlertMaker.showSimpleErrorMessage("No Motorbike selected", "Pls select a motor for edit.");
//            return;
//        }
//        
//        try {
//            //creating a loader object
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/garage/assistant/ui/addmotorbike/add_motorbike.fxml"));
//            Parent parent = loader.load();//then load it
//            
////          need to check
//            MotorbikeAddController controller = (MotorbikeAddController)loader.getController();
//            controller.inflateUI(selectedForEdit);
//            
//            Stage stage = new Stage(StageStyle.DECORATED);
//            stage.setTitle("Edit Motorbike");
//            stage.setScene(new Scene(parent));
//            GarageAssistantUtil.setStageIcon(stage);
//            stage.show();
//            
//            stage.setOnCloseRequest((e) -> {//refresh after edit (setOnCloseRequest)
//                handleRefresh(new ActionEvent());
//            });
//        } catch (IOException ex) {
//            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleMotorbikeMaintainOption(ActionEvent event) {
        //fetch the selected motorbike
        Motorbike selectedForMaintain = tblView.getSelectionModel().getSelectedItem();
        
        if (selectedForMaintain == null) {//invalid row
            AlertMaker.showSimpleErrorMessage("No Motorbike selected", "Pls select a motor for Maintain.");
            return;
        } else if ( DatabaseHandler.getInstance().isMotorbikeAlreadyIssued(selectedForMaintain) ) {//in use
            AlertMaker.showSimpleErrorMessage("Cant set", "This Motorbike is already in use!");
            return;
        }
        
        Alert altCfm = new Alert(Alert.AlertType.CONFIRMATION);
        altCfm.setTitle("Motorbike Maintenance");
        altCfm.setHeaderText(null);
        altCfm.setContentText("Are you sure want to set status for " + selectedForMaintain.getName() + "?");
        Optional<ButtonType> answer = altCfm.showAndWait();
        if (answer.get() == ButtonType.OK) {//OK
            Boolean res = DatabaseHandler.getInstance().maintainMotorbike(selectedForMaintain);
            if (res) {//success
                AlertMaker.showSimpleInforAlert("Success", selectedForMaintain.getName() + " is set!");
                handleRefresh(new ActionEvent());
            } else {//fail
                AlertMaker.showSimpleErrorMessage("Failed", selectedForMaintain.getName() + " could not be set!");
            }
        } else {//cancel
            AlertMaker.showSimpleInforAlert("Cancelled", "Motorbike is not set!");
        }
    }
    
    //list specific
    public static class Motorbike {
        private final SimpleStringProperty id;
        private final SimpleStringProperty producer;
        private final SimpleStringProperty name;
        private final SimpleStringProperty type;
        private final SimpleStringProperty color;
        private final SimpleStringProperty status;
        
        //constructor
        Motorbike(String id, String producer, String name, String type, String color, String status) {
            this.id = new SimpleStringProperty(id);
            this.producer = new SimpleStringProperty(producer);
            this.name = new SimpleStringProperty(name);
            this.type = new SimpleStringProperty(type);
            this.color = new SimpleStringProperty(color);
            this.status = new SimpleStringProperty(status);
        }

        public String getId() {
            return id.get();
        }

        public String getProducer() {
            return producer.get();
        }

        public String getName() {
            return name.get();
        }

        public String getColor() {
            return color.get();
        }
        
        public String getType() {
            return type.get();
        }

        public String getStatus() {
            return status.get();
        }
    }
    
}