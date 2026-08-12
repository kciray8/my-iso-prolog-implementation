package iaroslav.baranov.tracklog.wam.data;

public record FunctorCell(String name, int arity) implements Cell {
    @Override
    public CellTag getCellTag() {
        return null;
    }

    @Override
    public String toString() {
        return name + "/" + arity;
    }
}
