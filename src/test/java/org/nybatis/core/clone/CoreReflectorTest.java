package org.nybatis.core.clone;

import org.nybatis.core.clone.vo.Product;
import org.nybatis.core.clone.vo.ProductMeta;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-24
 */
public class CoreReflectorTest {

    @Test
    public void test() throws IOException {

        CoreReflector reflector = new CoreReflector();
        ParameterNameReader reader = new ParameterNameReader();

        Product product = getSample();

        Set<Field> fields = reflector.getFields( product );

        for( Field field : fields ) {
            System.out.printf( "%s : %s\n", field.getName(), reflector.getFieldValue(product, field) );
        }

        Set<Method> methods = reflector.getMethods( product );
//        Set<Method> methods = reflector.getMethods( product.getMeta() );

        for( Method method : methods ) {
            System.out.printf( "%s : %s\n", method, Arrays.toString(method.getParameters()) );
            System.out.println( reader.read( method ));
//            if( method.getName().equals( "setDescriptionStaticaly" ) ) {
//                System.out.println( "merong" );
//                System.out.println( reflector.getParameterNames( method ));
//            }
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