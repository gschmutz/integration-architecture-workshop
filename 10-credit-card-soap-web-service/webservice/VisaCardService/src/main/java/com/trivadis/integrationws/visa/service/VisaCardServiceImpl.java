package com.trivadis.integrationws.visa.service;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class VisaCardServiceImpl implements VisaCardService {

	public boolean validateCard(CreditCard card) throws InvalidCreditCardException {
		if (card.getCardNumber().equals("123456789")) {
			return true;
		} else if (card.getCardNumber().equals("9999")) {
			throw new InvalidCreditCardException(card);
		} else {
			return false;
		}
	}

	public String reserveAmount(CreditCard card, double amount) {
		if (card.getCardNumber().equals("123456789")) {
			return String.valueOf(Math.abs(new Random().nextInt()));
		} else {
			return "";
		}
	}

	public String bookAmount(CreditCard card, double amount) {
		if (card.getCardNumber().equals("123456789")) {
			return String.valueOf(Math.abs(new Random().nextInt()));
		} else {
			return "";
		}
	}
	
}
