//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.22 at 10:08:21 �U�� CET 
//


package pubMedCentralJaxbMixing2868029_2584013;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for title-group element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="title-group">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}article-title"/>
 *           &lt;element ref="{}alt-title"/>
 *         &lt;/sequence>
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
    "articleTitle",
    "altTitle"
})
@XmlRootElement(name = "title-group")
public class TitleGroup {

    @XmlElement(name = "article-title", required = true)
    protected ArticleTitle articleTitle;
    @XmlElement(name = "alt-title", required = true)
    protected AltTitle altTitle;

    /**
     * Gets the value of the articleTitle property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleTitle }
     *     
     */
    public ArticleTitle getArticleTitle() {
        return articleTitle;
    }

    /**
     * Sets the value of the articleTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleTitle }
     *     
     */
    public void setArticleTitle(ArticleTitle value) {
        this.articleTitle = value;
    }

    /**
     * Gets the value of the altTitle property.
     * 
     * @return
     *     possible object is
     *     {@link AltTitle }
     *     
     */
    public AltTitle getAltTitle() {
        return altTitle;
    }

    /**
     * Sets the value of the altTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link AltTitle }
     *     
     */
    public void setAltTitle(AltTitle value) {
        this.altTitle = value;
    }

}