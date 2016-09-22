package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.nybatis.core.db.constant.NullValue;

import java.io.IOException;

public class DoubleSerializer extends AbstractJsonSerializer<Double> {

    public DoubleSerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( Double value ) throws IOException {
        return value == NullValue.DOUBLE;
    }

}
