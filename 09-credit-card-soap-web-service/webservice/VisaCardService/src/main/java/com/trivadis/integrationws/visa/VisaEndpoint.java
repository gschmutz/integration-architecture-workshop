package com.trivadis.integrationws.visa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.trivadis.integrationws.visa.service.CreditCard;
import com.trivadis.integrationws.visa.service.InvalidCreditCardException;
import com.trivadis.integrationws.visa.service.VisaCardService;
import com.trivadis.integrationws.visacard.schemas.ValidateCardRequest;
import com.trivadis.integrationws.visacard.schemas.ValidateCardResponse;

@Endpoint
public class VisaEndpoint {
    private static final Log logger = LogFactory.getLog(VisaEndpoint.class);
	private static final String NAMESPACE_URI = "http://trivadis.com/integrationws/visaCard/schemas";

	private VisaCardService visaCardService;

	@Autowired
	public VisaEndpoint(VisaCardService visaCardService) {
		this.visaCardService = visaCardService;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ValidateCardRequest")
	@ResponsePayload
	public ValidateCardResponse validateCard(@RequestPayload ValidateCardRequest request) throws InvalidCreditCardException {
        if (logger.isDebugEnabled()) {
            logger.debug("Received ValidateCardRequest '" + request.getCardNumber() + "' cardHolderName '" + request.getCardholderName() + "' goodThrough " + request.getGoodThrough());
        }
        
		ValidateCardResponse response = new ValidateCardResponse();
		
        CreditCard card = new CreditCard();
        card.setCardNumber(request.getCardNumber());
        card.setCardholderName(request.getCardholderName());
        card.setCardNumber(request.getCardNumber());
        card.setGoodThrough(request.getGoodThrough());
        
        boolean valid = visaCardService.validateCard(card);
        response.setIsValid(valid);
        
		return response;
	}
}
