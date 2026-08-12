package iaroslav.baranov.tracklog.wam.data;

public record VariableCell(int ref) implements Cell {
    @Override
    public CellTag getCellTag() {
        return CellTag.REF;
    }

    @Override
    public String toString() {
        return "REF | " + ref;
    }
}
