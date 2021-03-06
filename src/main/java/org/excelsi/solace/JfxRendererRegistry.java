package org.excelsi.solace;


import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextFlow;


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
        if(o==null) return new DefaultRenderer();
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
            System.err.println("found custom renderer for "+o+": "+options);
            r = new CustomizingRenderer(r, options);
            _customizers.remove(o);
        }
        return r;
    }

    public Node render(Object o, Painter p) {
        JfxRenderer r = createRenderer(o);
        //System.err.println("Created renderer "+r+" for "+o);
        return r.render(o, p, this);
    }

    public static JfxRendererRegistry defaultRegistry() {
        JfxRendererRegistry r = new JfxRendererRegistry();
        r.register(o -> { return o instanceof Table; }, new TableRenderer());
        r.register(o -> { return o instanceof List; }, new ListRenderer());
        r.register(o -> { return o instanceof Node; }, new NodeRenderer());
        r.register(o -> { return o instanceof Image; }, new ImageRenderer());
        r.register(o -> { return o instanceof URL; }, new UrlRenderer());
        r.register(o -> { return o instanceof Annotation; }, new AnnotationRenderer());
        r.register(o -> { return o instanceof Text; }, new TextRenderer());
        return r;
    }

    private static class NodeRenderer implements JfxRenderer {
        public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
            return p.paint((Node) o);
        }
    }

    private static class AnnotationRenderer implements JfxRenderer {
        public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
            Annotation a = (Annotation) o;
            Label l = new Label(a.getText(), renderers.render(a.getLabelled(), p));
            if(a.getPos()!=null) {
                l.setContentDisplay(ContentDisplay.valueOf(a.getPos().toUpperCase()));
            }
            return p.paint(l);
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

    private static class TextRenderer implements JfxRenderer {
        private static final Map<String,String> EXPANSIONS = new HashMap<>();
        static {
            EXPANSIONS.put("/","italic");
            EXPANSIONS.put("^","superscript");
            EXPANSIONS.put("v","subscript");
            EXPANSIONS.put("*","bold");
            EXPANSIONS.put("`","normal");
            EXPANSIONS.put("c","console");
        }

        public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
            Text t = (Text) o;
            TextFlow tf = new TextFlow();
            javafx.scene.text.Text pseg = null;
            for(Text.Segment s:t.getSegments()) {
                javafx.scene.text.Text seg = new javafx.scene.text.Text(s.getText());
                String exp = EXPANSIONS.get(s.getType());
                //System.err.println("EXPansion for '"+s.getType()+"': "+exp);
                if(exp!=null) {
                    String style = t.getBase()+"-"+exp;
                    System.err.println("Style for '"+s.getText()+": '"+style+"'");
                    seg.getStyleClass().add(style);
                    double ptrans = pseg!=null?pseg.getTranslateY():0;
                    if("superscript".equals(exp)) {
                        seg.setTranslateY(ptrans + seg.getFont().getSize() * -0.5);
                    }
                    else if("subscript".equals(exp)) {
                        seg.setTranslateY(ptrans + seg.getFont().getSize() * 0.5);
                    }
                }
                pseg = seg;
                tf.getChildren().add(seg); //p.paint(seg));
            }
            return p.paint(tf);
        }
    }

    private static class DefaultRenderer implements JfxRenderer {
        public Node render(Object o, Painter p, JfxRendererRegistry renderers) {
            if(o instanceof Node) {
                return (Node) o;
            }
            return p.paint(new Label(o==null?"(null)":o.toString()));
        }
    }

    private static Node text(String text, Painter p) {
        return p.paint(new Label(text));
    }
}
