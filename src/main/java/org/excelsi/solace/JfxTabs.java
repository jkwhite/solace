package org.excelsi.solace;


import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;


public class JfxTabs extends TabPane implements MetaConsole {
    private ShellFactory _shellFactory;


    public JfxTabs(ShellFactory shellFactory) {
        _shellFactory = shellFactory;
        getStyleClass().add("tabs");
    }

    public void newTerminal() {
        Tab t = new Tab();
        t.setText(Integer.toString(1+getTabs().size()));
        t.setContent(new JfxConsole(_shellFactory.newShell(), _shellFactory.getMetaShell(), this));
        getTabs().add(t);
    }

    public void newWorksheet() {
    }

    public void closeConsole() {
    }
}
