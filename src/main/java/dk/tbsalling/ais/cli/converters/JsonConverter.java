package dk.tbsalling.ais.cli.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.tbsalling.aismessages.AISInputStreamReader;
import dk.tbsalling.aismessages.ais.BitString;

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
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // BitString has no bean properties; serialize it as its legacy '0'/'1' string representation
        mapper.registerModule(new SimpleModule().addSerializer(BitString.class, ToStringSerializer.instance));
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
