package org.excelsi.solace;


import java.util.Collection;
import java.util.Collections;

import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


public class RTInput extends BorderPane implements Input {
    private final CodeArea _c;


    public RTInput() {
        getStyleClass().add("input");
        _c = new CodeArea();
        //_c.getStyleClass().add("input");
        //getChildren().add(_c);
        _c.setStyleClass(0, _c.getLength(), "input");
        _c.setBackground(null);
        _c.setWrapText(true);
        setCenter(_c);
        _c.richChanges().subscribe(change -> {
            updateStyles();
        });
        //_c.caretPositionProperty().addListener((o, oldv, newv) -> {
            //updateStyles();
        //});
    }

    private void updateStyles() {
        int pos = _c.getCaretPosition();
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        spansBuilder.add(Collections.singleton("input"), pos-1);
        spansBuilder.add(Collections.singleton("cursor"), 1);
        spansBuilder.add(Collections.singleton("input"), _c.getLength()-pos);
        _c.setStyleSpans(0, spansBuilder.create());
    }

    @Override public String getText() {
        return _c.getText();
    }

    @Override public void setLine(String s) {
        _c.replaceText(0, _c.getLength(), s);
    }

    @Override public void append(Object o) {
    }

    @Override public void backspace() {
    }

    @Override public void left() {
    }

    @Override public void right() {
    }

    @Override public int getPos() {
        return 0;
    }

    @Override public void setPos(int i) {
    }

    @Override public String stringify() {
        return _c.getText();
    }

    @Override public void freeze() {
    }
}
