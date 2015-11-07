package org.excelsi.solace;


import java.util.Map;


public interface DynamicConsole {
    Map<String,Map<String,ColorHashMap>> getColors();
    Map<String, Runnable> getKeybindings();
    void bindkey(String key, Runnable r);
    String getColorscheme();
    void setColorscheme(String c);
    Renderer getRenderer();
    void brk();
}
