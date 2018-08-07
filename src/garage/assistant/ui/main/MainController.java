package garage.assistant.ui.main;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import garage.assistant.alert.AlertMaker;
import garage.assistant.database.DatabaseHandler;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController implements Initializable {
    @FXML
    private HBox motorbike_info;
    @FXML
    private HBox member_info;
    @FXML
    private TextField motorbikeIdInput;
    @FXML
    private Text motorbikeProducer;
    @FXML
    private Text motorbikeType;
    @FXML
    private Text motorbikeName;
    @FXML
    private Text motorbikeStatus;
    
    @FXML
    private JFXTextField memberIdInput;
    @FXML
    private Text memberName;
    @FXML
    private Text memberMobile;
    @FXML
    private StackPane rootPane;

    @FXML
    private ListView<String> lsvIssueData;
    @FXML
    private JFXTextField motorID;
    
    Boolean isReadyForSubmission = false;
    DatabaseHandler dbHandler;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //UI - set depth
        JFXDepthManager.setDepth(motorbike_info, 1); //0 - 5
        JFXDepthManager.setDepth(member_info, 1);
        
        dbHandler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void loadAddMember(ActionEvent event) {
        loadWindow("/garage/assistant/ui/addmember/member_add.fxml",
                "/garage/assistant/resources/member.png", "Add new Member");
    }

    @FXML
    private void loadAddMotorbike(ActionEvent event) {
        loadWindow("/garage/assistant/ui/addmotorbike/add_motorbike.fxml",
                "/garage/assistant/resources/mobtorbike.png", "Add new Motorbike");
    }

    @FXML
    private void loadMemberTable(ActionEvent event) {
        loadWindow("/garage/assistant/ui/listmember/member_list.fxml",
                "/garage/assistant/resources/member_list.png","All Member");
    }

    @FXML
    private void loadMotorbikeTable(ActionEvent event) {
        loadWindow("/garage/assistant/ui/listmotorbike/motorbike_list.fxml",
                "/garage/assistant/resources/motorbile_list.png","All Motorbike");
    }
    
    @FXML
    private void loadSettings(ActionEvent event) {
        loadWindow("/garage/assistant/settings/settings.fxml",
                "/garage/assistant/resources/setting.png","Settings");
    }
    
    //call another window in-app
    void loadWindow(String dirLct, String dirIcon, String title) {
        try {
            Parent parent  = FXMLLoader.load(getClass().getResource(dirLct));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            
            //set icon
            Image ico = new Image(dirIcon);
            stage.getIcons().add(ico);
            
            stage.show();
            
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadMotorbikeInfo(ActionEvent event) {
        String id = motorbikeIdInput.getText();
        String qr = "SELECT * FROM MOTORBIKE WHERE idMotorbike = '" + id + "'";
        Boolean flag = false;

        ResultSet rs = dbHandler.excQuery(qr);
        
        try {
            while(rs.next()) { //set info to textfield
                String mbProducer = rs.getString("producer");
                String mbName = rs.getString("name");
                int mbType = rs.getInt("type");
                Boolean mbStatus = rs.getBoolean("isAvail");
                
                motorbikeProducer.setText(mbProducer);
                
                switch (mbType) {//set type
                case 1:
                    motorbikeType.setText("[Motor]");
                    break;
                case 2:
                    motorbikeType.setText("[Car]");
                    break;
                case 3:
                    motorbikeType.setText("[Self-Driving Car]");
                    break;
                default:
                    motorbikeType.setText("null");
                    break;
                }
                
                motorbikeName.setText(mbName);
                String stt = (mbStatus) ? "Available" : "NOT Available";
                motorbikeStatus.setText(stt);
                flag = true;
            }
            if( !flag ) { //doesnt exist
                motorbikeName.setText("No such Motorbike found!");
                clrMotorbikeCached();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {
        String id = memberIdInput.getText();
        String qr = "SELECT * FROM MEMBER WHERE idMember = '" + id + "'";
        Boolean flag = false;
        
        ResultSet rs = dbHandler.excQuery(qr);

        try {
            while(rs.next()) { //set info to textfields
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");
                
                memberName.setText(mName);
                memberMobile.setText(mMobile);
                
                flag = true;
            }
            if( !flag ) { //doesnt exist
                memberName.setText("No such Member found!");
                clrMemberCached();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    //clear old text while can not find infomations
    void clrMotorbikeCached() {
        motorbikeProducer.setText("-");
        motorbikeType.setText("-");
        motorbikeStatus.setText("-");
    }
    
    //also clear old text
    void clrMemberCached() {
        memberMobile.setText("-");
    }

    @FXML
    private void loadIssueOperation(ActionEvent event) {
        String memID = memberIdInput.getText();
        String mtbID = motorbikeIdInput.getText();
        Boolean mtbStatus = false;
        
        //check if motor is ready for issue
        String chkStt = "SELECT * FROM MOTORBIKE WHERE idMotorbike = '" + mtbID + "'";;
        ResultSet rss = dbHandler.excQuery(chkStt);
        try {
            while (rss.next()) {
                mtbStatus = rss.getBoolean("isAvail");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //make an alert box to confirm
        Alert altCfm = new Alert(Alert.AlertType.CONFIRMATION);
        altCfm.setTitle("Confirm");
        altCfm.setHeaderText(null);
        altCfm.setContentText("Are you sure to issue [" + motorbikeName.getText()
                            + "] to [" + memberName.getText() + "]?");
        
        Optional<ButtonType> response = altCfm.showAndWait();

        if( response.get() == ButtonType.OK ) {//confirm
            String strIssue = "INSERT INTO ISSUE(id_motorbike,id_member, renew_count) VALUES (+"
                    + "'" + mtbID + "',"
                    + "'" + memID + "',"
                    + "'" + 0 + "')";
            
            String strUpdStt = "UPDATE MOTORBIKE SET isAvail = false WHERE idMotorbike = '" + mtbID + "'";
            
            if ( !mtbStatus ) {//not avaiable
                AlertMaker.showSimpleErrorMessage("Failed", "This motorbike is NOT available to issue!");
                return;
            }
            
            if(dbHandler.excAction(strIssue) && dbHandler.excAction(strUpdStt) ) {
                AlertMaker.showSimpleInforAlert("Success", "Issuing completed!");
            } else {//can not issue or update status
                AlertMaker.showSimpleErrorMessage("Failed", "Issue Operation can NOT be completed!");
            }
        } else {//cancel button
            AlertMaker.showSimpleInforAlert("Cancelled", "Issue Operation cancelled!");
        }
    }

    @FXML
    private void loadIssueInfo(ActionEvent event) {
        ObservableList<String> issueData = FXCollections.observableArrayList();
        
        isReadyForSubmission = false;
        
        String id = motorID.getText();
        String qr = "SELECT * FROM ISSUE WHERE id_motorbike = '" + id + "'";
        ResultSet rs = dbHandler.excQuery(qr);
        try {
            while(rs.next()) {
                String mtbID = id;
                String mmbID = rs.getString("id_member");
                Timestamp issTime = rs.getTimestamp("issueTime");
                int rnwCount = rs.getInt("renew_count");
                
                issueData.add("Issue time: " + issTime.toGMTString());
                issueData.add("Renew count: " + rnwCount);
                
                issueData.add("\nMotorbike information: ");
                qr = "SELECT * FROM MOTORBIKE WHERE idMotorbike = '" + mtbID + "'";
                ResultSet rst = dbHandler.excQuery(qr);
                while(rst.next()){
                    issueData.add("\tProducer: " + rst.getString("producer"));
                    issueData.add("\tName: " + rst.getString("name"));
                    issueData.add("\tColor: " + rst.getString("color"));
                }
                
                qr = "SELECT * FROM MEMBER WHERE idMember = '" + mmbID + "'";
                rst = dbHandler.excQuery(qr);
                issueData.add("\nMember information: ");
                while(rst.next()){
                    issueData.add("\tName: " + rst.getString("name"));
                    issueData.add("\tMobile: " + rst.getString("mobile"));
                    issueData.add("\tEmail: " + rst.getString("email"));
                }
                
                isReadyForSubmission = true;//everything is set
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        lsvIssueData.getItems().setAll(issueData);//set all these above into list view
    }

    @FXML
    private void loadSubmissionOperation(ActionEvent event) {
        if( !isReadyForSubmission ) { //not ready 
            AlertMaker.showSimpleErrorMessage("Failed!", "Invalid Motorbike to submit.");
            return;
        }
        
        //make an alert box to confirm
        Alert altCfm = new Alert(Alert.AlertType.CONFIRMATION);
        altCfm.setTitle("Confirm");
        altCfm.setHeaderText(null);
        altCfm.setContentText("Are you sure want to return the motorbike?");
        Optional<ButtonType> response = altCfm.showAndWait();
        if( response.get() == ButtonType.OK ) {//confirmed
            String id = motorID.getText();
            //1. remove the entry from the Issue table
            String actDel = "DELETE FROM ISSUE WHERE id_motorbike = '" + id + "'";
            //2. make the motor available in the database
            String actUpd = "UPDATE MOTORBIKE SET isAvail = true WHERE idMotorbike = '" + id + "'";
            if ( dbHandler.excAction(actDel) && dbHandler.excAction(actUpd) ) {//success
                AlertMaker.showSimpleInforAlert("Success!", "Motorbike has been submitted.");
            } else {//error
                AlertMaker.showSimpleErrorMessage("Failed!", "Submission has been failed.");
            }
        } else {//cancel button
            AlertMaker.showSimpleInforAlert("Cancelled", "Submission Operation cancelled!");
        }
    }

    //renew the issue time into current time
    @FXML
    private void loadRenewOperation(ActionEvent event) {
        if( !isReadyForSubmission ) { //not ready 
            AlertMaker.showSimpleErrorMessage("Failed!", "Invalid Motorbike to renew.");
            return;
        }
        
        String id = motorID.getText();
        
        //make an alert box to confirm
        Alert altCfm = new Alert(Alert.AlertType.CONFIRMATION);
        altCfm.setTitle("Confirm");
        altCfm.setHeaderText(null);
        altCfm.setContentText("Are you sure want to renew the motorbike?");
        Optional<ButtonType> response = altCfm.showAndWait();
        if( response.get() == ButtonType.OK ) {//OK button
            //change issueTime & renew_count
            String actUpd = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE id_motorbike = '" + id + "'";
            
            if ( dbHandler.excAction(actUpd) ) {//success
                AlertMaker.showSimpleInforAlert("Success!", "Motorbike has been renewed.");
                //System.out.println(actUpd);//print debug
            } else {//error
                AlertMaker.showSimpleErrorMessage("Failed!", "Renewal has been failed.");
            }
        } else {//cancel button
            AlertMaker.showSimpleInforAlert("Cancelled", "Renew Operation cancelled!");
        }
    }

    @FXML
    private void handleMenuClose(ActionEvent event) {
        ((Stage)rootPane.getScene().getWindow()).close();
    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        loadWindow("/garage/assistant/ui/addmember/member_add.fxml",
                    "/garage/assistant/resources/member.png", "Add new Member");
    }

    @FXML
    private void handleMenuAddMotorbike(ActionEvent event) {
        loadWindow("/garage/assistant/ui/addmotorbike/add_motorbike.fxml",
                    "/garage/assistant/resources/mobtorbike.png", "Add new Motorbike");
    }

    @FXML
    private void handleMenuViewMembers(ActionEvent event) {
        loadWindow("/garage/assistant/ui/listmember/member_list.fxml",
                    "/garage/assistant/resources/member_list.png","All Member");
    }

    @FXML
    private void handleMenuViewMotorbikes(ActionEvent event) {
        loadWindow("/garage/assistant/ui/listmotorbike/motorbike_list.fxml",
                    "/garage/assistant/resources/motorbile_list.png","All Motorbike");
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = ((Stage)rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());//toggle full screen & no full screen 
   }

}