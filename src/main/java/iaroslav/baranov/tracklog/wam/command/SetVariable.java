package iaroslav.baranov.tracklog.wam.command;

public record SetVariable(int register) implements Command{
    @Override
    public String toString() {
        return "set_variable X" + register;
    }
}
