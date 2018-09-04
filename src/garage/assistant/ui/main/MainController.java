package garage.assistant.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.jfoenix.transitions.hamburger.*;
import garage.assistant.alert.AlertMaker;
import garage.assistant.database.DatabaseHandler;
import garage.assistant.util.GarageAssistantUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    private JFXTextField motorID;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private Text txtMemberName;
    @FXML
    private Text txtMemberEmail;
    @FXML
    private Text txtMemberMobile;
    @FXML
    private Text txtMotorbikeProducer;
    @FXML
    private Text txtMotorbikeName;
    @FXML
    private Text txtMotorbikeType;
    @FXML
    private Text txtMotorbikeColor;
    @FXML
    private Text txtIssueDate;
    @FXML
    private Text txtIssueNoDays;
    @FXML
    private Text txtIssueFine;
    @FXML
    private BorderPane rootBorderPane;
    @FXML
    private JFXButton btnRenew;
    @FXML
    private JFXButton btnSubmission;
    @FXML
    private HBox submissionDataContainer;
    
    Boolean isReadyForSubmission = false;
    DatabaseHandler databseHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databseHandler = DatabaseHandler.getInstance();
        initDrawer();
    }

    @FXML
    private void loadMotorbikeInfo(ActionEvent event) {
        String id = motorbikeIdInput.getText();
        String qr = "SELECT * FROM MOTORBIKE WHERE idMotorbike = '" + id + "'";
        Boolean flag = false;

        ResultSet rs = databseHandler.excQuery(qr);

        try {
            while (rs.next()) { //set info to textfield
                String mbProducer = rs.getString("producer");
                String mbName = rs.getString("name");
                int mbType = rs.getInt("type");
                Boolean mbStatus = rs.getBoolean("isAvail");

                motorbikeProducer.setText(mbProducer);
                motorbikeType.setText(setType(rs.getInt("type")));//shorted
                motorbikeName.setText(mbName);
                String stt = (mbStatus) ? "Available" : "NOT Available";
                motorbikeStatus.setText(stt);
                flag = true;
            }
            if (!flag) { //doesnt exist
                clrMotorbikeCached();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String setType(int mtbType) {//set a type for motorbike
        String str = null;
        switch (mtbType) {
            case 1:
                str = "Motorbike";
                break;
            case 2:
                str = "Car";
                break;
            case 3:
                str = "Self-Driving Car";
                break;
            default:
                str = "Not exist in db yet";
                break;
        }
        return str;
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {
        String id = memberIdInput.getText();
        String qr = "SELECT * FROM MEMBER WHERE idMember = '" + id + "'";
        Boolean flag = false;

        ResultSet rs = databseHandler.excQuery(qr);

        try {
            while (rs.next()) { //set info to textfields
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");

                memberName.setText(mName);
                memberMobile.setText(mMobile);

                flag = true;
            }
            if (!flag) { //doesnt exist
                clrMemberCached();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //clear old text while can not find informations
    void clrMotorbikeCached() {
        motorbikeName.setText("No such Motorbike found!");
        motorbikeProducer.setText("-");
        motorbikeType.setText("-");
        motorbikeStatus.setText("-");
    }

    //also clear old text
    void clrMemberCached() {
        memberName.setText("No such Member found!");
        memberMobile.setText("-");
    }

    @FXML
    private void loadIssueOperation(ActionEvent event) {
        String memID = memberIdInput.getText();
        String mtbID = motorbikeIdInput.getText();
        Boolean mtbStatus = false;

        //check if motor is ready for issue
        String chkStt = "SELECT * FROM MOTORBIKE WHERE idMotorbike = '" + mtbID + "'";;
        ResultSet rss = databseHandler.excQuery(chkStt);
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

        if (response.get() == ButtonType.OK) {//confirm
            if (!mtbStatus) {//not avaiable
                AlertMaker.showSimpleErrorMessage("Failed", "This motorbike is NOT available to issue!");
                return;
            }

            String strIssue = "INSERT INTO ISSUE(id_motorbike,id_member, renew_count) VALUES (+"
                    + "'" + mtbID + "',"
                    + "'" + memID + "',"
                    + "'" + 0 + "')";

            String strUpdStt = "UPDATE MOTORBIKE SET isAvail = false WHERE idMotorbike = '" + mtbID + "'";

            if (databseHandler.excAction(strIssue) && databseHandler.excAction(strUpdStt)) {
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
        clearEntries();
        isReadyForSubmission = false;

        try{
            String id = motorID.getText();
            //use a single query for better performance
            String qr = "SELECT ISSUE.id_motorbike, ISSUE.id_member, ISSUE.issueTime, ISSUE.renew_count,\n" +
                        "MEMBER.name AS mbName, MEMBER.mobile, MEMBER.email,\n" +
                        "MOTORBIKE.producer, MOTORBIKE.name AS mtName, MOTORBIKE.type, MOTORBIKE.color\n" +
                        "FROM ISSUE\n" +
                        "LEFT JOIN MEMBER\n" +
                        "ON ISSUE.id_member = MEMBER.idMember\n" +
                        "LEFT JOIN MOTORBIKE\n" +
                        "ON ISSUE.id_motorbike = MOTORBIKE.idMotorbike\n" +
                        "WHERE ISSUE.id_motorbike = '" + id + "'";
            ResultSet rs = databseHandler.excQuery(qr);
            
            if(rs.next()) {//exist
                //member inf
                txtMemberName.setText(rs.getString("mbName"));
                txtMemberEmail.setText(rs.getString("email"));
                txtMemberMobile.setText(rs.getString("mobile"));
                
                //motorbike inf
                txtMotorbikeProducer.setText(rs.getString("producer"));
                txtMotorbikeName.setText(rs.getString("mtName"));
                txtMotorbikeType.setText(setType(rs.getInt("type")));//shorted
                txtMotorbikeColor.setText(rs.getString("color"));
                
                //issue inf
                Timestamp issueTime = rs.getTimestamp("issueTime");
                Date dateOfIssue = new Date(issueTime.getTime());
                txtIssueDate.setText(dateOfIssue.toString());
                Long timeElapsed = System.currentTimeMillis() - issueTime.getTime();
                Long days = TimeUnit.DAYS.convert(timeElapsed, TimeUnit.MILLISECONDS) + 1;
                String daysElapsed = String.format("Used for %d day(s)", days);
                txtIssueNoDays.setText(daysElapsed);
                Float fine = GarageAssistantUtil.getFineAmount(days.intValue());
                if (fine > 0) {
                    DecimalFormat currencyFormat = new DecimalFormat("####,###,###.#");
                    txtIssueFine.setText("Fine: $" + currencyFormat.format(GarageAssistantUtil.getFineAmount(days.intValue())));
                    txtIssueFine.setFill(Color.web("#E452E4"));
                } else {
                    txtIssueFine.setText("No fine");
                }
                isReadyForSubmission = true;//everything is set
                //enable controls
                toggleControls(true);
                submissionDataContainer.setOpacity(1);
            } else {//not exist
                BoxBlur blur = new BoxBlur(2,2,2);//effect
                //inform user
                JFXDialogLayout dialogLayout = new JFXDialogLayout();
                JFXButton button = new JFXButton("Let me try again!");
                button.getStyleClass().add("dialog-button");
                JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);//content for dialogLayout
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)->{//insert button inside dialogLayout
                    dialog.close();
                });
                dialogLayout.setHeading(new Label("No such Motor exists in Issue record!"));
                dialogLayout.setActions(button);
                dialog.show();
                dialog.setOnDialogClosed((JFXDialogEvent event1) -> {//reset blur effect
                    rootBorderPane.setEffect(null);
                });
                rootBorderPane.setEffect(blur);
            }
        } catch(Exception e) {
             e.printStackTrace();
        }
    }

    @FXML
    private void loadSubmissionOperation(ActionEvent event) {
        if (!isReadyForSubmission) { //not ready 
            AlertMaker.showSimpleErrorMessage("Failed!", "Invalid Motorbike to submit.");
            return;
        }

        //make an alert box to confirm
        Alert altCfm = new Alert(Alert.AlertType.CONFIRMATION);
        altCfm.setTitle("Confirm");
        altCfm.setHeaderText(null);
        altCfm.setContentText("Are you sure want to return the motorbike?");
        Optional<ButtonType> response = altCfm.showAndWait();
        if (response.get() == ButtonType.OK) {//confirmed
            String id = motorID.getText();
            //1. remove the entry from the Issue table
            String actDel = "DELETE FROM ISSUE WHERE id_motorbike = '" + id + "'";
            //2. make the motor available in the database
            String actUpd = "UPDATE MOTORBIKE SET isAvail = true WHERE idMotorbike = '" + id + "'";
            if (databseHandler.excAction(actDel) && databseHandler.excAction(actUpd)) {//success
                AlertMaker.showSimpleInforAlert("Success!", "Motorbike has been submitted.");
                loadIssueInfo(null);// refresh
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
        if (!isReadyForSubmission) { //not ready 
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
        if (response.get() == ButtonType.OK) {//OK button
            //change issueTime & renew_count
            String actUpd = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE id_motorbike = '" + id + "'";

            if (databseHandler.excAction(actUpd)) {//success
                AlertMaker.showSimpleInforAlert("Success!", "Motorbike has been renewed.");
                loadIssueInfo(null); //refresh
            } else {//error
                AlertMaker.showSimpleErrorMessage("Failed!", "Renewal has been failed.");
            }
        } else {//cancel button
            AlertMaker.showSimpleInforAlert("Cancelled", "Renew Operation cancelled!");
        }
    }

    @FXML
    private void handleMenuClose(ActionEvent event) {
        ((Stage) rootPane.getScene().getWindow()).close();
    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        GarageAssistantUtil.loadWindow(getClass().getResource("/garage/assistant/ui/addmember/member_add.fxml"), "Add new Member", null);
    }

    @FXML
    private void handleMenuAddMotorbike(ActionEvent event) {
        GarageAssistantUtil.loadWindow(getClass().getResource("/garage/assistant/ui/addmotorbike/add_motorbike.fxml"), "Add new Motorbike", null);
    }

    @FXML
    private void handleMenuViewMembers(ActionEvent event) {
        GarageAssistantUtil.loadWindow(getClass().getResource("/garage/assistant/ui/listmember/member_list.fxml"), "All Member", null);
    }

    @FXML
    private void handleMenuViewMotorbikes(ActionEvent event) {
        GarageAssistantUtil.loadWindow(getClass().getResource("/garage/assistant/ui/listmotorbike/motorbike_list.fxml"), "All Motorbike", null);
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = ((Stage) rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());//toggle full screen & no full screen 
    }

    private void initDrawer() {
        try {
            VBox toolbar = FXMLLoader.load(getClass().getResource("/garage/assistant/ui/main/toolbar/toolbar.fxml"));
            drawer.setSidePane(toolbar); //call VBox toolbar
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        HamburgerSlideCloseTransition task =  new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1); //use for toggle icon
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            task.setRate(task.getRate() * -1); //toggle icon
            task.play(); //call the toolbar
            
            if(drawer.isClosed()) {
                drawer.open();
            } else {
                drawer.close();
            }
        });
    }

    private void clearEntries() {
        txtMemberName.setText("-");
        txtMemberEmail.setText("-");
        txtMemberMobile.setText("-");

        //motorbike inf
        txtMotorbikeProducer.setText("-");
        txtMotorbikeName.setText("-");
        txtMotorbikeType.setText("-");//shorted
        txtMotorbikeColor.setText("-");

        //issue inf
        txtIssueDate.setText("-");
        txtIssueNoDays.setText("-");
        txtIssueFine.setText("-");
        
        toggleControls(false);
        submissionDataContainer.setOpacity(0);//hide it
    }
    
    private void toggleControls(Boolean enableFlag) {
        if (enableFlag) {
            btnRenew.setDisable(false);
            btnSubmission.setDisable(false);
        } else {
            btnRenew.setDisable(true);
            btnSubmission.setDisable(true);
        }
    }

}