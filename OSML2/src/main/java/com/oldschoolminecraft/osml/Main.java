package com.oldschoolminecraft.osml;

import java.io.File;
import java.util.UUID;

import com.deadmandungeons.skinutil.MinecraftSkinUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oldschoolminecraft.osml.auth.AuthFile;
import com.oldschoolminecraft.osml.auth.MojangAPI;
import com.oldschoolminecraft.osml.ui.LoginController;
import com.oldschoolminecraft.osml.update.ClientUpdater;
import com.oldschoolminecraft.osml.util.Configuration;
import com.oldschoolminecraft.osml.util.JSONWebResponse;
import com.oldschoolminecraft.osml.util.Util;
import com.oldschoolminecraft.osml.util.minecraft.MinecraftProfile;
import com.oldschoolminecraft.osml.util.minecraft.MinecraftProfile.Skin;
import com.oldschoolminecraft.osml.util.minecraft.MinecraftProfile.Textures;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    public static Main instance;
    
    public static String CURRENT_VERSION = "1.0.0";
    
    public static boolean loggedIn = false, debug = false;
    public static AuthFile authDataFile;
    public static Configuration config;
    public static ClientUpdater clientUpdater;
    
    public static File workingDirectory;
    public static File authFile;
    public static File configFile;
    public static File versionsDir;
    public static File tmpDir;
    public static File librariesDir;
    
    public static Stage stage;
    public static LoginController loginController;
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    private static double setY;
    
    private MinecraftProfile profile;
    
    public static void main(String[] args)
    {
        for (String arg : args)
            if (arg.equalsIgnoreCase("--ide"))
                debug = true;
        
        if (debug)
            System.out.println("Running in IDE mode. Self updater will be disabled.");
        
        launch(args);
    }
    
    @Override
    public void start(Stage stage)
    {
        try
        {
            instance = this;
            
            Main.stage = stage;
            
            // remove windows border
            stage.initStyle(StageStyle.UNDECORATED);
            
            ObjectMapper mapper = new ObjectMapper();
            
            configFile = new File("config.json");
            if (configFile.exists())
            {
                config = mapper.readValue(configFile, Configuration.class);
            } else {
                config = Configuration.defaultConfig;
                mapper.writeValue(configFile, config);
            }
            
            // init directories
            workingDirectory = new File(config.gameDirectory);
            versionsDir = new File(workingDirectory, "versions");
            tmpDir = new File(workingDirectory, "tmp");
            librariesDir = new File(workingDirectory, "libraries");
            
            if (!workingDirectory.exists() || !workingDirectory.isDirectory())
                workingDirectory.mkdir();
            if (!versionsDir.exists() || !versionsDir.isDirectory())
                versionsDir.mkdir();
            if (!tmpDir.exists() || !tmpDir.isDirectory())
                tmpDir.mkdir();
            if (!librariesDir.exists() || !librariesDir.isDirectory())
                librariesDir.mkdir();
            
            authFile = new File(workingDirectory, "auth.json");
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
            
            if (loggedIn)
                profile = new MinecraftProfile(UUID.fromString(authDataFile.uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")), authDataFile.username, new Textures(new Skin(Util.get("https://www.oldschoolminecraft.com/getskin?username=" + authDataFile.username), false), ""));
            
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
            
            // set version label
            loginController.getVersionLabel().setText("v" + CURRENT_VERSION);
            
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
            if (authFile.exists())
                authFile.delete();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(authFile, authDataFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void saveConfig()
    {
        try
        {
            if (configFile.exists())
                configFile.delete();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(configFile, config);
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
        
        // disable access to cosmetics manager until its complete
        loginController.getCosmeticsButton().setDisable(true);
        
        if (flag)
        {
            loginController.getSettingsButton().setLayoutY(setY + (24 + 5));
            loginController.getCosmeticsButton().setLayoutY(setY + (24 + 5));
            
            BackgroundImage myBI = new BackgroundImage(SwingFXUtils.toFXImage(MinecraftSkinUtil.getPlayerSkinFront(instance.profile, 6).getImage(), null), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            loginController.getPlayerPreview().setBackground(new Background(myBI));
            loginController.getPlayerPreview().setStyle("-fx-effect: dropshadow(three-pass-box, black, 30, 0.3, 0, 0);");
        } else {
            loginController.getSettingsButton().setLayoutY(setY);
            loginController.getCosmeticsButton().setLayoutY(setY);
        }
    }
}
