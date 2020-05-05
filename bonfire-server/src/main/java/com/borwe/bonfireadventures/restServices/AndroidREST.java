package com.borwe.bonfireadventures.restServices;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController()
public class AndroidREST{
    
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public Mono<String> helloTest(){
        return Mono.just("hello");
    }
}
