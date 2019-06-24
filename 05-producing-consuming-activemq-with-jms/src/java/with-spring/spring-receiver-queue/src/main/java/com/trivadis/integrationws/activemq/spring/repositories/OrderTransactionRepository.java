package com.trivadis.integrationws.activemq.spring.repositories;

import com.trivadis.integrationws.activemq.spring.documents.OrderTransaction;

public interface OrderTransactionRepository {
	public void save(OrderTransaction orderTransaction);
}
