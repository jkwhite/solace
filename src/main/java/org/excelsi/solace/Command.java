package org.excelsi.solace;


public class Command {
    private String _in;
    private Object _out;


    public String getIn() { return _in; }

    public Command setIn(String in) { _in = in; return this; }

    public Object getOut() { return _out; }

    public Command setOut(Object out) { _out = out; return this; }

    public String toString() {
        return "in: "+_in+"; out: "+_out;
    }
}
