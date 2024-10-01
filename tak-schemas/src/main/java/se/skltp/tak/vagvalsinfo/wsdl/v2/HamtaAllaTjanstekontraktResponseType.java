
package se.skltp.tak.vagvalsinfo.wsdl.v2;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for hamtaAllaTjanstekontraktResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hamtaAllaTjanstekontraktResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:skl:tp:vagvalsinfo:v2}tjanstekontraktInfo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hamtaAllaTjanstekontraktResponseType", propOrder = {
    "tjanstekontraktInfo"
})
public class HamtaAllaTjanstekontraktResponseType {

    protected List<TjanstekontraktInfoType> tjanstekontraktInfo;

    /**
     * Gets the value of the tjanstekontraktInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the tjanstekontraktInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTjanstekontraktInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TjanstekontraktInfoType }
     * 
     * 
     */
    public List<TjanstekontraktInfoType> getTjanstekontraktInfo() {
        if (tjanstekontraktInfo == null) {
            tjanstekontraktInfo = new ArrayList<TjanstekontraktInfoType>();
        }
        return this.tjanstekontraktInfo;
    }

}
