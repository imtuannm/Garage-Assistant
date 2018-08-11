package garage.assistant.ui.listmotorbike;

import garage.assistant.alert.AlertMaker;
import garage.assistant.database.DatabaseHandler;
import garage.assistant.ui.listmotorbike.MotorbikeListController.Motorbike;
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
import javafx.beans.property.SimpleBooleanProperty;
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
    private TableColumn<Motorbike, Boolean> availabilityCol;

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
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    private void loadData() {//push data from db to app
        DatabaseHandler handler = DatabaseHandler.getInstance();
        
        String qu = "SELECT * FROM MOTORBIKE";
        ResultSet rs = handler.excQuery(qu);
        
        try {
            while(rs.next()) {
                String mbId = rs.getString("idMotorbike");
                String mbProducer = rs.getString("producer");
                String mbNname = rs.getString("name");
                String mbColor = rs.getString("color");
                String mbType = setType(rs.getString("type"));//shorted
           
//                switch(Integer.parseInt(mbType)) {//require an Integer
//                    case 1:
//                        mbType = "Motorbike";
//                        break;
//                    case 2:
//                        mbType = "Car";
//                        break;
//                    case 3:
//                        mbType = "Self-driving Car";
//                        break;
//                    default:
//                        mbType = "Not exist in db yet";
//                        break;
//                }
                
                Boolean mbAvail = rs.getBoolean("isAvail");
                
                list.add(new Motorbike(mbId, mbProducer, mbNname, mbType, mbColor, mbAvail));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorbikeListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        tblView.setItems(list); //refresh right after delete
    }
    
    private static String setType(String mtbType) {//set a type for motorbike
        switch(Integer.parseInt(mtbType)) {//require an Integer
            case 1:
                mtbType = "Motorbike";
                break;
            case 2:
                mtbType = "Car";
                break;
            case 3:
                mtbType = "Self-Driving Car";
                break;
            default:
                mtbType = "Not exist in db yet";
                break;
        }
        return mtbType;
    }

    @FXML
    private void handleMotorbikeDeleteOption(ActionEvent event) {
        //fetch the selected row
        Motorbike selectedForDeletion = tblView.getSelectionModel().getSelectedItem();
        
        if ( selectedForDeletion == null ) {//invalid row
            AlertMaker.showSimpleErrorMessage("No Motorbike selected", "Pls select a motor for deletion.");
            return;
        }
        
        if ( DatabaseHandler.getInstance().isMotorbikeAlreadyIssued(selectedForDeletion) ) {
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
                AlertMaker.showSimpleInforAlert("Motorbike deleted", selectedForDeletion.getName() + "was deleted!");
                list.remove(selectedForDeletion);//remove selected one from the memory
            } else {//fail
                AlertMaker.showSimpleErrorMessage("Failed", selectedForDeletion.getName() + "could not be deleted!");
            }
        } else {//cancel
            AlertMaker.showSimpleInforAlert("Cancelled", "Motorbike is not deleted");
        }
        
    }

    @FXML
    private void handleMotorbikeEditOption(ActionEvent event) {
        Motorbike selectedForEdit = tblView.getSelectionModel().getSelectedItem();
        
        if ( selectedForEdit == null ) {//invalid row
            AlertMaker.showSimpleErrorMessage("No Motorbike selected", "Pls select a motor for edit.");
            return;
        }
        
        //TODO
    }
    
    public static class Motorbike {
        private final SimpleStringProperty id;
        private final SimpleStringProperty producer;
        private final SimpleStringProperty name;
        private final SimpleStringProperty type;
        private final SimpleStringProperty color;
        private final SimpleBooleanProperty availability;
        
        //constructor
        Motorbike(String id, String producer, String name, String type, String color, boolean availability) {
            this.id = new SimpleStringProperty(id);
            this.producer = new SimpleStringProperty(producer);
            this.name = new SimpleStringProperty(name);
            this.type = new SimpleStringProperty(type);
            this.color = new SimpleStringProperty(color);
            this.availability = new SimpleBooleanProperty(availability);
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

        public Boolean getAvailability() {
            return availability.get();
        }
    }
    
}
