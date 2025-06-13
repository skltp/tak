package se.skltp.tak.web.exception;

public class CustomSSLConfigurationException extends RuntimeException {
    public CustomSSLConfigurationException(String message) {
        super(message);
    }

    public CustomSSLConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
