package iaroslav.baranov.tracklog.ide.outer;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.lexer.Lexer;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.SourceParser;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.parser.text.TextParser;
import iaroslav.baranov.tracklog.processor.CompleteDatabase;
import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.processor.ExecutionResult;
import iaroslav.baranov.tracklog.processor.Processor;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateService;
import iaroslav.baranov.tracklog.service.TermService;
import iaroslav.baranov.tracklog.service.db.CompleteDatabaseService;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.getLastModifiedTime;

@SpringBootApplication(
        scanBasePackages = "iaroslav.baranov.tracklog",
        exclude = {DataSourceAutoConfiguration.class}
)
@AllArgsConstructor
@Slf4j
@Profile("ide")
public class SourceWatcher implements CommandLineRunner {
    private final TextParser textParser;
    private final Lexer lexer;
    private final UnificationService unificationService;
    private final TermService termService;
    private final BuildInPredicateService buildInPredicateService;
    private final CompleteDatabaseService completeDatabaseService;

    private final TermParser termParser;
    private final Processor processor;
    private final SourceParser sourceParser;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SourceWatcher.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    void interpret(Path srcPath) throws IOException {
        String srcCode = Files.readString(srcPath);
        Path root = srcPath.getParent();
        try {
            try {
                CompleteDatabase db = new CompleteDatabase();
                completeDatabaseService.init(db);

                ExecutionContext context = new ExecutionContext();
                context.setRoot(root);
                sourceParser.parse(
                        srcCode,
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
            } catch (Exception e) {
                log.info("Parsing error", e);
            }
        } catch (Exception e) {
            log.info("Lexer error", e);
        }
    }

    public static List<Path> collectSourceFiles(Path root) throws IOException {
        try (var stream = Files.walk(root)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".pl"))
                    .toList();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        String src = args[0];
        Path srcPath = Paths.get(src).toAbsolutePath().normalize();
        Path root = srcPath.getParent();
        List<Path> sourceFiles = collectSourceFiles(root);

        try {
            interpret(srcPath);

            Map<Path, FileTime> modifiedTimes = new HashMap<>();
            for (Path sourceFile : sourceFiles) {
                modifiedTimes.put(sourceFile, Files.getLastModifiedTime(sourceFile));
            }

            while (true) {
                boolean anyFileModified = false;
                for (Path sourceFile : sourceFiles) {
                    FileTime currentModifiedTime = getLastModifiedTime(sourceFile);
                    if (!currentModifiedTime.equals(modifiedTimes.get(sourceFile))) {
                        anyFileModified = true;
                    }
                }

                if (anyFileModified) {
                    log.info("Modified");
                    interpret(srcPath);
                }

                for (Path sourceFile : sourceFiles) {
                    modifiedTimes.put(sourceFile, Files.getLastModifiedTime(sourceFile));
                }
                Thread.sleep(200);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
