package org.excelsi.solace;


import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.scene.layout.BorderPane;
import javafx.application.Platform;


public class UrlRenderer implements JfxRenderer {
    public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
        BorderPane b = new BorderPane();
        Platform.runLater(()->{
            URL u = (URL) o;
            WebView v = new WebView();
            v.getEngine().load(u.toString());
            b.setCenter(v);
        });
        return b;
    }
}
