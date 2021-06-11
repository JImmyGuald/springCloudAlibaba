package com.hpalt.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@Configuration
@RequestMapping(produces = "application/json; charset=utf-8")
public class EchoController {

	@Value("${server.port}")
	private Integer port ; // 在一台主机上启动多个服务提供者，它的端口肯定不同

	@Value("${test.hello}")
	private String hello;

	/**
	 * reply message
	 * @param message
	 * @return
	 */
	@GetMapping("/echo/{message}")
	@SentinelResource(value = "doSomeThing", blockHandler = "exceptionHandler")
	public ResponseEntity<String> echo(@PathVariable("message")String message){
		System.out.println(hello);
		return ResponseEntity.ok(String.format("hello,%s,我是%s",message,port));
	}

	public void exceptionHandler(String str) {
		System.out.println(str);
	}

}

