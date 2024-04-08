package com.example.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("")
@RestController
@RequestMapping("/")
public class Controller {
    @Autowired
    AppService appService = new AppService();

    @GetMapping("/routh")
    public ResponseEntity<String> routhStabilityCheck(@RequestBody String equation) {
        return new ResponseEntity<>(appService.routhHurwitz(equation), HttpStatus.OK);
    }
}
