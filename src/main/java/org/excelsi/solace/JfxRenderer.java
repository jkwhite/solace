package org.excelsi.solace;


import javafx.scene.Node;


public interface JfxRenderer {
    Node render(Object o, Painter p, JfxRendererRegistry renderers);
}
