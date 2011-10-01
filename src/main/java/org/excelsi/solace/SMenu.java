package org.excelsi.solace;


import java.awt.*;
import javax.swing.*;


import com.apple.mrj.*;


public class SMenu implements MRJAboutHandler, MRJQuitHandler, MRJPrefsHandler {
    public void apply(JFrame f) {
        MRJApplicationUtils.registerAboutHandler(this);
        MRJApplicationUtils.registerPrefsHandler(this);
        MRJApplicationUtils.registerQuitHandler(this);
    }

    public void handleAbout() {
        about();
    }

    public static void about() {
        final String desc = "<html>Solace v0.1<br/>(c) 2011 John K White</html>";
        JFrame f = new JFrame("Solace");
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.BLACK);
        p.setForeground(Color.WHITE);
        ImageIcon i = new ImageIcon(SMenu.class.getResource("/solace1.jpg"));
        p.add(new JLabel(i), BorderLayout.WEST);
        JLabel l = new JLabel(desc);
        l.setBackground(Color.BLACK);
        l.setForeground(Color.WHITE);
        l.setFont(Font.decode(Font.SERIF+"-14"));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(l, BorderLayout.CENTER);
        f.getContentPane().add(p);
        f.setSize(new Dimension(300+i.getIconWidth(), i.getIconHeight()));
        f.setResizable(false);
        Things.center(f);
        f.setVisible(true);
    }

    public void handlePrefs() {
    }

    public void handleQuit() {
        quit();
    }

    public static void quit() {
        System.exit(0);
    }
}
