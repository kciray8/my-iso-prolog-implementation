package iaroslav.baranov.tracklog.service.bip.predicates.io;

import iaroslav.baranov.tracklog.ast.term.*;
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
@BIP(indicator = "open/4")
public class Open implements BuildInPredicate<OpenArgs> {
    private final UnificationService unificationService;

    @Override
    public BuildInPredicateExecutionResult executeInContext(OpenArgs args, ExecutionContext context) {
        Term sink = args.Source_sink();
        Term mode = args.Mode();
        Term stream = args.Stream();
        Term options = args.Options();

        if(sink instanceof NamedAtom at) {
            try {
                InputStream is = Files.newInputStream(Path.of(context.getRoot().toString(), at.name()));
                Stream streamObj = new Stream(is);
                UnificationResult result = unificationService.unifyTermsOrThrow(stream,  streamObj);
                return success(result.substitution());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new PredicateExecutionException("Not supported");
        }
    }
}
