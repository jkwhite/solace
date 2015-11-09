package org.excelsi.solace;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;


public class JfxConsole extends ScrollPane implements DynamicConsole {
    private Shell _shell;
    private MetaShell _ms;
    private MetaConsole _mc;
    private Map<String,Map<String,ColorHashMap>> _colors = new HashMap<String,Map<String,ColorHashMap>>();
    private Map<String,Runnable> _keyBindings = new HashMap<String,Runnable>();
    private String _colorscheme = "*";
    private String _prompt = "% ";
    private VBox _lines;
    private final History _history;
    private final Executor _pool = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });
    protected final Renderer _r = new Renderer();
    protected final JfxRendererRegistry _jfxr = JfxRendererRegistry.defaultRegistry();


    public JfxConsole(Shell s, MetaShell ms, MetaConsole mc) {
        getStyleClass().add("console");
        _shell = s;
        _ms = ms;
        _mc = mc;
        _shell.setVariable("$w", _mc);
        _shell.setVariable("$c", this);
        _shell.setVariable("$r", _r);
        _history = ms.createHistory();
        init();
    }

    public String prompt() {
        Object p = null;
        try {
            p = _shell.evaluate("prompt()").toString();
        }
        catch(Exception e) {
            System.err.println("failed computing prompt: "+e.toString());
            e.printStackTrace();
        }
        if(p==null) {
            p = _shell.getVariable("prompt");
            if(p==null) {
                p = "% ";
            }
        }
        return p.toString();
    }

    public Map<String,Map<String,ColorHashMap>> getColors() { return _colors; }
    public Map<String, Runnable> getKeybindings() { return _keyBindings; }
    public String getColorscheme() { return _colorscheme; }
    public Renderer getRenderer() { return _r; }

    public void bindkey(String key, Runnable r) {
        _keyBindings.put(key, r);
    }

    public void setColorscheme(String c) {
        try {
            _shell.evalScript(_ms.getColorscheme(c));
            _colorscheme = c;
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void print(Object o) {
        addOutput(o, "output");
    }

    @Override public void brk() {
        _input.append("^C");
        accept(false);
    }

    @Override public void bksp() {
        _input.backspace();
    }

    @Override public void cursorLeft() {
        _input.left();
    }

    @Override public void cursorRight() {
        _input.right();
    }

    @Override public void historyBack() {
        _input.setLine(_history.back());
    }

    @Override public void historyForward() {
        _input.setLine(_history.forward());
    }

    private void init() {
        _lines = new VBox();
        _lines.getStyleClass().add("console");
        setContent(_lines);
        _shell.init();
        addLine();
        setOnKeyPressed(k -> {
            if(k.getCode()==KeyCode.ENTER) {
                accept(true);
            }
            else if(!k.isShortcutDown()) {
                handleKey(k);
            }
        });
        setOnMouseClicked(e -> {
            requestFocus();
        });
    }

    private void handleKey(KeyEvent e) {
        // user-bound keys
        StringBuilder kb = new StringBuilder();
        if(e.isControlDown()) {
            kb.append("C-");
        }
        if(Character.isLetterOrDigit(e.getCharacter().charAt(0))||e.getCharacter().charAt(0)=='\t') {
            kb.append(e.getCharacter());
        }
        else {
            kb.append(e.getCode().toString().toLowerCase());
        }
        System.err.println("ID: "+e.getCode());
        System.err.println("TXT: "+e.getText());
        System.err.println("CHR: '"+e.getCharacter()+"'");
        System.err.println("KEY: "+kb);
        final Runnable r = _keyBindings.get(kb.toString());
        if(r!=null) {
            // special action
            e.consume();
            Platform.runLater(new Runnable() {
                public void run() {
                    r.run();
                }
            });
        }
        else if(e.getText().length()==1) {
            _input.append(e.getText());
        }
        else if(kb.length()==1) {
            _input.append(kb.toString());
        }
        else {
            System.err.println(String.format("unhandled key '%s'", kb));
        }
    }

    private void accept(boolean execute) {
        String text = _input.getText().replaceAll("\\n", "");
        _line.getChildren().remove(_input);
        Label in = new Label(text);
        in.getStyleClass().add("input");
        _line.getChildren().add(in);
        if(execute) {
            _pool.execute(()->{
                execute(text);
                addLine();
            });
        }
        else {
            addLine();
        }
    }

    private HBox _line;
    private LineInput _input;

    static class LineInput extends HBox implements Stringable {
        private final StringBuilder _text = new StringBuilder();
        private final Label _pre = new Label("");
        private final Label _curs = new Label(" ");
        private final Label _post = new Label("");
        private int _cursorIndex = 0;


        public LineInput() {
            getStyleClass().add("input");
            _curs.getStyleClass().add("cursor");
            getChildren().add(_pre);
            getChildren().add(_curs);
            getChildren().add(_post);
        }

        @Override public String stringify() {
            return _text.toString();
        }

        public void setLine(String s) {
            _text.setLength(0);
            _text.append(s);
            updateText();
        }

        public void append(String s) {
            if(_cursorIndex==_text.length()) {
                _text.append(s);
            }
            else {
                _text.insert(_cursorIndex, s);
            }
            _cursorIndex++;
            updateText();
        }

        public void backspace() {
            if(_text.length()>0) {
                if(_cursorIndex==_text.length()) {
                    _text.setLength(_text.length()-1);
                }
                else {
                    _text.deleteCharAt(_cursorIndex-1);
                }
                _cursorIndex--;
                updateText();
            }
        }

        public void left() {
            if(_cursorIndex>0) {
                if(_cursorIndex>_text.length()) {
                    _cursorIndex = _text.length();
                }
                _cursorIndex--;
                updateText();
            }
        }

        public void right() {
            if(_cursorIndex<_text.length()) {
                _cursorIndex++;
                updateText();
            }
        }

        public String getText() {
            return _text.toString();
        }

        private void updateText() {
            if(_cursorIndex<_text.length()) {
                _pre.setText(_text.substring(0, _cursorIndex));
                _curs.setText(_text.substring(_cursorIndex, _cursorIndex+1));
                _post.setText(_text.substring(_cursorIndex+1));
            }
            else {
                _pre.setText(_text.toString());
                _curs.setText(" ");
                _post.setText("");
            }
        }
    }

    private void addLine() {
        if(Platform.isFxApplicationThread()) {
            _line = new HBox();
            _line.getStyleClass().add("input");
            Label prompt = new Label(prompt());
            prompt.getStyleClass().add("prompt");
            _line.getChildren().add(prompt);
            _input = new LineInput();
            _line.getChildren().add(_input);
            _lines.getChildren().add(_line);
            _history.push(_input);
            Platform.runLater(()->{ setVvalue(getVmax()); });
        }
        else {
            Platform.runLater(()->{ addLine(); });
        }
    }

    private void addOutput(Object o, String type) {
        Node n = render(o, type);
        n.getStyleClass().add(type);
        if(Platform.isFxApplicationThread()) {
            _lines.getChildren().add(n);
            setVvalue(getVmax());
        }
        else {
            Platform.runLater(()->{
                _lines.getChildren().add(n);
                setVvalue(getVmax());
            });
        }
    }

    private void execute(String cmd) {
        if(!("".equals(cmd))) {
            Object o;
            try {
                o = _shell.evaluate(cmd);
                if(o!=null) {
                    addOutput(o, "output");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                o = e.getMessage();
                if(o==null) {
                    o = e.toString();
                }
                addOutput(o, "error");
            }
        }
    }

    private Node render(Object o, String type) {
        return _jfxr.render(o, (n) -> { n.getStyleClass().add(type); return n; });
    }
}
