package garage.assistant.ui.listmotorbike;

import garage.assistant.database.DatabaseHandler;
import garage.assistant.ui.listmotorbike.MotorbikeListController.Motorbike;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    private void loadData() {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        
        String qu = "SELECT * FROM MOTORBIKE";
        ResultSet rs = handler.excQuery(qu);
        
        try {
            while(rs.next()) {
                String idx = rs.getString("idMotorbike");
                String producerx = rs.getString("producer");
                String namex = rs.getString("name");
                String colorx = rs.getString("color");
                Boolean avail = rs.getBoolean("isAvail");
                
                list.add(new Motorbike(idx, producerx, namex, colorx, avail));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorbikeListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        tblView.getItems().setAll(list); 
    }
    
    public static class Motorbike {
        private final SimpleStringProperty id;
        private final SimpleStringProperty producer;
        private final SimpleStringProperty name;
        private final SimpleStringProperty color;
        private final SimpleBooleanProperty availability;
        
        //constructor
        Motorbike(String id, String producer, String name, String color, boolean availability) {
            this.id = new SimpleStringProperty(id);
            this.producer = new SimpleStringProperty(producer);
            this.name = new SimpleStringProperty(name);
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

        public Boolean getAvailability() {
            return availability.get();
        }
    }
    
}
