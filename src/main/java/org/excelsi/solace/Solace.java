package org.excelsi.solace;


import javax.swing.*;
import javafx.application.Application;
import org.excelsi.gimmal.App;


public class Solace implements App {
    private MetaConsole _mc;
    private String _mode;


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

    public void setMode(String mode) {
        _mode = mode;
    }

    public String getMode() {
        return _mode;
    }

    public void start() {
        switch(_mode) {
            case "swing":
                startSwing();
                break;
            case "jfx":
                startJfx();
                break;
        }
    }

    private void startSwing() {
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

    private void startJfx() {
        SolaceJfx.setMetaConsole(_mc);
        Application.launch(SolaceJfx.class, new String[0]);
    }
}
