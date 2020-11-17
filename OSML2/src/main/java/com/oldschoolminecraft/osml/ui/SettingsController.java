package com.oldschoolminecraft.osml.ui;

import java.awt.Desktop;
import java.io.File;

import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.util.Configuration;
import com.oldschoolminecraft.osml.util.Util;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController
{
    @FXML protected CheckBox chkGameDirectory;
    
    @FXML protected ChoiceBox<String> cmbVersion;
    
    @FXML protected CheckBox chkExecutable;
    @FXML protected CheckBox chkJVMArguments;
    
    @FXML protected TextField txtGameDirectory;
    @FXML protected TextField txtExecutable;
    @FXML protected TextField txtJVMArguments;
    
    @FXML protected Button saveButton;
    @FXML protected Button cancelButton;
    
    @FXML protected CheckBox chkDisableUpdates;
    
    @FXML public void initialize()
    {
        if (!Main.config.gameDirectory.equals(Configuration.defaultConfig.gameDirectory))
            chkGameDirectory.setSelected(true);
        if (!Main.config.javaExecutable.equals(Configuration.defaultConfig.javaExecutable))
            chkExecutable.setSelected(true);
        if (!Main.config.jvmArguments.equals(Configuration.defaultConfig.jvmArguments))
            chkJVMArguments.setSelected(true);
        
        txtGameDirectory.setDisable(!chkGameDirectory.isSelected());
        txtExecutable.setDisable(!chkExecutable.isSelected());
        txtJVMArguments.setDisable(!chkJVMArguments.isSelected());
        
        txtGameDirectory.setText(Main.config.gameDirectory);
        txtExecutable.setText(Main.config.javaExecutable);
        txtJVMArguments.setText(Main.config.jvmArguments);
        
        chkDisableUpdates.setSelected(Main.config.disableUpdates);
        
        cmbVersion.setValue("b1.7.3");
        cmbVersion.setItems(FXCollections.observableArrayList("b1.7.3"));
    }
    
    @FXML protected void handleOpenGameDir(ActionEvent event)
    {
        try
        {
            Desktop.getDesktop().open(Util.getLauncherDirectory());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML protected void handleSave(ActionEvent event)
    {
        Configuration config = new Configuration();
        
        if (chkGameDirectory.isSelected())
        {
            String gameDirVal = txtGameDirectory.getText();
            File gameDir = new File(gameDirVal);
            
            if (gameDir.exists() && gameDir.isDirectory())
                config.gameDirectory = gameDirVal;
            else
                config.gameDirectory = Configuration.defaultConfig.gameDirectory;
        } else
            config.gameDirectory = Configuration.defaultConfig.gameDirectory;
        
        if (chkExecutable.isSelected())
        {
            String execVal = txtExecutable.getText();
            File exec = new File(execVal);
            
            if (exec.exists() && exec.isFile())
                config.javaExecutable = execVal;
        } else
            config.javaExecutable = Configuration.defaultConfig.javaExecutable;
        
        if (chkJVMArguments.isSelected())
            config.jvmArguments = txtJVMArguments.getText();
        else
            config.jvmArguments = Configuration.defaultConfig.jvmArguments;
        
        config.disableUpdates = chkDisableUpdates.isSelected();
        
        Main.config = config;
        Main.saveConfig();
        
        ((Stage)saveButton.getScene().getWindow()).close();
    }
    
    @FXML protected void handleCancel(ActionEvent event)
    {
        ((Stage)cancelButton.getScene().getWindow()).close();
    }
    
    @FXML protected void handleCheck(ActionEvent event)
    {
        txtGameDirectory.setDisable(!chkGameDirectory.isSelected());
        txtExecutable.setDisable(!chkExecutable.isSelected());
        txtJVMArguments.setDisable(!chkJVMArguments.isSelected());
    }
}
