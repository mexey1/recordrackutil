package com.recordrack.ui;

import com.recordrack.logic.MoneyFormatter;
import javafx.scene.control.TableCell;

/**
 * The NonEditableCell class extends a TableCell whose content is set to a custom node(MoneyDisplayNode)
 * @param <T> the type of the TableView containing this cell
 * @param <E> the value to be contained in the cell
 */
public class NonEditableCell<T,E> extends TableCell<T,E>
{
    MoneyDisplayNode node;
    public NonEditableCell()
    {
        super();
        node = new MoneyDisplayNode(false);
        setGraphic(node.createNode());
    }

    /**
     * overridden to update the content of the cell
     * @param value value of the cell
     * @param empty cell is empty or not
     */
    @Override
    public void updateItem(E value, boolean empty)
    {
        super.updateItem(value,empty);
        //System.out.println("non editable "+value);
        //if the cell is empty, set text and graphic as null
        if(empty)
        {
            setText(null);
            setGraphic(null);
        }
        else
        {
            //if the cell isn't empty, we'd like to set text as null and create a MoneyDisplayNode and set the value as
            //the monetary value to be displayed
            setText(null);
            node.createNode();
            node.getLabel().setText(value==null?"":MoneyFormatter.formatMoney(value.toString()));
            setGraphic(node.createNode());
        }
        //node.getLabel().setText(MoneyFormatter.formatMoney((String)value));
    }
}
