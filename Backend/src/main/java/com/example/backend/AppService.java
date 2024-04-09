package com.example.backend;

import com.example.backend.service.routh_hurwitz.RouthHurwitz;
import org.springframework.stereotype.Service;

@Service
public class AppService {
    public String routhHurwitz(String equation) {
        RouthHurwitz routhHurwitz = new RouthHurwitz();
        routhHurwitz.init(equation);
        routhHurwitz.developRouthTable();
        return routhHurwitz.systemInfo().toString();
    }
}
