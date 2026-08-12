package iaroslav.baranov.tracklog.service.bip.predicates.io;

import iaroslav.baranov.tracklog.ast.term.Stream;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.predicates.PredicateExecutionException;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@AllArgsConstructor
@BIP(indicator = "close/1")
public class Close implements BuildInPredicate<CloseArgs> {
    @Override
    public BuildInPredicateExecutionResult execute(CloseArgs args) {
        Term stream = args.Stream();
        if(stream instanceof Stream s) {
            s.close();
            return success();
        } else {
            throw new PredicateExecutionException("stream is not a Stream term");
        }
    }
}
