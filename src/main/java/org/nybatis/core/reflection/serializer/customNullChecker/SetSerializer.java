package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.nybatis.core.db.constant.NullValue;

import java.io.IOException;
import java.util.Set;

public class SetSerializer extends AbstractJsonSerializer<Set> {

    public SetSerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( Set value ) throws IOException {
        return value == NullValue.SET;
    }

}
