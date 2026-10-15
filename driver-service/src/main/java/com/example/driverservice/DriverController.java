package com.example.driverservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DriverController {

    @Value("${server.port}")
    private String port;

    @Value("${spring.application.name:driver-service}")
    private String instanceName;

    @GetMapping("/drivers")
    public String getDrivers() {
        System.out.println(">>> [" + instanceName + "] Request handled on port: " + port);
        return "Hello from " + instanceName + " running on port: " + port;
    }
}
