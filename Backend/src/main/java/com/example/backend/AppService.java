package com.example.backend;

import com.example.backend.service.routh_hurwitz.RouthHurwitz;
import org.springframework.stereotype.Service;

import java.util.InputMismatchException;

@Service
public class AppService {
    public String routhHurwitz(String equation) {
        RouthHurwitz routhHurwitz = new RouthHurwitz();
        try {
            routhHurwitz.init(equation);
        } catch (InputMismatchException e) {
            return "Invalid Input";
        }
        routhHurwitz.developRouthTable();
        return routhHurwitz.systemInfo().toString();
    }

    public String signalFlowGraph() {

    }
}
