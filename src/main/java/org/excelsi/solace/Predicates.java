package org.excelsi.solace;


import java.util.function.Predicate;


public class Predicates {
    public static Predicate instof(Class c) {
        return (o -> { return c.isAssignableFrom(o.getClass()); });
    }
}
