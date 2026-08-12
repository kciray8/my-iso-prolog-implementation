package iaroslav.baranov.tracklog.wam.compile.tokens;

public record RegisterToken(
        int num,
        String name,
        int arity
) {
    public RegisterToken(int num){
        this(num,null,0);
    }

    @Override
    public String toString() {
        if(name == null){
            return "X" + num;
        } else {
            return "X" + num + " = " + name +"/" + arity;
        }
    }
}
