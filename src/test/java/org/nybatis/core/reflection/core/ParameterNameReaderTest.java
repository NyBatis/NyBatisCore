package org.nybatis.core.reflection.core;

import org.nybatis.core.clone.vo.Product;
import org.nybatis.core.clone.vo.ProductMeta;
import org.nybatis.core.reflection.core.CoreReflector;
import org.nybatis.core.reflection.core.ParameterNameReader;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-27
 */
public class ParameterNameReaderTest {

    @Test
    public void methodNames() {

        Product product = getSample();
        Class<? extends Product> klass = product.getClass();

        ParameterNameReader nameReader = new ParameterNameReader();

        CoreReflector reflector = new CoreReflector();
        Set<Method> methods = reflector.getMethods( klass );

        for( Method method : methods ) {
            System.out.printf( "%s : %s\n", method, nameReader.read( method ) );
        }

    }

    @Test
    public void constructorNames() {

        Product product = getSample();
        Class<? extends Product> klass = product.getClass();

        ParameterNameReader nameReader = new ParameterNameReader();

        CoreReflector reflector = new CoreReflector();
        Set<Constructor> contructors = reflector.getContructors( klass );

        for( Constructor constructor : contructors ) {
            System.out.println( nameReader.read( constructor ) );
        }

    }

    private Product getSample() {

        Product product = new Product();

        ProductMeta meta = new ProductMeta();

        product.setProdId( "001" );
        product.setProdName( "Book" );
        product.setPrice( 1000 );

        meta.setAuthor( Arrays.asList("Jim", "Gray", "Nayasis") );
        meta.setDescription( "simple description" );

        product.setMeta( meta );

        return product;

    }

}