package org.excelsi.solace;


import javafx.scene.Node;


@FunctionalInterface
public interface Painter {
    Node paint(Node n);
}
