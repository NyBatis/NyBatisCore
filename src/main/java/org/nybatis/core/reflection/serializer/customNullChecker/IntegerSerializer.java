package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.nybatis.core.db.constant.NullValue;

import java.io.IOException;

public class IntegerSerializer extends AbstractJsonSerializer<Integer> {

    public IntegerSerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( Integer value ) throws IOException {
        return value == NullValue.INTEGER;
    }

}
