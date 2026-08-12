package iaroslav.baranov.tracklog.web.api.toplevel;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.processor.CompleteDatabase;
import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.processor.ExecutionResult;
import iaroslav.baranov.tracklog.processor.Processor;
import iaroslav.baranov.tracklog.service.db.CompleteDatabaseService;
import iaroslav.baranov.tracklog.unification.Substitution;
import iaroslav.baranov.tracklog.web.TrackLog;
import iaroslav.baranov.tracklog.web.TrackLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/top-level")
@RequiredArgsConstructor
public class TopLevelController {
    private final CompleteDatabaseService completeDatabaseService;
    private final TrackLogService trackLogService;
    private final TrackLog trackLog;
    private final TermParser termParser;
    private final Processor processor;

    @PostMapping("query")
    public QueryResponse query(QueryRequest request) {
        String query = request.value();
        Term goal = termParser.parse(query);
        ExecutionContext executionContext = new ExecutionContext();

        boolean firstRun = true;
        ExecutionResult result = null;
        List<Answer> answers = new ArrayList<>();
        while(true) {
            if (firstRun) {
                result = processor.execute(goal, trackLog.getDb(), executionContext);
            } else {
                result = processor.reexecute(result.executionContext(), result.executionStack(), trackLog.getDb());
            }

            if (result.success()) {
                Map<String, String> answerSubstitutions = new LinkedHashMap<>();
                Substitution substitution = result.substitution();
                var entries = new ArrayList<>(substitution.getMap().entrySet());
                for (int i = 0; i < entries.size(); i++) {
                    var entry = entries.get(i);
                    if(entry.getKey().startsWith("_V")){
                        continue;
                    }
                    answerSubstitutions.put(entry.getKey(), entry.getValue().toCode());
                }
                Answer answer = new Answer(answerSubstitutions);
                answers.add(answer);
            } else {
                break;
            }

            firstRun = false;
        }

        return new QueryResponse(answers);
    }
}
