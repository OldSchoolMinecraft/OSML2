package com.oldschoolminecraft.osml.ui;

import java.io.File;

import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.mods.Mod;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ModsController
{
    @FXML protected ListView<String> lstMods;
    
    @FXML protected Button btnAddMod;
    @FXML protected Button btnRemoveMod;
    @FXML protected Button btnClose;
    
    @FXML protected void initialize()
    {
        for (Mod mod : Main.instance.modManager.mods)
            lstMods.getItems().add(mod.getName());
    }
    
    @FXML protected void onAddModAction(ActionEvent event)
    {
        try
        {
            FileChooser fileChooser = new FileChooser();
            
            ExtensionFilter jarFilter = new ExtensionFilter("JAR files (*.jar)", "*.jar");
            ExtensionFilter classFilter = new ExtensionFilter("CLASS files (*.class)", "*.class");
            
            fileChooser.getExtensionFilters().add(jarFilter);
            fileChooser.getExtensionFilters().add(classFilter);
            
            File file = fileChooser.showOpenDialog(btnAddMod.getScene().getWindow());
            
            if (file != null && file.exists())
                lstMods.getItems().add(file.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML protected void onRemoveModAction(ActionEvent event)
    {
        if (!lstMods.getSelectionModel().isEmpty())
        {
            for (String mod : lstMods.getSelectionModel().getSelectedItems())
                Main.instance.modManager.removeMod(mod);
        }
    }
    
    @FXML protected void onCloseAction()
    {
        Main.instance.modManager.save(new File(Main.modsDir, "manifest.json"));
        ((Stage) btnClose.getScene().getWindow()).close();
    }
}
