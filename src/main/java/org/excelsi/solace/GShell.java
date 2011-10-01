package org.excelsi.solace;


import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import java.net.URL;
import java.io.*;
import java.util.*;


public class GShell implements Shell {
    private static final String RC = System.getProperty("user.home")+"/.solacerc";
    private boolean _preparse = false;
    private Binding _binding = new CustomBinding();
    private GroovyShell _interpreter = new GroovyShell(_binding);
    private List<String> _imports = new ArrayList<String>();
    private MetaShell _ms;


    public GShell() {
        this(null);
    }

    public GShell(MetaShell ms) {
        _ms = ms;
    }

    public void setVariable(String name, Object value) {
        _binding.setVariable(name, value);
    }

    public Set getVariables() {
        return _binding.getVariables().keySet();
    }

    public void setPreparse(boolean p) { _preparse = p; }

    public boolean getPreparse() { return _preparse; }

    public List<String> getImports() {
        return _imports;
    }

    public Object evaluate(String cmd) {
        try {
            Iterable<ExpressionParser.Expr> exprs;
            if(_preparse) {
                exprs = new ExpressionParser().parse(cmd);
            }
            else {
                ExpressionParser.Expr e = new ExpressionParser.Expr();
                e.b.append(cmd);
                exprs = Arrays.asList(new ExpressionParser.Expr[]{e});
            }
            Object res = null;
            for(ExpressionParser.Expr e:exprs) {
                res = _interpreter.evaluate(buildImports(e.b.toString()));
                setVariable(e.name, res);
                setVariable("it", res);
            }
            //Object res = _interpreter.execute(cmd);
            setVariable("$_", res);
            setVariable("last", res);
            return res;
        }
        catch(Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public void init() {
        setVariable("$s", this);
        try {
            _interpreter.evaluate(getClass().getClassLoader().getResource("META-INF/solace/core.groovy").openStream());
            for(Enumeration<URL> us=getClass().getClassLoader().getResources("META-INF/solace/commands.groovy");us.hasMoreElements();) {
                URL u = us.nextElement();
                InputStream is = null;
                try {
                    is = u.openStream();
                    _interpreter.evaluate(is);
                    //_interpreter.execute(readFully(is));
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if(is!=null) try { is.close(); } catch(Exception e) {}
                }
            }
            for(File script:_ms.getScripts()) {
                evalScript(script);
            }
            File f = new File(RC);
            if(f.exists()) {
                evalScript(f);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void evalScript(File f) throws IOException {
        BufferedInputStream r = null;
        try {
            r = new BufferedInputStream(new FileInputStream(f));
            _interpreter.evaluate(r);
        }
        finally {
            if(r!=null) try { r.close(); } catch(IOException e) {}
        }
    }

    public static String readFully(InputStream is) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line=br.readLine())!=null) {
                sb.append(line);
                sb.append("\n");
            }
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try { br.close(); } catch(IOException e) {}
        }
        return sb.toString();
    }

    private String buildImports(String cmd) {
        StringBuilder b = new StringBuilder();
        for(int i=0;i<_imports.size();i++) {
            b.append(_imports.get(i)).append("\n");
        }
        b.append(cmd);
        return b.toString();
    }

    static class CustomBinding extends Binding {
        public Object getVariable(String word) {
            //System.err.println("looking up '"+word+"'");
            if(word.equals("by")||word.equals("the")||word.equals("of")) {
                return null;
            }
            return super.getVariable(word);
        }
    }
}
