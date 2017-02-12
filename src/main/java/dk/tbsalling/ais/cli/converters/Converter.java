package dk.tbsalling.ais.cli.converters;

import java.io.InputStream;
import java.io.OutputStream;

public interface Converter {
    void convert(InputStream in, OutputStream out);
}
