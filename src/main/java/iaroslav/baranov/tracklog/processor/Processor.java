package iaroslav.baranov.tracklog.processor;

import iaroslav.baranov.tracklog.ast.term.*;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.ast.text.PrologTextClause;
import iaroslav.baranov.tracklog.ast.text.PrologTextDirective;
import iaroslav.baranov.tracklog.ast.text.PrologTextNil;
import iaroslav.baranov.tracklog.parser.SourceParser;
import iaroslav.baranov.tracklog.parser.text.TextParser;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.predicates.backtracking.CurrentPredicateInfo;
import iaroslav.baranov.tracklog.service.db.CompleteDatabaseService;
import iaroslav.baranov.tracklog.service.db.Procedure;
import iaroslav.baranov.tracklog.processor.execution.stack.BacktrackInformation;
import iaroslav.baranov.tracklog.processor.execution.stack.ExecutionStack;
import iaroslav.baranov.tracklog.processor.execution.stack.ExecutionState;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateService;
import iaroslav.baranov.tracklog.service.TermService;
import iaroslav.baranov.tracklog.service.operator.OperatorService;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.Substitution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static iaroslav.baranov.tracklog.processor.ExecutionCommandType.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class Processor {
    private final UnificationService unificationService;
    private final TermService termService;
    private final BuildInPredicateService buildInPredicateService;
    private final CompleteDatabaseService completeDatabaseService;
    private final TextParser textParser;
    private final OperatorService operatorService;
    private final SourceParser sourceParser;

    public CompleteDatabase interpret(
            PrologText text,
            Path projectRoot
    ) {
        CompleteDatabase db = new CompleteDatabase();
        completeDatabaseService.addControlStructures(db);
        completeDatabaseService.addBuildInPredicates(db);

        ExecutionContext context = new ExecutionContext();
        context.setRoot(projectRoot);
        interpret(text, db, context);
        return db;
    }

    public void interpret(
            PrologText text,
            CompleteDatabase db,
            ExecutionContext context
    ) {
        switch (text) {
            case PrologTextClause clause:
                completeDatabaseService.addClause(db, clause);
                interpret(clause.text(), db, context);
                break;
            case PrologTextDirective directive:
                interpretDirective(directive, db, context);
                interpret(directive.text(), db, context);
                break;
            case PrologTextNil nil:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + text);
        }
    }

    private void interpretDirective(
            PrologTextDirective directive,
            CompleteDatabase db,
            ExecutionContext context
    ) {
        Term goal = directive.getDirectiveContent();
        interpretDirective(goal, db, context);
    }

    public void interpretDirective(
            Term directive,
            CompleteDatabase db,
            ExecutionContext context
    ) {
        long ms = System.currentTimeMillis();
        ExecutionResult executionResult = execute(directive, db, context);
        if (!executionResult.success()) {
            throw new ExecutionException("The following directive wasn't executed successfully: " + directive.toCode());
        }
        log.info("Directive {} executed successfully", directive.toCode());
        int size = executionResult.substitution().getSize();
        log.info("Size of map = {}", executionResult.substitution().getSize());
        log.info("Elapsed " +  (System.currentTimeMillis() - ms) + " ms");
        if(size == 1514) {
            System.out.println();
        }
    }

    public ExecutionResult execute(Term goal, CompleteDatabase db, ExecutionContext executionContext) {

        if (log.isDebugEnabled()) {
            log.debug("Execution requested: {}", goal.toCode());
        }

        ExecutionStack executionStack = new ExecutionStack();
        executionStack.initialize(goal);

        ExecutionCommand command = ExecutionCommand.selectProcedureForExecution();
        while (command.getType() != HALT) {
            command = executeCommand(command, executionStack, executionContext, db);
        }

        return prepareExecutionResult(executionContext, executionStack);
    }

    ExecutionCommand executeCommand(
            ExecutionCommand command,
            ExecutionStack executionStack,
            ExecutionContext executionContext,
            CompleteDatabase db) {
        if (command.getType() == SELECT_PROCEDURE_FOR_EXECUTION) {
            command = selectProcedureForExecution(executionStack, executionContext, db);
        } else if (command.getType() == EXECUTE_CONTROL_STRUCTURE) {
            command = executeControlStructure(executionStack, executionContext, db);
        } else if (command.getType() == EXECUTE_BUILD_IN_PREDICATE) {
            command = executeBuildInPredicate(command.getProcedure(), executionStack, executionContext, db);
        } else if (command.getType() == EXECUTE_USER_DEFINED_PROCEDURE) {
            command = executeUserDefinedProcedure(executionStack, executionContext, db);
        } else if (command.getType() == EXECUTE_USER_DEFINED_PROCEDURE_NO_MORE_CLAUSES) {
            command = executeUserDefinedProcedureNoMoreClauses(executionStack, executionContext, db);
        } else if (command.getType() == BACKTRACKING) {
            command = backtracking(executionStack, executionContext, db);
        } else if (command.getType() == EXECUTE_CUT) {
            command = executeCut(executionStack, executionContext, db);
        }
        return command;
    }

    ExecutionResult prepareExecutionResult(ExecutionContext context,
                                           ExecutionStack s) {
        if (s.isEmpty()) {
            return new ExecutionResult(); //A goal fails
        }

        ExecutionState es = s.currentState();
        if (es != null && es.getCurrentGoal().getDecoratedSubgoalStack().isEmpty()) {
            return new ExecutionResult(es.getSubstitution(), context, s); //A goal succeeds
        }
        throw new ExecutionException("Weird execution result");
    }

    public ExecutionResult reexecute(
            ExecutionContext context,
            ExecutionStack s,
            CompleteDatabase db
    ) {
        s.popCurrentState();

        ExecutionCommand command = ExecutionCommand.backtracking();
        while (command.getType() != HALT) {
            command = executeCommand(command, s, context, db);
        }

        return prepareExecutionResult(context, s);
    }

    //7.7.7  Selecting a procedure for execution
    private ExecutionCommand selectProcedureForExecution(
            ExecutionStack s,
            ExecutionContext context,
            CompleteDatabase db
    ) {
        log.debug("7.7.7 Selecting a procedure for execution");

        //Not in the standard algorithm but must be there (SHOULD IT? maybe remove it later)
        if (s.isEmpty()) {
            log.debug("Stack is empty, returning from 7.7.7");
            return ExecutionCommand.halt();
        }
        //Not in the standard algorithm but must be there (SHOULD IT? maybe remove it later)
        if (s.currentState().getCurrentGoal().getDecoratedSubgoalStack().isEmpty()) {
            log.debug("Decorated subgoals stack is empty, returning from 7.7.7");
            return ExecutionCommand.halt();
        }

        String activatorPF = s.currentActivator().getPrincipalFunctor();
        Procedure p = completeDatabaseService.findByIndicator(db, activatorPF);
        if (p == null) {
            throw new ExistenceErrorException("No procedure has a functor and arity agreeing " +
                    "with the functor and arity of current activator: " + activatorPF);
        }

        log.debug("Found a procedure: type = {}, predicate indicator = {}", p.type(), p.predicateIndicator());

        switch (p.type()) {
            case CONTROL_CONSTRUCT -> {
                s.updateBacktrackInfoToCtrl();
                return ExecutionCommand.executeControlStructure();
            }
            case BUILD_IN_PREDICATE -> {
                s.updateBacktrackInfoToBip();
                return ExecutionCommand.executeBuildInPredicate(p);
            }
            case USER_DEFINED_PROCEDURE -> {
                s.updateBacktrackInfoToUp(p.clauses());
                return ExecutionCommand.executeUserDefinedProcedure();
            }
        }
        return ExecutionCommand.halt();
    }

    //7.7.12 Executing a built-in predicate
    ExecutionCommand executeBuildInPredicate(
            Procedure p,
            ExecutionStack s,
            ExecutionContext context,
            CompleteDatabase db
    ) {
        Term activator = s.currentActivator();
        Term predicateTerm = p.getTerm();
        UnificationResult result = unificationService.unifyTerms(predicateTerm, activator);
        if (result.success()) {
            Substitution mgu = result.substitution();
            ExecutionState currentStateCopy = s.currentState().copyWithIncrement();
            currentStateCopy.incrementIndex();
            s.push(currentStateCopy);

            String predicateIndicator = p.predicateIndicator();
            BuildInPredicateExecutionResult executionResult;
            if (predicateIndicator.equals("findall/3")) {
                executionResult = executeFindAll(db, mgu, context);
            } else if (predicateIndicator.equals("op/3")) {
                executionResult = executeOp(db, mgu, context);
            } else if (predicateIndicator.equals("\\+/1")) {
                executionResult = executeNegationAsFailure(db, mgu, context);
            } else if (predicateIndicator.equals("current_predicate/1")) {
                executionResult = executeCurrentPredicatePredicate(db, mgu, context, s);
            } else if (predicateIndicator.equals("assertz/1")) {
                executionResult = executeAssertZ(db, mgu, context, s);
            } else if (predicateIndicator.equals("use_module/1")) {
                executionResult = executeUseModulePredicate(db, mgu, context, s);
            } else {
                executionResult = buildInPredicateService.execute(predicateIndicator, mgu, context);
            }
            if (executionResult.success()) {
                Substitution substitution = executionResult.substitution();
                currentStateCopy.getCurrentGoal().substitute(substitution);
                currentStateCopy.getSubstitution().addAll(substitution);
                currentStateCopy.replaceCurrentSubgoal(trueAtom(), s.currentState().getChoicePoint());
            } else {
                currentStateCopy.replaceCurrentSubgoal(failAtom(), s.currentState().getChoicePoint());
            }
            return ExecutionCommand.selectProcedureForExecution();
        }
        return ExecutionCommand.halt();
    }

    private BuildInPredicateExecutionResult executeAssertZ(
            CompleteDatabase db,
            Substitution mgu,
            ExecutionContext context,
            ExecutionStack s
    ) {
        Term term = mgu.get("Term");
        completeDatabaseService.addClause(db, term);

        return success();
    }

    NamedAtom trueAtom() {
        return new NamedAtom("true");
    }

    NamedAtom failAtom() {
        return new NamedAtom("fail");
    }

    BuildInPredicateExecutionResult executeCurrentPredicatePredicate(
            CompleteDatabase db,
            Substitution mgu,
            ExecutionContext executionContext,
            ExecutionStack s
    ) {
        List<Term> terms = new LinkedList<>();
        Term predicateIndicator = mgu.get("PI");
        for(String indicator: db.getIndicators()){
            Term term = completeDatabaseService.reconstructTermFromPredicateIndicator(indicator);
            if(unificationService.unifyTerms(term, predicateIndicator).success()){
                terms.add(term);
            }
        }
        CurrentPredicateInfo currentPredicateInfo = new CurrentPredicateInfo(terms);
        BacktrackInformation bi = s.currentState().getBacktrackInformation();
        bi.setPredicateBacktrackingInfo(currentPredicateInfo);

        Substitution substitution = unificationService.unifyTerms(predicateIndicator, terms.get(0)).substitution();
        return success(substitution);
    }

    BuildInPredicateExecutionResult executeUseModulePredicate(
            CompleteDatabase db,
            Substitution mgu,
            ExecutionContext context,
            ExecutionStack s
    ) {
        Term fileTerm = mgu.get("File");
        String fileName = ((NamedAtom) fileTerm).name();
        Path moduleSource = context.getRoot().resolve(fileName + ".pl");

        String srcCode = readString(moduleSource);
        sourceParser.parse(
                srcCode,
                term -> {
                    Term expandedTerm = expandTerm(term, db, context);
                    if (expandedTerm.getPrincipalFunctor().equals(":-/1")) {
                        CompoundTerm ct = (CompoundTerm) expandedTerm;
                        interpretDirective(ct.firstArg(), db, context);
                    } else {
                        completeDatabaseService.addClause(db, expandedTerm);
                    }
                }
        );

        return success();
    }

    public Term expandTerm(Term term, CompleteDatabase db, ExecutionContext context) {
        Term expandedTerm = term;
        Variable outputVariable = new Variable("OutputVariable");
        Term termExpansionGoal =
                new CompoundTerm("term_expansion", List.of(term, outputVariable));
        ExecutionResult executionResult = execute(termExpansionGoal, db, context);
        if(executionResult.success()) {
            expandedTerm = executionResult.substitution().get("OutputVariable");
        }
        return expandedTerm;
    }

    String readString(Path src) {
        try {
            return Files.readString(src);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    BuildInPredicateExecutionResult executeNegationAsFailure(
            CompleteDatabase db,
            Substitution mgu,
            ExecutionContext executionContext
    ) {
        Term goal = mgu.get("Goal");
        ExecutionResult result = execute(goal, db, executionContext);
        if (result.success()) {
            return failure();
        } else {
            return success();
        }
    }

    BuildInPredicateExecutionResult executeFindAll(
            CompleteDatabase db,
            Substitution mgu,
            ExecutionContext context
    ) {
        Term goal = mgu.get("Goal");
        Term template = mgu.get("Template");
        Term bag = mgu.get("Bag");
        boolean firstRun = true;
        ExecutionResult result = null;
        List<Term> terms = new ArrayList<>();

        while (true) {
            if (firstRun) {
                result = execute(goal, db, context);
            } else {
                result = reexecute(result.executionContext(), result.executionStack(), db);
            }

            if (result.success()) {
                Term substitutedTemplate = template.substitute(result.substitution());
                terms.add(substitutedTemplate);
            } else {
                break;
            }

            firstRun = false;
        }

        Term list = termService.convertToFunctionalList(terms);
        UnificationResult unificationResult = unificationService.unifyTerms(bag, list);

        return success(unificationResult.substitution());
    }

    BuildInPredicateExecutionResult executeOp(
            CompleteDatabase db,
            Substitution mgu,
            ExecutionContext context
    ) {
        Term precedenceArg = mgu.get("Precedence");
        Term typeArg = mgu.get("Type");
        Term nameArg = mgu.get("Name");

        int precedence = ((IntegerTerm) precedenceArg).num();
        String name = ((NamedAtom) nameArg).name();
        String associativity = ((NamedAtom) typeArg).name();

        operatorService.addOperator(name, precedence, associativity);

        return success();
    }

    BuildInPredicateExecutionResult success(Substitution substitution) {
        return new BuildInPredicateExecutionResult(true, substitution);
    }

    BuildInPredicateExecutionResult success() {
        return new BuildInPredicateExecutionResult(true, new Substitution());
    }

    BuildInPredicateExecutionResult failure() {
        return new BuildInPredicateExecutionResult(false, new Substitution());
    }

    ExecutionCommand executeControlStructure(
            ExecutionStack s,
            ExecutionContext context,
            CompleteDatabase db
    ) {
        Term currentActivator = s.currentActivator();
        String activatorPF = currentActivator.getPrincipalFunctor();

        if (activatorPF.equals("true/0")) {
            s.popCurrentDecoratedGoal();
            s.updateBacktrackInfoToNil();
            return ExecutionCommand.selectProcedureForExecution();
        } else if (activatorPF.equals("fail/0")) {
            s.popCurrentState();
            return ExecutionCommand.backtracking();
        } else if (activatorPF.equals(",/2")) {
            CompoundTerm activatorAsCompundTerm = (CompoundTerm) currentActivator;
            List<Term> activatorArgs = activatorAsCompundTerm.args();
            ExecutionState currentStateCopy = s.currentState().copyWithIncrement();
            currentStateCopy.replaceCurrentSubgoal(activatorArgs, s.currentDecoratedSubgoal().cutParent());
            currentStateCopy.setBacktrackInformation(BacktrackInformation.nil());
            currentStateCopy.incrementIndex();
            s.push(currentStateCopy);
            return ExecutionCommand.selectProcedureForExecution();
        } else if (activatorPF.equals("!/0")) {
            return ExecutionCommand.executeCut();
        } else if (activatorPF.equals("->/2")) {
            ExecutionState currentStateCopy = s.currentState().copyWithIncrement();
            currentStateCopy.setBacktrackInformation(BacktrackInformation.nil());
            int cp = currentStateCopy.getCurrentGoal().getCurrentCutParent();
            currentStateCopy.getCurrentGoal().popCurrentDecoratedGoal();
            int nn = s.currentState().getChoicePoint();
            CompoundTerm activatorAsCompundTerm = (CompoundTerm) currentActivator;
            List<Term> activatorArgs = activatorAsCompundTerm.args();
            Term ifTerm = activatorArgs.get(0);
            Term thenTerm = activatorArgs.get(1);
            currentStateCopy.getCurrentGoal().pushDecoratedSubgoal(thenTerm, cp);
            Term cutTerm = new NamedAtom("!");
            currentStateCopy.getCurrentGoal().pushDecoratedSubgoal(cutTerm, nn);
            currentStateCopy.getCurrentGoal().pushDecoratedSubgoal(ifTerm, nn);
            s.push(currentStateCopy);
            return ExecutionCommand.selectProcedureForExecution();
        } else if (activatorPF.equals(";/2")) {
            CompoundTerm activatorAsCompundTerm = (CompoundTerm) currentActivator;
            List<Term> activatorArgs = activatorAsCompundTerm.args();
            Term firstArgument = activatorArgs.get(0);
            if (firstArgument.getPrincipalFunctor().equals("->/2")) {
                CompoundTerm firstArgumentCT = (CompoundTerm) firstArgument;
                List<Term> firstArgumentArgs = firstArgumentCT.args();
                Term ifTerm = firstArgumentArgs.get(0);
                Term thenTerm = firstArgumentArgs.get(1);
                Term elseTerm = activatorArgs.get(1);
                ExecutionState currentStateCopy = s.currentState().copyWithIncrement();
                currentStateCopy.setBacktrackInformation(BacktrackInformation.nil());
                int cp = currentStateCopy.getCurrentGoal().getCurrentCutParent();
                currentStateCopy.getCurrentGoal().popCurrentDecoratedGoal();
                int n = s.currentState().getIndex();
                int nn = s.currentState().getChoicePoint();
                currentStateCopy.getCurrentGoal().pushDecoratedSubgoal(thenTerm, cp);
                Term cutTerm = new NamedAtom("!");
                currentStateCopy.getCurrentGoal().pushDecoratedSubgoal(cutTerm, nn);
                currentStateCopy.getCurrentGoal().pushDecoratedSubgoal(ifTerm, n);
                s.push(currentStateCopy);
                return ExecutionCommand.selectProcedureForExecution();
            } else {
                throw new ExecutionException("Not implemented");
            }
        } else if (activatorPF.startsWith("call/")) {
            CompoundTerm activatorAsCompundTerm = (CompoundTerm) currentActivator;
            List<Term> activatorArgs = activatorAsCompundTerm.args();
            Term g =  activatorArgs.get(0);
            List<Term> callArgs = activatorArgs.subList(1, activatorArgs.size());
            ExecutionState currentStateCopy = s.currentState().copyWithIncrement();
            currentStateCopy.setBacktrackInformation(BacktrackInformation.nil());
            currentStateCopy.getCurrentGoal().popCurrentDecoratedGoal();
            if(g instanceof Variable) {
                throw new InstantiationErrorException("Variable passed to call/N: " + g.toCode());
            }
            if(g instanceof NumericTerm) {
                throw new TypeErrorException("Number passed to call/N: " + g.toCode());
            }
            Term gAppended = appendArguments(g, callArgs);
            Term goal = convertToGoal(gAppended);
            int nn = s.currentState().getChoicePoint();
            currentStateCopy.getCurrentGoal().pushDecoratedSubgoal(goal, nn);
            s.push(currentStateCopy);
            return ExecutionCommand.selectProcedureForExecution();
        } else if (activatorPF.equals("repeat/0")) {
            ExecutionState currentStateCopy = s.currentState().copyWithIncrement();
            currentStateCopy.replaceCurrentSubgoal(new NamedAtom("true"), s.currentState().getChoicePoint());
            s.push(currentStateCopy);
            return ExecutionCommand.selectProcedureForExecution();
        } else {
            throw new ExecutionException("No execution branch for the control structure: " + activatorPF);
        }
    }

    private Term appendArguments(Term term, List<Term> args) {
        if(args.isEmpty()) {
            return term;
        } else {
            if(term instanceof CompoundTerm ct) {
                List<Term> updatedArgs = new ArrayList<>();
                updatedArgs.addAll(ct.args());
                updatedArgs.addAll(args);
                return new CompoundTerm(ct.atom(), updatedArgs);
            } else if(term instanceof NamedAtom na) {
                return new CompoundTerm(na, args);
            } else {
                throw new ExecutionException("Trying to append arguments to an unrecognized term: " + term);
            }
        }
    }

    //7.6.2 Converting a term to the body of a clause
    Term convertToGoal(Term term) {
        if(term instanceof Variable) {
            return new CompoundTerm("call", List.of(term));
        }
        //TODO b) recursive fill up from table
        return term;
    }

    //7.8.4 !/O - cut
    ExecutionCommand executeCut(
            ExecutionStack s,
            ExecutionContext context,
            CompleteDatabase db
    ) {
        ExecutionState copyOfCurrentState = s.currentState().copyWithIncrement();
        copyOfCurrentState.setBacktrackInformation(BacktrackInformation.nil());
        copyOfCurrentState.replaceCurrentSubgoal(new NamedAtom("true"), s.currentState().getChoicePoint());
        s.push(copyOfCurrentState);
        return ExecutionCommand.selectProcedureForExecution();
    }

    //7.7.11
    private ExecutionCommand executeUserDefinedProcedureNoMoreClauses(
            ExecutionStack s,
            ExecutionContext context,
            CompleteDatabase db
    ) {
        log.debug("No more clauses in BI, so the current state will be popped and backtracking performed");
        s.popCurrentState();
        return ExecutionCommand.backtracking();
    }

    //7.7.10
    private ExecutionCommand executeUserDefinedProcedure(ExecutionStack s, ExecutionContext context, CompleteDatabase db) {
        if (log.isDebugEnabled()) {
            log.debug("7.7.10 Executing a user-defined procedure");
            s.log("before 7.7.10");
        }

        BacktrackInformation backtrackInformation = s.currentState().getBacktrackInformation();
        boolean noMoreClauses = backtrackInformation.noClauses();

        if (noMoreClauses) {
            return ExecutionCommand.executeUserDefinedProcedureNoMoreClauses();
        } else {
            List<Term> backtrackClauses = backtrackInformation.getClauses();
            //Preparing up([c|CT])
            Term clause = backtrackClauses.get(0);//c
            Term headOfClause = clause.getHead();
            List<Term> otherClauses = new ArrayList<>(backtrackClauses.subList(1, backtrackClauses.size()));//CT

            Term currentActivator = s.currentActivator();
            UnificationResult unificationResult = unificationService.unifyTerms(headOfClause, currentActivator);
            if (unificationResult.success()) {
                Term renamedClause = termService.makeRenamedCopy(clause, context);
                Term headOfRenamedClause = renamedClause.getHead();
                UnificationResult secondUnification =
                        unificationService.unifyTerms(headOfRenamedClause, currentActivator);
                if (secondUnification.success()) {
                    Term bodyOfRenamedClause = renamedClause.getBody();
                    Substitution mguSubstitution = secondUnification.substitution();
                    Substitution currentStateSubstitution = s.currentState().getSubstitution();
                    Map<String, Term> substitutionMap = mguSubstitution.getMap();

                    Term bodyAfterSubstitution = bodyOfRenamedClause.substitute(substitutionMap);
                    if (log.isDebugEnabled()) {
                        log.debug("Body after substitution: {}", bodyAfterSubstitution.toCode());
                    }
                    ExecutionState stateCopy = s.copyOfCurrentState();
                    stateCopy.substituteInCurrentGoal(mguSubstitution);
                    stateCopy.replaceCurrentSubgoal(bodyAfterSubstitution, s.currentState().getChoicePoint());
                    stateCopy.setBacktrackInformation(BacktrackInformation.nil());
                    stateCopy.setSubstitution(currentStateSubstitution.compose(mguSubstitution));
                    stateCopy.incrementIndex();
                    s.push(stateCopy);

                    if (log.isDebugEnabled()) {
                        s.log("after 7.7.10");
                    }

                    return ExecutionCommand.selectProcedureForExecution();
                } else {
                    throw new ExecutionException("The head of clause was unifiable, but the head of renamed clause isn't");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Not unifiable: {} and {}", headOfClause.toCode(), currentActivator.toCode());
                }
                s.updateBacktrackInfoToUp(otherClauses); //BI = CT
                return ExecutionCommand.executeUserDefinedProcedure();
            }
        }
    }

    //7.7.8
    private ExecutionCommand backtracking(ExecutionStack s, ExecutionContext context, CompleteDatabase db) {
        log.debug("7.7.8 Backtracking");

        if (s.isEmpty()) {
            return ExecutionCommand.halt();
        }

        if (s.currentState().getCurrentGoal().getDecoratedSubgoalStack().isEmpty()) {
            log.debug("Decorated subgoals stack is empty, returning from 7.7.8");
            return ExecutionCommand.halt();
        }

        BacktrackInformation bi = s.currentState().getBacktrackInformation();
        switch (bi.getType()) {
            case UP -> {
                s.removeHeadOfClauseList();
                return ExecutionCommand.executeUserDefinedProcedure();
            }
            case BIP -> {
                if(bi.getPredicateBacktrackingInfo() != null) {
                    return backtrackingForPredicates(s, context, db, bi);
                } else {
                    s.currentState().replaceCurrentSubgoal(failAtom(), s.currentState().getChoicePoint());
                }
                return ExecutionCommand.selectProcedureForExecution();
            }
            case NIL -> {
                return ExecutionCommand.selectProcedureForExecution();
            }
            case CTRL -> {
                return backtrackingForControlStructures(s, context, db);
            }
        }
        return ExecutionCommand.halt();
    }

    private ExecutionCommand backtrackingForPredicates(
            ExecutionStack s,
            ExecutionContext context,
            CompleteDatabase db,
            BacktrackInformation bi
    ) {
        if(bi.getPredicateBacktrackingInfo() instanceof CurrentPredicateInfo cpi) {
            List<Term> predicateNames = cpi.getCollectedPredicateNames();
            predicateNames.removeFirst();

            if(predicateNames.isEmpty()) {
                s.currentState().replaceCurrentSubgoal(new NamedAtom("fail"), s.currentState().getChoicePoint());
            } else {
                Term predicateName = predicateNames.getFirst();
                CompoundTerm activator = (CompoundTerm) s.currentActivator();
                Term firstArg = activator.args().getFirst();
                Substitution substitution = unificationService.unifyTerms(predicateName, firstArg).substitution();
                s.currentState().getSubstitution().addAll(substitution);
                s.currentState().replaceCurrentSubgoal(new NamedAtom("true"), s.currentState().getChoicePoint());
            }
            return ExecutionCommand.selectProcedureForExecution();
        } else {
            throw new ExecutionException("The predicate backtracking info type isn't recognised: " +
                    bi.getPredicateBacktrackingInfo());
        }
    }

    private ExecutionCommand backtrackingForControlStructures(ExecutionStack s, ExecutionContext context, CompleteDatabase db) {
        String activatorFunctor = s.currentActivator().getPrincipalFunctor();
        if (activatorFunctor.equals(",/2")) {
            s.popCurrentState();
            return ExecutionCommand.backtracking();
        } else if (activatorFunctor.equals("!/0")) {
            int cut = s.currentState().getCurrentGoal().getCurrentCutParent();
            s.popCurrentState();
            while (!s.isEmpty() && cut < s.currentState().getIndex()) {
                s.popCurrentState();
            }
            return ExecutionCommand.backtracking();
        } else if (activatorFunctor.equals("->/2")) {
            s.popCurrentState();
            return ExecutionCommand.backtracking();
        } else if (activatorFunctor.equals(";/2")) {
            CompoundTerm activatorAsCompundTerm = (CompoundTerm) s.currentActivator();
            List<Term> activatorArgs = activatorAsCompundTerm.args();
            Term firstArgument = activatorArgs.get(0);
            if (firstArgument.getPrincipalFunctor().equals("->/2")) {
                CompoundTerm firstArgumentCT = (CompoundTerm) firstArgument;
                List<Term> firstArgumentArgs = firstArgumentCT.args();
                Term ifTerm = firstArgumentArgs.get(0);
                Term thenTerm = firstArgumentArgs.get(1);
                Term elseTerm = activatorArgs.get(1);
                ExecutionState currentStateCopy = s.currentState().copyWithIncrement();
                currentStateCopy.setBacktrackInformation(BacktrackInformation.nil());
                int cp = currentStateCopy.getCurrentGoal().getCurrentCutParent();
                currentStateCopy.getCurrentGoal().popCurrentDecoratedGoal();
                int n = s.currentState().getIndex();
                int nn = s.currentState().getChoicePoint();
                currentStateCopy.getCurrentGoal().pushDecoratedSubgoal(elseTerm, cp);
                Term cutTerm = new NamedAtom("!");
                currentStateCopy.getCurrentGoal().pushDecoratedSubgoal(cutTerm, nn);
                s.push(currentStateCopy);
                return ExecutionCommand.selectProcedureForExecution();
            } else {
                throw new ExecutionException("Not implemented");
            }
        } else if (activatorFunctor.equals("repeat/0")) {
            ExecutionState currentStateCopy = s.currentState().copyWithIncrement();
            currentStateCopy.replaceCurrentSubgoal(new NamedAtom("true"), s.currentState().getChoicePoint());
            s.push(currentStateCopy);
            return ExecutionCommand.selectProcedureForExecution();
        } else if (activatorFunctor.startsWith("call/")) {
            s.popCurrentState();
            return ExecutionCommand.backtracking();
        } else {
            throw new RuntimeException("No 'if' branch to backtrack from " + activatorFunctor);
        }
    }
}