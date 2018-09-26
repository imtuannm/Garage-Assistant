package garage.assistant.ui.listmotorbike;

import com.jfoenix.controls.JFXTextField;
import garage.assistant.alert.AlertMaker;
import garage.assistant.database.DatabaseHandler;
import garage.assistant.ui.listmotorbike.MotorbikeListController.Motorbike;
import garage.assistant.util.GarageAssistantUtil;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;

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
    @FXML
    private JFXTextField keyword;
    @FXML
    private TableColumn<Motorbike, String> feeCol;
    @FXML
    private TableColumn<Motorbike, String> fineCol;
    
    DatabaseHandler handler = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
        initCol();
        loadData();
    }    

    private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        producerCol.setCellValueFactory(new PropertyValueFactory<>("producer"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        feeCol.setCellValueFactory(new PropertyValueFactory<>("fee"));
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadData() {//push data from db to app
        list.clear();
        
        String qu = "SELECT * FROM MOTORBIKE";
        ResultSet rs = handler.excQuery(qu);
        
        try {
            while(rs.next()) {
                String id = rs.getString("idMotorbike");
                String producer = rs.getString("producer");
                String name = rs.getString("name");
                String color = rs.getString("color");
                String fee = String.valueOf(rs.getInt("baseFee"));
                String fine = String.valueOf(rs.getInt("finePercent"));
                String type = GarageAssistantUtil.categorizeVehicle(rs.getInt("type"));//shorted
                String status = GarageAssistantUtil.vehicleStatus(rs.getInt("status"));
                
                //create new object & add to list
                list.add(new Motorbike(id, producer, name, type, color, fee, fine, status));
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
            AlertMaker.showSimpleErrorMessage("No Vehicle selected", "Pls select a Vehicle for deletion.");
            return;
        }
        
        if ( DatabaseHandler.getInstance().isMotorbikeAlreadyIssued(selectedForDeletion) ) {//in use
            AlertMaker.showSimpleErrorMessage("Cant delete", "This Vehicle is already in use!");
            return;
        }
        
        //confirmation
        Alert altCfm = new Alert(Alert.AlertType.CONFIRMATION);
        altCfm.setTitle("Deleting Vehicle");
        altCfm.setHeaderText(null);
        altCfm.setContentText("Are you sure want to delete " + selectedForDeletion.getName() + "?");
        Optional<ButtonType> answer = altCfm.showAndWait();
        if (answer.get() == ButtonType.OK) {//OK
            Boolean res = DatabaseHandler.getInstance().deleteMotorbike(selectedForDeletion);
            if (res) {//success
                AlertMaker.showSimpleInforAlert("Vehicle deleted", selectedForDeletion.getName() + " was deleted!");
                list.remove(selectedForDeletion);//remove selected one from the memory
            } else {//fail
                AlertMaker.showSimpleErrorMessage("Failed", selectedForDeletion.getName() + " could not be deleted!");
            }
        } else {//cancel
            AlertMaker.showSimpleInforAlert("Cancelled", "Vehicle is not deleted");
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
        Motorbike selectedForMaintenance = tblView.getSelectionModel().getSelectedItem();
        
        if (selectedForMaintenance == null) {//invalid row
            AlertMaker.showSimpleErrorMessage("No Vehicle selected", "Pls select a Vehicle for Maintain.");
            return;
        } else if ( DatabaseHandler.getInstance().isMotorbikeAlreadyIssued(selectedForMaintenance) ) {//in use
            AlertMaker.showSimpleErrorMessage("Cant set", "This Vehicle is already in use!");
            return;
        }
        
        
        //confirmed
        Alert altCfm = new Alert(Alert.AlertType.CONFIRMATION);
        altCfm.setTitle("Vehicle Maintenance");
        altCfm.setHeaderText(null);
        altCfm.setContentText("Are you sure want to set status for " + selectedForMaintenance.getName() + "?");
        Optional<ButtonType> answer = altCfm.showAndWait();
        if (answer.get() == ButtonType.OK) {//OK
            Boolean res = DatabaseHandler.getInstance().maintainMotorbike(selectedForMaintenance);
            if (res) {//success
                AlertMaker.showSimpleInforAlert("Success", selectedForMaintenance.getName() + " is set!");
                handleRefresh(new ActionEvent());
            } else {//fail
                AlertMaker.showSimpleErrorMessage("Failed", selectedForMaintenance.getName() + " could not be set!");
            }
        } else {//cancel
            AlertMaker.showSimpleInforAlert("Cancelled", "Vehicle's status is not set!");
        }
    }

    @FXML
    private void handleSearchOperation(ActionEvent event) {
        list.clear();
        String search = keyword.getText();
        
        if (search == null) {//load all vehicle if user dont search
            handleRefresh(new ActionEvent());
        } else {
            String searchQuery = "SELECT * FROM MOTORBIKE WHERE idMotorbike LIKE '%" + search + "%'"
                                + " UNION SELECT * FROM MOTORBIKE WHERE producer LIKE '%" + search + "%'"
                                + " UNION SELECT * FROM MOTORBIKE WHERE name LIKE '%" + search + "%'"
                                + " UNION SELECT * FROM MOTORBIKE WHERE color LIKE '%" + search + "%'";                                
       
            System.out.println(searchQuery);//print debug

            ResultSet rs = handler.excQuery(searchQuery);
            try {
                while(rs.next()) {//get inf
                    String id = rs.getString("idMotorbike");
                    String producer = rs.getString("producer");
                    String name = rs.getString("name");
                    String color = rs.getString("color");
                    String fee = String.valueOf(rs.getInt("baseFee"));
                    String fine = String.valueOf(rs.getInt("finePercent"));
                    String type = GarageAssistantUtil.categorizeVehicle(rs.getInt("type"));//shorted
                    String status = GarageAssistantUtil.vehicleStatus(rs.getInt("status"));

                    //create a new object then add to list
                    list.add(new Motorbike(id, producer, name, type, color, fee, fine, status));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MotorbikeListController.class.getName()).log(Level.SEVERE, null, ex);
            }
            tblView.setItems(list); //set items to listview
        }        
    }

    //list specific
    public static class Motorbike {
        private final SimpleStringProperty id;
        private final SimpleStringProperty producer;
        private final SimpleStringProperty name;
        private final SimpleStringProperty type;
        private final SimpleStringProperty color;
        private final SimpleStringProperty fee;
        private final SimpleStringProperty fine;
        private final SimpleStringProperty status;
        
        //constructor
        Motorbike(String id, String producer, String name, String type, String color, String fee, String fine, String status) {
            this.id = new SimpleStringProperty(id);
            this.producer = new SimpleStringProperty(producer);
            this.name = new SimpleStringProperty(name);
            this.type = new SimpleStringProperty(type);
            this.color = new SimpleStringProperty(color);
            this.fee = new SimpleStringProperty(fee);
            this.fine = new SimpleStringProperty(fine);
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
        
        public String getFee() {
            return fee.get();
        }
        
        public String getFine() {
            return fine.get();
        }
        
        public String getType() {
            return type.get();
        }

        public String getStatus() {
            return status.get();
        }
    }
    
}