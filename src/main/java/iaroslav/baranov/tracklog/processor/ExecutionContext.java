package iaroslav.baranov.tracklog.processor;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.service.db.Procedure;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {
    private int uniqueVariablesCounter = 0;

    private Path root;

    private Map<NamedAtom, Term> flags = new HashMap<>();

    public void addFlag(NamedAtom flag, Term value) {
        flags.put(flag, value);
    }

    public Term getFlag(NamedAtom flag) {
        return flags.get(flag);
    }

    public boolean hasFlag(NamedAtom flag) {
        return flags.containsKey(flag);
    }

    public Path getRoot() {
        return root;
    }

    public void setRoot(Path root) {
        this.root = root;
    }

    public String generateUniqueVariableName(){
        return "_V" + uniqueVariablesCounter++;
    }
}
