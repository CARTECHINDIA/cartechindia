package com.cartechindia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dealer")
public class DealerController {

    @GetMapping("/home")
    public ResponseEntity<String> userHome(){
        return new ResponseEntity<>("Welcome to Dealer Page", HttpStatus.OK);
    }
}
