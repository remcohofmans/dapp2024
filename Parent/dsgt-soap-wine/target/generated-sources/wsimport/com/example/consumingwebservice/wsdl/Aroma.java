
package com.example.consumingwebservice.wsdl;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for aroma.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="aroma"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CITRUS"/&gt;
 *     &lt;enumeration value="BLACKBERRY"/&gt;
 *     &lt;enumeration value="CHERRY"/&gt;
 *     &lt;enumeration value="PASSION_FRUIT"/&gt;
 *     &lt;enumeration value="ROSE"/&gt;
 *     &lt;enumeration value="JASMINE"/&gt;
 *     &lt;enumeration value="VIOLET"/&gt;
 *     &lt;enumeration value="ELDERFLOWER"/&gt;
 *     &lt;enumeration value="PEPPER"/&gt;
 *     &lt;enumeration value="CLOVE"/&gt;
 *     &lt;enumeration value="CINNAMON"/&gt;
 *     &lt;enumeration value="VANILLA"/&gt;
 *     &lt;enumeration value="WET STONE"/&gt;
 *     &lt;enumeration value="SLATE"/&gt;
 *     &lt;enumeration value="SMOKE"/&gt;
 *     &lt;enumeration value="CEDAR"/&gt;
 *     &lt;enumeration value="PLUM"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "aroma")
@XmlEnum
public enum Aroma {

    CITRUS("CITRUS"),
    BLACKBERRY("BLACKBERRY"),
    CHERRY("CHERRY"),
    PASSION_FRUIT("PASSION_FRUIT"),
    ROSE("ROSE"),
    JASMINE("JASMINE"),
    VIOLET("VIOLET"),
    ELDERFLOWER("ELDERFLOWER"),
    PEPPER("PEPPER"),
    CLOVE("CLOVE"),
    CINNAMON("CINNAMON"),
    VANILLA("VANILLA"),
    @XmlEnumValue("WET STONE")
    WET_STONE("WET STONE"),
    SLATE("SLATE"),
    SMOKE("SMOKE"),
    CEDAR("CEDAR"),
    PLUM("PLUM");
    private final String value;

    Aroma(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Aroma fromValue(String v) {
        for (Aroma c: Aroma.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
