package org.nybatis.core.reflection.core.testClass;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class Employee extends Persion {

    private String department;

    public String getDepartment() {
        return department;
    }

    public void setDepartment( String department ) {
        this.department = department;
    }

    @Override
    public void setName( String name ) {
        super.setName( name );
    }
}
