package iaroslav.baranov.tracklog.wam.command;

public record PutStructure(String functor, int arity, int register) implements Command{
    @Override
    public String toString() {
        return "put_structure " + functor + "/" + arity + ", X" +register;
    }

}
