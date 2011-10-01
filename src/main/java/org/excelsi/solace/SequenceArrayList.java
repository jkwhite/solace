package org.excelsi.solace;


import java.util.ArrayList;
import java.util.Collection;


public class SequenceArrayList<E> extends ArrayList<E> implements Sequence<E> {
    public SequenceArrayList() {
    }

    public SequenceArrayList(Collection<E> c) {
        super(c);
    }
}
