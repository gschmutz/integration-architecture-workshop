package com.trivadis.integrationws.visa.service;


public interface AmexcoCardService {

	boolean validateCard(CreditCard card);
	
	int bookAmount(CreditCard card, double amount, boolean reseverationOnly);
}
