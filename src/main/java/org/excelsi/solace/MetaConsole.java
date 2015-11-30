package org.excelsi.solace;


public interface MetaConsole extends Editable {
    void nameTerminal(String name);
    void newTerminal();
    void nextTerminal();
    void prevTerminal();
    void newWorksheet();
    void closeConsole();
}
