package org.nybatis.core.db.constant;

import org.nybatis.core.model.NDate;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Null Value for orm mapper
 *
 * @author nayasis@gmail.com
 * @since 2016-09-21
 */
public class NullValue {

    public static final String       STRING            = "nybatis.nullValue";
    public static final Boolean      BOOLEAN           = new Boolean( false );
    public static final Integer      INTEGER           = new Integer( 0 );
    public static final Long         LONG              = new Long( 0 );
    public static final Float        FLOAT             = new Float( 0 );
    public static final Double       DOUBLE            = new Double( 0 );
    public static final BigDecimal   BIG_DECIMAL       = new BigDecimal( 0 );
    public static final String[]     ARRAY_STRING      = new String[]     {};
    public static final Boolean[]    ARRAY_BOOLEAN     = new Boolean[]    {};
    public static final Integer[]    ARRAY_INTEGER     = new Integer[]    {};
    public static final Long[]       ARRAY_LONG        = new Long[]       {};
    public static final Float[]      ARRAY_FLOAT       = new Float[]      {};
    public static final Double[]     ARRAY_DOUBLE      = new Double[]     {};
    public static final BigDecimal[] ARRAY_BIG_DECIMAL = new BigDecimal[] {};
    public static final NDate        NDATE             = new NDate( "0001-01-01" );
    public static final Date         DATE              = new NDate( "0001-01-01" ).toDate();
    public static final List         LIST              = new ArrayList<>();
    public static final NList        NLIST             = new NList();
    public static final Set          SET               = new HashSet<>();
    public static final Map          MAP               = new HashMap();
    public static final NMap         NMAP              = new NMap();

    public static boolean isNull( Object value ) {
        if( value == null ) return false;
        return
            value == NullValue.STRING            ||
            value == NullValue.BOOLEAN           ||
            value == NullValue.INTEGER           ||
            value == NullValue.LONG              ||
            value == NullValue.FLOAT             ||
            value == NullValue.DOUBLE            ||
            value == NullValue.BIG_DECIMAL       ||
            value == NullValue.ARRAY_STRING      ||
            value == NullValue.ARRAY_BOOLEAN     ||
            value == NullValue.ARRAY_INTEGER     ||
            value == NullValue.ARRAY_LONG        ||
            value == NullValue.ARRAY_FLOAT       ||
            value == NullValue.ARRAY_DOUBLE      ||
            value == NullValue.ARRAY_BIG_DECIMAL ||
            value == NullValue.NDATE             ||
            value == NullValue.DATE              ||
            value == NullValue.LIST              ||
            value == NullValue.NLIST             ||
            value == NullValue.SET               ||
            value == NullValue.MAP               ||
            value == NullValue.NMAP
            ;
    }

}