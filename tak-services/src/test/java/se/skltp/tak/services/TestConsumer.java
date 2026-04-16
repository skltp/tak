package se.skltp.tak.services;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaVirtualiseringarResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.SokVagvalsInfoInterface;
import se.skltp.tak.vagvalsinfo.wsdl.v2.SokVagvalsServiceSoap11LitDocService;

public class TestConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TestConsumer.class);
    private final SokVagvalsInfoInterface port;

    public static void main(String[] args) {
        logger.info("Fetch all virtualizations from TAK...");
        new TestConsumer("http://localhost:8080/SokVagvalsInfo/v2").test();
    }

    public TestConsumer(String url) {
        this.port = getPort(url);
    }

    public void test() {
        HamtaAllaVirtualiseringarResponseType t = port.hamtaAllaVirtualiseringar(null);
        logger.info("No of v's: {}", t.getVirtualiseringsInfo().size());
    }

    private SokVagvalsInfoInterface getPort(String url) {
        logger.info("Use TAK endpoint adress: {}", url);
        SokVagvalsServiceSoap11LitDocService service = new SokVagvalsServiceSoap11LitDocService(
            createEndpointUrlFromServiceAddress(url));
        SokVagvalsInfoInterface port = service.getSokVagvalsSoap11LitDocPort();

        setupMessageLogging(port);

        return port;
    }

    private void setupMessageLogging(SokVagvalsInfoInterface port) {
        Client client = ClientProxy.getClient(port);
        client.getInInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());
    }

    private URL createEndpointUrlFromWsdl(String adressOfWsdl) {
        try {
            return new URL(adressOfWsdl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param serviceAddress, e.g. http://localhost:8080/tppoc-vagvalsinfo-module-web-g/services/SokVagvalsInfoService
     * @return
     */
    private URL createEndpointUrlFromServiceAddress(String serviceAddress) {
        return createEndpointUrlFromWsdl(serviceAddress + "?wsdl");
    }
}
