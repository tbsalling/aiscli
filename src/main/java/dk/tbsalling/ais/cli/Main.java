package tbsalling.ais.cli;

import dk.tbsalling.aismessages.AISInputStreamReader;
import dk.tbsalling.aismessages.ais.messages.AISMessage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
            BufferedInputStream inputStream = new BufferedInputStream(System.in);

            AISInputStreamReader streamReader = new AISInputStreamReader(
                inputStream,
                aisMessage -> System.out.println(aisMessageToCsv(aisMessage))
            );

        try {
            streamReader.run();
        } catch (IOException e) {
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    };

    private static String aisMessageToCsv(AISMessage ais) {
        return ais.toString();
    }
}


