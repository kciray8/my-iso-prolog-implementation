package iaroslav.baranov.tracklog.processor;

import iaroslav.baranov.tracklog.service.db.Procedure;
import lombok.Getter;

public class ExecutionCommand {
    @Getter
    ExecutionCommandType type;

    @Getter
    private Procedure procedure;

    private ExecutionCommand(ExecutionCommandType type) {
        this.type = type;
    }

    private ExecutionCommand(ExecutionCommandType type, Procedure procedure) {
        this.type = type;
        this.procedure = procedure;
    }

    public static ExecutionCommand selectProcedureForExecution(){
        return new ExecutionCommand(ExecutionCommandType.SELECT_PROCEDURE_FOR_EXECUTION);
    }

    public static ExecutionCommand executeCut(){
        return new ExecutionCommand(ExecutionCommandType.EXECUTE_CUT);
    }

    public static ExecutionCommand halt(){
        return new ExecutionCommand(ExecutionCommandType.HALT);
    }

    public static ExecutionCommand executeControlStructure(){
        return new ExecutionCommand(ExecutionCommandType.EXECUTE_CONTROL_STRUCTURE);
    }

    public static ExecutionCommand executeBuildInPredicate(Procedure procedure){
        return new ExecutionCommand(ExecutionCommandType.EXECUTE_BUILD_IN_PREDICATE, procedure);
    }

    public static ExecutionCommand executeUserDefinedProcedure(){
        return new ExecutionCommand(ExecutionCommandType.EXECUTE_USER_DEFINED_PROCEDURE);
    }

    public static ExecutionCommand executeUserDefinedProcedureNoMoreClauses(){
        return new ExecutionCommand(ExecutionCommandType.EXECUTE_USER_DEFINED_PROCEDURE_NO_MORE_CLAUSES);
    }

    public static ExecutionCommand backtracking(){
        return new ExecutionCommand(ExecutionCommandType.BACKTRACKING);
    }
}
