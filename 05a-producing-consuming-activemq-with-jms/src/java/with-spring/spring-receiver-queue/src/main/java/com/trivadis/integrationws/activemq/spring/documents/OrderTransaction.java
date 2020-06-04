package com.trivadis.integrationws.activemq.spring.documents;

import java.math.BigDecimal;

public class OrderTransaction {

  private String id;
  private String from;
  private String to;
  private BigDecimal amount;

  public OrderTransaction(final String from, final String to, final BigDecimal amount) {
    this.from = from;
    this.to = to;
    this.amount = amount;
  }

public OrderTransaction() {
	super();
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getFrom() {
	return from;
}

public void setFrom(String from) {
	this.from = from;
}

public String getTo() {
	return to;
}

public void setTo(String to) {
	this.to = to;
}

public BigDecimal getAmount() {
	return amount;
}

public void setAmount(BigDecimal amount) {
	this.amount = amount;
}

@Override
public String toString() {
	return "OrderTransaction [id=" + id + ", from=" + from + ", to=" + to + ", amount=" + amount + "]";
}
  
  
}
