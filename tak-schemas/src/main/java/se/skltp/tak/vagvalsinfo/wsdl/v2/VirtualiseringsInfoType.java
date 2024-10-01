
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
 * <p>Java class for virtualiseringsInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="virtualiseringsInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="virtualiseringsInfoId" type="{urn:skl:tp:vagvalsinfo:v2}virtualiseringsInfoIdType"/&gt;
 *         &lt;element name="receiverId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="rivProfil" type="{http://www.w3.org/2001/XMLSchema}NCName"/&gt;
 *         &lt;element name="tjansteKontrakt" type="{http://www.w3.org/2001/XMLSchema}NCName"/&gt;
 *         &lt;element name="fromTidpunkt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="tomTidpunkt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="adress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
@XmlType(name = "virtualiseringsInfoType", propOrder = {
    "virtualiseringsInfoId",
    "receiverId",
    "rivProfil",
    "tjansteKontrakt",
    "fromTidpunkt",
    "tomTidpunkt",
    "adress",
    "any"
})
public class VirtualiseringsInfoType {

    @XmlElement(required = true)
    protected VirtualiseringsInfoIdType virtualiseringsInfoId;
    @XmlElement(required = true)
    protected String receiverId;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String rivProfil;
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
    @XmlElement(required = true)
    protected String adress;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the virtualiseringsInfoId property.
     * 
     * @return
     *     possible object is
     *     {@link VirtualiseringsInfoIdType }
     *     
     */
    public VirtualiseringsInfoIdType getVirtualiseringsInfoId() {
        return virtualiseringsInfoId;
    }

    /**
     * Sets the value of the virtualiseringsInfoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link VirtualiseringsInfoIdType }
     *     
     */
    public void setVirtualiseringsInfoId(VirtualiseringsInfoIdType value) {
        this.virtualiseringsInfoId = value;
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
     * Gets the value of the rivProfil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRivProfil() {
        return rivProfil;
    }

    /**
     * Sets the value of the rivProfil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRivProfil(String value) {
        this.rivProfil = value;
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
     * Gets the value of the adress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdress() {
        return adress;
    }

    /**
     * Sets the value of the adress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdress(String value) {
        this.adress = value;
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
