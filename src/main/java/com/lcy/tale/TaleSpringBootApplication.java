package com.lcy.tale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class TaleSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaleSpringBootApplication.class, args);
    }
}
