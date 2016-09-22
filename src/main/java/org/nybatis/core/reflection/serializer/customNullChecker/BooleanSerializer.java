package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.nybatis.core.db.constant.NullValue;

import java.io.IOException;

public class BooleanSerializer extends AbstractJsonSerializer<Boolean> {

    public BooleanSerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( Boolean value ) throws IOException {
        return value == NullValue.BOOLEAN;
    }

}
