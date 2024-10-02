
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
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3c.dom.Element;


/**
 * <p>Java class for anropsBehorighetsInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="anropsBehorighetsInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="anropsBehorighetsInfoId" type="{urn:skl:tp:vagvalsinfo:v2}anropsBehorighetsInfoIdType"/&gt;
 *         &lt;element name="receiverId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="senderId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="tjansteKontrakt" type="{http://www.w3.org/2001/XMLSchema}NCName"/&gt;
 *         &lt;element name="fromTidpunkt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="tomTidpunkt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="filterInfo" type="{urn:skl:tp:vagvalsinfo:v2}filterInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "anropsBehorighetsInfoType", propOrder = {
    "anropsBehorighetsInfoId",
    "receiverId",
    "senderId",
    "tjansteKontrakt",
    "fromTidpunkt",
    "tomTidpunkt",
    "filterInfo",
    "any"
})
public class AnropsBehorighetsInfoType {

    @XmlElement(required = true)
    protected AnropsBehorighetsInfoIdType anropsBehorighetsInfoId;
    @XmlElement(required = true)
    protected String receiverId;
    @XmlElement(required = true)
    protected String senderId;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String tjansteKontrakt;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fromTidpunkt;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar tomTidpunkt;
    @XmlElement(nillable = true)
    protected List<FilterInfoType> filterInfo;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the anropsBehorighetsInfoId property.
     * 
     * @return
     *     possible object is
     *     {@link AnropsBehorighetsInfoIdType }
     *     
     */
    public AnropsBehorighetsInfoIdType getAnropsBehorighetsInfoId() {
        return anropsBehorighetsInfoId;
    }

    /**
     * Sets the value of the anropsBehorighetsInfoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnropsBehorighetsInfoIdType }
     *     
     */
    public void setAnropsBehorighetsInfoId(AnropsBehorighetsInfoIdType value) {
        this.anropsBehorighetsInfoId = value;
    }

    /**
     * Gets the value of the receiverId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * Sets the value of the receiverId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiverId(String value) {
        this.receiverId = value;
    }

    /**
     * Gets the value of the senderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Sets the value of the senderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderId(String value) {
        this.senderId = value;
    }

    /**
     * Gets the value of the tjansteKontrakt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTjansteKontrakt() {
        return tjansteKontrakt;
    }

    /**
     * Sets the value of the tjansteKontrakt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTjansteKontrakt(String value) {
        this.tjansteKontrakt = value;
    }

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
     * Gets the value of the filterInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the filterInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFilterInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FilterInfoType }
     * 
     * 
     */
    public List<FilterInfoType> getFilterInfo() {
        if (filterInfo == null) {
            filterInfo = new ArrayList<FilterInfoType>();
        }
        return this.filterInfo;
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
