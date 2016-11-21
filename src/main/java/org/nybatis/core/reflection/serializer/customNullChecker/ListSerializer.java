package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.nybatis.core.db.constant.NullValue;

import java.io.IOException;
import java.util.List;

public class ListSerializer extends AbstractJsonSerializer<List> {

    public ListSerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( List value ) throws IOException {
        return value == NullValue.LIST;
    }

}
