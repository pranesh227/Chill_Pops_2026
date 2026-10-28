package com.chillpops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ChillPopsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChillPopsApplication.class, args);
    }
}
