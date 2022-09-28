package com.sogong.tejava;

import com.sogong.tejava.util.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(ApplicationProperties.class)
public class TejavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TejavaApplication.class, args);
    }

}
