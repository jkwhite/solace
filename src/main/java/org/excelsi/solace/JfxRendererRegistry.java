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


public class JfxRendererRegistry {
    private final Map<Predicate,JfxRenderer> _renderers = new LinkedHashMap<>();
    private final Map<Object,Map<String,Object>> _customizers = new HashMap<>();


    public void register(Predicate criterion, JfxRenderer renderer) {
        _renderers.put(criterion, renderer);
    }

    public Object remember(Object o, Map options) {
        Map collected = _customizers.get(o);
        if(collected==null) {
            collected = new HashMap<>();
            _customizers.put(o, collected);
        }
        for(Object eo:options.entrySet()) {
            Map.Entry e = (Map.Entry)eo;
            collected.put(e.getKey(), e.getValue());
        }
        return o;
    }

    public JfxRenderer createRenderer(Object o) {
        JfxRenderer r = null;
        for(Map.Entry<Predicate,JfxRenderer> e:_renderers.entrySet()) {
            if(e.getKey().test(o)) {
                r = e.getValue();
                break;
            }
        }
        if(r==null) {
            r = new DefaultRenderer();
        }
        Map options = _customizers.get(o);
        if(options!=null) {
            r = new CustomizingRenderer(r, options);
            _customizers.remove(o);
        }
        return r;
    }

    public Node render(Object o, Painter p) {
        JfxRenderer r = createRenderer(o);
        return r.render(o, p, this);
    }

    public static JfxRendererRegistry defaultRegistry() {
        JfxRendererRegistry r = new JfxRendererRegistry();
        r.register(o -> { return o instanceof Table; }, new TableRenderer());
        r.register(o -> { return o instanceof List; }, new ListRenderer());
        r.register(o -> { return o instanceof Node; }, new NodeRenderer());
        r.register(o -> { return o instanceof Image; }, new ImageRenderer());
        r.register(o -> { return o instanceof URL; }, new UrlRenderer());
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

    private static class TableRenderer implements JfxRenderer {
        public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
            Table t = (Table) o;
            GridPane g = new GridPane();
            if(t.getGrid()!=null) {
                g.setGridLinesVisible(t.getGrid());
            }
            if(t.getMargin()!=null) {
                g.setHgap(t.getMargin());
                g.setVgap(t.getMargin());
            }
            int col = 0, row = 0;
            for(Object child:t) {
                g.add(renderers.createRenderer(child).render(child, p, renderers), col, row);
                if(++col==t.getCols()) {
                    col = 0;
                    ++row;
                }
            }
            return p.paint(g);
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
