package com.trivadis.integrationws.visa.service;

import java.util.List;

public interface VisaCardService {

	boolean validateCard(CreditCard card) throws InvalidCreditCardException;

	String reserveAmount(CreditCard card, double amount);
	String bookAmount(CreditCard card, double amount);
}
