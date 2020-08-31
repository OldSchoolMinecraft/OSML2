package com.oldschoolminecraft.osml.ui;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oldschoolminecraft.osml.JSONWebResponse;
import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.auth.AuthFile;
import com.oldschoolminecraft.osml.auth.MojangAPI;
import com.oldschoolminecraft.osml.launch.Launcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

@SuppressWarnings("all")
public class LoginController
{
    @FXML protected Pane background;
    @FXML protected Pane logoPane;
    @FXML protected Pane playerPreview;
    
    @FXML protected TextField txtUsername;
    @FXML protected PasswordField txtPassword;
    
    @FXML protected Button btnClose;
    @FXML protected Button btnLogin;
    @FXML protected Button btnLogout;
    @FXML protected Button btnSettings;
    @FXML protected Button btnCosmetics;
    
    @FXML protected CheckBox chkRememberAccount;
    
    @FXML protected Label lblUsername;
    
    @FXML protected void handleCloseButtonAction(ActionEvent event)
    {
        // close
        System.exit(0);
    }
    
    @FXML protected void handleLoginAction(ActionEvent event)
    {
        System.out.println("Login action");
        
        if (!Main.loggedIn)
        {
            String username = txtUsername.getText();
            String password = txtPassword.getText();
            String clientToken = UUID.randomUUID().toString().replaceAll("-", "");
            
            JSONWebResponse res = MojangAPI.authenticate(username, password, clientToken);
            if (res.status == 200)
            {
                Main.authDataFile = new AuthFile();
                Main.authDataFile.uuid = res.data.getJSONObject("selectedProfile").getString("id");
                Main.authDataFile.username = res.data.getJSONObject("selectedProfile").getString("name");
                Main.authDataFile.accessToken = res.data.getString("accessToken");
                Main.authDataFile.clientToken = clientToken;
                
                if (chkRememberAccount.isSelected())
                    Main.saveAuthData();
                
                try
                {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(Main.authFile, Main.authDataFile);
                } catch (Exception ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Oh noes!");
                    alert.setHeaderText("Something went wrong!");
                    alert.setContentText(String.format("Failed to save auth data: " + ex.getMessage()));
                    alert.showAndWait();
                }
                
                Main.setLoggedIn(true);
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Oh noes!");
                alert.setHeaderText("Something went wrong!");
                alert.setContentText(String.format("Failed to authenticate (%s): " + (res.data.has("errorMessage") ? res.data.getString("errorMessage") : "unknown")));
                alert.showAndWait();
            }
        } else {
            new Launcher().launch();
        }
    }
    
    @FXML protected void handleLogoutAction(ActionEvent event)
    {
        MojangAPI.invalidate(Main.authDataFile.accessToken, Main.authDataFile.clientToken);
        Main.authFile.delete();
        Main.setLoggedIn(false);
    }
    
    @FXML protected void handleSettingsAction(ActionEvent event)
    {
        System.out.println("Settings action");
    }
    
    @FXML protected void handleCosmeticsAction(ActionEvent event)
    {
        System.out.println("Cosmetics action");
    }
    
    public Pane getBackground()
    {
        return background;
    }
    
    public Pane getLogoPane()
    {
        return logoPane;
    }
    
    public TextField getUsernameField()
    {
        return txtUsername;
    }
    
    public PasswordField getPasswordField()
    {
        return txtPassword;
    }
    
    public Button getLoginButton()
    {
        return btnLogin;
    }
    
    public Button getLogoutButton()
    {
        return btnLogout;
    }
    
    public Button getSettingsButton()
    {
        return btnSettings;
    }
    
    public Button getCosmeticsButton()
    {
        return btnCosmetics;
    }
    
    public Pane getPlayerPreview()
    {
        return playerPreview;
    }
    
    public CheckBox getRememberAccountBox()
    {
        return chkRememberAccount;
    }
    
    public Label getUsernameLabel()
    {
        return lblUsername;
    }
}
