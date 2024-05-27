
package be.kuleuven.dsgt4.SoapClient;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.3.0.2
 * Generated source version: 2.2
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
     *     returns be.kuleuven.dsgt4.SoapClient.GetOrderResponse
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
     *     returns be.kuleuven.dsgt4.SoapClient.GetMostExpensiveWineResponse
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
     *     returns be.kuleuven.dsgt4.SoapClient.GetWineResponse
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
     *     returns be.kuleuven.dsgt4.SoapClient.GetWineCardResponse
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
     *     returns be.kuleuven.dsgt4.SoapClient.GetCheapestWineResponse
     */
    @WebMethod
    @WebResult(name = "getCheapestWineResponse", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getCheapestWineResponse")
    public GetCheapestWineResponse getCheapestWine(
        @WebParam(name = "getCheapestWineRequest", targetNamespace = "http://winemenu.io/gt/webservice", partName = "getCheapestWineRequest")
        GetCheapestWineRequest getCheapestWineRequest);

}
