package iaroslav.baranov.tracklog.service.db;

import iaroslav.baranov.tracklog.ast.term.Term;

import java.util.List;

public final class Procedure {

        private final ProcedureType type;
        private final String predicateIndicator; // A compound term A/N
        private final List<Term> clauses;
        private final boolean dynamic;
        private final boolean isPublic;
        private final Term term;

        public Procedure(
                ProcedureType type,
                String predicateIndicator,
                List<Term> clauses,
                boolean dynamic,
                boolean isPublic,
                Term term
        ) {
                this.type =  type;
                this.predicateIndicator = predicateIndicator;
                this.clauses = clauses;
                this.dynamic = dynamic;
                this.isPublic = isPublic;
                this.term = term;
        }

        public Procedure(
                ProcedureType type,
                String predicateIndicator,
                List<Term> clauses
        ) {
                this(
                        type,
                        predicateIndicator,
                        clauses,
                        true,
                        true,
                        null
                );
        }

        public ProcedureType type() {
                return type;
        }

        public String predicateIndicator() {
                return predicateIndicator;
        }

        public List<Term> clauses() {
                return clauses;
        }

        public boolean isPublic() {
                return isPublic;
        }

        public Term getTerm() {
                return term;
        }
}