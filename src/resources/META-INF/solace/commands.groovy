import javax.swing.*;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import groovy.swing.SwingBuilder;
import org.excelsi.solace.Table;
import org.excelsi.solace.Annotation;
import groovy.util.Node;
import groovy.xml.XmlUtil;

ExpandoMetaClass.enableGlobally()

// keybindings
$c.keybindings['C-c'] = { $c.brk() }
$c.keybindings['C-d'] = { $w.closeConsole() }
$c.keybindings['back_space'] = { $c.bksp() }
$c.keybindings['up'] = { $c.historyBack() }
$c.keybindings['down'] = { $c.historyForward() }
$c.keybindings['left'] = { $c.cursorLeft() }
$c.keybindings['right'] = { $c.cursorRight() }

// cle
$c.keybindings['C-e'] = { $c.setCursorPos($c.getCursorLine().length()) }
$c.keybindings['C-a'] = { $c.setCursorPos(0) }

prompt = { '% ' }

// builtins

history = { $c.history.collectEntries { k,v -> [k,v.in] }.sequence() }
pow = { x, y -> Math.pow(x,y) }
prn = { $c.print(it) }
$name = { $w.nameTerminal(it) }

//n = { $w.newConsole() }
//c = { $w.closeConsole() }
//img = { new javax.swing.ImageIcon(it.toString()) }

infuse = { obj, opts ->
    opts.each { obj[it.key] = it.value }
    obj
}

java.lang.Object.metaClass.infuse = { opts ->
    //System.err.println("obj: ${delegate.toString()}");
    opts.each {
        //System.err.println("it.key: ${it.key}, it.value: ${it.value}");
        delegate[it.key] = it.value
        //System.err.println("res: ${delegate[it.key]}");
        //System.err.println("res: ${delegate.padding}");
    }
    //System.err.println("p2: ${delegate.padding}");
    //System.err.println("obj2: ${delegate.toString()}");
    delegate
}

table = { cols, list, opts=[:] ->
    infuse(new Table(cols, list), opts)
}

clear = { $c.clear() }


invert = {
    def f = $c.fg;
    def b = $c.bg;
    $c.fg = b;
    $c.bg = f;
    null
    //clear()
}

class RestXml {
    def xml_get(String url) {
        new XmlParser().parse(url)
    }
}

// metamethods

java.lang.String.metaClass.url = { delegate.toURL() }

javax.swing.JComponent.metaClass.label = {
    def p = new JPanel(new BorderLayout())
    p.add(delegate, BorderLayout.CENTER)
    p.add($r.color(new JLabel(it, SwingConstants.CENTER)), BorderLayout.SOUTH)
    return p
}

img = { u, opts=[:] ->
    infuse(new javafx.scene.image.ImageView(new javafx.scene.image.Image(u.toString())), opts)
}

label = { o, txt, opts=[:] ->
    System.err.println("labeling "+o);
    infuse(new Annotation(o, txt), opts)
}

javafx.scene.Node.metaClass.label = { text, opts=[:] ->
    infuse(new javafx.scene.control.Label(text, delegate), opts)
}

about = {
    img('/solace1.jpg').label(
'''Solace 1.0
(c) 2015 John K White
dhcmrlchtdj@gmail.com

adrift on earth where
the vast gulf between
wandering minds awake
wayward destinations,
console offers solace''', [style:"-fx-background-color:#444"])
}

java.util.List.metaClass.table = { cols, opts=[:] ->
    infuse(new Table(cols, delegate), opts)
}

java.util.List.metaClass.sequence = { opts=[:] ->
    infuse(new org.excelsi.solace.SequenceArrayList(delegate), opts)
}

java.util.Map.metaClass.sequence = { opts=[:] ->
    def l=[]
    delegate.each { l << "${it.key} => ${it.value}" }
    l.sequence opts
}

java.util.Iterator.metaClass.first = { cnt ->
    x=[]
    while(cnt-->0) { if(delegate.hasNext()) x << delegate.next() }
    x
}

java.util.Collection.metaClass.and = { c ->
    delegate.findAll { it.toString() ==~ c }
}

java.util.Collection.metaClass.where = { c -> delegate.findAll(c) }

//java.util.Collection.metaClass.gcs = {
    //def i=0
    //
//}

$c.renderer.link(Node, new org.excelsi.solace.Renderer() {
    JComponent render(Object o, String... context) {
        new JTextArea(editable:false, text:XmlUtil.serialize(o))
    }
})

imp org.excelsi.solace.Table

// JavaFX

sphere = { size=1 ->
    new javafx.scene.shape.Sphere(size)
}

getch = { time=-1 ->
    $c.getch(time)
}

pause = { time=-1 ->
    k = $c.getch(time)
    if(k=='escape') throw new RuntimeException("interrupted: '$k'")
}

java.lang.Object.metaClass.label = { txt, opts=[:] ->
    return label(delegate, txt, opts)
}

java.lang.Object.metaClass.fx = { opts ->
    return $r.remember(delegate, opts)
}

java.lang.Object.metaClass.style = { style ->
    /*if(delegate instanceof javafx.scene.Node) {
        delegate.getStyleClass().add(style);
    }*/
    return $r.remember(delegate, ['style':style])
}

java.lang.Object.metaClass.ctr = {
    return $r.remember(delegate, ['center':true])
}
