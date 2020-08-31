package com.oldschoolminecraft.osml;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oldschoolminecraft.osml.auth.AuthFile;
import com.oldschoolminecraft.osml.auth.MojangAPI;
import com.oldschoolminecraft.osml.ui.LoginController;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@SuppressWarnings("all")
public class Main extends Application
{
    public static boolean loggedIn = false;
    public static AuthFile authDataFile;
    public static Configuration config;
    
    public static File workingDirectory = Util.getWorkingDirectory();
    public static File authFile = new File(workingDirectory, "auth.json");
    public static File configFile = new File(workingDirectory, "config.json");
    
    public static Stage stage;
    public static LoginController loginController;
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    private static double setY;
    
    public static void main(String[] args)
    {
        launch(args);
    }
    
    @Override
    public void start(Stage stage)
    {
        try
        {
            Main.stage = stage;
            
            // remove windows border
            stage.initStyle(StageStyle.TRANSPARENT);
            
            //TODO: auto login
            
            
            ObjectMapper mapper = new ObjectMapper();
            
            if (authFile.exists())
            {
                authDataFile = mapper.readValue(authFile, AuthFile.class);
                JSONWebResponse res = MojangAPI.validate(authDataFile.accessToken, authDataFile.clientToken);
                if (res.status != 204)
                {
                    loggedIn = false;
                    System.out.println("Auto login failed: " + (res.data.has("error") ? res.data.getString("error") : "reason unknown"));
                    authFile.delete();
                } else
                    loggedIn = true;
            }
            
            if (configFile.exists())
            {
                config = mapper.readValue(configFile, Configuration.class);
            } else {
                config = Configuration.defaultConfig;
                mapper.writeValue(configFile, config);
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/LoginUI.fxml"));
            Parent root = loader.load();
            loginController = loader.getController();
            
            setY = loginController.getSettingsButton().getLayoutY();
            
            Scene scene = new Scene(root, 350, 400);
            
            stage.setTitle(loggedIn ? "Old School Minecraft" : "Login");
            stage.setResizable(false);
            stage.setScene(scene);
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - root.prefWidth(350)) / 2); 
            stage.setY((screenBounds.getHeight() - root.prefHeight(400)) / 2);
            
            root.setOnMousePressed(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            
            root.setOnMouseDragged(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
            
            stage.show();
            
            setLoggedIn(loggedIn);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void saveAuthData()
    {
        try
        {
            authFile.delete();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(authFile, authDataFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void setLoggedIn(boolean flag)
    {
        loggedIn = flag;
        
        loginController.getUsernameField().setVisible(flag ? false : true);
        loginController.getPasswordField().setVisible(flag ? false : true);
        loginController.getLoginButton().setText(flag ? "Play" : "Login");
        
        loginController.getPlayerPreview().setVisible(flag ? true : false);
        loginController.getRememberAccountBox().setVisible(flag ? false : true);
        loginController.getUsernameLabel().setVisible(flag ? true : false);
        loginController.getUsernameLabel().setText(flag ? authDataFile.username : "Username");
        
        loginController.getLogoutButton().setVisible(flag ? true : false);
        loginController.getSettingsButton().setDisable(flag ? false : true);
        loginController.getCosmeticsButton().setDisable(flag ? false : true);
        
        if (flag)
        {
            loginController.getSettingsButton().setLayoutY(setY + (24 + 5));
            loginController.getCosmeticsButton().setLayoutY(setY + (24 + 5));
            
            BackgroundImage myBI = new BackgroundImage(new Image("https://minotar.net/body/" + authDataFile.uuid, 90, 180, false, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            loginController.getPlayerPreview().setBackground(new Background(myBI));
            loginController.getPlayerPreview().setStyle("-fx-effect: dropshadow(three-pass-box, black, 30, 0.3, 0, 0);");
        } else {
            loginController.getSettingsButton().setLayoutY(setY);
            loginController.getCosmeticsButton().setLayoutY(setY);
        }
    }
}
