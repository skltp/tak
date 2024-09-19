package se.skltp.tak.services;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
            throw new RuntimeException(e);
        }
        if (msgBuffer != null) {
            message.setContent(InputStream.class, new java.io.ByteArrayInputStream(msgBuffer));
            String incomingMessage = new String(msgBuffer);
            String contentType = (String) message.get(Message.CONTENT_TYPE);
            MDC.put("payload", incomingMessage);
            MDC.put("contentType", contentType);

            Map<String, String> logFields = Map.of("event", "incoming_request",
                    "payload", incomingMessage,
                    "content_type", contentType);

            log.info("Incoming Request", logFields);

        }
    }
}
