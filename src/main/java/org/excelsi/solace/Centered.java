package org.excelsi.solace;


import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;


public class Centered extends StackPane {
    public Centered(final Node n, final String style) {
        //setCenter(n);
        getChildren().add(n);
        setAlignment(n, Pos.CENTER);
        getStyleClass().add(style);
    }

    public Centered(final Node n) {
        //final HBox h = new HBox();
        final VBox v = new VBox();
        //h.setAlignment(Pos.CENTER);
        v.setAlignment(Pos.CENTER);
        //h.getChildren().add(v);
        v.getChildren().add(n);
        getChildren().add(v);
        setAlignment(v, Pos.CENTER);
        //getChildren().add(n);
        //setAlignment(n, Pos.CENTER);
        //setCenter(n);
    }
}
