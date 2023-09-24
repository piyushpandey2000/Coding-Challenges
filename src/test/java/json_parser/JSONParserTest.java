package json_parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JSONParserTest {

    String[] validJson = {
        // step1
        "{}",

        // step2.1
        "{\"key\": \"value\"}",

        // step2.2
        """
            {
                "key": "value",
                "key2": "value"
            }""",

        // step3
        """
            {
                "key1": true,
                "key2": false,
                "key3": null,
                "key4": "value",
                "key5": 101
            }""",

        // step4.1
        """
            {
                "key": "value",
                "key-n": 101,
                "key-o": {},
                "key-l": []
            }""",

        // step4.2
        """
            {
                "key": "value",
                "key-n": 101,
                "key-o": {
                    "inner key": "inner value"
                },
                "key-l": ["list value"]
            }"""
    };

    String[] invalidJson = {
        // step1
        "",

        // step2.1
        "{\"key\": \"value\",}",

        // step2.2
        """
            {
                "key": "value",
                key2: "value"
            }""",

        // step3
        """
            {
                "key1": true,
                "key2": False,
                "key3": null,
                "key4": "value",
                "key5": 101
            }""",

        // step4
        """
            {
                "key": "value",
                "key-n": 101,
                "key-o": {
                    "inner key": "inner value"
                },
                "key-l": ['list value']
            }"""
    };

    @Test
    void testValid() {
        for (String json : validJson) {
            assertTrue(JSONParser.getInstance().parse(json));
        }
    }

    @Test
    void testInvalid() {
        for (String json : invalidJson) {
            assertFalse(JSONParser.getInstance().parse(json));
        }
    }
}
