package org.excelsi.solace;


import java.util.List;
import java.util.LinkedList;


public class History {
    private final List<String> _h = new LinkedList<>();
    private final Sink<String> _sink;
    private Stringable _current;
    private String _snapshot;
    private int _c;


    public History() {
        _sink = null;
    }

    public History(Iterable<String> source, Sink<String> sink) {
        _sink = sink;
        for(String s:source) {
            _h.add(s);
        }
        _c = _h.size();
    }

    public void push(Stringable line) {
        commit();
        _current = line;
        _c = _h.size();
    }

    public void commit() {
        if(_current!=null) {
            final String next = _current.stringify();
            persist(next);
            _current = null;
        }
        _c = _h.size();
    }

    public String back() {
        if(_c>0) {
            if(_c==_h.size() && _current!=null) {
                _snapshot = _current.stringify();
            }
            _c--;
        }
        return _h.get(_c);
    }

    public String forward() {
        if(_c<_h.size()) {
            _c++;
        }
        return _c==_h.size() ? _snapshot : _h.get(_c);
    }

    private void persist(String line) {
        if(! "".equals(line.trim()) && (_h.isEmpty() || !_h.get(_h.size()-1).equals(line))) {
            _h.add(line);
            if(_sink!=null) {
                _sink.write(line);
            }
        }
    }
}
