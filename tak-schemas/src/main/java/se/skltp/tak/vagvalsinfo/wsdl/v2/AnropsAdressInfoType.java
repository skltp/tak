
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
 * <p>Java class for anropsAdressInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="anropsAdressInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="adress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="rivtaProfilNamn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="vagvalsInfo" type="{urn:skl:tp:vagvalsinfo:v2}vagvalsInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "anropsAdressInfoType", propOrder = {
    "adress",
    "rivtaProfilNamn",
    "vagvalsInfo",
    "any"
})
public class AnropsAdressInfoType {

    @XmlElement(required = true)
    protected String adress;
    @XmlElement(required = true)
    protected String rivtaProfilNamn;
    @XmlElement(nillable = true)
    protected List<VagvalsInfoType> vagvalsInfo;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

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
     * Gets the value of the rivtaProfilNamn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRivtaProfilNamn() {
        return rivtaProfilNamn;
    }

    /**
     * Sets the value of the rivtaProfilNamn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRivtaProfilNamn(String value) {
        this.rivtaProfilNamn = value;
    }

    /**
     * Gets the value of the vagvalsInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the vagvalsInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVagvalsInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VagvalsInfoType }
     * 
     * 
     */
    public List<VagvalsInfoType> getVagvalsInfo() {
        if (vagvalsInfo == null) {
            vagvalsInfo = new ArrayList<VagvalsInfoType>();
        }
        return this.vagvalsInfo;
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
