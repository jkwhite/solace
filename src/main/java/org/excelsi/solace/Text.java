package org.excelsi.solace;


import java.util.*;


public class Text {
    private static final char MAGIC = '`';
    private static final Set<String> TOKENS = new HashSet<String>(Arrays.asList(
        new String[]{""}));
    private final List<Segment> _segs;
    private final String _base;


    public Text(String text, String base) {
        if(text==null) {
            System.err.println("NULL TEXT");
            Thread.dumpStack();
            text = "";
        }
        _segs = parse(text);
        _base = base;
    }

    public List<Segment> getSegments() {
        return _segs;
    }

    public String getBase() {
        return _base;
    }

    @Override public String toString() {
        return "text: "+_segs;
    }

    private static List<Segment> parse(String t) {
        List<Segment> segs = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inseg = true;
        int i=0;
        int st=0;

        String type = Character.toString(MAGIC);
        while(i<t.length()) {
            int ch = t.charAt(i);
            if(ch==MAGIC) {
                segs.add(new Segment(type,t.substring(st,i)));
                type = t.substring(i+1,i+2);
                i+=2;
                st=i;
                cur.setLength(0);
            }
            else {
                cur.append(ch);
                i++;
            }
        }
        if(st!=i) {
            segs.add(new Segment(type, t.substring(st)));
        }
        return segs;
    }

    public static class Segment {
        private final String _type;
        private final String _text;


        public Segment(String type, String text) {
            _type = type;
            _text = text;
        }

        public String getType() {
            return _type;
        }

        public String getText() {
            return _text;
        }

        @Override public String toString() {
            return _type+"::"+_text;
        }
    }
}
