package iaroslav.baranov.tracklog.service.db;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.ast.text.PrologTextClause;
import iaroslav.baranov.tracklog.ast.text.PrologTextDirective;
import iaroslav.baranov.tracklog.ast.text.PrologTextNil;
import iaroslav.baranov.tracklog.lexer.Lexer;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.processor.CompleteDatabase;
import iaroslav.baranov.tracklog.reflection.ReflectionService;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateService;
import iaroslav.baranov.tracklog.service.operator.OperatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompleteDatabaseService {
    private final Lexer lexer;
    private final OperatorService operatorService;
    private final TermParser termParser;
    private final BuildInPredicateService buildInPredicateService;
    private final ReflectionService reflectionService;

    public void addUserDefinedProcedures(
            CompleteDatabase db,
            PrologText prologText
    ){
        switch (prologText){
            case PrologTextClause clause:
                addClause(db, clause);
                addUserDefinedProcedures(db, clause.text());
                break;
            case PrologTextDirective directive:
                break;
            case PrologTextNil nil:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + prologText);
        }
    }

    public void init(
            CompleteDatabase db,
            PrologText prologText
    ) {
        addUserDefinedProcedures(db, prologText);
        addControlStructures(db);
        addBuildInPredicates(db);
        findOrCreateByIndicator(db, "term_expansion/2", ProcedureType.USER_DEFINED_PROCEDURE);
        addEmptyProcedures(db);
    }

    public void init(
            CompleteDatabase db
    ) {
        addControlStructures(db);
        addBuildInPredicates(db);
        addEmptyProcedures(db);
    }

    private void addEmptyProcedures(CompleteDatabase db){
        findOrCreateByIndicator(db, "term_expansion/2", ProcedureType.USER_DEFINED_PROCEDURE);
    }

    private Procedure findOrCreateByIndicator(
            CompleteDatabase db,
            String predicateIndicator,
            ProcedureType procedureType
    ){
        if(db.getProceduresMap().containsKey(predicateIndicator)){
            return db.getProceduresMap().get(predicateIndicator);
        } else {
            Procedure procedure = new Procedure(procedureType, predicateIndicator, new ArrayList<>());
            db.getProceduresMap().put(predicateIndicator, procedure);
            return procedure;
        }
    }

    public Procedure findByIndicator(
            CompleteDatabase db,
            String predicateIndicator
    ){
        if(db.getProceduresMap().containsKey(predicateIndicator)){
            return db.getProceduresMap().get(predicateIndicator);
        } else {
            return null;
        }
    }

    public void addClause(
            CompleteDatabase db,
            PrologTextClause clause
    ) {
        Term term = clause.term();
        addClause(db, term);
    }

    public void addClause(
            CompleteDatabase db,
            Term term
    ) {
        String indicator = getPredicateIndicator(term);
        String principalFunctor = term.getPrincipalFunctor();

        Procedure procedure = findOrCreateByIndicator(db, indicator, ProcedureType.USER_DEFINED_PROCEDURE);
        if(principalFunctor.equals(":-/2")){
            procedure.clauses().add(term);
        } else {
            List<Term> args = new ArrayList<>();
            args.add(term);
            args.add(new NamedAtom("true"));

            procedure.clauses().add(new CompoundTerm(new NamedAtom(":-"), args));
        }
    }

    String getPredicateIndicator(Term term){
        String principalFunctor = term.getPrincipalFunctor();
        if(principalFunctor.equals(":-/2")){
            CompoundTerm ct = (CompoundTerm)term;
            return ct.args().get(0).getPrincipalFunctor();
        }

        return term.getPrincipalFunctor();
    }

    public Term reconstructTermFromPredicateIndicator(String predicateIndicator){
        String[] data = predicateIndicator.split("/");
        String identifier = data[0];
        int arity = Integer.parseInt(data[1]);
        return new CompoundTerm("/", List.of(new NamedAtom(identifier), new IntegerTerm(arity)));
    }

    public void addControlStructures(CompleteDatabase db) {
        addControlStructure(db,"true/0");
        addControlStructure(db,"fail/0");
        addControlStructure(db,",/2");
        addControlStructure(db,";/2");
        addControlStructure(db,"!/0");
        addControlStructure(db,"->/2");
        addControlStructure(db,"repeat/0");
        addControlStructure(db,"call/1");
        addControlStructure(db,"call/2");
        addControlStructure(db,"call/3");
    }

    public void addBuildInPredicates(CompleteDatabase db){
        addBuildInPredicate(db,parse("findall(Template, Goal, Bag)"));
        addBuildInPredicate(db,parse("\\+(Goal)"));
        addBuildInPredicate(db,parse("current_predicate(PI)"));
        addBuildInPredicate(db,parse("use_module(File)"));
        addBuildInPredicate(db,parse("op(Precedence, Type, Name)"));
        addBuildInPredicate(db,parse("assertz(Term)"));

        var predicates = buildInPredicateService.getPredicates();
        for(String indicator : predicates.keySet()){
            BuildInPredicate predicate = predicates.get(indicator);
            String[] indicatorComponents = indicator.split("/");
            String name = indicatorComponents[0];
            Class<?> cls = reflectionService.getClassOfFirstArgumentOfGeneric(
                    predicate.getClass(),
                    BuildInPredicate.class
            );
            Constructor<?> ctor = cls.getDeclaredConstructors()[0];
            Parameter[] parameters = ctor.getParameters();

            List<Term> variables = new ArrayList<>();
            for (int i = 0; i < parameters.length; i++) {
                String argName = parameters[i].getName();
                variables.add(new Variable(argName));
            }
            if(variables.isEmpty()) {
                addBuildInPredicate(db, new NamedAtom(name));
            } else {
                addBuildInPredicate(db, new CompoundTerm(name, variables));
            }
        }
    }

    void addControlStructure(
            CompleteDatabase db,
            String predicateIndicator
    ){
        Procedure trueProcedure = new Procedure(
                ProcedureType.CONTROL_CONSTRUCT,
                predicateIndicator,
                new ArrayList<>(),
                false,
                true,
                null);
        db.getProceduresMap().put(predicateIndicator, trueProcedure);
    }

    void addBuildInPredicate(
            CompleteDatabase db,
            Term term
    ){
        String predicateIndicator = term.getPrincipalFunctor();
        Procedure procedure = new Procedure(
                ProcedureType.BUILD_IN_PREDICATE,
                predicateIndicator,
                new ArrayList<>(),
                false,
                true,
                term);
        if(db.getProceduresMap().containsKey(predicateIndicator)){
            throw new IllegalArgumentException(predicateIndicator + " is already in the DB");
        }
        db.getProceduresMap().put(predicateIndicator, procedure);
    }

    public void print(CompleteDatabase db) {
        System.out.println("Complete database: ");
        for(Procedure procedure: db.getProceduresMap().values()){
            System.out.println("=== Procedure: " + procedure.predicateIndicator() + " ===");
            for(Term term: procedure.clauses()){
                System.out.println(term.toCode());
            }
            System.out.println();
        }
    }

    public Term parse(String str) {
        List<Token> tokens = lexer.tokenize(str);
        return termParser.parse(tokens);
    }
}
