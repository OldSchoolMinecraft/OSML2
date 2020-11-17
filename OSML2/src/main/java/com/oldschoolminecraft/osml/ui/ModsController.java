package com.oldschoolminecraft.osml.ui;

import java.io.File;
import java.util.ArrayList;

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
    @FXML protected ListView<Mod> lstMods;
    
    @FXML protected Button btnAddMod;
    @FXML protected Button btnRemoveMod;
    @FXML protected Button btnClose;
    
    @FXML protected void initialize()
    {
        for (Mod mod : Main.modManager.mods)
            lstMods.getItems().add(mod);
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
                lstMods.getItems().add(new Mod(file.getName(), file.getAbsolutePath()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML protected void onRemoveModAction(ActionEvent event)
    {
        if (!lstMods.getSelectionModel().isEmpty())
        {
            for (Mod mod : lstMods.getSelectionModel().getSelectedItems())
                lstMods.getItems().remove(mod);
        }
    }
    
    @FXML protected void onCloseAction()
    {
        Main.modManager.mods.clear();
        for (Mod mod : lstMods.getItems())
            Main.modManager.mods.add(mod);
        Main.modManager.save();
        ((Stage) btnClose.getScene().getWindow()).close();
    }
    
    public boolean isModsEmpty()
    {
        return lstMods.getItems().isEmpty();
    }
    
    public ArrayList<Mod> getMods()
    {
        ArrayList<Mod> mods = new ArrayList<Mod>();
        for (Mod mod : lstMods.getItems())
            mods.add(mod);
        return mods;
    }
}
