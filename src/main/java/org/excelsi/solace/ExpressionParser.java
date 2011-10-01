package org.excelsi.solace;


import java.util.*;


public class ExpressionParser {
    private char _start;
    private char _end;
    private char _sep;


    public ExpressionParser() {
        this('(', ')', ';');
    }

    public ExpressionParser(char start, char end, char sep) {
        _start = start;
        _end = end;
        _sep = sep;
    }

    // show all rules of (create 1d.space from chaos)
    public Iterable<Expr> parse(String s) {
        final List<List<Expr>> order = new ArrayList<List<Expr>>();
        int height = 0;
        for(int i=0;i<s.length();i++) {
            char c = s.charAt(i);
            if(c==_start) {
                addNewBuilder(order, ++height);
            }
            else if(c==_end) {
                List<Expr> lli = order.get(height);
                Expr last = lli.get(lli.size()-1);
                if(--height<0) {
                    throw new RuntimeException("parse error");
                }
                List<Expr> li = order.get(height);
                if(li.isEmpty()) li.add(new Expr());
                Expr cur = li.get(li.size()-1);
                cur.b.append(last.name);
            }
            else if(c==_sep) {
                addNewBuilder(order, height);
            }
            else {
                add(c, order, height);
            }
        }
        if(height!=0) {
            throw new RuntimeException("parse error");
        }
        System.err.println("PARSED: "+order);

        List<Expr> exprs = new ArrayList<Expr>();
        for(int i=order.size()-1;i>=0;i--) {
            for(Expr lev:order.get(i)) {
                exprs.add(lev);
            }
        }
        return exprs;
    }

    private void addNewBuilder(List<List<Expr>> order, int height) {
        while(height>=order.size()) {
            order.add(new ArrayList(2));
        }
        order.get(height).add(new Expr());
    }

    private void add(char c, List<List<Expr>> order, int height) {
        while(height>=order.size()) {
            order.add(new ArrayList(2));
        }
        List<Expr> li = order.get(height);
        if(li.isEmpty()) {
            li.add(new Expr());
        }
        li.get(li.size()-1).b.append(c);
    }

    static class Expr {
        private static int _next = 0;


        public final String name = nextName();
        public final StringBuilder b = new StringBuilder();


        private static String nextName() {
            String n = "__n"+_next;
            _next++;
            return n;
        }

        public String toString() {
            return "[name: "+name+", expr: "+b+"]";
        }
    }
}
