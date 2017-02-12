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

import java.util.logging.LogManager;

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

        if (cmdLine.hasOption(OPTION_VERBOSE)) {
            verbose = true;
        } else {
            LogManager.getLogManager().reset();
        }

        if (cmdLine.hasOption(OPTION_OUTPUT)) {
            String format = cmdLine.getOptionValue(OPTION_OUTPUT);
            switch (format) {
                case "csv":
                    new CsvConverter().convert(System.in, System.out);
                    break;
                case "json":
                    new JsonConverter().convert(System.in, System.out);
                    break;
                default:
                    System.err.println("Unknown output format: " + format);
                    System.exit(-1);
            }
        }
    }

}


