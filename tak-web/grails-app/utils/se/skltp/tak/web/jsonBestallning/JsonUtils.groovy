package se.skltp.tak.web.jsonBestallning

class JsonUtils {

    static String cleanupString(String input) {
        if (input != null) {
            input = input.trim().replaceAll("\n", " ").replaceAll("\r", "");
            /*if (input.length() > 255) {
                input = input.substring(0, 254);
            }*/
            return input
        }
        else return "";
    }
}
