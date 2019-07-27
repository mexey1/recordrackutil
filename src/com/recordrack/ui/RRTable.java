package com.recordrack.ui;

import com.recordrack.db.CostPriceRowItem;
import com.recordrack.interfaces.RowItem;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Region;
import javafx.util.Callback;

/**
 * All tables in this application are created using this class. It contains methods for tweaking the appearance/performance
 * of the associated table
 * @param <T> the content type for the table
 */
public class RRTable<T>
{
    private String columns[] = null;
    private String property[] = null;
    private TableView<T> tableView;

    /**
     * constructor for this class
     * @param columns an array containing names for the columns
     * @param property an array containing properties that that are called from the PropertyValue bean
     */
    public RRTable(String[]columns, String [] property)
    {
        this.columns = columns;
        this.property = property;
    }

    /**
     * method to create a table view
     * @param isEditable should the table be editable or not
     * @return a reference to the created table
     */
    public TableView<T> createTable(boolean isEditable)
    {
        tableView = new TableView<>();
        int i=0;
        int j = 0;
        if(isEditable)
            tableView.setEditable(true);
        for(;i<columns.length;i++)
        {
            TableColumn<T,String> column = new TableColumn(columns[i]);
            if(i<property.length)
                column.setCellValueFactory(new PropertyValueFactory<>(property[i]));
            tableView.getColumns().add(column);
        }
        tableView.setPadding(new Insets(10,10,10,10));
        return tableView;
    }

    /**
     * this method is called to bind the unit cost price to the total cost price
     * @param index the column index for the dependent column
     */
    public void bindUnitCostPriceToTotal(int index)
    {
        TableColumn column = tableView.getColumns().get(index);
        /*column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<T, String> param)
            {
                CostPriceRowItem p = (CostPriceRowItem)param.getValue();
                DoubleBinding binding = p.getUnitCostProperty().multiply(Double.parseDouble(p.getNewQuantity()));
                SimpleStringProperty stringProperty = new SimpleStringProperty(Double.toString(binding.getValue()));
                p.getTotalCost().bind(stringProperty);
                return p.getTotalCost();
            }
        });*/
    }

    /**
     * call this method to use the custom a factory
     * @param index the index of the column to use a custom factory
     * @param isEditable should the column be editable
     */
    public void setCustomCellFactoy(int index, boolean isEditable)
    {
        TableColumn column = tableView.getColumns().get(index);
        column.setCellFactory(new Callback<TableColumn<T, String>, TableCell<T, String>>()
        {
            @Override
            public TableCell<T, String> call(TableColumn<T, String> param)
            {
                if(isEditable)
                    return new EditableCell();
                else
                    return new NonEditableCell();
            }
        });
    }

    /**
     * method to set a column as an editable. A textfield is displayed when user enters edit mode
     * @param index the index of the column to make editable
     */
    public void setCellAsTextfieldCell(int index)
    {
        TableColumn column = tableView.getColumns().get(index);
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event)
            {
                ((RowItem)tableView.getItems().get(event.getTablePosition().getRow())).setNewQuantity((String)event.getNewValue());
                tableView.refresh();
            }
        });
    }

    /**
     * method to make the width of columns of this table equal.
     * @param numberOfColumns the number of columns in this table
     */
    public void setColumnWidth(int numberOfColumns)
    {
        //we bind each column width to changes in the width of the parent container (bPane in this case)
        Parent node = tableView.getParent();
        double other = (double)1/numberOfColumns;
        System.out.println(other);
        NumberBinding numberBinding = ((Region)node).widthProperty().multiply(other);
        for(TableColumn column:tableView.getColumns())
        {
            column.prefWidthProperty().bind(numberBinding);
        }
    }
}
