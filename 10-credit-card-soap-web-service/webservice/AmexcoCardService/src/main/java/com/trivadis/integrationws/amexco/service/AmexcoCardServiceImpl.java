package com.trivadis.integrationws.visa.service;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class AmexcoCardServiceImpl implements AmexcoCardService {

	public boolean validateCard(CreditCard card) {
		if (card.getCardNumber().equals("123456789")) {
			return true;
		} else {
			return false;
		}
	}

	public int bookAmount(CreditCard card, double amount, boolean reseverationOnly) {
		if (card.getCardNumber().equals("123456789")) {
			return Math.abs(new Random().nextInt());
		} else {
			return 0;
		}
	}

	
}
