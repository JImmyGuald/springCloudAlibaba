package com.hpalt.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient(autoRegister = false) // 服务的发现和注册
@RestController
@EnableFeignClients
public class TestApp {
	public static void main(String[] args) {
		SpringApplication.run(TestApp.class ,args) ;
	}

}

