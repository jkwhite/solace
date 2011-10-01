package org.excelsi.solace;


import java.io.*;
import java.net.*;
import java.util.*;


public class MetaShell {
    private static final String COLORS = "/colors";
    private static final String PLUGINS = "/plugins";
    private static final String SCRIPTS = "/scripts";
    private static final String DB = "/var";
    private String _root;
    private ClassLoader _cl;


    public MetaShell() {
    }

    public void setRoot(String root) { _root = root; }

    public synchronized ClassLoader getClassLoader() {
        if(_cl==null) {
            List<URL> jars = new ArrayList<URL>();
            File dir = new File(root()+PLUGINS);
            if(dir.exists()) {
                for(File f:dir.listFiles()) {
                    if(f.getName().endsWith(".jar")) {
                        try {
                            jars.add(f.toURL());
                        }
                        catch(MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            _cl = new URLClassLoader(jars.toArray(new URL[0]));
        }
        return _cl;
    }

    public List<File> getScripts() {
        List<File> scripts = new ArrayList<File>();
        File dir = new File(root()+SCRIPTS);
        if(dir.exists()) {
            for(File f:dir.listFiles()) {
                if(f.getName().endsWith(".groovy")) {
                    scripts.add(f);
                }
            }
        }
        return scripts;
    }

    public File getColorscheme(String name) {
        return new File(root()+COLORS, name+".groovy");
    }

    //public Object getVar(String name) {
    //}
//
    //public void putVar(String name, Object o) {
    //}

    private String root() { return Things.replace(_root); }
}
