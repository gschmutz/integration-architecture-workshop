package com.trivadis.integrationws.activemq.spring.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import com.trivadis.integrationws.activemq.spring.documents.OrderTransaction;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/transaction")
public class OrderTransactionController {

  @Autowired private JmsTemplate jmsTemplate;

  @PostMapping("/send")
  public void send(@RequestBody OrderTransaction transaction) {
    System.out.println("Sending a transaction....");
    jmsTemplate.convertAndSend("OrderTransactionQueue", transaction);
  }
}
