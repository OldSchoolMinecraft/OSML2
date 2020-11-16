package com.oldschoolminecraft.osml.ui;

import com.deadmandungeons.skinutil.MinecraftSkinUtil;
import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.launch.Launcher;
import com.oldschoolminecraft.osml.update.ClientUpdater;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
    
    private double modsXOffset, modsYOffset;
    
    @FXML protected void onPlayAction(ActionEvent event)
    {
        ((Stage) btnPlay.getScene().getWindow()).close();
        new ClientUpdater(() ->  new Launcher().launch()).start();
    }
    
    @FXML protected void onLogoutAction(ActionEvent event)
    {
        //
    }
    
    @FXML protected void onCosmeticsAction(ActionEvent event)
    {
        //
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
    
    @FXML protected void onSettingsAction(ActionEvent event)
    {
        //
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
            lblUsername.setText(Main.instance.profile.getName());
            
            // skin preview
            BackgroundImage myBI = new BackgroundImage(SwingFXUtils.toFXImage(MinecraftSkinUtil.getPlayerSkinFront(Main.instance.profile, 7).getImage(), null), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            playerPreview.setBackground(new Background(myBI));
            playerPreview.setStyle("-fx-effect: dropshadow(three-pass-box, black, 30, 0.3, 0, 0);");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
