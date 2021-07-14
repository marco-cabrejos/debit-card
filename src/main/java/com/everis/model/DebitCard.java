package com.everis.model;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "debitCard")
@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DebitCard {
	
	@Id
	private String id;
	
	@Field(name = "cardNumber")
	@NotEmpty(message = "Debe ingresar un número de tarjeta")
	private String cardNumber;
	
	@Field(name = "listAccount")
	@NotEmpty(message = "Debe asociar al menos a una cuenta bancaria")
	private List<CardAccountAsociation> listAccount;
	
}
