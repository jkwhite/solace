package org.excelsi.solace;


import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import org.apache.commons.beanutils.BeanMap;


public abstract class Console extends JComponent implements KeyListener, CaretListener {
    public static final Font FONT = Font.decode(Font.MONOSPACED+"-14");
    private Map<String,Runnable> _keyBindings = new HashMap<String,Runnable>();
    private Map<String,Map<String,ColorHashMap>> _colors = new HashMap<String,Map<String,ColorHashMap>>();
    protected final Shell _shell;
    protected final MetaShell _ms;
    protected final MetaConsole _m;
    protected final Renderer _r = new Renderer();
    private static Color fg = Color.BLACK;
    private static Color bg = Color.WHITE;
    private String _colorscheme = "*";


    public Console(Shell s, MetaShell ms, MetaConsole m) {
        _ms = ms;
        _m = m;
        _shell = s;
        _shell.setVariable("$w", _m);
        _shell.setVariable("$c", this);
        _shell.setVariable("$r", _r);
    }

    public void init() {
        Renderer.setConsole(this);
        _shell.init();
    }

    //public void setFg(Color c) {
        //fg = c;
        //color(_north);
    //}
//
    //public Color getFg() { return fg; }
//
    //public void setBg(Color c) {
        //bg = c;
        //color(_north);
    //}

    //public Color getBg() { return bg; }

    public void setColorscheme(String c) {
        try {
            _shell.evalScript(_ms.getColorscheme(c));
            _colorscheme = c;
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getColorscheme() { return _colorscheme; }

    public Map<String,Map<String,ColorHashMap>> getColors() { return _colors; }

    public Renderer getRenderer() { return _r; }

    public abstract void clear();

    public abstract void hideSelect();

    public abstract void select(List items);

    public abstract void append(String s);

    public abstract String historyCmd();

    public abstract void focus();

    public synchronized JComponent render(Object o, String... context) {
        _r.clear();
        Renderer.setConsole(this);
        return _r.render(o, context);
    }

    public JComponent color(JComponent j, String... styles) {
        Map<String,ColorHashMap> mcm = _colors.get(_colorscheme);
        if(mcm==null) {
            mcm = _colors.get("*");
        }
        String key = j.getClass().getSimpleName();
        Map cm = null;
        if(styles!=null) {
            for(String style:styles) {
                cm = mcm.get(key+"."+style);
                if(cm==null) {
                    cm = mcm.get("*."+style);
                }
                if(cm!=null) break;
            }
        }
        if(cm==null) {
            cm = mcm.get(key+".*");
            if(cm==null) {
                cm = mcm.get("*");
                if(cm==null) {
                    cm = _colors.get("*").get("*");
                }
            }
        }
        BeanMap b = new BeanMap(j);
        for(Object o:cm.entrySet()) {
            Map.Entry e = (Map.Entry) o;
            if(b.containsKey(e.getKey())) {
                Object v = e.getValue();
                v = interpret(v);
                b.put(e.getKey(), v);
            }
        }
        //j.setBackground(bg);
        //j.setForeground(fg);
        j.setFont(FONT);
        return j;
    }

    protected Object process(String cmd) {
        try {
            Object o = _shell.evaluate(cmd);
            return o;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void execute();

    public Map<String, Runnable> getKeybindings() { return _keyBindings; }
    public void bindkey(String key, Runnable r) {
        _keyBindings.put(key, r);
    }

    public void keyPressed(KeyEvent e) {
        // user-bound keys
        StringBuilder kb = new StringBuilder();
        if(e.isControlDown()) {
            kb.append("C-");
        }
        if(Character.isLetterOrDigit(e.getKeyChar())||e.getKeyChar()=='\t') {
            kb.append(e.getKeyChar());
        }
        else {
            kb.append(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase());
        }
        //System.err.println("ISO: "+Character.isISOControl(e.getKeyCode()));
        //System.err.println("ID: "+e.getKeyCode());
        //System.err.println("CHR: '"+e.getKeyChar()+"'");
        //System.err.println("CHRI: '"+(int)e.getKeyChar()+"'");
        //System.err.println("TXT: "+KeyEvent.getKeyText(e.getKeyCode()));
        //System.err.println("LEY: "+kb);
        final Runnable r = _keyBindings.get(kb.toString());
        if(r!=null) {
            e.consume();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    r.run();
                }
            });
        }
    }

    public abstract void brk();

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    protected static Object interpret(Object vs) {
        if(vs instanceof String) {
            Color c = null;
            String v = (String) vs;
            if(v.startsWith("#")) {
                float[] rgba = new float[4];
                for(int i=0;i<rgba.length;i++) {
                    rgba[i] = Integer.parseInt(v.substring(1+2*i, 1+2*i+2), 16)/255f;
                }
                c = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
            }
            else {
                try {
                    c = (Color) Color.class.getField(v).get(null);
                }
                catch(Exception e) {
                    System.err.println(e.toString());
                    c = Color.WHITE;
                }
            }
            return c;
        }
        else {
            return vs;
        }
    }
}
