package com.oldschoolminecraft.osml.ui;

import java.io.File;

import org.json.JSONObject;

import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.util.Util;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class CosmeticsController
{
    @FXML protected Button btnChooseSkin;
    @FXML protected Button btnClose;
    
    @FXML protected void onChooseSkinAction(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        
        ExtensionFilter pngFilter = new ExtensionFilter("PNG files (*.png)", "*.png");
        
        fileChooser.getExtensionFilters().add(pngFilter);
        
        File file = fileChooser.showOpenDialog(btnChooseSkin.getScene().getWindow());
        
        if (file != null && file.exists())
        {
            try
            {
                JSONObject obj = new JSONObject(Util.uploadFile(String.format("https://www.oldschoolminecraft.com/changeskin?uuid=%s&token=%s", Main.authDataFile.uuid, Main.authDataFile.accessToken), file.getAbsolutePath()));
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Server responded");
                alert.setContentText(obj.getString("message"));
                alert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @FXML protected void onCloseAction(ActionEvent event)
    {
        Main.launcherController.updateSkin();
        ((Stage) btnClose.getScene().getWindow()).hide();
    }
}
