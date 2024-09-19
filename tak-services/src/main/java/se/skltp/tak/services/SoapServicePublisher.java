package se.skltp.tak.services;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.InputStream;

public class SoapServicePublisher {

    private static final Logger log = LoggerFactory.getLogger(SoapServicePublisher.class);
    private final Bus bus;
    private final AbstractPhaseInterceptor<Message> interceptor;

    public SoapServicePublisher(Bus bus, boolean logIncomingRequests) {
        this.bus = bus;
        interceptor = logIncomingRequests ? getInterceptor() : null;
    }

    Endpoint publish(String path, Object svc) {
        EndpointImpl epi = new EndpointImpl(bus, svc);
        epi.publish(path);
        if (interceptor != null) {
            epi.getInInterceptors().add(interceptor);
        }
        return epi;
    }

    private static AbstractPhaseInterceptor<Message> getInterceptor() {
        return new AbstractPhaseInterceptor<Message>(Phase.RECEIVE) {
            @Override
            public void handleMessage(Message message) {
                InputStream msgIs = message.getContent(InputStream.class);
                byte[] msgBuffer = null;
                try {
                    msgBuffer = msgIs.readAllBytes();
                } catch (IOException e) {
                    log.warn("Could not read incoming message", e);
                }
                if (msgBuffer != null) {
                    message.setContent(InputStream.class, new java.io.ByteArrayInputStream(msgBuffer));
                    String pre = "incoming.message.data.";
                    try {

                        MDC.put(pre+"payload", new String(msgBuffer));
                        MDC.put(pre+"headers", String.valueOf(message.get(Message.PROTOCOL_HEADERS)));
                        MDC.put(pre+"method", String.valueOf(message.get(Message.HTTP_REQUEST_METHOD)));
                        MDC.put(pre+"requestPath", String.valueOf(message.get(Message.PATH_INFO)));
                        MDC.put(pre+"requestUri", String.valueOf(message.get(Message.REQUEST_URI)));
                        MDC.put(pre+"requestUrl", String.valueOf(message.get(Message.REQUEST_URL)));
                        MDC.put(pre+"queryString", String.valueOf(message.get(Message.QUERY_STRING)));
                        MDC.put(pre+"encoding", String.valueOf(message.get(Message.ENCODING)));
                    } catch (Exception e){
                        MDC.put("messageParseError", e.getMessage());
                    } finally {
                        log.info("Incoming request");
                        MDC.clear();
                    }
                }
            }
        };
    }
}
