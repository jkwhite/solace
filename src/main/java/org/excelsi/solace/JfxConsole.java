package org.excelsi.solace;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.Group;
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
    protected final Renderer _r = new Renderer();


    public JfxConsole(Shell s, MetaShell ms, MetaConsole mc) {
        getStyleClass().add("console");
        _shell = s;
        _ms = ms;
        _mc = mc;
        _shell.setVariable("$w", _mc);
        _shell.setVariable("$c", this);
        _shell.setVariable("$r", _r);
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

    @Override
    public void brk() {
        _input.appendText("^C");
        accept(false);
    }

    private void init() {
        _lines = new VBox();
        _lines.getStyleClass().add("console");
        setContent(_lines);
        _shell.init();
        addLine();
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
        //System.err.println("ISO: "+Character.isISOControl(e.getKeyCode()));
        //System.err.println("ID: "+e.getCode());
        //System.err.println("TXT: "+e.getText());
        //System.err.println("CHR: '"+e.getCharacter()+"'");
        //System.err.println("CHRI: '"+(int)e.getKeyChar()+"'");
        //System.err.println("TXT: "+KeyEvent.getKeyText(e.getKeyCode()));
        //System.err.println("KEY: "+kb);
        final Runnable r = _keyBindings.get(kb.toString());
        if(r!=null) {
            e.consume();
            Platform.runLater(new Runnable() {
                public void run() {
                    r.run();
                }
            });
        }
    }

    private void accept(boolean execute) {
        String text = _input.getText().replaceAll("\\n", "");
        _line.getChildren().remove(_input);
        Label in = new Label(text);
        in.getStyleClass().add("input");
        _line.getChildren().add(in);
        if(execute) {
            execute(text);
        }
        addLine();
    }

    private HBox _line;
    private TextField _input;

    private void addLine() {
        _line = new HBox();
        _line.getStyleClass().add("input");
        Label prompt = new Label(prompt());
        prompt.getStyleClass().add("prompt");
        _line.getChildren().add(prompt);
        _input = new TextField();
        _input.setPrefColumnCount(100);
        _line.getChildren().add(_input);
        _input.setOnKeyPressed(k -> {
            if(k.getCode()==KeyCode.ENTER) {
                accept(true);
            }
            else {
                handleKey(k);
            }
        });
        _lines.getChildren().add(_line);
        Platform.runLater(() -> { setVvalue(getVmax()); });
    }

    private void addOutput(Object o, String type) {
        Node n = render(o);
        n.getStyleClass().add(type);
        _lines.getChildren().add(n);
    }

    private void execute(String cmd) {
        if(!("".equals(cmd))) {
            Object o;
            try {
                o = _shell.evaluate(cmd);
                addOutput(o, "output");
            }
            catch(Exception e) {
                e.printStackTrace();
                o = e.getMessage();
                addOutput(o, "error");
            }
        }
    }

    private Node render(Object o) {
        if(o==null) {
            o = "(null)";
        }
        if(o instanceof Node) {
            return (Node) o;
        }
        return new Label(o.toString());
    }
}
