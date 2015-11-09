package org.excelsi.solace;


public interface Sink<E> {
    void write(E e);
    void close();
}
