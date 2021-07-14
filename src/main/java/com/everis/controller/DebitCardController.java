package com.everis.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.everis.model.DebitCard;
import com.everis.service.IDebitCardService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/debitcard")
public class DebitCardController {
	@Autowired
	private IDebitCardService service;
			
	@GetMapping
	public Mono<ResponseEntity<List<DebitCard>>> findAll(){
		return service.findAll()
				.collectList()
				.flatMap(list -> {
					return list.size() > 0 ? 
							Mono.just(ResponseEntity
									.ok()
									.contentType(MediaType.APPLICATION_JSON)
									.body(list)) :
							Mono.just(ResponseEntity
									.noContent()
									.build());
				});
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<DebitCard>> findById(@PathVariable("id") String id){
		return service.findById(id)
				.map(objectFound -> ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(objectFound))
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
		
	}
	
	@GetMapping("/number/")
	public Mono<ResponseEntity<DebitCard>> findByAccountNumber(@RequestParam(name = "cardNumber") String accountNumber){
		log.info("findByAccountNumber");
		return service.findByCardNumber(accountNumber)
				.flatMap(a->{
					return Mono.just(ResponseEntity
							.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(a));
				})
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
	}
	
	@PostMapping
	public Mono<ResponseEntity<DebitCard>> create(@Valid @RequestBody DebitCard account, final ServerHttpRequest request){
		return service.validateAndCreate(account)
				.flatMap(a->{
					return Mono.just(ResponseEntity.created(null).contentType(MediaType.APPLICATION_JSON).body(a));
				});
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<DebitCard>> update(@RequestBody DebitCard creditCard, @PathVariable("id") String id){
		
		Mono<DebitCard> customerModification = Mono.just(creditCard);
		
		Mono<DebitCard> customerDatabase = service.findById(id);
		
		return customerDatabase
				.zipWith(customerModification, (a,b) -> {
					a.setCardNumber(creditCard.getCardNumber());
					a.setListAccount(creditCard.getListAccount());
					return a;
				})
				.flatMap(service::update)
				.map(objectUpdated -> ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(objectUpdated))
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
				
	}
	/*
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Response>> delete(@PathVariable("id") String id){
		
		Mono<Account> customerDatabase = service.findById(id);
		
		return customerDatabase
				.zipWith(customerDatabase, (a,b) -> {
					a.setDateClosed(LocalDateTime.now());
					return a;
				})
				.flatMap(service::update)
				.map(objectUpdated -> ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(Response
								.builder()
								.data("Cuenta eliminada")
								.build()))
				.defaultIfEmpty(ResponseEntity
						.badRequest()
						.body(Response
								.builder()
								.data("La cuenta no existe")
								.build()));
		
	}*/
}
