package iaroslav.baranov.tracklog.wam;

import iaroslav.baranov.tracklog.wam.command.Command;
import iaroslav.baranov.tracklog.wam.command.PutStructure;
import iaroslav.baranov.tracklog.wam.command.SetValue;
import iaroslav.baranov.tracklog.wam.command.SetVariable;
import iaroslav.baranov.tracklog.wam.data.Cell;
import iaroslav.baranov.tracklog.wam.data.FunctorCell;
import iaroslav.baranov.tracklog.wam.data.StructureCell;
import iaroslav.baranov.tracklog.wam.data.VariableCell;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MachineService {
    public void execute(List<Command> commands, MachineState state) {
        for(Command command : commands) {
            executeCommand(command, state);
        }
    }

    private void executeCommand(Command command, MachineState state) {
        if(command instanceof PutStructure putStructure) {
            int h = state.getH();
            state.setHeapCell(h, new StructureCell(h + 1));
            state.setHeapCell(h + 1, new FunctorCell(putStructure.functor(), putStructure.arity()));
            state.setVariableRegister(putStructure.register(), state.getHeap().get(h));
            state.setH(state.getH() + 2);
        }
        if(command instanceof SetVariable setVariable) {
            int h = state.getH();
            state.setHeapCell(h, new VariableCell(h));
            state.setVariableRegister(setVariable.register(), state.getHeap().get(h));
            state.setH(state.getH() + 1);
        }
        if(command instanceof SetValue setValue) {
            int h = state.getH();
            state.setHeapCell(h, state.getVariableRegister(setValue.register()));
            state.setH(state.getH() + 1);
        }
    }

    public String reconstructTerm(MachineState machineState, int root) {
        StringBuilder sb = new StringBuilder();
        Cell rootCell = machineState.getHeapCell(root);
        if(rootCell instanceof StructureCell sc) {
            int functorCellAddress = sc.ref();
            FunctorCell fc = (FunctorCell) machineState.getHeapCell(functorCellAddress);
            int lastArgAddress = functorCellAddress + fc.arity();
            sb.append(fc.name());
            List<String> subterms = new ArrayList<>();
            for(int addr = functorCellAddress + 1; addr <= lastArgAddress; addr++) {
                subterms.add(reconstructTerm(machineState, addr));
            }
            sb.append("(");
            sb.append(String.join(", ", subterms));
            sb.append(")");
        } else if (rootCell instanceof VariableCell v) {
            String varName = "X" + v.ref();
            sb.append(varName);
        }
        return sb.toString();
    }
}
