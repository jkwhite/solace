package org.excelsi.solace;


import java.awt.Color;
import java.util.HashMap;


public class ColorHashMap extends HashMap {
    @Override
    public Object put(Object ks, Object vs) {
        String k = (String) ks;
        if(vs instanceof String) {
            Color c = null;
            String v = (String) vs;
            if(v.startsWith("#")) {
                float[] rgba = new float[4];
                for(int i=0;i<rgba.length;i++) {
                    rgba[i] = Integer.parseInt(v.substring(1+2*i, 1+2*i+2), 16)/255f;
                }
                c = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
            }
            else {
                try {
                    c = (Color) Color.class.getField(v).get(null);
                }
                catch(Exception e) {
                    System.err.println(e.toString());
                }
            }
            return super.put(k, c);
        }
        else {
            return super.put(ks, vs);
        }
    }
}
