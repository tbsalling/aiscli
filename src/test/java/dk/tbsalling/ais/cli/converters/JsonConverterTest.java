package dk.tbsalling.ais.cli.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonConverterTest {

    @Test
    void convertsEveryMessageInSampleFileToJson() throws IOException {
        List<JsonNode> messages = convertSample();

        assertEquals(4, messages.size());
        assertEquals(List.of(412432822, 565046000, 4132801, 413044610),
                messages.stream().map(m -> m.at("/sourceMmsi/mmsi").asInt()).toList());
        assertEquals(List.of("ExtendedClassBEquipmentPositionReport", "PositionReportClassAScheduled",
                        "BaseStationReport", "ShipAndVoyageRelatedData"),
                messages.stream().map(m -> m.get("messageType").asText()).toList());
    }

    @Test
    void serializesBitStringAsBinaryDigits() throws IOException {
        for (JsonNode message : convertSample()) {
            String bitString = message.at("/metadata/bitString").asText();
            assertTrue(bitString.matches("[01]+"), bitString);
        }
    }

    @Test
    void serializesReceivedTimestampAsIsoText() throws IOException {
        for (JsonNode message : convertSample()) {
            JsonNode received = message.at("/metadata/received");
            assertTrue(received.isTextual(), received.toString());
            Instant.parse(received.asText());
        }
    }

    private static List<JsonNode> convertSample() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream in = JsonConverterTest.class.getResourceAsStream("/ais-sample.nmea")) {
            new JsonConverter().convert(in, out);
        }
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> messages = new ArrayList<>();
        for (String line : out.toString(StandardCharsets.UTF_8).split("\n"))
            messages.add(mapper.readTree(line));
        return messages;
    }

}
