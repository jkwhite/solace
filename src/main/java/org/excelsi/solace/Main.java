package org.excelsi.solace;


// hardcode gimmal.yaml to avoid dependency
public class Main {
    public static void main(String[] args) {
        MetaShell ms = new MetaShell();
        ms.setRoot("${user.home}/.solace");
        GShellFactory sf = new GShellFactory();
        sf.setMetaShell(ms);
        JfxMetaConsole mc = new JfxMetaConsole();
        mc.setShellFactory(sf);
        Solace s = new Solace();
        s.setMode("jfx");
        s.setMetaConsole(mc);
        s.start();
    }
}
