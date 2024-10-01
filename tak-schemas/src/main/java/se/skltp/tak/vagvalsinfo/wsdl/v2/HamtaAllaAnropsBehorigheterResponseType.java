
package se.skltp.tak.vagvalsinfo.wsdl.v2;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for hamtaAllaAnropsBehorigheterResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hamtaAllaAnropsBehorigheterResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:skl:tp:vagvalsinfo:v2}anropsBehorighetsInfo" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hamtaAllaAnropsBehorigheterResponseType", propOrder = {
    "anropsBehorighetsInfo"
})
public class HamtaAllaAnropsBehorigheterResponseType {

    @XmlElement(required = true)
    protected List<AnropsBehorighetsInfoType> anropsBehorighetsInfo;

    /**
     * Gets the value of the anropsBehorighetsInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the anropsBehorighetsInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnropsBehorighetsInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnropsBehorighetsInfoType }
     * 
     * 
     */
    public List<AnropsBehorighetsInfoType> getAnropsBehorighetsInfo() {
        if (anropsBehorighetsInfo == null) {
            anropsBehorighetsInfo = new ArrayList<AnropsBehorighetsInfoType>();
        }
        return this.anropsBehorighetsInfo;
    }

}
