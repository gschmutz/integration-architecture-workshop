package com.trivadis.integrationws.visa.service;

public class CreditCard {
	private String cardNumber;
	private String cardholderName;
	private String goodThrough;
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getCardholderName() {
		return cardholderName;
	}
	public void setCardholderName(String cardholderName) {
		this.cardholderName = cardholderName;
	}
	public String getGoodThrough() {
		return goodThrough;
	}
	public void setGoodThrough(String goodThrough) {
		this.goodThrough = goodThrough;
	}
	
	
}
