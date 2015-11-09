package org.excelsi.solace;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;


public class JfxRendererRegistry {
    private final Map<Predicate,JfxRenderer> _renderers = new HashMap<>();


    public void register(Predicate criterion, JfxRenderer renderer) {
        _renderers.put(criterion, renderer);
    }

    public JfxRenderer createRenderer(Object o) {
        for(Map.Entry<Predicate,JfxRenderer> e:_renderers.entrySet()) {
            if(e.getKey().test(o)) {
                return e.getValue();
            }
        }
        return new DefaultRenderer();
    }

    public Node render(Object o, Painter p) {
        JfxRenderer r = createRenderer(o);
        return r.render(o, p, this);
    }

    public static JfxRendererRegistry defaultRegistry() {
        JfxRendererRegistry r = new JfxRendererRegistry();
        r.register(o -> { return o instanceof List; }, new ListRenderer());
        r.register(o -> { return o instanceof Node; }, new NodeRenderer());
        r.register(o -> { return o instanceof Image; }, new ImageRenderer());
        return r;
    }

    private static class NodeRenderer implements JfxRenderer {
        public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
            return p.paint((Node) o);
        }
    }

    private static class ImageRenderer implements JfxRenderer {
        public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
            return p.paint(new ImageView((Image)o));
        }
    }

    private static class ListRenderer implements JfxRenderer {
        public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
            List list = (List) o;
            HBox b = new HBox();
            b.getChildren().add(text("[", p));
            for(int i=0;i<list.size();i++) {
                b.getChildren().add(renderers.createRenderer(list.get(i)).render(list.get(i), p, renderers));
                if(i<list.size()-1) {
                    b.getChildren().add(text(", ", p));
                }
            }
            b.getChildren().add(text("]", p));
            return p.paint(b);
        }
    }

    private static class DefaultRenderer implements JfxRenderer {
        public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
            return p.paint(new Label(o==null?"(null)":o.toString()));
        }
    }

    private static Node text(String text, Painter p) {
        return p.paint(new Label(text));
    }
}
