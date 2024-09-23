
package se.skltp.tak.vagvalsinfo.wsdl.v2;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the se.skltp.tak.vagvalsinfo.wsdl.v2 package. 
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

    private final static QName _AnropsBehorighetsInfo_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "anropsBehorighetsInfo");
    private final static QName _VirtualiseringsInfo_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "virtualiseringsInfo");
    private final static QName _TjanstekontraktInfo_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "tjanstekontraktInfo");
    private final static QName _TjanstekomponentInfo_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "tjanstekomponentInfo");
    private final static QName _HamtaAllaTjanstekontrakt_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaTjanstekontrakt");
    private final static QName _HamtaAllaTjanstekontraktRequest_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaTjanstekontraktRequest");
    private final static QName _HamtaAllaTjanstekontraktResponse_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaTjanstekontraktResponse");
    private final static QName _HamtaAllaAnropsBehorigheter_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaAnropsBehorigheter");
    private final static QName _HamtaAllaAnropsBehorigheterRequest_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaAnropsBehorigheterRequest");
    private final static QName _HamtaAllaAnropsBehorigheterResponse_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaAnropsBehorigheterResponse");
    private final static QName _HamtaAllaVirtualiseringar_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaVirtualiseringar");
    private final static QName _HamtaAllaVirtualiseringarRequest_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaVirtualiseringarRequest");
    private final static QName _HamtaAllaVirtualiseringarResponse_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaVirtualiseringarResponse");
    private final static QName _HamtaAllaTjanstekomponenter_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaTjanstekomponenter");
    private final static QName _HamtaAllaTjanstekomponenterRequest_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaTjanstekomponenterRequest");
    private final static QName _HamtaAllaTjanstekomponenterResponse_QNAME = new QName("urn:skl:tp:vagvalsinfo:v2", "hamtaAllaTjanstekomponenterResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: se.skltp.tak.vagvalsinfo.wsdl.v2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AnropsBehorighetsInfoType }
     * 
     */
    public AnropsBehorighetsInfoType createAnropsBehorighetsInfoType() {
        return new AnropsBehorighetsInfoType();
    }

    /**
     * Create an instance of {@link VirtualiseringsInfoType }
     * 
     */
    public VirtualiseringsInfoType createVirtualiseringsInfoType() {
        return new VirtualiseringsInfoType();
    }

    /**
     * Create an instance of {@link TjanstekontraktInfoType }
     * 
     */
    public TjanstekontraktInfoType createTjanstekontraktInfoType() {
        return new TjanstekontraktInfoType();
    }

    /**
     * Create an instance of {@link TjanstekomponentInfoType }
     * 
     */
    public TjanstekomponentInfoType createTjanstekomponentInfoType() {
        return new TjanstekomponentInfoType();
    }

    /**
     * Create an instance of {@link HamtaAllaTjanstekontraktResponseType }
     * 
     */
    public HamtaAllaTjanstekontraktResponseType createHamtaAllaTjanstekontraktResponseType() {
        return new HamtaAllaTjanstekontraktResponseType();
    }

    /**
     * Create an instance of {@link HamtaAllaAnropsBehorigheterResponseType }
     * 
     */
    public HamtaAllaAnropsBehorigheterResponseType createHamtaAllaAnropsBehorigheterResponseType() {
        return new HamtaAllaAnropsBehorigheterResponseType();
    }

    /**
     * Create an instance of {@link HamtaAllaVirtualiseringarResponseType }
     * 
     */
    public HamtaAllaVirtualiseringarResponseType createHamtaAllaVirtualiseringarResponseType() {
        return new HamtaAllaVirtualiseringarResponseType();
    }

    /**
     * Create an instance of {@link HamtaAllaTjanstekomponenterResponseType }
     * 
     */
    public HamtaAllaTjanstekomponenterResponseType createHamtaAllaTjanstekomponenterResponseType() {
        return new HamtaAllaTjanstekomponenterResponseType();
    }

    /**
     * Create an instance of {@link FilterInfoType }
     * 
     */
    public FilterInfoType createFilterInfoType() {
        return new FilterInfoType();
    }

    /**
     * Create an instance of {@link AnropsBehorighetsInfoIdType }
     * 
     */
    public AnropsBehorighetsInfoIdType createAnropsBehorighetsInfoIdType() {
        return new AnropsBehorighetsInfoIdType();
    }

    /**
     * Create an instance of {@link VirtualiseringsInfoIdType }
     * 
     */
    public VirtualiseringsInfoIdType createVirtualiseringsInfoIdType() {
        return new VirtualiseringsInfoIdType();
    }

    /**
     * Create an instance of {@link AnropsAdressInfoType }
     * 
     */
    public AnropsAdressInfoType createAnropsAdressInfoType() {
        return new AnropsAdressInfoType();
    }

    /**
     * Create an instance of {@link VagvalsInfoType }
     * 
     */
    public VagvalsInfoType createVagvalsInfoType() {
        return new VagvalsInfoType();
    }

    /**
     * Create an instance of {@link AnropsbehorighetInfoForTjanstekomponentType }
     * 
     */
    public AnropsbehorighetInfoForTjanstekomponentType createAnropsbehorighetInfoForTjanstekomponentType() {
        return new AnropsbehorighetInfoForTjanstekomponentType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AnropsBehorighetsInfoType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AnropsBehorighetsInfoType }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "anropsBehorighetsInfo")
    public JAXBElement<AnropsBehorighetsInfoType> createAnropsBehorighetsInfo(AnropsBehorighetsInfoType value) {
        return new JAXBElement<AnropsBehorighetsInfoType>(_AnropsBehorighetsInfo_QNAME, AnropsBehorighetsInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VirtualiseringsInfoType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link VirtualiseringsInfoType }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "virtualiseringsInfo")
    public JAXBElement<VirtualiseringsInfoType> createVirtualiseringsInfo(VirtualiseringsInfoType value) {
        return new JAXBElement<VirtualiseringsInfoType>(_VirtualiseringsInfo_QNAME, VirtualiseringsInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TjanstekontraktInfoType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TjanstekontraktInfoType }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "tjanstekontraktInfo")
    public JAXBElement<TjanstekontraktInfoType> createTjanstekontraktInfo(TjanstekontraktInfoType value) {
        return new JAXBElement<TjanstekontraktInfoType>(_TjanstekontraktInfo_QNAME, TjanstekontraktInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TjanstekomponentInfoType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TjanstekomponentInfoType }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "tjanstekomponentInfo")
    public JAXBElement<TjanstekomponentInfoType> createTjanstekomponentInfo(TjanstekomponentInfoType value) {
        return new JAXBElement<TjanstekomponentInfoType>(_TjanstekomponentInfo_QNAME, TjanstekomponentInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElement(nillable = true)
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaTjanstekontrakt")
    public JAXBElement<Object> createHamtaAllaTjanstekontrakt(Object value) {
        return new JAXBElement<Object>(_HamtaAllaTjanstekontrakt_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaTjanstekontraktRequest")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createHamtaAllaTjanstekontraktRequest(String value) {
        return new JAXBElement<String>(_HamtaAllaTjanstekontraktRequest_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HamtaAllaTjanstekontraktResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HamtaAllaTjanstekontraktResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaTjanstekontraktResponse")
    public JAXBElement<HamtaAllaTjanstekontraktResponseType> createHamtaAllaTjanstekontraktResponse(HamtaAllaTjanstekontraktResponseType value) {
        return new JAXBElement<HamtaAllaTjanstekontraktResponseType>(_HamtaAllaTjanstekontraktResponse_QNAME, HamtaAllaTjanstekontraktResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElement(nillable = true)
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaAnropsBehorigheter")
    public JAXBElement<Object> createHamtaAllaAnropsBehorigheter(Object value) {
        return new JAXBElement<Object>(_HamtaAllaAnropsBehorigheter_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaAnropsBehorigheterRequest")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createHamtaAllaAnropsBehorigheterRequest(String value) {
        return new JAXBElement<String>(_HamtaAllaAnropsBehorigheterRequest_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HamtaAllaAnropsBehorigheterResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HamtaAllaAnropsBehorigheterResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaAnropsBehorigheterResponse")
    public JAXBElement<HamtaAllaAnropsBehorigheterResponseType> createHamtaAllaAnropsBehorigheterResponse(HamtaAllaAnropsBehorigheterResponseType value) {
        return new JAXBElement<HamtaAllaAnropsBehorigheterResponseType>(_HamtaAllaAnropsBehorigheterResponse_QNAME, HamtaAllaAnropsBehorigheterResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElement(nillable = true)
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaVirtualiseringar")
    public JAXBElement<Object> createHamtaAllaVirtualiseringar(Object value) {
        return new JAXBElement<Object>(_HamtaAllaVirtualiseringar_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaVirtualiseringarRequest")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createHamtaAllaVirtualiseringarRequest(String value) {
        return new JAXBElement<String>(_HamtaAllaVirtualiseringarRequest_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HamtaAllaVirtualiseringarResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HamtaAllaVirtualiseringarResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaVirtualiseringarResponse")
    public JAXBElement<HamtaAllaVirtualiseringarResponseType> createHamtaAllaVirtualiseringarResponse(HamtaAllaVirtualiseringarResponseType value) {
        return new JAXBElement<HamtaAllaVirtualiseringarResponseType>(_HamtaAllaVirtualiseringarResponse_QNAME, HamtaAllaVirtualiseringarResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElement(nillable = true)
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaTjanstekomponenter")
    public JAXBElement<Object> createHamtaAllaTjanstekomponenter(Object value) {
        return new JAXBElement<Object>(_HamtaAllaTjanstekomponenter_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaTjanstekomponenterRequest")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createHamtaAllaTjanstekomponenterRequest(String value) {
        return new JAXBElement<String>(_HamtaAllaTjanstekomponenterRequest_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HamtaAllaTjanstekomponenterResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HamtaAllaTjanstekomponenterResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "urn:skl:tp:vagvalsinfo:v2", name = "hamtaAllaTjanstekomponenterResponse")
    public JAXBElement<HamtaAllaTjanstekomponenterResponseType> createHamtaAllaTjanstekomponenterResponse(HamtaAllaTjanstekomponenterResponseType value) {
        return new JAXBElement<HamtaAllaTjanstekomponenterResponseType>(_HamtaAllaTjanstekomponenterResponse_QNAME, HamtaAllaTjanstekomponenterResponseType.class, null, value);
    }

}
