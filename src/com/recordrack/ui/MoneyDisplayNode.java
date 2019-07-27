package com.recordrack.ui;


import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * This class creates a custom node which contains a label containing a naira sign and either a textField or label which would be displayed
 * as a custom component for a table cell
 */
public class MoneyDisplayNode
{
    private Node node;
    private HBox hBox;
    private boolean editable;
    //private boolean isForMoney;

    /**
     * constructor for MoneyDisplayNode
     * @param isEditable if this is true, the MoneyDisplayNode would contain a textfield else it'd contain a non-editable label
     */
    public MoneyDisplayNode(boolean isEditable)
    {
        this.editable = isEditable;
        //this.isForMoney = isForMoney;
        createNode();
    }


    /**
     * creates and return a parent node containing either a textfield or a label
     * @return a parent node containing contents to be displayed
     */
    public Node createNode()
    {
        if(hBox == null)
        {
            hBox = new HBox(5);
            Label money = new Label("\u20A6");
            money.setPrefSize(20,20);
            money.setStyle("-fx-background-color: #c0c0c0; -fx-text-fill: white; -fx-background-radius: 5 0 0 5;");
            money.setAlignment(Pos.CENTER);
            //money.setStyle("");
            if(editable)
            {
                node = new TextField();
                ((Control) node).setStyle("; -fx-padding: 0 0 0 5");
            }
            else
            {
                node = new Label();
                ((Control) node).setStyle("; -fx-padding: 0 0 0 10");
            }
            //((Control) node).setPrefWidth(Integer.MAX_VALUE);
            DoubleBinding binding = hBox.widthProperty().subtract(20);
            ((Control) node).prefWidthProperty().bind(binding);
            hBox.setPrefHeight(20);
            ((Control) node).setPrefHeight(20);
            hBox.getChildren().addAll(money,node);
        }
        return hBox;
    }

    /**
     * return a reference to the textfield contained in this object
     * @return returns a textfield or null
     */
    public TextField getTextField()
    {
        return (TextField) node;
    }

    /**
     * return a reference to the textfield contained in this object
     * @return returns a label or null
     */
    public Label getLabel()
    {
        return (Label)node;
    }


}
