package com.hpalt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class TestController {
	@Autowired
	private LoadBalancerClient loadBalancerClient;
	@Autowired
	private DiscoveryClient discoveryClient ;// 快速服务的发现

	@Autowired
	private TestI testI;

	@GetMapping("/discovery/{serviceName}")
	public ResponseEntity<List<String>> discovery(@PathVariable("serviceName") String serviceName){

		/**
		 * 通过服务的ID / 名称得到服务的实例列表
		 */
		List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

		if(instances==null || instances.isEmpty()){
			return ResponseEntity.notFound().build() ;
		}

		List<String> services = new ArrayList<>(instances.size());

		instances.forEach(instance->{
			services.add(instance.getHost()+":"+instance.getPort());
		});

		return ResponseEntity.ok(services) ;
	}

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate() ;
	}

	@Autowired
	private RestTemplate restTemplate ;

	@GetMapping("/rpcv1/{message}")
	public ResponseEntity<String> rpcV1(@PathVariable("message") String message){
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
				"http://nacos-provider/echo/{message}",
				String.class,
				message
		);
		if(responseEntity.getStatusCode()== HttpStatus.OK){
			return ResponseEntity.ok(String.format("远程调用成功，结果为%s",responseEntity.getBody())) ;
		}
		return ResponseEntity.badRequest().body("远程调用失败") ;
	}


	@GetMapping("/rpcv2/{message}")
	public ResponseEntity<String> rpcV2(@PathVariable("message") String message){
		List<ServiceInstance> instances = discoveryClient.getInstances("nacos-provider");
		if(instances==null || instances.isEmpty()){
			return ResponseEntity.badRequest().body("当前服务没有服务的提供者") ;
		}
		ServiceInstance serviceInstance = instances.get(0);
		String instance = serviceInstance.getHost()+":"+serviceInstance.getPort() ;
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
				String.format("http://%s/echo/{message}",instance),
				String.class,
				message
		);
		if(responseEntity.getStatusCode()==HttpStatus.OK){
			return ResponseEntity.ok(String.format("远程调用成功，结果为%s",responseEntity.getBody())) ;
		}
		return ResponseEntity.badRequest().body("远程调用失败") ;
	}


	@GetMapping("/rpcv3/{message}")
	public ResponseEntity<String> rpcV3(@PathVariable("message") String message){
		//List<ServiceInstance> instances = discoveryClient.getInstances("nacos-provider");
		/*if(instances==null || instances.isEmpty()){
			return ResponseEntity.badRequest().body("当前服务没有服务的提供者") ;
		}*/
		/*ServiceInstance serviceInstance = loadbalance(instances);*/
		/*System.out.println(serviceInstance);
		String instance = serviceInstance.getHost()+":"+serviceInstance.getPort() ;*/
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://nacos-provider/echo/" + message, String.class);
		/*ResponseEntity<String> responseEntity = restTemplate.getForEntity(
				String.format("http://%s/echo/{message}",instance),
				String.class,
				message
		);*/

		if(responseEntity.getStatusCode()==HttpStatus.OK){
			return ResponseEntity.ok(String.format("远程调用成功，结果为%s",responseEntity.getBody())) ;
		}
		return ResponseEntity.badRequest().body("远程调用失败") ;

	}

	/**
	 * 从一个服务的实例列表里面选择一个服务的实例
	 * @param instances
	 * 实例列表
	 * @return
	 * 具体的实例
	 */
	private ServiceInstance loadbalance(List<ServiceInstance> instances) {
		Random random = new Random(System.currentTimeMillis());
		return instances.get(random.nextInt(instances.size())) ;
	}


	@GetMapping("/rpcv4/{message}")
	public ResponseEntity<String> rpcV4(@PathVariable("message") String message){
		ServiceInstance serviceInstance = loadBalancerClient.choose("nacos-provider");
		String instance = serviceInstance.getHost()+":"+serviceInstance.getPort() ;
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
				//"http://nacos-provider/echo/{message}",
				String.format("http://%s/echo/{message}",instance),
				String.class,
				message
		);
		if(responseEntity.getStatusCode()==HttpStatus.OK){
			return ResponseEntity.ok(String.format("远程调用成功，结果为%s",responseEntity.getBody())) ;
		}
		return ResponseEntity.badRequest().body("远程调用失败") ;
	}

	@GetMapping("/rpcv5/{message}")
	public ResponseEntity<String> rpcV5(@PathVariable("message") String message){
		ResponseEntity<String> responseEntity = testI.echo(message);
		int i = 1/0;
		if(responseEntity.getStatusCode()==HttpStatus.OK){
			return ResponseEntity.ok(String.format("远程调用成功，结果为%s",responseEntity.getBody())) ;
		}
		return ResponseEntity.badRequest().body("远程调用失败") ;
	}
}

