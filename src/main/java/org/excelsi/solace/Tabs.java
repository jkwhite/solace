package org.excelsi.solace;


import javax.swing.*;
import java.awt.BorderLayout;


public class Tabs extends JComponent implements MetaConsole {
    private int _next = 1;
    private int _count = 0;
    private JTabbedPane _tabs;
    private Console _c;
    private ShellFactory _sf;


    public Tabs() {
        setLayout(new BorderLayout());
        setBackground(java.awt.Color.white);
        _tabs = new JTabbedPane(JTabbedPane.TOP);
        //add(_tabs, BorderLayout.CENTER);
    }

    public void setShellFactory(ShellFactory sf) { _sf = sf; }

    public void newTerminal() {
        addConsole(new TerminalConsole(_sf.newShell(), _sf.getMetaShell(), this));
    }

    public void newWorksheet() {
        //addConsole(new WorksheetConsole(_sf.newShell(), _sf.getMetaShell(), this));
    }

    public void addConsole(Console c) {
        if(++_count==1) {
            add(c, BorderLayout.CENTER);
            _c = c;
        }
        else {
            if(_count==2) {
                remove(_c);
                add(_tabs, BorderLayout.CENTER);
                _tabs.addTab("1", _c);
            }
            _tabs.addTab(""+_next, c);
        }
        c.init();
        c.focus();
        _next++;
    }

    public void closeConsole() {
        if(--_count==0) {
            System.exit(0);
        }
        else {
            _tabs.removeTabAt(_tabs.getSelectedIndex());
            if(_count==1) {
                _c = (Console) _tabs.getComponentAt(0);
                remove(_tabs);
                add(_c, BorderLayout.CENTER);
            }
        }
    }
}
