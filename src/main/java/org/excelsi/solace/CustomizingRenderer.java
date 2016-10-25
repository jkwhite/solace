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
import javafx.geometry.Pos;


public class CustomizingRenderer implements JfxRenderer {
    private final JfxRenderer _delegate;
    private final Map<String,Object> _options;


    public CustomizingRenderer(JfxRenderer delegate, Map<String,Object> options) {
        _delegate = delegate;
        _options = options;
    }

    public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
        Node n = _delegate.render(o, p, renderers);
        if(_options.containsKey("label")) {
            n = new Label(_options.get("label").toString(), n);
        }
        for(Map.Entry<String,Object> e:_options.entrySet()) {
            switch(e.getKey()) {
                case "center":
                    break;
                case "rotate":
                    n.setRotate((Integer)e.getValue());
                    break;
                case "style":
                    for(final String s:e.getValue().toString().split(",")) {
                        n.getStyleClass().add(s);
                    }
                    break;
                case "label":
                    break;
                default:
            }
        }
        if(_options.containsKey("center")) {
            if(n instanceof GridPane) {
                ((GridPane)n).setAlignment(Pos.CENTER);
            }
            /*
            if(n instanceof GridPane) {
                ((GridPane)n).setAlignment(Pos.CENTER);
            }
            else {
                BorderPane bp = new BorderPane();
                bp.setCenter(n);
                n = bp;
            }
            */
            n = new Centered(n);
        }
        return n;
    }
}
