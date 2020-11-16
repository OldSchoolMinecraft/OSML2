package com.oldschoolminecraft.osml.ui;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.Event;
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
        //TODO: load mods list
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
            {
                //TODO: remove mod
            }
        }
    }
    
    @FXML protected void onCloseAction()
    {
        ((Stage) btnClose.getScene().getWindow()).close();
    }
    
    @FXML protected void onUpdateAction(Event event)
    {
        
    }
}
