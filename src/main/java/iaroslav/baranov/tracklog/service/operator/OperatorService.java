package iaroslav.baranov.tracklog.service.operator;

import iaroslav.baranov.tracklog.parser.SourceParser;
import iaroslav.baranov.tracklog.parser.expression.OperatorAssociativity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperatorService {
    public List<Operator> getOperatorList() {
        return operatorList;
    }

    private List<Operator> operatorList;

    public Map<String, Operator> getBinaryOperators() {
        return binaryOperators;
    }

    public Map<String, Operator> getUnaryOperators() {
        return unaryOperators;
    }

    public Map<String, Operator> getOperators() {
        return operators;
    }

    private Map<String, Operator> binaryOperators =  new LinkedHashMap<>();
    private Map<String, Operator> unaryOperators =  new LinkedHashMap<>();
    private Map<String, Operator> operators =  new LinkedHashMap<>();

    // TODO add to exection context or something like that
    public OperatorService() {
        operatorList = new ArrayList<>();
        operatorList.add(new Operator(":-",1200, OperatorAssociativity.NONE));
        operatorList.add(new Operator(",", 1000, OperatorAssociativity.RIGHT));
        operatorList.add(new Operator("is",700, OperatorAssociativity.NONE));
        operatorList.add(new Operator("+",500, OperatorAssociativity.LEFT));
        operatorList.add(new Operator("-",500, OperatorAssociativity.LEFT));
        operatorList.add(new Operator("*",400, OperatorAssociativity.LEFT));
        operatorList.add(new Operator("/",400, OperatorAssociativity.LEFT));
        operatorList.add(new Operator("=",700, OperatorAssociativity.NONE));
        operatorList.add(new Operator("\\=",700, OperatorAssociativity.NONE));
        operatorList.add(new Operator("==",700, OperatorAssociativity.NONE));
        operatorList.add(new Operator("\\==",700, OperatorAssociativity.NONE));
        operatorList.add(new Operator("=..",700, OperatorAssociativity.NONE));

        operatorList.add(new Operator(">",700, OperatorAssociativity.NONE));
        operatorList.add(new Operator(">=",700, OperatorAssociativity.NONE));
        operatorList.add(new Operator("<",700, OperatorAssociativity.NONE));
        operatorList.add(new Operator("=<",700, OperatorAssociativity.NONE));

        operatorList.add(new Operator("->",1050, OperatorAssociativity.RIGHT));
        operatorList.add(new Operator(";",1100, OperatorAssociativity.RIGHT));

        //unary operators
        operatorList.add(new Operator("\\+",900));

        for (Operator operator : operatorList) {
            addOperatorToTables(operator);
        }
    }

    void addOperatorToTables(Operator operator) {
        operators.put(operator.name(), operator);
        if(operator.binary()){
            binaryOperators.put(operator.name(), operator);
        } else {
            unaryOperators.put(operator.name(), operator);
        }
    }

    public void addOperator(String name, int priority, String associativityCode) {
        OperatorAssociativity associativity = OperatorAssociativity.NONE;
        if(associativityCode.equals("xfx")){
            associativity = OperatorAssociativity.NONE;
        } else if (associativityCode.equals("yfx")){
            associativity = OperatorAssociativity.LEFT;
        } else if (associativityCode.equals("xfy")){
            associativity = OperatorAssociativity.RIGHT;
        }
        Operator operator = new Operator(name, priority, associativity);
        operatorList.add(operator);
        addOperatorToTables(operator);
    }

    public boolean isBinaryOperator(String name) {
        return binaryOperators.containsKey(name);
    }

    public boolean isUnaryOperator(String name) {
        return unaryOperators.containsKey(name);
    }
}


/*
+----------+-----------+----------------------------------+
| Priority | Specifier | Operator(s)                      |
+----------+-----------+----------------------------------+
| 1200     | xfx       | :- -->                           |
| 1200     | fx        | :- ?-                            |
| 1100     | xfy       | ;                                |
| 1050     | xfy       | ->                               |
| 1000     | xfy       | ,                                |
| 900      | fy        | \+                               |
| 700      | xfx       | = \=                             |
| 700      | xfx       | == \== @< @=< @> @>=             |
| 700      | xfx       | =..                              |
| 700      | xfx       | is =:= =\= < =< > >=             |
| 500      | yfx       | + - /\ \/                        |
| 400      | yfx       | * / // rem mod << >>             |
| 200      | xfx       | **                               |
| 200      | xfy       | ^                                |
| 200      | fy        | - \                              |
+----------+-----------+----------------------------------+
 */