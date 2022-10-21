package dk.tbsalling.ais.cli;

import dk.tbsalling.ais.cli.converters.CsvConverter;
import dk.tbsalling.ais.cli.converters.JsonConverter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.LogManager;

public class AisCli {


    public static final String OPTION_FROMFILE = "inputfile";
    public static final String OPTION_FROMFILE_ABBR = "i";
    public static final String OPTION_FROMFILE_NAME = "file";
    public static final String OPTION_OUTPUT = "output";
    public static final String OPTION_OUTPUT_ABBR = "o";
    public static final String OPTION_OUTPUT_FORMAT = "format";
    public static final String OPTION_VERBOSE = "verbose";
    public static final String OPTION_VERBOSE_ABBR = "v";

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
                .desc("Output received messages on stdout in given format ('csv', 'json').")
                .build()
        );
        OPTIONS.addOption(
                Option.builder(OPTION_FROMFILE_ABBR).longOpt(OPTION_FROMFILE)
                        .hasArg().argName(OPTION_FROMFILE_NAME).required()
                        .desc("Read NMEA input from given file instead of stdin")
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

        if (cmdLine.hasOption(OPTION_VERBOSE)) {
            verbose = true;
        } else {
            LogManager.getLogManager().reset();
        }

        InputStream inputStream = System.in;
        if (cmdLine.hasOption(OPTION_FROMFILE)) {
            try {
                inputStream = new BufferedInputStream(Files.newInputStream(Path.of(cmdLine.getOptionValue(OPTION_FROMFILE))));
            } catch (IOException e) {
                System.err.println("Failed to open input file: " + e.getMessage());
                System.exit(-1);
            }
        }

        if (cmdLine.hasOption(OPTION_OUTPUT)) {
            String format = cmdLine.getOptionValue(OPTION_OUTPUT);
            switch (format) {
                case "csv":
                    new CsvConverter().convert(inputStream, System.out);
                    break;
                case "json":
                    new JsonConverter().convert(inputStream, System.out);
                    break;
                default:
                    System.err.println("Unknown output format: " + format);
                    System.exit(-1);
            }
        }
    }

}


