package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.databind.JsonSerializer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.nybatis.core.db.constant.NullValue;

public class MapSerializer extends AbstractJsonSerializer<Map> {

    public MapSerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( Map value ) throws IOException {
        return value == NullValue.MAP;
    }

}
