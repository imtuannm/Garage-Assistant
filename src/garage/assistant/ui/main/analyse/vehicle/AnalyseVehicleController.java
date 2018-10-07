/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package garage.assistant.ui.main.analyse.vehicle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import garage.assistant.alert.AlertMaker;
import garage.assistant.database.DatabaseHandler;
import garage.assistant.ui.main.analyse.member.AnalyseMemberController;
import garage.assistant.util.GarageAssistantUtil;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
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
public class AnalyseVehicleController implements Initializable {

    @FXML
    private JFXTextField txtNoTop;
    @FXML
    private JFXComboBox<String> cbbVehicleAnalyzeFunctions;
    @FXML
    private JFXButton btnAnalyse;
    @FXML
    private ListView<String> lsvVehiclesAnalyze;

    private final String F_LIFETIME_REVIEW = "Lifetime review";
    private final String F_LONGEST = "TOP Longest days";
    
    ObservableList<String> choices = FXCollections.observableArrayList(F_LIFETIME_REVIEW, F_LONGEST);
    ObservableList<String> list = FXCollections.observableArrayList();
    DatabaseHandler databaseHandler = null;
    int noTop = 0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cbbVehicleAnalyzeFunctions.getItems().addAll(choices);
        databaseHandler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void handleAnalyseVehicles(ActionEvent event) {
        list.clear();//empty the list if exsited
        String sw = cbbVehicleAnalyzeFunctions.getSelectionModel().getSelectedItem().toString(); 
        int count = 0;
        String number = null;
        
        if ( cbbVehicleAnalyzeFunctions.getSelectionModel().getSelectedItem().isEmpty() ) {
            System.out.println(noTop);
            return;
        } else if (sw.equals(F_LIFETIME_REVIEW)) {
            String qr = "SELECT count(ISSUE.id_motorbike) as noOrders\n"
                        + "FROM ISSUE\n";

            System.out.println(qr);
            ResultSet rs = databaseHandler.excQuery(qr);
            
            list.add("\tLIFETIME REVIEW");
            
            try {
                while(rs.next()) {
                    number = rs.getString("noOrders");
                    list.add("Total BOOKED: " + number);
                }
                lsvVehiclesAnalyze.setItems(list);//set all above to list view
            } catch (SQLException ex) {
                Logger.getLogger(AnalyseMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            qr = "SELECT SUM(ISSUE.expectedReturnDay*MOTORBIKE.baseFee) AS total, MOTORBIKE.finePercent, MOTORBIKE.baseFee, MOTORBIKE.idMotorbike, MOTORBIKE.name, MOTORBIKE.type, MOTORBIKE.finePercent, MOTORBIKE.baseFee, MOTORBIKE.producer, MOTORBIKE.color\n" +
                        "FROM ISSUE\n" +
                        "JOIN MOTORBIKE\n" +
                        "ON ISSUE.id_motorbike = MOTORBIKE.idMotorbike\n";

            System.out.println(qr);
            rs = databaseHandler.excQuery(qr);

            try {
                while(rs.next()) {
                    int totalFee = rs.getInt("total");
                    
                    if (totalFee < 0)
                        totalFee *= -1;
                    
                    //add to list
                    list.add("Total INCOME: $" + totalFee);
                }
                lsvVehiclesAnalyze.setItems(list);//set all above to list view
            } catch (SQLException ex) {
                Logger.getLogger(AnalyseMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } else if (sw.equals(F_LONGEST)) {//function 3
            try {
                noTop = Integer.parseInt(txtNoTop.getText().replaceAll("[^\\w\\s]",""));
            } catch (NumberFormatException ex) {
                AlertMaker.showSimpleErrorMessage("Invalid Input", "Check your typing again!");
                return;
            }

            String qr = "SELECT ISSUE.expectedReturnDay, MOTORBIKE.finePercent, MOTORBIKE.baseFee, MOTORBIKE.idMotorbike, MOTORBIKE.name, MOTORBIKE.type, MOTORBIKE.finePercent, MOTORBIKE.baseFee, MOTORBIKE.producer, MOTORBIKE.color,\n" +
                        "MEMBER.name AS mbName, MEMBER.email, MEMBER.mobile\n" +
                        "FROM ISSUE\n" +
                        "JOIN MOTORBIKE\n" +
                        "ON ISSUE.id_motorbike = MOTORBIKE.idMotorbike\n"+
                        "JOIN MEMBER\n" +
                        "ON ISSUE.id_member = MEMBER.idMember\n" +
                        "ORDER BY expectedReturnDay DESC\n" +
                        "LIMIT "+ noTop;

            System.out.println(qr);
            ResultSet rs = databaseHandler.excQuery(qr);

            try {
                while(rs.next()) {
                    count++;
                    //vehicle inf
                    String mtbId = rs.getString("idMotorbike");
                    String mtbProducer = rs.getString("producer");
                    String mtbName = rs.getString("name");
                    String mtbType = GarageAssistantUtil.categorizeVehicle(rs.getInt("type"));
                    int mtbFee = rs.getInt("baseFee");
                    int mtbFinePer = rs.getInt("finePercent");
                    
                    //member inf
                    String mbrName = rs.getString("mbName");
                    String email =rs.getString("email");
                    String mobile = rs.getString("mobile");
                    
                    int dayss = rs.getInt("expectedReturnDay");
                    if (dayss > 0) {
                        //add to list
                        list.add("TOP " + count + " [" +dayss+" days]");
                        list.add("\t" + mtbId + " | " + mtbProducer + ", " + mtbName + " [" + mtbType + "]");
                        list.add("\tFee: $" + mtbFee);
                        list.add("\tFine / day: " + mtbFinePer + "%");
                        list.add("Member:");
                        list.add("\t" + mbrName);
                        list.add("\t" + email);
                        list.add("\t" + mobile);
                        list.add("");
                    }
                }
                lsvVehiclesAnalyze.setItems(list);//set all above to list view
            } catch (SQLException ex) {
                Logger.getLogger(AnalyseMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
