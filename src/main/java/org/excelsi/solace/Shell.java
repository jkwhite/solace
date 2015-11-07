package org.excelsi.solace;


import java.util.List;
import java.io.IOException;
import java.io.File;


public interface Shell {
    public void init();

    public void setVariable(String name, Object value);
    public Object getVariable(String name);

    public void setPreparse(boolean p);

    public boolean getPreparse();

    public List<String> getImports();

    public Object evaluate(String cmd);
    public void evalScript(File file) throws IOException;
}
