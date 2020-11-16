package com.oldschoolminecraft.osml.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.auth.AuthFile;
import com.oldschoolminecraft.osml.auth.HydraAPI;
import com.oldschoolminecraft.osml.launch.Launcher;
import com.oldschoolminecraft.osml.update.ClientUpdater;
import com.oldschoolminecraft.osml.util.JSONWebResponse;
import com.oldschoolminecraft.osml.util.Util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@SuppressWarnings("all")
public class LoginController
{
    @FXML protected Pane background;
    @FXML protected Pane logoPane;
    @FXML protected Pane playerPreview;
    
    @FXML protected Label lblVersion;
    
    @FXML protected TextField txtUsername;
    @FXML protected PasswordField txtPassword;
    
    @FXML protected Button btnClose;
    @FXML protected Button btnLogin;
    @FXML protected Button btnLogout;
    @FXML protected Button btnSettings;
    @FXML protected Button btnCosmetics;
    
    @FXML protected CheckBox chkRememberAccount;
    
    @FXML protected Label lblUsername;
    
    @FXML protected Button btnRegister;
    
    @FXML protected void handleCloseButtonAction(ActionEvent event)
    {
        // close
        System.exit(0);
    }
    
    @FXML protected void handleLoginAction(ActionEvent event)
    {
        if (!Main.loggedIn)
        {
            String username = txtUsername.getText();
            String password = txtPassword.getText();
            
            Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
            Matcher mat = pattern.matcher(username);

            JSONWebResponse res;
            if(mat.matches())
                res = HydraAPI.authenticateEmail(username, password);
            else
                res = HydraAPI.authenticateUsername(username, password);
            
            if (res.status == 200)
            {
                if (res.data.has("error"))
                {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Oh noes!");
                    alert.setHeaderText("Something went wrong!");
                    alert.setContentText("Hydra: " + res.data.getString("error"));
                    alert.showAndWait();
                    return;
                }
                
                Main.authDataFile = new AuthFile();
                Main.authDataFile.uuid = res.data.getString("uuid");
                Main.authDataFile.username = res.data.getString("username");
                Main.authDataFile.accessToken = res.data.getString("token");
                
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
                
                try
                {
                    Stage stage = new Stage();
                    
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/LauncherUI.fxml"));
                    Parent root = loader.load();
                    
                    Scene scene = new Scene(root, 600, 400);
                    
                    stage.setTitle("Launcher");
                    stage.setResizable(false);
                    stage.setScene(scene);
                    
                    stage.initStyle(StageStyle.UNDECORATED);
                    
                    stage.setX((scene.getX() + scene.getWidth() / 2d) - root.prefWidth(600) / 2d);
                    stage.setY((scene.getY() + scene.getHeight() / 2d) - root.prefHeight(400) / 2d);
                    
                    root.setOnMousePressed(new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent event)
                        {
                            settingsXOffset = event.getSceneX();
                            settingsYOffset = event.getSceneY();
                        }
                    });
                    
                    root.setOnMouseDragged(new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent event)
                        {
                            stage.setX(event.getScreenX() - settingsXOffset);
                            stage.setY(event.getScreenY() - settingsYOffset);
                        }
                    });
                    
                    stage.show();
                    Main.loginStage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Oh noes!");
                alert.setHeaderText("Something went wrong!");
                alert.setContentText(String.format("Failed to authenticate (status %s): %s", res.status, (res.data.has("errorMessage") ? res.data.getString("errorMessage") : "unknown")));
                alert.showAndWait();
            }
        } else {
            new ClientUpdater(() ->
            {
                Main.loginStage.close();
                new Launcher().launch();
            }).start();
        }
    }
    
    @FXML protected void handleLogoutAction(ActionEvent event)
    {
        HydraAPI.invalidate(Main.authDataFile.accessToken);
        Main.authFile.delete();
        Main.setLoggedIn(false);
    }
    
    private double settingsXOffset, settingsYOffset;
    @FXML protected void handleSettingsAction(ActionEvent event)
    {
        try
        {
            Stage stage = new Stage();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/SettingsUI.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 545, 286);
            
            stage.setTitle("Settings");
            stage.setResizable(false);
            stage.setScene(scene);
            
            stage.initOwner(Main.loginStage);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            stage.setX((Main.loginStage.getX() + Main.loginStage.getWidth() / 2d) - root.prefWidth(545) / 2d);
            stage.setY((Main.loginStage.getY() + Main.loginStage.getHeight() / 2d) - root.prefHeight(286) / 2d);
            
            //stage.setX((Main.stage.getWidth() - root.prefWidth(545)) / 2);
            //stage.setY((Main.stage.getHeight() - root.prefHeight(286)) / 2);
            
            root.setOnMousePressed(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    settingsXOffset = event.getSceneX();
                    settingsYOffset = event.getSceneY();
                }
            });
            
            root.setOnMouseDragged(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    stage.setX(event.getScreenX() - settingsXOffset);
                    stage.setY(event.getScreenY() - settingsYOffset);
                }
            });
            
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML protected void handleCosmeticsAction(ActionEvent event)
    {
        try
        {
            Stage stage = new Stage();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/CosmeticsUI.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 178, 160);
            
            stage.setTitle("Cosmetics");
            stage.setResizable(false);
            stage.setScene(scene);
            
            stage.initOwner(Main.loginStage);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - root.prefWidth(178)) / 2);
            stage.setY((screenBounds.getHeight() - root.prefHeight(160)) / 2);
            
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML protected void onRegisterAction(ActionEvent event)
    {
        try
        {
            Stage stage = new Stage();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/RegisterUI.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 300, 350);
            
            stage.setTitle("Register");
            stage.setResizable(false);
            stage.setScene(scene);
            
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - root.prefWidth(300)) / 2);
            stage.setY((screenBounds.getHeight() - root.prefHeight(350)) / 2);
            
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
    
    public Label getVersionLabel()
    {
        return lblVersion;
    }
}
