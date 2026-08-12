package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.LeftAndRight;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@BIP(indicator = "set_prolog_flag/2")
public class SetPrologFlag implements BuildInPredicate<SetPrologFlagArgs> {
    @Override
    public BuildInPredicateExecutionResult executeInContext(SetPrologFlagArgs args, ExecutionContext context) {
        Term key = args.Key();
        Term value = args.Value();

        context.addFlag((NamedAtom) key, value);

        return success();
    }
}
