package se.skltp.tak.web.jsonBestallning

class JsonUtils {

    static String cleanupString(String input) {
        input = input.trim().replaceAll("\n", " ").replaceAll("\r", "");
        return input
    }
}
