package iaroslav.baranov.tracklog.service.unification;

import iaroslav.baranov.tracklog.unification.Substitution;

public record UnificationResult (
        Substitution substitution,
        boolean success
){
    UnificationResult(Substitution substitution) {
        this(substitution, true);
    }

    UnificationResult() {
        this(null, false);
    }
}
