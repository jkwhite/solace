package org.excelsi.solace;


import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.FlowLayout;
import java.awt.GridLayout;


public class CommandRenderer extends Renderer {
    public JComponent render(Object o, String... context) {
        Command c = (Command) o;
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(new JLabel(c.getIn()));
        //System.err.println("out is: "+c.getOut());
        if(c.getOut()!=null) {
            p.add(super.render(c.getOut(), context));
        }
        return p;
    }
}
