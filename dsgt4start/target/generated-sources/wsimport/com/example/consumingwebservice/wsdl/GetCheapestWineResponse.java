
package com.example.consumingwebservice.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="wine" type="{http://winemenu.io/gt/webservice}wine"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "wine"
})
@XmlRootElement(name = "getCheapestWineResponse")
public class GetCheapestWineResponse {

    @XmlElement(required = true)
    protected Wine wine;

    /**
     * Gets the value of the wine property.
     * 
     * @return
     *     possible object is
     *     {@link Wine }
     *     
     */
    public Wine getWine() {
        return wine;
    }

    /**
     * Sets the value of the wine property.
     * 
     * @param value
     *     allowed object is
     *     {@link Wine }
     *     
     */
    public void setWine(Wine value) {
        this.wine = value;
    }

}
