//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.07.15 at 04:20:22 PM CEST 
//

package eUtilAbstract2135422Jaxb;

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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}PubmedArticle"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pubmedArticle"
})
@XmlRootElement(name = "PubmedArticleSet")
public class PubmedArticleSet {

    @XmlElement(name = "PubmedArticle", required = true)
    protected PubmedArticle pubmedArticle;

    /**
     * Gets the value of the pubmedArticle property.
     * 
     * @return
     *     possible object is
     *     {@link PubmedArticle }
     *     
     */
    public PubmedArticle getPubmedArticle() {
        return pubmedArticle;
    }

    /**
     * Sets the value of the pubmedArticle property.
     * 
     * @param value
     *     allowed object is
     *     {@link PubmedArticle }
     *     
     */
    public void setPubmedArticle(PubmedArticle value) {
        this.pubmedArticle = value;
    }

}