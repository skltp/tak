
package se.skltp.tak.vagvalsinfo.wsdl.v2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for vagvalsInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="vagvalsInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fromTidpunkt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="tomTidpunkt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="tjanstekontraktNamnrymd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="logiskAdressHsaId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="logiskAdressBeskrivning" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
@XmlType(name = "vagvalsInfoType", propOrder = {
    "fromTidpunkt",
    "tomTidpunkt",
    "tjanstekontraktNamnrymd",
    "logiskAdressHsaId",
    "logiskAdressBeskrivning",
    "any"
})
public class VagvalsInfoType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fromTidpunkt;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar tomTidpunkt;
    @XmlElement(required = true)
    protected String tjanstekontraktNamnrymd;
    @XmlElement(required = true)
    protected String logiskAdressHsaId;
    @XmlElement(required = true)
    protected String logiskAdressBeskrivning;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the fromTidpunkt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFromTidpunkt() {
        return fromTidpunkt;
    }

    /**
     * Sets the value of the fromTidpunkt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFromTidpunkt(XMLGregorianCalendar value) {
        this.fromTidpunkt = value;
    }

    /**
     * Gets the value of the tomTidpunkt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTomTidpunkt() {
        return tomTidpunkt;
    }

    /**
     * Sets the value of the tomTidpunkt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTomTidpunkt(XMLGregorianCalendar value) {
        this.tomTidpunkt = value;
    }

    /**
     * Gets the value of the tjanstekontraktNamnrymd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTjanstekontraktNamnrymd() {
        return tjanstekontraktNamnrymd;
    }

    /**
     * Sets the value of the tjanstekontraktNamnrymd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTjanstekontraktNamnrymd(String value) {
        this.tjanstekontraktNamnrymd = value;
    }

    /**
     * Gets the value of the logiskAdressHsaId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogiskAdressHsaId() {
        return logiskAdressHsaId;
    }

    /**
     * Sets the value of the logiskAdressHsaId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogiskAdressHsaId(String value) {
        this.logiskAdressHsaId = value;
    }

    /**
     * Gets the value of the logiskAdressBeskrivning property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogiskAdressBeskrivning() {
        return logiskAdressBeskrivning;
    }

    /**
     * Sets the value of the logiskAdressBeskrivning property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogiskAdressBeskrivning(String value) {
        this.logiskAdressBeskrivning = value;
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
