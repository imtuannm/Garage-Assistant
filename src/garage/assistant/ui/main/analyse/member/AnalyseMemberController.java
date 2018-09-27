/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package garage.assistant.ui.main.analyse.member;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import garage.assistant.database.DatabaseHandler;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 *
 * @author root
 */
public class AnalyseMemberController implements Initializable {

    @FXML
    private JFXComboBox<String> cbbMemberAnalyze;
    ObservableList<String> choices = FXCollections.observableArrayList("Top issued time");
    @FXML
    private JFXButton btnAnalyse;
    @FXML
    private JFXTextField txtNoTop;
    @FXML
    private ListView<String> lsvMembersAnalyze;
    
    ObservableList<String> list = FXCollections.observableArrayList();
    DatabaseHandler databaseHandler = null;
    int noTop = 0;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cbbMemberAnalyze.getItems().addAll(choices);
        databaseHandler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void handleAnalyseMembers(ActionEvent event) {
        list.clear();
        String sw = cbbMemberAnalyze.getSelectionModel().getSelectedItem().toString(); 
        noTop = Integer.parseInt(txtNoTop.getText());
        int count = 0;
        
        if (noTop < 1 || cbbMemberAnalyze.getSelectionModel().getSelectedItem().isEmpty() ) {
            System.out.println(noTop);
            return;
        } else if (sw.equals("Top issued time")) {
            String qr = "SELECT count(issue.id_member) as noOrders, member.idMember, member.name, member.mobile, member.email\n" +
                            "FROM ISSUE\n" +
                            "JOIN MEMBER\n" +
                            "ON ISSUE.id_member = MEMBER.idMember\n" +
                            "WHERE isSubmitted = '0'\n" +
                            "ORDER BY noOrders DESC\n" +
                            "LIMIT 0, " + noTop;

            System.out.println(qr);
            ResultSet rs = databaseHandler.excQuery(qr);

            try {
                while(rs.next()) {
                    count++;
                    //member inf
                    String mbrId = rs.getString("idMember");
                    String mbrName = rs.getString("name");
                    String mbrMobile = rs.getString("mobile");
                    String mbrEmail = rs.getString("email");
                    String number = rs.getString("noOrders");

                    //add to list
                    list.add("");
                    list.add("TOP " + count);
                    list.add("\t" + mbrName + " | " + mbrId);
                    list.add("Contact:");
                    list.add("\tMobile: "+ mbrMobile);
                    list.add("\tEmail: " + mbrEmail);
                    list.add("Total: " + number + " of issued times.");
                }
                lsvMembersAnalyze.setItems(list);//set all above to list view
            } catch (SQLException ex) {
                Logger.getLogger(AnalyseMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
