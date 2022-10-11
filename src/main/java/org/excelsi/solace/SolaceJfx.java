package org.excelsi.solace;


import java.io.File;
import java.io.IOException;

import javafx.stage.FileChooser;
import javafx.application.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.geometry.Pos;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCombination;


public class SolaceJfx extends Application {
    private static JfxMetaConsole _mc;


    public static void setMetaConsole(MetaConsole mc) {
        _mc = (JfxMetaConsole) mc;
    }

    @Override
    public void start(final Stage stage) {
        stage.setTitle("Solace");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Solace");

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        JfxTabs tabs = new JfxTabs(_mc.getShellFactory());
        _mc.setDelegate(tabs);
        BorderPane root = new BorderPane();
        root.setCenter(tabs);
        root.setTop(createMenu(stage, tabs));

        Scene scene = new Scene(root, 1280, 1024, true, SceneAntialiasing.BALANCED);
        scene.getStylesheets().add("/META-INF/solace/solace-default.css");
        String usercss = _mc.getShellFactory().getMetaShell().getUserStylesheetUrl();
        if(usercss!=null) {
            //scene.setUserAgentStylesheet(usercss);
            scene.getStylesheets().add(usercss);
        }
        scene.setFill(Color.BLACK);
        stage.setX(screen.getMinX());
        stage.setY(screen.getMinY());
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());

        stage.setTitle("Solace");
        //stage.setFullScreen(true);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        _mc.newTerminal();
    }

    private Node createMenu(final Stage stage, final MetaConsole mc) {
        Menu shell = new Menu("Shell");
        MenuItem newc = new MenuItem("New Tab");
        newc.setAccelerator(KeyCombination.keyCombination("Shortcut+T"));
        newc.setOnAction((e)->{ mc.newTerminal(); });
        MenuItem closec = new MenuItem("Close");
        closec.setOnAction((e)->{ mc.closeConsole(); });
        closec.setAccelerator(KeyCombination.keyCombination("Shortcut+W"));
        shell.getItems().addAll(newc, closec);

        Menu edit = new Menu("Edit");
        MenuItem cut = new MenuItem("Cut");
        cut.setAccelerator(KeyCombination.keyCombination("Shortcut+X"));
        cut.setOnAction((e)->{ mc.cutSelection(); });
        MenuItem copy = new MenuItem("Copy");
        copy.setAccelerator(KeyCombination.keyCombination("Shortcut+C"));
        copy.setOnAction((e)->{ mc.copySelection(); });
        MenuItem paste = new MenuItem("Paste");
        paste.setAccelerator(KeyCombination.keyCombination("Shortcut+V"));
        paste.setOnAction((e)->{ mc.pasteBuffer(); });
        edit.getItems().addAll(cut, copy, paste);

        Menu view = new Menu("View");
        MenuItem fullsc = new MenuItem("Full Screen");
        fullsc.setAccelerator(KeyCombination.keyCombination("Shortcut+ENTER"));
        fullsc.setOnAction((e)->{ stage.setFullScreen(!stage.isFullScreen()); });
        view.getItems().addAll(fullsc);

        Menu window = new Menu("Window");
        MenuItem shiftr = new MenuItem("Next Tab");
        shiftr.setAccelerator(KeyCombination.keyCombination("Shortcut+RIGHT"));
        shiftr.setOnAction((e)->{ mc.nextTerminal(); });
        MenuItem shiftl = new MenuItem("Prev Tab");
        shiftl.setAccelerator(KeyCombination.keyCombination("Shortcut+LEFT"));
        shiftl.setOnAction((e)->{ mc.prevTerminal(); });
        MenuItem screenshot = new MenuItem("Screenshot ...");
        screenshot.setOnAction((e)->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Screenshot");
            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile != null) {
                String ss = selectedFile.toString();
                if(!ss.endsWith(".png")) {
                    ss = ss+".png";
                }
                _mc.screenshot(ss);
            }
        });
        window.getItems().addAll(shiftr, shiftl, screenshot);

        MenuBar mb = new MenuBar();
        mb.setUseSystemMenuBar(true);
        mb.getMenus().addAll(shell, edit, view, window);
        return mb;
    }
}
