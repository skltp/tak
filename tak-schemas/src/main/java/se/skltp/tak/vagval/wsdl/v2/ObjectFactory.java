
package se.skltp.tak.vagval.wsdl.v2;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the se.skltp.tak.vagval.wsdl.v2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: se.skltp.tak.vagval.wsdl.v2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VisaVagvalRequest }
     * 
     */
    public VisaVagvalRequest createVisaVagvalRequest() {
        return new VisaVagvalRequest();
    }

    /**
     * Create an instance of {@link VisaVagvalResponse }
     * 
     */
    public VisaVagvalResponse createVisaVagvalResponse() {
        return new VisaVagvalResponse();
    }

    /**
     * Create an instance of {@link ResetVagvalCacheRequest }
     * 
     */
    public ResetVagvalCacheRequest createResetVagvalCacheRequest() {
        return new ResetVagvalCacheRequest();
    }

    /**
     * Create an instance of {@link ResetVagvalCacheResponse }
     * 
     */
    public ResetVagvalCacheResponse createResetVagvalCacheResponse() {
        return new ResetVagvalCacheResponse();
    }

}
