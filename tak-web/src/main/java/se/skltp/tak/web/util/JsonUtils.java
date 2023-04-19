package se.skltp.tak.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    public static String cleanupString(String input) {
        if (input != null) {
            input = input.trim().replaceAll("\n", " ").replaceAll("\r", "");
            if (input.length() > 255) {
                input = input.substring(0, 254);
                log.warn("A string exceeding the maximum length (255) has been truncated. String begins with: " + input);
            }
            return input;
        } else return "";
    }
}
