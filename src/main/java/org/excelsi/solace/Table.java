package org.excelsi.solace;


import java.util.*;


public class Table extends ArrayList {
    private int _cols;
    private int _rows;
    private int _padding = 5;
    private Integer _margin = 5;
    private Boolean _grid;


    public Table(List data) {
        super(data);
        _cols = 5;
    }

    public Table(int cols, List data) {
        super(data);
        _cols = cols;
    }

    public Object get(int row, int col) {
        return get(row*_cols+col);
    }

    public int getRows() { return (int) Math.ceil(size()/(float)_cols); }

    public int getCols() { return _cols; }

    public void setColumns(int cols) { _cols = cols; }
    public int getColumns() { return _cols; }

    public int getPadding() { return _padding; }

    public void setPadding(int padding) { _padding = padding; }

    public Table padding(int padding) { _padding = padding; return this; }

    public Integer getMargin() {
        return _margin;
    }

    public void setMargin(Integer margin) {
        _margin = margin;
    }

    public Boolean getGrid() {
        return _grid;
    }

    public void setGrid(Boolean grid) {
        _grid = grid;
    }

    public String toString() {
        return super.toString()+"; cols="+getCols()
            +", rows="+getRows()+", padding="+getPadding();
    }
}
