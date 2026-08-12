package iaroslav.baranov.tracklog.wam;

import iaroslav.baranov.tracklog.wam.data.Cell;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Heap {
    @Getter
    @Setter
    private List<Cell> heap = new ArrayList<>();

    public void setCell(int index, Cell cell) {
        if(heap.size() < index) {
            throw new WAMExecutionException("heap.size() < index");
        } else if (heap.size() == index) {
            heap.add(cell);
        } else {
            heap.set(index, cell);
        }
    }

    public Cell get(int index) {
        return heap.get(index);
    }

}
