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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;


public class JfxTabs extends TabPane implements MetaConsole {
    private ShellFactory _shellFactory;


    public JfxTabs(ShellFactory shellFactory) {
        _shellFactory = shellFactory;
        getStyleClass().add("tabs");
        getStyleClass().add("console");
        getTabs().addListener((ListChangeListener) change -> {
            final StackPane header = (StackPane) lookup(".tab-header-area");
            if(header!=null) {
                if(getTabs().size() == 1) {
                    header.setPrefHeight(0);
                }
                else {
                    header.setPrefHeight(-1);
                }
            }
        });
    }

    @Override public void nameTerminal(String name) {
        if(Platform.isFxApplicationThread()) {
            getSelectionModel().getSelectedItem().setText(name);
        }
        else {
            Platform.runLater(()->{ nameTerminal(name); });
        }
    }

    @Override public void newTerminal() {
        Tab t = new Tab();
        t.getStyleClass().add("tabs");
        t.setText(Integer.toString(1+getTabs().size()));
        JfxConsole cons = new JfxConsole(_shellFactory.newShell(), _shellFactory.getMetaShell(), this);
        //BorderPane bp = new BorderPane();
        //bp.getStyleClass().add("console");
        //bp.setTop(cons);
        //ScrollPane sp = new ScrollPane();
        //sp.getStyleClass().add("console");
        //bp.setCenter(sp);
        t.setContent(cons);
        getTabs().add(t);
        getSelectionModel().select(t);
        cons.init();
        cons.requestFocus();
    }

    @Override public void nextTerminal() {
        getSelectionModel().select((getSelectionModel().getSelectedIndex()+1)%getTabs().size());
        focusConsole();
    }

    @Override public void prevTerminal() {
        int idx = getSelectionModel().getSelectedIndex()-1;
        getSelectionModel().select(idx>=0?idx:getTabs().size()-1);
        focusConsole();
    }

    public void newWorksheet() {
    }

    public void closeConsole() {
        Tab t = getSelectionModel().getSelectedItem();
        if(t!=null) {
            getTabs().remove(t);
        }
        focusConsole();
    }

    @Override public void cutSelection() {
        currentConsole().cutSelection();
    }

    @Override public void copySelection() {
        currentConsole().copySelection();
    }

    @Override public void pasteBuffer() {
        currentConsole().pasteBuffer();
    }

    private JfxConsole currentConsole() {
        return (JfxConsole) getSelectionModel().getSelectedItem().getContent();
    }

    private void focusConsole() {
        Tab nt = getSelectionModel().getSelectedItem();
        if(nt!=null) {
            nt.getContent().requestFocus();
        }
    }
}
