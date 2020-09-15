package com.oldschoolminecraft.osml.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.ui.UpdateController;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ClientUpdater extends Thread
{
    public Stage stage;
    public UpdateController controller;
    public double xOffset, yOffset;
    
    private long totalDownloadSize = 0, downloadedSize = 0;
    private ArrayList<Download> downloads = new ArrayList<Download>();
    
    public void run()
    {
        try
        {
            openUI();
            
            VersionManifest version = new VersionManager().loadInternal("b1.7.3");
            
            for (String input : version.libraries)
            {
                Library lib = new Library(input);
                downloads.add(new Download(lib));
            }
            
            downloads.add(new Download(new Library(version.client)));
            
            for (Download dl : downloads)
            {
                dl.connect();
                totalDownloadSize += dl.getContentLength();
            }
            
            for (Download dl : downloads)
            {
                Platform.runLater(() ->
                {
                    try
                    {
                        InputStream in = dl.getConnection().getInputStream();
                        File file = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", dl.getLibrary().getName(), dl.getLibrary().getVersion(), dl.getLibrary().getName(), dl.getLibrary().getVersion()));
                        file.getParentFile().mkdirs();
                        FileOutputStream fout = new FileOutputStream(file);
                        
                        long currentFileTotalSize = dl.getContentLength();
                        long currentFileDownloadedSize = 0;
                        
                        int bytesRead = -1;
                        byte[] buffer = new byte[4096];
                        while ((bytesRead = in.read(buffer)) != -1)
                        {
                            currentFileDownloadedSize += bytesRead;
                            downloadedSize += bytesRead;
                            final int currentProgress = (int) ((((double) downloadedSize) / ((double) totalDownloadSize)) * 100000d);
                            
                            controller.getProgressBar().setProgress(currentProgress);
                            
                            try
                            {
                                controller.getCurrentFileLabel().setText(String.format("%s %s%% (%s%% total)", dl.getLibrary().getName(), (currentFileDownloadedSize / currentFileTotalSize) * 100, (downloadedSize / totalDownloadSize) * 100));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            
                            fout.write(buffer, 0, bytesRead);
                        }
                        in.close();
                        fout.close();
                        dl.getConnection().disconnect();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
            
            controller.getOKButton().setOnAction((event) ->
            {
                //TODO: copy files from tmp to proper location
                ((Stage)controller.getOKButton().getScene().getWindow()).close();
            });
            
            controller.getOKButton().setDisable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void openUI()
    {
        Platform.runLater(() ->
        {
            try
            {
                stage = new Stage();
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/UpdateUI.fxml"));
                Parent root = loader.load();
                controller = loader.getController();
                
                int width = 400;
                int height = 125;
                
                Scene scene = new Scene(root, width, height);
                
                stage.setResizable(false);
                stage.setScene(scene);
                
                stage.setX((Main.stage.getX() + Main.stage.getWidth() / 2d) - root.prefWidth(width) / 2d);
                stage.setY((Main.stage.getY() + Main.stage.getHeight() / 2d) - root.prefHeight(height) / 2d);
                
                root.setOnMousePressed(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        xOffset = event.getSceneX();
                        yOffset = event.getSceneY();
                    }
                });
                
                root.setOnMouseDragged(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        stage.setX(event.getScreenX() - xOffset);
                        stage.setY(event.getScreenY() - yOffset);
                    }
                });
                
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
