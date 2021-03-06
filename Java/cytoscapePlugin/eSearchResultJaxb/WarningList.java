// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.01.26 at 06:35:42 PM CET 
//

package eSearchResultJaxb;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{}PhraseIgnored" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}QuotedPhraseNotFound" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}OutputMessage" maxOccurs="unbounded" minOccurs="0"/>
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
    "phraseIgnored",
    "quotedPhraseNotFound",
    "outputMessage"
})
@XmlRootElement(name = "WarningList")
public class WarningList {

    @XmlElement(name = "PhraseIgnored")
    protected List<String> phraseIgnored;
    @XmlElement(name = "QuotedPhraseNotFound")
    protected List<String> quotedPhraseNotFound;
    @XmlElement(name = "OutputMessage")
    protected List<String> outputMessage;

    /**
     * Gets the value of the phraseIgnored property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the phraseIgnored property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhraseIgnored().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPhraseIgnored() {
        if (phraseIgnored == null) {
            phraseIgnored = new ArrayList<String>();
        }
        return this.phraseIgnored;
    }

    /**
     * Gets the value of the quotedPhraseNotFound property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quotedPhraseNotFound property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuotedPhraseNotFound().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getQuotedPhraseNotFound() {
        if (quotedPhraseNotFound == null) {
            quotedPhraseNotFound = new ArrayList<String>();
        }
        return this.quotedPhraseNotFound;
    }

    /**
     * Gets the value of the outputMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outputMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutputMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOutputMessage() {
        if (outputMessage == null) {
            outputMessage = new ArrayList<String>();
        }
        return this.outputMessage;
    }

}
