package garage.assistant.database;

import garage.assistant.ui.listmember.MemberListController;
import garage.assistant.ui.listmotorbike.MotorbikeListController.Motorbike;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import static garage.assistant.util.GarageAssistantUtil.STATUS_0;
import static garage.assistant.util.GarageAssistantUtil.STATUS_1;
import static garage.assistant.util.GarageAssistantUtil.STATUS_2;
import static garage.assistant.util.GarageAssistantUtil.STATUS_M1;
import static garage.assistant.util.GarageAssistantUtil.VEHICLE_1;
import static garage.assistant.util.GarageAssistantUtil.VEHICLE_2;
import static garage.assistant.util.GarageAssistantUtil.VEHICLE_3;

public final class DatabaseHandler {
    private static DatabaseHandler handler = null;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/garagemanagement?useUnicode=true&characterEncoding=UTF-8";
    private static final String USR = "root";
    private static final String PWD = "toor";
    private static Connection conn = null;
    private static Statement stmt = null;
    
//  PRIVATE constructor
//  prevent crashes when invoked by many class
//  no classes can create direct object of this database handler
//  so as not to conflict database handler
    private DatabaseHandler() {
        createConnection();      
        
//        setupMotorbikeTable();
//        setupMemberTable();
//        setupIssueTable();
    }
    
//  share a single db object across all the classes
//  call DatabaseHandler.getInstance() give objects dbHandler object
    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler; //reuse if already existed
    }
    
    void createConnection() { //create the connection between app & database using JDBC
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();//install driver
            conn = DriverManager.getConnection(DB_URL, USR, PWD);
        } catch (Exception e) {//another app is running
            JOptionPane.showMessageDialog(null, "Can't load database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);//close the newer app
        }
    }
    
    void setupMotorbikeTable() {
        String TABLE_NAME = "MOTORBIKE";
        try {
            stmt = conn.createStatement();
            
            DatabaseMetaData dbm = conn.getMetaData(); //access metadata of the table
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            
            if ( tables.next() ) { //already exist
                System.out.println("The table " + TABLE_NAME + " already exists.");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "{"
                        + "idMotorbike VARCHAR(9)primary key,\n"
                        + "producer VARCHAR (45),\n"
                        + "name VARCHAR (45),\n"
                        + "type INT (1),\n"
                        + "color VARCHAR (45),\n"
                        + "isAvail BOOLEAN deault true"
                        + "}" );
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "\nSetup MOTORBIKE Database");
        } finally {
        }
    }
    
    private void setupMemberTable() {
        String TABLE_NAME = "MEMBER";
        try {
            stmt = conn.createStatement();
            
            DatabaseMetaData dbm = conn.getMetaData(); //access metadata of the table
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            
            if ( tables.next() ) {
                System.out.println("The table " + TABLE_NAME + " already exists.");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "{"
                        + "idMember VARCHAR(45)primary key,\n"
                        + "name VARCHAR (100),\n"
                        + "mobile VARCHAR (13),\n"
                        + "email VARCHAR (45)"
                        + "}" );
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "\nSetup MEMBER Database");
        } finally {
        }    
    }
    
    void setupIssueTable() {
        String TABLE_NAME = "ISSUE";
        try {
            stmt = conn.createStatement();
            
            DatabaseMetaData dbm = conn.getMetaData(); //access metadata of the table
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            
            if ( tables.next() ) {
                System.out.println("The table " + TABLE_NAME + " already exists.");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "{"
                        + "id_motorbike VARCHAR(9) primary key,\n"
                        + "id_member VARCHAR(9) primary key,\n"
                        + "issueTime timestamp default CURRENT_TIMESTAMP primary key,\n"
                        + "renew_count integer (11) default 0,\n"
                        + "deposit DOUBLE default 500000,\n"
                        + "FOREIGN KEY (id_Motorbike) REFERENCES MOTORBIKE (idMotorbike),\n"
                        + "FOREIGN KEY (id_Member) REFERENCES MEMBER (idMember)"
                        + "}" );
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "\nSetup ISSUE Database");
        } finally {
        }    
    }
    
    //shorting out the execute query segment
    //SELECT, COUNT...
    public ResultSet excQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execute Query: databaseHandler" + ex.getLocalizedMessage());
            return null;
        } finally {
        }
        return result; //different
    }

    //change the tables
    //INSERT, DELETE, UPDATE (, DROP, ALTER)...
    public boolean excAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery: databaseHandler" + ex.getLocalizedMessage());
            return false;
        } finally {
        }
    }
    
    public boolean deleteMotorbike(Motorbike motorbike) {
        try {
            String delStmt = "DELETE FROM MOTORBIKE WHERE idMotorbike = ?";
            PreparedStatement stmt = conn.prepareStatement(delStmt);
            stmt.setString(1, motorbike.getId());
            int res = stmt.executeUpdate();//insert, update, delete
            if (res == 1) {//no exception if res == 0
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;  
    }
    
    public boolean maintainMotorbike(Motorbike motorbike) {
        PreparedStatement stmt = null;
        try {                 
            String delStmt = "UPDATE MOTORBIKE SET status = status*(-1) WHERE idMotorbike = ?";
            stmt = conn.prepareStatement(delStmt);
            stmt.setString(1, motorbike.getId());
            int res = stmt.executeUpdate();//insert, update, delete
            if (res == 1) {//no exception if res == 0
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;  
    }
    
    public boolean isMotorbikeAlreadyIssued(Motorbike motorbike) {
        try {
            String chkStmt = "SELECT COUNT(*) FROM ISSUE WHERE id_motorbike = ?";
            PreparedStatement stmt = conn.prepareStatement(chkStmt);
            stmt.setString(1, motorbike.getId());//first one -> 1
            ResultSet rs = stmt.executeQuery();
            if ( rs.next() ) {
                int count = rs.getInt(1);
                return ( count > 0 );
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean isMemberHasAnyMotorbikes(MemberListController.Member member) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE id_member = ?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            
            stmt.setString(1, member.getId());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updateMotorbike(Motorbike motorbike) {
        try {
            String strUpd = "UPDATE MOTORBIKE SET producer = ?, name = ?, type = ?, color = ? WHERE idMotorbike = ?";
            PreparedStatement stmt = conn.prepareStatement(strUpd);
            
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updateMember(MemberListController.Member member) {
        try {
            String strUpd = "UPDATE MEMBER SET name = ?, mobile = ?, email = ?, password = ? WHERE idMember = ?";
            PreparedStatement stmt = conn.prepareStatement(strUpd);
            
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getMobile());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getPassword());
            stmt.setString(5, member.getId());
            
            int res = stmt.executeUpdate();
            
            return (res > 0); //check if succcessful or not
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Boolean deleteMember(MemberListController.Member member) {
        try {
            String delStmt = "DELETE FROM MEMBER WHERE idMember = ?";
            PreparedStatement stmt = conn.prepareStatement(delStmt);
            
            stmt.setString(1, member.getId());
            
            int res = stmt.executeUpdate();//insert, update, delete
            if (res == 1) {//no exception if res == 0
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false; 
    }
    
    public ObservableList<PieChart.Data> getMotorbikeStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        
        try {
            //fetch data
            String strMan = "SELECT COUNT(*) FROM MOTORBIKE WHERE status = -1";
            String strNotAvail = "SELECT COUNT(*) FROM MOTORBIKE WHERE status = 0";
            String strAvail = "SELECT COUNT(*) FROM MOTORBIKE WHERE status = 1";
            String strBooked = "SELECT COUNT(*) FROM MOTORBIKE WHERE status = 2";
            
            //push to data ObservableList
            ResultSet rs = excQuery(strMan);
            if(rs.next()) {
                int uM = rs.getInt(1);
                data.add(new PieChart.Data(STATUS_M1 + " (" + uM + ")", uM));
            }
            
            rs = excQuery(strNotAvail);
            if(rs.next()) {
                int notAvail = rs.getInt(1);
                data.add(new PieChart.Data(STATUS_0 + " (" + notAvail + ")", notAvail));
            }
            
            rs = excQuery(strBooked);
            if(rs.next()) {
                int booked = rs.getInt(1);
                data.add(new PieChart.Data(STATUS_2 + " (" + booked + ")", booked));
            }
            
            rs = excQuery(strAvail);
            if(rs.next()) {
                int avail = rs.getInt(1);
                data.add(new PieChart.Data(STATUS_1 + " (" + avail + ")", avail));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }     
        
        return data;
    }
    
    public ObservableList<PieChart.Data> getMotorbikeTypes() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            //fetch data
            String strMtb = "SELECT COUNT(*) FROM MOTORBIKE WHERE type = 1";
            String strCar = "SELECT COUNT(*) FROM MOTORBIKE WHERE type = 2";
            String strSD = "SELECT COUNT(*) FROM MOTORBIKE WHERE type = 3";
            
            //push to data ObservableList
            ResultSet rs = excQuery(strMtb);
            if(rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data(VEHICLE_1 + " (" + count + ")", count));
            }
            
            rs = excQuery(strCar);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data(VEHICLE_2 + " (" + count + ")", count));
            }
            
            rs = excQuery(strSD);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data(VEHICLE_3 + " (" + count + ")", count));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    }
}