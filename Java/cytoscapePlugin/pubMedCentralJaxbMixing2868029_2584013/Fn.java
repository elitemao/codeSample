//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.22 at 10:08:21 �U�� CET 
//


package pubMedCentralJaxbMixing2868029_2584013;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for fn element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="fn">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}label" minOccurs="0"/>
 *           &lt;element ref="{}p"/>
 *         &lt;/sequence>
 *         &lt;attribute name="fn-type" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *         &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "label",
    "p"
})
@XmlRootElement(name = "fn")
public class Fn {

    protected String label;
    @XmlElement(required = true)
    protected P p;
    @XmlAttribute(name = "fn-type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fnType;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String id;

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the p property.
     * 
     * @return
     *     possible object is
     *     {@link P }
     *     
     */
    public P getP() {
        return p;
    }

    /**
     * Sets the value of the p property.
     * 
     * @param value
     *     allowed object is
     *     {@link P }
     *     
     */
    public void setP(P value) {
        this.p = value;
    }

    /**
     * Gets the value of the fnType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFnType() {
        return fnType;
    }

    /**
     * Sets the value of the fnType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFnType(String value) {
        this.fnType = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
