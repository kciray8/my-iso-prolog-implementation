package iaroslav.baranov.tracklog.service.bip.predicates.io;

import iaroslav.baranov.tracklog.ast.term.Stream;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.predicates.PredicateExecutionException;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@BIP(indicator = "get_char/2")
public class GetChar implements BuildInPredicate<GetCharArgs> {
    private final UnificationService unificationService;

    @Override
    public BuildInPredicateExecutionResult execute(GetCharArgs args) {
        Term streamTerm = args.S_or_a();
        Term ch = args.Char();
        if (streamTerm instanceof Stream s) {
            int charAsInt;
            do {
                charAsInt = s.read();
            } while (charAsInt == '\r');
            Term charTerm;
            if(charAsInt != -1) {
                String charValue = String.valueOf((char) charAsInt);
                charTerm = new NamedAtom(charValue);
            } else {
                charTerm = new NamedAtom("end_of_file");
            }
            UnificationResult result = unificationService.unifyTermsOrThrow(ch, charTerm);
            return success(result.substitution());
        } else {
            throw new PredicateExecutionException("stream is not a Stream term");
        }
    }
}
