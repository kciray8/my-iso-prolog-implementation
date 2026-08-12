package iaroslav.baranov.tracklog.wam.compile.register;

public record VariableRegister(int num, String varName) implements Register {
    @Override
    public String toString() {
        return "X" + num + " = " + varName;
    }
}
