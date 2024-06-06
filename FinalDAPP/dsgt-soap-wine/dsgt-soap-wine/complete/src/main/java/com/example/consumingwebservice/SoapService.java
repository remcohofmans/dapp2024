package com.example.consumingwebservice;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.consumingwebservice.wsdl.GetWineCardRequest;
import com.example.consumingwebservice.wsdl.GetWineCardResponse;
import com.example.consumingwebservice.wsdl.Wine;

@Service
public class SoapService {

	
	  public List<Wine> getWines(GetWineCardRequest request) {
	    	SoapClient soapClient=new SoapClient();
	    	
	    	GetWineCardResponse response = soapClient.getWine();
			if (response != null && response.getWine() != null) {
				List<Wine> wines = response.getWine();
				return wines;
			}
	        return null;
	    }
}
