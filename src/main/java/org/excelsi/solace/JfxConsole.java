package org.excelsi.solace;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.Stack;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Box;
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
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;


public class JfxConsole extends ScrollPane implements DynamicConsole {
    private Shell _shell;
    private MetaShell _ms;
    private MetaConsole _mc;
    private Map<String,Map<String,ColorHashMap>> _colors = new HashMap<String,Map<String,ColorHashMap>>();
    private Map<String,Runnable> _keyBindings = new HashMap<String,Runnable>();
    private String _colorscheme = "*";
    private String _theme = null;
    private String _prompt = "% ";
    private VBox _lines;
    private final History _history;
    private final Stack<KeyCallback> _keyCallbacks = new Stack<>();
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
        _shell.setVariable("$r", _jfxr);
        _history = ms.createHistory();
    }

    @Override public void cutSelection() {
    }

    @Override public void copySelection() {
    }

    @Override public void pasteBuffer() {
        final DataFormat[] precedence = new DataFormat[]{
            DataFormat.IMAGE,
            DataFormat.URL,
            DataFormat.HTML,
            DataFormat.PLAIN_TEXT};
        Clipboard clipboard = Clipboard.getSystemClipboard();
        //System.err.println("types: "+clipboard.getContentTypes());
        for(DataFormat f:precedence) {
            Object o = clipboard.getContent(f);
            if(o!=null) {
                _input.append(o);
                scrollToBottom();
                break;
            }
        }
    }

    @FunctionalInterface
    interface KeyCallback {
        void handleKey(String key);
    }

    @Override public String getch(final long timeout) {
        final List<String> k = new ArrayList<>(1);
        synchronized(k) {
            _keyCallbacks.push((key)->{synchronized(k) {k.add(key);k.notify();}});
            try {
                if(timeout>0) {
                    k.wait(timeout);
                }
                else {
                    k.wait();
                }
            }
            catch(InterruptedException e) {
            }
        }
        return !k.isEmpty()?k.get(0):"";
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

    public void setTheme(final String theme) {
        if(_theme!=null) {
            getScene().getStylesheets().remove(_ms.findThemeStylesheetUrl(_theme));
        }
        final String url = _ms.findThemeStylesheetUrl(theme);
        getScene().getStylesheets().add(url);
    }

    public String getTheme() {
        return _theme;
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

    public void unprint() {
        removeOutput();
    }

    @Override public void clear() {
        if(Platform.isFxApplicationThread()) {
            while(_lines.getChildren().size()>1) {
                _lines.getChildren().remove(1);
            }
            //addStrut();
        }
        else {
            Platform.runLater(()->{ clear(); });
        }
    }

    @Override public void brk() {
        _input.append("^C");
        accept(false);
    }

    @Override public void bksp() {
        scrollToBottom();
        _input.backspace();
    }

    @Override public void cursorLeft() {
        _input.left();
    }

    @Override public void cursorRight() {
        _input.right();
    }

    @Override public int getCursorPos() {
        return _input.getPos();
    }

    @Override public void setCursorPos(int i) {
        _input.setPos(i);
    }

    @Override public String getCursorLine() {
        return _input.getText();
    }

    @Override public void historyBack() {
        _input.setLine(_history.back());
        scrollToBottom();
    }

    @Override public void historyForward() {
        _input.setLine(_history.forward());
        scrollToBottom();
    }

    public void screenshot(String file) {
        if(Platform.isFxApplicationThread()) {
            synchronized(file) {
                try {
                    File save = new File(file);
                    final WritableImage i = getScene().snapshot(null);
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(i, null), "png", save);
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                finally {
                    file.notify();
                }
            }
        }
        else {
            synchronized(file) {
                Platform.runLater(()->screenshot(file));
                try {
                    file.wait();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void init() {
        _lines = new VBox();
        _lines.getStyleClass().add("console");
        setContent(_lines);
        addStrut();
        _shell.init();
        addLine();
        setOnKeyPressed(k -> {
            if(!_keyCallbacks.isEmpty()) {
                if(!k.isShortcutDown()) {
                    _keyCallbacks.pop().handleKey(mapKeyEvent(k));
                }
            }
            else if(k.getCode()==KeyCode.ENTER) {
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

    private void addStrut() {
        final Rectangle strut = new Rectangle(getWidth(), 1d);
        _lines.getChildren().add(strut);
    }

    private static String mapKeyEvent(final KeyEvent e) {
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
        //System.err.println("ID: "+e.getCode());
        //System.err.println("TXT: "+e.getText());
        //System.err.println("CHR: '"+e.getCharacter()+"'");
        //System.err.println("KEY: "+kb);
        return kb.toString();
    }

    private void handleKey(KeyEvent e) {
        final String kb = mapKeyEvent(e);
        final Runnable r = _keyBindings.get(kb);
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
            scrollToBottom();
        }
        else if(kb.length()==1) {
            _input.append(kb);
            scrollToBottom();
        }
        else {
            System.err.println(String.format("unhandled key '%s'", kb));
        }
    }

    private void accept(boolean execute) {
        String text;
        synchronized(this) {
            _input.freeze();
            text = _input.getText().replaceAll("\\n", "");
        }
        /*
        _line.getChildren().remove(_input);
        Label in = new Label(text);
        in.getStyleClass().add("input");
        _line.getChildren().add(in);
        */
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

    //private HBox _line;
    private BorderPane _line;
    private Input _input;

    private Input createInput(double prefw) {
        return new LineInput();
        /*
        RTInput in = new RTInput();
        System.err.println("prefw="+prefw);
        prefw = 800;
        in.setPrefWidth(prefw);
        in.setMinWidth(prefw);
        return in;
        */
    }

    private void waitForScrollUpdate(int waitTimes) {
        //System.err.println("times: "+waitTimes);
        if(waitTimes>0) {
            if(getVvalue()==getVmax()) {
                //System.err.println("wait");
                Platform.runLater(()->{ waitForScrollUpdate(waitTimes-1); });
            }
            else {
                //System.err.println("scroll");
                scrollToBottom();
            }
        }
        else {
            //System.err.println("default scroll");
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        setVvalue(getVmax());
    }
    /*
    private Runnable createScroller(int times) {
        int vval = getVvalue();
        return new Runnable() {
            @Override public void run() {
                if(vval!=getVmax()) {
                    setVvalue(getVmax());
                    Platform.runLater(createScroller());
                }
                else if(--times>0) {

            }
        };
    }
    */

    private void addLine() {
        if(Platform.isFxApplicationThread()) {
            BorderPane line = new BorderPane();
            Styles.s(line,"input");
            Label prompt = new Label(prompt());
            Styles.s(prompt,"prompt");
            line.setLeft(prompt);
            double prefw = getWidth()-prompt.getWidth();
            Input input = createInput(prefw);
            line.setCenter((Node)input);
            _lines.getChildren().add(line);
            _history.push(input);
            synchronized(this) {
                _line = line;
                _input = input;
            }
            //System.err.println("vvalue: "+getVvalue());
            //System.err.println("vmax: "+getVmax());
            /*
            Platform.runLater(()->{
                System.err.println("in vvalue: "+getVvalue());
                System.err.println("in vmax: "+getVmax());
                setVvalue(getVmax());
            });
            */
            scrollToBottom();
            waitForScrollUpdate(20);
        }
        else {
            Platform.runLater(()->{ addLine(); });
        }
    }

    private void addOutput(Object o, String type) {
        Node n = render(o, type);
        //Styles.s(n, "console", type);
        //TODO: why was this here?
        if(n instanceof Region) {
            ((Region)n).setPrefWidth(getWidth());
        }
        if(Platform.isFxApplicationThread()) {
            _lines.getChildren().add(n);
            scrollToBottom();
            waitForScrollUpdate(20);
        }
        else {
            Platform.runLater(()->{
                _lines.getChildren().add(n);
                scrollToBottom();
                waitForScrollUpdate(20);
            });
        }
    }

    private void removeOutput() {
        if(Platform.isFxApplicationThread()) {
            _lines.getChildren().remove(_lines.getChildren().size()-1);
            scrollToBottom();
            waitForScrollUpdate(20);
        }
        else {
            Platform.runLater(()->{
                _lines.getChildren().remove(_lines.getChildren().size()-1);
                scrollToBottom();
                waitForScrollUpdate(20);
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
