package org.excelsi.solace;


import javax.swing.*;
import org.excelsi.gimmal.App;


public class Solace implements App {
    private MetaConsole _mc;


    public static void main(String[] args) throws Exception {
        if(args.length==1) {
            GShell s = new GShell();
            System.out.println(s.evaluate(GShell.readFully(new java.io.FileInputStream(args[0]))));
        }
        else {
            new Solace().start();
        }
    }

    public void setMetaConsole(MetaConsole mc) { _mc = mc; }

    public MetaConsole getMetaConsole() { return _mc; }

    public void start() {
        JFrame f = new JFrame("Solace");
        if(_mc instanceof JComponent) {
            f.getContentPane().add((JComponent) _mc);
        }
        f.setSize(Things.getScreenSize());
        new SMenu().apply(f);
        Things.center(f);
        _mc.newTerminal();
        f.pack();
        f.setSize(Things.getScreenSize());
        f.setVisible(true);
    }
}
