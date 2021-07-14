package com.everis.client;

import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.config.CustomRibbonConfiguration;

@Configuration
@LoadBalancerClient(name = "load-balancer", configuration = CustomRibbonConfiguration.class)
public class Client {
	@Value("${gateway.baseUrl}")
	private String baseUrl;
    @Bean
    @LoadBalanced
	public WebClient getWebClient()
	{
	    return WebClient.builder()
	            .baseUrl(baseUrl)
	            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	            .build();
	}
}

