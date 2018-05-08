package org.excelsi.solace;


import javafx.scene.Node;


public class Styles {
    public static Node s(Node c, String... styles) {
        for(String s:styles) {
            c.getStyleClass().add(s);
        }
        return c;
    }
}
