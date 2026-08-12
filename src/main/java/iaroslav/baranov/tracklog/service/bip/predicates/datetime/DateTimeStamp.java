package iaroslav.baranov.tracklog.service.bip.predicates.datetime;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.FloatNumberTerm;
import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.predicates.FormatArgs;
import iaroslav.baranov.tracklog.service.bip.predicates.WrongPredicateArgumentTypeException;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.Substitution;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@BIP(indicator = "date_time_stamp/2")
public class DateTimeStamp implements BuildInPredicate<DateTimeStampArgs> {
    private final UnificationService unificationService;

    @Override
    public BuildInPredicateExecutionResult execute(DateTimeStampArgs args) {
        Term dateTime = args.DateTime();
        Term timeStamp = args.TimeStamp();

        if(dateTime instanceof CompoundTerm ct) {
            if(ct.getPrincipalFunctor().equals("date/9")) {
                IntegerTerm year = (IntegerTerm) ct.args().get(0);
                IntegerTerm month = (IntegerTerm) ct.args().get(1);
                IntegerTerm day = (IntegerTerm) ct.args().get(2);
                IntegerTerm hour = (IntegerTerm) ct.args().get(3);
                IntegerTerm minutes = (IntegerTerm) ct.args().get(4);
                IntegerTerm seconds = (IntegerTerm) ct.args().get(5);
                IntegerTerm offset = (IntegerTerm) ct.args().get(6);

                LocalDateTime localDateTime = LocalDateTime.of(
                        year.num(),
                        month.num(),
                        day.num(),
                        hour.num(),
                        minutes.num(),
                        seconds.num(),
                        0
                );
                ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(offset.num());
                long epochSeconds = localDateTime.toEpochSecond(zoneOffset);
                Term epochSecondsTerm = new FloatNumberTerm(epochSeconds);

                UnificationResult unificationResult = unificationService.unifyTermsOrThrow(timeStamp, epochSecondsTerm);
                return new BuildInPredicateExecutionResult(true, unificationResult.substitution());
            } else {
                throw new WrongPredicateArgumentTypeException("Not a date/9 term: " + dateTime.toCode());
            }
        } else {
            throw new WrongPredicateArgumentTypeException("Not a date/9 term: " + dateTime.toCode());
        }
    }
}
