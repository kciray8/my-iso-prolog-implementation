package iaroslav.baranov.tracklog.wam;

import iaroslav.baranov.tracklog.wam.data.Cell;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class MachineState {
    @Getter
    private Heap heap = new Heap();
    private List<Cell> variableRegisters = new ArrayList<>();

    @Getter
    @Setter
    private int h = 0;

    public void setHeapCell(int index, Cell cell) {
        heap.setCell(index, cell);
    }

    public Cell getHeapCell(int index) {
        return heap.get(index);
    }
    public void setVariableRegister(int index, Cell cell) {
        while(variableRegisters.size() <= index) {
            variableRegisters.add(null);
        }
        variableRegisters.set(index, cell);
    }

    public Cell getVariableRegister(int index) {
        return variableRegisters.get(index);
    }


}
