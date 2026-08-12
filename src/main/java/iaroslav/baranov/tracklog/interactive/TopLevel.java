package iaroslav.baranov.tracklog.interactive;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.ide.outer.SourceWatcher;
import iaroslav.baranov.tracklog.parser.SourceParser;
import iaroslav.baranov.tracklog.processor.CompleteDatabase;
import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.service.ExecutionResultService;
import iaroslav.baranov.tracklog.service.db.CompleteDatabaseService;
import iaroslav.baranov.tracklog.lexer.Lexer;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.parser.text.TextParser;
import iaroslav.baranov.tracklog.processor.ExecutionResult;
import iaroslav.baranov.tracklog.processor.Processor;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateService;
import iaroslav.baranov.tracklog.service.TermService;
import iaroslav.baranov.tracklog.service.operator.OperatorService;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.Substitution;
import iaroslav.baranov.tracklog.web.security.SecurityConfig;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication(
        scanBasePackages = "iaroslav.baranov.tracklog",
        exclude = {
                DataSourceAutoConfiguration.class,
                SecurityAutoConfiguration.class
        }
)
@AllArgsConstructor
@Slf4j
@Profile("toplevel")
public class TopLevel implements CommandLineRunner {
    static void main(String[] args) {
        SpringApplication app = new SpringApplication(TopLevel.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    private final Lexer lexer;
    private final OperatorService operatorService;
    private final TermParser termParser;
    private final TextParser textParser;
    private final SourceParser sourceParser;
    private final UnificationService unificationService;
    private final TermService termService;
    private final BuildInPredicateService buildInPredicateService;
    private final Processor processor;
    private final CompleteDatabaseService completeDatabaseService;
    private final ExecutionResultService executionResultService;

    @Override
    public void run(String... args) throws Exception {
        println("Toplevel is running...");

        Path sourcePath = Paths.get("C:\\Users\\kcira\\sys\\projects\\TrackLog\\ide\\kb.pl");
        String code = Files.readString(sourcePath);
        System.out.println("Code:");
        System.out.println(code);

        CompleteDatabase db = new CompleteDatabase();
        completeDatabaseService.init(db);
        Path root = sourcePath.getParent();

        ExecutionContext context = new ExecutionContext();
        context.setRoot(root);
        sourceParser.parse(
                code,
                term -> {
                    Term expandedTerm = processor.expandTerm(term, db, context);
                    if (expandedTerm.getPrincipalFunctor().equals(":-/1")) {
                        CompoundTerm ct = (CompoundTerm) expandedTerm;
                        processor.interpretDirective(ct.firstArg(), db, context);
                    } else {
                        completeDatabaseService.addClause(db, expandedTerm);
                    }
                }
        );

        inputLoop(db);
    }

    private void inputLoop(CompleteDatabase db){
        ExecutionContext context = new ExecutionContext();
        Scanner scanner = new Scanner(System.in);
        String predefinedInput = null;
        while (true){
            print("?- ");
            String expression;
            if(predefinedInput != null) {
                expression = predefinedInput;
                println(predefinedInput);
                predefinedInput = null;
            } else {
                expression = scanner.nextLine();
            }
            try {
                List<Token> tokens = lexer.tokenize(expression);
                Term expressionTerm = termParser.parse(tokens);
                println("Parsed as: " + expressionTerm.toCode());
                try {
                    tryToSatisfy(expressionTerm, db, context);
                }catch(Exception e){
                    println("Execution error: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e){
                println("Parsing error: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    void tryToSatisfy(Term goal, CompleteDatabase db, ExecutionContext context) {
        boolean firstRun = true;
        ExecutionResult result = null;

        while(true) {
            if (firstRun) {
                result = processor.execute(goal, db, context);
            } else {
                result = processor.reexecute(result.executionContext(), result.executionStack(), db);
            }

            if (result.success()) {
                executionResultService.printTrue();
                Substitution substitution = result.substitution();
                var entries = new ArrayList<>(substitution.getMap().entrySet());
                for (int i = 0; i < entries.size(); i++) {
                    var entry = entries.get(i);
                    if(entry.getKey().startsWith("_V")){
                        continue;
                    }
                    String closingStr;
                    if(i == entries.size() - 1) {
                        closingStr = " ;";
                    } else {
                        closingStr = ",";
                    }
                    println(entry.getKey() + " = " + entry.getValue().toCode() + closingStr);
                }
            } else {
                if(firstRun) {
                    executionResultService.printFalse();
                }
                break;
            }

            firstRun = false;
        }
    }

    private void println(String str) {
        System.out.println(str);
    }

    private void println() {
        System.out.println();
    }

    private void print(String str) {
        System.out.print(str);
    }
}
