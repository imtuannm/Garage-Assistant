package garage.assistant.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.Statement;

public final class DatabaseHandler {
    private static DatabaseHandler handler = null;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/garagemanagement?useUnicode=true&characterEncoding=UTF-8";
    private static final String USR = "root";
    private static final String PWD = "toor";
    private static Connection conn = null;
    private static Statement stmt = null;
    
    /*
        PRIVATE constructor
        prevent crashes when invoked by many class
        no classes can create direct object of this database handler
        so as not to conflict database handler
    */
    private DatabaseHandler() {
        createConnection();
        
        setupMotorbikeTable();
        setupMemberTable();
    }
    
    //single object is shared across all the classes
    //call DatabaseHandler.getInstance() give objects dbHandler object
    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler; //reuse if already exist
    }
    
    void createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();//install driver
            conn = DriverManager.getConnection(DB_URL, USR, PWD);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Can't load database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    void setupMotorbikeTable() {
        String TABLE_NAME = "MOTORBIKE";
        try {
            stmt = conn.createStatement();
            
            DatabaseMetaData dbm = conn.getMetaData(); //access metadata of the table
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            
            if ( tables.next() ) {
                System.out.println("The table " + TABLE_NAME + " already exists.");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "{"
                        + "idMotorbike VARCHAR(9)primary key,\n"
                        + "producer VARCHAR (45),\n"
                        + "name VARCHAR (45),\n"
                        + "color VARCHAR (45),\n"
                        + "isAvail BOOLEAN deault true"
                        + "}" );
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "\nSetup Database");
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
            System.err.println(e.getMessage() + "\nSetup Database");
        } finally {
        }    
    }
    
    public ResultSet excQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        }
        catch (SQLException ex) {
            System.out.println("Exception at excQuery: dataHandler" + ex.getLocalizedMessage());
            return null;
        }
        finally {
        }
        return result; //different
    }

    //insert data, crt tables,...
    public boolean excAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery: dataHandler" + ex.getLocalizedMessage());
            return false;
        }
        finally {
        }
    }
    
}