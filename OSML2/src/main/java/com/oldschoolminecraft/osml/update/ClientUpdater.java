package com.oldschoolminecraft.osml.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.ui.UpdateController;
import com.oldschoolminecraft.osml.util.OS;
import com.oldschoolminecraft.osml.util.UpdateEvent;
import com.oldschoolminecraft.osml.util.Util;

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
    private ArrayList<Download> metaDownloads = new ArrayList<Download>();
    private ArrayList<Download> downloads = new ArrayList<Download>();
    
    public UpdateEvent event;
    
    public ClientUpdater(UpdateEvent event)
    {
        this.event = event;
    }
    
    public void run()
    {
        try
        {
            openUI();
            
            VersionManifest version = new VersionManager().loadInternal("b1.7.3");
            
            for (String input : version.libraries)
            {
                Library lib = new Library(input);
                Download dl = new Download(lib);
                dl.getMeta();
                metaDownloads.add(dl);
            }
            
            Download clientDL = new Download(new Library(version.client));
            clientDL.getMeta();
            metaDownloads.add(clientDL);
            
            String nativesInput = version.natives.windows;
            switch (OS.getOS())
            {
                default:
                    nativesInput = version.natives.windows;
                    break;
                case Windows:
                    nativesInput = version.natives.windows;
                    break;
                case Linux:
                    nativesInput = version.natives.linux;
                    break;
                case Mac:
                    nativesInput = version.natives.osx;
                    break;
            }
            Download nativesDL = new Download(new Library(nativesInput));
            nativesDL.getMeta();
            metaDownloads.add(nativesDL);
            
            Platform.runLater(() ->
            {
                int totalMetaDownloads = metaDownloads.size();
                int completedMetaDownloads = 0;
                for (Download dl : metaDownloads)
                {
                    try
                    {
                        if (Main.config.disableUpdates)
                        {
                            System.out.println(String.format("Skipping %s-%s (updates disabled)", dl.getLibrary().name, dl.getLibrary().version));
                            continue;
                        }
                        
                        completedMetaDownloads++;
                        int currentProgress = (completedMetaDownloads / totalMetaDownloads) * 100;
                        controller.getProgressBar().setProgress(currentProgress);
                        
                        File file = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", dl.getLibrary().name, dl.getLibrary().version, dl.getLibrary().name, dl.getLibrary().version));
                        file.getParentFile().mkdirs();
                        if (file.exists())
                        {
                            String diskHash = Util.sha256File(file.getAbsolutePath());
                            String metaHash = dl.getFileHash();
                            if (diskHash.equals(metaHash))
                            {
                                System.out.println(String.format("Skipping library: %s-%s", dl.getLibrary().name, dl.getLibrary().version));
                                controller.getCurrentFileLabel().setText(String.format("%s-%s skipped (%s%% checked)", dl.getLibrary().name, dl.getLibrary().version, currentProgress * 100));
                                controller.getCurrentFileLabel().setVisible(true);
                                continue;
                            } else {
                                System.out.println(String.format("Library %s-%s already exists, but will be updated.", dl.getLibrary().name, dl.getLibrary().version));
                                
                                file.delete();
                                downloads.add(dl);
                                totalDownloadSize += dl.getFileSize();
                            }
                        } else {
                            System.out.println(String.format("Download requested for library: %s-%s", dl.getLibrary().name, dl.getLibrary().version));
                            
                            downloads.add(dl);
                            totalDownloadSize += dl.getFileSize();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                
                if (downloads.size() < 1)
                {
                    System.out.println("Download queue is empty");
                    
                    closeUI();
                    controller.close();
                    event.onComplete();
                    return;
                }
                
                for (Download dl : downloads)
                {
                    try
                    {
                        if (Main.config.disableUpdates)
                        {
                            System.out.println(String.format("Skipping %s-%s (updates disabled)", dl.getLibrary().name, dl.getLibrary().version));
                            continue;
                        }
                        
                        System.out.println(String.format("Downloading %s-%s", dl.getLibrary().name, dl.getLibrary().version));
                    
                        dl.connect();
                    
                        InputStream in = dl.getConnection().getInputStream();
                        File file = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", dl.getLibrary().name, dl.getLibrary().version, dl.getLibrary().name, dl.getLibrary().version));
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
                                controller.getCurrentFileLabel().setText(String.format("%s-%s %s%% (%s%% downloaded)", dl.getLibrary().name, dl.getLibrary().version, (currentFileDownloadedSize / currentFileTotalSize) * 100, (downloadedSize / totalDownloadSize) * 100));
                                controller.getCurrentFileLabel().setVisible(true);
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
                }
                
                controller.getOKButton().setOnAction((event) ->
                {
                    ((Stage)controller.getOKButton().getScene().getWindow()).close();
                });
                
                //controller.getOKButton().setDisable(false);
                closeUI();
                controller.close();
                event.onComplete();
            });
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
                
                stage.setX((Main.launcherStage.getX() + Main.launcherStage.getWidth() / 2d) - root.prefWidth(width) / 2d);
                stage.setY((Main.launcherStage.getY() + Main.launcherStage.getHeight() / 2d) - root.prefHeight(height) / 2d);
                
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
                
                if (!Main.config.disableUpdates)
                    stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
    private void closeUI()
    {
        stage.close();
    }
}
