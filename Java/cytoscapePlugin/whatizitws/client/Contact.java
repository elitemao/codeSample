
package whatizitws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for contact complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contact">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pipelineName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="convertToHtml" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contact", propOrder = {
    "pipelineName",
    "text",
    "convertToHtml"
})
public class Contact {

    protected String pipelineName;
    protected String text;
    protected boolean convertToHtml;

    /**
     * Gets the value of the pipelineName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPipelineName() {
        return pipelineName;
    }

    /**
     * Sets the value of the pipelineName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPipelineName(String value) {
        this.pipelineName = value;
    }

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Gets the value of the convertToHtml property.
     * 
     */
    public boolean isConvertToHtml() {
        return convertToHtml;
    }

    /**
     * Sets the value of the convertToHtml property.
     * 
     */
    public void setConvertToHtml(boolean value) {
        this.convertToHtml = value;
    }

}
