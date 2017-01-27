package dk.tbsalling.ais.cli;

import com.google.gson.Gson;
import dk.tbsalling.aismessages.AISInputStreamReader;
import dk.tbsalling.aismessages.ais.messages.AISMessage;

import java.io.BufferedInputStream;
import java.io.IOException;

public class Main {

    private final static Gson GSON = new Gson();

    public static void main(String[] args) {
            BufferedInputStream inputStream = new BufferedInputStream(System.in);

            AISInputStreamReader streamReader = new AISInputStreamReader(
                inputStream,
                ais -> System.out.println(GSON.toJson(ais))
            );

        try {
            streamReader.run();
        } catch (IOException e) {
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

}


