package org.excelsi.solace;


import java.awt.*;
import javax.swing.*;


public class Things {
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static void center(Window w) {
        Dimension dim = getScreenSize();
        Rectangle frame = w.getBounds();
        w.setLocation((dim.width-frame.width)/2, (dim.height-frame.height)/2);
    }

    //public static T coalesce<T>(T... obj) {
        //for(T o:obj) {
            //if(o!=null) return o;
        //}
        //return null;
    //}

    public static String replace(String s) {
        final String START = "${";
        final String END = "}";
        StringBuilder sb = new StringBuilder(s);
        while(true) {
            int k = sb.indexOf(START);
            if(k==-1) {
                break;
            }
            int j = sb.indexOf(END, k);
            if(j==-1) {
                break;
            }
            String prop = sb.substring(k+START.length(), j);
            String rep = System.getProperty(prop, "");
            sb.replace(k, j+1, rep);
        }
        return sb.toString();
    }
}
