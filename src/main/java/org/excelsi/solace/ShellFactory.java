package org.excelsi.solace;


public interface ShellFactory {
    public Shell newShell();
    public void setMetaShell(MetaShell ms);
    public MetaShell getMetaShell();
}
