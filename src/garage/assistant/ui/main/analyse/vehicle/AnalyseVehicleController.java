/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package garage.assistant.ui.main.analyse.vehicle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import garage.assistant.database.DatabaseHandler;
import garage.assistant.ui.main.analyse.member.AnalyseMemberController;
import garage.assistant.util.GarageAssistantUtil;
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
public class AnalyseVehicleController implements Initializable {

    @FXML
    private JFXTextField txtNoTop;
    @FXML
    private JFXComboBox<String> cbbVehicleAnalyzeFunctions;
    @FXML
    private JFXButton btnAnalyse;
    @FXML
    private ListView<String> lsvVehiclesAnalyze;

    private final String F1 = "Top booked";
    private final String F2 = "Top Gained";
    private final String F3 = "Longest Days";
    
    ObservableList<String> choices = FXCollections.observableArrayList(F1, F2, F3);
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
        list.clear();
        String sw = cbbVehicleAnalyzeFunctions.getSelectionModel().getSelectedItem().toString(); 
        noTop = Integer.parseInt(txtNoTop.getText());
        int count = 0;
        String number = null;
        
        if (noTop < 1 || cbbVehicleAnalyzeFunctions.getSelectionModel().getSelectedItem().isEmpty() ) {
            System.out.println(noTop);
            return;
        } else if (sw.equals(F1)) {
            String qr = "SELECT count(ISSUE.id_motorbike) as noOrders, MOTORBIKE.idMotorbike, MOTORBIKE.name, MOTORBIKE.type, MOTORBIKE.finePercent, MOTORBIKE.baseFee, MOTORBIKE.producer, MOTORBIKE.color\n"
                        + "FROM ISSUE\n"
                        + "JOIN MOTORBIKE\n"
                        + "ON ISSUE.id_motorbike = MOTORBIKE.idMotorbike\n"
                        + "ORDER BY noOrders DESC\n"
                        + "LIMIT 0, " + noTop;

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
                    number = rs.getString("noOrders");

                    //add to list
                    list.add("");
                    list.add("TOP " + count);
                    list.add("\t" + mtbProducer + ", " + mtbName + " (" + mtbType + ")");
                    list.add("\t" + mtbId);
                    list.add("\tFee: " + mtbFee);
                    list.add("\tFine / day: " + mtbFinePer);
                }
                list.add("Total: " + number + " of be issued times.");
                lsvVehiclesAnalyze.setItems(list);//set all above to list view
            } catch (SQLException ex) {
                Logger.getLogger(AnalyseMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } else if (sw.equals(F2)) {//function 2
            String qr = "SELECT SUM(ISSUE.expectedReturnDay*MOTORBIKE.baseFee) As total, MOTORBIKE.finePercent, MOTORBIKE.baseFee, MOTORBIKE.idMotorbike, MOTORBIKE.name, MOTORBIKE.type, MOTORBIKE.finePercent, MOTORBIKE.baseFee, MOTORBIKE.producer, MOTORBIKE.color\n" +
                        "FROM ISSUE\n" +
                        "JOIN MOTORBIKE\n" +
                        "ON ISSUE.id_motorbike = MOTORBIKE.idMotorbike\n" +
                        "ORDER BY total DESC\n" +
                        "LIMIT 0," + noTop;

            String totalFee = null;
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
                    totalFee = rs.getString("total");
                    
                    //add to list
                    list.add("");
                    list.add("TOP " + count);
                    list.add("\t" + mtbProducer + ", " + mtbName + " (" + mtbType + ")");
                    list.add("\t" + mtbId);
                    list.add("\tFee: " + mtbFee);
                    list.add("\tFine / day: " + mtbFinePer);
                }
                list.add("Total FEE: $" + totalFee);
                lsvVehiclesAnalyze.setItems(list);//set all above to list view
            } catch (SQLException ex) {
                Logger.getLogger(AnalyseMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } else if (sw.equals(F3)) {//function 3
            String qr = "SELECT ISSUE.expectedReturnDay, MOTORBIKE.finePercent, MOTORBIKE.baseFee, MOTORBIKE.idMotorbike, MOTORBIKE.name, MOTORBIKE.type, MOTORBIKE.finePercent, MOTORBIKE.baseFee, MOTORBIKE.producer, MOTORBIKE.color,\n" +
                        "MEMBER.name AS mbName, MEMBER.email, MEMBER.mobile\n" +
                        "FROM ISSUE\n" +
                        "JOIN MOTORBIKE\n" +
                        "ON ISSUE.id_motorbike = MOTORBIKE.idMotorbike\n"+
                        "JOIN MEMBER\n" +
                        "ON ISSUE.id_member = MEMBER.idMember\n" +
                        "ORDER BY expectedReturnDay DESC\n" +
                        "LIMIT 0, "+ noTop;

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
                    
                    //add to list
                    list.add("");
                    list.add("TOP " + count + " [" +dayss+" days]");
                    list.add("\t" + mtbProducer + ", " + mtbName + " (" + mtbType + ")");
                    list.add("\t" + mtbId);
                    list.add("\tFee: " + mtbFee);
                    list.add("\tFine / day: " + mtbFinePer);
                    list.add("Member:");
                    list.add("\t" + mbrName);
                    list.add("\t" + email);
                    list.add("\t" + mobile);
                }
                lsvVehiclesAnalyze.setItems(list);//set all above to list view
            } catch (SQLException ex) {
                Logger.getLogger(AnalyseMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
