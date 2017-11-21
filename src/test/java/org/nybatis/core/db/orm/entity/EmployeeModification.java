package org.nybatis.core.db.orm.entity;

import java.sql.Types;
import org.nybatis.core.db.annotation.Column;
import org.nybatis.core.db.annotation.Pk;
import org.nybatis.core.db.sql.mapper.SqlType;

/**
 * Table Modification
 *
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class EmployeeModification extends Employee {

    @Pk
    @Column( defaultValue = "1" )
    private String subKey;

    public String getSubKey() {
        return subKey;
    }

    public void setSubKey( String subKey ) {
        this.subKey = subKey;
    }

    @Column( length = 21, precision = 4, notNull = true, type = Types.VARCHAR )
    public double getIncome() {
        return super.getIncome();
    }

}
