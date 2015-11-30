package org.excelsi.solace;


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
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;


public class LineInput extends HBox implements Input {
    private final StringBuilder _text = new StringBuilder();
    private final Label _pre = new Label("");
    private final Label _curs = new Label(" ");
    private final Label _post = new Label("");
    private int _cursorIndex = 0;
    private volatile boolean _frozen;


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

    @Override public void setLine(String s) {
        _text.setLength(0);
        _text.append(s);
        updateText();
    }

    @Override public void append(Object o) {
        if(!_frozen) {
            if(o instanceof CharSequence) {
                appendText(o.toString());
            }
            else {
                System.err.println("nope: "+o);
            }
        }
    }

    @Override public void backspace() {
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

    @Override public void left() {
        if(_cursorIndex>0) {
            if(_cursorIndex>_text.length()) {
                _cursorIndex = _text.length();
            }
            _cursorIndex--;
            updateText();
        }
    }

    @Override public void right() {
        if(_cursorIndex<_text.length()) {
            _cursorIndex++;
            updateText();
        }
    }

    @Override public int getPos() {
        return _cursorIndex;
    }

    @Override public void setPos(int i) {
        _cursorIndex = i;
        updateText();
    }

    @Override public String getText() {
        return _text.toString();
    }

    @Override public void freeze() {
        _frozen = true;
        updateText();
    }

    private void appendText(String s) {
        if(_cursorIndex==_text.length()) {
            _text.append(s);
        }
        else {
            _text.insert(_cursorIndex, s);
        }
        _cursorIndex+=s.length();
        updateText();
    }

    private void updateText() {
        if(_frozen) {
            _pre.setText(_text.toString());
            _curs.setText("");
            _post.setText("");
        }
        else {
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
}
