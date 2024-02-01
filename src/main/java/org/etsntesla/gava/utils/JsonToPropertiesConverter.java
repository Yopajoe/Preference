package org.etsntesla.gava.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class JsonToPropertiesConverter {
    public static Properties convertJsonToProperties(String json) throws IOException {
        // Create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Read JSON and convert to JsonNode
        JsonNode jsonNode = objectMapper.readTree(json);

        // Create AppProperties instance
        Properties properties = new Properties();

        // Iterate over JSON fields
        Iterator<String> fieldIterator = jsonNode.fieldNames();
        while (fieldIterator.hasNext()) {
            String fieldName = fieldIterator.next();
            String value = jsonNode.get(fieldName).asText();
            properties.setProperty(fieldName, value);
        }

        return properties;
    }
}