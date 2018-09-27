/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package garage.assistant.ui.main.analyse.member;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author root
 */
public class AnalyseMemberController implements Initializable {

    @FXML
    private JFXComboBox<String> cbbMemberAnalyze;
    ObservableList<String> choices = FXCollections.observableArrayList("a","b");
    @FXML
    private JFXButton btnAnalyse;
    @FXML
    private JFXTextField txtNoTop;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cbbMemberAnalyze.getItems().addAll(choices); 
    }    

    @FXML
    private void handleAnalyseMembers(ActionEvent event) {
        String sw = cbbMemberAnalyze.getSelectionModel().getSelectedItem().toString();
        int noTop = Integer.parseInt(txtNoTop.getText());
        String qr = "SELECT TOP " + noTop +" DESC ISSUE.id_motorbike, ISSUE.id_member, ISSUE.issueTime, ISSUE.expectedReturnDay, \n" +
                    "MEMBER.name AS mbName, MEMBER.mobile, MEMBER.email,\n" +
                    "MOTORBIKE.producer, MOTORBIKE.name AS mtName, MOTORBIKE.type, MOTORBIKE.color, MOTORBIKE.status, MOTORBIKE.baseFee, MOTORBIKE.finePercent\n" +
                    "FROM ISSUE\n" +
                    "LEFT JOIN MEMBER\n" +
                    "ON ISSUE.id_member = MEMBER.idMember\n" +
                    "LEFT JOIN MOTORBIKE\n" +
                    "ON ISSUE.id_motorbike = MOTORBIKE.idMotorbike";
    }
    
    
}
