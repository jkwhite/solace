package org.excelsi.solace;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.FlowLayout;
import java.awt.GridLayout;


public class SequenceRenderer extends Renderer {
    public JComponent render(Object o, String... context) {
        Sequence t = (Sequence) o;
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        int i=0;
        for(Object c:t) {
            final JComponent jc = super.render(c, context);
            final int j = i;
            jc.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Console co = console(jc);
                    co.append("$h[\""+co.historyCmd()+"\"].out["+j+"]");
                }
            });
            p.add(jc);
            i++;
        }
        return p;
    }
}
