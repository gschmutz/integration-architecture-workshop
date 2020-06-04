/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trivadis.integrationws.visa.service;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Exception thrown when a specified flight cannot be found.
 *
 * @author Guido Schmutz
 */
@SoapFault(faultCode = FaultCode.CLIENT)
public class InvalidCreditCardException extends Exception {

    private CreditCard creditCard;

    public InvalidCreditCardException(CreditCard creditCard) {
        super("No credit card with number [" + creditCard.getCardNumber() + "] and name [" + creditCard.getCardholderFirstName() + "" + creditCard.getCardholderLastName() + "]");
        this.creditCard = creditCard;
    }

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

}
