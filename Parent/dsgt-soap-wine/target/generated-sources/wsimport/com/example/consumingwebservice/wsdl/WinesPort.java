
package com.example.consumingwebservice.wsdl;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 3.0.0
 * Generated source version: 3.0
 * 
 */
@WebService(name = "WinesPort", targetNamespace = "http://winemenu.io/gt/webservice")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface WinesPort {


    /**
     * 
     * @param getOrderRequest
     * @return
     *     returns com.example.consumingwebservice.wsdl.GetOrderResponse
     */
    @WebMethod
    @WebResult(name = "getOrderResponse", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getOrderResponse")
    public GetOrderResponse getOrder(
        @WebParam(name = "getOrderRequest", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getOrderRequest")
        GetOrderRequest getOrderRequest);

    /**
     * 
     * @param getMostExpensiveWineRequest
     * @return
     *     returns com.example.consumingwebservice.wsdl.GetMostExpensiveWineResponse
     */
    @WebMethod
    @WebResult(name = "getMostExpensiveWineResponse", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getMostExpensiveWineResponse")
    public GetMostExpensiveWineResponse getMostExpensiveWine(
        @WebParam(name = "getMostExpensiveWineRequest", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getMostExpensiveWineRequest")
        GetMostExpensiveWineRequest getMostExpensiveWineRequest);

    /**
     * 
     * @param getWineRequest
     * @return
     *     returns com.example.consumingwebservice.wsdl.GetWineResponse
     */
    @WebMethod
    @WebResult(name = "getWineResponse", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getWineResponse")
    public GetWineResponse getWine(
        @WebParam(name = "getWineRequest", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getWineRequest")
        GetWineRequest getWineRequest);

    /**
     * 
     * @param getWineCardRequest
     * @return
     *     returns com.example.consumingwebservice.wsdl.GetWineCardResponse
     */
    @WebMethod
    @WebResult(name = "getWineCardResponse", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getWineCardResponse")
    public GetWineCardResponse getWineCard(
        @WebParam(name = "getWineCardRequest", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getWineCardRequest")
        GetWineCardRequest getWineCardRequest);

    /**
     * 
     * @param getCheapestWineRequest
     * @return
     *     returns com.example.consumingwebservice.wsdl.GetCheapestWineResponse
     */
    @WebMethod
    @WebResult(name = "getCheapestWineResponse", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getCheapestWineResponse")
    public GetCheapestWineResponse getCheapestWine(
        @WebParam(name = "getCheapestWineRequest", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getCheapestWineRequest")
        GetCheapestWineRequest getCheapestWineRequest);

}
