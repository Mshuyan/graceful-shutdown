package com.shuyan.gracefulshutdown.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author will
 */
@RestController
public class TestController {

    @GetMapping("/test1")
    public String test1(){
        Long cycle = 250000000L;
        while ((cycle--) != 0L){
            System.out.print("");
        }
        return "test1";
    }

    @GetMapping("/test2")
    public String test2(){
        return "test2";
    }
}
