
package com.example.consumingwebservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.example.consumingwebservice.wsdl.GetWineCardRequest;
import com.example.consumingwebservice.wsdl.GetWineCardResponse;

public class SoapClient extends WebServiceGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(SoapClient.class);

	public GetWineCardResponse getWine() {

		GetWineCardRequest request = new GetWineCardRequest();

		log.info("Requesting...");

		GetWineCardResponse response = (GetWineCardResponse) getWebServiceTemplate()
				.marshalSendAndReceive("http://dapp.uksouth.cloudapp.azure.com:12000/ws/wines", request,
						new SoapActionCallback(
								"http://spring.io/guides/gs-producing-web-service/GetWineCardRequest"));

		return response;
	}

}
