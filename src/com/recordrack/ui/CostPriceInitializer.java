package com.recordrack.ui;

import com.recordrack.db.Queries;
import com.recordrack.db.CostPriceRowItem;
import com.recordrack.interfaces.QueryResultListener;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.json.JSONArray;

import javax.management.Query;

public class CostPriceInitializer
{
    /**
     * CostPriceInitializer is the first scene that shows up when the user runs the program for the first time. It provides
     * UI components for defining the cost price of each item in the record-rack database
     */

    protected VBox vBox = null;
    protected TableView<CostPriceRowItem> tableView;
    protected Queries queries;
    protected BorderPane bPane;
    protected Scene scene;
    //protected VBox progressBarContainer;
    protected RRTable<CostPriceRowItem> rrTable;
    private QueryResultListener<JSONArray> listener;
    private boolean showCostPrice;
    private HBox saveButtonBox;


    /**
     * Creates a CostPriceInitializer instance. Users must call getCostPriceScene method to retrieve the scene to be displayed
     * on a stage(window)
     */
    public CostPriceInitializer(boolean showCostPrice)
    {
        //create a queries instance and retrieve all current quantities
        queries = new Queries();
        listener = new QueryResultListener<JSONArray>() {
            @Override
            public void doAction(JSONArray result)
            {
                CostPriceRowItem[] costPriceRowItems = new CostPriceRowItem[result.length()];
                int i=0;
                for(; i< costPriceRowItems.length; i++)
                    costPriceRowItems[i] = new CostPriceRowItem(result.getJSONObject(i));

                displayResults(costPriceRowItems);
            }
        };
        if(showCostPrice)
            queries.getCurrentQuantityForItem("",listener);
        else
            queries.getCurrentQuantity(listener);

        this.showCostPrice = showCostPrice;
    }

    /**
     * method called to get the scene containing UI components for the cost price initialization
     * @return a scene object containing cost price initialization components
     */
    public Scene getCostPriceScene()
    {
        if(scene == null)
        {
            bPane = new BorderPane();
            bPane.setPadding(new Insets(10,10,10,10));
            Node top = createTop();
            bPane.setTop(top);
            BorderPane.setMargin(top,new Insets(0,0,10,0));
            bPane.setBottom(createBottom());
            bPane.setCenter(createCenter());

            scene = new Scene(bPane,700,500);
            rrTable.setColumnWidth(6);//this has to be called after the view has been added to the scene
        }

        return scene;
    }

    /**
     * returns parent node containing the UI components for the displaying the cost price
     * @return the cost price UI parent node
     */
    public Node getSceneParentNode()
    {
        if(bPane == null)
            getCostPriceScene();
        return bPane;
    }

    /**
     * method for creating and initializing UI components that are displayed in the center
     * @return parent node containing the UI components
     */
    protected Node createCenter()
    {
        String [] columns = new String[]{"Category","Item","Quantity","Unit", "Cost", "Total Cost"};
        //properties to be accessed from cellValueFactory bean
        String [] props = new String[]{"category","item","quantity","baseUnit","unitCost","totalCost"};
        rrTable = new RRTable<CostPriceRowItem>(columns,props);
        tableView = rrTable.createTable(!showCostPrice);//,new int[]{4},new boolean[]{true},new boolean[]{true});
        rrTable.bindUnitCostPriceToTotal(5);
        rrTable.setCustomCellFactoy(4,true);
        rrTable.setCustomCellFactoy(5,false);

        return tableView;
    }

    /**
     * method for creating and initializing UI components that are displayed at the top
     * @return parent node containing the UI components
     */
    protected Node createTop()
    {
        //create the header for the table. A little description letting the user know what is expected
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0,0,10,0));
        Label big = new Label("Cost price entry");
        Label small = new Label("Enter the cost price of the base unit of each item");
        big.setPrefSize(Integer.MAX_VALUE,10);
        small.setPrefSize(Integer.MAX_VALUE,10);
        big.setFont(new Font("Arial",25));
        small.setFont(new Font("Arial",15));
        vBox.getChildren().addAll(big,small);

        return vBox;
    }

    /**
     * method for creating and initializing UI components that are displayed at the bottom
     * @return parent node containing the UI components
     */
    protected Node createBottom()
    {
        //create the bottom of the table
        saveButtonBox = new HBox();
        Button button = new Button("Save changes");
        button.setPrefWidth(200);
        saveButtonBox.setAlignment(Pos.CENTER_RIGHT);

        //define what happens when the save changes button is clicked
        button.setOnAction(setHandler());

        //((Label) node).setPrefWidth(Integer.MAX_VALUE);

        DoubleBinding binding = bPane.widthProperty().subtract(button.getWidth());
        //progressBarContainer.prefWidthProperty().bind(binding);
        saveButtonBox.setPadding(new Insets(10,0,10,0));
        saveButtonBox.getChildren().addAll(button);

        if(showCostPrice)
            saveButtonBox.setVisible(false);
        return saveButtonBox;
    }
    /**
     * method called when the getInitialQuantities query returns
     * @param costPriceRowItems the rowItem objects representing contents for each row in the table
     */
    private void displayResults(CostPriceRowItem[] costPriceRowItems)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                tableView.getItems().addAll(costPriceRowItems);
            }
        });
    }

    /**
     * method called to hide the save button
     */
    public void hideSaveButton()
    {
        saveButtonBox.setVisible(false);
    }

    /**
     * method to show the save button
     */
    public void showSaveButton()
    {
        saveButtonBox.setVisible(true);
    }


    /**
     * method to define the handler that takes care of the "save changes" button press
     * @return returns the created handler
     */
    protected EventHandler setHandler()
    {
        EventHandler handler = new EventHandler()
        {
            @Override
            public void handle(Event event)
            {
                //first, we'd like to ask the user to confirm they want to save the changes
                int choice = RecordRackAlert.showConfirmationDialog("Save changes","Are you sure you'd like to commit the changes?");
                if(choice==1)//we'd only proceed if the user chooses YES
                {
                    //let's display the progress bar and all
                    //progressBarContainer.setVisible(true);
                    RecordRackAlert.createProgressDialog();
                    updateProgressBarOnUIThread(0,"Creating cost price table");
                    //let's go ahead an create the cost_price table
                    initiateCostPriceTableCreation();
                }
            }
        };
        return handler;
    }

    /**
     * private method to initiate the cost price table creation
     */
    private void initiateCostPriceTableCreation()
    {
        new Queries().createCostPriceTable(new QueryResultListener<Boolean>()
        {
            @Override
            public void doAction(Boolean result)
            {
                if(result)//successful
                {
                    updateProgressBarOnUIThread(50,"cost price table created successfully");
                    //next, we'd go ahead and insert the records
                    initiateCostPriceRecordInsertion();
                }
                else
                {
                    updateProgressBarOnUIThread(100,"There was an error creating cost price table");
                }
            }
        });
    }

    /**
     * private method to save the cost price data into the table
     */
    private void initiateCostPriceRecordInsertion()
    {
        CostPriceRowItem[] items = new CostPriceRowItem[1];
        items = tableView.getItems().toArray(items);
        new Queries().insertRowItemsToCostPriceTable(new QueryResultListener<Boolean>()
        {
            @Override
            public void doAction(Boolean result)
            {
                if(result)//successful
                {
                    try
                    {
                        updateProgressBarOnUIThread(100,"Records have been successfully saved");
                        System.out.println(this.getClass()+" scene created");
                        if(!showCostPrice)
                            moveToMainScene();
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else //define what happens when the save transaction failed. this portion of the code would be updated later
                {
                    updateProgressBarOnUIThread(100,"There was an error saving the records to the table");
                }
            }
        },items);
    }

    /**
     * once the user has saved the new cost price values, we'd have to move to the next scene containing tabs for
     * searching for items and displaying profit/loss data
     */
    private void moveToMainScene()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Main.getInstance().createMainScene();
            }
        });
    }

    /**
     * method called to ensure update to the progress bar is done on the UI thread
     * @param percent how far the progress is
     * @param message text to display to the user
     */
    protected void updateProgressBarOnUIThread(double percent, String message)
    {
        if(!Platform.isFxApplicationThread())
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run()
                {
                    updateProgressBar(percent,message);
                }
            });
        }
        else
        {
            updateProgressBar(percent,message);
        }
    }

    /**
     * method called to update progress bar. This method should not be called directly, call updateProgressBarOnUIThread instead
     * @param percent the percentage to update the progressbar ti
     * @param message text to display to the user
     */
    private void updateProgressBar(double percent,String message)
    {
        RecordRackAlert.setProgress(percent,message);
        //ProgressBar bar =(ProgressBar) progressBarContainer.getChildren().get(0);
        //bar.setProgress(percent);
        //Label label = (Label) progressBarContainer.getChildren().get(1);
        //label.setText(message);
    }
}
