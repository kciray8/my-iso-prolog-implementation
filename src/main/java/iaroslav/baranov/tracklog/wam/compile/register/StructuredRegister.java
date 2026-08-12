package iaroslav.baranov.tracklog.wam.compile.register;

import java.util.List;
import java.util.stream.Collectors;

public record StructuredRegister(
        int num,
        String functorName,
        List<Integer> variables
) implements Register {
    @Override
    public String toString() {
        return "X" + num + " = " + functorName + "(" +
                String.join(",", variables.stream()
                        .map(i -> "X" + i.toString())
                        .toList()) +
                ")";
    }
}
