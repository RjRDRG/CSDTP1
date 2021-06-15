package com.fct.csd.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import static com.fct.csd.common.util.Serialization.stringToBytes;

public class ByteDeserializer extends StdDeserializer<byte[]> {

    public ByteDeserializer() {
        this(null);
    }

    public ByteDeserializer(Class<byte[]> t) {
        super(t);
    }

    @Override
    public byte[] deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return stringToBytes(node.asText());
    }
}