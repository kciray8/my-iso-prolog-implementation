package iaroslav.baranov.tracklog.service.operator;

import iaroslav.baranov.tracklog.parser.expression.OperatorAssociativity;

public record Operator(
        String name,
        int priority,
        OperatorAssociativity associativity,
        boolean binary
) {
    Operator(String name,
             int priority,
             OperatorAssociativity associativity){
        this(name,priority,associativity,true);
    }

    Operator(String name, int priority){
        this(name,priority,OperatorAssociativity.NONE,false);
    }
}
