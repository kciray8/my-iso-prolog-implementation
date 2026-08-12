package iaroslav.baranov.tracklog.wam.compile;

import iaroslav.baranov.tracklog.ast.term.AtomicTerm;
import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.service.TermFlatteningService;
import iaroslav.baranov.tracklog.wam.command.Command;
import iaroslav.baranov.tracklog.wam.command.PutStructure;
import iaroslav.baranov.tracklog.wam.command.SetValue;
import iaroslav.baranov.tracklog.wam.command.SetVariable;
import iaroslav.baranov.tracklog.wam.compile.register.Register;
import iaroslav.baranov.tracklog.wam.compile.register.StructuredRegister;
import iaroslav.baranov.tracklog.wam.compile.register.VariableRegister;
import iaroslav.baranov.tracklog.wam.compile.tokens.RegisterToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Component
public class QueryCompiler {
    private final TermFlatteningService termFlatteningService;

    public List<Command> compile(Term term){
        if(term instanceof Variable v){
            throw new QueryCompilerException("Query can't be a variable: " + v.name());
        }
        List<Term> terms = termFlatteningService.flatten(term);

        List<Register> registers = flattenedTermsToRegisters(terms);

        List<StructuredRegister> flattenedRegisters = flattenRegisters(registers);
        List<RegisterToken> registerTokens = getRegisterTokens(flattenedRegisters);

        List<Command> commands = new ArrayList<>();

        Set<Integer> encounteredRegisters = new HashSet<>();
        for(RegisterToken register: registerTokens){
            if(register.name() != null) {
                commands.add(new PutStructure(register.name(), register.arity(), register.num()));
                encounteredRegisters.add(register.num());
            } else {
                if(encounteredRegisters.contains(register.num())){
                    commands.add(new SetValue(register.num()));
                } else {
                    encounteredRegisters.add(register.num());
                    commands.add(new SetVariable(register.num()));
                }
            }
        }

        return commands;
    }

    private List<Register> flattenedTermsToRegisters(List<Term> terms) {
        List<Register> registers = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
            Term term = terms.get(i);
            if(term instanceof CompoundTerm ct){
                List<Integer> args = new ArrayList<>();
                for(Term arg: ct.args()) {
                    Variable v = (Variable)arg;
                    args.add(Integer.parseInt(v.name()) + 1);
                }
                registers.add(new StructuredRegister(i + 1, ct.getName(), args));
            } else  if (term instanceof AtomicTerm at){
                registers.add(new StructuredRegister(i + 1, at.toCode(), new ArrayList<>()));
            } else if (term instanceof Variable v){
                registers.add(new VariableRegister(i + 1, v.name()));
            }
        }

        return registers;
    }

    private List<RegisterToken> getRegisterTokens(List<StructuredRegister> flattenedRegisters) {
        List<RegisterToken> tokens = new ArrayList<>();

        for(StructuredRegister register: flattenedRegisters){
            int name = register.num();
            String functorName = register.functorName();
            int arity = register.variables().size();
            tokens.add(new RegisterToken(name, functorName, arity));
            for(int varName: register.variables()){
                tokens.add(new RegisterToken(varName));
            }
        }

        return tokens;
    }

    private List<StructuredRegister> flattenRegisters(List<Register> registers) {
        List<StructuredRegister> result = new ArrayList<>();
        result.add((StructuredRegister)registers.get(0));
        int i = 0;
        do{
            StructuredRegister register = result.get(i);
            for(int varNum: register.variables().reversed()) {
                Register refRegister = registers.get(varNum - 1);
                if(refRegister instanceof StructuredRegister sr){
                    result.add(sr);
                }
            }
            i++;
        }while(i < result.size());

        return result.reversed();
    }

}
