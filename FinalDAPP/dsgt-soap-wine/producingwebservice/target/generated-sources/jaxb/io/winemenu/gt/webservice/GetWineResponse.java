//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.05.30 at 03:20:34 PM CEST 
//


package io.winemenu.gt.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
@XmlRootElement(name = "getWineResponse")
public class GetWineResponse {

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
