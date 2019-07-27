package com.recordrack.ui;

import com.recordrack.db.DatabaseThread;
import com.recordrack.db.Queries;
import com.recordrack.db.ThreadPool;
import com.recordrack.interfaces.QueryResultListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private Queries queries;
    private Stage stage;
    private static Main main;

    @Override
    public void start(Stage stage)
    {

        this.stage = stage;
        System.out.println("Getting current quantities");
        main = this;
        displayFirstScene();
    }

    @Override
    public void init()
    {
        queries = new Queries();
        queries.setTimeZone();
    }

    private void displayFirstScene()
    {
        //first, we'd check if the cost_price table exists. If it doesn't exist, then this is the first time the user is
        //running the application and no commits have been made, else, we show the second scene as user must have inserted
        //cost price
        queries.checkIfTableExists("cost_price",new QueryResultListener<Integer>()
        {
            @Override
            public void doAction(Integer result)
            {
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(result>0)//if the table exists
                        {

                            createMainScene();
                            stage.show();
                        }
                        else
                        {
                            stage.setScene(new CostPriceInitializer(false).getCostPriceScene());
                            stage.show();
                        }
                    }
                });

            }
        });
    }

    public static Main getInstance()
    {
        return main;
    }

    public void createMainScene()
    {
        Scene scene = new Scene(createStage(),700,500);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop()
    {
        ThreadPool.getInstance().shutdown();
        DatabaseThread.getInstance().shutdown();
    }

    /**
     * if user has created and saved cost prices, then this method would be called
     * @return returns the parent node object
     */
    private Parent createStage()
    {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        //tabPane.setTabPWidth(Double.MAX_VALUE);
        createTabs(tabPane);
        return tabPane;
    }


    /**
     * method called from createStage method to create the tabs
     * @param tabPane the tabpane that needs to be populated
     */
    private void createTabs(TabPane tabPane)
    {
        String [] buttons = new String[]{"Add cost price", "View profits"};

        for (int i=0;i<buttons.length;i++)
        {
            Tab tab = new Tab();
            //tab.setStyle();
            tab.setText(buttons[i]);
            if(i==0)
                setItemSearchTab(tab);
            else if(i==1)
                setProfitLossTab(tab);
            tabPane.getTabs().add(tab);
        }
    }

    /**
     * this method defines the UI the user would see in the Add cost price tab
     * @param tab the tab to whih the ui components are to be added to
     */
    private void setItemSearchTab(Tab tab)
    {
        ItemSearchView itemSearchView = new ItemSearchView();
        tab.setContent(itemSearchView.getSceneParentNode());
    }

    private void setProfitLossTab(Tab tab)
    {
        ProfitAndLossViewer profitAndLossViewer = new ProfitAndLossViewer();
        tab.setContent(profitAndLossViewer.getUiNode());
    }

    /**
     * this method is called to initialize the search field. It sets up a listener for text changes and calls
     *
     * @param field the search field to be initialized
     */
    private void initSearchField(TextField field)
    {

    }
}
