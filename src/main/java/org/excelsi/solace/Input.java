package org.excelsi.solace;


public interface Input extends Stringable {
    String getText();
    void setLine(String s);
    void append(Object o);
    void backspace();
    void left();
    void right();
    void freeze();
    int getPos();
    void setPos(int i);
}
