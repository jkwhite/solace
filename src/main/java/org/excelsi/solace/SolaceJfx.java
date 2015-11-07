package org.excelsi.solace;


import javafx.application.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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


public class SolaceJfx extends Application {
    private static JfxMetaConsole _mc;


    public static void setMetaConsole(MetaConsole mc) {
        _mc = (JfxMetaConsole) mc;
    }

    @Override
    public void start(final Stage stage) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        JfxTabs console = new JfxTabs(_mc.getShellFactory());
        Parent root = console;
        _mc.setDelegate(console);

        Scene scene = new Scene(root, 1280, 1024, true, SceneAntialiasing.DISABLED);
        scene.getStylesheets().add("/org/excelsi/solace/solace-default.css");
        scene.setFill(Color.BLACK);
        stage.setX(screen.getMinX());
        stage.setY(screen.getMinY());
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());

        stage.setTitle("Solace");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        _mc.newTerminal();
    }
}
