package garage.assistant.ui.about;

import garage.assistant.util.GarageAssistantUtil;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class AboutController implements Initializable {
    private static final String FACEBOOK = "https://www.facebook.com/itsTrada";
    private static final String GITHUB = "https://github.com/itsTrada";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
        private void loadWebpage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
            handleWebpageLoadException(url);
        }
    }

    private void handleWebpageLoadException(String url) {
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.load(url);
        Stage stage = new Stage();
        Scene scene = new Scene(new StackPane(browser));
        stage.setScene(scene);
        stage.setTitle("Garage Assistant Project");
        stage.show();
        GarageAssistantUtil.setStageIcon(stage);
    }

    @FXML
    private void loadGithub(ActionEvent event) {
         loadWebpage(GITHUB);
    }

    @FXML
    private void loadFacebook(ActionEvent event) {
        loadWebpage(FACEBOOK);
    }
    
}
