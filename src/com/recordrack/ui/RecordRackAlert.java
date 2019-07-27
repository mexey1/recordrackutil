package com.recordrack.ui;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class RecordRackAlert
{
    private static VBox progressBarContainer ;
    private static Dialog<Boolean> dialog;
    /**
     * a static method to show an information alert dialog
     * @param message the message to be displayed
     * @param title the title of the alert
     */
    public static void showInformationAlert(String message,String title)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setContentText(message);
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get()==ButtonType.OK)
                    alert.close();
            }
        });
    }

    /**
     * method to create and show a confirmation dialog
     * @param title the title of the dialog
     * @param message message to display to the user
     * @return an integer representing the choice/selection of the user
     */
    public static int showConfirmationDialog(String title, String message)
    {
        int res = -1;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,null,ButtonType.YES,ButtonType.NO);
        alert.setTitle(title);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get()== ButtonType.YES)
            res=1;
        else
            res = 0;
        return  res;
    }

    /**
     * method called to create a dialog for letting the user know the status of an activity
     */
    public static void createProgressDialog()
    {
        dialog = new Dialog();
        Image image = new Image(RecordRackAlert.class.getResourceAsStream("/resources/work-in-progress.png"),100,100,true,false);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        ProgressBar progressBar = new ProgressBar();
        Label label = new Label();
        progressBar.setPrefHeight(20);
        progressBar.setMaxHeight(20);
        progressBar.setPrefWidth(Integer.MAX_VALUE);
        label.setPrefHeight(20);
        label.setPrefWidth(Integer.MAX_VALUE);
        label.setText("Initializing database connection");
        progressBarContainer = new VBox(10);
        progressBarContainer.setAlignment(Pos.CENTER);
        progressBarContainer.getChildren().addAll(label,progressBar);
        //progressBarContainer.setVisible(false);
        progressBarContainer.setPadding(new Insets(0,10,0,10));

        dialog.setHeaderText("We are working really hard to execute your request");
        dialog.setGraphic(imageView);
        dialog.getDialogPane().setContent(progressBarContainer);
        dialog.getDialogPane().setPrefSize(500,200);
        dialog.show();
    }

    /**
     * method called to update the progress of an activity
     * @param val the value to update to
     * @param progress the text to display to the user
     */
    public static void setProgress(double val, String progress)
    {
        try
        {
            Label label = (Label) progressBarContainer.getChildren().get(0);
            label.setText(progress);

            ProgressBar bar = (ProgressBar) progressBarContainer.getChildren().get(1);
            bar.setProgress(val);

            if(val ==100)
            {
                Thread.sleep(100);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            }

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
