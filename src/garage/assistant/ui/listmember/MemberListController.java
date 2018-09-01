package garage.assistant.ui.listmember;

import garage.assistant.alert.AlertMaker;
import garage.assistant.database.DatabaseHandler;
import garage.assistant.ui.addmember.MemberAddController;
import garage.assistant.ui.listmotorbike.MotorbikeListController;
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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MemberListController implements Initializable {

    ObservableList<Member> list = FXCollections.observableArrayList();

    @FXML
    private TableView<Member> tblView;
    @FXML
    private TableColumn<Member, String> idCol; //from the Member, display String
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> mobileCol;
    @FXML
    private TableColumn<Member, String> emailCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }
    
    private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    
    private void loadData() { //load data from database
        list.clear();
        
        DatabaseHandler handler = DatabaseHandler.getInstance();
        
        String qu = "SELECT * FROM MEMBER";
        ResultSet rs = handler.excQuery(qu);
        
        try {
            while(rs.next()) { //retrieve data
                String mId = rs.getString("idMember");
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");
                String mEmail = rs.getString("email");
                
                list.add(new Member(mId, mName, mMobile, mEmail)); //push to list
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorbikeListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblView.setItems(list); //push data from list to table View
    }

    @FXML
    private void handleMemberEdit(ActionEvent event) {
        //fetch the selected motorbike
        Member selectedForEdit = tblView.getSelectionModel().getSelectedItem();
        
        if (selectedForEdit == null) {//invalid row
            AlertMaker.showSimpleErrorMessage("No Member selected", "Pls select a member for edit.");
            return;
        }
        
        try {
            //creating a loader object
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/garage/assistant/ui/addmember/member_add.fxml"));
            Parent parent = loader.load();//then load it
            
            //load data to edit
            MemberAddController controller = (MemberAddController)loader.getController();
            controller.inflateUI(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            GarageAssistantUtil.setStageIcon(stage);
            stage.show();
            
            stage.setOnCloseRequest((e) -> {//refresh right after edit (setOnCloseRequest)
                handleRefresh(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleMemberDelete(ActionEvent event) {
        //fetch the selected row
        MemberListController.Member selectedForDeletion = tblView.getSelectionModel().getSelectedItem();
        
        if ( selectedForDeletion == null ) {//invalid row
            AlertMaker.showSimpleErrorMessage("No Member selected", "Pls select a member for deletion.");
            return;
        }
        
        if ( DatabaseHandler.getInstance().isMemberHasAnyMotorbikes(selectedForDeletion) ) {//in use
            AlertMaker.showSimpleErrorMessage("Cant delete", "This Member is issuing a motorbike!");
            return;
        }
        
        //confirmation
        Alert altCfm = new Alert(Alert.AlertType.CONFIRMATION);
        altCfm.setTitle("Deleting Motorbike");
        altCfm.setHeaderText(null);
        altCfm.setContentText("Are you sure want to delete " + selectedForDeletion.getName() + "?");
        Optional<ButtonType> answer = altCfm.showAndWait();
        if (answer.get() == ButtonType.OK) {//OK
            Boolean res = DatabaseHandler.getInstance().deleteMember(selectedForDeletion);
            if (res) {//success
                AlertMaker.showSimpleInforAlert("Member deleted", selectedForDeletion.getName() + " was deleted!");
                //refresh
                list.remove(selectedForDeletion);//remove selected one from the memory
            } else {//fail
                AlertMaker.showSimpleErrorMessage("Failed", selectedForDeletion.getName() + " could not be deleted!");
            }
        } else {//cancel
            AlertMaker.showSimpleInforAlert("Cancelled", "Member is not deleted");
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    public static class Member {
        private final SimpleStringProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;
        
        //constructor
        public Member(String id, String name, String mobile, String email) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
        }

        public String getId() {
            return id.get();
        }
        
        public String getName() {
            return name.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }
    }    
    
}
