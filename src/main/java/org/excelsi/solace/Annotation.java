package org.excelsi.solace;


public class Annotation {
    private final Object _o;
    private final String _text;
    private String _pos;


    public Annotation(Object o, String text) {
        _o = o;
        _text = text;
    }

    public Object getLabelled() {
        return _o;
    }

    public String getText() {
        return _text;
    }

    public void setPos(String p) {
        _pos = p;
    }

    public String getPos() {
        return _pos;
    }

    @Override public String toString() {
        return _text+": "+_o;
    }
}
