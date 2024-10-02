
package se.skltp.tak.vagvalsinfo.wsdl.v2;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for tjanstekontraktInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tjanstekontraktInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="namnrymd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="majorVersion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="minorVersion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="beskrivning" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tjanstekontraktInfoType", propOrder = {
    "namnrymd",
    "majorVersion",
    "minorVersion",
    "beskrivning",
    "any"
})
public class TjanstekontraktInfoType {

    @XmlElement(required = true)
    protected String namnrymd;
    @XmlElement(required = true)
    protected String majorVersion;
    @XmlElement(required = true)
    protected String minorVersion;
    @XmlElement(required = true)
    protected String beskrivning;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the namnrymd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamnrymd() {
        return namnrymd;
    }

    /**
     * Sets the value of the namnrymd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamnrymd(String value) {
        this.namnrymd = value;
    }

    /**
     * Gets the value of the majorVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMajorVersion() {
        return majorVersion;
    }

    /**
     * Sets the value of the majorVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMajorVersion(String value) {
        this.majorVersion = value;
    }

    /**
     * Gets the value of the minorVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinorVersion() {
        return minorVersion;
    }

    /**
     * Sets the value of the minorVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinorVersion(String value) {
        this.minorVersion = value;
    }

    /**
     * Gets the value of the beskrivning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeskrivning() {
        return beskrivning;
    }

    /**
     * Sets the value of the beskrivning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeskrivning(String value) {
        this.beskrivning = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

}
