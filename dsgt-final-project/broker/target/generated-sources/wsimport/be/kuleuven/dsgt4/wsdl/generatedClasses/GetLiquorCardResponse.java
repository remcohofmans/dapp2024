
package be.kuleuven.dsgt4.wsdl.generatedClasses;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="liquor" type="{http://liquormenu.io/gt/webservice}liquor" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "liquor"
})
@XmlRootElement(name = "getLiquorCardResponse")
public class GetLiquorCardResponse {

    protected List<Liquor> liquor;

    /**
     * Gets the value of the liquor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the liquor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLiquor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Liquor }
     * 
     * 
     */
    public List<Liquor> getLiquor() {
        if (liquor == null) {
            liquor = new ArrayList<Liquor>();
        }
        return this.liquor;
    }

}
