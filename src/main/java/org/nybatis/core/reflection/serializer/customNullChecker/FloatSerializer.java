package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.nybatis.core.db.constant.NullValue;

import java.io.IOException;

public class FloatSerializer extends AbstractJsonSerializer<Float> {

    public FloatSerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( Float value ) throws IOException {
        return value == NullValue.FLOAT;
    }

}
