
package se.skltp.tak.vagval.wsdl.v2;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="resetResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="processingLog" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "resetResult",
    "processingLog"
})
@XmlRootElement(name = "resetVagvalCacheResponse")
public class ResetVagvalCacheResponse {

    protected boolean resetResult;
    @XmlElement(required = true)
    protected List<String> processingLog;

    /**
     * Gets the value of the resetResult property.
     * 
     */
    public boolean isResetResult() {
        return resetResult;
    }

    /**
     * Sets the value of the resetResult property.
     * 
     */
    public void setResetResult(boolean value) {
        this.resetResult = value;
    }

    /**
     * Gets the value of the processingLog property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the processingLog property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessingLog().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getProcessingLog() {
        if (processingLog == null) {
            processingLog = new ArrayList<String>();
        }
        return this.processingLog;
    }

}
