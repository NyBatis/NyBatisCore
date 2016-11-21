package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.nybatis.core.db.constant.NullValue;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalSerializer extends AbstractJsonSerializer<BigDecimal> {

    public BigDecimalSerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( BigDecimal value ) throws IOException {
        return value == NullValue.BIG_DECIMAL;
    }

}
