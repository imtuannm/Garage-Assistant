/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package garage.assistant.ui.main.toolbar;

import garage.assistant.util.GarageAssistantUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author root
 */
public class ToolbarController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void loadAddMember(ActionEvent event) {
        GarageAssistantUtil.loadWindow(getClass().getResource("/garage/assistant/ui/addmember/member_add.fxml"), "Add new Member", null);
    }

    @FXML
    private void loadAddMotorbike(ActionEvent event) {
        GarageAssistantUtil.loadWindow(getClass().getResource("/garage/assistant/ui/addmotorbike/add_motorbike.fxml"), "Add new Motorbike", null);
    }

    @FXML
    private void loadMemberTable(ActionEvent event) {
        GarageAssistantUtil.loadWindow(getClass().getResource("/garage/assistant/ui/listmember/member_list.fxml"), "All Member", null);
    }
    
    @FXML
    private void loadMotorbikeTable(ActionEvent event) {
        GarageAssistantUtil.loadWindow(getClass().getResource("/garage/assistant/ui/listmotorbike/motorbike_list.fxml"), "All Motorbike", null);
    }

    @FXML
    private void loadSettings(ActionEvent event) {
        GarageAssistantUtil.loadWindow(getClass().getResource("/garage/assistant/settings/settings.fxml"), "Settings", null);
    }
    
}