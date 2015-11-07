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

    @Override
    public void newTerminal() {
        _delegate.newTerminal();
    }

    @Override
    public void newWorksheet() {
        _delegate.newWorksheet();
    }

    @Override
    public void closeConsole() {
        _delegate.closeConsole();
    }
}
