package garage.assistant.settings;

import com.google.gson.Gson;
import garage.assistant.alert.AlertMaker;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;

public class Preferences {
    //default values
    public static final String CONFIG_FILE = "config.txt";
    int nDaysWithoutFine;
    float finePerDay;
    String username;
    String password;
    
    //constructor, set default values
    public Preferences() {
        nDaysWithoutFine = 2;
        finePerDay = 500000;
        username = "tuan";
        setPassword("root");//store after being hashed
    }

    public int getnDaysWithoutFine() {
        return nDaysWithoutFine;
    }

    public void setnDaysWithoutFine(int nDaysWithoutFine) {
        this.nDaysWithoutFine = nDaysWithoutFine;
    }

    public float getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(float finePerDay) {
        this.finePerDay = finePerDay;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {//assign a security method
        if(password.length() < 13)//valid, what user enter
            this.password = DigestUtils.shaHex(password);//hash then store the pass
        else//not hashing a hashed pass
            this.password = password;
    }
    
    public static void initConfig() {
        Writer writer = null;
        try {
            Preferences preference = new Preferences();
            Gson gson = new Gson();//converting an object into gson 'string'
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer); //write to preference CONFIG_FILE throught Gson
        } catch (IOException ex) {//can not convert
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();//close the writer
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static Preferences getPreferences() {
        Gson gson = new Gson();
        Preferences preferences = new Preferences();
        try {//read from file
            preferences = gson.fromJson(new FileReader(CONFIG_FILE), Preferences.class);
        } catch (FileNotFoundException ex) {//not found an existing file
            initConfig();//than create one with defalt value
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }
        return preferences;
    }
    
    public static void writePreferencesToFile(Preferences preference) {//write the current preference
        Writer writer = null;
        try {
            Gson gson = new Gson();//converting an object into gson 'string'
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);
            
            AlertMaker.showSimpleInforAlert("Success", "Settings updated ");
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            AlertMaker.showErrorMessage(ex, "Failed", "Cant save the configuration file");
        } finally {
            try {
                writer.close();//close the writer
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
