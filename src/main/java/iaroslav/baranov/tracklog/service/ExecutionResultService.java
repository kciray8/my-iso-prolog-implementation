package iaroslav.baranov.tracklog.service;

import org.springframework.stereotype.Service;

@Service
public class ExecutionResultService {
    public void printTrue(){
        System.out.println("true.");
    }

    public void printFalse() {
        System.out.println("false.");
    }
}
