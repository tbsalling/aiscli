package dk.tbsalling.ais.cli.converters;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvConverterTest {

    @Test
    void convertsSampleFileToCsv() throws IOException {
        try (CSVParser parser = parseSample()) {
            assertEquals(List.of("received", "sourceMmsi", "digest", "msgtype", "valid", "lat", "lng", "cog", "sog", "cls",
                    "shipname", "callsigns", "shiptype", "bow", "stern", "port", "starboard", "destination", "draught", "eta",
                    "imo", "dev", "day", "month", "year", "hour", "minute", "second"), parser.getHeaderNames());

            List<CSVRecord> records = parser.getRecords();
            assertEquals(4, records.size());
            records.forEach(r -> assertEquals(28, r.size()));
        }
    }

    @Test
    void convertsBaseStationReport() throws IOException {
        CSVRecord record = convertSample().get(2);

        assertEquals("4132801", record.get("sourceMmsi"));
        assertEquals("A8F19241A6BA2700E6495F440BCC7D5C78654836", record.get("digest"));
        assertEquals("4", record.get("msgtype"));
        assertEquals("true", record.get("valid"));
        assertEquals("25.224487", record.get("lat"));
        assertEquals("118.98596", record.get("lng"));
        assertEquals(List.of("14", "3", "2012", "11", "30", "14"),
                List.of(record.get("day"), record.get("month"), record.get("year"),
                        record.get("hour"), record.get("minute"), record.get("second")));
    }

    @Test
    void convertsShipAndVoyageData() throws IOException {
        CSVRecord record = convertSample().get(3);

        assertEquals("413044610", record.get("sourceMmsi"));
        assertEquals("5", record.get("msgtype"));
        assertEquals("HAIXUN 1010", record.get("shipname"));
        assertEquals("NotAvailable", record.get("shiptype"));
        assertEquals("Gps", record.get("dev"));
        assertEquals("", record.get("lat"));
    }

    @Test
    void convertsPositionReport() throws IOException {
        CSVRecord record = convertSample().get(1);

        assertEquals("565046000", record.get("sourceMmsi"));
        assertEquals("1", record.get("msgtype"));
        assertEquals("51.372234", record.get("lat"));
        assertEquals("2.8492534", record.get("lng"));
        assertEquals("256.7", record.get("cog"));
        assertEquals("12.1", record.get("sog"));
        assertEquals("A", record.get("cls"));
    }

    private static List<CSVRecord> convertSample() throws IOException {
        try (CSVParser parser = parseSample()) {
            return parser.getRecords();
        }
    }

    private static CSVParser parseSample() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream in = CsvConverterTest.class.getResourceAsStream("/ais-sample.nmea")) {
            new CsvConverter().convert(in, out);
        }
        return CSVFormat.DEFAULT.builder().setHeader().get().parse(new StringReader(out.toString(StandardCharsets.UTF_8)));
    }

}
