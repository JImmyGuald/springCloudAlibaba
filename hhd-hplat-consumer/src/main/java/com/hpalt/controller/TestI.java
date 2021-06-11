package com.hpalt.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author ganj
 * @Description
 * @Date 2021/6/10 16:46
 */
@FeignClient("nacos-provider")//声明调用的提供者的name
public interface TestI {
    @GetMapping(value = "/echo/{message}")
    ResponseEntity<String> echo(@PathVariable("message") String message);
}
