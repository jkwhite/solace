package org.excelsi.solace;


public interface Curses {
    void brk();
    void bksp();
    void cursorLeft();
    void cursorRight();
    int getCursorPos();
    void setCursorPos(int i);
    String getCursorLine();
}
