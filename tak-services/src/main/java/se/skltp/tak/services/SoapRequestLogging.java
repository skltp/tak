package se.skltp.tak.services;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.InputStream;

public class SoapRequestLogging extends AbstractPhaseInterceptor<Message> {


    private static final Logger log = LoggerFactory.getLogger(SoapRequestLogging.class);

    public SoapRequestLogging() {
        super(Phase.RECEIVE);
    }

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
            try {
                MDC.clear();
                MDC.put("payload", new String(msgBuffer));
                MDC.put("headers", String.valueOf(message.get(Message.PROTOCOL_HEADERS)));
                MDC.put("method", String.valueOf(message.get(Message.HTTP_REQUEST_METHOD)));
                MDC.put("queryString", String.valueOf(message.get(Message.QUERY_STRING)));
                MDC.put("acceptContentType", String.valueOf(message.get(Message.ACCEPT_CONTENT_TYPE)));
                MDC.put("encoding", String.valueOf(message.get(Message.ENCODING)));
            } catch (Exception e){
                MDC.put("error message", e.getMessage());
            } finally {
                log.info("Incoming request");
                MDC.clear();
            }

        }
    }
}
