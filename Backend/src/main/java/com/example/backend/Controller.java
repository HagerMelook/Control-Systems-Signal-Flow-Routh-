package com.example.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin()
@RestController
@RequestMapping("/")
public class Controller {
    @Autowired
    AppService appService = new AppService();

    @PostMapping("/routh")
    @ResponseBody
    public ResponseEntity<String> routhStabilityCheck(@RequestBody String equation) {
        equation = equation.replaceAll(" ", "");
        if (equation.isEmpty())
            return new ResponseEntity<>("Invalid Input", HttpStatus.BAD_REQUEST);
        String output = appService.routhHurwitz(equation);
        if (output.equals("Invalid Input"))
            return new ResponseEntity<>(output, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    /* Signal Flow Graph API */
    @PostMapping("/flowgraph")
    @ResponseBody
    public void signalFlowGraph(@RequestBody String graph) {
        this.appService.signalFlowGraph(graph);
    }

    @GetMapping("/flowgraph/analysis")
    public ResponseEntity<String> signalFlowGraphAnalysis() {
        return new ResponseEntity<>(appService.signalFlowGraphAnalysis(), HttpStatus.OK);
    }

}
