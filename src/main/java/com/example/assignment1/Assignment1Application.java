package com.example.assignment1;
import model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import services.JavaSmtpGmailSenderService;
import services.MatchService;

import java.util.List;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan({"controller","model", "repositories", "services"})
@EntityScan("model")
@EnableJpaRepositories("repositories")
public class Assignment1Application {
    @Autowired
    private JavaSmtpGmailSenderService senderService;

    public static void main(String[] args) {
        SpringApplication.run(Assignment1Application.class, args);
    }
//    @EventListener(ApplicationReadyEvent.class)
//    public void sendMail(){
//        senderService.sendEmail("andreeamariaonaci@gmail.com","This is subject","This is email body");
//    }
}