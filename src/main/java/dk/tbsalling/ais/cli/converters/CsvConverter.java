package dk.tbsalling.ais.cli.converters;

import dk.tbsalling.aismessages.AISInputStreamReader;
import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.ais.messages.BaseStationReport;
import dk.tbsalling.aismessages.ais.messages.DynamicDataReport;
import dk.tbsalling.aismessages.ais.messages.ShipAndVoyageData;
import dk.tbsalling.aismessages.ais.messages.StaticDataReport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;

public class CsvConverter implements Converter {

    public CsvConverter() {
    }

    @Override
    public void convert(InputStream in, OutputStream out) {
        if (!(out instanceof PrintStream))
            out = new PrintStream(out);

        try (CSVPrinter csvPrinter = CSVFormat.DEFAULT.print((PrintStream) out)) {
            csvPrinter.printRecord(headers());

            if (!(in instanceof BufferedInputStream))
                in = new BufferedInputStream(in);

            AISInputStreamReader streamReader = new AISInputStreamReader(
                in,
                ais -> {
                    try {
                        csvPrinter.printRecord(toCsvRecord(ais));
                    } catch (IOException e) {
                        System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
            );

            streamReader.run();
        } catch (IOException e) {
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static Iterable<String> headers() {
        List headers = new ArrayList();

        headers.add("received");
        headers.add("sourceMmsi");
        headers.add("digest");
        headers.add("msgtype");
        headers.add("valid");

        headers.add("lat");
        headers.add("lng");

        headers.add("cog");
        headers.add("sog");
        headers.add("cls");

        headers.add("shipname");
        headers.add("callsigns");
        headers.add("shiptype");
        headers.add("bow");
        headers.add("stern");
        headers.add("port");
        headers.add("starboard");

        headers.add("destination");
        headers.add("draught");
        headers.add("eta");
        headers.add("imo");
        headers.add("dev");

        headers.add("day");
        headers.add("month");
        headers.add("year");
        headers.add("hour");
        headers.add("minute");
        headers.add("second");

        return headers;
    }

    private static Iterable<?> toCsvRecord(AISMessage ais) {
        List<Object> fields = new ArrayList<>(28);

        fields.add(ais.getMetadata().received());
        fields.add(ais.getSourceMmsi().getMmsi());
        try {
            fields.add(HexFormat.of().withUpperCase().formatHex(ais.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        fields.add(ais.getMessageType().getCode());
        fields.add(true); // Since aismessages 4.1.0, invalid messages are rejected during decoding

        if (ais instanceof DynamicDataReport ddr) {
            fields.add(ddr.getLatitude());
            fields.add(ddr.getLongitude());
        } else if (ais instanceof BaseStationReport bsr) {
            fields.add(bsr.getLatitude());
            fields.add(bsr.getLongitude());
        } else {
            addNulls(fields, 2);
        }

        if (ais instanceof DynamicDataReport ddr) {
            fields.add(ddr.getCourseOverGround());
            fields.add(ddr.getSpeedOverGround());
            fields.add(ddr.getTransponderClass().getValue());
        } else {
            addNulls(fields, 3);
        }

        if (ais instanceof StaticDataReport sdr) {
            fields.add(sdr.getShipName());
            fields.add(sdr.getCallsign());
            fields.add(sdr.getShipType() != null ? sdr.getShipType().getValue() : null);
            fields.add(sdr.getToBow());
            fields.add(sdr.getToStern());
            fields.add(sdr.getToPort());
            fields.add(sdr.getToStarboard());
        } else {
            addNulls(fields, 7);
        }

        if (ais instanceof ShipAndVoyageData svd) {
            fields.add(svd.getDestination());
            fields.add(svd.getDraught());
            fields.add(svd.getEtaAfterReceived().orElse(null));
            fields.add(svd.getImo().getImo());
            fields.add(svd.getPositionFixingDevice() != null ? svd.getPositionFixingDevice().getValue() : null);
        } else {
            addNulls(fields, 5);
        }

        if (ais instanceof BaseStationReport bsr) {
            fields.add(bsr.getDay());
            fields.add(bsr.getMonth());
            fields.add(bsr.getYear());
            fields.add(bsr.getHour());
            fields.add(bsr.getMinute());
            fields.add(bsr.getSecond());
        } else {
            addNulls(fields, 6);
        }

        if (fields.size() != 28)
            throw new RuntimeException("Internal application error: fields.size=" + fields.size());

        return fields;
    }

    private static void addNulls(List<Object> fields, int n) {
        fields.addAll(Collections.nCopies(n, null));
    }

}
