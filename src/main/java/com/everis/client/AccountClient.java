package com.everis.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.model.Account;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AccountClient {
	@Autowired
	private WebClient webClient;
	@SuppressWarnings("rawtypes")
	@Autowired
    private ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;
	public Mono<Account> findByAccountNumber(String accountNumber){
		log.info("findByAccountNumber");
		return webClient
				.get()
				.uri(builder->builder
						.path("/account/number/")
						.queryParam("accountNumber", accountNumber)
						.build())
				.retrieve()
				.bodyToMono(Account.class);
	}
}
