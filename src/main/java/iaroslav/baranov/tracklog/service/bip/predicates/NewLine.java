package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@BIP(indicator = "nl/0")
public class NewLine implements BuildInPredicate<NoArgs> {
    @Override
    public BuildInPredicateExecutionResult execute(NoArgs args) {
        System.out.println();
        return success();
    }
}
