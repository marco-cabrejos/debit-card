package com.everis.service;

import com.everis.model.DebitCard;

import reactor.core.publisher.Mono;

public interface IDebitCardService extends ICRUDService<DebitCard, String> {
	Mono<DebitCard> findByCardNumber(String cardNumber);
	Mono<DebitCard> validateAndCreate(DebitCard debitCard);
}
