package iaroslav.baranov.tracklog.web;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.parser.text.TextParser;
import iaroslav.baranov.tracklog.processor.CompleteDatabase;
import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.processor.ExecutionResult;
import iaroslav.baranov.tracklog.processor.Processor;
import iaroslav.baranov.tracklog.service.db.CompleteDatabaseService;
import iaroslav.baranov.tracklog.unification.Substitution;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackLogService {
    private final CompleteDatabaseService completeDatabaseService;
    private final TextParser textParser;
    private final Processor processor;
    private final Environment environment;

    private final TrackLog trackLog;

    @PostConstruct
    public void init() throws IOException {
        if (environment.matchesProfiles("web")) {
            initDB();
        }
    }

    private void initDB() throws IOException {
        log.info("TrackLog will be initialized...");
        CompleteDatabase db = new CompleteDatabase();
        completeDatabaseService.init(db);
        trackLog.setDb(db);

        Path sourcePath = Paths.get("C:\\Users\\kcira\\sys\\projects\\TrackLog\\ide\\kb.pl");
        String code = Files.readString(sourcePath);
        PrologText text = textParser.parse(code);

        ExecutionContext context = new ExecutionContext();
        context.setRoot(sourcePath.getParent());
        processor.interpret(text, db, context);

        log.info("Init complete");
    }
}
