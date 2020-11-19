package com.oldschoolminecraft.osml.ui;

import com.deadmandungeons.skinutil.MinecraftSkinUtil;
import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.auth.HydraAPI;
import com.oldschoolminecraft.osml.launch.Launcher;
import com.oldschoolminecraft.osml.update.ClientUpdater;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LauncherController
{
    @FXML protected Button btnPlay;
    @FXML protected Button btnLogout;
    
    @FXML protected Button btnCosmetics;
    @FXML protected Button btnMods;
    @FXML protected Button btnSettings;
    
    @FXML protected Button btnClose;

    @FXML protected Pane playerPreview;
    
    @FXML protected Label lblVersion;
    @FXML protected Label lblUsername;
    
    private double modsXOffset, modsYOffset, cosmeticsXOffset, cosmeticsYOffset;
    
    private Stage stage;
    
    @FXML protected void onPlayAction(ActionEvent event)
    {
        new ClientUpdater(() ->
        {
            ((Stage) btnPlay.getScene().getWindow()).hide();
            new Launcher().debugLaunch();
        }).start();
    }
    
    private double loginXOffset, loginYOffset;
    @FXML protected void onLogoutAction(ActionEvent event)
    {
        try
        {
            HydraAPI.invalidate(Main.authDataFile.accessToken);
            Main.authFile.delete();
            
            Stage stage = new Stage();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/LoginUI.fxml"));
            Parent root = loader.load();
            Main.loginController = loader.getController();
            
            Scene scene = new Scene(root, 350, 400);
            
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(Main.loggedIn ? "Old School Minecraft" : "Login");
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
                    loginXOffset = event.getSceneX();
                    loginYOffset = event.getSceneY();
                }
            });
            
            root.setOnMouseDragged(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    stage.setX(event.getScreenX() - loginXOffset);
                    stage.setY(event.getScreenY() - loginYOffset);
                }
            });
            
            ((Stage) btnPlay.getScene().getWindow()).close();
            
            stage.show();
            
            Main.loginStage = stage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML protected void onCosmeticsAction(ActionEvent event)
    {
        try
        {
            Stage stage = new Stage();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/CosmeticsUI.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 350, 150);
            
            stage.setTitle("Cosmetics");
            stage.setResizable(false);
            stage.setScene(scene);
            
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - root.prefWidth(350)) / 2);
            stage.setY((screenBounds.getHeight() - root.prefHeight(150)) / 2);
            
            root.setOnMousePressed(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    cosmeticsXOffset = event.getSceneX();
                    cosmeticsYOffset = event.getSceneY();
                }
            });
            
            root.setOnMouseDragged(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    stage.setX(event.getScreenX() - cosmeticsXOffset);
                    stage.setY(event.getScreenY() - cosmeticsYOffset);
                }
            });
            
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML protected void onModsAction(ActionEvent event)
    {
        try
        {
            Stage stage = new Stage();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ModsUI.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 400, 500);
            
            stage.setTitle("Mods");
            stage.setResizable(false);
            stage.setScene(scene);
            
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - root.prefWidth(400)) / 2);
            stage.setY((screenBounds.getHeight() - root.prefHeight(500)) / 2);
            
            root.setOnMousePressed(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    modsXOffset = event.getSceneX();
                    modsYOffset = event.getSceneY();
                }
            });
            
            root.setOnMouseDragged(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    stage.setX(event.getScreenX() - modsXOffset);
                    stage.setY(event.getScreenY() - modsYOffset);
                }
            });
            
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private double settingsXOffset, settingsYOffset;
    @FXML protected void onSettingsAction(ActionEvent event)
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
            
            stage.setX((this.stage.getX() + this.stage.getWidth() / 2d) - root.prefWidth(545) / 2d);
            stage.setY((this.stage.getY() + this.stage.getHeight() / 2d) - root.prefHeight(286) / 2d);
            
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
    
    @FXML protected void onCloseAction(ActionEvent event)
    {
        System.exit(0);
    }
    
    @FXML protected void initialize()
    {
        try
        {
            // version number
            lblVersion.setText(Main.CURRENT_VERSION);
            
            // username
            lblUsername.setMaxWidth(Double.MAX_VALUE);
            AnchorPane.setLeftAnchor(lblUsername, 0.0);
            AnchorPane.setRightAnchor(lblUsername, 0.0);
            lblUsername.setAlignment(Pos.CENTER);
            lblUsername.setText(Main.instance.profile.getName());
            
            // skin preview
            updateSkin();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void updateSkin()
    {
        BackgroundImage myBI = new BackgroundImage(SwingFXUtils.toFXImage(MinecraftSkinUtil.getPlayerSkinFront(Main.instance.profile, 7).getImage(), null), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        playerPreview.setBackground(new Background(myBI));
        playerPreview.setStyle("-fx-effect: dropshadow(three-pass-box, black, 30, 0.3, 0, 0);");
    }
    
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
}
