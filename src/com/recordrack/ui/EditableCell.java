package com.recordrack.ui;

import com.recordrack.logic.MoneyFormatter;
import com.recordrack.db.CostPriceRowItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;

/**
 * Cell whose content is editable
 * @param <T> Type of data contained in the TableView
 * @param <E> Type of data contained in the cell
 */
public class EditableCell<T,E> extends TableCell<T,E>
{
    private MoneyDisplayNode node;
    private MoneyDisplayNode nonEditableNode;
    private boolean shouldImplementFocusListener, shouldImplementTextChangeListener;

    public EditableCell()
    {
        super();
        nonEditableNode = new MoneyDisplayNode(false);
        nonEditableNode.createNode();
        setText(null);
        setGraphic(nonEditableNode.createNode());
    }


    /**
     * this method is overridden to monitor when the user double clicks the cell to edit the text field contained therein.
     * The method creates a new node containing a textfield the user can type into.
     */
    @Override
    public void startEdit()
    {
        if(!isEmpty())
        {
            //System.out.println("Starting edit");
            super.startEdit();
            createCellContent();
            setText(null);
            setGraphic(node.createNode());
            node.getTextField().setText(getString());
            node.getTextField().selectAll();
        }
    }

    /**
     * this method is overridden to update the cell during table creation or user updates a field and commits changes
     * by pressing ENTER
     * @param item the new value to update to
     * @param empty boolean value depicting the cell as empty or not
     */
    @Override
    public void updateItem(E item,boolean empty)
    {
        //System.out.println("updating "+empty+" "+(getItem()==null?getItem():getItem().getClass()));
        super.updateItem(item,empty);
        //System.out.println("trying Is editing "+isEditing());
        //setText(item==null?"n":item.toString());

        if(empty)
        {
            setText(null);
            setGraphic(null);
        }
        else if(isEditing())
        {
            if(node!=null)
                node.getTextField().setText(getString());
            setText(null);
            setGraphic(node.createNode());
        }
        else
        {
            setText(null);
            nonEditableNode.createNode();
            nonEditableNode.getLabel().setText(item==null?"":MoneyFormatter.formatMoney(item.toString()));
            setGraphic(nonEditableNode.createNode());
        }
    }

    /**
     * this method is called from within the startEdit method to create and display cell content
     * containing a textfield
     */
    private void createCellContent()
    {
        node = new MoneyDisplayNode(true);//.getTextField();

        //we create a handler to listen for when the user presses ENTER key.
        //when ENTER is pressed, we go on to commit edit, update the data model with new data entered by user
        //and refresh the table to display updated data in the model backing the table
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                EditableCell.super.commitEdit((E)node.getTextField().getText());
                //commitEdit((E)node.getTextField().getText());
                //setGraphic(null);
                double res = MoneyFormatter.unFormatMoney(node.getTextField().getText());
                ((CostPriceRowItem)getTableView().getItems().get(getTableRow().getIndex())).setNewUnitCost(Double.toString(res));
                setGraphic(null);
                setText(node.getTextField().getText());
                getTableView().refresh();
                commitEdit((E)node.getTextField().getText());
            }
        };
        node.getTextField().setOnAction(handler);
    }

    /**
     * convenience method for getting a string representation of the content of the cell
     * @return string representation of the content of the cell
     */
    private String getString()
    {
        return getItem()==null?"": MoneyFormatter.formatMoney(getItem().toString());
    }

}
