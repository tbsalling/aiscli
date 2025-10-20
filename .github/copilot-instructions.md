# GitHub Copilot Instructions for AISCli

## Project Overview
AISCli is a command-line utility for parsing, filtering, and manipulating NMEA-armoured AIS (Automatic Identification System) messages. It converts raw AIS messages into more manageable formats like JSON or CSV.

## Technology Stack
- **Language**: Java 21
- **Build Tool**: Maven
- **Key Dependency**: aismessages v4.1.0
- **Output Formats**: JSON, CSV

## Project Structure
```
aiscli/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── dk/tbsalling/ais/cli/
│   │           ├── AisCli.java           # Main entry point
│   │           └── converters/
│   │               ├── Converter.java     # Interface for converters
│   │               ├── CsvConverter.java  # CSV output converter
│   │               └── JsonConverter.java # JSON output converter
│   └── test/
│       └── resources/
│           └── ais-sample.nmea           # Sample AIS data
├── pom.xml                               # Maven configuration
└── README.md                             # Project documentation
```

## Build and Test
- **Build**: `mvn clean compile`
- **Test**: `mvn test`
- **Package**: `mvn package`
- **Run**: `java -jar target/aiscli-1.0-SNAPSHOT-jar-with-dependencies.jar -o <format>`

Note: Java 21 is required. Set `JAVA_HOME` to Java 21 before building.

## Code Style and Conventions
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Keep methods focused and concise
- Document complex logic with comments
- Handle exceptions appropriately

## Important Notes
- The project uses aismessages v4.1.0, which introduced significant API changes from v3.x:
  - Many message types are now interfaces (e.g., `DynamicDataReport`, `StaticDataReport`)
  - Method names follow strict camelCase (e.g., `getMmsi()` not `getMMSI()`)
  - Metadata class is now a Record with accessor methods (e.g., `received()` not `getReceived()`)
  - Some methods return `Optional` types (e.g., `getEtaAfterReceived()`)
- The project requires Java 21 due to the aismessages dependency

## Key Classes and Interfaces
- **AISMessage**: Base class for all AIS messages
- **DynamicDataReport**: Interface for position reports with speed/course
- **StaticDataReport**: Interface for ship static data (name, type, dimensions)
- **BaseStationReport**: Class for base station reports
- **ShipAndVoyageData**: Class for ship and voyage information
- **AISInputStreamReader**: Reads and parses NMEA-armoured AIS messages

## Adding New Features
When adding new converters or output formats:
1. Implement the `Converter` interface
2. Handle AIS message types appropriately using `instanceof` checks
3. Use the metadata accessor methods from the message objects
4. Consider the different message types and their specific fields
5. Add appropriate error handling

## Testing
- Test files are located in `src/test/resources/`
- Use `ais-sample.nmea` for testing with real AIS data
- Test both CSV and JSON output formats
- Verify output against expected formats

## Common Issues
- **Wrong Java Version**: Ensure Java 21 is installed and `JAVA_HOME` is set correctly
- **Dependency Issues**: Run `mvn clean` if you encounter unexpected compilation errors
- **API Changes**: When upgrading aismessages, check for API changes in the release notes

## License
This project is free for non-commercial use. See the LICENSE file for details.
