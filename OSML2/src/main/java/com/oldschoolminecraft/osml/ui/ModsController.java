package com.oldschoolminecraft.osml.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.mods.Mod;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ModsController
{
    @FXML protected ListView<Mod> lstMods;
    
    @FXML protected Button btnAddMod;
    @FXML protected Button btnRemoveMod;
    @FXML protected Button btnMoveUp;
    @FXML protected Button btnMoveDown;
    @FXML protected Button btnClose;
    
    @FXML protected void initialize()
    {
        lstMods.setCellFactory(CheckBoxListCell.forListView(new Callback<Mod, ObservableValue<Boolean>>()
        {
            public ObservableValue<Boolean> call(Mod param)
            {
                return param.enabledProperty();
            }
        }));
        
        Main.modManager.mods.clear();
        Main.modManager.load();
        for (Mod mod : Main.modManager.mods)
            lstMods.getItems().add(mod);
    }
    
    @FXML protected void onAddModAction(ActionEvent event)
    {
        try
        {
            FileChooser fileChooser = new FileChooser();
            
            ExtensionFilter modFilter = new ExtensionFilter("MOD files (*.jar, *.zip)", "*.jar", "*.zip");
            fileChooser.getExtensionFilters().add(modFilter);
            
            List<File> files = fileChooser.showOpenMultipleDialog(btnAddMod.getScene().getWindow());
            
            if (files != null) // sanity check in case the operation is cancelled
                for (File file : files)
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
    
    @FXML protected void onMoveUp(ActionEvent event)
    {
        if (!lstMods.getSelectionModel().isEmpty())
        {
            int index = lstMods.getSelectionModel().getSelectedIndex();
            if (index > 0)
            {
                int newIndex = index - 1;
                
                if (newIndex < 0 || newIndex >= lstMods.getItems().size())
                    return;
                
                Mod mod = lstMods.getSelectionModel().getSelectedItem();
                
                lstMods.getItems().remove(mod);
                lstMods.getItems().add(newIndex, mod);
                lstMods.getSelectionModel().select(newIndex);
            }
        }
    }
    
    @FXML protected void onMoveDown(ActionEvent event)
    {
        if (!lstMods.getSelectionModel().isEmpty())
        {
            int index = lstMods.getSelectionModel().getSelectedIndex();
            if (index < lstMods.getItems().size() - 1)
            {
                int newIndex = index + 1;
                
                if (newIndex < 0 || newIndex >= lstMods.getItems().size())
                    return;
                
                Mod mod = lstMods.getSelectionModel().getSelectedItem();
                
                lstMods.getItems().remove(mod);
                lstMods.getItems().add(newIndex, mod);
                lstMods.getSelectionModel().select(newIndex);
            }
        }
    }
    
    @FXML protected void onCloseAction()
    {
        ArrayList<Mod> mods = new ArrayList<Mod>();
        for (Mod mod : lstMods.getItems())
            mods.add(mod);
        Main.modManager.save(mods);
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
