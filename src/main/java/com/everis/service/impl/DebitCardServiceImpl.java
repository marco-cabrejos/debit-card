package com.everis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.everis.client.AccountClient;
import com.everis.model.Account;
import com.everis.model.CardAccountAsociation;
import com.everis.model.DebitCard;
import com.everis.repository.IDebitCardRepository;
import com.everis.repository.IRepository;
import com.everis.service.IDebitCardService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DebitCardServiceImpl extends CRUDServiceImpl<DebitCard, String> implements IDebitCardService {

	@Autowired
	private IDebitCardRepository repository;
	
	@Autowired
	private AccountClient accountClient;
	
	@Override
	protected IRepository<DebitCard, String> getRepository() {
		return repository;
	}

	@Override
	public Mono<DebitCard> findByCardNumber(String cardNumber) {
		return repository.findByCardNumber(cardNumber);
	}

	@Override
	public Mono<DebitCard> validateAndCreate(DebitCard debitCard) {
		Mono<DebitCard> monoDebitCard = Mono.just(debitCard);
		Mono<DebitCard> monoDebitCardFoundByCardNumber = findByCardNumber(debitCard.getCardNumber())
				.flatMap(dc->{
					if(dc.getId()!=null) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Número de tarjeta "+dc.getCardNumber()+" ya existe");
					}
					return Mono.just(dc);
				}).switchIfEmpty(Mono.just(DebitCard.builder().build())); 
		Mono<List<CardAccountAsociation>> monoListaAsociacion = Flux.fromIterable(debitCard.getListAccount())
		.flatMap(item->{
			log.info("iterando 1");
			Mono<Account> monoAccount = accountClient.findByAccountNumber(item.getAccount().getAccountNumber());
			Mono<CardAccountAsociation> asociation = Mono.just(item);
			return monoAccount
					.zipWith(asociation,(a,b)->{
						b.setAccount(a);
						return b;
					}).defaultIfEmpty(CardAccountAsociation.builder().account(Account.builder().accountNumber(item.getAccount().getAccountNumber()).build()).build());
			})
		.collectList()
		.flatMap(list->{
			log.info("list ");
			list.stream().forEach(i->{
				if(i.getAccount().getId()==null || i.getAccount().getId().isEmpty()) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Número de cuenta "+i.getAccount().getAccountNumber()+" no existe");
				}
			});
			return Mono.just(list);
		});
		
		return monoDebitCard
				.zipWith(monoListaAsociacion,(a,b)->{
					a.setListAccount(b);
					return a;
					})
				.zipWith(monoDebitCardFoundByCardNumber,(a,b)->{
					return a;
					})
				.flatMap(debit->{
					return create(debit);
					});
	}
	
}
