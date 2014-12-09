/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.tak.services;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaVirtualiseringarResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.SokVagvalsInfoInterface;
import se.skltp.tak.vagvalsinfo.wsdl.v2.SokVagvalsServiceSoap11LitDocService;

/**
 * Created by magnus on 25/10/14.
 */
public class TestConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TestConsumer.class);
    private final SokVagvalsInfoInterface port;

    public static void main(String[] args) {
        configureLog4J();

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

    /**
     * Configure Log4J programmatic instead of a separate log4j - config file
     */
    static private void configureLog4J() {
        System.setProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Log4jLogger");
        org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO); // Change to DEBUG to see some output
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n")));
    }
}
