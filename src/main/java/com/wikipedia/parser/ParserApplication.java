package com.wikipedia.parser;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParserApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ParserApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
