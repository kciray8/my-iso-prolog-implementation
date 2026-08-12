package iaroslav.baranov.tracklog.wam.command;

public record SetValue(int register) implements Command {
    @Override
    public String toString() {
        return "set_value X" + register;
    }
}
