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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for sec element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="sec">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;choice maxOccurs="unbounded" minOccurs="0">
 *             &lt;element ref="{}sec"/>
 *             &lt;element ref="{}title"/>
 *           &lt;/choice>
 *           &lt;choice>
 *             &lt;choice maxOccurs="unbounded">
 *               &lt;element ref="{}p"/>
 *               &lt;element ref="{}fig"/>
 *               &lt;element ref="{}table-wrap"/>
 *             &lt;/choice>
 *             &lt;element ref="{}supplementary-material" maxOccurs="unbounded"/>
 *           &lt;/choice>
 *         &lt;/sequence>
 *         &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *         &lt;attribute name="sec-type" type="{http://www.w3.org/2001/XMLSchema}NCName" />
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
    "secOrTitle",
    "pOrFigOrTableWrap",
    "supplementaryMaterial"
})
@XmlRootElement(name = "sec")
public class Sec {

    @XmlElements({
        @XmlElement(name = "sec", required = true, type = Sec.class),
        @XmlElement(name = "title", required = true, type = Title.class)
    })
    protected List<Object> secOrTitle;
    @XmlElements({
        @XmlElement(name = "table-wrap", required = true, type = TableWrap.class),
        @XmlElement(name = "fig", required = true, type = Fig.class),
        @XmlElement(name = "p", required = true, type = P.class)
    })
    protected List<Object> pOrFigOrTableWrap;
    @XmlElement(name = "supplementary-material", required = true)
    protected List<SupplementaryMaterial> supplementaryMaterial;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String id;
    @XmlAttribute(name = "sec-type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String secType;

    /**
     * Gets the value of the secOrTitle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the secOrTitle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSecOrTitle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Sec }
     * {@link Title }
     * 
     * 
     */
    public List<Object> getSecOrTitle() {
        if (secOrTitle == null) {
            secOrTitle = new ArrayList<Object>();
        }
        return this.secOrTitle;
    }

    /**
     * Gets the value of the pOrFigOrTableWrap property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pOrFigOrTableWrap property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPOrFigOrTableWrap().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TableWrap }
     * {@link Fig }
     * {@link P }
     * 
     * 
     */
    public List<Object> getPOrFigOrTableWrap() {
        if (pOrFigOrTableWrap == null) {
            pOrFigOrTableWrap = new ArrayList<Object>();
        }
        return this.pOrFigOrTableWrap;
    }

    /**
     * Gets the value of the supplementaryMaterial property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplementaryMaterial property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplementaryMaterial().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupplementaryMaterial }
     * 
     * 
     */
    public List<SupplementaryMaterial> getSupplementaryMaterial() {
        if (supplementaryMaterial == null) {
            supplementaryMaterial = new ArrayList<SupplementaryMaterial>();
        }
        return this.supplementaryMaterial;
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

    /**
     * Gets the value of the secType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecType() {
        return secType;
    }

    /**
     * Sets the value of the secType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecType(String value) {
        this.secType = value;
    }

}
