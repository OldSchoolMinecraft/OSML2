package com.oldschoolminecraft.osml.ui;

import com.oldschoolminecraft.osml.Main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class UpdateController
{
    @FXML protected ProgressBar progressBar;
    @FXML protected Label lblCurrentFile;
    @FXML protected Button btnOK;
    
    @FXML protected void okAction(ActionEvent event)
    {
        Main.clientUpdater.event.fire();
        close();
    }
    
    public void close()
    {
        Platform.runLater(() ->
        {
            ((Stage)btnOK.getScene().getWindow()).close();
        });
    }
    
    public ProgressBar getProgressBar()
    {
        return progressBar;
    }
    
    public Label getCurrentFileLabel()
    {
        return lblCurrentFile;
    }
    
    public Button getOKButton()
    {
        return btnOK;
    }
}
