package org.excelsi.solace;


import javafx.scene.Node;


public class Jfx {
    public static Node rootFor(Node n) {
        while(n!=null && !(n instanceof JfxConsole)) {
            //System.err.println("n: "+n+", p: "+n.getParent());
            n = n.getParent();
        }
        return n;
    }
}
