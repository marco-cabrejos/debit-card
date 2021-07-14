package com.everis.repository;

import com.everis.model.DebitCard;

import reactor.core.publisher.Mono;

public interface IDebitCardRepository extends IRepository<DebitCard, String> {
	Mono<DebitCard> findByCardNumber(String cardNumber);
}
