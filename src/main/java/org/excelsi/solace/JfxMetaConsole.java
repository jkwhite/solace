package org.excelsi.solace;


public class JfxMetaConsole implements MetaConsole {
    private MetaConsole _delegate;
    private ShellFactory _shellFactory;


    public void setDelegate(MetaConsole delegate) {
        _delegate = delegate;
    }

    public void setShellFactory(ShellFactory shellFactory) {
        _shellFactory = shellFactory;
    }

    public ShellFactory getShellFactory() {
        return _shellFactory;
    }

    @Override public void nameTerminal(String name) {
        _delegate.nameTerminal(name);
    }

    @Override public void newTerminal() {
        _delegate.newTerminal();
    }

    @Override public void nextTerminal() {
        _delegate.nextTerminal();
    }

    @Override public void prevTerminal() {
        _delegate.prevTerminal();
    }

    @Override public void newWorksheet() {
        _delegate.newWorksheet();
    }

    @Override public void closeConsole() {
        _delegate.closeConsole();
    }

    @Override public void cutSelection() {
        _delegate.cutSelection();
    }

    @Override public void copySelection() {
        _delegate.copySelection();
    }

    @Override public void pasteBuffer() {
        _delegate.pasteBuffer();
    }
}
