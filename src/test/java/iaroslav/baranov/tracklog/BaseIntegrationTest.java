package iaroslav.baranov.tracklog;

import iaroslav.baranov.tracklog.lexer.Lexer;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.parser.text.TextParser;
import iaroslav.baranov.tracklog.wam.MachineService;
import iaroslav.baranov.tracklog.wam.compile.QueryCompiler;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@AllArgsConstructor
@NoArgsConstructor
@SpringBootTest(classes = {TestApplication.class})
@ActiveProfiles("test")
public class BaseIntegrationTest {
    @Autowired
    protected Lexer lexer;

    @Autowired
    protected TermParser termParser;

    @Autowired
    protected TextParser textParser;

    @Autowired
    protected MachineService machineService;

    @Autowired
    protected QueryCompiler queryCompiler;
}
