//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.22 at 10:08:21 �U�� CET 
//


package pubMedCentralJaxbMixing2868029_2584013;

import java.math.BigInteger;
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
 * <p>Java class for mixed-citation element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="mixed-citation">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;choice maxOccurs="unbounded">
 *             &lt;element ref="{}article-title"/>
 *             &lt;element ref="{}year"/>
 *             &lt;element ref="{}person-group"/>
 *           &lt;/choice>
 *           &lt;element ref="{}comment" minOccurs="0"/>
 *           &lt;element ref="{}source" minOccurs="0"/>
 *           &lt;choice minOccurs="0">
 *             &lt;element ref="{}volume"/>
 *             &lt;sequence>
 *               &lt;element ref="{}publisher-loc"/>
 *               &lt;element ref="{}publisher-name"/>
 *             &lt;/sequence>
 *           &lt;/choice>
 *           &lt;element ref="{}fpage" minOccurs="0"/>
 *           &lt;element ref="{}lpage" minOccurs="0"/>
 *           &lt;element ref="{}pub-id" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;attribute name="publication-type" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
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
    "articleTitleOrYearOrPersonGroup",
    "comment",
    "source",
    "volume",
    "publisherLoc",
    "publisherName",
    "fpage",
    "lpage",
    "pubId"
})
@XmlRootElement(name = "mixed-citation")
public class MixedCitation {

    @XmlElements({
        @XmlElement(name = "year", required = true, type = BigInteger.class),
        @XmlElement(name = "person-group", required = true, type = PersonGroup.class),
        @XmlElement(name = "article-title", required = true, type = ArticleTitle.class)
    })
    protected List<Object> articleTitleOrYearOrPersonGroup;
    protected Comment comment;
    protected String source;
    protected BigInteger volume;
    @XmlElement(name = "publisher-loc")
    protected String publisherLoc;
    @XmlElement(name = "publisher-name")
    protected String publisherName;
    protected String fpage;
    protected BigInteger lpage;
    @XmlElement(name = "pub-id")
    protected PubId pubId;
    @XmlAttribute(name = "publication-type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String publicationType;

    /**
     * Gets the value of the articleTitleOrYearOrPersonGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the articleTitleOrYearOrPersonGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArticleTitleOrYearOrPersonGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * {@link PersonGroup }
     * {@link ArticleTitle }
     * 
     * 
     */
    public List<Object> getArticleTitleOrYearOrPersonGroup() {
        if (articleTitleOrYearOrPersonGroup == null) {
            articleTitleOrYearOrPersonGroup = new ArrayList<Object>();
        }
        return this.articleTitleOrYearOrPersonGroup;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link Comment }
     *     
     */
    public Comment getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Comment }
     *     
     */
    public void setComment(Comment value) {
        this.comment = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the volume property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVolume() {
        return volume;
    }

    /**
     * Sets the value of the volume property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVolume(BigInteger value) {
        this.volume = value;
    }

    /**
     * Gets the value of the publisherLoc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublisherLoc() {
        return publisherLoc;
    }

    /**
     * Sets the value of the publisherLoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublisherLoc(String value) {
        this.publisherLoc = value;
    }

    /**
     * Gets the value of the publisherName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublisherName() {
        return publisherName;
    }

    /**
     * Sets the value of the publisherName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublisherName(String value) {
        this.publisherName = value;
    }

    /**
     * Gets the value of the fpage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFpage() {
        return fpage;
    }

    /**
     * Sets the value of the fpage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFpage(String value) {
        this.fpage = value;
    }

    /**
     * Gets the value of the lpage property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLpage() {
        return lpage;
    }

    /**
     * Sets the value of the lpage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLpage(BigInteger value) {
        this.lpage = value;
    }

    /**
     * Gets the value of the pubId property.
     * 
     * @return
     *     possible object is
     *     {@link PubId }
     *     
     */
    public PubId getPubId() {
        return pubId;
    }

    /**
     * Sets the value of the pubId property.
     * 
     * @param value
     *     allowed object is
     *     {@link PubId }
     *     
     */
    public void setPubId(PubId value) {
        this.pubId = value;
    }

    /**
     * Gets the value of the publicationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicationType() {
        return publicationType;
    }

    /**
     * Sets the value of the publicationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicationType(String value) {
        this.publicationType = value;
    }

}
