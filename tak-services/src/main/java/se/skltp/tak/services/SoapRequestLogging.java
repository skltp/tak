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
                String pre = "incoming.message.data";
                MDC.clear();
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
}
