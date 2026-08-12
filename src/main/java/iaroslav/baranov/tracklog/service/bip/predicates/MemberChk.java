package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.LeftAndRight;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@BIP(indicator = "memberchk/2")
public class MemberChk implements BuildInPredicate<MemberChkArg> {
    @Override
    public BuildInPredicateExecutionResult execute(MemberChkArg args) {
        Term elem = args.Elem();
        Term list = args.List();
        return check(elem, list);
    }

    BuildInPredicateExecutionResult check(Term elem, Term list) {
        if(list instanceof CompoundTerm ct && ct.getName().equals(".")) {
            Term e = ct.firstArg();
            Term innerList = ct.secondArg();
            if(e.equals(elem)) {
                return success();
            } else {
                return check(elem, innerList);
            }
        } else {
            return failure();
        }
    }
}
