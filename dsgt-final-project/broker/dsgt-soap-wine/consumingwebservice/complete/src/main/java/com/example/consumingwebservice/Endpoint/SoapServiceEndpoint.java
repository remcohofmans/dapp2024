package com.example.consumingwebservice.Endpoint;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.example.consumingwebservice.SoapService;
import com.example.consumingwebservice.wsdl.GetWineCardRequest;
import com.example.consumingwebservice.wsdl.Wine;

@Endpoint
public class SoapServiceEndpoint {

	private static final String NAMESPACE = "http://winemenu.io/gt/webservice";
	
	@Autowired
	private SoapService soapService;
	
	@PayloadRoot(namespace=NAMESPACE,localPart = "GetWineCardRequest")
	@ResponsePayload
	public List<Wine> getWines(@RequestPayload GetWineCardRequest request){
		return soapService.getWines(request);
	}
}
