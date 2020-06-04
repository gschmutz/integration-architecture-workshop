package com.trivadis.integrationws.activemq.spring.repositories;

import org.springframework.stereotype.Component;

import com.trivadis.integrationws.activemq.spring.documents.OrderTransaction;

@Component
public class OrderTransactionRepositoryImpl implements OrderTransactionRepository {

	@Override
	public void save(OrderTransaction orderTransaction) {
		System.out.println("save transaction " + orderTransaction);

	}

}
