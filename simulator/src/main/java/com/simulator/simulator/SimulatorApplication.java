package com.simulator.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication
@EnableScheduling
public class SimulatorApplication {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){return new ServerEndpointExporter();}

    public static void main(String[] args) {
        SpringApplication.run(SimulatorApplication.class, args);
    }

}
