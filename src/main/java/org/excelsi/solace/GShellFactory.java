package org.excelsi.solace;


public class GShellFactory implements ShellFactory {
    private MetaShell _ms;


    public Shell newShell() { return new GShell(_ms); }

    public void setMetaShell(MetaShell ms) { _ms = ms; }

    public MetaShell getMetaShell() { return _ms; }
}
