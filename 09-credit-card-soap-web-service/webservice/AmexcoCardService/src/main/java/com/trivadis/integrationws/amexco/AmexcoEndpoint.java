package com.trivadis.integrationws.visa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.trivadis.integrationws.visa.service.AmexcoCardService;
import com.trivadis.integrationws.visa.service.CreditCard;
import com.trivadis.integrationws.visacard.schemas.ValidateRequest;
import com.trivadis.integrationws.visacard.schemas.ValidateResponse;

@Endpoint
public class AmexcoEndpoint {
	private static final String NAMESPACE_URI = "http://trivadis.com/integrationws/visaCard/schemas";

	private AmexcoCardService visaCardService;

	@Autowired
	public AmexcoEndpoint(AmexcoCardService visaCardService) {
		this.visaCardService = visaCardService;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ValidateRequest")
	@ResponsePayload
	public ValidateResponse validateCard(@RequestPayload ValidateRequest request) {
		ValidateResponse response = new ValidateResponse();
		
		CreditCard card = new CreditCard();
        card.setCardNumber(request.getCardNumber());
        card.setCardholderFirstName(request.getCardholderFirstName());
        card.setCardholderLastName(request.getCardholderLastName());
        card.setValidThroughMonth(request.getValidThroughMonth());
        card.setValidThroughYear(request.getValidThroughYear());
        
        boolean valid = visaCardService.validateCard(card);
        if (valid) {
        	response.setStatus("valid card");
        } else {
        	response.setStatus("invalid card");
        }

		return response;
	}
}
