package com.recordrack.ui;

import com.recordrack.db.*;
import com.recordrack.interfaces.IncomeExpenseListener;
import com.recordrack.interfaces.QueryResultListener;
import com.recordrack.logic.DateUtil;
import com.recordrack.logic.Type;
import javafx.application.Platform;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import org.json.JSONArray;

import java.time.LocalDate;

/**
 * class that creates the UI for displaying income/expense results
 */
public class IncomeExpenseViewer
{
    private Queries queries;
    private QueryResultListener<JSONArray> queryResultListener;
    private double totalIncomeValue = 0;
    private TableView<IncomeRowItem> tableView;
    private MoneyDisplayNode totalIncome;
    private BorderPane bPane;
    private IncomeExpenseListener listener;
    private Type type;

    /**
     * constructor for creating an instance of this class. It takes an IncomeExpenseListener object for reporting
     * income/expense results for display. The Type is an enum telling which type this instance represents,
     * an Income or Expense
     * @param listener IncomeExpenseListener object for reporting income/expense results to the UI
     * @param type what this object is for, one of Type.Icome or Type.Expense
     */
    public IncomeExpenseViewer(IncomeExpenseListener listener, Type type)
    {
        queries = new Queries();
        this.type = type;
        this.listener = listener;
        String start = DateUtil.convertDateToStartOfDay(LocalDate.now());
        String end = DateUtil.convertDateToEndOfDay(LocalDate.now());
        queryResultListener = new QueryResultListener<JSONArray>()
        {
            @Override
            public void doAction(JSONArray result)
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        totalIncomeValue = 0;
                        IncomeRowItem rowItem[] = new IncomeRowItem[result.length()];

                        for(int i=0; i< rowItem.length; i++)
                        {
                            rowItem[i] = new IncomeRowItem(result.getJSONObject(i));
                            totalIncomeValue+=Double.parseDouble(rowItem[i].getAmountPaid());
                        }
                        displayResults(rowItem);
                    }
                });
            }
        };
        if(type == Type.Income)
            queries.getIncomeForDate(start,end,queryResultListener);
        else
            queries.getExpenseForDate(start,end,queryResultListener);
    }

    /**
     *method called to query the database for income records for a date range
     * @param start start of date range
     * @param end end of date range
     */
    public void displayIncomeForDate(String start,String end)
    {
        queries.getIncomeForDate(start,end,queryResultListener);
    }

    /**
     *method called to query the database for expense records for a date range
     * @param start start of date range
     * @param end end of date range
     */
    public void  displayExpenseForDate(String start,String end)
    {
        queries.getExpenseForDate(start,end,queryResultListener);
    }

    /**
     * display results after querying the database
     * @param rowItems array of IncomeRowItem objects to be displayed on the income/expense tables
     */
    private void displayResults(IncomeRowItem[] rowItems)
    {
        tableView.getItems().clear();
        tableView.getItems().addAll(rowItems);
        if(type == Type.Income)
            listener.setIncome(totalIncomeValue);
        else
            listener.setExpense(totalIncomeValue);
        //totalIncome.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalIncomeValue)));
    }

    /**
     * method called to get retrieve the parent node containing UI components for displaying income/expense data
     * @return parent node for income/expense
     */
    public Node getUiNode()
    {
        if (bPane == null)
        {
            bPane = new BorderPane();
            String[] columns = new String[]{"Name","Purpose","Amount"};
            String[] property = new String[]{"name","purpose","amountPaid"};
            RRTable<IncomeRowItem> rrTable = new RRTable<>(columns,property);
            tableView = rrTable.createTable(false);
            rrTable.setCustomCellFactoy(2,false);

            HBox hBox1 = new HBox();
            hBox1.setPadding(new Insets(10,0,0,0));
            NumberBinding widthBinding = hBox1.widthProperty().multiply(0.3);
            for (int i=0;i<2;i++)
            {
                Label label = new Label();
                //label.setStyle("-fx-background-color:blue");
                label.prefWidthProperty().bind(widthBinding);
                hBox1.getChildren().add(label);
            }
            /*totalIncome = new MoneyDisplayNode(false);
            ((HBox)totalIncome.createNode()).prefWidthProperty().bind(widthBinding);
            totalIncome.getLabel().setText(MoneyFormatter.formatMoney(Double.toString(totalIncomeValue)));
            hBox1.setAlignment(Pos.CENTER_RIGHT);
            hBox1.getChildren().add(totalIncome.createNode());*/

            Label label = null;
            if(type == Type.Income)
                 label = new Label("Income");
            else
                label = new Label("Expense");
            label.setFont(new Font("Arial",20));

            bPane.setCenter(tableView);
            bPane.setBottom(hBox1);
            bPane.setTop(label);
            rrTable.setColumnWidth(3);
        }

        return bPane;
    }
}
