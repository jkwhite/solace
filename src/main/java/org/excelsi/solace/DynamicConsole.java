package org.excelsi.solace;


import java.util.Map;


public interface DynamicConsole extends Curses, Editable {
    Map<String,Map<String,ColorHashMap>> getColors();
    Map<String, Runnable> getKeybindings();
    void bindkey(String key, Runnable r);
    String getColorscheme();
    void setColorscheme(String c);
    Renderer getRenderer();
    void historyBack();
    void historyForward();
    void print(Object o);
    void clear();
}
