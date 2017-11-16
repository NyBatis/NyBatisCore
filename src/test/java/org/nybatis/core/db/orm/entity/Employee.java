package org.nybatis.core.db.orm.entity;

import java.sql.Types;
import org.nybatis.core.db.annotation.Column;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class Employee extends Persion {

    public Employee() {}

    public Employee( String key, String name, int age, double income, String id, String dummy, String department ) {
        super( key, name, age, income, id, dummy );
        this.department = department;
    }

    @Column( type = Types.CLOB )
    private String department;

    public String getDepartment() {
        return department;
    }

    public void setDepartment( String department ) {
        this.department = department;
    }

    @Column( length = 21, precision = 4, notNull = true )
    public double getIncome() {
        return super.getIncome();
    }
}
