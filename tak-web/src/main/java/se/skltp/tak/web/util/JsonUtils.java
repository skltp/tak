/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
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
