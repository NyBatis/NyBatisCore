package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.nybatis.core.db.constant.NullValue;

import java.io.IOException;

public class ArraySerializer extends AbstractJsonSerializer<Object> {

    public ArraySerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( Object value ) throws IOException {
        return
                value == NullValue.ARRAY_STRING ||
                value == NullValue.ARRAY_BOOLEAN ||
                value == NullValue.ARRAY_INTEGER ||
                value == NullValue.ARRAY_LONG ||
                value == NullValue.ARRAY_FLOAT ||
                value == NullValue.ARRAY_STRING ||
                value == NullValue.ARRAY_DOUBLE ||
                value == NullValue.ARRAY_BIG_DECIMAL;
    }

}