package garage.assistant.ui.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import garage.assistant.alert.AlertMaker;
import garage.assistant.database.DatabaseHandler;
import garage.assistant.ui.listmember.MemberListController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    @FXML
    private JFXTextField txtPassword;
    
    private boolean isInEditMode = Boolean.FALSE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler  = DatabaseHandler.getInstance();
    }    

    @FXML
    private void actSave(ActionEvent event) {
        String id = txtId.getText().replaceAll("[^\\w\\s]","");
        String name = txtName.getText().replaceAll("[^\\w\\s]","");
        String mobile = txtMobile.getText().replaceAll("[^\\w\\s]","");
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        
        Boolean flag = id.isEmpty() || name.isEmpty() || mobile.isEmpty() || email.isEmpty() || password.isEmpty();
        
        if ( flag ) {
            AlertMaker.showSimpleErrorMessage("Something is missed", "Pls fill in all fields");
            return;
        }
        
        if(isInEditMode) {
            handleUpdateMember();
            return;
        }
        
        String qu = "INSERT INTO MEMBER VALUES ("
                + "'" + id + "',"
                + "'" + name + "',"
                + "'" + mobile + "',"
                + "'" + email + "',"
                + "'" + password + "'"
                + ")";
        System.out.println(qu);//print debug
        
        if( handler.excAction(qu) ) {
            AlertMaker.showSimpleInforAlert("Success", "New Member added!");   
        } else {
            AlertMaker.showSimpleErrorMessage("Failed", "Can not add Member!");
        }
    }

    @FXML
    private void actCancel(ActionEvent event) {
        Stage stage = (Stage) rootPanez.getScene().getWindow();
        stage.close();
    }
    
    public void inflateUI(MemberListController.Member member) {
        txtId.setText(member.getId());
        txtName.setText(member.getName());
        txtMobile.setText(member.getMobile());
        txtEmail.setText(member.getEmail());
        txtPassword.setText(member.getPassword());
        
        txtId.setEditable(Boolean.FALSE);//cant edit the primary key
        isInEditMode = Boolean.TRUE;
    }

    private void handleUpdateMember() {
        MemberListController.Member member = new MemberListController.Member(txtId.getText(), txtName.getText(), txtMobile.getText(), txtEmail.getText(), txtPassword.getText());
        if ( handler.updateMember(member) ) {
            AlertMaker.showSimpleInforAlert("Success", "Member updated!");
        } else {
            AlertMaker.showSimpleErrorMessage("Failed", "Cant update member!");
        }
    }
    
}