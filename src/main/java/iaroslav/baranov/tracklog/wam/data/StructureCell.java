package iaroslav.baranov.tracklog.wam.data;

public record StructureCell(int ref) implements Cell {
    @Override
    public CellTag getCellTag() {
        return CellTag.STR;
    }

    @Override
    public String toString() {
        return "STR | " + ref;
    }
}
