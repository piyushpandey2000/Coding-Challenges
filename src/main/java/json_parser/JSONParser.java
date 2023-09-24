package json_parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONParser {

    private JSONParser() {}

    private static final Logger logger = LoggerFactory.getLogger(JSONParser.class);
    private static final JSONParser instance = new JSONParser();

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String NULL = "null";

    public static JSONParser getInstance() {
        return instance;
    }

    public boolean parse(String jsonString) {
        int idx = 0;
        try {
            idx = parseValue(jsonString, idx);
        } catch (JSONParseException e) {
            logger.info("Could not parse json: {}", jsonString, e);
            return false;
        }

        return jsonString.length() != 0 && idx == jsonString.length();
    }

    private int parseKey(String str, int idx) throws JSONParseException {

        // skipping spaces and newline
        idx = skipSpaces(str, idx);

        if (str.charAt(idx) != '"') {
            throw new JSONParseException(
                    String.format("opening double-quotes expected at char %d. Found %c", idx, str.charAt(idx)));
        }
        // skipping first double-quote
        idx++;

        while (str.charAt(idx) != '"') {
            idx++;
        }

        return ++idx;
    }

    private int parseColon(String str, int idx) throws JSONParseException {
        idx = skipSpaces(str, idx);

        if (str.charAt(idx) != ':') {
            throw new JSONParseException(String.format("colon expected at char %d. Found %c", idx, str.charAt(idx)));
        }
        idx++;

        idx = skipSpaces(str, idx);

        return idx;
    }

    private int parseValue(String str, int idx) throws JSONParseException {
        idx = skipSpaces(str, idx);

        if (idx < str.length()) {
            if (str.charAt(idx) == '{') {
                idx = parseObject(str, idx);
            } else if (str.charAt(idx) == '[') {
                idx = parseArray(str, idx);
            } else if (str.charAt(idx) == '"') {
                while (str.charAt(++idx) != '"')
                    ;
                idx++;
            } else if (idx + 3 < str.length()
                    && (str.regionMatches(idx, TRUE, 0, TRUE.length())
                            || str.regionMatches(idx, NULL, 0, NULL.length()))) {
                idx = idx + 4;
            } else if (idx + 4 < str.length() && str.regionMatches(idx, FALSE, 0, FALSE.length())) {
                idx = idx + 5;
            } else {
                while (isDigit(str.charAt(idx))) {
                    idx++;
                }
            }
        }

        return idx;
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private int parseKeyValue(String str, int idx, boolean isFirstVal) throws JSONParseException {
        if (isFirstVal && str.charAt(idx) == '}') {
            return idx;
        }

        idx = parseKey(str, idx);
        idx = parseColon(str, idx);
        idx = parseValue(str, idx);

        idx = skipSpaces(str, idx);

        switch (str.charAt(idx)) {
            case ',' -> {
                idx++;
                return parseKeyValue(str, idx, false);
            }
            case '}' -> {
                return idx;
            }
            default -> throw new JSONParseException(String.format(
                    "comma ',' or closing braces '}' expected after key-value pair at char %d. Found %c",
                    idx, str.charAt(idx)));
        }
    }

    private int parseObject(String str, int idx) throws JSONParseException {
        idx = skipSpaces(str, idx);

        if (str.charAt(idx) != '{') {
            throw new JSONParseException(
                    String.format("opening braces '{' expected at char %d. Found %c", idx, str.charAt(idx)));
        }
        idx++;

        idx = parseKeyValue(str, idx, true);
        return ++idx;
    }

    private int parseArrayValue(String str, int idx) throws JSONParseException {
        idx = skipSpaces(str, idx);

        idx = parseValue(str, idx);

        idx = skipSpaces(str, idx);

        switch (str.charAt(idx)) {
            case ',' -> {
                idx++;
                return parseArrayValue(str, idx);
            }
            case ']' -> {
                return idx;
            }
            default -> throw new JSONParseException(String.format(
                    "comma ',' or closing braces ']' expected after array element at char %d. Found %c",
                    idx, str.charAt(idx)));
        }
    }

    private int parseArray(String str, int idx) throws JSONParseException {
        idx = skipSpaces(str, idx);

        if (str.charAt(idx) != '[') {
            throw new JSONParseException(
                    String.format("opening braces '[' expected at char %d. Found %c", idx, str.charAt(idx)));
        }
        idx++;

        idx = parseArrayValue(str, idx);
        return ++idx;
    }

    private int skipSpaces(String str, int idx) {
        while (idx < str.length() && (str.charAt(idx) == ' ' || str.charAt(idx) == '\n')) {
            idx++;
        }
        return idx;
    }
}
