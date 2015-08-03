package com.griddynamics.urlshorter;

import com.griddynamics.urlshorter.http.HttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan("com.griddynamics.urlshorter")
public class Main {
    @Autowired
    @Qualifier("HttpServer")
    HttpServer httpPoint;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

}