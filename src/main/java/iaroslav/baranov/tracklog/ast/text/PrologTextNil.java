package iaroslav.baranov.tracklog.ast.text;

public record PrologTextNil() implements PrologText {
    @Override
    public String toCode() {
        return "";
    }
}
