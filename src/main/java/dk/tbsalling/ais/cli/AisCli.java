package dk.tbsalling.ais.cli;

import dk.tbsalling.aismessages.AISInputStreamReader;
import dk.tbsalling.aismessages.ais.messages.*;
import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class AisCli {

    public static final String OPTION_VERBOSE = "verbose";
    public static final String OPTION_VERBOSE_ABBR = "v";
    public static final String OPTION_OUTPUT = "output";
    public static final String OPTION_OUTPUT_ABBR = "o";
    public static final String OPTION_OUTPUT_FORMAT = "format";

    public static boolean verbose = false;

    private static final Options OPTIONS = new Options();

    public static void main(String[] args) {
        addOptions();

        if (args.length <= 1) {
            help();
            System.exit(-1);
        }

        try {
            parseOptions(args);
            System.exit(0);
        } catch (ParseException e) {
            System.err.println("Command line parsing failed: " + e.getMessage());
            help();
            System.exit(-1);
        }
    }

    private static void addOptions() {
        OPTIONS.addOption(
            Option.builder(OPTION_VERBOSE_ABBR).longOpt(OPTION_VERBOSE)
                .desc("Produce verbose output.")
                .build()
        );
        OPTIONS.addOption(
            Option.builder(OPTION_OUTPUT_ABBR).longOpt(OPTION_OUTPUT)
                .hasArg().argName(OPTION_OUTPUT_FORMAT).required()
                .desc("Output received messages on stdout in given format ('csv').")
                .build()
        );
    }

    private static void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "java -jar aiscli.jar", OPTIONS, true );
    }

    private static void parseOptions(String[] args) throws ParseException {
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmdLine = commandLineParser.parse(OPTIONS, args);

        if (cmdLine.hasOption(OPTION_VERBOSE))
            verbose = true;

        if (cmdLine.hasOption(OPTION_OUTPUT)) {
            String format = cmdLine.getOptionValue(OPTION_OUTPUT);
            switch (format) {
                case "csv":
                    ais2csv(System.in, System.out);
                    break;
                default:
                    System.err.println("Unknown output format: " + format);
                    System.exit(-1);
            }
        }
    }

    private static void ais2csv(InputStream in, PrintStream out) {
        try (CSVPrinter csvPrinter = CSVFormat.DEFAULT.print(out)) {
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
        List fields = new ArrayList();

        try {
            fields.add(ais.getMetadata().getReceived());
            fields.add(ais.getSourceMmsi().getMMSI());
            fields.add(bytesToHex(ais.digest()));
            fields.add(ais.getMessageType().getCode());
            fields.add(ais.isValid());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        if (ais instanceof DynamicDataReport || ais instanceof BaseStationReport) {
            if (ais instanceof DynamicDataReport) {
                DynamicDataReport ddr = (DynamicDataReport) ais;
                fields.add(ddr.getLatitude());
                fields.add(ddr.getLongitude());
            } else if (ais instanceof BaseStationReport) {
                BaseStationReport bsr = (BaseStationReport) ais;
                fields.add(bsr.getLatitude());
                fields.add(bsr.getLongitude());
            }
        } else {
            fields.add(null);
            fields.add(null);
        }

        if (ais instanceof DynamicDataReport) {
            DynamicDataReport ddr = (DynamicDataReport) ais;
            fields.add(ddr.getCourseOverGround());
            fields.add(ddr.getSpeedOverGround());
            fields.add(ddr.getTransponderClass().getValue());
        } else {
            fields.add(null);
            fields.add(null);
            fields.add(null);
        }

        if (ais instanceof StaticDataReport) {
            StaticDataReport sdr = (StaticDataReport) ais;
            fields.add(sdr.getShipName());
            fields.add(sdr.getCallsign());
            fields.add(sdr.getShipType().getValue());
            fields.add(sdr.getToBow());
            fields.add(sdr.getToStern());
            fields.add(sdr.getToPort());
            fields.add(sdr.getToStarboard());
        } else {
            fields.add(null);
            fields.add(null);
            fields.add(null);
            fields.add(null);
            fields.add(null);
            fields.add(null);
            fields.add(null);
        }

        if (ais instanceof ShipAndVoyageData) {
            ShipAndVoyageData svd = (ShipAndVoyageData) ais;
            fields.add(svd.getDestination());
            fields.add(svd.getDraught());
            fields.add(svd.getEta());
            fields.add(svd.getImo().getIMO());
            fields.add(svd.getPositionFixingDevice() != null ? svd.getPositionFixingDevice().getValue() : null);
        } else {
            fields.add(null);
            fields.add(null);
            fields.add(null);
            fields.add(null);
            fields.add(null);
        }

        if (ais instanceof BaseStationReport) {
            BaseStationReport bsr = (BaseStationReport) ais;
            fields.add(bsr.getDay());
            fields.add(bsr.getMonth());
            fields.add(bsr.getYear());
            fields.add(bsr.getHour());
            fields.add(bsr.getMinute());
            fields.add(bsr.getSecond());
        } else {
            fields.add(null);
            fields.add(null);
            fields.add(null);
            fields.add(null);
            fields.add(null);
            fields.add(null);
        }

        if (fields.size() != 28)
            throw new RuntimeException("Internal application error: fields.size=" + fields.size());

        return fields;
    }

    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}


