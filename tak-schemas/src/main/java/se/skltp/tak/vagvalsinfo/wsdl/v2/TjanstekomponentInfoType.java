
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
 * <p>Java class for tjanstekomponentInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tjanstekomponentInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="hsaId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="beskrivning" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="anropsAdressInfo" type="{urn:skl:tp:vagvalsinfo:v2}anropsAdressInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="anropsbehorighetInfo" type="{urn:skl:tp:vagvalsinfo:v2}anropsbehorighetInfoForTjanstekomponentType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "tjanstekomponentInfoType", propOrder = {
    "hsaId",
    "beskrivning",
    "anropsAdressInfo",
    "anropsbehorighetInfo",
    "any"
})
public class TjanstekomponentInfoType {

    @XmlElement(required = true)
    protected String hsaId;
    @XmlElement(required = true)
    protected String beskrivning;
    @XmlElement(nillable = true)
    protected List<AnropsAdressInfoType> anropsAdressInfo;
    @XmlElement(nillable = true)
    protected List<AnropsbehorighetInfoForTjanstekomponentType> anropsbehorighetInfo;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the hsaId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHsaId() {
        return hsaId;
    }

    /**
     * Sets the value of the hsaId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHsaId(String value) {
        this.hsaId = value;
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
     * Gets the value of the anropsAdressInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the anropsAdressInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnropsAdressInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnropsAdressInfoType }
     * 
     * 
     */
    public List<AnropsAdressInfoType> getAnropsAdressInfo() {
        if (anropsAdressInfo == null) {
            anropsAdressInfo = new ArrayList<AnropsAdressInfoType>();
        }
        return this.anropsAdressInfo;
    }

    /**
     * Gets the value of the anropsbehorighetInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the anropsbehorighetInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnropsbehorighetInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnropsbehorighetInfoForTjanstekomponentType }
     * 
     * 
     */
    public List<AnropsbehorighetInfoForTjanstekomponentType> getAnropsbehorighetInfo() {
        if (anropsbehorighetInfo == null) {
            anropsbehorighetInfo = new ArrayList<AnropsbehorighetInfoForTjanstekomponentType>();
        }
        return this.anropsbehorighetInfo;
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
