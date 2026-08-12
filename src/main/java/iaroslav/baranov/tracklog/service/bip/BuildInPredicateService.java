package iaroslav.baranov.tracklog.service.bip;

import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.processor.ExecutionException;
import iaroslav.baranov.tracklog.reflection.ReflectionService;
import iaroslav.baranov.tracklog.service.evaluation.EvaluationService;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.Substitution;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildInPredicateService {
    private final EvaluationService evaluationService;
    private final UnificationService unificationService;
    private final ReflectionService reflectionService;

    private final List<BuildInPredicate> allPredicates;
    @Getter
    private final Map<String, BuildInPredicate<? extends Args>> predicates = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        for (BuildInPredicate<? extends Args> predicate : allPredicates) {
            Class<?> targetClass = AopUtils.getTargetClass(predicate);
            BIP bip = AnnotationUtils.findAnnotation(targetClass, BIP.class);
            predicates.put(bip.indicator(), predicate);
        }
    }

    public BuildInPredicateExecutionResult execute(String indicator, Substitution unifier, ExecutionContext context) {
        if (predicates.containsKey(indicator)) {
            BuildInPredicate predicate = predicates.get(indicator);
            Class<?> argsClass = reflectionService.getClassOfFirstArgumentOfGeneric(
                    predicate.getClass(),
                    BuildInPredicate.class
            );
            Args args = (Args) reflectionService.createRecordFromSubstitution(argsClass, unifier);
            return predicate.executeInContext(args, context);
        }

        throw new ExecutionException("Predicate indicator not found among registered build-in predicates: " + indicator);
    }

}
