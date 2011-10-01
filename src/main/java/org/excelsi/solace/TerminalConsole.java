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


public class TerminalConsole extends Console {
    private String PROMPT = "> ";
    private Map<String,Command> _history = new LinkedHashMap<String,Command>();
    private int _maxLines = 200;
    private JTextArea _current;
    private JPanel _root, _north;
    private JScrollPane _scroll;
    private long _histpos = 0;
    private long _minHistory = 0;
    private long _nextHistory = 0;
    private JPanel _select = new JPanel();
    private Image _image;


    public TerminalConsole(Shell s, MetaShell ms, MetaConsole m) {
        super(s, ms, m);
        _shell.setVariable("$h", _history);
        setLayout(new BorderLayout());
        _root = new JPanel();
        _root.setLayout(new BoxLayout(_root, BoxLayout.Y_AXIS));
        _north = new JPanel(new BorderLayout());
        _north.add(_root, BorderLayout.NORTH);
        _scroll = new JScrollPane(_north, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //color(p);
        add(_scroll);
        /*
        try {
            _image = ImageIO.read(new File("/Users/jkwhite/Pictures/m45_volskiy_1300.jpg"));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        */
    }

    public void init() {
        super.init();
        color(_root);
        color(_north);
        addLine();
        addHistory("");
    }

    public void paint(Graphics g) {
        super.paint(g);
        //g.drawImage(_image, 0, 0, null);
    }

    public String prompt() {
        //return "> ";
        return _shell.evaluate("prompt()").toString();
    }

    public void clear() {
        _root.removeAll();
        color(_north);
    }

    public void hideSelect() {
        _select.setVisible(false);
    }

    public void select(List items) {
        hideSelect();
        _select.removeAll();
        _select.add(render(new Table(4, items), "choice"));
        _select.setSize(_select.getPreferredSize());
        if(_select.getParent()==null) {
            getRootPane().getLayeredPane().add(color(_select), new Integer(0), 0);
            getRootPane().getLayeredPane().moveToFront(_select);
        }
        Point p = _current.getLocation();
        p = SwingUtilities.convertPoint(_current, 0, 24, getRootPane().getLayeredPane());
        _select.setLocation(p.x, p.y);
        _select.setVisible(true);
    }

    public void focus() {
        _current.requestFocusInWindow();
    }

    public void addLine() {
        JTextArea a = new JTextArea(1, 80);
        //a.setCaretColor(getFg());
        color(a, "input");
        if(_current!=null) {
            _current.removeKeyListener(this);
            _current.removeCaretListener(this);
            _current.setEditable(false);
        }
        a.setText("");
        a.setCaretPosition(a.getText().length());
        a.setEditable(true);
        JPanel p = (JPanel) color(new JPanel(new BorderLayout()), "input");
        p.add(color(new JLabel(prompt()), "prompt", "input"), BorderLayout.WEST);
        p.add(a, BorderLayout.CENTER);
        addRoot(p);
        _current = a;
        _current.addKeyListener(this);
        _current.addCaretListener(this);
        _current.requestFocusInWindow();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                _scroll.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
            }
        });
    }

    public void append(String s) {
        _current.setText(_current.getText()+s);
        _current.setCaretPosition(_current.getText().length());
    }

    public synchronized JComponent render(Object o, String... context) {
        _r.clear();
        Renderer.setConsole(this);
        return _r.render(o, context);
    }

    public void addOutput(Object o) {
        if(o!=null) {
            JPanel w = (JPanel) color(new JPanel(new BorderLayout()));
            JPanel c = (JPanel) color(new JPanel(new BorderLayout()));
            w.add(render(o), BorderLayout.CENTER);
            int wi = _scroll.getWidth();
            c.add(w, BorderLayout.WEST);
            addRoot(c);
            c.invalidate();
            Dimension d = c.getPreferredSize();
            //System.err.println("cd: "+d);
            //System.err.println("scro: "+wi);
            //System.err.println("scro2: "+_scroll.getPreferredSize());
            //c.setPreferredSize(new Dimension(wi, d.height));
            //c.invalidate();
            //System.err.println("c3: "+c.getPreferredSize());
            setHistoryOut(_nextHistory-1, o);
        }
    }

    private void addRoot(JComponent c) {
        _root.add(c);
        if(_root.getComponentCount()>_maxLines) {
            _root.remove(0);
        }
    }

    private JComponent createLine() {
        JTextArea t = new JTextArea(1, 80);
        //t.setCaretColor(getFg());
        return color(t);
    }

    protected void execute() {
        hideSelect();
        //_current.setText(_current.getText().substring(0, _current.getText().length()-1));
        _current.setText(_current.getText().replaceAll("\\n", ""));
        String cmd = _current.getText().trim();
        if(!("".equals(cmd))) {
            setHistory(_nextHistory-1, cmd);
            Object o;
            try {
                o = process(cmd);
            }
            catch(Exception e) {
                e.printStackTrace();
                o = e.getMessage();
            }
            addOutput(o);
            addHistory("");
            _histpos = _nextHistory-1;
        }
        addLine();
    }

    public void caretUpdate(CaretEvent e) {
        //if(e.getDot()<PROMPT.length()&&_current.getText().length()>=PROMPT.length()) {
            //_current.setCaretPosition(PROMPT.length());
        //}
    }

    public String getCurrentLine() { return _current.getText(); }
    public int getCaretPosition() { return _current.getCaretPosition(); }
    public String getLineToCaret() { return getCurrentLine().substring(0,getCaretPosition()); }

    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if(_histpos==_nextHistory) {
                    setHistory(_nextHistory-1, _current.getText());
                }
                if(--_histpos<0) {
                    _histpos = 0;
                }
                _current.setText(getHistory(_histpos).getIn());
                _current.setCaretPosition(getHistory(_histpos).getIn().length());
                e.consume();
                break;
            case KeyEvent.VK_DOWN:
                if(++_histpos>=_nextHistory) {
                    _histpos = _nextHistory-1;
                }
                _current.setText(getHistory(_histpos).getIn());
                _current.setCaretPosition(getHistory(_histpos).getIn().length());
                e.consume();
                break;
            case KeyEvent.VK_E:
                if(e.isControlDown()) {
                    _current.setCaretPosition(_current.getText().length());
                    e.consume();
                }
                break;
            case KeyEvent.VK_A:
                if(e.isControlDown()) {
                    _current.setCaretPosition(0);
                    e.consume();
                }
                break;
        }
        super.keyPressed(e);
    }

    public void brk() {
        hideSelect();
        addLine();
    }

    public void keyTyped(KeyEvent e) {
        //System.err.println("KEY: "+e);
        //System.err.println("KEYCODE: "+e.getKeyCode());
        //System.err.println("KEYCHARINT: "+((int) e.getKeyChar()));
        super.keyTyped(e);
        switch(e.getKeyChar()) {
            case '\n':
                e.consume();
                execute();
                break;
        }
    }

    private void addHistory(String line) {
        _history.put(Long.toString(_nextHistory), new Command().setIn(line));
        _nextHistory++;
    }

    private void setHistory(long h, String line) {
        String key = Long.toString(h);
        if(_history.containsKey(key)) {
            _history.get(key).setIn(line);
        }
        else {
            _history.put(key, new Command().setIn(line));
        }
    }

    private void setHistoryOut(long h, Object output) {
        _history.get(Long.toString(h)).setOut(output);
    }

    private Command getHistory(long h) {
        return _history.get(Long.toString(h));
    }

    public Map<String,Command> getHistory() {
        return _history;
    }

    public String historyCmd() {
        return Long.toString(_nextHistory-2);
    }
}
