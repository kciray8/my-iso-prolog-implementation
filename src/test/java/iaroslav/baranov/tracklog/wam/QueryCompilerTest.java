package iaroslav.baranov.tracklog.wam;

import iaroslav.baranov.tracklog.BaseIntegrationTest;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.wam.command.Command;
import iaroslav.baranov.tracklog.wam.compile.QueryCompiler;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RequiredArgsConstructor
class QueryCompilerTest extends BaseIntegrationTest {

    @Autowired
    private TermParser termParser;

    @Test
    public void shouldCompile(){
        Term term = termParser.parse("p(Z, h(Z,W), f(W))");
        List<Command> commands = queryCompiler.compile(term);
        Assertions.assertEquals(9, commands.size());
    }

    @Test
    public void shouldRun(){
        Term term = termParser.parse("p(Z, h(Z,W), f(W))");
        List<Command> commands = queryCompiler.compile(term);
        MachineState machineState = new MachineState();
        machineService.execute(commands, machineState);
        String reconstructedTerm = machineService.reconstructTerm(machineState, 7);
        Assertions.assertEquals("p(X2, h(X2, X3), f(X3))", reconstructedTerm);
    }
}