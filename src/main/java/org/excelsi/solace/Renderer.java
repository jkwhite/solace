package org.excelsi.solace;


import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import org.apache.log4j.Logger;


public class Renderer {
    private static final Map<Class, Renderer> RENDS = new LinkedHashMap<Class, Renderer>();
    private static final Logger log = Logger.getLogger(Renderer.class);
    protected static final ThreadLocal<Set> _rendered = new ThreadLocal<Set>();
    private static ThreadLocal<Console> _console = new ThreadLocal<Console>();

    static {
        RENDS.put(Sequence.class, new SequenceRenderer());
        RENDS.put(Table.class, new TableRenderer());
        RENDS.put(List.class, new ListRenderer());
        RENDS.put(ImageIcon.class, new ImageIconRenderer());
        RENDS.put(JComponent.class, new JComponentRenderer());
        RENDS.put(Object.class, new DefaultRenderer());
    }

    public static void setConsole(Console c) { _console.set(c); }

    public static JComponent color(JComponent j, String... context) {
        return _console.get().color(j, context);
    }

    public void link(Class clz, Renderer r) {
        DefaultRenderer dr = (DefaultRenderer) RENDS.get(Object.class);
        dr.link(clz, r);
    }

    public void clear() {
        _rendered.set(new HashSet());
    }

    public JComponent render(Object o, String... context) {
        if(_rendered.get().contains(o)) {
            log.info("already added default "+o);
            return color(new JLabel("*"), context);
        }
        for(Map.Entry<Class,Renderer> e:RENDS.entrySet()) {
            if(e.getKey().isAssignableFrom(o.getClass())) {
                JComponent j = e.getValue().render(o, context);
                color(j, context);
                _rendered.get().add(o);
                return j;
            }
        }
        return new JLabel("no renderer for "+o.getClass().getName());
    }

    static class ListRenderer extends Renderer {
        public JComponent render(Object o, String... context) {
            if(_rendered.get().contains(o)) {
                log.info("already added list "+o);
                return color(new JLabel("*"), context);
            }
            _rendered.get().add(o);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            //p.setPreferredSize(new java.awt.Dimension(800,100));
            p.add(color(new JLabel("[")));
            List li = (List) o;
            for(int i=0;i<li.size();i++) {
                final JComponent jc = super.render(li.get(i), context);
                final int j = i;
                jc.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        Console co = console(jc);
                        co.append("$h[\""+co.historyCmd()+"\"].out["+j+"]");
                    }
                });
                p.add(jc);
                if(i<li.size()-1) {
                    p.add(color(new JLabel(","), context));
                }
            }
            p.add(color(new JLabel("]"), context));
            return p;
        }
    }

    static class ArrayRenderer extends Renderer {
        public JComponent render(Object o, String... context) {
            return new ListRenderer().render(Arrays.asList((Object[])o, context));
        }
    }

    static class ImageIconRenderer extends Renderer {
        public JComponent render(Object o, String... context) {
            return new JLabel((ImageIcon)o);
        }
    }

    static class JComponentRenderer extends Renderer {
        public JComponent render(Object o, String... context) {
            return (JComponent) o;
        }
    }

    static class StringRenderer extends Renderer {
        public JComponent render(Object o, String... context) {
            //return new JLabel(o.toString());
            JTextArea t = new JTextArea(o.toString());
            t.setEditable(false);
            return t;
        }
    }

    static class DefaultRenderer extends Renderer {
        private Map<Class,Renderer> _renderers = new LinkedHashMap<Class,Renderer>();


        public void link(Class clz, Renderer r) {
            _renderers.put(clz, r);
        }

        public JComponent render(Object o, String... context) {
            if(_rendered.get().contains(o)) {
                log.info("already added default default "+o);
                return color(new JLabel("*"), context);
            }
            _rendered.get().add(o);
            Renderer r = _renderers.get(o.getClass());
            if(r==null) {
                if(o.getClass().isArray()) {
                    r = new ArrayRenderer();
                }
                else {
                    try {
                        Class c = Class.forName(o.getClass().getName()+"Renderer");
                        r = (Renderer) c.newInstance();
                    }
                    catch(Exception e) {
                        r = new StringRenderer();
                    }
                }
                _renderers.put(o.getClass(), r);
            }
            JComponent c = r.render(o, context);
            return c;
        }
    }

    protected static Console console(java.awt.Container child) {
        while(!(child instanceof Console)) {
            child = child.getParent();
        }
        return (Console) child;
    }
}
