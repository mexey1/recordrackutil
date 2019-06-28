package com.recordrack.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;



public class Main extends Application
{
    public void start(Stage stage)
    {
        Scene scene = new Scene(createStage(),400,400);
        stage.setScene(scene);
        stage.show();

    }

    private Parent createStage()
    {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        //tabPane.setTabPWidth(Double.MAX_VALUE);
        createTabs(tabPane);


        return tabPane;
    }

    private void createTabs(TabPane tabPane)
    {
        String [] buttons = new String[]{"Add cost price", "View profits"};

        for (String b: buttons)
        {
            Tab tab = new Tab();
            //tab.setStyle();
            tab.setText(b);
            setTabScene(tab);
            tabPane.getTabs().add(tab);
        }
    }

    private void setTabScene(Tab tab)
    {
        Image image = new Image(getClass().getResourceAsStream("/resources/search.png"),60,60,true,false);
        BorderPane bPane = new BorderPane();
        bPane.setPrefSize(Double.MAX_VALUE,Double.MAX_VALUE);

        HBox hBox = new HBox();
        hBox.setPrefWidth(Double.MAX_VALUE);
        //hBox.setStyle("-fx-background-color: #ffd700");
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setSpacing(10);
        TextField searchField = new TextField();
        searchField.setPrefColumnCount(100);


        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        imageView.setPreserveRatio(true);
        hBox.getChildren().addAll(imageView,searchField);


        bPane.setTop(hBox);
        tab.setContent(bPane);
    }
}
