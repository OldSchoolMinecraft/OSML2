package com.oldschoolminecraft.osml.ui;

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
    @FXML protected Button btnCancel;
    
    @FXML protected void okAction(ActionEvent event)
    {
        //
    }
    
    @FXML protected void cancelAction(ActionEvent event)
    {
        ((Stage)btnOK.getScene().getWindow()).close();
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
    
    public Button getCancelButton()
    {
        return btnCancel;
    }
}
