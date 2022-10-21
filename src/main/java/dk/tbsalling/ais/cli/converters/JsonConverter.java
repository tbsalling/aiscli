package dk.tbsalling.ais.cli.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.tbsalling.aismessages.AISInputStreamReader;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class JsonConverter implements Converter {

    public JsonConverter() {
    }

    @Override
    public void convert(InputStream in, OutputStream out) {
        final PrintStream output = out instanceof PrintStream ? (PrintStream) out : new PrintStream(out);
        final BufferedInputStream input = in instanceof BufferedInputStream ? (BufferedInputStream) in : new BufferedInputStream(in);

        final ObjectMapper mapper = new ObjectMapper();
        AISInputStreamReader streamReader = new AISInputStreamReader(
                input,
                ais -> {
                    try {
                        output.println(mapper.writeValueAsString(ais));
                    } catch (JsonProcessingException e) {
                        System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
        );

        streamReader.run();
    }

}
