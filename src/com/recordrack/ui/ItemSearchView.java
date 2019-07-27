package com.recordrack.ui;

import com.recordrack.db.CostPriceRowItem;
import com.recordrack.db.Queries;
import com.recordrack.interfaces.QueryResultListener;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.json.JSONArray;

/**
 * class containing UI components for searching and editing cost prices of items
 */
public class ItemSearchView extends CostPriceInitializer
{
    private QueryResultListener<JSONArray> listener;
    private RRTable<CostPriceRowItem> rrTable;
    private TableView<CostPriceRowItem> tableView;
    private TextField searchField;
    private boolean isShowingSearchTableView;

    public ItemSearchView()
    {
        super(true);
        //hideSaveButton();
        init();
    }

    /**
     * private method to initialize a listener for updating UI upon completion of a query as well as
     * create the TableView for displaying the results of a search
     */
    private void init()
    {
        listener = new QueryResultListener<JSONArray>()
        {
            @Override
            public void doAction(JSONArray result)
            {
                displayResults(result);
            }
        };
        //bPane = new BorderPane();
        //bPane.setPrefSize(Double.MAX_VALUE,Double.MAX_VALUE);

        //let's create the table
        String [] columns = new String[]{"Category","Item","Quantity available", "Quantity at new price","Unit","Unit cost","New unit cost","Total cost"};
        String [] property = new String[]{"category","item","quantity","newQuantity","baseUnit","unitCost","newUnitCost","totalCost"};
        rrTable = new RRTable<>(columns,property);
        tableView = rrTable.createTable(true);

        //bPane.setCenter(tableView);
        rrTable.setCustomCellFactoy(6,true);
        rrTable.setCellAsTextfieldCell(3);
        rrTable.setCustomCellFactoy(7,false);
        rrTable.setCustomCellFactoy(5,false);
        rrTable.bindUnitCostPriceToTotal(6);
        //bPane.setTop(hBox);
    }

    /**
     * this method is overriden to define a custom UI that is displayed at the top of the screen
     * @return parent node containing the components at the top
     */
    @Override
    protected Node createTop()
    {
        Image image = new Image(getClass().getResourceAsStream("/resources/search_1.png"),100,100,true,false);
        HBox hBox = new HBox();
        hBox.setPrefWidth(Double.MAX_VALUE);
        //hBox.setStyle("-fx-background-color: #ffd700");
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setSpacing(10);
        searchField = new TextField();
        searchField.setPromptText("Type an item to search for");
        //searchField.setPrefColumnCount(100);
        searchField.setPrefWidth(Integer.MAX_VALUE);
        searchField.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if(newValue.length()>0 && !isShowingSearchTableView)
                {
                    bPane.setCenter(tableView);
                    rrTable.setColumnWidth(8);
                    isShowingSearchTableView = true;
                    showSaveButton();
                }
                else if(newValue.length()==0)
                {
                    bPane.setCenter(ItemSearchView.super.tableView);
                    isShowingSearchTableView = false;
                    hideSaveButton();
                }
                searchForItem(newValue);
            }
        });

        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(35);
        imageView.setFitHeight(35);
        imageView.setPreserveRatio(true);
        bPane.setPadding(new Insets(10,10,10,10));
        hBox.setStyle("-fx-background-color: #D3D3D3;  -fx-background-radius: 5 5 5 5;");
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(imageView,searchField);

        //line of code for displaying the prompt text immediately
        hBox.requestFocus();

        return hBox;
    }

    /*    public Node getSearch()
    {
        return super.bPane;
    }*/

    /**
     * method called to search for an item
     * @param text the item to search for
     */
    private void searchForItem(String text)
    {
        queries.getCurrentQuantityForItem(text,listener);
    }

    /**
     * method overridden to define what happens when the save changes button is clicked
     * @return event handler object
     */
    @Override
    public EventHandler setHandler()
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
                    System.out.println(this.getClass()+" creating dialog");
                    //let's create and display a progress dialog
                    RecordRackAlert.createProgressDialog();
                    //progressBarContainer.setVisible(true);
                    System.out.println(this.getClass()+" updating progress bar");
                    updateProgressBarOnUIThread(0,"saving records to database ");
                    //let's go ahead an create the cost_price table
                    updateCostPriceTable();
                }
            }
        };
        return handler;
    }

    /**
     * private method to update the cost price table
     */
    private void updateCostPriceTable()
    {
        CostPriceRowItem[] items = new CostPriceRowItem[1];
        items = tableView.getItems().toArray(items);
        System.out.println(this.getClass()+" "+items[0].getCostValue());
        queries.insertRowItemsToCostPriceTable(new QueryResultListener<Boolean>()
        {
            @Override
            public void doAction(Boolean result)
            {
                if(result)//successful
                {
                    updateProgressBarOnUIThread(100,"Records saved successfully");
                    //we'd reset value of the searchfield to empty, but this would have to be on the UI thread
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run()
                        {
                            searchField.setText("");
                        }
                    });

                }
                else //define what happens when the save transaction failed. this portion of the code would be updated later
                {
                    updateProgressBarOnUIThread(100,"There was an error while trying to update values");
                }
            }
        },items);
    }

    /**
     * private method to display the results of a search
     * @param result JSONArray containing the results of the search
     */
    private void displayResults(JSONArray result)
    {
        CostPriceRowItem []rowItems = new CostPriceRowItem[result.length()];
        for(int i=0;i<rowItems.length;i++)
            rowItems[i] = new CostPriceRowItem(result.getJSONObject(i));
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                tableView.getItems().clear();
                tableView.getItems().addAll(rowItems);
            }
        });
    }
}
