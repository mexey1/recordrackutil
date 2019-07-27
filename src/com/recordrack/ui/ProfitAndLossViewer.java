package com.recordrack.ui;

import com.recordrack.db.*;
import com.recordrack.interfaces.IncomeExpenseListener;
import com.recordrack.interfaces.QueryResultListener;
import com.recordrack.logic.DateUtil;
import com.recordrack.logic.MoneyFormatter;
import com.recordrack.logic.Type;
import javafx.application.Platform;
import javafx.beans.binding.NumberBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.json.JSONArray;

import java.time.LocalDate;

public class ProfitAndLossViewer
{
    private Queries queries;
    private TableView<ProfitLossRowItem> tableView;
    private Label date;
    private WeakChangeListener weakChangeListener;
    private QueryResultListener<JSONArray> queryResultListener;
    private MoneyDisplayNode totalRevenue,totalCost,totalProfit;
    private MoneyDisplayNode finalIncome,finalExpense,finalRevenue,finalProfit,finalCost;
    private double totalRevenueValue,totalCostValue,totalProfitValue,incomeValue,expenseValue;
    private IncomeExpenseViewer incomeExpenseViewer,expenseViewer;
    private BorderPane borderPane;
    private LocalDate localDate ;
    private IncomeExpenseListener incomeExpenseListener;

    /**
     * constructor to create and initializes listeners for displaying query results
     */
    public ProfitAndLossViewer()
    {
        queries = new Queries();
        localDate = LocalDate.now();
        String start = DateUtil.convertDateToStartOfDay(LocalDate.now());
        String end = DateUtil.convertDateToEndOfDay(LocalDate.now());
        queryResultListener = new QueryResultListener<JSONArray>()
        {
            @Override
            public void doAction(JSONArray result)
            {
                ProfitLossRowItem[] profitLossRowItems = new ProfitLossRowItem[result.length()];
                System.out.println(Class.class+" do action called "+profitLossRowItems.length);
                int i=0;
                totalRevenueValue = 0;
                totalCostValue = 0;
                for(; i< profitLossRowItems.length; i++)
                {
                    profitLossRowItems[i] = new ProfitLossRowItem(result.getJSONObject(i));
                    //sum up revenue and cost for each item in one shot
                    totalRevenueValue+=Double.parseDouble(profitLossRowItems[i].getRevenue());
                    totalCostValue+=Double.parseDouble(profitLossRowItems[i].getTotalCost());
                }
                totalProfitValue = totalRevenueValue - totalCostValue;

                System.out.println(Class.class+" do action called");
                displayResults(profitLossRowItems);
            }
        };

        incomeExpenseListener = new IncomeExpenseListener()
        {
            @Override
            public void setIncome(double income)
            {
                incomeValue = income;
                finalIncome.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(income)));
                double f = income+totalRevenueValue - totalCostValue - expenseValue;
                if(f>0)//#008000
                    finalProfit.getLabel().setStyle("-fx-font-weight:bold;-fx-text-fill:white;-fx-background-color:#008000");
                else if(f<0)
                    finalProfit.getLabel().setStyle("-fx-font-weight:bold;-fx-text-fill:white;-fx-background-color:#B22222");
                finalProfit.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(Math.abs(f))));
            }

            @Override
            public void setExpense(double expense)
            {
                expenseValue = expense;
                finalExpense.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(expense)));
                double f = incomeValue+totalRevenueValue - totalCostValue - expenseValue;
                if(f>0)
                    finalProfit.getLabel().setStyle("-fx-font-weight:bold;-fx-text-fill:white;-fx-background-color:#008000");
                else if(f<0)
                    finalProfit.getLabel().setStyle("-fx-font-weight:bold;-fx-text-fill:white;-fx-background-color:#B22222");
                finalProfit.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(Math.abs(f))));
            }
        };
        queries.getCostPriceAndProfitForDate(start,end,queryResultListener);
    }

    /**
     * method called to display results
     * @param array array of ProfitLossRowItem to be displayed to the user
     */
    private void displayResults(ProfitLossRowItem[] array)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                System.out.println("Called to add items to table view");
                tableView.getItems().clear();
                tableView.getItems().addAll(array);

                totalRevenue.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalRevenueValue)));
                totalCost.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalCostValue)));
                totalProfit.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalProfitValue)));

                finalRevenue.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalRevenueValue)));
                finalCost.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalCostValue)));
            }
        });
    }

    /**
     * this method creates and binds all components displayed for the profit/loss viewer
     * @return the parent node containing all profit/loss components
     */
    public Node getUiNode()
    {
        VBox vBox = new VBox(10);

        HBox hBox = new HBox(20);

        hBox.setPadding(new Insets(0,10,10,10));
        incomeExpenseViewer = new IncomeExpenseViewer(incomeExpenseListener, Type.Income);
        expenseViewer = new IncomeExpenseViewer(incomeExpenseListener,Type.Expense);
        NumberBinding binding = vBox.widthProperty().multiply(0.5);
        ((BorderPane) incomeExpenseViewer.getUiNode()).prefWidthProperty().bind(binding);
        ((BorderPane)expenseViewer.getUiNode()).prefWidthProperty().bind(binding);

        hBox.getChildren().addAll(incomeExpenseViewer.getUiNode(),expenseViewer.getUiNode());

        NumberBinding heightBinding = vBox.heightProperty().multiply(0.85);
        ((BorderPane)getNode()).prefHeightProperty().bind(heightBinding);

        vBox.getChildren().addAll(getNode(),hBox,createSummary());
        //ScrollPane scrollPane = new ScrollPane();
        //scrollPane.setContent(vBox);
        return vBox;
    }


    /**
     * creates the summary component of the report. The summary is displayed at the bottom
     * @return the parent node containing the summary components
     */
    private Node createSummary()
    {
        VBox vBox = new VBox(10);

        HBox revenueBox = new HBox(10);
        Label revenueLabel = new Label("Revenue");
        finalRevenue = new MoneyDisplayNode(false);
        finalRevenue.createNode();
        ((HBox)finalRevenue.createNode()).setPrefWidth(300);
        finalRevenue.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalCostValue)));
        revenueBox.getChildren().addAll(revenueLabel,finalRevenue.createNode());
        revenueBox.setAlignment(Pos.CENTER_RIGHT);

        HBox incomeBox = new HBox(10);
        Label incomeLabel = new Label("Income ");
        finalIncome = new MoneyDisplayNode(false);
        finalIncome.createNode();
        ((HBox)finalIncome.createNode()).setPrefWidth(300);
        finalIncome.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalCostValue)));
        incomeBox.getChildren().addAll(incomeLabel,finalIncome.createNode());
        incomeBox.setAlignment(Pos.CENTER_RIGHT);

        HBox expenseBox = new HBox(10);
        Label expenseLabel = new Label("Expense");
        finalExpense = new MoneyDisplayNode(false);
        finalExpense.createNode();
        ((HBox)finalExpense.createNode()).setPrefWidth(300);
        finalExpense.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalCostValue)));
        expenseBox.getChildren().addAll(expenseLabel,finalExpense.createNode());
        expenseBox.setAlignment(Pos.CENTER_RIGHT);

        HBox costBox = new HBox(10);
        Label costLabel = new Label("Cost   ");
        finalCost = new MoneyDisplayNode(false);
        finalCost.createNode();
        ((HBox)finalCost.createNode()).setPrefWidth(300);
        finalCost.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalCostValue)));
        costBox.getChildren().addAll(costLabel,finalCost.createNode());
        costBox.setAlignment(Pos.CENTER_RIGHT);

        HBox profitBox = new HBox(10);
        Label profitLabel = new Label("Profit  ");
        finalProfit = new MoneyDisplayNode(false);
        finalProfit.createNode();
        ((HBox)finalProfit.createNode()).setPrefWidth(300);
        finalProfit.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalCostValue)));
        profitBox.getChildren().addAll(profitLabel,finalProfit.createNode());
        profitBox.setAlignment(Pos.CENTER_RIGHT);

        vBox.getChildren().addAll(revenueBox,incomeBox,expenseBox,costBox,profitBox);
        vBox.setPrefWidth(Integer.MAX_VALUE);
        vBox.setPadding(new Insets(0,20,10,0));

        return vBox;
    }

    /**
     * method to create the table displaying the sales and profit/loss details to the user
     * @return parent node containing the profit/loss components
     */
    private Node getNode()
    {
        if(borderPane == null)
        {
            borderPane = new BorderPane();

            //create the ui table for displaying the profit/loss
            String columns[] = new String[]{"Category","Item","Quantity sold","Unit","Revenue","Total cost","Profit"};
            String property[] = new String[]{"category","item","quantity","unit","revenue","totalCost","profit"};
            RRTable<ProfitLossRowItem> rrTable = new RRTable<>(columns,property);
            tableView = rrTable.createTable(false);
            rrTable.setCustomCellFactoy(4,false);
            rrTable.setCustomCellFactoy(5,false);
            rrTable.setCustomCellFactoy(6,false);

            //define what would be at the center of the screen
            VBox center = new VBox(10);
            HBox hBox1 = new HBox(10);
            //we want the totalRevenue,totalCost,totalProfit nodes to resize equally as the screen is resized
            NumberBinding widthBinding = borderPane.widthProperty().multiply(0.14);
            for (int i=0;i<4;i++)
            {
                Label label = new Label();
                label.prefWidthProperty().bind(widthBinding);
                hBox1.getChildren().add(label);
            }
            totalRevenue = new MoneyDisplayNode(false);
            totalCost = new MoneyDisplayNode(false);
            totalProfit = new MoneyDisplayNode(false);
            //add components to the horizontal box
            hBox1.getChildren().addAll(totalRevenue.createNode(),totalCost.createNode(),totalProfit.createNode());
            ((HBox)totalRevenue.createNode()).prefWidthProperty().bind(widthBinding);
            ((HBox)totalCost.createNode()).prefWidthProperty().bind(widthBinding);
            ((HBox)totalProfit.createNode()).prefWidthProperty().bind(widthBinding);
            center.getChildren().addAll(tableView,hBox1);

            //create the ui component that would be displayed at the top
            Label title = new Label("Profit/Loss ");
            title.setFont(new Font("Arial",25));
            date = new Label();
            date.setFont(new Font("Arial",15));
            setDateText("Today");
            VBox vBox = new VBox(5);

            NumberBinding widthBinding1 = borderPane.widthProperty().multiply(0.7);
            vBox.prefWidthProperty().bind(widthBinding1);
            vBox.getChildren().addAll(title,date);
            vBox.setPadding(new Insets(0,10,10,0));

            //create layout for the calendar and refresh buttons
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.getChildren().addAll(vBox,createCalendarButton(),createRefreshButton());

            borderPane.setTop(hBox);
            borderPane.setCenter(center);

            rrTable.setColumnWidth(7);

            borderPane.setPadding(new Insets(10,10,10,10));
        }
        return borderPane;
    }

    /**
     * method called to display to the user the date whose data is currently being shown.
     * @param string the date as string to be displayed
     */
    private void setDateText(String string)
    {
        TextFlow textFlow = new TextFlow();
        Text text = new Text("Viewing profit/loss for ");
        Text text1 = new Text(string);
        text1.setStyle("-fx-font-weight: bold");
        textFlow.getChildren().addAll(text,text1);
        date.setGraphic(textFlow);
    }

    /**
     * creates and initializes a date picker which lets the user select what date to view profit/loss
     * @return the created date picker
     */
    private Node createCalendarButton()
    {
        DatePicker picker = new DatePicker();
        picker.setValue(LocalDate.now());
        picker.setPrefHeight(32);
        picker.getEditor().setPromptText("Select date");
        ChangeListener listener = new ChangeListener()
        {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue)
            {
                setDateText(DateUtil.formatDate((LocalDate)newValue,"EEEE, MMMM dd, yyyy"));
                localDate = (LocalDate)newValue;
                loadData(localDate);
            }
        };
        picker.valueProperty().addListener(listener);
        picker.setConverter(DateUtil.getDateConverter("dd-MM-yyyy"));
        return picker;
    }

    /**
     * button to let the user refresh data that is currently shown
     * @return the refresh button
     */
    private Node createRefreshButton()
    {
        Image image = new Image(getClass().getResourceAsStream("/resources/refresh.png"),80,80,true,false);
        ImageView reload = new ImageView();
        reload.setImage(image);
        reload.setFitWidth(25);
        reload.setFitHeight(25);
        reload.setPreserveRatio(true);
        Button button = new Button("Reload data",reload);
        button.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                loadData(null);
            }
        });

        return button;
    }

    /**
     * method to load sales, income and expense data for a given date
     * @param newValue the date excluding hour,minute,second components whose data is to be loaded e.g 07-23-2019
     */
    public void loadData(LocalDate newValue)
    {
        String start = DateUtil.convertDateToStartOfDay((LocalDate) newValue==null?localDate:newValue);
        String end = DateUtil.convertDateToEndOfDay((LocalDate)newValue==null?localDate:newValue);
        //localDate = (LocalDate)newValue;
        queries.getCostPriceAndProfitForDate(start,end,queryResultListener);
        incomeExpenseViewer.displayIncomeForDate(start,end);
        expenseViewer.displayExpenseForDate(start,end);
    }
}
