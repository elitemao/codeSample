//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.22 at 10:08:21 �U�� CET 
//


package pubMedCentralJaxbMixing2868029_2584013;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for contrib element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="contrib">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}name"/>
 *           &lt;element ref="{}role" minOccurs="0"/>
 *           &lt;element ref="{}xref" maxOccurs="unbounded"/>
 *         &lt;/sequence>
 *         &lt;attribute name="contrib-type" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
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
    "name",
    "role",
    "xref"
})
@XmlRootElement(name = "contrib")
public class Contrib {

    @XmlElement(required = true)
    protected Name name;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String role;
    @XmlElement(required = true)
    protected List<Xref> xref;
    @XmlAttribute(name = "contrib-type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String contribType;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link Name }
     *     
     */
    public Name getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link Name }
     *     
     */
    public void setName(Name value) {
        this.name = value;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the xref property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xref property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXref().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Xref }
     * 
     * 
     */
    public List<Xref> getXref() {
        if (xref == null) {
            xref = new ArrayList<Xref>();
        }
        return this.xref;
    }

    /**
     * Gets the value of the contribType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContribType() {
        return contribType;
    }

    /**
     * Sets the value of the contribType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContribType(String value) {
        this.contribType = value;
    }

}