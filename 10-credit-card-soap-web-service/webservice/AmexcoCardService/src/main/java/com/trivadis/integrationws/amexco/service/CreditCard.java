package com.trivadis.integrationws.visa.service;

public class CreditCard {
	private String cardNumber;
	private String cardholderFirstName;
	private String cardholderLastName;
	private String validThroughMonth;
	private String validThroughYear;
	
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getCardholderFirstName() {
		return cardholderFirstName;
	}
	public void setCardholderFirstName(String cardholderFirstName) {
		this.cardholderFirstName = cardholderFirstName;
	}
	public String getCardholderLastName() {
		return cardholderLastName;
	}
	public void setCardholderLastName(String cardholderLastName) {
		this.cardholderLastName = cardholderLastName;
	}
	public String getValidThroughMonth() {
		return validThroughMonth;
	}
	public void setValidThroughMonth(String validThroughMonth) {
		this.validThroughMonth = validThroughMonth;
	}
	public String getValidThroughYear() {
		return validThroughYear;
	}
	public void setValidThroughYear(String validThroughYear) {
		this.validThroughYear = validThroughYear;
	}
}
